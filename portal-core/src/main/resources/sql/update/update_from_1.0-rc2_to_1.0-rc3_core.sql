ALTER TABLE core_box ADD COLUMN hide_title BIT NOT NULL;
UPDATE core_box SET hide_title = 0;