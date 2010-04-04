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
package org.devproof.portal.core.module.common.panel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.panel.UsernamePanel;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * the part in blogs downloads, etc "created by [name] at [date]
 * 
 * @author Carsten Hufe
 */
public class MetaInfoPanel<T extends BaseEntity> extends Panel {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "displayDateFormat")
	private SimpleDateFormat dateFormat;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	@SpringBean(name = "userService")
	private UserService userService;

    private IModel<T> entityModel;

    public MetaInfoPanel(String id, IModel<T> entityModel) {
		super(id, entityModel);
        this.entityModel = entityModel;
		add(createCreatedContainer());
		add(createModifiedContainer());
		add(createSameModifierCreatorContainer());
	}

    private WebMarkupContainer createSameModifierCreatorContainer() {
        WebMarkupContainer sameModified = newSameModifierCreatorContainer();
        sameModified.add(createModifiedAtLabel());
		return sameModified;
	}

    private WebMarkupContainer newSameModifierCreatorContainer() {
        return new WebMarkupContainer("sameModified") {
            private static final long serialVersionUID = 5672313742753946319L;
            @Override
            public boolean isVisible() {
                return showModifiedBy() && isSameAuthor() && !isEqualCreationModificationTime()
                            && !showModifiedAtAsCreatedAt();
            }
        };
    }

    private boolean isSameAuthor() {
        BaseEntity entity = entityModel.getObject();
        return entity.getCreatedBy().equals(entity.getModifiedBy());
	}

    private boolean isEqualCreationModificationTime() {
        BaseEntity entity = entityModel.getObject();
		return entity.getCreatedAt().equals(entity.getModifiedAt());
	}

    private Label createModifiedAtLabel() {
        IModel<String> modifiedAtModel = createModifiedAtModel();
        return new Label("modifiedAt", modifiedAtModel);
	}


    private IModel<String> createModifiedAtModel() {
        return new LoadableDetachableModel<String>() {
            private static final long serialVersionUID = -1304908710263470243L;

            @Override
            protected String load() {
                BaseEntity entity = entityModel.getObject();
		        return dateFormat.format(entity.getModifiedAt());
            }
        };
    }

    private WebMarkupContainer createModifiedContainer() {
        WebMarkupContainer modified = newModifiedContainer();
        modified.add(createModifiedAtLabel());
		modified.add(createModifiedUsernamePanel());
		return modified;
	}

    private WebMarkupContainer newModifiedContainer() {
        return new WebMarkupContainer("modified") {
            @Override
            public boolean isVisible() {
                return showModifiedBy() && !isSameAuthor() && !isEqualCreationModificationTime()
                                && !showModifiedAtAsCreatedAt();
            }
        };
    }

    private UsernamePanel createModifiedUsernamePanel() {
        IModel<String> modifiedByModel = new PropertyModel<String>(entityModel, "modifiedBy");
		return new UsernamePanel("modifiedBy", modifiedByModel) {
            @Override
            protected boolean showRealName() {
                return showRealAuthor();
            }
        };
	}

    private WebMarkupContainer createCreatedContainer() {
		WebMarkupContainer created = new WebMarkupContainer("created");
		created.add(createCreatedAtLabel());
		created.add(createCreatedUsernamePanel());
		return created;
	}

    private UsernamePanel createCreatedUsernamePanel() {
        IModel<String> createdByModel = new PropertyModel<String>(entityModel, "createdBy");
        return new UsernamePanel("createdBy", createdByModel) {
            private static final long serialVersionUID = 7238227449225588141L;
            @Override
            protected boolean showRealName() {
                return showRealAuthor();
            }
        };
	}

    private Label createCreatedAtLabel() {
		return new Label("createdAt", createCreatedAtModel());
	}

    private IModel<String> createCreatedAtModel() {
        return new LoadableDetachableModel<String>() {
            private static final long serialVersionUID = -1304908710263470243L;

            @Override
            protected String load() {
                BaseEntity entity = entityModel.getObject();
                Date created = showModifiedAtAsCreatedAt() ? entity.getModifiedAt() : entity.getCreatedAt();
		        return dateFormat.format(created);
            }
        };
    }

    private Boolean showModifiedBy() {
        return configurationService.findAsBoolean(CommonConstants.CONF_SHOW_MODIFIED_BY);
    }

    private Boolean showRealAuthor() {
        return configurationService.findAsBoolean(CommonConstants.CONF_SHOW_REAL_AUTHOR);
    }

    private Boolean showModifiedAtAsCreatedAt() {
        return configurationService
                .findAsBoolean(CommonConstants.CONF_SHOW_MODIFIED_AT_AS_CREATED_AT);
    }
}
