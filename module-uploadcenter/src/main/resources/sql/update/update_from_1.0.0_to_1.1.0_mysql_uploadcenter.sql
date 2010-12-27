SET FOREIGN_KEY_CHECKS=0;
UPDATE core_role_right_xref SET right_id = 'uploadcenter.author' WHERE right_id LIKE 'page.UploadCenterPage';
UPDATE core_right SET right_id = 'uploadcenter.author', description = 'Upload Center Author' WHERE right_id LIKE 'page.UploadCenterPage';
SET FOREIGN_KEY_CHECKS=1;
