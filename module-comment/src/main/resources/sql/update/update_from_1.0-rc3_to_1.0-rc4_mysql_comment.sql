RENAME TABLE comment TO comments;
ALTER TABLE comments CHANGE COLUMN comment content text not null;
