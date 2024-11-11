/*
 Navicat Premium Data Transfer

 Source Server         : 内网测试服
 Source Server Type    : MariaDB
 Source Server Version : 50560
 Source Host           : 192.168.2.220:3306
 Source Schema         : da_tang

 Target Server Type    : MariaDB
 Target Server Version : 50560
 File Encoding         : 65001

 Date: 18/11/2019 15:08:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
  `uid` bigint(20) NOT NULL DEFAULT 0,
  `passwd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `createTime` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  `mac` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `phoneVer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `phoneOsVer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '姓名',
  `icon` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像',
  `sex` tinyint(4) NULL DEFAULT 1 COMMENT '性别',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '城市',
  `identityCard` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '身份证',
  `otherPlatformToken` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '其他平台身份表示',
  `type` tinyint(4) NULL DEFAULT 0 COMMENT '注册账号类型,0: 游客登陆, 1: 手机号登陆, 2: 快速登陆, 3: 微信登陆',
  `payPassword` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '支付密码',
  `noNeedPayPassword` tinyint(4) NOT NULL DEFAULT 0 COMMENT '免密支付状态：0-关闭; 1-开启;',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '状态, 0: 正常, 1: 删除, 2: 封号',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `phone`(`phone`) USING BTREE,
  INDEX `otherPlatformToken`(`otherPlatformToken`(191)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for accountBlackList
-- ----------------------------
DROP TABLE IF EXISTS `accountBlackList`;
CREATE TABLE `accountBlackList`  (
  `uid` int(11) NOT NULL COMMENT '玩家ID',
  `createdAt` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updatedAt` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for accountUid
-- ----------------------------
DROP TABLE IF EXISTS `accountUid`;
CREATE TABLE `accountUid`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT 'uid',
  `good` tinyint(4) NULL DEFAULT 0 COMMENT '靓号, 0: 不是, 1: 是',
  `state` tinyint(4) NULL DEFAULT 1 COMMENT '状态, 1: 可用, 0: 不可用',
  INDEX `uid`(`uid`) USING BTREE,
  INDEX `state`(`state`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for accountWithdrawRecord
-- ----------------------------
DROP TABLE IF EXISTS `accountWithdrawRecord`;
CREATE TABLE `accountWithdrawRecord`  (
  `uid` bigint(20) NOT NULL,
  `playerUid` bigint(20) NULL DEFAULT NULL COMMENT '玩家Uid',
  `account` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提现账号',
  `address` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户行',
  `realName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `amount` bigint(20) NULL DEFAULT NULL COMMENT '申请金额（单位：分）',
  `fee` int(11) NULL DEFAULT NULL COMMENT '费率百分比',
  `getMoney` bigint(20) NULL DEFAULT NULL COMMENT '到账金额',
  `type` tinyint(4) NULL DEFAULT NULL COMMENT '提现方式类型 1:微信  2:支付宝  3:银行卡',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '提现状态：0-未审核; 1-通过; 2-拒绝;',
  `financeUid` bigint(20) NULL DEFAULT NULL COMMENT '平台财务Uid',
  `createdAt` bigint(20) NULL DEFAULT NULL COMMENT '申请时间 毫秒时间戳',
  `updatedAt` bigint(20) NULL DEFAULT NULL COMMENT '审核时间 毫秒时间戳',
  `moneyType` int(11) NULL DEFAULT 1 COMMENT '提现货币类型：1-钱包; 2-星币',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for accountWithdrawType
-- ----------------------------
DROP TABLE IF EXISTS `accountWithdrawType`;
CREATE TABLE `accountWithdrawType`  (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `playerUid` bigint(20) NULL DEFAULT NULL COMMENT '玩家ID',
  `type` tinyint(4) NULL DEFAULT NULL COMMENT '类型 1:微信 2:支付宝 3:银行卡',
  `payeeAccount` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收款方账号',
  `payeeRealName` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收款方真实姓名',
  `payeeAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户行地址',
  `payeeLocation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户行所在地',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态 0:不可用 1:可用; 2-常用',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for arena
-- ----------------------------
DROP TABLE IF EXISTS `arena`;
CREATE TABLE `arena`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '竞技场uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `createTime` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  `endTime` bigint(20) NULL DEFAULT -1 COMMENT '结束时间',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `timeLimit` int(11) NULL DEFAULT 1 COMMENT '时间限制',
  `minArena` int(11) NULL DEFAULT 0 COMMENT '加入最小竞技值',
  `costModel` int(11) NULL DEFAULT 0 COMMENT '付费模式',
  `costModelValue` int(11) NULL DEFAULT 0 COMMENT '付费模式值',
  `rule` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '规则',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '状态, 0: 正常, 1: 关闭',
  `totalCost` int(11) NULL DEFAULT 0 COMMENT '总消耗',
  `scoreRank` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '竞技场分数排行',
  `floorUid` bigint(20) NULL DEFAULT -1 COMMENT '楼层uid',
  `extra` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '额外',
  `updateTime` bigint(20) NULL DEFAULT 0 COMMENT '更新时间',
  `leaveArenaValue` int(11) NULL DEFAULT 0 COMMENT '离开竞技值',
  `banList` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '禁言列表',
  `ownerType` tinyint(4) NULL DEFAULT 1 COMMENT '竞技场拥有着',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for arenaScore
-- ----------------------------
DROP TABLE IF EXISTS `arenaScore`;
CREATE TABLE `arenaScore`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '竞技场战绩uid',
  `arenaUid` bigint(20) NULL DEFAULT 0 COMMENT '竞技场uid',
  `arenaGameType` int(11) NULL DEFAULT 0 COMMENT '竞技场游戏类型',
  `arenaGameSubType` int(11) NULL DEFAULT 0 COMMENT '竞技场游戏子类型',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `beginTime` bigint(20) NULL DEFAULT 0 COMMENT '开始时间',
  `endTime` bigint(20) NULL DEFAULT 0 COMMENT '结束时间',
  `bureau` int(11) NULL DEFAULT 0 COMMENT '局数',
  `score` int(11) NULL DEFAULT 0 COMMENT '本轮积分',
  `state` int(11) NULL DEFAULT 0 COMMENT '状态,0:正常, 1:已经清理, 2: 正在使用, 3; 已经废弃',
  `clearPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '清理操作玩家uid',
  `clearTime` bigint(20) NULL DEFAULT 0 COMMENT '清理操作时间',
  `allCnt` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '大结算记录',
  `recordUid` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '战绩列表',
  `ownerType` tinyint(4) NULL DEFAULT 1 COMMENT '战绩拥有着',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchArenaUidAndPlayerUid`(`arenaUid`, `playerUid`, `state`) USING BTREE,
  INDEX `searchPlayerUid`(`playerUid`, `ownerType`, `state`) USING BTREE,
  INDEX `searchGroupUidAndPlayerUid`(`groupUid`, `ownerType`, `playerUid`, `state`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for arenaScoreDetail
-- ----------------------------
DROP TABLE IF EXISTS `arenaScoreDetail`;
CREATE TABLE `arenaScoreDetail`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '记录uid',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '时间',
  `score` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '分数',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for arenaValueRecord
-- ----------------------------
DROP TABLE IF EXISTS `arenaValueRecord`;
CREATE TABLE `arenaValueRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '竞技值修改记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `operatorPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '操作玩家uid',
  `arenaValue` int(11) NULL DEFAULT 0 COMMENT '修改竞技值',
  `operatorTime` bigint(20) NULL DEFAULT 0 COMMENT '操作时间',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '0: 正常, 1: 废弃',
  `optType` tinyint(4) NULL DEFAULT 0 COMMENT '操作类型',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `search`(`groupUid`, `playerUid`, `state`, `optType`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for assistantWeChat
-- ----------------------------
DROP TABLE IF EXISTS `assistantWeChat`;
CREATE TABLE `assistantWeChat`  (
  `uid` bigint(20) NULL DEFAULT NULL,
  `weChat` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客服微信号',
  `province` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '省',
  `city` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '市',
  `district` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '区',
  `adCode` bigint(20) NOT NULL DEFAULT 0 COMMENT '区域编号'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for autoUpDeputyRecord
-- ----------------------------
DROP TABLE IF EXISTS `autoUpDeputyRecord`;
CREATE TABLE `autoUpDeputyRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '竞技值修改记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `operatorTime` bigint(20) NULL DEFAULT 0 COMMENT '操作时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `search`(`groupUid`, `playerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for boxRoomScore
-- ----------------------------
DROP TABLE IF EXISTS `boxRoomScore`;
CREATE TABLE `boxRoomScore`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '包厢房间战绩uid',
  `roomUid` bigint(20) NULL DEFAULT 0 COMMENT '房间uid',
  `roomId` int(11) NULL DEFAULT 0 COMMENT '房间id',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `roomType` int(11) NULL DEFAULT 0 COMMENT '房间类型 2.2人场 3.3人场',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `boxUid` bigint(20) NULL DEFAULT 0 COMMENT '包厢uid',
  `playerUid1` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家1',
  `playerUid2` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家2',
  `playerUid3` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家3',
  `playerUid4` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家4',
  `playerUid5` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家5',
  `playerUid6` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家6',
  `playerUid7` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家7',
  `playerUid8` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家8',
  `playerUid9` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家9',
  `playerUid10` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家10',
  `playerUid11` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家11',
  `beginTime` bigint(20) NULL DEFAULT 0 COMMENT '开始时间',
  `endTime` bigint(20) NULL DEFAULT 0 COMMENT '结束时间',
  `totalScore` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '总战绩',
  `record` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '战绩记录',
  `gain` int(11) NULL DEFAULT 0 COMMENT '打赏次数',
  `gainList` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '打赏列表',
  `mark` int(4) NULL DEFAULT 0 COMMENT '是否标记 0.未标记 1.已标记',
  `ownerType` int(4) NULL DEFAULT 1 COMMENT '1.亲友圈 2.雀友圈',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for forbid
-- ----------------------------
DROP TABLE IF EXISTS `forbid`;
CREATE TABLE `forbid`  (
  `uid` bigint(20) NOT NULL COMMENT 'uid',
  `groupType` int(11) NOT NULL COMMENT '类型(0亲友圈1联盟)',
  `groupUid` bigint(20) NOT NULL COMMENT '群uid',
  `playerUids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '防作弊玩家id集合',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '防作弊玩家信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gameDownloadRecord
-- ----------------------------
DROP TABLE IF EXISTS `gameDownloadRecord`;
CREATE TABLE `gameDownloadRecord`  (
  `uid` bigint(20) NOT NULL,
  `userUid` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `groupUid` bigint(20) NULL DEFAULT NULL COMMENT '群Uid',
  `uuid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'uuid',
  `deceiveType` tinyint(4) UNSIGNED NULL DEFAULT NULL COMMENT '设备类型：1-IOS; 2-Andriod',
  `createdAt` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '群uid',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '群名',
  `desc` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '群介绍',
  `icon` varchar(355) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '群icon',
  `boxLike` tinyint(4) NULL DEFAULT 0 COMMENT '点赞开关',
  `owner` bigint(20) NULL DEFAULT 0 COMMENT '群创建者',
  `createTime` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  `maxMemberCnt` int(11) NULL DEFAULT 500 COMMENT '成员上限',
  `members` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '成员',
  `applys` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '申请者',
  `leaves` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '离开者',
  `state` int(11) NULL DEFAULT 0 COMMENT '状态: 0:正常, 1: 拒绝, 2:删除',
  `serviceCharge` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '服务费记录',
  `box` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '包厢信息',
  `arena` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '竞技场信息',
  `totalCost` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '累计消耗',
  `totalServiceCharge` bigint(20) NULL DEFAULT 0 COMMENT '总服务费',
  `arenaScoreRank` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '竞技场分数排行',
  `boxScoreRank` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '包厢分数排行',
  `quickAddSwitch` tinyint(4) NULL DEFAULT 1 COMMENT '快速上分开关',
  `flrSwitch` tinyint(4) NULL DEFAULT 0,
  `mineRedPackConf` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '埋雷红包设置',
  `totalIncArenaValue` bigint(20) NULL DEFAULT 0 COMMENT '总添加竞技值',
  `totalDecArenaValue` bigint(20) NULL DEFAULT 0 COMMENT '总减少竞技值',
  `totalIncArenaValueByWallet` bigint(20) NULL DEFAULT 0 COMMENT '总添加竞技值通过钱包',
  `totalIncArenaValueByQuick` bigint(20) NULL DEFAULT 0 COMMENT '总添加竞技值通过快速充值',
  `totalDecServiceCharge` bigint(20) NULL DEFAULT 0 COMMENT '总减少服务费',
  `totalDecServiceValue` bigint(20) NULL DEFAULT 0 COMMENT '总减少服务费',
  `announcement` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群公告',
  `announcementExpireAt` bigint(20) NOT NULL DEFAULT 0 COMMENT '群公告过期时间戳，毫秒',
  `quickDecSwitch` tinyint(4) NOT NULL DEFAULT 0 COMMENT '快速下分开关',
  `shareSwitch` tinyint(4) NOT NULL DEFAULT 0 COMMENT '分享开关',
  `shareAward` int(12) NOT NULL DEFAULT 0 COMMENT '分享奖励值',
  `floor` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '楼层信息',
  `financeSwitch` tinyint(4) NOT NULL DEFAULT 0 COMMENT '财务开关',
  `serviceChargeDivideAdmin` int(11) NOT NULL DEFAULT 0 COMMENT '服务费之管理费比例',
  `newbieChatSwitch` int(11) NOT NULL DEFAULT 0 COMMENT '新人聊天受限开关，0-关闭，1-开启',
  `mineRedScoreRank` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '红包埋雷分数排行',
  `groupSwitch` bigint(20) NULL DEFAULT 0 COMMENT '群开关',
  `autoUpDeputyConf` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自动升副帮主设置',
  `quests` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '小组任务',
  `autoAgreeSwitch` tinyint(4) NULL DEFAULT 0 COMMENT '自动进群开关',
  `delPlayer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '被删除的玩家',
  `leagueUid` bigint(20) NULL DEFAULT -1 COMMENT '联盟uid',
  `serviceChargeDivideAdminByLeague` int(11) NULL DEFAULT 0 COMMENT '群联盟管理费',
  `gameDesc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '游戏描述',
  `pavilionUid` bigint(20) NULL DEFAULT -1 COMMENT '雀友馆uid',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupArenaCostDiamondDaily
-- ----------------------------
DROP TABLE IF EXISTS `groupArenaCostDiamondDaily`;
CREATE TABLE `groupArenaCostDiamondDaily`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '群竞技场钻石每日消耗uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `arenaCost` int(11) NULL DEFAULT 0 COMMENT '竞技场消耗',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '每日零点时间戳',
  `boxCost` int(11) NULL DEFAULT 0 COMMENT '包厢消耗',
  `mineRedCost` int(11) NULL DEFAULT 0 COMMENT '红包埋雷消耗',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchGroupUid`(`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupArenaCostDiamondDetail
-- ----------------------------
DROP TABLE IF EXISTS `groupArenaCostDiamondDetail`;
CREATE TABLE `groupArenaCostDiamondDetail`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '群竞技场钻石消耗uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `gameUid` bigint(20) NULL DEFAULT 0 COMMENT '游戏uid',
  `bureau` int(11) NULL DEFAULT 0 COMMENT '局数',
  `cost` int(11) NULL DEFAULT 0 COMMENT '消耗',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '每日零点时间戳',
  `roomType` tinyint(4) NULL DEFAULT 1 COMMENT '房间类型',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchTimeAndGroupUidAndRoomType`(`time`, `groupUid`, `roomType`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupCurrencyRecord
-- ----------------------------
DROP TABLE IF EXISTS `groupCurrencyRecord`;
CREATE TABLE `groupCurrencyRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '群货币记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '时间戳',
  `arenaValue` int(11) NULL DEFAULT 0 COMMENT '当天最后竞技值',
  `incArenaValue` int(11) NULL DEFAULT 0 COMMENT '当天添加竞技值',
  `decArenaValue` int(11) NULL DEFAULT 0 COMMENT '当前减少竞技值',
  `incArenaValueByWallet` int(11) NULL DEFAULT 0 COMMENT '当前通过钱包添加竞技值',
  `incArenaValueByQuick` int(11) NULL DEFAULT 0 COMMENT '当前通过快速充值添加竞技值',
  `serviceValue` int(11) NULL DEFAULT 0 COMMENT '当天最后服务费',
  `chiefServiceValue` int(11) NULL DEFAULT 0 COMMENT '当天最后群主服务费',
  `decServiceValue` int(11) NULL DEFAULT 0 COMMENT '当天减少服务费',
  `decServiceValueByLeague` int(11) NULL DEFAULT 0 COMMENT '当前减少联盟服务费',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByGroupUidAndTime`(`groupUid`, `time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupFinancialApplyRecord
-- ----------------------------
DROP TABLE IF EXISTS `groupFinancialApplyRecord`;
CREATE TABLE `groupFinancialApplyRecord`  (
  `uid` bigint(20) NOT NULL,
  `userUid` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户uid',
  `groupUid` bigint(20) NOT NULL DEFAULT 0 COMMENT '群uid',
  `financeUid` bigint(20) NOT NULL DEFAULT 0 COMMENT '财务uid',
  `amount` bigint(20) NOT NULL DEFAULT 0 COMMENT '单位：分',
  `type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '1-支付宝 2-微信',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0-申请中；1-已完成；2-已拒绝；3-待处理上分；99-处理锁定中',
  `img` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上传的图片',
  `createdAt` bigint(20) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `updatedAt` bigint(20) NOT NULL DEFAULT 0 COMMENT '更新时间',
  `times` bigint(20) NOT NULL DEFAULT 0 COMMENT '今日申请次数'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupGiveMoney
-- ----------------------------
DROP TABLE IF EXISTS `groupGiveMoney`;
CREATE TABLE `groupGiveMoney`  (
  `uid` bigint(20) NOT NULL,
  `playerUid` bigint(20) NULL DEFAULT NULL,
  `tarPlayerUid` bigint(20) NULL DEFAULT NULL,
  `value` bigint(20) NULL DEFAULT NULL,
  `time` bigint(20) NULL DEFAULT NULL,
  `groupUid` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupServiceSystemRecord
-- ----------------------------
DROP TABLE IF EXISTS `groupServiceSystemRecord`;
CREATE TABLE `groupServiceSystemRecord`  (
  `uid` bigint(20) NOT NULL,
  `playerUid` bigint(20) NULL DEFAULT NULL COMMENT '玩家ID',
  `playerName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '玩家名称',
  `groupId` bigint(20) NULL DEFAULT NULL COMMENT '群ID',
  `joinTime` bigint(20) NULL DEFAULT NULL COMMENT '加入时间',
  `serviceUid` bigint(20) NULL DEFAULT NULL COMMENT '客服ID',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hundredArenaBureauRecord
-- ----------------------------
DROP TABLE IF EXISTS `hundredArenaBureauRecord`;
CREATE TABLE `hundredArenaBureauRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '百人场局数记录uid',
  `arenaUid` bigint(20) NULL DEFAULT 0 COMMENT '百人场uid',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '开局时间',
  `endTime` bigint(20) NULL DEFAULT 0 COMMENT '结束时间',
  `cardInfo` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '牌信息',
  `bankerPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '庄家uid',
  `bankerWinValue` int(11) NULL DEFAULT 0 COMMENT '庄家输赢分数',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByArenaUidAndBankerPlayerUid`(`arenaUid`, `bankerPlayerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hundredArenaHBMLBureauRecord
-- ----------------------------
DROP TABLE IF EXISTS `hundredArenaHBMLBureauRecord`;
CREATE TABLE `hundredArenaHBMLBureauRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '百人场局数记录uid',
  `arenaUid` bigint(20) NULL DEFAULT 0 COMMENT '百人场uid',
  `value` int(11) NULL DEFAULT 0 COMMENT '赢的金额',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '开局时间',
  `endTime` bigint(20) NULL DEFAULT 0 COMMENT '结束时间',
  `rebRecord` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '下注记录',
  `bankerPlayerUid` bigint(20) NULL DEFAULT NULL,
  `bankerWinValue` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByArenaUid`(`arenaUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hundredArenaHBMLRebRecord
-- ----------------------------
DROP TABLE IF EXISTS `hundredArenaHBMLRebRecord`;
CREATE TABLE `hundredArenaHBMLRebRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '百人场下注记录uid',
  `arenaUid` bigint(20) NULL DEFAULT 0 COMMENT '百人场uid',
  `rebPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '下注玩家uid',
  `index` int(11) NULL DEFAULT 0 COMMENT '下注位置',
  `value` int(11) NULL DEFAULT 0 COMMENT '下注金额',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '下注时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByPlayer`(`rebPlayerUid`, `arenaUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hundredArenaRebRecord
-- ----------------------------
DROP TABLE IF EXISTS `hundredArenaRebRecord`;
CREATE TABLE `hundredArenaRebRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '百人场下注记录uid',
  `arenaUid` bigint(20) NULL DEFAULT 0 COMMENT '百人场uid',
  `rebPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '下注玩家uid',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '下注时间',
  `rebInfo` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '下注信息',
  `bankerCardType` tinyint(4) NULL DEFAULT 0 COMMENT '庄家牌型',
  `rebValue` int(11) NULL DEFAULT 0 COMMENT '下注金额',
  `winValue` int(11) NULL DEFAULT 0 COMMENT '输赢金额',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群ID',
  `fanliValue` int(11) NULL DEFAULT 0 COMMENT '返利',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '所在游戏类型',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByPlayer`(`rebPlayerUid`, `arenaUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for league
-- ----------------------------
DROP TABLE IF EXISTS `league`;
CREATE TABLE `league`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT 'uid',
  `createTime` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  `endTime` bigint(20) NULL DEFAULT 0 COMMENT '结束时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '联盟名',
  `desc` varchar(1014) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '联盟描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '联盟icon',
  `announcementExpireAt` bigint(20) NULL DEFAULT -1 COMMENT '公告过期时间',
  `announcement` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '公告',
  `serviceChargeDivideAdmin` int(11) NULL DEFAULT 0 COMMENT '管理费比例',
  `floor` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '楼层信息',
  `member` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '成员',
  `state` int(11) NULL DEFAULT 0 COMMENT '状态: 0:正常, 1: 拒绝, 2:删除',
  `costDiamond` bigint(20) NULL DEFAULT 0 COMMENT '钻石消耗',
  `leaderUid` bigint(20) NULL DEFAULT -1 COMMENT '盟主uid',
  `viceLeader` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联盟副盟主',
  `arenaScoreCnt` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '战绩条数',
  `curArenaScore` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '当前战绩',
  `gameDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联盟主打游戏描述',
  `money` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT ' 购买过联盟币的玩家货币信息，退出联盟也不清理',
  `openJoin` int(11) NULL DEFAULT NULL COMMENT '是否开放加入',
  `rewardValueDivide` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '获取比例',
  `quests` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '小组任务(满足局数领取竞技分活动)',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for leagueRecord
-- ----------------------------
DROP TABLE IF EXISTS `leagueRecord`;
CREATE TABLE `leagueRecord`  (
  `uid` bigint(20) NOT NULL,
  `playerUid` bigint(20) NOT NULL,
  `beginAmount` int(20) NOT NULL DEFAULT 0 COMMENT '操作前的金额',
  `amount` int(20) NOT NULL DEFAULT 0 COMMENT '操作金额',
  `inMoney` int(20) NOT NULL DEFAULT 0 COMMENT '收入金额',
  `outMoney` int(20) NOT NULL DEFAULT 0 COMMENT '支出金额',
  `month` bigint(20) NOT NULL DEFAULT 0 COMMENT '本月开始时间戳',
  `action` tinyint(4) NOT NULL DEFAULT 0 COMMENT '操作类型：操作：0我给其他人上分 1我给其他人下分 2其他人给我上分 3其他人给我下分；',
  `optPlayer` bigint(20) NOT NULL COMMENT '操作人Uid',
  `leagueUid` bigint(20) NOT NULL DEFAULT 0 COMMENT '联盟Uid',
  `createdAt` bigint(20) NOT NULL DEFAULT 0 COMMENT '操作时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `playerUid_index`(`playerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for leagueRoomScore
-- ----------------------------
DROP TABLE IF EXISTS `leagueRoomScore`;
CREATE TABLE `leagueRoomScore`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '包厢房间战绩uid',
  `roomUid` bigint(20) NULL DEFAULT 0 COMMENT '房间uid',
  `roomId` int(11) NULL DEFAULT 0 COMMENT '房间id',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `roomType` int(11) NULL DEFAULT 0 COMMENT '房间类型 1.可少人 2.2人场 3.3人场',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '联盟uid',
  `arenaUid` bigint(20) NULL DEFAULT 0 COMMENT '竞技场uid',
  `playerUid1` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家1',
  `playerUid2` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家2',
  `playerUid3` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家3',
  `playerUid4` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家4',
  `playerUid5` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家5',
  `playerUid6` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家6',
  `playerUid7` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家7',
  `playerUid8` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家8',
  `playerUid9` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家9',
  `playerUid10` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家10',
  `playerUid11` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家11',
  `beginTime` bigint(20) NULL DEFAULT 0 COMMENT '开始时间',
  `endTime` bigint(20) NULL DEFAULT 0 COMMENT '结束时间',
  `totalScore` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '总战绩',
  `record` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '战绩记录',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '联盟战绩数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for leagueValueRecord
-- ----------------------------
DROP TABLE IF EXISTS `leagueValueRecord`;
CREATE TABLE `leagueValueRecord`  (
  `uid` bigint(20) NOT NULL COMMENT '自增id',
  `playerUid` bigint(20) NULL DEFAULT NULL COMMENT '收益人uid',
  `optTime` bigint(20) NULL DEFAULT NULL COMMENT '操作日期',
  `leagueUid` bigint(20) NULL DEFAULT NULL COMMENT '联盟uid',
  `value` bigint(20) NULL DEFAULT NULL COMMENT '数值',
  `groupUid` bigint(20) NULL DEFAULT NULL COMMENT '群uid',
  `optPlayerUid` bigint(20) NULL DEFAULT NULL COMMENT '执行人uid',
  `fromType` int(11) NULL DEFAULT 0 COMMENT '记录产生来源\r\n0兑换成竞技值扣除\r\n1联盟管理费添加\r\n2群管理费添加\r\n3一条线抽成添加\r\n4直属抽成添加\r\n5其他抽成都是群主的',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for locationInfo
-- ----------------------------
DROP TABLE IF EXISTS `locationInfo`;
CREATE TABLE `locationInfo`  (
  `uid` bigint(20) NOT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `location_index`(`location`(191)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logAccount
-- ----------------------------
DROP TABLE IF EXISTS `logAccount`;
CREATE TABLE `logAccount`  (
  `uid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志记录唯一ID',
  `targetUid` bigint(20) NOT NULL COMMENT '账号ID',
  `action` int(11) NOT NULL COMMENT '日志类型：1-注册，2-登录，3-注销',
  `timestamp` bigint(20) NOT NULL COMMENT '发生时的unix timestamp',
  `accountType` int(11) NOT NULL DEFAULT 0 COMMENT '注册账号类型,0: 游客登陆, 1: 手机号登陆, 2: 快速登陆, 3: 微信登陆',
  `serverId` int(11) NOT NULL DEFAULT 0 COMMENT '服务器ID',
  `deviceModel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '机型',
  `deviceSn` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '设备序号',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '地址',
  `osVersion` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '终端系统版本',
  `appVersion` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'App版本',
  `channelId` int(11) NOT NULL DEFAULT 0 COMMENT '渠道ID',
  `mobileNumber` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '手机号',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `idx_logaccount_target`(`targetUid`) USING BTREE,
  INDEX `idx_logaccount_action`(`action`) USING BTREE,
  INDEX `idx_logaccount_time`(`timestamp`) USING BTREE,
  INDEX `idx_logaccount_accountType`(`accountType`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logAccountRemain
-- ----------------------------
DROP TABLE IF EXISTS `logAccountRemain`;
CREATE TABLE `logAccountRemain`  (
  `uid` bigint(20) NOT NULL,
  `date` bigint(20) NOT NULL COMMENT '日期时间戳 单位秒',
  `registerNum` bigint(20) NOT NULL DEFAULT 0 COMMENT '注册人数',
  `day_2` bigint(20) NOT NULL DEFAULT 0 COMMENT '2日留存',
  `day_3` bigint(20) NOT NULL DEFAULT 0 COMMENT '3日留存',
  `day_4` bigint(20) NOT NULL DEFAULT 0 COMMENT '4日留存',
  `day_5` bigint(20) NOT NULL DEFAULT 0 COMMENT '5日留存',
  `day_6` bigint(20) NOT NULL DEFAULT 0 COMMENT '6日留存',
  `day_7` bigint(20) NOT NULL DEFAULT 0 COMMENT '7日留存',
  `day_14` bigint(20) NOT NULL DEFAULT 0 COMMENT '14日留存',
  `day_30` bigint(20) NOT NULL DEFAULT 0 COMMENT '30日留存',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mail
-- ----------------------------
DROP TABLE IF EXISTS `mail`;
CREATE TABLE `mail`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '邮件uid',
  `senderPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '发送者playerUid, -1: 为系统',
  `receivePlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '接收者playerUid',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '邮件标题',
  `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '邮件内容',
  `item` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '道具信息',
  `sendTime` bigint(20) NULL DEFAULT 0 COMMENT '发送时间',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '状态: 0: 正常, 1: 已读, 2: 删除',
  `itemState` tinyint(4) NULL DEFAULT 0 COMMENT '道具状态, 0: 不可领取, 1: 可领取, 2: 已领取',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mailBox
-- ----------------------------
DROP TABLE IF EXISTS `mailBox`;
CREATE TABLE `mailBox`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '邮箱uid',
  `messageUid` bigint(20) NULL DEFAULT 0 COMMENT '邮件uid',
  `messageUidByPlayer` bigint(20) NULL DEFAULT 0 COMMENT '相对于玩家邮件uid',
  `toPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '消息收件人uid',
  `tagPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '收件人uid',
  `fromPlayerUid` bigint(20) NULL DEFAULT -1 COMMENT '发件人uid',
  `fromPlayerName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '发件人name',
  `fromPlayerIcon` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '发件人icon',
  `fromGroupUid` bigint(20) NULL DEFAULT -1 COMMENT '来自群uid',
  `fromLeagueUid` bigint(20) NULL DEFAULT NULL COMMENT '来自联盟uid',
  `fromPavilionUid` bigint(20) NULL DEFAULT -1 COMMENT '来自雀友馆uid',
  `messageType` tinyint(4) NULL DEFAULT 0 COMMENT '消息类型',
  `contentType` tinyint(4) NULL DEFAULT 0 COMMENT '内容类型',
  `message` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息内容',
  `sayTime` bigint(20) NULL DEFAULT 0 COMMENT '发送时间',
  `param` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '状态',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `search`(`messageUid`) USING BTREE,
  INDEX `searchByPlayer`(`toPlayerUid`, `messageUidByPlayer`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mailBoxUid
-- ----------------------------
DROP TABLE IF EXISTS `mailBoxUid`;
CREATE TABLE `mailBoxUid`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '玩家uid',
  `lastMsgUid` bigint(20) NULL DEFAULT 0 COMMENT '最后消息uid',
  `lastMsgUidByClient` bigint(20) NULL DEFAULT 0 COMMENT '客户端最后一条消息uid',
  `recallMsgUid` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '撤回消息uid集合',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mineRedPacketReceivedRecord
-- ----------------------------
DROP TABLE IF EXISTS `mineRedPacketReceivedRecord`;
CREATE TABLE `mineRedPacketReceivedRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '领取埋雷红包记录Uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '领取者Uid',
  `redPacketUid` bigint(20) NULL DEFAULT 0 COMMENT '领取的红包Uid',
  `amount` int(11) NULL DEFAULT 0 COMMENT '领取的红包金额 单位:分',
  `feedback` int(11) NULL DEFAULT 0 COMMENT '中雷金额',
  `specialType` tinyint(4) NULL DEFAULT 0 COMMENT '是否特殊奖励：0-不是；1-豹子；2-顺子；3-0.01奖励',
  `specialAward` int(11) NULL DEFAULT 0 COMMENT '特殊奖励金额',
  `createdAt` bigint(20) NULL DEFAULT 0 COMMENT '领取的时间戳',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByRedPacketUid`(`redPacketUid`) USING BTREE,
  INDEX `searchByGroupUidAndPlayerUid`(`groupUid`, `playerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mineRedPacketSendRecord
-- ----------------------------
DROP TABLE IF EXISTS `mineRedPacketSendRecord`;
CREATE TABLE `mineRedPacketSendRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '红包Uid',
  `senderUid` bigint(20) NULL DEFAULT 0 COMMENT '发送者uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '红包所在群uid',
  `amount` int(11) NULL DEFAULT 0 COMMENT '红包总金额 单位:分',
  `count` int(11) NULL DEFAULT 0 COMMENT '红包数量',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '红包描述',
  `mine` int(11) NULL DEFAULT 0 COMMENT '埋雷数字',
  `mineMultiple` int(11) NULL DEFAULT 0 COMMENT '埋雷倍数*10',
  `specialAwards` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '特殊奖励',
  `costModel` int(11) NULL DEFAULT 0 COMMENT '抽水模式',
  `costModelValue` int(11) NULL DEFAULT 0 COMMENT '抽水值',
  `createdAt` bigint(20) NULL DEFAULT 0 COMMENT '红包发送时间戳',
  `expiredAt` bigint(20) NULL DEFAULT 0 COMMENT '红包过期时间戳',
  `clearAt` bigint(20) NULL DEFAULT 0 COMMENT '红包被领完时间戳',
  `receivedCount` int(11) NULL DEFAULT 0 COMMENT '被领取的红包数量',
  `receivedAmount` int(11) NULL DEFAULT 0 COMMENT '被领取的总金额 单位:分',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '红包状态: 0-领取中;1-已领完;2-过期;3-被退还',
  `returnAmount` int(11) NULL DEFAULT 0 COMMENT '被退还的总金额 单位:分',
  `feedbackCnt` int(11) NULL DEFAULT 0 COMMENT '中雷次数',
  `receivers` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '接收者列表',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByGroupUidAndSenderUid`(`groupUid`, `senderUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for moneyExpendRecord
-- ----------------------------
DROP TABLE IF EXISTS `moneyExpendRecord`;
CREATE TABLE `moneyExpendRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT 'uid',
  `fromUid` bigint(20) NULL DEFAULT -1 COMMENT '亲友圈id、联盟id',
  `playerUid` bigint(20) NULL DEFAULT -1 COMMENT '玩家ID',
  `operatorUid` bigint(20) NULL DEFAULT -1 COMMENT '操作人id',
  `value` bigint(20) NULL DEFAULT 0 COMMENT '房卡',
  `expendType` int(20) NULL DEFAULT 0 COMMENT '消耗或增长类型',
  `expendTime` bigint(20) NULL DEFAULT 0 COMMENT '消耗或增长时间',
  `roomType` int(16) NULL DEFAULT 0 COMMENT '类型（0别的、1大厅、2亲友圈、3联盟）',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for nearbyNote
-- ----------------------------
DROP TABLE IF EXISTS `nearbyNote`;
CREATE TABLE `nearbyNote`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '附近留言uid',
  `fromPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '留言玩家uid',
  `toPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '被留言玩家uid',
  `note` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '留言内容',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '状态, 0: 新增, 1: 查看过了, 2: 删除',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for nickname
-- ----------------------------
DROP TABLE IF EXISTS `nickname`;
CREATE TABLE `nickname`  (
  `uid` bigint(20) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `state` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pavilion
-- ----------------------------
DROP TABLE IF EXISTS `pavilion`;
CREATE TABLE `pavilion`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT 'uid',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '雀友馆名称',
  `desc` varchar(1014) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '雀友馆描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '雀友馆icon',
  `gameDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '雀友馆主打游戏描述',
  `createTime` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  `ownerUid` bigint(20) NULL DEFAULT -1 COMMENT '馆主uid',
  `announcementExpireAt` bigint(20) NULL DEFAULT -1 COMMENT '公告过期时间',
  `announcement` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '公告',
  `floor` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '楼层信息',
  `member` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '成员',
  `state` int(11) NULL DEFAULT 0 COMMENT '状态: 0:正常, 1: 拒绝, 2:删除',
  `vicePavilion` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '雀友馆副馆主',
  `openJoin` int(11) NULL DEFAULT 0 COMMENT '加入雀友馆开关',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for playFieldRecord
-- ----------------------------
DROP TABLE IF EXISTS `playFieldRecord`;
CREATE TABLE `playFieldRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '比赛场记录uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '比赛场记录玩家uid',
  `type` int(11) NULL DEFAULT 0 COMMENT '比赛场类型id',
  `rank` int(11) NULL DEFAULT 0 COMMENT '比赛排名',
  `reward` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '比赛奖励',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '比赛时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for playFieldRewardRecord
-- ----------------------------
DROP TABLE IF EXISTS `playFieldRewardRecord`;
CREATE TABLE `playFieldRewardRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '比赛奖励uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '比赛获奖玩家uid',
  `type` int(11) NULL DEFAULT 0 COMMENT '比赛类型id',
  `reward` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '比赛奖励',
  `rank` int(11) NULL DEFAULT 0 COMMENT '比赛排名',
  `state` int(11) NULL DEFAULT 0 COMMENT '比赛奖励领取状态, 0: 未领取, 1: 已领取',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '比赛时间',
  `giveTime` bigint(20) NULL DEFAULT 0 COMMENT '领取时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT 'uid',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '昵称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像url',
  `sex` tinyint(4) NULL DEFAULT 0 COMMENT '性别,1:男, 0:女',
  `zone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '区域',
  `roomId` int(11) NULL DEFAULT -1 COMMENT '房间Id',
  `arenaUid` bigint(20) NULL DEFAULT -1 COMMENT '竞技场Uid',
  `playFieldUid` bigint(20) NULL DEFAULT -1 COMMENT '比赛场Uid',
  `createTimestamp` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  `lastLoginTime` bigint(20) NULL DEFAULT 0 COMMENT '最后登陆时间',
  `lastLogoutTime` bigint(20) NULL DEFAULT 0 COMMENT '最后登出时间',
  `lastLoginIp` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '最后登录ip',
  `lat` double NULL DEFAULT 0 COMMENT '纬度',
  `lng` double NULL DEFAULT 0 COMMENT '经度',
  `money` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '货币',
  `alias` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '别名{uid:name,...,uid:name}',
  `tags` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '标签',
  `msgTopV2` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息顶置',
  `msgMute` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息静音{uid:name,...,uid:name}',
  `groups` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '[guid,guid,...,guid]',
  `friend` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '[uid,uid,...,uid]',
  `recommend` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '推荐信息',
  `visitCard` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '名片信息',
  `born` bigint(20) NULL DEFAULT 0 COMMENT '出生年月',
  `signature` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '个性签名',
  `emotion` tinyint(4) NULL DEFAULT 0 COMMENT '情感',
  `showImage` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '展示图片',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '封面图片',
  `privilege` int(11) NULL DEFAULT 0 COMMENT '权限等级',
  `ownerGroupCnt` int(11) NULL DEFAULT 0 COMMENT '群数量',
  `ownerArenaCnt` int(11) NULL DEFAULT 0 COMMENT '竞技场数量',
  `hundredArenaReb` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '百人场下注总额',
  `curHundredArena` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '当前百人场',
  `recharge` bigint(20) NULL DEFAULT 0 COMMENT '总充值',
  `defaultIcon` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '默认头像',
  `wechat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '微信',
  `bizChannel` int(11) NULL DEFAULT -1 COMMENT '业务渠道',
  `isEmpower` bit(1) NULL DEFAULT b'0' COMMENT '是否授权',
  `ownerLeagueCnt` int(11) NULL DEFAULT 0 COMMENT '拥有联盟数量',
  `isDoneGame` int(11) NULL DEFAULT 0 COMMENT '是否完成过一轮游戏',
  `ownerPavilionCnt` int(11) NULL DEFAULT 0 COMMENT '雀友馆数量',
  `bankCard` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `bankCardHolder` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡持卡人',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for playerMoneyConsumeRecord
-- ----------------------------
DROP TABLE IF EXISTS `playerMoneyConsumeRecord`;
CREATE TABLE `playerMoneyConsumeRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT 'uid',
  `playerUid` bigint(20) NULL DEFAULT -1 COMMENT '玩家ID',
  `value1` bigint(20) NULL DEFAULT 0 COMMENT '大厅消耗=sum(大厅房卡消耗+大厅房卡消耗返还)',
  `value2` bigint(20) NULL DEFAULT 0 COMMENT '亲友圈消耗（亲友圈房卡消耗+亲友圈房卡消耗返还）',
  `value3` bigint(20) NULL DEFAULT 0 COMMENT '联盟消耗（大联盟房卡消耗+大联盟房卡消耗返还）',
  `monthValue1` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '大厅月统计消耗，格式：[\'startTime\':开始记录时间,\'time\':最后更新时间,[消耗数量,消耗数量,...]]',
  `monthValue2` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '亲友圈月统计消耗，格式：[\'startTime\':开始记录时间,\'time\':最后更新时间,[消耗数量,消耗数量,...]]',
  `monthValue3` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '联盟月统计消耗，格式：[\'startTime\':开始记录时间,\'time\':最后更新时间,[消耗数量,消耗数量,...]]',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '玩家房卡消耗数量统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for questGetRewardRecord
-- ----------------------------
DROP TABLE IF EXISTS `questGetRewardRecord`;
CREATE TABLE `questGetRewardRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '竞技值修改记录uid',
  `leagueUid` bigint(20) NULL DEFAULT NULL COMMENT '联盟id',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `arenaUid` bigint(20) NULL DEFAULT 0 COMMENT '竞技场uid',
  `arenaValue` int(11) NULL DEFAULT 0 COMMENT '修改竞技值',
  `operatorTime` bigint(20) NULL DEFAULT 0 COMMENT '操作时间',
  `gameType` int(11) NULL DEFAULT NULL COMMENT '游戏类型',
  `subType` int(11) NULL DEFAULT NULL COMMENT '小类型',
  `bureau` int(11) NULL DEFAULT NULL COMMENT '对局数',
  `startTime` bigint(20) NULL DEFAULT NULL COMMENT '开始时间',
  `endTime` bigint(20) NULL DEFAULT NULL COMMENT '结束时间',
  `period` int(11) NULL DEFAULT NULL COMMENT '活动周期',
  `param` int(11) NULL DEFAULT 0 COMMENT '活动条件',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `search`(`groupUid`, `playerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rankData
-- ----------------------------
DROP TABLE IF EXISTS `rankData`;
CREATE TABLE `rankData`  (
  `uid` bigint(20) NOT NULL,
  `fromUid` bigint(20) NULL DEFAULT NULL,
  `rankType` tinyint(255) NULL DEFAULT NULL,
  `updateTime` bigint(20) NULL DEFAULT NULL,
  `todayRanks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `yesterdayRanks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `anteayerRanks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for recommend
-- ----------------------------
DROP TABLE IF EXISTS `recommend`;
CREATE TABLE `recommend`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '推荐记录uid',
  `recommendPlayerUid` bigint(20) NULL DEFAULT -1 COMMENT '推荐用户uid',
  `recommendedPlayerUid` bigint(20) NULL DEFAULT -1 COMMENT '被推荐用户uid',
  `groupUid` bigint(20) NOT NULL DEFAULT -1 COMMENT '关联的群UID',
  `state` int(11) NOT NULL DEFAULT 1 COMMENT '状态，1-正常，2-已清除',
  `diamond` int(11) NOT NULL DEFAULT 0 COMMENT '房卡',
  `diamondSum` int(11) NULL DEFAULT 0 COMMENT '房卡总数',
  `bindingTime` bigint(20) NULL DEFAULT 0 COMMENT '绑定时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '房间uid',
  `roomId` int(11) NULL DEFAULT 100000 COMMENT '房间Id',
  `ownerPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '房间拥有者uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `createTime` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  `endTime` bigint(20) NULL DEFAULT -1 COMMENT '结束时间',
  `state` int(11) NULL DEFAULT 0 COMMENT '0:初始化, 1:进行中, 2:完成',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `curBureau` int(11) NULL DEFAULT 0 COMMENT '当前局数',
  `cost` int(11) NULL DEFAULT 0 COMMENT '消耗',
  `rule` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '房间玩法',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for roomScore
-- ----------------------------
DROP TABLE IF EXISTS `roomScore`;
CREATE TABLE `roomScore`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '房间战绩uid',
  `roomUid` bigint(20) NULL DEFAULT 0 COMMENT '房间uid',
  `roomId` int(11) NULL DEFAULT 0 COMMENT '房间id',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid1` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家1',
  `playerUid2` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家2',
  `playerUid3` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家3',
  `playerUid4` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家4',
  `playerUid5` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家5',
  `playerUid6` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家6',
  `playerUid7` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家7',
  `playerUid8` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家8',
  `playerUid9` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家9',
  `playerUid10` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家10',
  `playerUid11` bigint(20) NULL DEFAULT 0 COMMENT '参与玩家11',
  `beginTime` bigint(20) NULL DEFAULT 0 COMMENT '开始时间',
  `endTime` bigint(20) NULL DEFAULT 0 COMMENT '结束时间',
  `totalScore` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '总战绩',
  `record` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '战绩记录',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceChargeAdmin
-- ----------------------------
DROP TABLE IF EXISTS `serviceChargeAdmin`;
CREATE TABLE `serviceChargeAdmin`  (
  `uid` bigint(20) NOT NULL,
  `groupUid` bigint(20) NOT NULL COMMENT '群UID',
  `playerUid` bigint(20) NOT NULL COMMENT '提供竞技值的玩家UID',
  `chargeAt` bigint(20) NOT NULL COMMENT 'UNIX时间戳',
  `arenaValue` int(11) NOT NULL COMMENT '竞技值之服务费',
  `arenaValueByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟竞技值收益',
  `ownerType` tinyint(4) NULL DEFAULT 1 COMMENT '服务费拥有着',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '竞技值服务费之管理费' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceChargeArenaDaily
-- ----------------------------
DROP TABLE IF EXISTS `serviceChargeArenaDaily`;
CREATE TABLE `serviceChargeArenaDaily`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '服务费每日记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `gameUid` bigint(20) NULL DEFAULT 0 COMMENT '游戏uid',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '每日零点时间戳',
  `bureau` int(11) NULL DEFAULT 0 COMMENT '局数',
  `cost` int(11) NULL DEFAULT 0 COMMENT '消耗',
  `roomType` tinyint(4) NULL DEFAULT 1 COMMENT '房间类型',
  `bureauByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟局数',
  `costByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟消耗',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchTimeAndGroupUidWithRoomType`(`time`, `groupUid`, `roomType`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceChargeDaily
-- ----------------------------
DROP TABLE IF EXISTS `serviceChargeDaily`;
CREATE TABLE `serviceChargeDaily`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '服务费每日记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '每日零点时间戳',
  `arenaCost` int(11) NULL DEFAULT 0 COMMENT '竞技场消耗',
  `boxCost` int(11) NULL DEFAULT 0 COMMENT '包厢消耗',
  `mineRedCost` int(11) NULL DEFAULT 0 COMMENT '红包埋雷消耗',
  `arenaCostByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟竞技场消耗',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceChargeDetail
-- ----------------------------
DROP TABLE IF EXISTS `serviceChargeDetail`;
CREATE TABLE `serviceChargeDetail`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '服务费详细记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `gameUid` bigint(20) NULL DEFAULT 0 COMMENT '游戏uid',
  `gameType` int(11) NULL DEFAULT 0 COMMENT '游戏类型',
  `gameSubType` int(11) NULL DEFAULT 0 COMMENT '游戏子类型',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `bureau` int(11) NULL DEFAULT 0 COMMENT '局数',
  `cost` int(11) NULL DEFAULT 0 COMMENT '消耗',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '每日零点时间戳',
  `roomType` tinyint(4) NULL DEFAULT 1 COMMENT '房间类型',
  `bureauByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟局数',
  `costByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟消耗',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchTimeAnGameUidWithRoomType`(`time`, `gameUid`, `roomType`) USING BTREE,
  INDEX `searchTimeAndGroupUidAndPlayerUidWithRoomType`(`time`, `groupUid`, `playerUid`, `roomType`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceChargePlayerDaily
-- ----------------------------
DROP TABLE IF EXISTS `serviceChargePlayerDaily`;
CREATE TABLE `serviceChargePlayerDaily`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '服务费每日记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '添加玩家uid',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '每日零点时间戳',
  `arenaCost` int(11) NULL DEFAULT 0 COMMENT '竞技场消耗',
  `boxCost` int(11) NULL DEFAULT 0 COMMENT '包厢消耗',
  `mineRedCost` int(11) NULL DEFAULT 0 COMMENT '红包埋雷消耗',
  `arenaCostByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟竞技场消耗',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchTimeAndGroupUidWithRoomType`(`time`, `groupUid`) USING BTREE,
  INDEX `searchGroupUidAndPlayerUidWithRoomType`(`groupUid`, `playerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceChargePlayerDetail
-- ----------------------------
DROP TABLE IF EXISTS `serviceChargePlayerDetail`;
CREATE TABLE `serviceChargePlayerDetail`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '服务费每日详细记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '添加玩家uid',
  `costPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '消耗玩家uid',
  `time` bigint(20) NULL DEFAULT 0 COMMENT '每日零点时间戳',
  `arenaBureau` int(11) NULL DEFAULT 0 COMMENT '竞技场局数',
  `arenaCost` int(11) NULL DEFAULT 0 COMMENT '竞技场消耗',
  `type` tinyint(4) NULL DEFAULT -1 COMMENT '来源类型, 1: 成员, 2: 上线, 3: 一条线, 4: 群主',
  `boxCost` int(11) NULL DEFAULT 0 COMMENT '包厢消耗',
  `mineRedCost` int(11) NULL DEFAULT 0 COMMENT '红包埋雷消耗',
  `boxBureau` int(11) NULL DEFAULT 0 COMMENT '包厢局数',
  `mineRedBureau` int(11) NULL DEFAULT 0 COMMENT '红包埋雷局数',
  `arenaBureauByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟竞技场局数',
  `arenaCostByLeague` int(11) NULL DEFAULT 0 COMMENT '联盟竞技场消耗',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchTimeAndGroupUidAndPlayerUidWitTypeAndhRoomType`(`time`, `groupUid`, `playerUid`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceChargeRecord
-- ----------------------------
DROP TABLE IF EXISTS `serviceChargeRecord`;
CREATE TABLE `serviceChargeRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '服务费修改记录uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `operatorPlayerUid` bigint(20) NULL DEFAULT 0 COMMENT '操作玩家uid',
  `serviceChargeValue` int(11) NULL DEFAULT 0 COMMENT '修改服务费',
  `operatorTime` bigint(20) NULL DEFAULT 0 COMMENT '操作时间',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '0: 正常, 1: 废弃',
  `serviceChargeValueByLeague` int(11) NULL DEFAULT 0 COMMENT '修改联盟服务费',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `search`(`groupUid`, `playerUid`, `state`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for texasInsuranceRecord
-- ----------------------------
DROP TABLE IF EXISTS `texasInsuranceRecord`;
CREATE TABLE `texasInsuranceRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT 'uid',
  `groupUid` bigint(20) NULL DEFAULT 0 COMMENT '群uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `leagueUid` bigint(20) NULL DEFAULT 0 COMMENT '联盟uid',
  `arenaUid` bigint(20) NULL DEFAULT NULL COMMENT '竞技场uid',
  `operatorTime` bigint(20) NULL DEFAULT 0 COMMENT '操作时间',
  `value` int(11) NULL DEFAULT 0 COMMENT '保险值',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `search`(`groupUid`, `playerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for todayStatistics
-- ----------------------------
DROP TABLE IF EXISTS `todayStatistics`;
CREATE TABLE `todayStatistics`  (
  `uid` bigint(20) NOT NULL,
  `playerUid` bigint(20) NULL DEFAULT NULL COMMENT '玩家uid',
  `fromUid` bigint(20) NULL DEFAULT NULL COMMENT '数值来源对象uid，如联盟数据填写leagueUid',
  `statisticsType` tinyint(255) NULL DEFAULT NULL COMMENT '统计类型',
  `value` int(11) NULL DEFAULT NULL COMMENT '数值',
  `updateTime` bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排行榜相关数据存储' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trendsIssue
-- ----------------------------
DROP TABLE IF EXISTS `trendsIssue`;
CREATE TABLE `trendsIssue`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '发布uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '发布玩家uid',
  `trendsUid` bigint(20) NULL DEFAULT 0 COMMENT '发布动态uid',
  `state` tinyint(4) NULL DEFAULT 0 COMMENT '发布状态, 0: 正常, 1: 添加, 2: 添加完成, 3: 删除, 4: 删除完成',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `playerUid`(`playerUid`, `state`) USING BTREE,
  INDEX `trendsUid`(`trendsUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trendsMeta
-- ----------------------------
DROP TABLE IF EXISTS `trendsMeta`;
CREATE TABLE `trendsMeta`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '动态uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '发表玩家uid',
  `content` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '发表内容',
  `images` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '发表图片/视频等url',
  `postTime` bigint(20) NULL DEFAULT 0 COMMENT '发布时间',
  `lookType` tinyint(4) NULL DEFAULT 0 COMMENT '公开类型, 0: 公开, 1: 只有好友可看, 2: 只有附近可看, 3: 指定好友可看, 4: 指定好友不可看',
  `limitPlayerUids` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '限制玩家uids',
  `showLocation` tinyint(4) NULL DEFAULT 1 COMMENT '是否公开位置, 1: 显示, 2: 不显示',
  `lat` double NULL DEFAULT 0 COMMENT 'gps纬度',
  `lng` double NULL DEFAULT 0 COMMENT 'gps经度',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trendsTimeline
-- ----------------------------
DROP TABLE IF EXISTS `trendsTimeline`;
CREATE TABLE `trendsTimeline`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '动态时间轴uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '玩家uid',
  `trendsUid` bigint(20) NULL DEFAULT 0 COMMENT '动态uid',
  `isOwner` tinyint(4) NULL DEFAULT 0 COMMENT '是否自己的',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `trendsUid`(`trendsUid`) USING BTREE,
  INDEX `playerAndTrendsUid`(`playerUid`, `trendsUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for walletMineRedPacketReceivedRecord
-- ----------------------------
DROP TABLE IF EXISTS `walletMineRedPacketReceivedRecord`;
CREATE TABLE `walletMineRedPacketReceivedRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '领取埋雷红包记录Uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '领取者Uid',
  `playerName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '领取者昵称',
  `playerIcon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '领取者头像',
  `redPacketUid` bigint(20) NULL DEFAULT 0 COMMENT '领取的红包Uid',
  `amount` int(20) NULL DEFAULT 0 COMMENT '领取的红包金额 单位:分',
  `feedback` int(20) NULL DEFAULT 0 COMMENT '是否中雷 0-未中；1-中雷',
  `specialAward` int(20) NULL DEFAULT 0 COMMENT '是否特殊奖励：0-不是；1-豹子；2-顺子；3-0.01奖励',
  `createdAt` bigint(20) NULL DEFAULT 0 COMMENT '领取的时间戳',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByRedPacketUid`(`redPacketUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for walletMineRedPacketSendRecord
-- ----------------------------
DROP TABLE IF EXISTS `walletMineRedPacketSendRecord`;
CREATE TABLE `walletMineRedPacketSendRecord`  (
  `uid` bigint(20) NOT NULL COMMENT '红包Uid',
  `senderUid` bigint(20) UNSIGNED NOT NULL COMMENT '发送者uid',
  `groupUid` bigint(20) UNSIGNED NOT NULL COMMENT '红包所在群uid',
  `amount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包总金额 单位:分',
  `count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包数量',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '红包描述',
  `mine` int(10) NOT NULL COMMENT '埋雷数字',
  `mineMultiple` int(10) NOT NULL DEFAULT 0 COMMENT '埋雷倍数*10',
  `specialAwards` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '特殊奖励',
  `createdAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包发送时间戳',
  `expiredAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包过期时间戳',
  `clearAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包被领完时间戳',
  `receivedCount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '被领取的红包数量',
  `receivedAmount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '被领取的总金额 单位:分',
  `status` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包状态: 0-领取中;1-已领完;2-过期;3-被退还',
  `returnAmount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '被退还的总金额 单位:分',
  `receivers` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收者列表',
  `specialAward` int(11) NULL DEFAULT 0 COMMENT '特殊奖励',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for walletRecord
-- ----------------------------
DROP TABLE IF EXISTS `walletRecord`;
CREATE TABLE `walletRecord`  (
  `uid` bigint(20) NOT NULL,
  `playerUid` bigint(20) NOT NULL,
  `beginAmount` int(20) NOT NULL DEFAULT 0 COMMENT '操作前的金额',
  `amount` int(20) NOT NULL DEFAULT 0 COMMENT '操作金额',
  `inMoney` int(20) NOT NULL DEFAULT 0 COMMENT '收入金额',
  `outMoney` int(20) NOT NULL DEFAULT 0 COMMENT '支出金额',
  `month` bigint(20) NOT NULL DEFAULT 0 COMMENT '本月开始时间戳',
  `action` tinyint(4) NOT NULL DEFAULT 0 COMMENT '操作类型：操作：0-发红包；1-领取红包；2-红包过期退还；3-钱包充值；4-钱包提现；5-发起转账；6-领取转账；7-拒绝转账；8-转账过期退还；',
  `createdAt` bigint(20) NOT NULL DEFAULT 0 COMMENT '操作时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `playerUid_index`(`playerUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for walletRedPacket
-- ----------------------------
DROP TABLE IF EXISTS `walletRedPacket`;
CREATE TABLE `walletRedPacket`  (
  `uid` bigint(20) NOT NULL COMMENT '红包Uid',
  `senderUid` bigint(20) UNSIGNED NOT NULL COMMENT '发送者uid',
  `groupUid` bigint(20) UNSIGNED NOT NULL COMMENT '红包所在群uid',
  `amount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包总金额 单位:分',
  `count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包数量',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '红包描述',
  `type` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包类型：0-普通红包;1-拼手气红包',
  `createdAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包发送时间戳',
  `expiredAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包过期时间戳',
  `clearAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包被领完时间戳',
  `receivers` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `receivedCount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '被领取的红包数量',
  `receivedAmount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '被领取的总金额 单位:分',
  `status` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包状态: 0-领取中;1-已领完;2-过期;3-被退还',
  `returnAmount` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '被退还的总金额 单位:分',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for walletRedPacketReceivedRecord
-- ----------------------------
DROP TABLE IF EXISTS `walletRedPacketReceivedRecord`;
CREATE TABLE `walletRedPacketReceivedRecord`  (
  `uid` bigint(20) NOT NULL DEFAULT 0 COMMENT '领取红包记录Uid',
  `playerUid` bigint(20) NULL DEFAULT 0 COMMENT '领取者Uid',
  `playerName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '领取者昵称',
  `playerIcon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '领取者头像',
  `redPacketUid` bigint(20) NULL DEFAULT 0 COMMENT '领取的红包Uid',
  `amount` int(11) NULL DEFAULT 0 COMMENT '领取的红包金额 单位:分',
  `createdAt` bigint(20) NULL DEFAULT 0 COMMENT '领取的时间戳',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `searchByRedPacketUid`(`redPacketUid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for walletTransfer
-- ----------------------------
DROP TABLE IF EXISTS `walletTransfer`;
CREATE TABLE `walletTransfer`  (
  `uid` bigint(20) NOT NULL COMMENT '转账记录ID',
  `transferUid` bigint(20) NOT NULL COMMENT '发起转账人Uid',
  `targetUid` bigint(20) NOT NULL COMMENT '接收转账人Uid',
  `amount` bigint(20) NOT NULL COMMENT '转账金额(单位:分)',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '转账描述',
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '转账状态: 0-转账中;1-已接收;2-被退回',
  `createdAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '转账时间',
  `expiredAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '到期时间',
  `receivedAt` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '领取时间',
  `transferType` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '转账类型：0-转账；1-个人红包',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
