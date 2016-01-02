/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50625
 Source Host           : localhost
 Source Database       : cungu

 Target Server Type    : MySQL
 Target Server Version : 50625
 File Encoding         : utf-8

 Date: 11/22/2015 21:31:06 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `cg_sequence`
-- ----------------------------
DROP TABLE IF EXISTS `cg_sequence`;
CREATE TABLE `cg_sequence` (
  `name` varchar(50) NOT NULL,
  `sharding` tinyint(4) NOT NULL,
  `value` bigint(16) unsigned NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`name`,`sharding`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `cg_sequence`
-- ----------------------------
BEGIN;
INSERT INTO `cg_sequence` VALUES ('OrderId', '1', '0', '2015-11-22 21:27:28'), ('OrderId', '2', '10', '2015-11-22 21:26:05'), ('OrderId', '3', '20', '2015-11-22 21:26:05'), ('OrderId', '4', '30', '2015-11-22 21:26:05'), ('OrderId', '5', '40', '2015-11-22 21:26:05'), ('OrderId', '6', '50', '2015-11-22 21:26:05'), ('OrderId', '7', '60', '2015-11-22 21:26:05'), ('OrderId', '8', '70', '2015-11-22 21:26:05'), ('OrderId', '9', '80', '2015-11-22 21:26:05'), ('OrderId', '10', '90', '2015-11-22 21:26:05');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
