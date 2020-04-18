CREATE SCHEMA if not exists `DataProject`;
USE DataProject;
DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
  DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;
  /*CREATE SCHEMA `DataProject`;*/
  ALTER SCHEMA `DataProject`  DEFAULT CHARACTER SET utf8  DEFAULT COLLATE utf8_general_ci ;
  CREATE TABLE `DataProject`.`av`
(
    `id`	int PRIMARY KEY auto_increment,
	`word`	TEXT,
	`html`	TEXT,
	`description`	TEXT,
	`pronounce`	TEXT
);
CREATE TABLE `DataProject`.`va`
(
    `id`	int PRIMARY KEY auto_increment,
	`word`	TEXT,
	`html`	TEXT,
	`description`	TEXT,
	`pronounce`	TEXT
);


END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;

