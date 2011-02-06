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
package org.devproof.portal.core.module.configuration.page;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.ConfigurationConstants;
import org.devproof.portal.core.module.configuration.entity.Configuration;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(ConfigurationConstants.ADMIN_RIGHT)
@ModulePage(mountPath = "/admin/configuration", registerGlobalAdminLink = true)
public class ConfigurationPage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    private List<Configuration> allConfigurations = new ArrayList<Configuration>();

    public ConfigurationPage(PageParameters params) {
        super(params);
        add(createConfigurationForm());
    }

    private Form<List<Configuration>> createConfigurationForm() {
        Form<List<Configuration>> form = newConfigurationForm();
        form.add(createRepeatingConfiguration());
        return form;
    }

    private RepeatingView createRepeatingConfiguration() {
        List<String> groups = configurationService.findConfigurationGroups();
        RepeatingView table = new RepeatingView("repeatingConfiguration");
        for (String group : groups) {
            table.add(createGroupHeaderRowContainer(table.newChildId(), group));
            List<Configuration> configurations = configurationService.findConfigurationsByGroup(group);
            for (Configuration configuration : configurations) {
                table.add(createEditRowContainer(table.newChildId(), configuration));
                allConfigurations.add(configuration);
            }
        }
        return table;
    }

    private WebMarkupContainer createEditRowContainer(String id, Configuration configuration) {
        WebMarkupContainer row = new WebMarkupContainer(id);
        row.add(createEditRowLabel(configuration));
        row.add(createEditorForConfiguration(configuration));
        return row;
    }

    private Label createEditRowLabel(Configuration configuration) {
        return new Label("description", configuration.getDescription());
    }

    private WebMarkupContainer createGroupHeaderRowContainer(String id, String group) {
        WebMarkupContainer row = new WebMarkupContainer(id);
        row.add(createGroupHeader(group));
        row.add(createEmptyLabel());
        return row;
    }

    private Label createEmptyLabel() {
        return new Label("editor", "");
    }

    private GroupHeader createGroupHeader(String group) {
        return new GroupHeader("description", group);
    }

    private Form<List<Configuration>> newConfigurationForm() {
        return new Form<List<Configuration>>("form") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                for (Configuration configuration : allConfigurations) {
                    configurationService.save(configuration);
                }
                info(getString("msg.saved"));
            }
        };
    }

    private Class<?> getClassByString(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Override this method to provide custom editors for your fields.
     *
     * @param configuration {@link org.devproof.portal.core.module.configuration.entity.Configuration}
     * @return editor fragment for for the matching configuration type
     */

    protected Component createEditorForConfiguration(Configuration configuration) {
        if (configuration.getKey().startsWith(ConfigurationConstants.SPRING_CONFIGURATION_PREFIX)) {
            // special case for accessing a spring dao
            return new SpringBeanEditor("editor", configuration);
        } else {
            Class<?> clazz;
            try {
                clazz = Class.forName(configuration.getType());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (Boolean.class.isAssignableFrom(clazz)) {
                return new BooleanEditor("editor", configuration);
            } else if (clazz.isEnum()) {
                return new EnumEditor("editor", configuration);

            } else if (Date.class.isAssignableFrom(clazz)) {
                return new DateEditor("editor", configuration);
            } else {
                return new ValueEditor("editor", configuration);
            }
        }
    }

    private class ValueEditor extends Fragment {
        private static final long serialVersionUID = 1L;
        private Configuration configuration;

        public ValueEditor(String id, Configuration configuration) {
            super(id, "valueEditor", ConfigurationPage.this);
            this.configuration = configuration;
            add(createAppropriateValueTextField());
        }

        private Component createAppropriateValueTextField() {
            Class<?> clazz = getClassByString(configuration.getType());
            if (Double.class.isAssignableFrom(clazz)) {
                return createDoubleField();
            } else if (Integer.class.isAssignableFrom(clazz)) {
                return createIntegerField();
            } else if (String.class.isAssignableFrom(clazz)) {
                return createStringField();
            } else {
                throw new IllegalArgumentException("Configuration type is not allowed!");
            }
        }

        private RequiredTextField<Integer> createIntegerField() {
            IModel<String> label = new PropertyModel<String>(configuration, "key");
            RequiredTextField<Integer> textField = new RequiredTextField<Integer>("edit", new PropertyModel<Integer>(configuration, "integerValue"));
            textField.setLabel(label);
            return textField;
        }

        private RequiredTextField<Double> createDoubleField() {
            IModel<String> label = new PropertyModel<String>(configuration, "key");
            RequiredTextField<Double> textField = new RequiredTextField<Double>("edit", new PropertyModel<Double>(configuration, "doubleValue"));
            textField.setLabel(label);
            return textField;
        }

        private RequiredTextField<String> createStringField() {
            IModel<String> label = new PropertyModel<String>(configuration, "key");
            RequiredTextField<String> textField = new RequiredTextField<String>("edit", new PropertyModel<String>(configuration, "value"));
            textField.setLabel(label);
            return textField;
        }
    }

    private class BooleanEditor extends Fragment {
        private static final long serialVersionUID = 1L;
        private Configuration configuration;

        public BooleanEditor(String id, Configuration configuration) {
            super(id, "booleanEditor", ConfigurationPage.this);
            this.configuration = configuration;
            add(createCheckBox());
        }

        private FormComponent<Boolean> createCheckBox() {
            CheckBox checkBox = new CheckBox("edit", new PropertyModel<Boolean>(configuration, "booleanValue"));
            checkBox.setLabel(new PropertyModel<String>(configuration, "key"));
            return checkBox;
        }
    }

    private class EnumEditor extends Fragment {
        private static final long serialVersionUID = 1L;
        private Configuration configuration;

        public EnumEditor(String id, Configuration configuration) {
            super(id, "enumEditor", ConfigurationPage.this);
            this.configuration = configuration;
            add(createEnumDropDownChoice());
        }

        private DropDownChoice<String> createEnumDropDownChoice() {
            List<String> enumChoices = getEnumChoices();
            DropDownChoice<String> ddc = new DropDownChoice<String>("edit", new PropertyModel<String>(configuration, "value"), enumChoices);
            ddc.setLabel(new PropertyModel<String>(configuration, "key"));
            ddc.setRequired(true);
            return ddc;
        }

        private List<String> getEnumChoices() {
            Class<?> clazz = getClassByString(configuration.getType());
            Object tmp[] = clazz.getEnumConstants();
            List<String> values = new ArrayList<String>();
            for (int i = 0; i < tmp.length; i++) {
                values.add(((Enum<?>) tmp[i]).name());
            }
            return values;
        }
    }

    private class SpringBeanEditor extends Fragment {
        private static final long serialVersionUID = 1L;

        private Configuration configuration;
        private List<Configuration> possibleSelectionValues;

        public SpringBeanEditor(String id, Configuration configuration) {
            super(id, "springBeanEditor", ConfigurationPage.this);
            this.configuration = configuration;
            this.possibleSelectionValues = createPossibleSelectionValues();
            add(createSpringDropDownChoice());
        }

        private DropDownChoice<Configuration> createSpringDropDownChoice() {
            DropDownChoice<Configuration> ddc = new DropDownChoice<Configuration>("edit", possibleSelectionValues, new ChoiceRenderer<Configuration>("description", "value"));
            ddc.setModel(newConfigurationModel());
            ddc.setLabel(new PropertyModel<String>(configuration, "key"));
            ddc.setRequired(true);
            return ddc;
        }

        private List<Configuration> createPossibleSelectionValues() {
            List<Configuration> possibleSelectionValues = new ArrayList<Configuration>();
            String typeWithoutPrefix = configuration.getKey().substring(ConfigurationConstants.SPRING_CONFIGURATION_PREFIX.length());
            int index = typeWithoutPrefix.indexOf('.');
            String springBeanName = typeWithoutPrefix.substring(0, index);
            typeWithoutPrefix = typeWithoutPrefix.substring(index + 1);
            index = typeWithoutPrefix.indexOf('.');
            String methodName = typeWithoutPrefix.substring(0, index);
            typeWithoutPrefix = typeWithoutPrefix.substring(index + 1);

            ApplicationContext context = ((PortalApplication) getApplication()).getSpringContext();
            Object springBean = context.getBean(springBeanName);
            try {
                Method method = springBean.getClass().getMethod(methodName);
                List<?> results = (List<?>) method.invoke(springBean);
                if (results != null && results.size() > 0) {
                    if (typeWithoutPrefix.contains(".")) {
                        index = typeWithoutPrefix.indexOf('.');
                        String displayName = typeWithoutPrefix.substring(0, index);
                        typeWithoutPrefix = typeWithoutPrefix.substring(index + 1);
                        index = typeWithoutPrefix.indexOf('.');
                        String primaryKey = typeWithoutPrefix.substring(0, index);

                        Method primaryKeyMethod = results.get(0).getClass().getMethod(PortalUtil.addGet(primaryKey));
                        Method displayMethod = results.get(0).getClass().getMethod(PortalUtil.addGet(displayName));
                        Method primaryKeyMethodToString = primaryKeyMethod.getReturnType().getMethod("toString");
                        Method displayMethodToString = displayMethod.getReturnType().getMethod("toString");

                        for (Object result : results) {
                            Configuration c = createConfigurationEntity(primaryKeyMethod, displayMethod, primaryKeyMethodToString, displayMethodToString, result);
                            possibleSelectionValues.add(c);
                        }
                    } else {
                        for (Object result : results) {
                            Configuration c = new Configuration();
                            c.setDescription((String) result);
                            c.setValue((String) result);
                            possibleSelectionValues.add(c);
                        }
                    }
                }
                return possibleSelectionValues;
            } catch (Exception e) {
                throw new UnhandledException("Invalid spring configuration key!", e);
            }
        }

        private Configuration createConfigurationEntity(Method primaryKeyMethod, Method displayMethod, Method primaryKeyMethodToString, Method displayMethodToString, Object tmp) throws IllegalAccessException, InvocationTargetException {
            Configuration c = new Configuration();
            c.setDescription((String) displayMethodToString.invoke(displayMethod.invoke(tmp)));
            c.setValue((String) primaryKeyMethodToString.invoke(primaryKeyMethod.invoke(tmp)));
            return c;
        }

        private IModel<Configuration> newConfigurationModel() {
            return new IModel<Configuration>() {

                private static final long serialVersionUID = 1L;

                public Configuration getObject() {
                    return configuration;
                }

                public void setObject(Configuration object) {
                    configuration.setValue(object.getValue());
                }

                public void detach() {

                }
            };
        }
    }

    private class DateEditor extends Fragment {
        private static final long serialVersionUID = 1L;
        private Configuration configuration;

        public DateEditor(String id, Configuration configuration) {
            super(id, "dateEditor", ConfigurationPage.this);
            this.configuration = configuration;
            add(createDateField());
        }

        private DateTextField createDateField() {
            DateTextField dateTextField = new DateTextField("edit", new PropertyModel<Date>(configuration, "dateValue"));
            dateTextField.add(new DatePicker());
            dateTextField.setLabel(new PropertyModel<String>(configuration, "key"));
            dateTextField.setRequired(true);
            return dateTextField;
        }
    }

    private class GroupHeader extends Fragment {
        private static final long serialVersionUID = 1L;
        private String headline;

        public GroupHeader(String id, String headline) {
            super(id, "groupHeader", ConfigurationPage.this);
            this.headline = headline;
            add(createHeadlineLabel());
        }

        private Label createHeadlineLabel() {
            return new Label("headline", headline);
        }
    }
}
