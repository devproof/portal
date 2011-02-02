package org.devproof.portal.core.module.mount.panel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
 * @author Carsten Hufe
 */
// TODO unit test
// TODO vorbelegung
// TODO cleanup
// TODO remove doubles in gleichen und bei anderen
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
        this.mountPointsToRemoveModel = new ListModel<MountPoint>(new ArrayList<MountPoint>());

        final Form<MountPoint> form = newForm();

        ListView<MountPoint> listView = new ListView<MountPoint>("mountUrls", mountPointsModel) {
            private static final long serialVersionUID = -7992014373658362790L;

            @Override
            protected void populateItem(final ListItem<MountPoint> item) {
                item.add(createMountUrlField(item));
                item.add(createAddLink(item));
                item.add(createDeleteLink(item));
                item.add(createOverrideCheckBox(item));
                // TODO ....
                item.add(new Label("overrideDescription", "here some stuff"));
                item.setOutputMarkupId(true);
            }
        };
        listView.setOutputMarkupId(true);
        form.add(listView);
        form.setOutputMarkupId(true);
        add(form);
    }

    private CheckBox createOverrideCheckBox(final ListItem<MountPoint> item) {
        CheckBox overrideInput = new CheckBox("overrideInput", new Model<Boolean>()) {
            private static final long serialVersionUID = -5152560765406977370L;

            @Override
            public boolean isEnabled() {
                return true;
//                return item.getModelObject().getMountPath().co
            }
        };
//        overrideInput.setRequired(true);
        return overrideInput;
    }

    private TextField<String> createMountUrlField(ListItem<MountPoint> item) {
        final AutoCompleteTextField<String> tf = new AutoCompleteTextField<String>("mountUrl", new PropertyModel<String>(item.getModel(), "mountPath")) {
            private static final long serialVersionUID = -6260288249626574703L;

            @Override
            protected Iterator<String> getChoices(String input) {
                return mountService.findMountPointsStartingWith(input).iterator();
            }


        };
        tf.add(new AjaxFormComponentUpdatingBehavior("onblur") {
            private static final long serialVersionUID = 4284924299561641727L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String value = tf.getValue();
                if(StringUtils.isNotBlank(value)) {
                    if(!value.startsWith("/")) {
                        value = "/" + value;
                    }
                    value = StringUtils.deleteWhitespace(value);
                    value = StringUtils.removeEnd(value, "/");
                    tf.setModelObject(value);
                    target.addComponent(tf);
                }
            }

//            @Override
//            protected void onEvent(AjaxRequestTarget target) {
//
////                target.appendJavascript("alert('refresh here for check box');");
//            }
        });
//        tf.setRequired(true);
        tf.setOutputMarkupId(true);
        return tf;
    }

    private AjaxSubmitLink createAddLink(final ListItem<MountPoint> item) {
        AjaxSubmitLink link = new AjaxSubmitLink("addLink") {
            private static final long serialVersionUID = -7468700234171627262L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                MountPoint mp = newMountPoint();
                mountPointsModel.getObject().add(item.getIndex() + 1, mp);
                target.addComponent(form);
            }
        };
        link.add(new Image("addImage", CommonConstants.REF_ADD_IMG));
        return link;
    }

    private AjaxSubmitLink createDeleteLink(final ListItem<MountPoint> item) {
        AjaxSubmitLink link = new AjaxSubmitLink("deleteLink") {

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
        };
        link.add(new Image("deleteImage", CommonConstants.REF_DELETE_IMG));
        return link;
    }

    private MountPoint newMountPoint() {
        MountPoint mp = new MountPoint();
        mp.setHandlerKey(this.handlerKey);
        return mp;
    }

    private IModel<List<MountPoint>> createMountPointsModel() {
        String relatedContentId = relatedContentIdModel.getObject();
        List<MountPoint> mountPoints = new ArrayList<MountPoint>();
        if(StringUtils.isEmpty(relatedContentId)) {
            mountPoints.add(newMountPoint());
        }
        else {
            mountPoints = mountService.findMountPoints(relatedContentId, handlerKey);
        }
        return new ListModel<MountPoint>(mountPoints);
    }

    private Form<MountPoint> newForm() {
        return new Form<MountPoint>("form");
    }

    public void storeMountPoints() {
                            String relatedContentId = relatedContentIdModel.getObject();
                    List<MountPoint> mountPoints = mountPointsModel.getObject();
                    for(MountPoint mountPoint : mountPoints) {
                        mountPoint.setRelatedContentId(relatedContentId);
                        mountService.save(mountPoint);
                    }
                    List<MountPoint> mountPointsToRemove = mountPointsToRemoveModel.getObject();
                    for(MountPoint mountPoint : mountPointsToRemove) {
                        if(!mountPoint.isTransient()) {
                            mountService.delete(mountPoint);
                        }
                    }
    }
}
