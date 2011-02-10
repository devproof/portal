CREATE TABLE "ARTICLE_HISTORIZED"
 (
  "ID" NUMBER(10,0) NOT NULL ENABLE,
  "VERSION_NUMBER" NUMBER(10, 0) NOT NULL,
  "CREATED_AT" TIMESTAMP (6),
  "CREATED_BY" VARCHAR2(30),
  "MODIFIED_AT" TIMESTAMP (6),
  "MODIFIED_BY" VARCHAR2(30),
  "ACTION" VARCHAR2(50),
  "ACTION_AT" TIMESTAMP (6),
  "RESTORED_FROM_VERSION" NUMBER(10, 0),
  "TITLE" VARCHAR2(255 CHAR),
  "TEASER" CLOB,
  "FULL_ARTICLE" CLOB,
  "TAGS" CLOB,
  "RIGHTS" CLOB,
  "ARTICLE_ID" NUMBER(10, 0) NOT NULL,
   PRIMARY KEY ("ID") ENABLE
 );
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ArticleHistoryPage','Article: History',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ArticleHistoryPage');

ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427FEB2F584";
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.ArticlePage';
DELETE FROM core_right WHERE right_id LIKE 'page.ArticlePage';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.ArticleBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.ArticleBoxPanel';
UPDATE core_role_right_xref SET right_id = 'article.author' WHERE right_id LIKE 'page.ArticleEditPage';
UPDATE core_right SET right_id = 'article.author', description = 'Article Author' WHERE right_id LIKE 'page.ArticleEditPage';
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427FEB2F584";

-- copy current content ids to mount_points
INSERT INTO core_mount_point (id, related_content_id, handler_key, mount_path, default_url)
  (SELECT HIBERNATE_SEQUENCE.NEXTVAL, id , 'article', concat('/article/', content_id), 1 from article);
ALTER TABLE article_page DROP PRIMARY KEY;
ALTER TABLE article_page ADD (id NUMBER(10,0));
UPDATE article_page set id = HIBERNATE_SEQUENCE.NEXTVAL;
ALTER TABLE article_page ADD PRIMARY KEY (id);
ALTER TABLE article DROP COLUMN content_id;
ALTER TABLE article_page DROP COLUMN content_id;