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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
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
public class ExtendedLabel extends Label {
	private static final long serialVersionUID = 1L;
	private static final String PRETAG = "[string2img";
	private static final String CLOSE_SEP = "]";
	private static final String POSTTAG = "[/string2img]";
	private static final Map<String, ImgResourceReference> images = new ConcurrentHashMap<String, ImgResourceReference>();

	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
    private IModel<String> contentModel;

    public ExtendedLabel(String id, IModel<String> contentModel) {
		super(id, contentModel);
        this.contentModel = contentModel;
		setDefaultModel(createConvertedContentModel());
		setEscapeModelStrings(false);
	}

    private IModel<String> createConvertedContentModel() {
        return new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                cleanupExpiredImages();
                String modifiedContent = contentModel.getObject();
                String tagParts[] = StringUtils.substringsBetween(modifiedContent, PRETAG, POSTTAG);
                if (tagParts != null) {
                    for (String tagPart : tagParts) {
                        String fullTag = PRETAG + tagPart + POSTTAG;
                        String splittedAttribute[] = StringUtils.split(StringUtils.substringBefore(fullTag, CLOSE_SEP), "= ");
                        int fontSize = getFontSize(splittedAttribute);
                        List<String> str2ImgLines = getTextLines(tagPart);
                        Font font = getFont(fontSize);
                        ImgResourceReference imgResource = getImageResourceAndCache(str2ImgLines, font);
                        modifiedContent = replaceTagWithImage(modifiedContent, fullTag, imgResource);
                    }
                }
                return modifiedContent;
            }
        };
    }

	private ImgResourceReference getImageResourceAndCache(List<String> str2ImgLines, Font font) {
		String uuid = String.valueOf(str2ImgLines.hashCode());
		String2ImageResource resource = new String2ImageResource(str2ImgLines, font);
		ImgResourceReference imgResource = images.get(uuid);
		if (imgResource == null) {
			imgResource = new ImgResourceReference(uuid, resource);
			// ConcurrentHashMap
			images.put(uuid, imgResource);
		}
		return imgResource;
	}

	private String replaceTagWithImage(String modifiedContent, String fullTag, ImgResourceReference imgResource) {
		modifiedContent = modifiedContent.replace(fullTag, "<img src=\"" + getRequestCycle().urlFor(imgResource)
				+ "\" alt=\"\"/>");
		return modifiedContent;
	}

	private void cleanupExpiredImages() {
		Iterator<String> it = images.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			ImgResourceReference ref = images.get(key);
			if (ref.isExpired()) {
				ref.invalidate();
				getApplication().getSharedResources().remove(key);
				it.remove();
			}
		}
	}

	private Font getFont(int fontSize) {
		String fontName = configurationService.findAsString(CommonConstants.CONF_STRING2IMG_FONT);
        return new Font(fontName, Font.PLAIN, fontSize);
	}

	private List<String> getTextLines(String tagPart) {
		String str2img = StringUtils.substringAfter(tagPart, CLOSE_SEP);
		List<String> str2ImgLines = new ArrayList<String>();
		String tmp[] = StringUtils.splitByWholeSeparator(str2img, "<br />");
		for (String t : tmp) {
			str2ImgLines.add(HtmlUtils.htmlUnescape(t.replaceAll("\\<.*?>", "")));
		}
		return str2ImgLines;
	}

	private int getFontSize(String[] attrs) {
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
		return size;
	}

	public static class ImgResourceReference extends ResourceReference {
		private static final long serialVersionUID = 1L;
		private static final long MAX_AGE = 1000 * 60 * 10; // TEN MINUTES
		private Resource resource;
		private Date time = PortalUtil.now();

		public ImgResourceReference(String hash, Resource resource) {
			super(ExtendedLabel.class, hash);
			this.resource = resource;
		}

		@Override
		public Resource newResource() {
			return resource;
		}

		public boolean isExpired() {
			return (time.getTime() + MAX_AGE) < PortalUtil.now().getTime();
		}
	}
}
