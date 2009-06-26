/*
 * Copyright 2009 Carsten Hufe devproof.org
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
	private transient ConfigurationService configurationService;
	private final TreeTable tree;
	private final File rootFolder;
	private File selectedFolder;
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode selectedNode;
	private final boolean hasRightCreateDownload;

	public UploadCenterPage(final PageParameters params) {
		super(params);
		this.rootFolder = this.configurationService.findAsFile(UploadCenterConstants.CONF_UPLOADCENTER_FOLDER);
		this.selectedFolder = this.rootFolder;
		PortalSession session = (PortalSession) getSession();
		this.hasRightCreateDownload = session.hasRight("page.DownloadEditPage");

		final ModalWindow modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		this.add(modalWindow);

		IColumn columns[] = new IColumn[] { new PropertyTreeColumn(new ColumnLocation(Alignment.MIDDLE, 8, Unit.PROPORTIONAL), this.getString("tableFilename"), "userObject.name"),
				new PropertyRenderableColumn(new ColumnLocation(Alignment.MIDDLE, 4, Unit.PROPORTIONAL), this.getString("tableFilesize"), "userObject.size"),
				new PropertyRenderableColumn(new ColumnLocation(Alignment.MIDDLE, 4, Unit.PROPORTIONAL), this.getString("tableFiledate"), "userObject.date"),
				new PropertyLinkedColumn(new ColumnLocation(Alignment.RIGHT, 80, Unit.PX), "", "userObject.file", modalWindow) };

		this.tree = new TreeTable("treeTable", createTreeModel(), columns) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onNodeLinkClicked(final AjaxRequestTarget target, final TreeNode node) {
				if (UploadCenterPage.this.tree.getTreeState().isNodeSelected(node)) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) node;
					FileBean fileBean = (FileBean) n.getUserObject();

					if (fileBean.getFile().isDirectory()) {
						UploadCenterPage.this.selectedFolder = fileBean.getFile();
						UploadCenterPage.this.selectedNode = (DefaultMutableTreeNode) node;
					} else {
						UploadCenterPage.this.selectedFolder = fileBean.getFile().getParentFile();
						UploadCenterPage.this.selectedNode = (DefaultMutableTreeNode) node.getParent();
					}
				} else {
					UploadCenterPage.this.selectedFolder = UploadCenterPage.this.rootFolder;
					UploadCenterPage.this.selectedNode = UploadCenterPage.this.rootNode;
				}
			}
		};
		this.tree.getTreeState().collapseAll();
		this.tree.setRootLess(true);
		this.add(this.tree);

		AjaxLink<ModalWindow> uploadLink = new AjaxLink<ModalWindow>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				modalWindow.setInitialHeight(400);
				modalWindow.setInitialWidth(600);

				modalWindow.setPageCreator(new ModalWindow.PageCreator() {
					private static final long serialVersionUID = 1L;

					public Page createPage() {
						return new UploadFilePage(UploadCenterPage.this.selectedFolder);
					}
				});
				modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
					private static final long serialVersionUID = 1L;

					public void onClose(final AjaxRequestTarget target) {
						UploadCenterPage.this.forceRefresh(target);
					}
				});
				modalWindow.show(target);
			}
		};
		uploadLink.add(new Label("linkName", this.getString("uploadLink")));
		addPageAdminBoxLink(uploadLink);
		AjaxLink<ModalWindow> createFolderLink = new AjaxLink<ModalWindow>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				modalWindow.setInitialHeight(170);
				modalWindow.setInitialWidth(400);

				modalWindow.setContent(new CreateFolderPanel(modalWindow.getContentId(), UploadCenterPage.this.selectedFolder) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onCreate(final AjaxRequestTarget target) {
						UploadCenterPage.this.forceRefresh(target);
						modalWindow.close(target);
					}

				});

				modalWindow.show(target);
			}
		};
		createFolderLink.add(new Label("linkName", this.getString("createFolderLink")));
		addPageAdminBoxLink(createFolderLink);

	}

	private TreeModel createTreeModel() {

		this.rootNode = new DefaultMutableTreeNode(new FileBean(this.rootFolder, this.dateFormat));
		this.selectedNode = this.rootNode;
		TreeModel model = new DefaultTreeModel(this.rootNode);
		this.add(this.rootNode, this.rootFolder);
		return model;

	}

	private void add(final DefaultMutableTreeNode parent, final File folder) {
		List<File> files = new ArrayList<File>();
		if (folder != null) {
			File tmpFiles[] = folder.listFiles();
			if (tmpFiles != null) {
				for (File file : tmpFiles) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(new FileBean(file, this.dateFormat));
					if (file.isDirectory()) {
						parent.add(child);
						this.add(child, file);
					} else {
						files.add(file);
					}
				}
			}
		}
		for (File file : files) {
			parent.add(new DefaultMutableTreeNode(new FileBean(file, this.dateFormat)));
		}
	}

	private class PropertyLinkedColumn extends PropertyRenderableColumn {
		private static final long serialVersionUID = 1L;
		private final ModalWindow modalWindow;

		public PropertyLinkedColumn(final ColumnLocation location, final String header, final String propertyExpression, final ModalWindow modalWindow) {
			super(location, header, propertyExpression);
			this.modalWindow = modalWindow;

		}

		@Override
		public Component newCell(final MarkupContainer parent, final String id, final TreeNode node, final int level) {
			return new UploadCenterPanel(id, new PropertyModel<File>(node, getPropertyExpression()), this.modalWindow, UploadCenterPage.this.hasRightCreateDownload) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) node;
					n.removeFromParent();
					TreeModel model = new DefaultTreeModel(UploadCenterPage.this.rootNode);
					UploadCenterPage.this.tree.setModelObject(model);
					UploadCenterPage.this.tree.modelChanged();
					UploadCenterPage.this.tree.updateTree(target);
					UploadCenterPage.this.selectedFolder = UploadCenterPage.this.rootFolder;
					UploadCenterPage.this.selectedNode = UploadCenterPage.this.rootNode;
				}
			};
		}

		@Override
		public IRenderable newCell(final TreeNode node, final int level) {
			return null;
		}
	}

	private void forceRefresh(final AjaxRequestTarget target) {
		this.selectedNode.removeAllChildren();
		this.add(UploadCenterPage.this.selectedNode, UploadCenterPage.this.selectedFolder);
		TreeModel model = new DefaultTreeModel(UploadCenterPage.this.rootNode);
		this.tree.setModelObject(model);
		this.tree.modelChanged();
		this.tree.updateTree(target);
	}
}
