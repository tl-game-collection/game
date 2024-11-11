create table if not exists `account` (
  `uid` bigint default 0,
  `passwd` varchar(32) default "",
  `createTime` bigint default 0 comment "创建时间",
  `mac` varchar(64) default "",
  `phone` varchar(11) default "",
  `phoneVer` varchar(255) default "",
  `phoneOsVer` varchar(255) default "",
  `name` varchar(255) default "" comment "姓名",
  `icon` varchar(1024) default "" comment "头像",
  `sex` tinyint default 1 comment "性别",
  `city` varchar(255) default "" comment "城市",
  `identityCard` varchar(20) default "" comment "身份证",
  `otherPlatformToken` varchar(255) default "" comment "其他平台身份表示",
  `type` tinyint default 0 comment "注册账号类型,0: 游客登陆, 1: 手机号登陆, 2: 快速登陆, 3: 微信登陆",
  `payPassword` varchar(11) default "" comment "支付密码",
  `state` tinyint default 0 comment "状态, 0: 正常, 1: 删除, 2: 封号",
  index phone(phone),
  index otherPlatformToken(otherPlatformToken),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `player` (
  `uid` bigint default 0 comment "uid",
  `name` varchar(255) default "" comment "昵称",
  `icon` varchar(255) default "" comment "头像url",
  `sex` tinyint default 0 comment "性别,1:男, 0:女",
  `zone` varchar(255) default "" comment "区域",
  `roomId` int(11) default -1 comment "房间Id",
  `arenaUid` bigint default -1 comment "竞技场Uid",
  `playFieldUid` bigint default -1 comment "比赛场Uid",
  `createTimestamp` bigint default 0 comment "创建时间",
  `lastLoginTime` bigint default 0 comment "最后登陆时间",
  `lastLogoutTime` bigint default 0 comment "最后登出时间",
  `lastLoginIp` varchar(32) default 0 comment "最后登录ip",
  `lat` double default 0 comment "纬度",
  `lng` double default 0 comment "经度",
  `money` varchar(1024) default "" comment "货币",
  `alias` longtext default "" comment "别名{uid:name,...,uid:name}",
  `tags` longtext default "" comment "标签",
  `msgTopV2` longtext default "" comment "消息顶置{uid:name,...,uid:name}",
  `msgMute` longtext default "" comment "消息静音{uid:name,...,uid:name}",
  `groups` longtext default "" comment "[guid,guid,...,guid]",
  `friend` longtext default "" comment "[uid,uid,...,uid]",
  `recommend` varchar(1024) default "" comment "推荐信息",
  `visitCard` varchar(2048) default "" comment "名片信息",
  `born` bigint default 0 comment "出生年月",
  `signature` varchar(1024) default "" comment "个性签名",
  `emotion` tinyint default 0 comment "情感",
  `showImage` varchar(2048) default "" comment "展示图片",
  `cover` varchar(255) default "" comment "封面图片",
  `privilege` int default 0 comment "权限等级",
  `ownerGroupCnt` int default 0 comment "拥有群数量",
  `ownerLeagueCnt` int default 0 comment "拥有联盟数量",
  `ownerArenaCnt` int default 0 comment "拥有竞技场数量",
  `hundredArenaReb` longtext default "" comment "百人场下注总额",
  `curHundredArena` longtext default "" comment "当前百人竞技场",
  `recharge` bigint default 0 comment "总充值",
  `defaultIcon` char(2) default 0 comment "默认头像",
  `wechat` varchar(255) default '' comment '微信',
  `ownerPavilionCnt` int default 0 comment "拥有雀友馆数量",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `group` (
  `uid` bigint default 0 comment "群uid",
  `name` varchar(255) default "" comment "群名",
  `desc` varchar(1024) default "" comment "群介绍",
  `icon` varchar(355) default "" comment "群icon",
  `boxLike` tinyint default 0 comment "点赞开关",
  `owner` bigint default 0 comment "群创建者",
  `createTime` bigint default 0 comment "创建时间",
  `maxMemberCnt` int default 500 comment "成员上限",
  `members` longtext default "" comment "成员",
  `applys` longtext default "" comment "申请者",
  `leaves` longtext default "" comment "离开者",
  `state` int(11) default 0 comment "状态: 0:正常, 1: 拒绝, 2:删除",
  `serviceCharge` longtext default "" comment "服务费记录",
  `box` longtext default "" comment "包厢信息",
  `arena` longtext default "" comment "竞技场信息",
  `totalCost` varchar(1024) default "" comment "累计消耗",
  `totalServiceCharge` bigint default 0 comment "总服务费",
  `arenaScoreRank` longtext default "" comment "竞技场分数排行",
  `boxScoreRank` longtext default "" comment "包厢分数排行",
  `mineRedScoreRank` longtext default "" comment "红包埋雷分数排行",
  `quickAddSwitch` tinyint default 1 comment "快速上分开关",
  `flrSwitch` tinyint default 0 comment "防拉人开关",
  `totalIncArenaValue` bigint default 0 comment "总添加竞技值",
  `totalDecArenaValue` bigint default 0 comment "总减少竞技值",
  `totalIncArenaValueByWallet` bigint default 0 comment "总添加竞技值通过钱包",
  `totalIncArenaValueByQuick` bigint default 0 comment "总添加竞技值通过快速充值",
  `totalDecServiceValue` bigint default 0 comment "总减少服务费",
  `mineRedPackConf` longtext default "" comment "红包埋雷配置",
  `floor` longtext default "" comment "楼层信息",
  `announcement` varchar(1024) default '' comment'群公告',
  `announcementExpireAt` bigint default 0 comment '群公告过期时间戳，毫秒',
  `quickDecSwitch` tinyint default 0 comment '快速下分开关',
  `shareSwitch` tinyint default 0 comment '分享开关',
  `shareAward` tinyint default 0 comment '分享奖励值',
  `serviceChargeDivideAdmin` int default 0 comment '服务费之管理费比例',
  `serviceChargeDivideAdminByLeague` int default 0 comment "群联盟管理费",
  `newbieChatSwitch` tinyint default 0 comment '新人聊天受限开关',
  `groupSwitch` bigint default 0 comment "群开关",
  `autoUpDeputyConf` longtext default "" comment '自动升副帮主设置',
  `quests` longtext  default "" comment '小组任务',
  `leagueUid` bigint default -1 comment '联盟uid',
  `gameDesc` longtext default "" comment '游戏描述',
  `pavilionUid` bigint default -1 comment '雀友馆uid',
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `room` (
  `uid` bigint default 0 comment "房间uid",
  `roomId` int(11) default 100000 comment "房间Id",
  `ownerPlayerUid` bigint default 0 comment "房间拥有者uid",
  `groupUid` bigint default 0 comment "群uid",
  `createTime` bigint default 0 comment "创建时间",
  `endTime` bigint default -1 comment "结束时间",
  `state` int(11) default 0 comment "0:初始化, 1:进行中, 2:完成",
  `gameType` int(11) default 0 comment "游戏类型",
  `gameSubType` int(11) default 0 comment "游戏子类型",
  `curBureau` int(11) default 0 comment "当前局数",
  `cost` int(11) default 0 comment "消耗",
  `rule` varchar(512) default "" comment "房间玩法",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `roomScore` (
  `uid` bigint default 0 comment "房间战绩uid",
  `roomUid` bigint default 0 comment "房间uid",
  `roomId` int default 0 comment "房间id",
  `gameType` int default 0 comment "游戏类型",
  `gameSubType` int default 0 comment "游戏子类型",
  `groupUid` bigint default 0 comment "群uid",
  `playerUid1` bigint default 0 comment "参与玩家1",
  `playerUid2` bigint default 0 comment "参与玩家2",
  `playerUid3` bigint default 0 comment "参与玩家3",
  `playerUid4` bigint default 0 comment "参与玩家4",
  `playerUid5` bigint default 0 comment "参与玩家5",
  `playerUid6` bigint default 0 comment "参与玩家6",
  `playerUid7` bigint default 0 comment "参与玩家7",
  `playerUid8` bigint default 0 comment "参与玩家8",
  `playerUid9` bigint default 0 comment "参与玩家9",
  `playerUid10` bigint default 0 comment "参与玩家10",
  `playerUid11` bigint default 0 comment "参与玩家11",
  `beginTime` bigint default 0 comment "开始时间",
  `endTime` bigint default 0 comment "结束时间",
  `totalScore` varchar(2048) default "" comment "总战绩",
  `record` longtext default "" comment "战绩记录",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `arena` (
  `uid` bigint default 0 comment "竞技场uid",
  `groupUid` bigint default 0 comment "群uid",
  `createTime` bigint default 0 comment "创建时间",
  `updateTime` bigint default 0 comment "更新时间",
  `endTime` bigint default -1 comment "结束时间",
  `gameType` int(11) default 0 comment "游戏类型",
  `gameSubType` int(11) default 0 comment "游戏子类型",
  `timeLimit` int(11) default 1 comment "时间限制",
  `minArena` int(11) default 0 comment "加入最小竞技值",
  `leaveArenaValue` int(11) default 0 comment "离开竞技值",
  `costModel` int(11) default 0 comment "付费模式",
  `costModelValue` int(11) default 0 comment "付费模式值",
  `rule` varchar(512) default "" comment "规则",
  `extra` varchar(2048) default "" comment "额外",
  `state` tinyint default 0 comment "状态, 0: 正常, 1: 关闭",
  `totalCost` int(11) default 0 comment "总消耗",
  `scoreRank` longtext default "" comment "竞技场分数排行",
  `floorUid` bigint default -1 comment "楼层uid",
  `ownerType` tinyint default 1 comment "竞技场拥有着",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `arenaScore` (
  `uid` bigint default 0 comment "竞技场战绩uid",
  `arenaUid` bigint default 0 comment "竞技场uid",
  `arenaGameType` int default 0 comment "竞技场游戏类型",
  `arenaGameSubType` int default 0 comment "竞技场游戏子类型",
  `groupUid` bigint default 0 comment "群uid",
  `playerUid` bigint default 0 comment "玩家uid",
  `beginTime` bigint default 0 comment "开始时间",
  `endTime` bigint default 0 comment "结束时间",
  `bureau` int default 0 comment "局数",
  `score` int default 0 comment "本轮积分",
  `state` int default 0 comment "状态,0:正常, 1:已经清理, 2: 正在使用, 3; 已经废弃",
  `recordUid` longtext default "" comment "战绩记录",
  `clearPlayerUid` bigint default 0 comment "清理操作玩家uid",
  `clearTime` bigint default 0 comment "清理操作时间",
  `allCnt` varchar(2048) default "" comment "大结算记录",
  `ownerType` tinyint default 1 comment "战绩拥有着",
  index searchPlayerUid(`playerUid`, `ownerType`, `state`),
  index searchGroupUidAndPlayerUid(`groupUid`, `ownerType`, `playerUid`, `state`),
  index searchArenaUidAndPlayerUid(`arenaUid`, `playerUid`, `state`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `arenaScoreDetail` (
  `uid` bigint default 0 comment "记录uid",
  `time` bigint default 0 comment "时间",
  `score` varchar(4096) default "" comment "分数",
  primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `mail` (
  `uid` bigint default 0 comment "邮件uid",
  `senderPlayerUid` bigint default 0 comment "发送者playerUid, -1: 为系统",
  `receivePlayerUid` bigint default 0 comment "接收者playerUid",
  `title` varchar(255) default "" comment "邮件标题",
  `content` varchar(2048) default "" comment "邮件内容",
  `item` varchar(2048) default "" comment "道具信息",
  `sendTime` bigint default 0 comment "发送时间",
  `state` tinyint default 0 comment "状态: 0: 正常, 1: 已读, 2: 删除",
  `itemState` tinyint default 0 comment "道具状态, 0: 不可领取, 1: 可领取, 2: 已领取",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `boxRoomScore` (
  `uid` bigint default 0 comment "包厢房间战绩uid",
  `roomUid` bigint default 0 comment "房间uid",
  `roomId` int default 0 comment "房间id",
  `gameType` int default 0 comment "游戏类型",
  `gameSubType` int default 0 comment "游戏子类型",
  `groupUid` bigint default 0 comment "群uid",
  `boxUid` bigint default 0 comment "包厢uid",
  `playerUid1` bigint default 0 comment "参与玩家1",
  `playerUid2` bigint default 0 comment "参与玩家2",
  `playerUid3` bigint default 0 comment "参与玩家3",
  `playerUid4` bigint default 0 comment "参与玩家4",
  `playerUid5` bigint default 0 comment "参与玩家5",
  `playerUid6` bigint default 0 comment "参与玩家6",
  `playerUid7` bigint default 0 comment "参与玩家7",
  `playerUid8` bigint default 0 comment "参与玩家8",
  `playerUid9` bigint default 0 comment "参与玩家9",
  `playerUid10` bigint default 0 comment "参与玩家10",
  `playerUid11` bigint default 0 comment "参与玩家11",
  `beginTime` bigint default 0 comment "开始时间",
  `endTime` bigint default 0 comment "结束时间",
  `totalScore` varchar(2048) default "" comment "总战绩",
  `record` longtext default "" comment "战绩记录",
  `gain` int default 0 comment "打赏次数",
  `gainList` varchar(4096) default "" comment "打赏列表",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `recommend` (
  `uid` bigint default 0 comment "推荐记录uid",
  `recommendPlayerUid` bigint default -1 comment "推荐用户uid",
  `recommendedPlayerUid` bigint default -1 comment "被推荐用户uid",
  `groupUid` bigint default -1 comment "关联的群UID",
  `state` tinyint default 1 comment "状态，1-正常，2-已清除",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `playFieldRecord` (
  `uid` bigint default 0 comment "比赛场记录uid",
  `playerUid` bigint default 0 comment "比赛场记录玩家uid",
  `type` int default 0 comment "比赛场类型id",
  `rank` int default 0 comment "比赛排名",
  `reward` varchar(1024) default "" comment "比赛奖励",
  `time` bigint default 0 comment "比赛时间",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `playFieldRewardRecord` (
  `uid` bigint default 0 comment "比赛奖励uid",
  `playerUid` bigint default 0 comment "比赛获奖玩家uid",
  `type` int default 0 comment "比赛类型id",
  `reward` varchar(1024) default "" comment "比赛奖励",
  `rank` int default 0 comment "比赛排名",
  `state` int default 0 comment "比赛奖励领取状态, 0: 未领取, 1: 已领取",
  `time` bigint default 0 comment "比赛时间",
  `giveTime` bigint default 0 comment "领取时间",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `nearbyNote` (
  `uid` bigint default 0 comment "附近留言uid",
  `fromPlayerUid` bigint default 0 comment "留言玩家uid",
  `toPlayerUid` bigint default 0 comment "被留言玩家uid",
  `note` varchar(1024) default "" comment "留言内容",
  `state` tinyint default 0 comment "状态, 0: 新增, 1: 查看过了, 2: 删除",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `mailBox` (
  `uid` bigint default 0 comment "邮箱uid",
  `messageUid` bigint default 0 comment "邮件uid",
  `messageUidByPlayer` bigint default 0 comment "相对于玩家邮件uid",
  `toPlayerUid` bigint default 0 comment "消息收件人uid",
  `tagPlayerUid` bigint default 0 comment "收件人uid",
  `fromPlayerUid` bigint default -1 comment "发件人uid",
  `fromGroupUid` bigint default -1 comment "来自群uid",
  `messageType` tinyint default 0 comment "消息类型",
  `contentType` tinyint default 0 comment "内容类型",
  `message` varchar(4096) comment "消息内容",
  `sayTime` bigint default 0 comment "发送时间",
  `param` varchar(2048) default "" comment "参数",
  `state` tinyint default 0 comment "状态",
  index search(`messageUid`),
  index searchByPlayer(`toPlayerUid`, `messageUidByPlayer`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `mailBoxUid` (
  `uid` bigint default 0 comment "玩家uid",
  `lastMsgUid` bigint default 0 comment "最后消息uid",
  `lastMsgUidByClient` bigint default 0 comment "客户端最后一条消息uid",
  `recallMsgUid` longtext default "" comment "撤回消息uid集合",
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `trendsMeta` (
    `uid` bigint default 0 comment "动态uid",
    `playerUid` bigint default 0 comment "发表玩家uid",
    `content` varchar(4096) default "" comment "发表内容",
    `images` varchar(4096) default "" comment "发表图片/视频等url",
    `postTime` bigint default 0 comment "发布时间",
    `lookType` tinyint default 0 comment "公开类型, 0: 公开, 1: 只有好友可看, 2: 只有附近可看, 3: 指定好友可看, 4: 指定好友不可看",
    `limitPlayerUids` longtext default "" comment "限制玩家uids",
    `showLocation` tinyint default 1 comment "是否公开位置, 1: 显示, 2: 不显示",
    `lat` double default 0 comment "gps纬度",
    `lng` double default 0 comment "gps经度",
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `trendsIssue` (
    `uid` bigint default 0 comment "发布uid",
    `playerUid` bigint default 0 comment "发布玩家uid",
    `trendsUid` bigint default 0 comment "发布动态uid",
    `state` tinyint default 0 comment "发布状态, 0: 正常, 1: 添加, 2: 添加完成, 3: 删除, 4: 删除完成",
    index playerUid(`playerUid`, `state`),
    index trendsUid(`trendsUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `trendsTimeline` (
    `uid` bigint default 0 comment "动态时间轴uid",
    `playerUid` bigint default 0 comment "玩家uid",
    `trendsUid` bigint default 0 comment "动态uid",
    `isOwner` tinyint default 0 comment "是否自己的",
    index trendsUid(`trendsUid`),
    index playerAndTrendsUid(`playerUid`, `trendsUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `arenaValueRecord` (
    `uid` bigint default 0 comment "竞技值修改记录uid",
    `groupUid` bigint default 0 comment "群uid",
    `playerUid` bigint default 0 comment "玩家uid",
    `operatorPlayerUid` bigint default 0 comment "操作玩家uid",
    `arenaValue` int default 0 comment "修改竞技值",
    `operatorTime` bigint default 0 comment "操作时间",
    `state` tinyint default 0 comment "0: 正常, 1: 废弃",
    `optType` tinyint default 0 comment "操作类型",
    index search(`groupUid`, `playerUid`, `state`, `optType`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `serviceChargeRecord` (
    `uid` bigint default 0 comment "服务费修改记录uid",
    `groupUid` bigint default 0 comment "群uid",
    `playerUid` bigint default 0 comment "玩家uid",
    `operatorPlayerUid` bigint default 0 comment "操作玩家uid",
    `serviceChargeValue` int default 0 comment "修改服务费",
    `serviceChargeValueByLeague` int default 0 comment "修改联盟服务费",
    `operatorTime` bigint default 0 comment "操作时间",
    `state` tinyint default 0 comment "0: 正常, 1: 废弃",
    index search(`groupUid`, `playerUid`, `state`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `groupArenaCostDiamondDaily` (
    `uid` bigint default 0 comment "群竞技场钻石每日消耗uid",
    `groupUid` bigint default 0 comment "群uid",
    `time` bigint default 0 comment "每日零点时间戳",
    `arenaCost` int default 0 comment "竞技场消耗",
    `boxCost` int default 0 comment "包厢消耗",
    `mineRedCost` int default 0 comment "红包埋雷消耗",
    index searchGroupUid(`uid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `groupArenaCostDiamondDetail` (
    `uid` bigint default 0 comment "群竞技场钻石消耗uid",
    `groupUid` bigint default 0 comment "群uid",
    `roomType` tinyint default 1 comment "房间类型";
    `gameUid` bigint default 0 comment "游戏uid",
    `bureau` int default 0 comment "局数",
    `cost` int default 0 comment "消耗",
    `gameType` int default 0 comment "游戏类型",
    `gameSubType` int default 0 comment "游戏子类型",
    `time` bigint default 0 comment "每日零点时间戳",
    index searchTimeAndGroupUidAndRoomType(`time`, `groupUid`, `roomType`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `serviceChargeDaily` (
    `uid` bigint default 0 comment "服务费每日记录uid",
    `groupUid` bigint default 0 comment "群uid",
    `time` bigint default 0 comment "每日零点时间戳",
    `arenaCost` int default 0 comment "竞技场消耗",
    `boxCost` int default 0 comment "包厢消耗",
    `mineRedCost` int default 0 comment "红包埋雷消耗",
    `arenaCostByLeague` int default 0 comment "联盟竞技场消耗",
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `serviceChargePlayerDaily` (
    `uid` bigint default 0 comment "服务费每日记录uid",
    `groupUid` bigint default 0 comment "群uid",
    `playerUid` bigint default 0 comment "添加玩家uid",
    `time` bigint default 0 comment "每日零点时间戳",
    `roomType` tinyint default 1 comment "房间类型",
    `arenaCost` int default 0 comment "竞技场消耗",
    `boxCost` int default 0 comment "包厢消耗",
    `mineRedCost` int default 0 comment "红包埋雷消耗",
    `arenaCostByLeague` int default 0 comment "联盟竞技场消耗",
    index searchTimeAndGroupUid(`time`, `groupUid`),
    index searchGroupUidAndPlayerUid(`groupUid`, `playerUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `serviceChargeArenaDaily` (
    `uid` bigint default 0 comment "服务费每日记录uid",
    `roomType` tinyint default 1 comment "房间类型",
    `groupUid` bigint default 0 comment "群uid",
    `gameUid` bigint default 0 comment "竞技场uid",
    `gameType` int default 0 comment "游戏类型",
    `gameSubType` int default 0 comment "游戏子类型",
    `time` bigint default 0 comment "每日零点时间戳",
    `bureau` int default 0 comment "局数",
    `cost` int default 0 comment "消耗",
    `bureauByLeague` int default 0 comment "联盟局数",
    `costByLeague` int default 0 comment "联盟消耗",
    index searchTimeAndGroupUidWithRoomType(`time`, `groupUid`, `roomType`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `serviceChargeDetail` (
    `uid` bigint default 0 comment "服务费详细记录uid",
    `roomType` tinyint default 1 comment "房间类型",
    `groupUid` bigint default 0 comment "群uid",
    `gameUid` bigint default 0 comment "游戏uid",
    `gameType` int default 0 comment "游戏类型",
    `gameSubType` int default 0 comment "游戏子类型",
    `playerUid` bigint default 0 comment "玩家uid",
    `bureau` int default 0 comment "局数",
    `cost` int default 0 comment "消耗",
    `time` bigint default 0 comment "每日零点时间戳",
    `bureauByLeague` int default 0 comment "联盟局数",
    `costByLeague` int default 0 comment "联盟消耗",
    index searchTimeAndGroupUidAndPlayerUidWithRoomTYpe(`time`, `groupUid`, `playerUid`, `roomType`),
    index searchTimeAndGameUidWithRoomType(`time`, `gameUid`, `roomType`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `serviceChargePlayerDetail` (
    `uid` bigint default 0 comment "服务费每日详细记录uid",
    `groupUid` bigint default 0 comment "群uid",
    `playerUid` bigint default 0 comment "添加玩家uid",
    `costPlayerUid` bigint default 0 comment "消耗玩家uid",
    `time` bigint default 0 comment "每日零点时间戳",
    `arenaCost` int default 0 comment "竞技场消耗",
    `boxCost` int default 0 comment "包厢消耗",
    `mineRedCost` int default 0 comment "红包埋雷消耗",
    `arenaBureau` int default 0 comment "竞技场局数",
    `boxBureau` int default 0 comment "包厢局数",
    `mineRedBureau` int default 0 comment "红包埋雷局数",
    `type` tinyint default -1 comment "来源类型, 1: 成员, 2: 上线, 3: 一条线, 4: 群主",
    `arenaBureauByLeague` int default 0 comment "联盟竞技场局数",
    `arenaCostByLeague` int default 0 comment "联盟竞技场消耗",
    index searchTimeAndGroupUidAndPlayerUidWithType(`time`, `groupUid`, `playerUid`, `costPlayerUid`, `type`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `hundredArenaHBMLRebRecord` (
    `uid` bigint default 0 comment "百人场下注记录uid",
    `arenaUid` bigint default 0 comment "百人场uid",
    `rebPlayerUid` bigint default 0 comment "下注玩家uid",
    `index` int default 0 comment "下注位置",
    `value` int default 0 comment "下注金额",
    `time` bigint default 0 comment "下注时间",
    index searchByPlayer(`rebPlayerUid`, `arenaUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `hundredArenaHBMLBureauRecord` (
    `uid` bigint default 0 comment "百人场局数记录uid",
    `arenaUid` bigint default 0 comment "百人场uid",
    `value` int default 0 comment "赢的金额",
    `time` bigint default 0 comment "开局时间",
    `endTime` bigint default 0 comment "结束时间",
    `rebRecord` longtext default "" comment "下注记录",
    index searchByArenaUid(`arenaUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `hundredArenaRebRecord` (
    `uid` bigint default 0 comment "百人场下注记录uid",
    `arenaUid` bigint default 0 comment "百人场uid",
    `rebPlayerUid` bigint default 0 comment "下注玩家uid",
    `time` bigint default 0 comment "下注时间",
    `rebInfo` longtext default "" comment "下注信息",
    `bankerCardType` tinyint default 0 comment "庄家牌型",
    `rebValue` int default 0 comment "下注金额",
    `winValue` int default 0 comment "输赢金额",
    index searchByPlayer(`rebPlayerUid`, `arenaUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `hundredArenaBureauRecord` (
    `uid` bigint default 0 comment "百人场局数记录uid",
    `arenaUid` bigint default 0 comment "百人场uid",
    `time` bigint default 0 comment "开局时间",
    `endTime` bigint default 0 comment "结束时间",
    `bankerPlayerUid` bigint default 0 comment "庄家uid",
    `bankerWinValue` int(11) default 0 comment "庄家输赢分数",
    `cardInfo` longtext default "" comment "牌信息",
    index searchByArenaUidAndBankerPlayerUid(`arenaUid`, `bankerPlayerUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

-- TODO 2018-12-17 add
create table if not exists `accountWithdrawType` (
    `uid` bigint default 0 comment "转账类型uid",
    `playerUid` bigint default 0 comment "玩家uid",
    `type` tinyint default 1 comment "转账类型, 1:微信  2:支付宝  3:银行卡",
    `payeeAccount` varchar(255) default "" comment "收款账号",
    `payeeRealName` varchar(255) default "" comment "收款方真实姓名",
    `payeeAddress` varchar(255) default "" comment '开户行地址',
    `payeeLocation` varchar(255) default "" comment '开户行所在地',
    `status` tinyint default 1 comment "状态 0:不可用 1:可用",
    index searchByPlayer(`playerUid`),
    primary key(`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `walletRedPacket` (
    `uid` bigint default 0 comment "红包Uid",
    `senderUid` bigint default 0 comment '发送者uid',
    `groupUid` bigint default 0 comment '红包所在群uid',
    `amount` int default 0 comment '红包总金额 单位:分',
    `count` int default 0 comment '红包数量',
    `content` varchar(255) default "" comment '红包描述',
    `type` tinyint default 0 comment '红包类型：0-普通红包;1-拼手气红包',
    `createdAt` bigint default 0 comment '红包发送时间戳',
    `expiredAt` bigint default 0 comment '红包过期时间戳',
    `clearAt` bigint default 0 comment '红包被领完时间戳',
    `receivers` longtext default "" comment "接收玩家uid",
    `receivedCount` int default 0 comment '被领取的红包数量',
    `receivedAmount` int default 0 comment '被领取的总金额 单位:分',
    `status` tinyint default 0 comment '红包状态: 0-领取中;1-已领完;2-过期;3-被退还',
    `returnAmount` int default 0 comment '被退还的总金额 单位:分',
    index searchByExpire(`expiredAt`),
    PRIMARY KEY (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `walletRedPacketReceivedRecord` (
    `uid` bigint default 0 comment '领取红包记录Uid',
    `playerUid` bigint default 0 comment '领取者Uid',
    `playerName` varchar(255) default 0 comment '领取者昵称',
    `playerIcon` varchar(255) default 0 comment '领取者头像',
    `redPacketUid` bigint(20) default 0 comment '领取的红包Uid',
    `amount` int default 0 comment '领取的红包金额 单位:分',
    `createdAt` bigint default 0 comment '领取的时间戳',
    index searchByRedPacketUid(`redPacketUid`),
    PRIMARY KEY (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `walletTransfer` (
    `uid` bigint default 0 comment '转账记录ID',
    `transferUid` bigint default 0 comment '发起转账人Uid',
    `targetUid` bigint default 0 comment '接收转账人Uid',
    `amount` bigint default 0 comment '转账金额(单位:分)',
    `content` varchar(255) default "" comment '转账描述',
    `status` tinyint default 0 comment '转账状态: 0-转账中;1-已接收;2-被退回',
    `createdAt` bigint default 0 comment '转账时间',
    `expiredAt` bigint default 0 comment '到期时间',
    `receivedAt` bigint default 0 comment '领取时间',
    `transferType` tinyint default 0 comment '转账类型：0-转账；1-个人红包',
    index searchByTargetUid(`targetUid`),
    index searchByExpire(`expiredAt`),
    PRIMARY KEY (`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2018-12-17 add end

-- TODO 2018-12-24 add
create table if not exists `logAccount` (
  `uid` bigint default 0 comment '日志记录唯一ID',
  `targetUid` bigint default 0 comment '账号ID',
  `action` int default 0 comment '日志类型：1-注册，2-登录，3-注销',
  `timestamp` bigint default 0 comment '发生时的unix timestamp',
  `accountType` int default 0 default '0' COMMENT '注册账号类型,0: 游客登陆, 1: 手机号登陆, 2: 快速登陆, 3: 微信登陆',
  `serverId` int default 0 comment '服务器ID',
  `deviceModel` varchar(32) default "" comment '机型',
  `deviceSn` varchar(32) default "" comment '设备序号',
  `address` varchar(255) default "" comment '地址',
  `osVersion` varchar(32) default "" comment '终端系统版本',
  `appVersion` varchar(32) default "" comment 'App版本',
  `channelId` int default 0 comment '渠道ID',
  `mobileNumber` varchar(32) default "" comment '手机号',
  index `idx_logAccount_target` (`targetUid`),
  index `idx_logAccount_action` (`action`),
  index `idx_logAccount_time` (`timestamp`),
  index `idx_logAccount_accountType` (`accountType`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2018-12-24 add end

-- TODO 2019-01-03 add
create table if not exists `walletRecord` (
  `uid` bigint default 0 comment "钱包操作记录uid",
  `playerUid` bigint default 0 comment "操作玩家uid",
  `beginAmount` int default 0 comment '操作前的金额',
  `amount` int default 0 comment '操作金额',
  `inMoney` int default 0 comment '收入金额',
  `outMoney` int default 0 comment '支出金额',
  `month` bigint default 0 comment '本月开始时间戳',
  `action` tinyint default 0 comment '操作类型：操作：0-发红包；1-领取红包；2-红包过期退还；3-钱包充值；4-钱包提现；5-发起转账；6-领取转账；7-拒绝转账；8-转账过期退还；',
  `createdAt` bigint default 0 comment '操作时间',
  index `searchByPlayerUidAndMonth` (`playerUid`, `month`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2019-01-03 add end

-- TODO 2019-01-05 add
create table if not exists `groupCurrencyRecord` (
  `uid` bigint default 0 comment "群货币记录uid",
  `groupUid` bigint default 0 comment "群uid",
  `time` bigint default 0 comment "时间戳",
  `arenaValue` int default 0 comment "当天最后竞技值",
  `incArenaValue` int default 0 comment "当天添加竞技值",
  `decArenaValue` int default 0 comment "当前减少竞技值",
  `incArenaValueByWallet` int default 0 comment "当前通过钱包添加竞技值",
  `incArenaValueByQuick` int default 0 comment "当前通过快速充值添加竞技值",
  `serviceValue` int default 0 comment "当天最后服务费",
  `chiefServiceValue` int default 0 comment "当天最后群主服务费",
  `decServiceValue` int default 0 comment "当天减少服务费",
  `decServiceValueByLeague` int default 0 comment "当前减少联盟服务费",
  index `searchByGroupUid` (`groupUid`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2019-01-05 add end

-- TODO 2019-01-07 add
create table if not exists `walletMineRedPacketSendRecord` (
  `uid` bigint default 0 comment '红包Uid',
  `senderUid` bigint default 0 comment '发送者uid',
  `groupUid` bigint default 0 comment '红包所在群uid',
  `amount` int default 0 comment '红包总金额 单位:分',
  `count` int default 0 comment '红包数量',
  `content` varchar(255) default "" comment '红包描述',
  `mine` int default 0 comment '埋雷数字',
  `mineMultiple` int default 0 comment '埋雷倍数*10',
  `specialAwards` varchar(255) default "" comment '特殊奖励',
  `createdAt` bigint default 0 comment '红包发送时间戳',
  `expiredAt` bigint default 0 comment '红包过期时间戳',
  `clearAt` bigint default 0 comment '红包被领完时间戳',
  `receivedCount` int default 0 comment '被领取的红包数量',
  `receivedAmount` int default 0 comment '被领取的总金额 单位:分',
  `status` tinyint default 0 comment '红包状态: 0-领取中;1-已领完;2-过期;3-被退还',
  `returnAmount` int default 0 comment '被退还的总金额 单位:分',
  `receivers` varchar(1024) default "" comment '接收者列表',
  `specialAward` int default 0 comment '特殊奖励',
  index `searchBySenderUidAndExpired`(`senderUid`, `expiredAt`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `walletMineRedPacketReceivedRecord` (
  `uid` bigint default 0 comment '领取埋雷红包记录Uid',
  `playerUid` bigint default 0 comment '领取者Uid',
  `redPacketUid` bigint(20) default 0 comment '领取的红包Uid',
  `amount` int default 0 comment '领取的红包金额 单位:分',
  `feedback` int default 0 comment '是否中雷 0-未中；1-中雷',
  `specialAward` int default 0 comment '是否特殊奖励：0-不是；1-豹子；2-顺子；3-0.01奖励',
  `createdAt` bigint default 0 comment '领取的时间戳',
  index `searchByRedPacketUid` (`redPacketUid`),
  index `searchByPlayerUid` (`playerUid`)
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2019-01-07 add end

-- TODO 2019-01-26 add
create table if not exists `logAccountRemain` (
  `uid` bigint default 0 comment "uid",
  `date` bigint default -1 comment '日期时间戳 单位秒',
  `registerNum` bigint default 0 comment '注册人数',
  `day_2` bigint default 0 comment '2日留存',
  `day_3` bigint default 0 comment '3日留存',
  `day_4` bigint default 0 comment '4日留存',
  `day_5` bigint default 0 comment '5日留存',
  `day_6` bigint default 0 comment '6日留存',
  `day_7` bigint default 0 comment '7日留存',
  `day_14` bigint default 0 comment '14日留存',
  `day_30` bigint default 0 comment '30日留存',
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `nickname` (
  `uid` bigint default 0 comment "uid",
  `name` varchar(255) default "" comment "姓名",
  `state` int default '0',
  primary key (`uid`)
) engine=innodb default CHARSET=utf8mb4;
-- TODO 2019-01-26 add end

-- TODO 2019-02-02 add
create table if not exists `groupFinancialApplyRecord` (
  `uid` bigint default 0 comment "uid",
  `userUid` bigint default 0 comment '用户uid',
  `groupUid` bigint default 0 comment '群uid',
  `financeUid` bigint default 0 comment '财务uid',
  `amount` bigint default 0 comment '单位：分',
  `type` tinyint default 0 comment '1-支付宝 2-微信',
  `status` tinyint default 0 comment '0-申请中；1-已完成；2-已拒绝',
  `img` varchar(512)  default "" comment '上传的图片',
  `createdAt` bigint default 0 comment '创建时间',
  `updatedAt` bigint default 0 comment '更新时间',
  `times` int default 0 comment "次数",
  primary key (`uid`)
) engine=innodb default charset=utf8mb4;
-- TODO 2019-02-02 add end

-- TODO 2019-02-22 add
create table if not exists `accountUid` (
  `uid` bigint default 0 comment "uid",
  `good` tinyint default 0 comment "靓号, 0: 不是, 1: 是",
  `state` tinyint default 1 comment "状态, 1: 可用, 0: 不可用",
  index `uid` (`uid`),
  index `state` (`state`)
) engine=innodb default charset=utf8mb4;
-- TODO 2019-02-22 add end

-- TODO 2019-03-03 add
create table if not exists `serviceChargeAdmin` (
  `uid` bigint default 0 comment "uid",
  `groupUid` bigint default 0 comment '群UID',
  `playerUid` bigint default  0 comment '提供竞技值的玩家UID',
  `chargeAt` bigint default 0 comment 'UNIX时间戳',
  `arenaValue` int default 0 comment '竞技值之服务费',
  `arenaValueByLeague` int default 0 comment "联盟竞技值收益",
  `ownerType` tinyint default 1 comment '服务费拥有着',
  primary key (`uid`)
) engine=innodb default charset=utf8mb4;
-- TODO 2019-03-03 add end

-- TODO 2019-03-14 add
create table if not exists `mineRedPacketSendRecord` (
  `uid` bigint default 0 comment '红包Uid',
  `senderUid` bigint default 0 comment '发送者uid',
  `groupUid` bigint default 0 comment '红包所在群uid',
  `amount` int default 0 comment '红包总金额 单位:分',
  `count` int default 0 comment '红包数量',
  `content` varchar(255) default "" comment '红包描述',
  `mine` int default 0 comment '埋雷数字',
  `mineMultiple` int default 0 comment '埋雷倍数*10',
  `specialAwards` varchar(255) default "" comment '特殊奖励',
  `costModel` int default 0 comment '抽水模式',
  `costModelValue` int default 0 comment '抽水值',
  `createdAt` bigint default 0 comment '红包发送时间戳',
  `expiredAt` bigint default 0 comment '红包过期时间戳',
  `clearAt` bigint default 0 comment '红包被领完时间戳',
  `receivedCount` int default 0 comment '被领取的红包数量',
  `receivedAmount` int default 0 comment '被领取的总金额 单位:分',
  `status` tinyint default 0 comment '红包状态: 0-领取中;1-已领完;2-过期;3-被退还',
  `returnAmount` int default 0 comment '被退还的总金额 单位:分',
  `feedbackCnt` int default 0 comment '中雷次数',
  `receivers` varchar(1024) default "" comment '接收者列表',
  index `searchByGroupUidAndSenderUid`(`groupUid`, `senderUid`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;

create table if not exists `mineRedPacketReceivedRecord` (
  `uid` bigint default 0 comment '领取埋雷红包记录Uid',
  `playerUid` bigint default 0 comment '领取者Uid',
  `groupUid` bigint default 0 comment '群uid',
  `redPacketUid` bigint(20) default 0 comment '领取的红包Uid',
  `amount` int default 0 comment '领取的红包金额 单位:分',
  `feedback` int default 0 comment '中雷金额',
  `specialType` tinyint default 0 comment '是否特殊奖励：0-不是；1-豹子；2-顺子；3-0.01奖励',
  `specialAward` int default 0 comment '特殊奖励金额',
  `createdAt` bigint default 0 comment '领取的时间戳',
  index `searchByRedPacketUid` (`redPacketUid`),
  index `searchByGroupUidAndPlayerUid` (`groupUid`, `playerUid`),
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2019-03-14 add end

-- TODO 2019-04-04 add
create table if not exists `gameDownloadRecord` (
  `uid` bigint default 0 comment "uid",
  `userUid` bigint default 0 comment '用户ID',
  `groupUid` bigint default 0 comment '群Uid',
  `uuid` varchar(128) default "" comment 'uuid',
  `deceiveType` tinyint default 1 comment '设备类型：1-IOS; 2-Andriod',
  `createdAt` bigint default 0 comment '创建时间',
  primary key (`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2019-04-04 add end

-- TODO 2019-05-10 add
create table if not exists `assistantWeChat` (
  `uid` bigint default 0 comment "uid",
  `weChat` varchar(512) default '' comment '客服微信号',
  `province` varchar(512) default '' comment '省',
  `city` varchar(512) default '' comment '市',
  `district` varchar(512) default '' comment '区',
  `adCode` bigint default 0 comment '区域编号',
  primary key (`uid`),
  index `search` (`province`, `city`, `district`),
  index `adCode` (`adCode`)
)engine=innodb default charset=utf8mb4;

create table if not exists `locationInfo` (
  `uid` bigint default 0 comment "uid",
  `location` varchar(255) default "" comment "位置",
  `info` longtext default "" comment "位置信息",
  primary key (`uid`),
  index `location` (`location`)
)engine=innodb default charset=utf8mb4;
-- TODO 2019-05-10 add end

-- TODO add 2019-07-03
create table `questGetRewardRecord` (
  `uid` bigint default 0 comment '竞技值修改记录uid',
  `groupUid` bigint default 0 comment '群uid',
  `playerUid` bigint default 0 comment '玩家uid',
  `arenaUid` bigint default 0 comment '操作玩家uid',
  `arenaValue` int default 0 comment '修改竞技值',
  `operatorTime` bigint default 0 comment '操作时间',
  `gameType` int default 0 comment '游戏类型',
  `subType` int default 0 comment '小类型',
  `bureau` int default 0 comment '局数',
  primary key (`uid`),
  index `search` (`groupUid`,`playerUid`)
)engine=innodb default charset=utf8mb4;

create table `autoUpDeputyRecord` (
  `uid` bigint default 0 comment 'uid',
  `groupUid` bigint default 0 comment '群uid',
  `playerUid` bigint default 0 comment '玩家uid',
  `operatorTime` bigint default 0 comment '操作时间',
  primary key (`uid`),
  index `search` (`groupUid`,`playerUid`)
)engine=innodb default charset=utf8mb4;
-- TODO add 2019-07-03 end

-- TODO 2019-07-22 add
create table if not exists `league` (
  `uid` bigint default 0 comment "uid",
  `createTime` bigint default 0 comment "创建时间",
  `endTime` bigint default 0 comment "结束时间",
  `leaderUid` bigint default -1 comment "盟主uid",
  `name` varchar(255) default "" comment "联盟名",
  `desc` varchar(1014) default "" comment "联盟描述",
  `gameDesc` varchar(1014) default "" comment "联盟推荐游戏描述",
  `icon` varchar(255) default "" comment "联盟icon",
  `announcementExpireAt` bigint default -1 comment "公告过期时间",
  `announcement` varchar(1024) default "" comment "公告",
  `serviceChargeDivideAdmin` int default 0 comment "管理费比例",
  `costDiamond` bigint default 0 comment "钻石消耗",
  `floor` longtext default "" comment "楼层信息",
  `member` longtext default "" comment "成员",
  `arenaScoreCnt` longtext default "" comment "战绩条数",
  `curArenaScore` longtext default "" comment "当前战绩",
  `state` int(11) default 0 comment "状态: 0:正常, 1: 拒绝, 2:删除",
  `money` longtext default "" comment "玩家联盟货币",
  primary key(`uid`)
)engine=innodb default charset=utf8mb4;
-- TODO 2019-07-22 add end

-- delete table data
delete from `account`;
delete from `player`;
delete from `group`;
delete from `room`;
delete from `roomScore`;
delete from `arena`;
delete from `arenaScore`;
delete from `mail`;
delete from `boxRoomScore`;
delete from `recommend`;
delete from `playFieldRecord`;
delete from `playFieldRewardRecord`;
delete from `nearbyNote`;
delete from `mailBox`;
delete from `mailBoxUid`;
delete from `trendsMeta`;
delete from `trendsIssue`;
delete from `trendsTimeline`;
delete from `arenaValueRecord`;
delete from `serviceChargeRecord`;
delete from `groupArenaCostDiamondDaily`;
delete from `groupArenaCostDiamondDetail`;
delete from `serviceChargeDaily`;
delete from `serviceChargePlayerDaily`;
delete from `serviceChargeArenaDaily`;
delete from `serviceChargeDetail`;
delete from `serviceChargePlayerDetail`;

-- drop table
drop table `account`;
drop table `player`;
drop table `group`;
drop table `room`;
drop table `roomScore`;
drop table `arena`;
drop table `arenaScore`;
drop table `mail`;
drop table `boxRoomScore`;
drop table `recommend`;
drop table `playFieldRecord`;
drop table `playFieldRewardRecord`;
drop table `nearbyNote`;
drop table `mailBox`;
drop table `mailBoxUid`;
drop table `trendsMeta`;
drop table `trendsIssue`;
drop table `trendsTimeline`;
drop table `arenaValueRecord`;
drop table `serviceChargeRecord`;
drop table `groupArenaCostDiamondDaily`;
drop table `groupArenaCostDiamondDetail`;
drop table `serviceChargeDaily`;
drop table `serviceChargePlayerDaily`;
drop table `serviceChargeArenaDaily`;
drop table `serviceChargeDetail`;
drop table `serviceChargePlayerDetail`;

-- index
alter table `account` add index phone(phone);
alter table `account` add index otherPlatformToken(otherPlatformToken);
alter table `mailBox` add index search(`messageUid`);
alter table `mailBox` add index searchByPlayer(`toPlayerUid`, `messageUidByPlayer`);

-- TODO 2018-09-25 add
alter table `player` add createTimestamp bigint default 0 comment "创建时间";
alter table `player` add born bigint default 0 comment "出生年月";
alter table `player` add signature varchar(1024) default "" comment "个性签名";
alter table `player` add emotion tinyint default 0 comment "情感";
alter table `player` add showImage varchar(2048) default "" comment "展示图片";
alter table `player` add cover varchar(255) default "" comment "封面图片";
update `player` set createTimestamp = 1537372800000;
update `player` set born = 946656000000;
-- TODO 2018-09-25 add end

-- TODO 2018-09-26 add
alter table `account` add type tinyint default 0 comment "账号登陆类型,0: 游客登陆, 1: 手机号登陆, 2: 快速登陆, 3: 微信登陆";
alter table `group` add totalCost varchar(1024) default "" comment "累计消耗";
-- TODO 2018-09-26 add end

-- TODO 2018-09-28 add
alter table `arenaScore` add index searchPlayerUid(`playerUid`);
alter table `arenaScore` add index searchGroupUidAndPlayerUid(`groupUid`, `playerUid`);
alter table `arenaScore` add index searchArenaUidAndPlayerUid(`arenaUid`, `playerUid`);
-- TODO 2018-09-28 add end

-- TODO 2018-09-29 add
alter table `group` drop serviceCharge;
alter table `group` add totalServiceCharge bigint default 0 comment "总服务费";
alter table `arena` drop deputyDivide;
alter table `arenaScore` add `clearPlayerUid` bigint default 0 comment "清理操作玩家uid";
alter table `arenaScore` add `clearTime` bigint default 0 comment "清理操作时间";
drop table `serviceCharge`;
-- TODO 2018-09-29 add end

-- TODO 2018-10-07 add
alter table `arenaValueRecord` add `state` tinyint default 0 comment "0: 正常, 1; 废弃";
alter table `serviceChargeRecord` add `state` tinyint default 0 comment "0: 正常, 1; 废弃";
-- TODO 2018-10-07 add end

-- TODO 2018-10-10 add
alter table `arenaScore` add `allCnt` varchar(2048) default "" comment "大结算记录";
-- TODO 2018-10-10 add end

-- TODO 2018-10-11 add
alter table `player` add `privilege` bigint default 0 comment "权限";
-- TODO 2018-10-11 add end

-- TODO 2018-10-12 add
alter table `boxRoomScore` add `gain` int default 0 comment "打赏次数";
alter table `boxRoomScore` add `gainList` varchar(4096) default "" comment "打赏列表";
-- TODO 2018-10-12 add end

-- TODO 2018-10-16 add
alter table `group` add `leaves` longtext default "" comment "离开者";
alter table `player` add `ownerGroupCnt` int default 0 comment "群数量";
alter table `player` add `groupLimit` int default 0 comment "群上限";
-- TODO 2018-10-16 add end

-- TODO 2018-10-17 add
alter table `player` drop `groupLimit`;
alter table `player` change `privilege` `privilege` int default 0 comment "权限等级";
-- TODO 2018-10-17 add end

-- TODO 2018-10-19 add
alter table `player` change `msgTop` `msgTopV2` longtext default "" comment "消息顶置";
-- TODO 2018-10-19 add end

-- TODO 2018-10-27 add
alter table `player` add `ownerArenaCnt` int default 0 comment "竞技场数量";
-- TODO 2018-10-27 add end

-- TODO 2018-10-29 add
alter table `group` add `arenaScoreRank` longtext default "" comment "竞技场分数排行";
alter table `arena` add `scoreRank` longtext default "" comment "竞技场分数排行";
alter table `arena` drop `allScore`;
alter table `arenaScore` add `recordUid` longtext default "" comment "战绩列表";
alter table `arenaScore` drop `record`;
-- TODO 2018-10-29 add end

-- TODO 2018-11-01 add
drop table `boxRank`;
alter table `group` add `boxScoreRank` longtext default "" comment "包厢分数排行";
-- TODO 2018-11-01 add end

-- TODO 2018-11-20 add
alter table `group` add `quickAddSwitch` tinyint default 1 comment "快速上分开关";
-- TODO 2018-11-20 add end

-- TODO 2018-12-04 add
alter table `player` add `hundredArenaReb` longtext default "" comment "百人场下注总额";
-- TODO 2018-12-04 add end

-- TODO 2018-12-08 add
alter table `player` add `curHundredArena` longtext default "" comment "当前百人场";
-- TODO 2018-12-08 add end

-- TODO 2018-12-17 add
alter table `account` add `payPassword` varchar(11) default "" comment "支付密码";
-- TODo 2018-12-17 add end

-- TODO 2018-12-29 add
alter table `group` add `flrSwitch` tinyint default 0 comment "防拉人开关";
-- TODO 2018-12-29 add end

-- TODO 2019-01-05 add
alter table `group` add `totalIncArenaValue` bigint default 0 comment "总添加竞技值";
alter table `group` add `totalDecArenaValue` bigint default 0 comment "总减少竞技值";
alter table `group` add `totalIncArenaValueByWallet` bigint default 0 comment "总添加竞技值通过钱包";
alter table `group` add `totalIncArenaValueByQuick` bigint default 0 comment "总添加竞技值通过快速充值";
alter table `group` add `totalDecServiceValue` bigint default 0 comment "总减少服务费";
alter table `group` add `mineRedPackConf` longtext default "" comment "红包埋雷配置";
-- TODO 2019-01-05 add end

-- TODO 2019-01-09 add
alter table `group` drop `totalIncArenaValue`;
alter table `group` drop `totalDecArenaValue`;
alter table `group` drop `totalIncArenaValueByWallet`;
alter table `group` drop `totalIncArenaValueByQuick`;
alter table `group` drop `totalDecServiceValue`;
alter table `group` drop `totalServiceCharge`;
-- TODO 2019-01-01 add end

-- TODO 2019-01-25 add
alter table `group` add `floor` longtext default "" comment "楼层信息";
alter table `arena` add `floorUid` bigint default -1 comment "楼层uid";
alter table `group` add `announcement` varchar(1024) default '' comment'群公告';
alter table `group` add `announcementExpireAt` bigint default 0 comment '群公告过期时间戳，毫秒';
alter table `group` add `quickDecSwitch` tinyint default 0 comment '快速下分开关';
alter table `group` add `shareSwitch` tinyint default 0 comment '分享开关';
alter table `group` add `shareAward` tinyint default 0 comment '分享奖励值';
alter table `recommend` add `groupUid` bigint default -1 comment '关联的群UID';
-- TODO 2019-01-25 add end

-- TODO 2019-02-22 add
alter table `arenaValueRecord` add `optType` tinyint default 0 comment "操作类型";
alter table `mailBox` drop `fromPlayerName`;
alter table `mailBox` drop `fromPlayerIcon`;
alter table `account` add `state` tinyint default 0 comment "状态, 0: 正常, 1: 删除, 2: 封号";
alter table `recommend` add `state` tinyint default 1 comment "状态，1-正常，2-已清除";
alter table `groupFinancialApplyRecord` add `times` int default 0 comment "次数";
-- TODO 2019-02-22 add end

-- TODO 2019-03-01 add
alter table `arenaValueRecord` drop index search;
alter table `arenaValueRecord` add index search(`groupUid`, `playerUid`, `state`, `optType`);
alter table `serviceChargePlayerDetail` add `type` tinyint default -1 comment "来源类型, 1: 成员, 2: 上线, 3: 一条线, 4: 群主";
alter table `serviceChargePlayerDetail` drop index searchTimeAndGroupUidAndPlayerUid;
alter table `serviceChargePlayerDetail` add index searchTimeAndGroupUidAndPlayerUid(`time`, `groupUid`, `playerUid`, `costPlayerUid`, `type`);
alter table `group` add `serviceChargeDivideAdmin` int default 0 comment '服务费之管理费比例';
-- TODO 2019-03-01 add

-- TODO 2019-03-11 add
alter table `group` add `newbieChatSwitch` tinyint default 0 comment '新人聊天受限开关';
-- TODO 2019-03-11 add end

-- TODO 2019-03-14 add
alter table `player` add `recharge` bigint default 0 comment "总充值";
alter table `player` add `defaultIcon` char(2) default "" comment "默认头像";
alter table `group` add `mineRedScoreRank` longtext default "" comment "红包埋雷分数排行";
alter table `group` add `groupSwitch` bigint default 0 comment "群开关";
-- TODO 2019-03-14 add end

-- TODO 2019-03-18 add
alter table `walletMineRedPacketSendRecord` add `specialAward` int default 0 comment '特殊奖励';
alter table `mineRedPacketReceivedRecord` add `groupUid` bigint default 0 comment '群uid';
alter table `mineRedPacketReceivedRecord` drop index `searchByPlayerUid`;
alter table `mineRedPacketReceivedRecord` add index `searchByGroupUidAndPlayerUid` (`groupUid`, `playerUid`);
alter table `mineRedPacketSendRecord` drop index `searchBySenderUidAndExpired`;
alter table `mineRedPacketSendRecord` add index `searchByGroupUidAndSenderUid`(`groupUid`, `senderUid`);
-- TODO 2019-03-18 add end

-- TODO 2019-03-30 add
alter table `serviceChargeDaily` change `cost` `arenaCost` int default 0 comment "竞技场消耗";
alter table `serviceChargeDaily` add `boxCost` int default 0 comment "包厢消耗";
alter table `serviceChargeDaily` add `mineRedCost` int default 0 comment "红包埋雷消耗";
alter table `serviceChargePlayerDaily` change `cost` `arenaCost` int default 0 comment "竞技场消耗";
alter table `serviceChargePlayerDaily` add `boxCost` int default 0 comment "包厢消耗";
alter table `serviceChargePlayerDaily` add `mineRedCost` int default 0 comment "红包埋雷消耗";
alter table `serviceChargeArenaDaily` add `roomType` tinyint default 1 comment "房间类型";
alter table `serviceChargeArenaDaily` change `arenaUid` `gameUid` bigint default 0 comment "游戏uid";
alter table `serviceChargeArenaDaily` drop index `searchTimeAndGroupUid`;
alter table `serviceChargeArenaDaily`add index `searchTimeAndGroupUidWithRoomType`(`time`, `groupUid`, `roomType`);
alter table `serviceChargeDetail` add `roomType` tinyint default 1 comment "房间类型";
alter table `serviceChargeDetail` change `arenaUid` `gameUid` bigint default 0 comment "游戏uid";
alter table `serviceChargeDetail` drop index `searchTimeAndGroupUidAndPlayerUid`;
alter table `serviceChargeDetail` add index `searchTimeAndGroupUidAndPlayerUidWithRoomType`(`time`, `groupUid`, `playerUid`, `roomType`);
alter table `serviceChargeDetail` drop index `searchTimeAndArenaUid`;
alter table `serviceChargeDetail` add index `searchTimeAnGameUidWithRoomType`(`time`, `gameUid`, `roomType`);
alter table `serviceChargePlayerDetail` change `cost` `arenaCost` int default 0 comment "竞技场消耗";
alter table `serviceChargePlayerDetail` add `boxCost` int default 0 comment "包厢消耗";
alter table `serviceChargePlayerDetail` add `mineRedCost` int default 0 comment "红包埋雷消耗";
alter table `serviceChargePlayerDetail` change `bureau` `arenaBureau` int default 0 comment "竞技场局数";
alter table `serviceChargePlayerDetail` add `boxBureau` int default 0 comment "包厢局数";
alter table `serviceChargePlayerDetail` add `mineRedBureau` int default 0 comment "红包埋雷局数";
alter table `groupArenaCostDiamondDetail` add `roomType` tinyint default 1 comment "房间类型";
alter table `groupArenaCostDiamondDetail` change `arenaUid` `gameUid` bigint default 0 comment "游戏uid";
alter table `groupArenaCostDiamondDetail` drop index `searchTimeAndGroupUid`;
alter table `groupArenaCostDiamondDetail` add index searchTimeAndGroupUidAndRoomType(`time`, `groupUid`, `roomType`);
alter table `groupArenaCostDiamondDaily` change `cost` `arenaCost` int default 0 comment "竞技场消耗";
alter table `groupArenaCostDiamondDaily` add `boxCost` int default 0 comment "包厢消耗";
alter table `groupArenaCostDiamondDaily` add `mineRedCost` int default 0 comment "红包埋雷消耗";
-- TODO 2019-03-30 add end

-- TODO 2019-04-10 add
alter table `arena` add `extra` varchar(2048) default "" comment "额外";
-- TODO 2019-04-10 add end

-- TODO 2019-04-16 add
alter table `arena` add `updateTime` bigint default 0 comment "更新时间";
-- TODO 2019-04-16 add end

-- TODO 2019-04-23 add
alter table `arena` add `leaveArenaValue` int(11) default 0 comment "离开竞技值";
-- TODO 2019-04-23 add end

-- TODO 2019-05-07 add
alter table `hundredArenaBureauRecord` add `bankerPlayerUid` bigint default 0 comment "庄家uid";
alter table `hundredArenaBureauRecord` add `bankerWinValue` int(11) default 0 comment "庄家输赢分数";
alter table `hundredArenaBureauRecord` drop index `searchByArenaUid`;
alter table `hundredArenaBureauRecord` add index searchByArenaUidAndBankerPlayerUid(`arenaUid`, `bankerPlayerUid`);
-- TODO 2019-05-07 add end

-- TODO 2019-05-10 add
alter table `hundredArenaRebRecord` add `rebValue` int(11) default 0 comment "下注金额";
alter table `hundredArenaRebRecord` add `winValue` int(11) default 0 comment "输赢金额";
insert into `assistantWeChat` values ('0', 'yhqp999', '', '', '', '0');
-- TODO 2019-05-10 add end

-- TODO 2019-05-31 add
alter table `player` add `wechat` varchar(255) default '' comment '微信';
-- TODO 2019-05-31 add end

-- TODO 2019-06-12 add
alter table `accountWithdrawType` add `payeeAddress` varchar(255) default "" comment '开户行地址';
alter table `accountWithdrawType` add `payeeLocation` varchar(255) default "" comment '开户行所在地';
-- TODO 2019-06-12 add end

-- TODO 2019-06-19 add
alter table `group` add `autoUpDeputyConf` longtext default "" comment '自动升副帮主设置';
alter table `group` add `quests` longtext  default "" comment '小组任务';
-- TODO 2019-06-19 add end

-- TODO 2019-07-11 add
alter table `questGetRewardRecord` add `startTime` bigint default 0 comment "开始时间";
alter table `questGetRewardRecord` add `endTime` bigint default 0 comment "结束时间";
alter table `questGetRewardRecord` add `period` bigint default 0 comment "周期";
-- TODO 2019-07-11 add end

-- TODO 2019-07-22 add
alter table `group` add `leagueUid` bigint default -1 comment "联盟uid";
alter table `arena` add `ownerType` tinyint default 1 comment "竞技场拥有着";
alter table `serviceChargeAdmin` add `arenaValueByLeague` int default 0 comment "联盟竞技值收益";
alter table `serviceChargeDaily` add `arenaCostByLeague` int default 0 comment "联盟竞技场消耗";
alter table `serviceChargePlayerDaily` add `arenaCostByLeague` int default 0 comment "联盟竞技场消耗";
alter table `serviceChargeArenaDaily` add `bureauByLeague` int default 0 comment "联盟局数";
alter table `serviceChargeArenaDaily` add `costByLeague` int default 0 comment "联盟消耗";
alter table `serviceChargeDetail` add `bureauByLeague` int default 0 comment "联盟局数";
alter table `serviceChargeDetail` add `costByLeague` int default 0 comment "联盟消耗G";
alter table `serviceChargePlayerDetail` add `arenaBureauByLeague` int default 0 comment "联盟竞技场局数";
alter table `serviceChargePlayerDetail` add `arenaCostByLeague` int default 0 comment "联盟竞技场消耗";
alter table `arenaScore` add `ownerType` tinyint default 1 comment "战绩拥有着";
alter table `arenaScore` drop index `searchPlayerUid`;
alter table `arenaScore` add index searchPlayerUid(`playerUid`, `ownerType`, `state`);
alter table `arenaScore` drop index `searchGroupUidAndPlayerUid`;
alter table `arenaScore` add index searchGroupUidAndPlayerUid(`groupUid`, `ownerType`, `playerUid`, `state`);
alter table `groupCurrencyRecord` add `decServiceValueByLeague` int default 0 comment "当前减少联盟服务费";
alter table `serviceChargeRecord` add `serviceChargeValueByLeague` int default 0 comment "修改联盟服务费";
alter table `player` add `ownerLeagueCnt` int default 0 comment "拥有联盟数量";
-- TODO 2019-07-22 add end

-- TODO 2019-07-30 add
alter table `serviceChargeAdmin` add `ownerType` tinyint default 1 comment '服务费拥有着';
alter table `group` add `serviceChargeDivideAdminByLeague` int default 0 comment "群联盟管理费";
-- TODO 2019-07-31 add end

-- TODO 2019-09-24 add
alter table `league` add `gameDesc` varchar(1014) default "" comment '联盟推荐游戏描述';
-- TODO 2019-09-24 add end

-- TODO 2019-09-26 add
alter table `boxRoomScore` add `mark` int default 0 comment '某一条战况是否被标记';
-- TODO 2019-09-26 add end

-- TODO 2019-10-18 add
alter table `boxRoomScore` add `roomType` int default 0 comment '房间类型 1.可少人 2.2人场 3.3人场';
alter table `leagueRoomScore` add `roomType` int default 0 comment '房间类型 1.可少人 2.2人场 3.3人场';
-- TODO 2019-10-18 add end

-- TODO 2019-10-29 add
CREATE TABLE `moneyExpendRecord` (
  `uid` bigint(20) NOT NULL DEFAULT '0' COMMENT 'uid',
  `fromUid` bigint(20) DEFAULT '-1' COMMENT '亲友圈id、联盟id',
  `playerUid` bigint(20) DEFAULT '-1' COMMENT '玩家ID',
  `operatorUid` bigint(20) DEFAULT '-1' COMMENT '操作人id',
  `value` bigint(20) DEFAULT '0' COMMENT '房卡',
  `expendType` bigint(20) DEFAULT '0' COMMENT '消耗或增长类型',
  `expendTime` bigint(20) DEFAULT '0' COMMENT '消耗或增长时间',
  `roomType` bigint(20) DEFAULT '0' COMMENT '类型（0别的、1大厅、2亲友圈、3联盟）',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- TODO 2019-10-29 add end

-- add  20191031
DROP TABLE IF EXISTS `playerMoneyConsumeRecord`;
CREATE TABLE `playerMoneyConsumeRecord` (
  `uid` bigint(20) NOT NULL DEFAULT '0' COMMENT 'uid',
  `playerUid` bigint(20) DEFAULT '-1' COMMENT '玩家ID',
  `value1` bigint(20) DEFAULT '0' COMMENT '大厅消耗=sum(大厅房卡消耗+大厅房卡消耗返还)',
  `value2` bigint(20) DEFAULT '0' COMMENT '亲友圈消耗（亲友圈房卡消耗+亲友圈房卡消耗返还）',
  `value3` bigint(20) DEFAULT '0' COMMENT '联盟消耗（大联盟房卡消耗+大联盟房卡消耗返还）',
  `monthValue1` longtext COMMENT '大厅月统计消耗，格式：[''startTime'':开始记录时间,''time'':最后更新时间,[消耗数量,消耗数量,...]]',
  `monthValue2` longtext COMMENT '亲友圈月统计消耗，格式：[''startTime'':开始记录时间,''time'':最后更新时间,[消耗数量,消耗数量,...]]',
  `monthValue3` longtext COMMENT '联盟月统计消耗，格式：[''startTime'':开始记录时间,''time'':最后更新时间,[消耗数量,消耗数量,...]]',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='玩家房卡消耗数量统计';

-- add 20191107
ALTER TABLE `player` ADD COLUMN `bankCard`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡号' AFTER `ownerPavilionCnt`;
ALTER TABLE `player` ADD COLUMN `bankCardHolder`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡持卡人' AFTER `bankCard`;

-- add 20191114
ALTER TABLE `league` ADD COLUMN `rewardValueDivide`  longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '获取比例' AFTER `openJoin`;

ALTER TABLE `leagueValueRecord` ADD COLUMN `fromType` int(11) NULL DEFAULT NULL COMMENT '记录产生来源\r\n0兑换成竞技值扣除\r\n1联盟管理费添加\r\n2群管理费添加\r\n3一条线抽成添加\r\n4直属抽成添加\r\n5其他抽成都是群主的' AFTER `optPlayerUid`;
ALTER TABLE `league` ADD COLUMN `quests`  longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '小组任务(满足局数领取竞技分活动)' AFTER `rewardValueDivide`;
ALTER TABLE `questGetRewardRecord` ADD COLUMN `leagueUid`  bigint(20) NULL DEFAULT NULL COMMENT '联盟id' AFTER `uid`;


