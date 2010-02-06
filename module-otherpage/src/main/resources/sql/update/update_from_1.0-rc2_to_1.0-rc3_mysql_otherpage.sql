INSERT INTO other_page (id,created_at,created_by,modified_at,modified_by,content,content_id) VALUES (2,{ts '2009-01-06 19:40:23.000'},'admin',{ts '2009-01-06 19:40:23.000'},'admin','<p>Terms of use for registration.</p>','terms_of_use');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (2,'otherPage.view.preview');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (2,'otherPage.view.guest');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (2,'otherPage.view.registered');

ALTER TABLE other_page_right_xref DROP FOREIGN KEY FKFA33F5DFFEB2F584;
ALTER TABLE other_page_right_xref ADD CONSTRAINT `FKFA33F5DFFEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`) ON DELETE CASCADE;