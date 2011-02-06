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
  "CONTENT_ID" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "TITLE" VARCHAR2(255 CHAR),
  "TEASER" CLOB,
  "FULL_ARTICLE" CLOB,
  "TAGS" CLOB,
  "RIGHT" CLOB,
  "ARTICLE_ID" NUMBER(10, 0) NOT NULL,
   PRIMARY KEY ("ID") ENABLE
 );
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ArticleHistoryPage','Article: History',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ArticleHistoryPage');

-- TODO oracle migration scripts for urls
-- TODO oracle migration scripts for rights