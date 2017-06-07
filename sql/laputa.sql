/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : laputa

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-06-07 14:16:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for flashed_tokens
-- ----------------------------
DROP TABLE IF EXISTS `flashed_tokens`;
CREATE TABLE `flashed_tokens` (
  `token` char(32) NOT NULL,
  `app_name` varchar(255) NOT NULL,
  `email` text,
  `project_id` bigint(64) NOT NULL,
  `device_id` bigint(64) NOT NULL,
  `is_activated` tinyint(1) DEFAULT '0',
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`token`,`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for purchase
-- ----------------------------
DROP TABLE IF EXISTS `purchase`;
CREATE TABLE `purchase` (
  `email` varchar(255) NOT NULL,
  `reward` int(11) NOT NULL,
  `transactionId` varchar(255) NOT NULL,
  `price` double(16,6) DEFAULT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`email`,`transactionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for redeem
-- ----------------------------
DROP TABLE IF EXISTS `redeem`;
CREATE TABLE `redeem` (
  `token` char(32) NOT NULL,
  `company` text,
  `isRedeemed` tinyint(1) DEFAULT '0',
  `reward` int(11) NOT NULL DEFAULT '0',
  `email` text,
  `version` int(11) NOT NULL DEFAULT '1',
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for reporting_app_command_stat_minute
-- ----------------------------
DROP TABLE IF EXISTS `reporting_app_command_stat_minute`;
CREATE TABLE `reporting_app_command_stat_minute` (
  `region` varchar(255) NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `response` bigint(64) DEFAULT NULL,
  `register` bigint(64) DEFAULT NULL,
  `login` bigint(64) DEFAULT NULL,
  `load_profile` bigint(64) DEFAULT NULL,
  `app_sync` bigint(64) DEFAULT NULL,
  `sharing` bigint(64) DEFAULT NULL,
  `get_token` bigint(64) DEFAULT NULL,
  `ping` bigint(64) DEFAULT NULL,
  `activate` bigint(64) DEFAULT NULL,
  `deactivate` bigint(64) DEFAULT NULL,
  `refresh_token` bigint(64) DEFAULT NULL,
  `get_graph_data` bigint(64) DEFAULT NULL,
  `export_graph_data` bigint(64) DEFAULT NULL,
  `set_widget_property` bigint(64) DEFAULT NULL,
  `bridge` bigint(64) DEFAULT NULL,
  `hardware` bigint(64) DEFAULT NULL,
  `get_share_dash` bigint(64) DEFAULT NULL,
  `get_share_token` bigint(64) DEFAULT NULL,
  `refresh_share_token` bigint(64) DEFAULT NULL,
  `share_login` bigint(64) DEFAULT NULL,
  `create_project` bigint(64) DEFAULT NULL,
  `update_project` bigint(64) DEFAULT NULL,
  `delete_project` bigint(64) DEFAULT NULL,
  `hardware_sync` bigint(64) DEFAULT NULL,
  `internal` bigint(64) DEFAULT NULL,
  `sms` bigint(64) DEFAULT NULL,
  `tweet` bigint(64) DEFAULT NULL,
  `email` bigint(64) DEFAULT NULL,
  `push` bigint(64) DEFAULT NULL,
  `add_push_token` bigint(64) DEFAULT NULL,
  `create_widget` bigint(64) DEFAULT NULL,
  `update_widget` bigint(64) DEFAULT NULL,
  `delete_widget` bigint(64) DEFAULT NULL,
  `create_device` bigint(64) DEFAULT NULL,
  `update_device` bigint(64) DEFAULT NULL,
  `delete_device` bigint(64) DEFAULT NULL,
  `get_devices` bigint(64) DEFAULT NULL,
  `create_tag` bigint(64) DEFAULT NULL,
  `update_tag` bigint(64) DEFAULT NULL,
  `delete_tag` bigint(64) DEFAULT NULL,
  `get_tags` bigint(64) DEFAULT NULL,
  `add_energy` bigint(64) DEFAULT NULL,
  `get_energy` bigint(64) DEFAULT NULL,
  `get_server` bigint(64) DEFAULT NULL,
  `connect_redirect` bigint(64) DEFAULT NULL,
  `web_sockets` bigint(64) DEFAULT NULL,
  `eventor` bigint(64) DEFAULT NULL,
  `webhooks` bigint(64) DEFAULT NULL,
  `appTotal` bigint(64) DEFAULT NULL,
  `hardTotal` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`region`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for reporting_app_stat_minute
-- ----------------------------
DROP TABLE IF EXISTS `reporting_app_stat_minute`;
CREATE TABLE `reporting_app_stat_minute` (
  `region` varchar(255) NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `active` bigint(64) DEFAULT NULL,
  `active_week` bigint(64) DEFAULT NULL,
  `active_month` bigint(64) DEFAULT NULL,
  `minute_rate` bigint(64) DEFAULT NULL,
  `connected` bigint(64) DEFAULT NULL,
  `online_apps` bigint(64) DEFAULT NULL,
  `online_hards` bigint(64) DEFAULT NULL,
  `total_online_apps` bigint(64) DEFAULT NULL,
  `total_online_hards` bigint(64) DEFAULT NULL,
  `registrations` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`region`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for reporting_average_daily
-- ----------------------------
DROP TABLE IF EXISTS `reporting_average_daily`;
CREATE TABLE `reporting_average_daily` (
  `email` varchar(255) NOT NULL,
  `project_id` bigint(64) NOT NULL,
  `device_id` bigint(64) NOT NULL,
  `pin` int(2) NOT NULL,
  `pinType` char(1) NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `value` double(16,6) DEFAULT NULL,
  PRIMARY KEY (`email`,`project_id`,`device_id`,`pin`,`pinType`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for reporting_average_hourly
-- ----------------------------
DROP TABLE IF EXISTS `reporting_average_hourly`;
CREATE TABLE `reporting_average_hourly` (
  `email` varchar(255) NOT NULL,
  `project_id` bigint(64) NOT NULL,
  `device_id` bigint(64) NOT NULL,
  `pin` int(2) NOT NULL,
  `pinType` char(1) NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `value` double(16,6) DEFAULT NULL,
  PRIMARY KEY (`email`,`project_id`,`device_id`,`pin`,`pinType`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for reporting_average_minute
-- ----------------------------
DROP TABLE IF EXISTS `reporting_average_minute`;
CREATE TABLE `reporting_average_minute` (
  `email` varchar(255) NOT NULL,
  `project_id` bigint(64) NOT NULL,
  `device_id` bigint(64) NOT NULL,
  `pin` int(2) NOT NULL,
  `pinType` char(1) NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `value` double(16,6) DEFAULT NULL,
  PRIMARY KEY (`email`,`project_id`,`device_id`,`pin`,`pinType`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for reporting_http_command_stat_minute
-- ----------------------------
DROP TABLE IF EXISTS `reporting_http_command_stat_minute`;
CREATE TABLE `reporting_http_command_stat_minute` (
  `region` varchar(255) NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_hardware_connected` bigint(64) DEFAULT NULL,
  `is_app_connected` bigint(64) DEFAULT NULL,
  `get_pin_data` bigint(64) DEFAULT NULL,
  `update_pin` bigint(64) DEFAULT NULL,
  `email` bigint(64) DEFAULT NULL,
  `push` bigint(64) DEFAULT NULL,
  `get_project` bigint(64) DEFAULT NULL,
  `qr` bigint(64) DEFAULT NULL,
  `get_history_pin_data` bigint(64) DEFAULT NULL,
  `total` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`region`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for reporting_raw_data
-- ----------------------------
DROP TABLE IF EXISTS `reporting_raw_data`;
CREATE TABLE `reporting_raw_data` (
  `email` varchar(255) NOT NULL,
  `project_id` bigint(64) NOT NULL,
  `device_id` bigint(64) NOT NULL,
  `pin` int(2) NOT NULL,
  `pinType` char(1) NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `stringValue` text,
  `doubleValue` double(16,6) DEFAULT NULL,
  PRIMARY KEY (`email`,`project_id`,`device_id`,`pin`,`pinType`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `email` varchar(255) NOT NULL,
  `appName` varchar(255) NOT NULL,
  `region` mediumtext,
  `name` mediumtext,
  `pass` mediumtext,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_logged` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_logged_ip` mediumtext,
  `is_facebook_user` tinyint(1) DEFAULT NULL,
  `is_super_admin` tinyint(1) DEFAULT '0',
  `energy` int(11) DEFAULT NULL,
  `json` mediumtext,
  PRIMARY KEY (`email`,`appName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
