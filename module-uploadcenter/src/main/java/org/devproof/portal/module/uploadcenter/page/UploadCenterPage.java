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
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.uploadcenter.UploadCenterConstants;
import org.devproof.portal.module.uploadcenter.bean.FileBean;
import org.devproof.portal.module.uploadcenter.panel.CreateFolderPanel;
import org.devproof.portal.module.uploadcenter.panel.UploadCenterPanel;
import org.devproof.portal.module.uploadcenter.panel.UploadFilePanel;

/**
 * @author Carsten Hufe
 */
public class UploadCenterPage extends TemplatePage {

	private static final long serialVersionUID = 3247255196536400455L;
	@SpringBean(name = "displayDateTimeFormat")
	private DateFormat dateFormat;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private File rootFolder;
	private File selectedFolder;
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode selectedNode;
	private boolean hasRightCreateDownload;
	private BubblePanel bubblePanel;
	private TreeTable folderTreeTable;

	public UploadCenterPage(PageParameters params) {
		super(params);
		setRootFolder();
		setSelectedFolder();
		setHasRightToCreate();
		add(createBubblePanel());
		add(createFolderTreeTable());
		addPageAdminBoxLink(createUploadLink());
		addPageAdminBoxLink(createFolderLink());
	}

	private void setSelectedFolder() {
		selectedFolder = rootFolder;
	}

	private void setRootFolder() {
		rootFolder = configurationService.findAsFile(UploadCenterConstants.CONF_UPLOADCENTER_FOLDER);
	}

	private AjaxLink<BubblePanel> createFolderLink() {
		AjaxLink<BubblePanel> createFolderLink = newCreateFolderLink();
		createFolderLink.add(createFolderLinkLabel());
		return createFolderLink;
	}

	private Label createFolderLinkLabel() {
		return new Label("linkName", getString("createFolderLink"));
	}

	private AjaxLink<BubblePanel> createUploadLink() {
		AjaxLink<BubblePanel> uploadLink = newUploadLink(bubblePanel);
		uploadLink.add(createUploadLinkLabel());
		return uploadLink;
	}

	private Label createUploadLinkLabel() {
		return new Label("linkName", getString("uploadLink"));
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
						bubblePanel) };

		folderTreeTable = newFolderTreeTable(columns);
		folderTreeTable.getTreeState().collapseAll();
		folderTreeTable.setRootLess(true);
		return folderTreeTable;
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
					setSelectedFolder();
					selectedNode = rootNode;
				}
			}
		};
	}

	private AjaxLink<BubblePanel> newCreateFolderLink() {
		return new AjaxLink<BubblePanel>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				bubblePanel.setContent(createCreateFolderPanel());
				bubblePanel.showModal(target);
			}

			private CreateFolderPanel createCreateFolderPanel() {
				return new CreateFolderPanel(bubblePanel.getContentId(), selectedFolder) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onCreate(AjaxRequestTarget target) {
						UploadCenterPage.this.forceRefresh(target);
						bubblePanel.hide(target);
					}
				};
			}
		};
	}

	private BubblePanel createBubblePanel() {
		bubblePanel = new BubblePanel("bubblePanel");
		return bubblePanel;
	}

	private void setHasRightToCreate() {
		PortalSession session = (PortalSession) getSession();
		hasRightCreateDownload = session.hasRight("page.DownloadEditPage");
	}

	public boolean hasRightCreateDownload() {
		return hasRightCreateDownload;
	}

	private AjaxLink<BubblePanel> newUploadLink(final BubblePanel bubblePanel) {
		return new AjaxLink<BubblePanel>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				bubblePanel.setContent(createUploadFilePanel(bubblePanel));
				bubblePanel.showModal(target);
			}

			private UploadFilePanel createUploadFilePanel(final BubblePanel bubblePanel) {
				return new UploadFilePanel(bubblePanel.getContentId(), selectedFolder) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit() {
						forceRefresh(null);
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
		private BubblePanel bubblePanel;

		public PropertyLinkedColumn(ColumnLocation location, String header, String propertyExpression,
				BubblePanel bubblePanel) {
			super(location, header, propertyExpression);
			this.bubblePanel = bubblePanel;

		}

		@Override
		public Component newCell(MarkupContainer parent, String id, final TreeNode node, int level) {
			return new UploadCenterPanel(id, new PropertyModel<File>(node, getPropertyExpression()), bubblePanel,
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
