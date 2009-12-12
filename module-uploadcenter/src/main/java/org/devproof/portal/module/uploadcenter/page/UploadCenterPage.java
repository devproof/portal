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
package org.devproof.portal.module.uploadcenter.page;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.PageCreator;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.IRenderable;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyRenderableColumn;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.uploadcenter.UploadCenterConstants;
import org.devproof.portal.module.uploadcenter.bean.FileBean;
import org.devproof.portal.module.uploadcenter.panel.CreateFolderPanel;
import org.devproof.portal.module.uploadcenter.panel.UploadCenterPanel;

/**
 * @author Carsten Hufe
 */
public class UploadCenterPage extends TemplatePage {

	private static final long serialVersionUID = 3247255196536400455L;
	@SpringBean(name = "dateTimeFormat")
	private DateFormat dateFormat;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private File rootFolder;
	private File selectedFolder;
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode selectedNode;
	private boolean hasRightCreateDownload;
	private ModalWindow modalWindow;
	private TreeTable folderTreeTable;

	public UploadCenterPage(PageParameters params) {
		super(params);
		rootFolder = configurationService.findAsFile(UploadCenterConstants.CONF_UPLOADCENTER_FOLDER);
		selectedFolder = rootFolder;
		setHasRightToCreate();
		add(modalWindow = createModalWindow());
		add(folderTreeTable = createFolderTreeTable());
		addPageAdminBoxLink(createUploadLink());
		addPageAdminBoxLink(createFolderLink());
	}

	private AjaxLink<ModalWindow> createFolderLink() {
		AjaxLink<ModalWindow> createFolderLink = newCreateFolderLink();
		createFolderLink.add(new Label("linkName", getString("createFolderLink")));
		return createFolderLink;
	}

	private AjaxLink<ModalWindow> createUploadLink() {
		AjaxLink<ModalWindow> uploadLink = newUploadLink(modalWindow);
		uploadLink.add(new Label("linkName", getString("uploadLink")));
		return uploadLink;
	}

	private TreeTable createFolderTreeTable() {
		IColumn columns[] = new IColumn[] {
				new PropertyTreeColumn(new ColumnLocation(Alignment.MIDDLE, 8, Unit.PROPORTIONAL), this
						.getString("tableFilename"), "userObject.name"),
				new PropertyRenderableColumn(new ColumnLocation(Alignment.MIDDLE, 4, Unit.PROPORTIONAL), this
						.getString("tableFilesize"), "userObject.size"),
				new PropertyRenderableColumn(new ColumnLocation(Alignment.MIDDLE, 4, Unit.PROPORTIONAL), this
						.getString("tableFiledate"), "userObject.date"),
				new PropertyLinkedColumn(new ColumnLocation(Alignment.RIGHT, 80, Unit.PX), "", "userObject.file",
						modalWindow) };

		TreeTable tree = newFolderTreeTable(columns);
		tree.getTreeState().collapseAll();
		tree.setRootLess(true);
		return tree;
	}

	private TreeTable newFolderTreeTable(IColumn[] columns) {
		return new TreeTable("treeTable", createTreeModel(), columns) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node) {
				if (getTreeState().isNodeSelected(node)) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) node;
					FileBean fileBean = (FileBean) n.getUserObject();

					if (fileBean.getFile().isDirectory()) {
						selectedFolder = fileBean.getFile();
						selectedNode = (DefaultMutableTreeNode) node;
					} else {
						selectedFolder = fileBean.getFile().getParentFile();
						selectedNode = (DefaultMutableTreeNode) node.getParent();
					}
				} else {
					selectedFolder = rootFolder;
					selectedNode = rootNode;
				}
			}
		};
	}

	private AjaxLink<ModalWindow> newCreateFolderLink() {
		return new AjaxLink<ModalWindow>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalWindow.setInitialHeight(170);
				modalWindow.setInitialWidth(400);
				modalWindow.setContent(createCreateFolderPanel());
				modalWindow.show(target);
			}

			private CreateFolderPanel createCreateFolderPanel() {
				return new CreateFolderPanel(modalWindow.getContentId(), selectedFolder) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onCreate(AjaxRequestTarget target) {
						UploadCenterPage.this.forceRefresh(target);
						modalWindow.close(target);
					}

				};
			}
		};
	}

	private ModalWindow createModalWindow() {
		ModalWindow modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		return modalWindow;
	}

	private void setHasRightToCreate() {
		PortalSession session = (PortalSession) getSession();
		hasRightCreateDownload = session.hasRight("page.DownloadEditPage");
	}

	public boolean hasRightCreateDownload() {
		return hasRightCreateDownload;
	}

	private AjaxLink<ModalWindow> newUploadLink(final ModalWindow modalWindow) {
		return new AjaxLink<ModalWindow>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalWindow.setInitialHeight(400);
				modalWindow.setInitialWidth(600);
				modalWindow.setPageCreator(createPageCreator());
				modalWindow.setWindowClosedCallback(createWindowCloseCallback());
				modalWindow.show(target);
			}

			private PageCreator createPageCreator() {
				return new ModalWindow.PageCreator() {
					private static final long serialVersionUID = 1L;

					public Page createPage() {
						return new UploadFilePage(selectedFolder);
					}
				};
			}

			private WindowClosedCallback createWindowCloseCallback() {
				return new ModalWindow.WindowClosedCallback() {
					private static final long serialVersionUID = 1L;

					public void onClose(AjaxRequestTarget target) {
						UploadCenterPage.this.forceRefresh(target);
					}
				};
			}
		};
	}

	private TreeModel createTreeModel() {
		rootNode = new DefaultMutableTreeNode(new FileBean(rootFolder, dateFormat));
		selectedNode = rootNode;
		TreeModel model = new DefaultTreeModel(rootNode);
		add(rootNode, rootFolder);
		return model;

	}

	private void add(DefaultMutableTreeNode parent, File folder) {
		List<File> files = new ArrayList<File>();
		if (folder != null) {
			File tmpFiles[] = folder.listFiles();
			if (tmpFiles != null) {
				for (File file : tmpFiles) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(new FileBean(file, dateFormat));
					if (file.isDirectory()) {
						parent.add(child);
						add(child, file);
					} else {
						files.add(file);
					}
				}
			}
		}
		for (File file : files) {
			parent.add(new DefaultMutableTreeNode(new FileBean(file, dateFormat)));
		}
	}

	private class PropertyLinkedColumn extends PropertyRenderableColumn {
		private static final long serialVersionUID = 1L;
		private ModalWindow modalWindow;

		public PropertyLinkedColumn(ColumnLocation location, String header, String propertyExpression,
				ModalWindow modalWindow) {
			super(location, header, propertyExpression);
			this.modalWindow = modalWindow;

		}

		@Override
		public Component newCell(MarkupContainer parent, String id, final TreeNode node, int level) {
			return new UploadCenterPanel(id, new PropertyModel<File>(node, getPropertyExpression()), modalWindow,
					hasRightCreateDownload) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) node;
					n.removeFromParent();
					TreeModel model = new DefaultTreeModel(rootNode);
					folderTreeTable.setModelObject(model);
					folderTreeTable.modelChanged();
					folderTreeTable.updateTree(target);
					selectedFolder = rootFolder;
					selectedNode = rootNode;
				}
			};
		}

		@Override
		public IRenderable newCell(TreeNode node, int level) {
			return null;
		}
	}

	private void forceRefresh(AjaxRequestTarget target) {
		selectedNode.removeAllChildren();
		add(selectedNode, selectedFolder);
		TreeModel model = new DefaultTreeModel(rootNode);
		folderTreeTable.setModelObject(model);
		folderTreeTable.modelChanged();
		folderTreeTable.updateTree(target);
	}
}
