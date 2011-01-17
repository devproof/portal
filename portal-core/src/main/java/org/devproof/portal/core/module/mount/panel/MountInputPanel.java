package org.devproof.portal.core.module.mount.panel;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
public class MountInputPanel extends Panel {
    private static final long serialVersionUID = 1220436300695707556L;

    @SpringBean(name = "mountService")
    private MountService mountService;

    public MountInputPanel(String id, String handlerKey) {
        super(id);
        Form<MountPoint> form = newForm();

        List<MountPoint> mountPoints = new ArrayList<MountPoint>();
        mountPoints.add(new MountPoint());
        IModel<List<MountPoint>> mountPointsModel = new ListModel<MountPoint>(mountPoints);
        ListView<MountPoint> listView = new ListView<MountPoint>("mountUrls", mountPointsModel) {
            private static final long serialVersionUID = -7992014373658362790L;

            @Override
            protected void populateItem(ListItem<MountPoint> item) {
                item.add(new TextField<String>("mountUrl", new PropertyModel<String>(item.getModel(), "mountPath")));
            }
        };
        form.add(listView);
        add(form);
    }

    private Form<MountPoint> newForm() {
        return new Form<MountPoint>("form");
    }

    public void save(String relatedContentId) {

    }
}
