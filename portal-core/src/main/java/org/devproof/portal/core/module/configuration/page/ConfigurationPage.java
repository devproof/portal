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

	public ConfigurationPage(final PageParameters params) {
		super(params);
		final List<ConfigurationEntity> allConfigurations = new ArrayList<ConfigurationEntity>();
		Form<List<ConfigurationEntity>> form = new Form<List<ConfigurationEntity>>("form") {
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
		add(form);

		List<String> groups = configurationService.findConfigurationGroups();
		RepeatingView tableRow = new RepeatingView("tableRow");
		form.add(tableRow);
		for (String group : groups) {
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
			row.add(new GroupHeader("description", group));
			row.add(new Label("editor", ""));
			tableRow.add(row);

			List<ConfigurationEntity> configurations = configurationService.findConfigurationsByGroup(group);
			for (ConfigurationEntity configuration : configurations) {
				row = new WebMarkupContainer(tableRow.newChildId());
				row.add(new Label("description", configuration.getDescription()));
				row.add(getEditorForConfiguration(configuration));
				tableRow.add(row);
				allConfigurations.add(configuration);
			}
		}
	}

	/**
	 * Override this method to provide custom editors for your fields.
	 * 
	 * @param configurationEntity
	 *            {@link ConfigurationEntity}
	 * @return editor fragment for for the matching configuration type
	 */

	protected Component getEditorForConfiguration(final ConfigurationEntity configurationEntity) {
		if (configurationEntity.getKey().startsWith(ConfigurationConstants.SPRING_CONFIGURATION_PREFIX)) {
			// special case foraccessing a spring dao
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

		public ValueEditor(final String id, final ConfigurationEntity configurationEntity) {
			super(id, "valueEditor", ConfigurationPage.this);
			Class<?> clazz;
			try {
				clazz = Class.forName(configurationEntity.getType());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			IModel<String> label = Model.of(configurationEntity.getKey());
			if (Double.class.isAssignableFrom(clazz)) {
				add(new RequiredTextField<Double>("edit", new PropertyModel<Double>(configurationEntity, "doubleValue"))
						.setLabel(label));
			} else if (Integer.class.isAssignableFrom(clazz)) {
				add(new RequiredTextField<Integer>("edit", new PropertyModel<Integer>(configurationEntity,
						"integerValue")).setLabel(label));
			} else if (String.class.isAssignableFrom(clazz)) {
				add(new RequiredTextField<String>("edit", new PropertyModel<String>(configurationEntity, "value"))
						.setLabel(label));
			} else {
				throw new IllegalArgumentException("Configuration type is not allowed!");
			}
		}
	}

	private class BooleanEditor extends Fragment {
		private static final long serialVersionUID = 1L;

		public BooleanEditor(final String id, final ConfigurationEntity configurationEntity) {
			super(id, "booleanEditor", ConfigurationPage.this);
			add(new CheckBox("edit", new PropertyModel<Boolean>(configurationEntity, "booleanValue")).setLabel(Model
					.of(configurationEntity.getKey())));
		}
	}

	private class EnumEditor extends Fragment {
		private static final long serialVersionUID = 1L;

		public EnumEditor(final String id, final ConfigurationEntity configurationEntity) {
			super(id, "enumEditor", ConfigurationPage.this);
			Class<?> clazz;
			try {
				clazz = Class.forName(configurationEntity.getType());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}

			Object tmp[] = clazz.getEnumConstants();
			List<String> values = new ArrayList<String>();
			for (int i = 0; i < tmp.length; i++) {
				values.add(((Enum<?>) tmp[i]).name());
			}
			DropDownChoice<String> ddc = new DropDownChoice<String>("edit", new PropertyModel<String>(
					configurationEntity, "value"), values);
			ddc.setLabel(Model.of(configurationEntity.getDescription()));
			ddc.setRequired(true);

			add(ddc);
		}
	}

	private class SpringBeanEditor extends Fragment {
		private static final long serialVersionUID = 1L;

		public SpringBeanEditor(final String id, final ConfigurationEntity configurationEntity) {
			super(id, "springBeanEditor", ConfigurationPage.this);

			String typeWithoutPrefix = configurationEntity.getKey().substring(
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
			List<ConfigurationEntity> result = new ArrayList<ConfigurationEntity>();
			try {
				Method method = springBean.getClass().getMethod(methodName);
				List<?> tmpResult = (List<?>) method.invoke(springBean);
				if (tmpResult != null && tmpResult.size() > 0) {
					Method primaryKeyMethod = tmpResult.get(0).getClass().getMethod(PortalUtil.addGet(primaryKey));
					Method displayMethod = tmpResult.get(0).getClass().getMethod(PortalUtil.addGet(displayName));
					Method primaryKeyMethodToString = primaryKeyMethod.getReturnType().getMethod("toString");
					Method displayMethodToString = displayMethod.getReturnType().getMethod("toString");

					for (Object tmp : tmpResult) {
						ConfigurationEntity c = new ConfigurationEntity();
						c.setDescription((String) displayMethodToString.invoke(displayMethod.invoke(tmp)));
						c.setValue((String) primaryKeyMethodToString.invoke(primaryKeyMethod.invoke(tmp)));
						result.add(c);
					}
				}

			} catch (Exception e) {
				throw new RuntimeException("Invalid spring configuration key!", e);
			}

			DropDownChoice<ConfigurationEntity> ddc = new DropDownChoice<ConfigurationEntity>("edit", result,
					new ChoiceRenderer<ConfigurationEntity>("description", "value"));
			ddc.setModel(new IModel<ConfigurationEntity>() {

				private static final long serialVersionUID = 1L;

				public ConfigurationEntity getObject() {
					return configurationEntity;
				}

				public void setObject(final ConfigurationEntity object) {
					configurationEntity.setValue(object.getValue());
				}

				public void detach() {

				}
			});
			ddc.setLabel(Model.of(configurationEntity.getDescription()));
			ddc.setRequired(true);

			add(ddc);
		}
	}

	private class DateEditor extends Fragment {
		private static final long serialVersionUID = 1L;

		public DateEditor(final String id, final ConfigurationEntity configurationEntity) {
			super(id, "dateEditor", ConfigurationPage.this);
			DateTextField dateTextField = new DateTextField("edit", new PropertyModel<Date>(configurationEntity,
					"dateValue"));
			dateTextField.add(new DatePicker());
			add(dateTextField.setLabel(Model.of(configurationEntity.getKey())).setRequired(true));
		}
	}

	private class GroupHeader extends Fragment {
		private static final long serialVersionUID = 1L;

		public GroupHeader(final String id, final String headline) {
			super(id, "groupHeader", ConfigurationPage.this);
			add(new Label("headline", headline));
		}
	}
}
