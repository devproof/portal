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
package org.devproof.portal.core.module.user.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.user.entity.UserEntity;

import java.text.SimpleDateFormat;

/**
 * @author Carsten Hufe
 */
public class UserInfoPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "displayDateFormat")
	private SimpleDateFormat dateFormat;
	@SpringBean(name = "displayDateTimeFormat")
	private SimpleDateFormat dateTimeFormat;
    private IModel<UserEntity> userModel;

    public UserInfoPanel(String id, IModel<UserEntity> userModel) {
		super(id, new CompoundPropertyModel<UserEntity>(userModel));
        this.userModel = userModel;
        add(createUsernameLabel());
		add(createFirstnameLabel());
		add(createLastnameLabel());
		add(createBirthdayLabel());
		add(createEmailLabel());
		add(createActiveLabel());
		add(createConfirmedLabel());
		add(createRegistrationDateLabel());
		add(createLastLoginTimeLabel());
		add(createLastIpLabel());
	}

	private Label createLastIpLabel() {
		return new Label("lastIp");
	}

	private Label createLastLoginTimeLabel() {
        IModel<String> lastLoginAtModel = createLastLoginAtModel();
        return new Label("lastLoginAt", lastLoginAtModel);
	}

    private IModel<String> createLastLoginAtModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 205970170080353722L;

            @Override
            public String getObject() {
                UserEntity user = userModel.getObject();
                return user.getLastLoginAt() != null ? dateTimeFormat.format(user.getLastLoginAt())	: "";
            }
        };
    }

    private Label createRegistrationDateLabel() {
		return new Label("registeredAt", createRegistrationDateModel());
	}

    private IModel<String> createRegistrationDateModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = -3479899287276996424L;

            @Override
            public String getObject() {
                UserEntity user = userModel.getObject();
                return user.getRegistrationDate() != null ? dateTimeFormat.format(user.getRegistrationDate()) : "";
            }
        };
    }

	private Label createConfirmedLabel() {
		return new Label("confirmed", createConfirmedModel());
	}

    private IModel<String> createConfirmedModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = -5826310794583978729L;

            @Override
            public String getObject() {
                UserEntity user = userModel.getObject();
                return user.getConfirmed() != null ? getString("confirmed." + user.getConfirmed().toString()) : "";
            }
        };
    }

	private Label createActiveLabel() {
		return new Label("active", createActiveModel());
	}

    private IModel<String> createActiveModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = -8308295898891386393L;

            @Override
            public String getObject() {
                UserEntity user = userModel.getObject();
                return  user.getActive() != null ? getString("active." + user.getActive().toString()) : "";
            }
        };
    }

	private Label createEmailLabel() {
		return new Label("email");
	}

	private Label createBirthdayLabel() {
		return new Label("birthday", createBirthdayModel());
	}

    private IModel<String> createBirthdayModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = -1935766462928249555L;
            @Override
            public String getObject() {
                UserEntity user = userModel.getObject();
                return user.getBirthday() != null ? dateFormat.format(user.getBirthday()) : "";
            }
        };
    }

	private Label createLastnameLabel() {
		return new Label("lastname");
	}

	private Label createFirstnameLabel() {
		return new Label("firstname");
	}

	private Label createUsernameLabel() {
		return new Label("username");
	}
}
