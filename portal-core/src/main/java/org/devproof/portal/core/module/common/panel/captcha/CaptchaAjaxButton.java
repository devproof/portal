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
package org.devproof.portal.core.module.common.panel.captcha;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.util.PortalUtil;

/**
 * @author Carsten Hufe
 */
public abstract class CaptchaAjaxButton extends AjaxSubmitLink {
    private static final long serialVersionUID = 1L;
    private BubblePanel bubbleWindow;

    public CaptchaAjaxButton(String id) {
        this(id, null);
    }

    public CaptchaAjaxButton(String id, BubblePanel bubbleWindow) {
        super(id);
        this.bubbleWindow = bubbleWindow;
        PortalUtil.addJQuery(this);
        setOutputMarkupId(true);
    }

    @Override
    final protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        if (showCaptcha()) {
            CaptchaPanel captchaPanel = createCaptchaPanel();
            BubblePanel bubblePanel = getBubbleWindow();
            bubblePanel.setContent(captchaPanel);
            bubblePanel.showModal(target);
        } else {
            onClickAndCaptchaValidated(target);
        }
    }

    private CaptchaPanel createCaptchaPanel() {
        return new CaptchaPanel(getBubbleWindow().getContentId()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onClickAndCaptchaValidated(AjaxRequestTarget target) {
                getBubbleWindow().hide(target);
                CaptchaAjaxButton.this.onClickAndCaptchaValidated(target);
            }

            @Override
            protected void onCancel(AjaxRequestTarget target) {
                getBubbleWindow().hide(target);
            }
        };
    }

    private boolean showCaptcha() {
        return !PortalSession.get().hasRight("captcha.disabled");
    }

    public BubblePanel getBubbleWindow() {
        return bubbleWindow;
    }

    public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
