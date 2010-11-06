/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.uploadcenter.model;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.uploadcenter.UploadCenterConstants;
import org.devproof.portal.module.uploadcenter.bean.FileBean;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;

/**
 * @author Carsten Hufe
 */
public class FileTreeModel implements TreeModel, Serializable {
    private static final long serialVersionUID = -2968099023331323691L;
    private DefaultMutableTreeNode root;
    private File rootFolder;

    @SpringBean(name = "displayDateTimeFormat")
    private DateFormat dateFormat;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    public FileTreeModel() {
        InjectorHolder.getInjector().inject(this);
        this.rootFolder = configurationService.findAsFile(UploadCenterConstants.CONF_UPLOADCENTER_FOLDER);
        this.root = getNode(rootFolder);
    }

    private DefaultMutableTreeNode getNode(File file) {
        if (file.isDirectory()) {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(new FileBean(file, dateFormat));
            File[] tmpFiles = file.listFiles();
            if(tmpFiles != null) {
				for (File tmpFile : tmpFiles) {
	            	folderNode.add(getNode(tmpFile));
	            }
            }
            return folderNode;
        } else {
            return new DefaultMutableTreeNode(new FileBean(file, dateFormat));
        }
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return getFileFromNode(node).isFile();
    }

    private File getFileFromNode(Object obj) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
        FileBean o = (FileBean) node.getUserObject();
        return o.getFile();
    }

    public int getChildCount(Object parent) {
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parent;
        return parentNode.getChildCount();
//        File file = getFileFromNode(parent);
//        String[] children = file.list();
//        if (children == null) {
//            return 0;
//        }
//        return children.length;
    }

    public Object getChild(Object parent, int index) {
//        File parentFile = getFileFromNode(parent);
//        String[] children = parentFile.list();
//
//        if ((children == null) || (index >= children.length)) {
//            return null;
//        }
//        File file = new File(parentFile, children[index]);
//     return getNode(file);
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parent;
        return parentNode.getChildAt(index);
    }

    public int getIndexOfChild(Object parent, Object child) {
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parent;
        return parentNode.getIndex((TreeNode) child);
//        File parentFile = getFileFromNode(parent);
//        String[] children = parentFile.list();
//
//        if (children == null) {
//            return -1;
//        }
//        File childFile = getFileFromNode(child);
//        String childname = childFile.getName();
//        for (int i = 0; i < children.length; i++) {
//            if (childname.equals(children[i])) {
//                return i;
//            }
//        }
//        return -1;
    }

    public void valueForPathChanged(TreePath path, Object newvalue) {
    }

    public void addTreeModelListener(TreeModelListener l) {
    }

    public void removeTreeModelListener(TreeModelListener l) {
    }

    public void forceReload() {
        this.root = getNode(rootFolder);
    }
}
