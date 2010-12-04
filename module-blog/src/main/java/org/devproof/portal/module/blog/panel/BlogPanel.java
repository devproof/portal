package org.devproof.portal.module.blog.panel;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.print.PrintConstants;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogTag;
import org.devproof.portal.module.blog.page.BlogEditPage;
import org.devproof.portal.module.blog.page.BlogHistoryPage;
import org.devproof.portal.module.blog.page.BlogPage;
import org.devproof.portal.module.blog.page.BlogPrintPage;
import org.devproof.portal.module.blog.service.BlogService;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.module.comment.panel.ExpandableCommentPanel;

import java.util.List;

/**
 * @author Carsten Hufe
 */
// TODO unit test
// TODO sachen zum editieren ausblenden in der view
public class BlogPanel extends Panel {
    private static final long serialVersionUID = -7287096143680409742L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    private IModel<Blog> blogModel;

    public BlogPanel(String id, IModel<Blog> blogModel) {
        super(id, blogModel);
        this.blogModel = blogModel;
        add(createCSSHeaderContributor());
        add(createAppropriateAuthorPanel());
        add(createHeadline());
        add(createMetaInfoPanel());
        add(createPrintLink());
        add(createTagPanel());
        add(createContentLabel());
        add(createCommentPanel());
        setOutputMarkupId(true);
    }

    public boolean hideAuthorButtons() {
        return false;
    }

    public boolean hideComments() {
        return false;
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(BlogConstants.REF_BLOG_CSS);
    }

    private Component createPrintLink() {
        Blog blog = blogModel.getObject();
        BookmarkablePageLink<BlogPrintPage> link = new BookmarkablePageLink<BlogPrintPage>("printLink", BlogPrintPage.class, new PageParameters("0=" + blog.getId()));
        link.add(createPrintImage());
        return link;
    }

    private Component createPrintImage() {
        return new Image("printImage", PrintConstants.REF_PRINTER_IMG);
    }

    private Component createAppropriateAuthorPanel() {
        if (isAuthor()) {
            return createAuthorPanel();
        } else {
            return createEmptyAuthorPanel();
        }
    }

    private AuthorPanel<Blog> createAuthorPanel() {
        return new AuthorPanel<Blog>("authorButtons", blogModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected MarkupContainer newHistorizationLink(String markupId) {
                return new BookmarkablePageLink<BlogHistoryPage>(markupId, BlogHistoryPage.class) {
                    private static final long serialVersionUID = 1918205848493398092L;

                    @Override
                    public PageParameters getPageParameters() {
                        PageParameters params = new PageParameters();
                        params.put("id", blogModel.getObject().getId());
                        return params;
                    }
                };
            }

            @Override
            public void onDelete(AjaxRequestTarget target) {
                blogService.delete(getEntityModel().getObject());
                BlogPanel.this.setVisible(false);
                target.addComponent(BlogPanel.this);
                onDeleted(target);
            }

            @Override
            public void onEdit(AjaxRequestTarget target) {
                setResponsePage(new BlogEditPage(blogModel));
            }

            @Override
            public boolean isVisible() {
                return !hideAuthorButtons();
            }
        };
    }

    private WebMarkupContainer createEmptyAuthorPanel() {
        return new WebMarkupContainer("authorButtons");
    }

    private BookmarkablePageLink<BlogPage> createHeadline() {
        BookmarkablePageLink<BlogPage> headlineLink = new BookmarkablePageLink<BlogPage>("headlineLink", BlogPage.class);
        Blog blog = blogModel.getObject();
        headlineLink.setParameter("id", blog.getId());
        headlineLink.add(headlineLinkLabel());
        return headlineLink;
    }

    private Label headlineLinkLabel() {
        IModel<String> headliineModel = new PropertyModel<String>(blogModel, "headline");
        return new Label("headlineLabel", headliineModel);
    }

    private MetaInfoPanel<Blog> createMetaInfoPanel() {
        return new MetaInfoPanel<Blog>("metaInfo", blogModel);
    }

    private ExtendedLabel createContentLabel() {
        IModel<String> contentModel = new PropertyModel<String>(blogModel, "content");
        return new ExtendedLabel("content", contentModel);
    }

    private Component createCommentPanel() {
        Blog blog = blogModel.getObject();
        DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
        conf.setModuleContentId(blog.getId().toString());
        conf.setModuleName(BlogPage.class.getSimpleName());
        conf.setViewRights(blog.getCommentViewRights());
        conf.setWriteRights(blog.getCommentWriteRights());
        return new ExpandableCommentPanel("comments", conf) {
            private static final long serialVersionUID = 9069146819843965337L;

            @Override
            public boolean isVisible() {
                return super.isVisible() && !hideComments();
            }
        };
    }

    private TagContentPanel<BlogTag> createTagPanel() {
        IModel<List<BlogTag>> blogTagModel = new PropertyModel<List<BlogTag>>(blogModel, "tags");
        return new TagContentPanel<BlogTag>("tags", blogTagModel, BlogPage.class);
    }


    protected boolean isAuthor() {
        PortalSession session = (PortalSession) getSession();
        return session.hasRight(BlogConstants.AUTHOR_RIGHT);
    }

    /**
     * Hook method
     * Called when the blog entry was deleted
     *
     * @param target AjaxRequestTarget
     */
    protected void onDeleted(AjaxRequestTarget target) {
    }
}
