-- since 1.1
CREATE TABLE "OTHER_PAGE_HISTORIZED"
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
  "RIGHTS" CLOB,
  "OTHER_PAGE_ID" NUMBER(10, 0) NOT NULL,
   PRIMARY KEY ("ID") ENABLE
 );

-- since 1.1
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.OtherPageHistoryPage','Other Page Administration: History',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.OtherPageHistoryPage');

ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427FEB2F584";
UPDATE core_role_right_xref SET right_id = 'otherPage.author' WHERE right_id LIKE 'page.OtherPagePage';
UPDATE core_right SET right_id = 'otherPage.author', description = 'Other Page Author' WHERE right_id LIKE 'page.OtherPagePage';
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427FEB2F584";

-- copy current content ids to mount_points
INSERT INTO core_mount_point (id, related_content_id, handler_key, mount_path, default_url)
  (SELECT HIBERNATE_SEQUENCE.NEXTVAL, id , 'otherPage', concat('/other/', content_id), 1 from other_page);
ALTER TABLE other_page DROP COLUMN content_id;