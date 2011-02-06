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
package org.devproof.portal.core.module.common.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

/**
 * <p>
 * The <code>ProgressBar</code> component displays a horizontal progress bar
 * that is updatable via AJAX and displays the progress of some task. The
 * current progress is given with a <code>Progression</code> value object.
 * </p>
 * <p/>
 * <p>
 * This is a small example of a static <code>ProgressBar</code> without AJAX
 * updates:
 * </p>
 * <p/>
 * <pre>
 * &lt;code&gt;
 * final ProgressBar bar;
 * add(bar = new ProgressBar(&quot;progress&quot;, new ProgressionModel() {
 * 	protected Progression getProgression() {
 * 		// progress is an int instance variable defined somewhere else
 * 		return new Progression(progress);
 *     }
 * }));
 * &lt;/code&gt;
 * </pre>
 * <p/>
 * <p>
 * If the model for getting the <code>Progression</code> is not known at the
 * time of construction, it could be injected later.
 * </p>
 * <p/>
 * <p>
 * The progress bar can be used both actively or passively (e.g. to show
 * progress in a wizard). The active progress bar must be started from within an
 * ajax request (e.g. AjaxButton), as shown below:
 * </p>
 * <p/>
 * <pre>
 * &lt;code&gt;
 * form.add(new AjaxButton(&quot;button&quot;) {
 *     protected void onSubmit(AjaxRequestTarget target, Form form) {
 *         bar.start(target);
 *         // start some task
 *     }
 * }
 * &lt;/code&gt;
 * </pre>
 * <p/>
 * <p>
 * The <code>ProgressBar</code> is automatically stopped (including AJAX
 * updates) when the <code>isDone()</code> method of the <code>Progress</code>
 * object returns true. The bar can be stopped anytime using the
 * <code>stop()</code> method.
 * </p>
 *
 * @author Christopher Hlubek (hlubek), modified by Carsten Hufe
 *         <p/>
 *         Modified by Carsten Hufe - make it compatible to Wicket 1.4 -
 *         customized it (I hate pink)
 */
public class ProgressBar extends Panel {

    private static final long serialVersionUID = 1L;

    private static final ResourceReference CSS = new ResourceReference(ProgressBar.class, "ProgressBar.css");
    private int width = 400;
    private ProgressionModel model;

    public ProgressBar(String id, ProgressionModel model) {
        super(id, model);
        this.model = model;
        add(createCSSHeaderContributor());
        add(createProgressPercentageLabel());
        add(createProgressMessageLabel());
        add(createProgressBarContainer());
        setOutputMarkupId(true);
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(CSS);
    }

    private Component createProgressBarContainer() {
        return new WebMarkupContainer("bar").add(new AttributeModifier("style", true, new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                // ProgressionModel model = (ProgressionModel)
                // getModel();
                Progression progression = model.getProgression();
                // set the width of the bar in % of the progress
                // this is coupled with the specific CSS
                return "width: " + progression.getProgress() + "%";
            }
        }));
    }

    private Label createProgressMessageLabel() {
        return new Label("progressMessage", createProgressMessageModel());
    }

    private Label createProgressPercentageLabel() {
        return new Label("progessPercentage", createProgressPercentageModel());
    }

    /**
     * Create the model for the label on the bar.
     * <p/>
     * This could be overridden for a custom label
     *
     * @return A model for the bar label
     */
    protected AbstractReadOnlyModel<String> createProgressPercentageModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                Progression progression = model.getProgression();
                return progression.getProgress() + "%";
            }
        };
    }

    /**
     * Create the model for the message label on the bar.
     * <p/>
     * This could be overridden for a custom message label
     *
     * @return A model for the bar message label
     */
    protected IModel<String> createProgressMessageModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return model.getProgression().getProgressMessage();
            }
        };
    }

    /**
     * Start the progress bar.
     * <p/>
     * This must happen in an AJAX request.
     *
     * @param target
     */
    public void start(AjaxRequestTarget target) {
        setVisible(true);
        add(new DynamicAjaxSelfUpdatingTimerBehavior(Duration.ONE_SECOND) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                // ProgressionModel model = (ProgressionModel) getModel();
                Progression progression = model.getProgression();
                if (progression.isDone()) {
                    // stop the self update
                    stop();
                    // do custom action
                    ProgressBar.this.onFinished(target);
                }
            }
        });
        if (getParent() != null) {
            target.addComponent(getParent());
        } else {
            target.addComponent(this);
        }
    }

    /**
     * Override this method for custom action on finish of the task when
     * progression.isDone()
     * <p/>
     * This could be cleaning up or hiding the ProgressBar for example.
     *
     * @param target
     */
    protected void onFinished(AjaxRequestTarget target) {

    }

    /**
     * @return the width of the ProgressBar
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width of the progress bar.
     *
     * @param width the width of the ProgressBar in px
     */
    public void setWidth(int width) {
        this.width = width;
    }
}
