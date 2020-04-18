CREATE SCHEMA if not exists `DataProject`;
USE DataProject;
DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
  DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;
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
ALTER TABLE `DataProject`.`AV` 
ADD COLUMN `IdTitle` TEXT NULL AFTER `Pronounce`,
CHANGE COLUMN `id` `Id` INT(11) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `word` `Word` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `html` `Html` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `description` `Description` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `pronounce` `Pronounce` TEXT NULL DEFAULT NULL ;

ALTER TABLE `DataProject`.`AV` 
RENAME TO  `DataProject`.`AV` ;

ALTER TABLE `DataProject`.`va`
ADD COLUMN `IdTitle` TEXT NULL AFTER `Pronounce`,
CHANGE COLUMN `id` `Id` INT(11) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `word` `Word` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `html` `Html` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `description` `Description` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `pronounce` `Pronounce` TEXT NULL DEFAULT NULL ;

ALTER TABLE `DataProject`.`va` 
RENAME TO  `DataProject`.`VA` ;
/**/
ALTER TABLE `DataProject`.`VA`
CHANGE COLUMN `IdTitle` `IdTopic` TEXT NULL DEFAULT NULL ;
ALTER TABLE `DataProject`.`AV`
CHANGE COLUMN `idTitle` `IdTopic` TEXT NULL DEFAULT NULL ;

/*8-4-2020*/
CREATE TABLE `DataProject`.`User` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
     `Name` text,
     `Password` text not null,
     `Email` text not null, /* = username: dùng để đăng nhập khi người dùng đăng ký tài khoản*/
     `NumberPhone` text,
     `Birthday` date,
     `Image` text,
	 `IdRole` int
);
CREATE TABLE `DataProject`.`Role` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
     `Role` text
);
ALTER TABLE `DataProject`.`User` ADD FOREIGN KEY (`IdRole`) REFERENCES `DataProject`.`Role` (`Id`);/* liên kết khóa ngoại.*/
/*10-4-2020*/
create table `DataProject`.`Remind`(
`Id` INT PRIMARY KEY AUTO_INCREMENT,
`IdUser` int,
`TurnOnOff` bool,
`NumberWordOnDay` int,
`TimeStartRemind` time,
`TimeStopRemind` time
);
ALTER TABLE `DataProject`.`Remind` ADD FOREIGN KEY (`IdUser`) REFERENCES `DataProject`.`User` (`Id`);/* liên kết khóa ngoại.*/

create table `DataProject`.`RemindSupport`(
`Id` INT PRIMARY KEY AUTO_INCREMENT,
`IdRemind` int,
`IdWord` int
);
ALTER TABLE `DataProject`.`RemindSupport` ADD FOREIGN KEY (`IdRemind`) REFERENCES `DataProject`.`Remind` (`Id`);/* liên kết khóa ngoại.*/
ALTER TABLE `DataProject`.`RemindSupport` ADD FOREIGN KEY (`IdWord`) REFERENCES `DataProject`.`AV` (`Id`);/* liên kết khóa ngoại.*/

create table `DataProject`.`WordLike`(
`Id` INT PRIMARY KEY AUTO_INCREMENT,
`IdUser` int,
`IdWord` int
);
ALTER TABLE `DataProject`.`WordLike` ADD FOREIGN KEY (`IdUser`) REFERENCES `DataProject`.`User` (`Id`);/* liên kết khóa ngoại.*/
ALTER TABLE `DataProject`.`WordLike` ADD FOREIGN KEY (`IdWord`) REFERENCES `DataProject`.`AV` (`Id`);/* liên kết khóa ngoại.*/

create table `DataProject`.`SearchHistory`(
`Id` INT PRIMARY KEY AUTO_INCREMENT,
`IdUser` int,
`IdWord` int,
`TimeSearch` datetime
);
ALTER TABLE `DataProject`.`SearchHistory` ADD FOREIGN KEY (`IdUser`) REFERENCES `DataProject`.`User` (`Id`);/* liên kết khóa ngoại.*/
ALTER TABLE `DataProject`.`SearchHistory` ADD FOREIGN KEY (`IdWord`) REFERENCES `DataProject`.`AV` (`Id`);/* liên kết khóa ngoại.*/

create table `DataProject`.`Topic`(
`Id` INT PRIMARY KEY AUTO_INCREMENT,
`NameTopic` text,
`Translate` text
);
ALTER TABLE `DataProject`.`AV` ADD FOREIGN KEY (`IdTopic`) REFERENCES `DataProject`.`Topic` (`Id`);/* liên kết khóa ngoại.*/
ALTER TABLE `DataProject`.`VA` ADD FOREIGN KEY (`IdTopic`) REFERENCES `DataProject`.`Topic` (`Id`);/* liên kết khóa ngoại.*/

END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;