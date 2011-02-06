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
package org.devproof.portal.core.module.mount.panel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.service.MountService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides the UI to handle global mount URLs
 *
 * @author Carsten Hufe
 */
public class MountInputPanel extends Panel {
    private static final long serialVersionUID = 1220436300695707556L;

    @SpringBean(name = "mountService")
    private MountService mountService;
    private IModel<List<MountPoint>> mountPointsModel;
    private IModel<List<MountPoint>> mountPointsToRemoveModel;
    private String handlerKey;
    private IModel<String> relatedContentIdModel;

    public MountInputPanel(String id, String handlerKey, IModel<String> relatedContentIdModel) {
        super(id);
        this.handlerKey = handlerKey;
        this.relatedContentIdModel = relatedContentIdModel;
        this.mountPointsModel = createMountPointsModel();
        this.mountPointsToRemoveModel = createMountPointsToRemoveModel();
        add(createForm());
    }

    private Form<MountPoint> createForm() {
        Form<MountPoint> form = newForm();
        form.add(createDefaultUrlGroup());
        form.setOutputMarkupId(true);
        return form;
    }

    private RadioGroup<Integer> createDefaultUrlGroup() {
        RadioGroup<Integer> defaultUrlGroup = new RadioGroup<Integer>("defaultUrlGroup", createDefaultUrlGroupModel());
        defaultUrlGroup.add(createMountUrlListView());
        return defaultUrlGroup;
    }

    private IModel<Integer> createDefaultUrlGroupModel() {
        return new IModel<Integer>() {
            private static final long serialVersionUID = -8339571053130192758L;

            @Override
            public Integer getObject() {
                return findSelectedIndex(mountPointsModel.getObject());
            }

            @Override
            public void setObject(Integer object) {
                List<MountPoint> mountPoints = mountPointsModel.getObject();
                for (int i = 0; i < mountPoints.size(); i++) {
                    MountPoint mountPoint = mountPoints.get(i);
                    mountPoint.setDefaultUrl(object != null && object == i);
                }
            }

            @Override
            public void detach() {
            }
        };
    }

    private ListModel<MountPoint> createMountPointsToRemoveModel() {
        return new ListModel<MountPoint>(new ArrayList<MountPoint>());
    }

    private ListView<MountPoint> createMountUrlListView() {
        ListView<MountPoint> listView = newMountUrlListView();
        listView.setOutputMarkupId(true);
        return listView;
    }

    private ListView<MountPoint> newMountUrlListView() {
        return new ListView<MountPoint>("mountUrls", mountPointsModel) {
            private static final long serialVersionUID = -7992014373658362790L;

            @Override
            protected void populateItem(final ListItem<MountPoint> item) {
                item.add(createMountPointLine(item));
                item.setOutputMarkupId(true);
            }
        };
    }

    private MountPointLine createMountPointLine(ListItem<MountPoint> item) {
        return new MountPointLine("mountPointLine", item);
    }


    private MountPoint newMountPoint() {
        MountPoint mp = new MountPoint();
        mp.setHandlerKey(this.handlerKey);
        return mp;
    }

    private IModel<List<MountPoint>> createMountPointsModel() {
        String relatedContentId = relatedContentIdModel.getObject();
        List<MountPoint> mountPoints = new ArrayList<MountPoint>();
        if (StringUtils.isNotEmpty(relatedContentId)) {
            mountPoints = mountService.findMountPoints(relatedContentId, handlerKey);
        }
        if (mountPoints.size() == 0) {
            MountPoint mp = newMountPoint();
            mp.setDefaultUrl(true);
            mountPoints.add(mp);
        }
        return new ListModel<MountPoint>(mountPoints);
    }

    private Form<MountPoint> newForm() {
        return new Form<MountPoint>("form");
    }

    /**
     * saves the selected mount points
     */
    public void storeMountPoints() {
        mountService.save(mountPointsModel.getObject(), relatedContentIdModel.getObject());
        mountService.delete(mountPointsToRemoveModel.getObject());
    }

    private int findSelectedIndex(List<MountPoint> mountPoints) {
        for (int i = 0; i < mountPoints.size(); i++) {
            MountPoint mountPoint = mountPoints.get(i);
            if (mountPoint.isDefaultUrl()) {
                return i;
            }
        }
        return -1;
    }

    private class MountPointLine extends Fragment {
        private static final long serialVersionUID = 1L;
        private ListItem<MountPoint> item;
        private AutoCompleteTextField<String> mountUrlField;
        private Label overrideDescriptionLabel;

        public MountPointLine(String id, ListItem<MountPoint> item) {
            super(id, "mountPointLine", MountInputPanel.this);
            this.item = item;
            add(createMountUrlField());
            add(createAddLink());
            add(createDeleteLink());
            add(createDefaultUrlRadio());
            add(createOverrideDescriptionLabel());
        }

        private Radio<Integer> createDefaultUrlRadio() {
            return new Radio<Integer>("defaultUrl", new PropertyModel<Integer>(item, "index"));
        }

        private Label createOverrideDescriptionLabel() {
            overrideDescriptionLabel = newOverrideDescriptionLabel();
            overrideDescriptionLabel.setOutputMarkupId(true);
            return overrideDescriptionLabel;
        }

        private Label newOverrideDescriptionLabel() {
            return new Label("overrideDescription", new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = -245277654798313891L;

                @Override
                public String getObject() {
                    MountPoint mountPoint = item.getModelObject();
                    String mountPath = mountPoint.getMountPath();
                    boolean existsPath = mountService.existsPath(mountPath);
                    String mountPointRelatedContentId = mountPoint.getRelatedContentId();
                    if (existsPath) {
                        MountPoint existingMountPoint = mountService.resolveMountPoint(mountPath);
                        if (!existingMountPoint.getRelatedContentId().equals(mountPointRelatedContentId)) {
                            return getString("warning.override");
                        }
                    }
                    return "";
                }
            });
        }

        private TextField<String> createMountUrlField() {
            mountUrlField = newMountUrlField();
            mountUrlField.add(createOnBlurBeaviour());
            mountUrlField.setOutputMarkupId(true);
            return mountUrlField;
        }

        private AutoCompleteTextField<String> newMountUrlField() {
            return new AutoCompleteTextField<String>("mountUrl", new PropertyModel<String>(item.getModel(), "mountPath")) {
                private static final long serialVersionUID = -6260288249626574703L;

                @Override
                protected Iterator<String> getChoices(String input) {
                    return mountService.findMountPointsStartingWith(input).iterator();
                }
            };
        }

        private AjaxFormComponentUpdatingBehavior createOnBlurBeaviour() {
            return new AjaxFormComponentUpdatingBehavior("onblur") {
                private static final long serialVersionUID = 4284924299561641727L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    String value = mountUrlField.getValue();
                    if (StringUtils.isNotBlank(value)) {
                        if (!value.startsWith("/")) {
                            value = "/" + value;
                        }
                        value = StringUtils.deleteWhitespace(value);
                        value = StringUtils.removeEnd(value, "/");
                        mountUrlField.setModelObject(value);
                        target.addComponent(mountUrlField);
                        target.addComponent(overrideDescriptionLabel);
                    }
                }

            };
        }

        private AjaxSubmitLink createAddLink() {
            AjaxSubmitLink link = newAddLink();
            link.add(createAddImage());
            return link;
        }

        private Image createAddImage() {
            return new Image("addImage", CommonConstants.REF_ADD_IMG);
        }

        private AjaxSubmitLink newAddLink() {
            return new AjaxSubmitLink("addLink") {
                private static final long serialVersionUID = -7468700234171627262L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    MountPoint mp = newMountPoint();
                    mountPointsModel.getObject().add(item.getIndex() + 1, mp);
                    target.addComponent(form);
                }
            };
        }

        private AjaxSubmitLink createDeleteLink() {
            AjaxSubmitLink link = newDeleteLink();
            link.add(createDeleteImage());
            return link;
        }

        private Image createDeleteImage() {
            return new Image("deleteImage", CommonConstants.REF_DELETE_IMG);
        }

        private AjaxSubmitLink newDeleteLink() {
            return new AjaxSubmitLink("deleteLink") {

                private static final long serialVersionUID = -6314148778603007009L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    List<MountPoint> mountPoints = mountPointsModel.getObject();
                    int index = item.getIndex();
                    MountPoint mountPoint = mountPoints.get(index);
                    mountPointsToRemoveModel.getObject().add(mountPoint);
                    mountPoints.remove(index);
                    target.addComponent(form);
                }

                @Override
                public boolean isVisible() {
                    return mountPointsModel.getObject().size() > 1;
                }
            };
        }
    }
}
