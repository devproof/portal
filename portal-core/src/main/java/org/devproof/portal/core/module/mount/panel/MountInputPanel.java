package org.devproof.portal.core.module.mount.panel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.service.MountService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
// TODO unit test
// TODO vorbelegung
// TODO cleanup
public class MountInputPanel extends Panel {
    private static final long serialVersionUID = 1220436300695707556L;

    @SpringBean(name = "mountService")
    private MountService mountService;
    private IModel<List<MountPoint>> mountPointsModel;
    private IModel<List<MountPoint>> mountPointsToRemoveModel;
    private String handlerKey;
    private IModel<String> relatedContentIdModel;
//    private boolean submittedByRowModifcationLink = false;

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
                item.add(new TextField<String>("mountUrl", new PropertyModel<String>(item.getModel(), "mountPath")));
                item.add(new AjaxSubmitLink("addLink") {
                    private static final long serialVersionUID = -7468700234171627262L;

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        MountPoint mp = newMountPoint();
                        mountPointsModel.getObject().add(item.getIndex() + 1, mp);
                        target.addComponent(form);
//                        submittedByRowModifcationLink = true;
                    }
                });
                item.add(new AjaxSubmitLink("deleteLink") {

                    private static final long serialVersionUID = -6314148778603007009L;

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        List<MountPoint> mountPoints = mountPointsModel.getObject();
                        int index = item.getIndex();
                        MountPoint mountPoint = mountPoints.get(index);
                        mountPointsToRemoveModel.getObject().add(mountPoint);
                        mountPoints.remove(index);
                        target.addComponent(form);
//                        submittedByRowModifcationLink = true;
                    }
                });
                item.setOutputMarkupId(true);
            }
        };
        listView.setOutputMarkupId(true);
        form.add(listView);
        form.setOutputMarkupId(true);
        add(form);
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
        return new Form<MountPoint>("form") {
//
//            private static final long serialVersionUID = -2087270841617239034L;
//
//            @Override
//            protected void onSubmit() {
//                if(!submittedByRowModifcationLink) {
//
//                }
//                submittedByRowModifcationLink = false;
//            }
        };
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
