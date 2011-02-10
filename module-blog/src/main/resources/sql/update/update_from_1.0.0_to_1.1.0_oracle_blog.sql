CREATE TABLE "BLOG_HISTORIZED"
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
  "CONTENT" CLOB,
  "HEADLINE" VARCHAR2(255),
  "TAGS" CLOB,
  "RIGHTS" CLOB,
  "BLOG_ID" NUMBER(10, 0) NOT NULL,
   PRIMARY KEY ("ID") ENABLE
 );

-- since 1.1
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.BlogHistoryPage','Blog: History',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.BlogHistoryPage');

ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427FEB2F584";
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.BlogPage';
DELETE FROM core_right WHERE right_id LIKE 'page.BlogPage';
UPDATE core_role_right_xref SET right_id = 'blog.author' WHERE right_id LIKE 'page.BlogEditPage';
UPDATE core_right SET right_id = 'blog.author', description = 'Blog Author' WHERE right_id LIKE 'page.BlogEditPage';
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427FEB2F584";