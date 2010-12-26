alter table article_page change content content mediumtext;
CREATE TABLE `article_historized` (
  `id` int(11) NOT NULL auto_increment,
  `version_number` int(11) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `action` varchar(50) default NULL,
  `action_at` datetime default NULL,
  `restored_from_version` int(11) default NULL,
  `content_id` varchar(255) default NULL,
  `teaser` text,
  `title` varchar(255) default NULL,
  `full_article` mediumtext,
  `tags` text default NULL,
  `rights` text default NULL,
  `article_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  CONSTRAINT `FK3DB0669D97F357` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ArticleHistoryPage','Article: History',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ArticleHistoryPage');

-- page.ArticlePage loeschen
-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.author','Article: Author',{ts '2009-01-10 21:48:33.000'},'admin',{ts '2009-01-10 21:48:33.000'},'admin');
