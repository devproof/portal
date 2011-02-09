CREATE TABLE bookmark (
  id int(11) NOT NULL auto_increment,
  created_at datetime default NULL,
  created_by varchar(30) default NULL,
  modified_at datetime default NULL,
  modified_by varchar(30) default NULL,
  broken bit(1) default NULL,
  description text,
  hits int(11) default NULL,
  number_of_votes int(11) default NULL,
  sum_of_rating int(11) default NULL,
  title varchar(255) default NULL,
  url varchar(255) default NULL,
  source varchar(255) default NULL,
  sync_hash varchar(255) default NULL,
  sync_username varchar(255) default NULL,
  PRIMARY KEY  (id)
);
CREATE TABLE bookmark_right_xref (
  bookmark_id int(11) NOT NULL,
  right_id varchar(50) NOT NULL,
  KEY FK6B52A007BAE949C5 (bookmark_id),
  KEY FK6B52A007FEB2F584 (right_id),
  CONSTRAINT FK6B52A007FEB2F584 FOREIGN KEY (right_id) REFERENCES core_right (right_id) ON DELETE CASCADE,
  CONSTRAINT FK6B52A007BAE949C5 FOREIGN KEY (bookmark_id) REFERENCES bookmark (id)
);
CREATE TABLE bookmark_tag (
  tagname varchar(255) NOT NULL,
  created_at datetime default NULL,
  created_by varchar(30) default NULL,
  modified_at datetime default NULL,
  modified_by varchar(30) default NULL,
  PRIMARY KEY  (tagname)
);
CREATE TABLE bookmark_tag_xref (
  bookmark_id int(11) NOT NULL,
  tagname varchar(255) NOT NULL,
  KEY FKC59FF789BAE949C5 (bookmark_id),
  KEY FKC59FF78986BC2764 (tagname),
  CONSTRAINT FKC59FF78986BC2764 FOREIGN KEY (tagname) REFERENCES bookmark_tag (tagname),
  CONSTRAINT FKC59FF789BAE949C5 FOREIGN KEY (bookmark_id) REFERENCES bookmark (id)
);

