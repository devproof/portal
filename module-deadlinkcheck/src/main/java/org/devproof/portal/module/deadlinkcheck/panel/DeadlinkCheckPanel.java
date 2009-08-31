/*
 * Copyright 2009 Carsten Hufe devproof.org
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

	public DeadlinkCheckPanel(final String id, final String section, final List<T> listToCheck) {
		super(id, Model.of(listToCheck));
		this.maxItem = listToCheck.size();

		final FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		add(feedback);

		final Form<?> form = new Form<Object>("form");
		form.setOutputMarkupId(true);
		add(form);

		final ProgressionModel model = new ProgressionModel() {
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
		final ProgressBar bar = new ProgressBar("bar", model) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onFinished(final AjaxRequestTarget target) {
				info(new StringResourceModel(section + "Finished", this, null,
						new Object[] { DeadlinkCheckPanel.this.brokenFound }).getString());
				target.addComponent(feedback);
			}
		};
		form.add(bar);

		add(new Label("title", getString(section + "Title")));
		form.add(new Label("description", getString(section + "Description")));

		final String baseUrl = RequestUtils.toAbsolutePath("");

		form.add(new IndicatingAjaxButton("startButton", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				bar.start(target);
				new Thread() {
					@Override
					public void run() {
						Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(),
								443));

						HttpClient client = new HttpClient();
						for (T link : listToCheck) {
							String url = link.getUrl();
							boolean isBroken = false;

							if (url.startsWith("file:/")) {
								try {
									URI uri = new URI(url);
									File downloadFile = new File(uri);
									isBroken = !downloadFile.canRead();
								} catch (URISyntaxException e) {
									// nothing to log
									isBroken = true;
								}
							} else {
								if (!url.startsWith("http://") && !url.startsWith("https://")
										&& !url.startsWith("ftp://")) {
									if (url.startsWith("/")) {
										url = url.substring(1);
									}
									url = baseUrl + url;
								}

								try {
									new URI(url);
								} catch (URISyntaxException e1) {
									isBroken = true;
								}
								if (!isBroken) {
									HttpMethod method = new GetMethod(url);
									try {
										int httpCode = client.executeMethod(method);
										isBroken = (httpCode / 100) != 2;
									} catch (HttpException e) {
										isBroken = true;
									} catch (IOException e) {
										isBroken = true;
									}
									method.releaseConnection();
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
				}.start();

				setVisible(false);
			}
		});
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
