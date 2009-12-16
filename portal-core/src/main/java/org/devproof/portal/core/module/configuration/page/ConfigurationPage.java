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
package org.devproof.portal.core.module.configuration.page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.ConfigurationConstants;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.springframework.context.ApplicationContext;

/**
 * @author Carsten Hufe
 */
public class ConfigurationPage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	private List<ConfigurationEntity> allConfigurations = new ArrayList<ConfigurationEntity>();

	public ConfigurationPage(PageParameters params) {
		super(params);
		add(createConfigurationForm());
	}

	private Form<List<ConfigurationEntity>> createConfigurationForm() {
		Form<List<ConfigurationEntity>> form = newConfigurationForm();
		form.add(createTableRepeatingView());
		return form;
	}

	private RepeatingView createTableRepeatingView() {
		List<String> groups = configurationService.findConfigurationGroups();
		RepeatingView table = new RepeatingView("tableRow");
		for (String group : groups) {
			table.add(createGroupHeaderRowContainer(table.newChildId(), group));
			List<ConfigurationEntity> configurations = configurationService.findConfigurationsByGroup(group);
			for (ConfigurationEntity configuration : configurations) {
				table.add(createEditRowContainer(table.newChildId(), configuration));
				allConfigurations.add(configuration);
			}
		}
		return table;
	}

	private WebMarkupContainer createEditRowContainer(String id, ConfigurationEntity configuration) {
		WebMarkupContainer row = new WebMarkupContainer(id);
		row.add(createEditRowLabel(configuration));
		row.add(createEditorForConfiguration(configuration));
		return row;
	}

	private Label createEditRowLabel(ConfigurationEntity configuration) {
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

	private Form<List<ConfigurationEntity>> newConfigurationForm() {
		return new Form<List<ConfigurationEntity>>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				for (ConfigurationEntity configuration : allConfigurations) {
					configurationService.save(configuration);
				}
				configurationService.refreshGlobalConfiguration();
				info(getString("msg.saved"));
			}
		};
	}

	private Class<?> getClassByString(String type) {
		try {
			return Class.forName(type);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Override this method to provide custom editors for your fields.
	 * 
	 * @param configurationEntity
	 *            {@link ConfigurationEntity}
	 * @return editor fragment for for the matching configuration type
	 */

	protected Component createEditorForConfiguration(ConfigurationEntity configurationEntity) {
		if (configurationEntity.getKey().startsWith(ConfigurationConstants.SPRING_CONFIGURATION_PREFIX)) {
			// special case for accessing a spring dao
			return new SpringBeanEditor("editor", configurationEntity);
		} else {
			Class<?> clazz;
			try {
				clazz = Class.forName(configurationEntity.getType());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			if (Boolean.class.isAssignableFrom(clazz)) {
				return new BooleanEditor("editor", configurationEntity);
			} else if (clazz.isEnum()) {
				return new EnumEditor("editor", configurationEntity);

			} else if (Date.class.isAssignableFrom(clazz)) {
				return new DateEditor("editor", configurationEntity);
			} else {
				return new ValueEditor("editor", configurationEntity);
			}
		}
	}

	private class ValueEditor extends Fragment {
		private static final long serialVersionUID = 1L;
		private ConfigurationEntity configuration;

		public ValueEditor(String id, ConfigurationEntity configuration) {
			super(id, "valueEditor", ConfigurationPage.this);
			this.configuration = configuration;
			add(createAppropriateValueTextField());
		}

		private Component createAppropriateValueTextField() {
			Class<?> clazz = getClassByString(configuration.getType());
			if (Double.class.isAssignableFrom(clazz)) {
				return createDoubleTextField();
			} else if (Integer.class.isAssignableFrom(clazz)) {
				return createIntegerTextField();
			} else if (String.class.isAssignableFrom(clazz)) {
				return createStringTextField();
			} else {
				throw new IllegalArgumentException("Configuration type is not allowed!");
			}
		}

		private RequiredTextField<Integer> createIntegerTextField() {
			IModel<String> label = Model.of(configuration.getKey());
			RequiredTextField<Integer> textField = new RequiredTextField<Integer>("edit", new PropertyModel<Integer>(
					configuration, "integerValue"));
			textField.setLabel(label);
			return textField;
		}

		private RequiredTextField<Double> createDoubleTextField() {
			IModel<String> label = Model.of(configuration.getKey());
			RequiredTextField<Double> textField = new RequiredTextField<Double>("edit", new PropertyModel<Double>(
					configuration, "doubleValue"));
			textField.setLabel(label);
			return textField;
		}

		private RequiredTextField<String> createStringTextField() {
			IModel<String> label = Model.of(configuration.getKey());
			RequiredTextField<String> textField = new RequiredTextField<String>("edit", new PropertyModel<String>(
					configuration, "value"));
			textField.setLabel(label);
			return textField;
		}
	}

	private class BooleanEditor extends Fragment {
		private static final long serialVersionUID = 1L;
		private ConfigurationEntity configuration;

		public BooleanEditor(String id, ConfigurationEntity configuration) {
			super(id, "booleanEditor", ConfigurationPage.this);
			this.configuration = configuration;
			add(createCheckBox());
		}

		private FormComponent<Boolean> createCheckBox() {
			CheckBox checkBox = new CheckBox("edit", new PropertyModel<Boolean>(configuration, "booleanValue"));
			checkBox.setLabel(Model.of(configuration.getKey()));
			return checkBox;
		}
	}

	private class EnumEditor extends Fragment {
		private static final long serialVersionUID = 1L;
		private ConfigurationEntity configuration;

		public EnumEditor(String id, ConfigurationEntity configuration) {
			super(id, "enumEditor", ConfigurationPage.this);
			this.configuration = configuration;
			add(createEnumDropDownChoice());
		}

		private DropDownChoice<String> createEnumDropDownChoice() {
			List<String> enumChoices = getEnumChoices();
			DropDownChoice<String> ddc = new DropDownChoice<String>("edit", new PropertyModel<String>(configuration,
					"value"), enumChoices);
			ddc.setLabel(Model.of(configuration.getDescription()));
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

		private ConfigurationEntity configuration;
		private List<ConfigurationEntity> possibleSelectionValues;

		public SpringBeanEditor(String id, ConfigurationEntity configuration) {
			super(id, "springBeanEditor", ConfigurationPage.this);
			this.configuration = configuration;
			setPossibleSelectionValues();
			add(createSpringDropDownChoice());
		}

		private DropDownChoice<ConfigurationEntity> createSpringDropDownChoice() {
			DropDownChoice<ConfigurationEntity> ddc = new DropDownChoice<ConfigurationEntity>("edit",
					possibleSelectionValues, new ChoiceRenderer<ConfigurationEntity>("description", "value"));
			ddc.setModel(newConfigurationModel());
			ddc.setLabel(Model.of(configuration.getDescription()));
			ddc.setRequired(true);
			return ddc;
		}

		private void setPossibleSelectionValues() {
			possibleSelectionValues = new ArrayList<ConfigurationEntity>();
			String typeWithoutPrefix = configuration.getKey().substring(
					ConfigurationConstants.SPRING_CONFIGURATION_PREFIX.length());
			int index = typeWithoutPrefix.indexOf('.');
			String springBeanName = typeWithoutPrefix.substring(0, index);
			typeWithoutPrefix = typeWithoutPrefix.substring(index + 1);
			index = typeWithoutPrefix.indexOf('.');
			String methodName = typeWithoutPrefix.substring(0, index);
			typeWithoutPrefix = typeWithoutPrefix.substring(index + 1);
			index = typeWithoutPrefix.indexOf('.');
			String displayName = typeWithoutPrefix.substring(0, index);
			typeWithoutPrefix = typeWithoutPrefix.substring(index + 1);
			index = typeWithoutPrefix.indexOf('.');
			String primaryKey = typeWithoutPrefix.substring(0, index);

			ApplicationContext context = ((PortalApplication) getApplication()).getSpringContext();
			Object springBean = context.getBean(springBeanName);
			try {
				Method method = springBean.getClass().getMethod(methodName);
				List<?> results = (List<?>) method.invoke(springBean);
				if (results != null && results.size() > 0) {
					Method primaryKeyMethod = results.get(0).getClass().getMethod(PortalUtil.addGet(primaryKey));
					Method displayMethod = results.get(0).getClass().getMethod(PortalUtil.addGet(displayName));
					Method primaryKeyMethodToString = primaryKeyMethod.getReturnType().getMethod("toString");
					Method displayMethodToString = displayMethod.getReturnType().getMethod("toString");

					for (Object result : results) {
						ConfigurationEntity c = createConfigurationEntity(primaryKeyMethod, displayMethod,
								primaryKeyMethodToString, displayMethodToString, result);
						possibleSelectionValues.add(c);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Invalid spring configuration key!", e);
			}
		}

		private ConfigurationEntity createConfigurationEntity(Method primaryKeyMethod, Method displayMethod,
				Method primaryKeyMethodToString, Method displayMethodToString, Object tmp)
				throws IllegalAccessException, InvocationTargetException {
			ConfigurationEntity c = new ConfigurationEntity();
			c.setDescription((String) displayMethodToString.invoke(displayMethod.invoke(tmp)));
			c.setValue((String) primaryKeyMethodToString.invoke(primaryKeyMethod.invoke(tmp)));
			return c;
		}

		private IModel<ConfigurationEntity> newConfigurationModel() {
			return new IModel<ConfigurationEntity>() {

				private static final long serialVersionUID = 1L;

				public ConfigurationEntity getObject() {
					return configuration;
				}

				public void setObject(ConfigurationEntity object) {
					configuration.setValue(object.getValue());
				}

				public void detach() {

				}
			};
		}
	}

	private class DateEditor extends Fragment {
		private static final long serialVersionUID = 1L;
		private ConfigurationEntity configuration;

		public DateEditor(String id, ConfigurationEntity configuration) {
			super(id, "dateEditor", ConfigurationPage.this);
			this.configuration = configuration;
			add(createDateTextField());
		}

		private DateTextField createDateTextField() {
			DateTextField dateTextField = new DateTextField("edit", new PropertyModel<Date>(configuration, "dateValue"));
			dateTextField.add(new DatePicker());
			dateTextField.setLabel(Model.of(configuration.getKey()));
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
