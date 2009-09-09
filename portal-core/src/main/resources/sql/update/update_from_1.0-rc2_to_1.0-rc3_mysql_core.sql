ALTER TABLE core_box ADD COLUMN hide_title BIT NOT NULL;
UPDATE core_box SET hide_title = 0;
UPDATE core_box SET sort = sort + 1;
INSERT INTO core_box (created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES ({ts '2009-01-05 12:19:08.000'},'admin',{ts '2009-01-05 14:28:30.000'},'admin','OtherBoxPanel','<!-- AddThis Button BEGIN -->
<a class="addthis_button" href="http://www.addthis.com/bookmark.php?v=250&amp"><img src="http://s7.addthis.com/static/btn/v2/lg-share-en.gif" width="125" height="16" alt="Bookmark and Share" style="border:0"/></a><script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
<!-- AddThis Button END -->
<br/>',1,'AddThis.com',1);
SELECT @newSort := MAX(sort) + 1 FROM core_box;
INSERT INTO core_box (created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES ({ts '2009-01-05 12:19:08.000'},'admin',{ts '2009-01-05 14:28:30.000'},'admin','FeedBoxPanel',null,@newSort,'Feed Box',1);
