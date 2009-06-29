CREATE TABLE download
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
   download_size varchar(255),
   licence varchar(255),
   manufacturer varchar(255),
   manufacturer_homepage varchar(255),
   price varchar(255),
   software_version varchar(255),
   PRIMARY KEY(id)
)
;
CREATE TABLE download_right_xref
(
   download_id int NOT NULL,
   right_id varchar(50) NOT NULL
)
;
CREATE TABLE download_tag
(
   tagname varchar(255) NOT NULL,
   created_at timestamp,
   created_by varchar(30),
   modified_at timestamp,
   modified_by varchar(30),
   PRIMARY KEY(tagname)
)
;
CREATE TABLE download_tag_xref
(
   download_id int NOT NULL,
   tagname varchar(255) NOT NULL
)
;