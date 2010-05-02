
CREATE TABLE "COMMENTS"
 (	"ID" NUMBER(10,0) NOT NULL ENABLE,
  "CREATED_AT" TIMESTAMP (6),
  "CREATED_BY" VARCHAR2(30 CHAR),
  "MODIFIED_AT" TIMESTAMP (6),
  "MODIFIED_BY" VARCHAR2(30 CHAR),
  "ACCEPTED" NUMBER(1,0),
  "AUTOMATIC_BLOCKED" NUMBER(1,0),
  "CONTENT" CLOB,
  "GUEST_EMAIL" VARCHAR2(50 CHAR),
  "GUEST_NAME" VARCHAR2(50 CHAR),
  "IP_ADDRESS" VARCHAR2(39 CHAR),
  "MODULE_CONTENT_ID" VARCHAR2(20 CHAR),
  "MODULE_NAME" VARCHAR2(20 CHAR),
  "NUMBER_OF_BLAMES" NUMBER(10,0),
  "REVIEWED" NUMBER(1,0),
   PRIMARY KEY ("ID") ENABLE
 );

CREATE INDEX module_name_idx ON comments (module_name);
CREATE INDEX module_content_id_idx ON comments (module_content_id);
CREATE INDEX module_accepted_idx ON comments (accepted);
CREATE INDEX module_reviewed_idx ON comments (reviewed);
CREATE INDEX module_automatic_blocked_idx ON comments (automatic_blocked);
