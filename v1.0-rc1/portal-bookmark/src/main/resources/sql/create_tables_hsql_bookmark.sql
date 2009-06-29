CREATE TABLE bookmark
(
   id int NOT NULL,
   created_at timestamp,
   created_by varchar(30),
   modified_at timestamp,
   modified_by varchar(30),
   broken bit,
   description varchar(10000),
   hits int,
   number_of_votes int,
   sum_of_rating int,
   title varchar(255),
   url varchar(255),
   source varchar(255),
   sync_hash varchar(255),
   sync_username varchar(255),
   PRIMARY KEY(id)
)
;
CREATE TABLE bookmark_right_xref
(
   bookmark_id int NOT NULL,
   right_id varchar(50) NOT NULL
)
;
CREATE TABLE bookmark_tag
(
   tagname varchar(255) NOT NULL,
   created_at timestamp,
   created_by varchar(30),
   modified_at timestamp,
   modified_by varchar(30),
   PRIMARY KEY(tagname)
)
;
CREATE TABLE bookmark_tag_xref
(
   bookmark_id int NOT NULL,
   tagname varchar(255) NOT NULL
)
;