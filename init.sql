DROP DATABASE IF EXISTS visio;

CREATE DATABASE visio;

CREATE USER IF NOT EXISTS 'visio'@'localhost' IDENTIFIED BY 'aaa';

GRANT ALL ON `visio`.* TO 'visio'@'localhost';
