/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.module.deadlinkcheck.panel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.devproof.portal.core.module.common.component.ProgressBar;
import org.devproof.portal.core.module.common.component.Progression;
import org.devproof.portal.core.module.common.component.ProgressionModel;
import org.devproof.portal.core.module.common.util.httpclient.ssl.EasySSLProtocolSocketFactory;
import org.devproof.portal.module.deadlinkcheck.entity.BaseLinkEntity;

/**
 * Panel for checking deadlinks (bookmarks and downloads)
 * 
 * @author Carsten Hufe
 */
public abstract class DeadlinkCheckPanel<T extends BaseLinkEntity> extends Panel {

	private static final long serialVersionUID = 1L;

	private int progressInPercent = 0;
	private int actualItem = 0;
	private int maxItem = 0;
	private int brokenFound = 0;
	private String section;
	private List<T> listToCheck;
	private FeedbackPanel feedbackPanel;
	private ProgressBar progressBar;

	public DeadlinkCheckPanel(String id, String section, List<T> listToCheck) {
		super(id, Model.ofList(listToCheck));
		this.section = section;
		this.listToCheck = listToCheck;
		maxItem = listToCheck.size();

		add(feedbackPanel = createFeedbackPanel());
		add(new Label("title", getString(section + "Title")));
		add(createDeadlinkCheckForm());

	}

	private Form<Void> createDeadlinkCheckForm() {
		Form<Void> form = new Form<Void>("form");
		form.add(new Label("description", getString(section + "Description")));
		form.add(progressBar = createProgressBar());
		form.setOutputMarkupId(true);
		form.add(createAjaxButton());
		return form;
	}

	private IndicatingAjaxButton createAjaxButton() {
		return new IndicatingAjaxButton("startButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String baseUrl = RequestUtils.toAbsolutePath("");
				progressBar.start(target);
				newDeadlinkCheckThread(baseUrl).start();
				setVisible(false);
			}

			private Thread newDeadlinkCheckThread(final String baseUrl) {
				return new Thread() {
					@Override
					public void run() {
						Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(),
								443));

						for (T link : listToCheck) {
							String url = link.getUrl();
							boolean isBroken = false;
							if (isLocalFile(url)) {
								try {
									URI uri = new URI(url);
									File downloadFile = new File(uri);
									isBroken = !downloadFile.canRead();
								} catch (URISyntaxException e) {
									// nothing to log
									isBroken = true;
								}
							} else {
								url = buildAbsoluteUrl(baseUrl, url);
								isBroken = isBrokenURIFormat(url);
								if (!isBroken) {
									isBroken = isHttpCallBroken(url);
								}
							}
							//													
							if (isBroken) {
								DeadlinkCheckPanel.this.onBroken(link);
								DeadlinkCheckPanel.this.brokenFound++;
							} else {
								DeadlinkCheckPanel.this.onValid(link);
							}
							DeadlinkCheckPanel.this.actualItem++;
							DeadlinkCheckPanel.this.progressInPercent = (int) (((double) DeadlinkCheckPanel.this.actualItem / (double) DeadlinkCheckPanel.this.maxItem) * 100d);
						}
						// The bar is stopped automatically, if progress is done
					}

					private boolean isHttpCallBroken(String url) {
						HttpClient client = new HttpClient();
						HttpMethod method = new GetMethod(url);
						boolean isBroken = false;
						try {
							int httpCode = client.executeMethod(method);
							isBroken = (httpCode / 100) != 2;
						} catch (HttpException e) {
							isBroken = true;
						} catch (IOException e) {
							isBroken = true;
						}
						method.releaseConnection();
						return isBroken;
					}

					private boolean isBrokenURIFormat(String url) {
						try {
							new URI(url);
						} catch (URISyntaxException e1) {
							return true;
						}
						return false;
					}

					private String buildAbsoluteUrl(final String baseUrl, String url) {
						if (isNotExternalUrl(url)) {
							if (isRelativeUrl(url)) {
								url = url.substring(1);
							}
							url = baseUrl + url;
						}
						return url;
					}

					private boolean isRelativeUrl(String url) {
						return url.startsWith("/");
					}

					private boolean isNotExternalUrl(String url) {
						return !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("ftp://");
					}

					private boolean isLocalFile(String url) {
						return url.startsWith("file:/");
					}
				};
			}
		};
	}

	private ProgressionModel createProgressionModel() {
		return new ProgressionModel() {
			private static final long serialVersionUID = 1L;

			// Get current progress from page field
			@Override
			protected Progression getProgression() {
				String descr = new StringResourceModel(section + "Progress", DeadlinkCheckPanel.this, null,
						new Object[] { DeadlinkCheckPanel.this.actualItem, DeadlinkCheckPanel.this.maxItem })
						.getString();
				return new Progression(DeadlinkCheckPanel.this.progressInPercent, descr);
			}
		};
	}

	private ProgressBar createProgressBar() {
		ProgressionModel progressionModel = createProgressionModel();
		return new ProgressBar("bar", progressionModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onFinished(final AjaxRequestTarget target) {
				info(new StringResourceModel(section + "Finished", this, null,
						new Object[] { DeadlinkCheckPanel.this.brokenFound }).getString());
				target.addComponent(feedbackPanel);
			}
		};
	}

	private FeedbackPanel createFeedbackPanel() {
		FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		return feedback;
	}

	/**
	 * called on every broken entity
	 * 
	 * @param brokenEntity
	 *            broken entity
	 */
	public abstract void onBroken(final T brokenEntity);

	/**
	 * called on every valid entity
	 * 
	 * @param validEntity
	 *            valid entity
	 */
	public abstract void onValid(final T validEntity);
}
