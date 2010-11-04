/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.string.UrlUtils;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.user.entity.User;

/**
 * @author Carsten Hufe
 */
public class PortalUtil {

    /**
     * Converts user to email placeholder
     */
    public static EmailPlaceholderBean createEmailPlaceHolderByUser(User user) {
        EmailPlaceholderBean placeholder = new EmailPlaceholderBean();
        placeholder.setBirthday(user.getBirthday());
        placeholder.setUsername(user.getUsername());
        placeholder.setEmail(user.getEmail());
        placeholder.setFirstname(user.getFirstname());
        placeholder.setLastname(user.getLastname());
        return placeholder;
    }

    /**
     * Generates a MD5 key
     *
     * @param value some text, for example a password
     */
    public static String generateMd5(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(value.getBytes(), 0, value.length());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new UnhandledException("MD5 ist not available: ", e);
        }
    }

    /**
     * Returns the bean name without get
     *
     * @param str methodname with get
     * @return property name
     */
    public static String removeGet(String str) {
        String back = str.substring(3);
        back = back.substring(0, 1).toLowerCase() + back.substring(1);
        return back;
    }

    /**
     * Returns the bean name with get
     *
     * @param str property name without get
     * @return the method name with get
     */
    public static String addGet(String str) {
        return "get" + str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * @return instance of date with the actual time
     */
    public static Date now() {
        return new Date();
    }

    public static String toUrl(ResourceReference ref, Request request) {
        return UrlUtils.rewriteToContextRelative("resources/" + ref.getSharedResourceKey(), request);
    }

    /**
     * Returns a random string for captchas
     *
     * @param min minimum value
     * @param max maxumum value;
     * @return random string for capture
     */
    public static String randomString(int min, int max) {
        int num = randomInt(min, max);
        byte b[] = new byte[num];
        for (int i = 0; i < num; i++) {
            b[i] = (byte) randomInt('a', 'z');
        }
        return new String(b);
    }

    /**
     * Random integer value
     *
     * @param min minimal value
     * @param max maximum value
     * @return random int value
     */
    private static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * Returns the {@link PageConfiguration} by simple class name
     *
     * @param confs    List with configurations
     * @param pageName Simple class name of page
     * @return matching {@link PageConfiguration}
     */
    public static PageConfiguration getConfigurationByPageName(Collection<PageConfiguration> confs, String pageName) {
        for (PageConfiguration conf : confs) {
            if (pageName.equals(conf.getPageClass().getSimpleName())) {
                return conf;
            }
        }
        return null;
    }

    public static void addSyntaxHightlighter(Component component, String theme) {
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shCore.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushJava.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushSql.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushBash.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushXml.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushJScript.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushCpp.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushCSharp.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushCss.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushDelphi.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushDiff.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushPhp.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushPython.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushRuby.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushVb.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushScala.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushPlain.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushGroovy.js"));
        component.add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/SyntaxHighlighter/shCore.css"));
        component.add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/SyntaxHighlighter/shTheme" + theme + ".css"));
        Map<String, Object> values = new MiniMap<String, Object>(1);
        values.put("swfPath", RequestCycle.get().urlFor(CommonConstants.REF_SYNTAXHIGHLIGHTER_SWF));
        component.add(TextTemplateHeaderContributor.forJavaScript(CommonConstants.class, "js/SyntaxHighlighter/SyntaxHighlighterCopy.js", new MapModel<String, Object>(values)));
    }

    public static void addJQuery(Component component) {
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/jquery-1.4.2.min.js"));
        component.add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/jquery.center.js"));
    }

    public static String getParameterAsString(String key) {
        PageParameters pageParameters = RequestCycle.get().getPageParameters();
        if (pageParameters == null) {
            return null;
        }
        return pageParameters.getString(key);
    }

    public static Integer getParameterAsInteger(String key) {
        PageParameters pageParameters = RequestCycle.get().getPageParameters();
        if (pageParameters == null) {
            return null;
        }
        return pageParameters.getAsInteger(key);
    }

    public static Boolean getParameterAsBoolean(String key) {
        PageParameters pageParameters = RequestCycle.get().getPageParameters();
        if (pageParameters == null) {
			return null;
		}
		return pageParameters.getAsBoolean(key);
	}

}
