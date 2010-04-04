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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.devproof.portal.core.module.common.component.ProgressBar;
import org.devproof.portal.core.module.common.component.Progression;
import org.devproof.portal.core.module.common.component.ProgressionModel;
import org.devproof.portal.core.module.common.util.httpclient.ssl.EasySSLProtocolSocketFactory;
import org.devproof.portal.module.deadlinkcheck.entity.BaseLinkEntity;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
    private IModel<List<T>> listToCheckModel;
    private FeedbackPanel feedbackPanel;
	private ProgressBar progressBar;
	private volatile boolean threadActive = false;

	public DeadlinkCheckPanel(String id, String section, IModel<List<T>> listToCheckModel) {
		super(id, listToCheckModel);
		this.section = section;
        this.listToCheckModel = listToCheckModel;
        add(createFeedbackPanel());
		add(createTitleLabel());
		add(createDeadlinkCheckForm());
	}

	private Label createTitleLabel() {
		return new Label("title", getString(section + "Title"));
	}

	private Form<Void> createDeadlinkCheckForm() {
		Form<Void> form = new Form<Void>("form");
		form.add(createDescriptionLabel());
		form.add(createProgressBar());
		form.add(createAjaxButton());
		form.add(createCancelButton());
		form.setOutputMarkupId(true);
		return form;
	}

	private Label createDescriptionLabel() {
		return new Label("description", getString(section + "Description"));
	}

	private AjaxButton createAjaxButton() {
		return new AjaxButton("startButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String baseUrl = RequestUtils.toAbsolutePath("");
				progressBar.start(target);
				newDeadlinkCheckThread(baseUrl).start();
				setEnabled(false);
			}

			private Thread newDeadlinkCheckThread(final String baseUrl) {
				return new Thread() {
					@Override
					public void run() {
						threadActive = true;
						Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(),
								443));
                        List<T> listToCheck = listToCheckModel.getObject();
                        for (T link : listToCheck) {
							if (!threadActive) {
								return;
							}
							String url = link.getUrl();
							boolean isBroken;
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
						boolean isBroken;
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

					private String buildAbsoluteUrl(String baseUrl, String url) {
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

	private AjaxLink<Void> createCancelButton() {
		return new AjaxLink<Void>("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				threadActive = false;
				onCancel(target);
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
		progressBar = new ProgressBar("bar", progressionModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onFinished(AjaxRequestTarget target) {
				info(new StringResourceModel(section + "Finished", this, null,
						new Object[] { DeadlinkCheckPanel.this.brokenFound }).getString());
				target.addComponent(feedbackPanel);
			}
		};
		return progressBar;
	}

	private FeedbackPanel createFeedbackPanel() {
		feedbackPanel = new FeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		return feedbackPanel;
	}

	/**
	 * called on every broken entity
	 * 
	 * @param brokenEntity
	 *            broken entity
	 */
	public abstract void onBroken(T brokenEntity);

	/**
	 * called on every valid entity
	 * 
	 * @param validEntity
	 *            valid entity
	 */
	public abstract void onValid(T validEntity);

	/**
	 * called when the cancel button is clicked
	 * 
	 * @param target
	 */
	public abstract void onCancel(AjaxRequestTarget target);
}
