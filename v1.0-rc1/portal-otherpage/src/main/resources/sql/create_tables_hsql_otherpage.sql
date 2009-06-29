CREATE TABLE other_page
(
   id int NOT NULL,
   created_at timestamp,
   created_by varchar(30),
   modified_at timestamp,
   modified_by varchar(30),
   content varchar(10000),
   content_id varchar(255),
   PRIMARY KEY(id)
)
;
CREATE TABLE other_page_right_xref
(
   other_id int NOT NULL,
   right_id varchar(50) NOT NULL
);
