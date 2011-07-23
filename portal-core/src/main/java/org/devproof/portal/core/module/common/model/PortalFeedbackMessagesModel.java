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

package org.devproof.portal.core.module.common.model;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;

import java.util.ArrayList;
import java.util.List;

public class PortalFeedbackMessagesModel extends FeedbackMessagesModel {
        private static final long serialVersionUID = -8276573292357290113L;
    private Component component;

    public PortalFeedbackMessagesModel(Component component) {
            super(component);
        this.component = component;
    }

        @Override
        protected List<FeedbackMessage> processMessages(List<FeedbackMessage> messages) {
            List<FeedbackMessage> result = new ArrayList<FeedbackMessage>();
            for (FeedbackMessage message : messages) {
                Component reporter = message.getReporter();
                if (!hasValidationDisplayBehaviour(reporter)) {
                    result.add(message);
                }
            }
            if(result.size() != messages.size()) {
                result.add(0, new FeedbackMessage(null, component.getString("formErrorHint"), FeedbackMessage.ERROR));
            }
            return result;
        }

        private boolean hasValidationDisplayBehaviour(Component reporter) {
            if (reporter != null) {
                for (Behavior behavior : reporter.getBehaviors()) {
                    if (behavior instanceof ValidationDisplayBehaviour) {
                        return true;
                    }
                }
            }
            return false;
        }
    }