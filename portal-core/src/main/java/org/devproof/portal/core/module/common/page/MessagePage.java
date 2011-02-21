/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.devproof.portal.core.app.PortalSession;

/**
 * @author Carsten Hufe
 */
// TODO needs a full clean up ...
public class MessagePage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public MessagePage(PageParameters params) {
        super(params);
        String msg = params.getString("message");
        if (msg == null) {
            msg = "unknown.error";
        }
        add(new Label("message", getString(msg)));
    }

    public MessagePage(String message, boolean error) {
        this(message, error, true);
    }


    public MessagePage(String message, boolean error, boolean key) {
        super(new PageParameters());
        Label label = new Label("message", key ? getString(message): message);
        label.add(new SimpleAttributeModifier("class", error ? "feedbackPanelERROR" : "feedbackPanelINFO"));
        add(label);
    }

    public static MessagePage getMessagePage(String message) {
        return new MessagePage(message, false, false);
    }

    public static MessagePage getErrorPage(String message) {
        return new MessagePage(message, true, false);
    }

    public static MessagePage getMessagePageWithLogout(String message) {
        ((PortalSession) Session.get()).logoutUser();
        return new MessagePage(message, false, false);
    }

    @Override
    protected String getRobots() {
        return "noindex";
    }
}
