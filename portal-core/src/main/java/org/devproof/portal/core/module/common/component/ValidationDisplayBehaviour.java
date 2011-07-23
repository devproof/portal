/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devproof.portal.core.module.common.component;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.*;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.ValidationErrorFeedback;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.devproof.portal.core.module.common.CommonConstants;

import java.util.Map;

/**
 * @author Carsten Hufe
 */
public class ValidationDisplayBehaviour extends Behavior {
    private static final long serialVersionUID = 1L;
    public final static ResourceReference ERRORHINT_IMAGE_REF = new PackageResourceReference(CommonConstants.class, "img/errorhint.gif");
    private IFeedbackMessageFilter errorLevelFilter = new ErrorLevelFeedbackMessageFilter(FeedbackMessage.ERROR);

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.renderCSSReference(new PackageResourceReference(ValidationDisplayBehaviour.class, "ValidationDisplayBehaviour.css"));
        response.renderJavaScriptReference(new PackageResourceReference(TooltipLabel.class, "TooltipLabel.js"));
        super.renderHead(component, response);
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        FormComponent<?> formComponent = (FormComponent<?>) component;
        FeedbackMessages msgs = Session.get().getFeedbackMessages();
        IFeedbackMessageFilter componentMessageFilter = getFeedbackFilter(formComponent);
        if (msgs.hasMessage(componentMessageFilter)) {
            markForError(formComponent, tag);
        }
    }

    private IFeedbackMessageFilter getFeedbackFilter(FormComponent<?> formComponent) {
        ComponentFeedbackMessageFilter componentFilter = new ComponentFeedbackMessageFilter(formComponent);
        return new AndFeedbackFilter(errorLevelFilter, componentFilter);
    }

    public static void markForError(Component component, ComponentTag tag) {
        if (component instanceof RadioChoice<?>) {
            addCssClass(tag, "error");
        } else {
            addCssClass(tag, "error");
        }
    }

    private static void addCssClass(ComponentTag tag, String cssClass) {
        String value = tag.getAttribute("class");
        if (value == null) {
            tag.put("class", cssClass);
        } else if (!value.contains(cssClass)) {
            tag.put("class", value + " " + cssClass);
        }
    }

    @Override
    public void afterRender(Component component) {
        FormComponent<?> formComponent = (FormComponent<?>) component;
        String msg = getFeedbackMessage(formComponent);
        if (msg != null) {
            printErrorMessage(msg, component);
        }
        super.afterRender(component);
    }

    private void printErrorMessage(CharSequence msg, Component componentWithError) {
        PackageTextTemplate template = new PackageTextTemplate(ValidationDisplayBehaviour.class, "ValidationDisplayBehaviour.html");
        Map<String, Object> variables = new MiniMap<String, Object>(4);
        variables.put("message", msg);
        variables.put("imageUrl", componentWithError.urlFor(ERRORHINT_IMAGE_REF, new PageParameters()));
        variables.put("imageId", componentWithError.getMarkupId() + "Image");
        variables.put("popupId", componentWithError.getMarkupId() + "Popup");
        componentWithError.getResponse().write(template.asString(variables));
    }

    private String getFeedbackMessage(FormComponent<?> component) {
        FeedbackMessage feedbackMessage = component.getFeedbackMessage();
        if ((feedbackMessage != null) && (feedbackMessage.getLevel() >= FeedbackMessage.ERROR)) {
            feedbackMessage.markRendered();
            if (feedbackMessage.getMessage() instanceof ValidationErrorFeedback) {
                ValidationErrorFeedback error = (ValidationErrorFeedback) feedbackMessage.getMessage();
                return error.getMessage();
            } else {
                return (String) feedbackMessage.getMessage();
            }
        }
        return null;
    }
}