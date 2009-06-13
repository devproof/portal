CREATE TABLE blog
(
   id int NOT NULL,
   created_at timestamp,
   created_by varchar(30),
   modified_at timestamp,
   modified_by varchar(30),
   content varchar(10000),
   headline varchar(255),
   PRIMARY KEY(id)
)
;
CREATE TABLE blog_right_xref
(
   blog_id int NOT NULL,
   right_id varchar(50) NOT NULL
)
;
CREATE TABLE blog_tag
(
   tagname varchar(255) NOT NULL,
   created_at timestamp,
   created_by varchar(30),
   modified_at timestamp,
   modified_by varchar(30),
   PRIMARY KEY(tagname)
)
;
CREATE TABLE blog_tag_xref
(
   blog_id int NOT NULL,
   tagname varchar(255) NOT NULL
)
;
