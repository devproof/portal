SET FOREIGN_KEY_CHECKS=0;
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.BookmarkPage';
DELETE FROM core_right WHERE right_id LIKE 'page.BookmarkPage';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.BookmarkBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.BookmarkBoxPanel';
UPDATE core_role_right_xref SET right_id = 'bookmark.author' WHERE right_id LIKE 'page.BookmarkEditPage';
UPDATE core_right SET right_id = 'bookmark.author', description = 'Bookmark Author' WHERE right_id LIKE 'page.BookmarkEditPage';
SET FOREIGN_KEY_CHECKS=1;
