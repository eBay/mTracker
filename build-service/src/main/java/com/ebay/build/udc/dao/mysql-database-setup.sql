-- log in as 'root'
CREATE DATABASE usagedb;

-- 'usagedb'@'%'
CREATE USER 'usagedb'@'%' IDENTIFIED BY 'usagedb';

GRANT ALL PRIVILEGES ON usagedb.* TO 'usagedb'@'%'
    IDENTIFIED BY 'usagedb' WITH GRANT OPTION;

-- 'usagedb'@'localhost'
CREATE USER 'usagedb'@'localhost' IDENTIFIED BY 'usagedb';

GRANT ALL PRIVILEGES ON usagedb.* TO 'usagedb'@'localhost'
    IDENTIFIED BY 'usagedb' WITH GRANT OPTION;

-- enable event scheduler	
SET GLOBAL event_scheduler=ON;

-- log in as "kernel"

USE usagedb;

-- create table "usagedata"

DROP TABLE IF EXISTS `usagedata`;

CREATE TABLE `usagedata` (

  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `IDEType` varchar(32) NOT NULL,
  `IDEVersion` varchar(32) NOT NULL,
  `SessionId` BIGINT(32) unsigned DEFAULT '0',
  `Host` varchar(128) NOT NULL,
  `User` varchar(32) NOT NULL,
  `Kind` varchar(32) NOT NULL,
  `What` varchar(32) DEFAULT NULL,
  `Description` text NOT NULL,
  `BundleId` varchar(128) DEFAULT NULL,
  `BundleVersion` varchar(128) DEFAULT NULL,
  `AccessTime` datetime NOT NULL,
  `Duration` int(11) unsigned DEFAULT '0',
  `Size` int(11) unsigned DEFAULT '0',
  `Quantity` int(11) unsigned DEFAULT '0',
  `Exception` text,
  `Properties` text,
  PRIMARY KEY (`Id`),
  KEY `AccessTimeKindKey` (`AccessTime`,`Kind`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


-- create table "session"

DROP TABLE IF EXISTS `session`;

CREATE TABLE `session` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Properties` text,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

