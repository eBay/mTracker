-- --------------------------------------------------------
-- Host:                         svcdb05.arch.ebay.com
-- Server version:               5.1.48-log - MySQL Community Server (GPL)
-- Server OS:                    pc-solaris2.10
-- HeidiSQL version:             7.0.0.4053
-- Date/time:                    2013-03-12 16:36:57
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;

-- Dumping database structure for trackingadmin
DROP DATABASE IF EXISTS `trackingadmin`;
CREATE DATABASE IF NOT EXISTS "trackingadmin" /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `trackingadmin`;


-- Dumping structure for table trackingadmin.RBT_PLUGIN
DROP TABLE IF EXISTS `RBT_PLUGIN`;
CREATE TABLE IF NOT EXISTS "RBT_PLUGIN" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT,
  "group_id" varchar(50) NOT NULL DEFAULT '0',
  "artifact_id" varchar(50) NOT NULL DEFAULT '0',
  "version" varchar(20) DEFAULT NULL,
  "plugin_key" varchar(150) DEFAULT NULL,
  "phase" varchar(50) DEFAULT NULL,
  PRIMARY KEY ("id")
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table trackingadmin.RBT_PROJECT
DROP TABLE IF EXISTS `RBT_PROJECT`;
CREATE TABLE IF NOT EXISTS "RBT_PROJECT" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT,
  "name" varchar(200) NOT NULL,
  "group_id" varchar(100) DEFAULT NULL,
  "artifact_id" varchar(100) DEFAULT NULL,
  "type" varchar(10) DEFAULT NULL,
  "version" varchar(50) DEFAULT NULL,
  "start_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  "duration" int(10) NOT NULL,
  "status" varchar(5) NOT NULL,
  "pool_name" varchar(50) DEFAULT NULL,
  PRIMARY KEY ("id")
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Raptor Build Tracking - Project';

-- Data exporting was unselected.


-- Dumping structure for table trackingadmin.RBT_RAW_DATA
DROP TABLE IF EXISTS `RBT_RAW_DATA`;
CREATE TABLE IF NOT EXISTS "RBT_RAW_DATA" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT,
  "plugin_id" int(10) unsigned NOT NULL DEFAULT '0',
  "session_id" int(10) unsigned NOT NULL DEFAULT '0',
  "project_id" int(10) unsigned NOT NULL DEFAULT '0',
  "event_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  "duration" int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY ("id")
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table trackingadmin.RBT_SESSION
DROP TABLE IF EXISTS `RBT_SESSION`;
CREATE TABLE IF NOT EXISTS "RBT_SESSION" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT,
  "pool_name" varchar(200) DEFAULT NULL,
  "machine_name" varchar(200) DEFAULT NULL,
  "environment" varchar(20) NOT NULL,
  "start_time" timestamp NULL DEFAULT NULL,
  "duration" int(10) unsigned NOT NULL,
  "user_name" varchar(100) NOT NULL,
  "goals" varchar(100) DEFAULT NULL,
  "status" varchar(5) NOT NULL,
  "maven_version" varchar(50) DEFAULT NULL,
  "java_version" varchar(50) DEFAULT NULL,
  "git_url" varchar(200) DEFAULT NULL,
  "git_branch" varchar(100) DEFAULT NULL,
  "jekins_url" varchar(200) DEFAULT NULL,
  PRIMARY KEY ("id")
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Raptor Bulid Tracking - Session';

-- Data exporting was unselected.


-- Dumping structure for table trackingadmin.RBT_SESSION_PROJECT
DROP TABLE IF EXISTS `RBT_SESSION_PROJECT`;
CREATE TABLE IF NOT EXISTS "RBT_SESSION_PROJECT" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT,
  "session_id" int(10) NOT NULL,
  "project_id" int(10) NOT NULL,
  PRIMARY KEY ("id")
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
