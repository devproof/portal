alter table article_page change content content mediumtext;
CREATE TABLE article_historized (
  id int(11) NOT NULL auto_increment,
  version_number int(11) NOT NULL,
  created_at datetime default NULL,
  created_by varchar(30) default NULL,
  modified_at datetime default NULL,
  modified_by varchar(30) default NULL,
  action varchar(50) default NULL,
  action_at datetime default NULL,
  restored_from_version int(11) default NULL,
  content_id varchar(255) default NULL,
  teaser text,
  title varchar(255) default NULL,
  full_article mediumtext,
  tags text default NULL,
  rights text default NULL,
  article_id int(11) NOT NULL,
  PRIMARY KEY  (id),
  CONSTRAINT FK3DB0669D97F357 FOREIGN KEY (article_id) REFERENCES article (id)
);

SET FOREIGN_KEY_CHECKS=0;
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.ArticlePage';
DELETE FROM core_right WHERE right_id LIKE 'page.ArticlePage';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.ArticleBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.ArticleBoxPanel';
UPDATE core_role_right_xref SET right_id = 'article.author' WHERE right_id LIKE 'page.ArticleEditPage';
UPDATE core_right SET right_id = 'article.author', description = 'Article Author' WHERE right_id LIKE 'page.ArticleEditPage';
SET FOREIGN_KEY_CHECKS=1;

-- copy current content ids to mount_points
INSERT INTO core_mount_point (related_content_id, handler_key, mount_path, default_url)
 (select id, 'article', concat('/article/', content_id), 1 from article);
ALTER TABLE article_page DROP PRIMARY KEY;
ALTER TABLE article_page ADD COLUMN id int(11) NOT NULL auto_increment FIRST, ADD PRIMARY KEY (id);
ALTER TABLE article DROP COLUMN content_id;
ALTER TABLE article_page DROP COLUMN content_id;