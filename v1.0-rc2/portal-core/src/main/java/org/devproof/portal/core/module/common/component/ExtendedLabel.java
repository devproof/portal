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
package org.devproof.portal.core.module.common.component;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.resource.String2ImageResource;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.springframework.web.util.HtmlUtils;

/**
 * Portal extended label - Parses the string2img tag
 * 
 * @author Carsten Hufe
 * 
 */
public class ExtendedLabel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final String PRETAG = "[string2img";
	private static final String CLOSE_SEP = "]";
	private static final String POSTTAG = "[/string2img]";
	private static final Map<String, ImgResourceReference> images = new ConcurrentHashMap<String, ImgResourceReference>();

	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	public ExtendedLabel(final String id, final String content) {
		super(id);

		RepeatingView repeating = new RepeatingView("repeating");
		this.add(repeating);

		String s = content;
		String tags[] = StringUtils.substringsBetween(s, PRETAG, POSTTAG);
		List<String> other = new ArrayList<String>();
		if (tags != null) {
			for (String tag : tags) {
				String t = PRETAG + tag + POSTTAG;
				other.add(StringUtils.substringBefore(s, t));
				s = StringUtils.substringAfter(s, t);
			}
		}
		other.add(s);

		int i = 0;
		for (String label : other) {
			WebMarkupContainer container = new WebMarkupContainer(repeating.newChildId());
			repeating.add(container);
			Label item = new Label("label", label);
			item.setEscapeModelStrings(false);
			container.add(item);

			if (tags != null && tags.length > i) {
				String img = tags[i];
				String attrs[] = StringUtils.split(StringUtils.substringBefore(img, CLOSE_SEP), "= ");
				int size = 12;
				for (int j = 0; j < attrs.length; j++) {
					String value = attrs[j].trim();
					if ("size".equalsIgnoreCase(value) && (j + 1) < attrs.length) {
						try {
							size = Integer.valueOf(attrs[j + 1].trim());
						} catch (NumberFormatException e) {
							// do nothing!
						}
					}
				}

				img = StringUtils.substringAfter(img, CLOSE_SEP);
				List<String> lines = new ArrayList<String>();
				String tmp[] = StringUtils.splitByWholeSeparator(img, "<br />");
				StringBuilder hash = new StringBuilder();
				for (String t : tmp) {
					lines.add(HtmlUtils.htmlUnescape(t.replaceAll("\\<.*?>", "")));
					hash.append(t.hashCode());
				}
				String uuid = String.valueOf(hash.toString());
				String fontName = this.configurationService.findAsString(CommonConstants.CONF_STRING2IMG_FONT);
				Font font = new Font(fontName, Font.PLAIN, size);
				String2ImageResource resource = new String2ImageResource(lines, font);
				ImgResourceReference imgResource = images.get(uuid);
				if (imgResource == null) {
					imgResource = new ImgResourceReference(uuid, resource);
					// is internally syncronized
					if (!images.containsKey(hash.toString())) {
						Iterator<String> it = images.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next();
							ImgResourceReference ref = images.get(key);
							if (ref.isExpired()) {
								ref.invalidate();
								((WebApplication) getApplication()).getSharedResources().remove(key);
								it.remove();
							}
						}
						images.put(uuid, imgResource);
					}
				}
				container.add(new Image("image", imgResource) {
					private static final long serialVersionUID = 1L;

					@Override
					protected boolean getStatelessHint() {
						return true;
					}
				});
			} else {
				container.add(new WebMarkupContainer("image").setVisible(false));
			}

			i++;
		}
	}

	public static class ImgResourceReference extends ResourceReference {
		private static final long serialVersionUID = 1L;
		private static final long MAX_AGE = 1000 * 60 * 10; // TEN MINUTES
		private final Resource resource;
		private final Date time = PortalUtil.now();

		public ImgResourceReference(final String hash, final Resource resource) {
			super(ExtendedLabel.class, hash);
			this.resource = resource;
		}

		@Override
		public Resource newResource() {
			return this.resource;
		}

		public boolean isExpired() {
			return (this.time.getTime() + MAX_AGE) < PortalUtil.now().getTime();
		}
	};
}
