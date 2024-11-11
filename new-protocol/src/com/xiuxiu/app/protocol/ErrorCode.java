package com.xiuxiu.app.protocol;

import com.xiuxiu.core.net.protocol.IErrorCode;

public enum ErrorCode implements IErrorCode {
    OK(0, "成功"),
    FAIL(1, "失败"),

    // 服务器内部错误
    SERVER_INTERNAL_ERROR(                                  0x00010001, "服务器内部错误"),
    SERVER_DB_ERROR(                                        0x00010002, "数据库错误"),
    SERVER_NET_ERROR(                                       0x00010003, "网络错误"),
    SERVER_NET_TIMEOUT(                                     0x00010004, "网络超时"),
    SERVER_BUSY(                                            0x00010005, "系统繁忙，请稍候重试"),

    // 通用提示
    GM_(                                                    0x00020001, "通用"),
    GM_INVALID_OPERATE(                                     0x00020002, "无效操作"),
    GM_INVAILD_PROTOCOL(                                    0x00020003, "无效协议"),
    GM_NOT_SUPPORT(                                         0x00020004, "不支持"),

    // 通用请求
    REQUEST_INVALID(                                        0x00030001, "无效请求"),
    REQUEST_INVALID_DATA(                                   0x00030002, "无效请求数据"),
    REQUEST_OPERATE_ERROR(                                  0x00030003, "请求操作错误"),
    REQUEST_NO_DEFAULT_ASSISTANT(                           0x00030004, "未设置默认区域客服微信"),
    REQUEST_INVALID_TOKEN(                                  0x00030005, "请求token无效"),
    REQUEST_TYPE_NULL(                                      0x00030006, "类型错误请求无效"),
    REQUEST_TAKE_FAIL(                                      0x00030007, "下家报单,出单张必须为手牌最大牌"),
    REQUEST_TAKE_FAIL_MUSTTHREE(                            0x00030008, "必须出含有黑三的牌"),

    // 账号
    ACCOUNT_EXISTS(                                         0x00040001, "账号已经存在"),
    ACCOUNT_PAY_PASSWORD_NOT_IN(                            0x00040002, "账号未设置支付密码"),
    ACCOUNT_WALLET_PAY_TYPE_NOT_EXISTS(                     0x00040003, "钱包支付方式不存在"),
    ACCOUNT_WALLET_PAY_MONEY_LESS(                          0x00040004, "钱包支付金额必须大于0"),
    ACCOUNT_WALLET_WITHDRAW_TYPE_NOT_EXISTS(                0x00040005, "钱包支付方式不存在"),
    ACCOUNT_WALLET_PAY_PASSWORD_NUMBER(                     0x00040006, "支付密码必须为数字"),
    ACCOUNT_WALLET_WITHDRAW_MONEY_NUM(                      0x00040007, "钱包提现金额必须为50的倍数"),
    ACCOUNT_WALLET_WITHDRAW_LACK(                           0x00040008, "提现金额不足"),
    ACCOUNT_WALLET_WITHDRAW_TYPE_NULL(                      0x00040009, "钱包提现方式为空"),
    ACCOUNT_PAY_PASSWORD_VERIFY_FAIL(                       0x0004000a, "支付密码验证失败"),
    ACCOUNT_WITHDRAW_INVALID_VALUE(                         0x0004000b, "非法的额度"),
    ACCOUNT_WITHDRAW_TYPE_NOT_EXISTS(                       0x0004000c, "支付方式不存在"),
    ACCOUNT_BAN_FAIL(                                       0x0004000e, "封号/解封失败"),
    ACCOUNT_WITHDRAW_TYPE_NOT_CHOOSE(                       0x0004000f, "请选择收款方式"),
    ACCOUNT_WALLET_WITHDRAW_TYPE_NOT_AVAILABLE(             0x00040010, "该提现方式不可用"),
    ACCOUNT_WALLET_WITHDRAW_TYPE_NOT_YOURS(                 0x00040011, "该提现方式不属于你"),
    ACCOUNT_WALLET_ENABLE_WITHDRAW_LACK(                    0x00040012, "可提现金额不足"),
    ACCOUNT_WITHDRAW_FINANCE_NOT_ONLINE(                    0x00040013, "当前无在线平台财务，请联系客服"),
    ACCOUNT_NOT_PLATFORM_FINANCE(                           0x00040014, "当前用户没有平台财务权限"),
    ACCOUNT_WITHDRAW_RECORD_NOT_EXIST(                      0x00040015, "提现记录不存在"),
    ACCOUNT_WITHDRAW_IN_GAME_(                      		0x00040016, "当前在游戏中无法提现"),

    // 玩家自己
    PLAYER_EXISTS(                                          0x00050001, "玩家已经存在"),
    PLAYER_MATCH_IN(                                        0x00050002, "玩家在比赛场中"),
    PLAYER_ARENA_IN(                                        0x00050003, "玩家在竞技场中"),
    PLAYER_BOX_IN(                                          0x00050004, "玩家在包厢中"),
    PLAYER_ROOM_IN(                                         0x00050005, "玩家在房间中"),
    PLAYER_MATCH_IN_OTHER(                                  0x00050006, "玩家在其他比赛场中"),
    PLAYER_ARENA_IN_OTHER(                                  0x00050007, "玩家在其他竞技场中"),
    PLAYER_BOX_IN_OTHER(                                    0x00050008, "玩家在其他包厢中"),
    PLAYER_ROOM_IN_OTHER(                                   0x00050009, "玩家在其他房间中"),
    PLAYER_MATCH_NOT_IN(                                    0x0005000a, "玩家不在比赛场中"),
    PLAYER_ARENA_NOT_IN(                                    0x0005000b, "玩家不在竞技场中"),
    PLAYER_BOX_NOT_IN(                                      0x0005000c, "玩家不在包厢中"),
    PLAYER_ROOM_NOT_IN(                                     0x0005000d, "玩家不在房间中"),
    PLAYER_MATCH_ROOM_IN(                                   0x0005000e, "玩家在比赛场房间中"),
    PLAYER_ARENA_ROOM_IN(                                   0x0005000f, "玩家在竞技场房间中"),
    PLAYER_BOX_ROOM_IN(                                     0x00050010, "玩家在包厢房间中"),
    PLAYER_MATCH_ROOM_NOT_IN(                               0x00050011, "玩家不在比赛场房间中"),
    PLAYER_ARENA_ROOM_NOT_IN(                               0x00050012, "玩家不在竞技场房间中"),
    PLAYER_BOX_ROOM_NOT_IN(                                 0x00050013, "玩家不在包厢房间中"),
    PLAYER_GROUP_NOT_IN(                                    0x00050014, "玩家不在群中"),
    PLAYER_CLUB_LIMIT(                                     0x00050015, "拥有亲友圈上限"),
    PLAYER_ARENA_LIMIT(                                     0x00050016, "拥有竞技场上限"),
    PLAYER_SEARCH_ERR(                                      0x00050017, "搜索玩家错误"),
    PLAYER_HAVE_NO_QRCODE(                                  0x00050018, "未上传收款二维码"),
    PLAYER_NOT_CHANGE_ICON_0(                               0x00050019, "你充值没有达到100以上，无法换头像"),
    PLAYER_NOT_CHANGE_NAME_0(                               0x0005001a, "你充值没有达到100以上，不能换昵称"),
    PLAYER_SEARCH_TIMES_LIMIT(                              0x0005001b, "你搜索次数已满，请联系客服"),
    PLAYER_SEARCH_ERROR_LIMIT(                              0x0005001c, "你搜索错误此时已满，请联系客服"),
    PLAYER_PRIVILEGE_NO_LEAGUE(                             0x0005001d, "没有创建联盟权限"),
    PLAYER_PRIVILEGE_NO_GROUP(                              0x0005001e, "没有创建群权限"),
    PLAYER_LEAGUE_LIMIT(                                    0x0005001f, "拥有联盟上限"),
    PLAYER_LEAGUE_NOT_LEVEL_GROUP(							0x00050020, "拥有联盟无法解散群"),
    PLAYER_LEAGUE_CONVERSION(							    0x00050021, "玩家以兑换过"),


    // 好友
    FRIEND_EXISTS(                                          0x00060001, "好友已经存在"),

    // 亲友圈
    GROUP_EXISTS(                                           0x00070001, "亲友圈已经存在"),
    GROUP_MEMBER_MAX(                                       0x00070002, "亲友圈成员已满"),
    GROUP_ARENA_LESS_MINE_RED(                              0x00070003, "竞技分必须大于陪包金额"),
    GROUP_MEMBER_NOT_FOUND(                                 0x00070004, "成员不存在"),
    GROUP_UPLINE_ERROR(                                     0x00070005, "上线错误"),
    GROUP_UPLINE_CYCLE(                                     0x00070006, "上线错误, 循环上线"),
    GROUP_QUEST_GET_REWARD_NOT_CONDITION(                   0x00070007, "比赛场组任务领取奖励次数条件不足"),
    GROUP_CHIEF_NOT(                                        0x00070008, "不是圈主"),
    GROUP_LEAGUE_HAS(                                       0X00070009, "已经有联盟"),
    GROUP_NOT_LEAGUE(                                       0x0007000a, "没有联盟"),
    GROUP_MERGE_FAIL(                                       0x0007000b, "合亲友圈失败"),
    FROM_GROUP_NOT_EXIST(                                   0x0007000c, "副圈主不存在"),
    TO_GROUP_NOT_EXIST(                                     0x0007000d, "主亲友圈不存在"),
    FROM_GROUP_SAME_WITH_TO_GROUP(                          0x0007000e, "主亲友圈不能和副亲友圈相同"),
    NEW_GROUP_EXIST(                                        0x0007000f, "新亲友圈ID已存在"),
    GROUP_SPLIT_FAIL(                                       0x00070010, "分亲友圈失败"),
    GROUP_MEMBER_LENGTH_NULL(                               0x00070011, "分亲友圈成员不能为空"),
    GROUP_OWNER_CANNOT_SPLIT(                               0x00070012, "亲友圈主无法被分出"),
    GROUP_HAS(                                              0x00070013, "已经有亲友圈"),
    GROUP_REOMMEND(                                         0x00070014, "进圈失败，只能通过主动搜索亲友圈ID加入"),
    GROUP_CHANGE_ARENA_DIVIDE_NO_NEED(                      0x00070015, "普通成员无法修改一条线奖励分"),


    // 竞技场
    ARENA_EXISTS(                                           0x00080001, "竞技场已经存在"),
    ARENA_NOT_EXISTS(                                       0x00080002, "竞技场不存在"),
    ARENA_ALREADY_READY(                                    0x00080003, "已经准备"),
    ARENA_MATCH_PLAYER_MAX(                                 0x00080004, "竞技场该匹配队列人员已满"),
    ARENA_MATCH_COOL(                                       0x00080005, "竞技场匹配冷却中"),
    ARENA_MATCH_IN(                                         0x00080006, "在竞技场匹配队列中"),
    ARENA_MATCH_NOT_IN(                                     0x00080007, "不在竞技场匹配队列中"),
    ARENA_MATCH_STATE_NOT_READY(                            0x00080008, "竞技场匹配队列不是准备状态"),
    ARENA_MATCH_ALREADY_READY(                              0x00080009, "竞技场匹配队列已经准备"),
    ARENA_MATCH_NOT_EXISTS(                                 0x0008000a, "竞技场匹配房间不存在"),
    ARENA_MATCH_WATCH_ERR(                                  0x0008000b, "竞技场观察失败"),
    ARENA_UP_BANKER_FAIL(                                   0x0008000c, "竞技场上庄失败"),
    ARENA_NOTE_FAIL(                                        0x0008000e, "竞技场下注失败"),
    ARENA_NOT_START(                                        0x0008000f, "竞技场没开始"),
    ARENA_NOT_WATCH(                                        0x00080010, "不在竞技场观察中"),
    ARENA_ALREADY_SIT_DOWN(                                 0x00080011, "已经坐下"),
    ARENA_ALREADY_START(                                    0x00080012, "已经开始"),
    ARENA_REB_FAIL(                                         0x00080013, "押注失败"),
    ARENA_ALREADY_REB(                                      0x00080014, "已经押注"),
    ARENA_HBML_OVER(                                        0x00080015, "红包结束"),
    ARENA_HBML_ISDRAW(                                      0x00080016, "红包已经领取"),
    ARENA_LEAVE_FAIL_LESS_MIN_BUREAU(                       0x00080017, "竞技场最低局数限制"),
    ARENA_HUNDRED_REB_LIMIT_10000(                          0x00080018, "下注已达到最大值,不能再下注"),
    ARENA_HUNDRED_NOT_IN(                                   0x00080019, "不在百人场中"),
    ARENA_HUNDRED_VIP_SEAT_LIMIT(                           0x0008001a, "竞技分不满VIP竞技分限制,请带够竞技分,再来入座"),
    ARENA_HUNDRED_VIP_SEAT_HAS(                             0x0008001b, "该座位已经有人了, 请选其他空闲座位"),
    ARENA_MAX(                                              0x0008001C, "创建竞技场上限"),
    ARENA_HUNDRED_ALREADY_REB(                              0x0008001d, "已经押注,无法离开"),
    ARENA_HUNDRED_CURRENT_BANKER(                           0x0008001e, "正在上庄,无法离开"),


    // 比赛
    MATCH_EXISTS(                                           0x00080001, "比赛已经存在"),

    // 房间
    ROOM_EXISTS(                                            0x00090001, "房间已经存在"),
    ROOM_FINISH(                                            0x00090002, "房间已经结束"),
    ROOM_MIDDLE_JOIN(                                       0x00090003, "房间中途加入"),
    ROOM_INDEX_NOT_EMPTY(                                   0x00090004, "房间该位置已经有人"),
    ROOM_NOT_WATCH(                                         0x00090005, "不在该房间围观"),
    ROOM_NOT_START(                                         0x00090006, "房间还没开始"),
    ROOM_NOT_ACTION(                                        0x00090007, "房间没动作"),
    ROOM_ACTION_ERR(                                        0x00090008, "房间动作错误"),
    ROOM_NOT_CUR_PLAYER(                                    0x00090009, "不是当前操作玩家"),
    ROOM_ALREADY_SHOW_OFF(                                  0x0009000a, "已经炫耀过了"),
    ROOM_NOT_LOOP_SIT_UP(                                   0x0009000b, "没满足最低轮数无法站起"),
    CHECK_IP_SAME(                                          0x0009000C, "不能加入"),
    CHECK_IP_SAME_STDOWN(                                   0x0009000d, "IP相同无法坐下"),
    FORBID_SAME_JOIN(                                       0x0009000e, "房间受限,无法加入"),

    // 房间--麻将相关
    ROOM_MJ_INVALID_CARD(                                   0x00090100, "非法牌"),
    ROOM_MJ_HAND_CARD_NOT_ENOUGH(                           0x00090101, "手牌不足"),
    ROOM_MJ_ALREADY_LIANG_PAI(                              0x00090102, "已经亮牌"),
    ROOM_LEAVE_LIMIT(                                       0x00090103, "离开限制"),
    ROOM_MJ_AUTO_TAKE(                                      0x00090104, "主动打"),
    ROOM_MJ_OVER(                                           0x00090105, "已经结束"),
    // 房间-牌九
    ROOM_NOT_IN(                                            0x00090200, "不在房间里"),
    ROOM_ALREADY_READY(                                     0x00090201, "已经准备过了"),
    ROOM_BUSY(                                            0x00090202, "房间爆满"),

    // 邮件
    MAIL_EXISTS(                                            0x000a0001, "邮件已经存在"),

    // 聊天
    CHAT_EXISTS(                                            0x000b0001, "聊天已经存在"),

    // 楼层
    FLOOR_NAME_NULL(                                        0x000c0001, "楼层名字为空"),
    FLOOR_TYPE_ERR(                                         0x000c0002, "楼层类型错误"),
    FLOOR_FLOOR_TYPE_ERR(                                   0x000c0003, "楼层分类错误"),
    FLOOR_NOT_EXISTS(                                       0x000c0003, "楼层不存在"),
    FLOOR_NOT_PRIVILEGE(                                    0x000c0004, "权限不足，无法操作"),
    FLOOR_BOX_MAX(                                          0x000c0005, "玩法桌已达到上限，无法创建"),
    FLOOR_MAX(                                              0x000c0006, "楼层已达到上限，无法创建"),

    // 联盟
    LEAGUE_NAME_LONG(                                       0x000d0001, "联盟名过长"),
    LEAGUE_DESC_LONG(                                       0x000d0002, "联盟介绍过长"),
    LEAGUE_ICON_LONG(                                       0x000d0003, "联盟图标过长"),
    LEAGUE_NOT_EXISTS(                                      0x000d0004, "联盟不存在"),
    LEAGUE_JOIN_FAIL(                                       0x000d0005, "加入联盟失败"),
    LEAGUE_LEAVE_FAIL(                                      0x000d0006, "离开联盟失败"),
    LEAGUE_NOT_LEADER(                                      0x000d0007, "不是联盟盟主"),
    LEAGUE_KILL_MEMBER_FAIL(                                0x000d0008, "剔除成员失败"),
    LEAGUE_KILL_LEADER_LAST_GROUP_FAIL(						0x000d0009, "无法删除盟主最后一个亲友圈"),
    LEAGUE_NOT_ENOUGH(                                      0x000d000a, "星币不足"),
    LEAGUE_GAME_DESC_LONG(                                  0x000d000b, "联盟主打游戏描述过长"),
    LEAGUE_NAME_REPETITION(                                 0x000d000c, "联盟名称重复"),
    LEAGUE_NEED_GROUP_CHIEF(                                0x000d000d, "必须是圈主"),
    LEAGUE_NOT_IN(                                          0x000d000e, "不在联盟中"),
    LEAGUE_OPEN_JOIN_FALL(                                  0x000d000f, "是否开放加入失败"),
    LEAGUE_NOT_OPEN_JOIN(                                   0x000d0010, "该联盟被设置为不可见"),

    PAVILION_NOT_EXISTS(                                     0x000e0001, "雀友馆不存在"),
    PAVILION_EXISTS(                                         0x000e0002, "亲友圈已有雀友馆，馆主不是群主"),
    PAVILION_LAST_GROUP_FAIL(                                0x000e0003, "馆主不能退出雀友馆"),
    PAVILION_LEAVE_FAIL(                                      0x000e0004, "离开雀友馆失败"),
    PAVILION_ICON_LONG(                                      0x000e0005, "雀友馆图标过长"),
    PAVILION_NAME_REPETITION(                                0x000e0006, "雀友馆名称重复"),

    // 新版大唐
    CLUB_GOLD_NOT_EXISTS(                                       10000,"比赛场不存在"),
    CLUB_CARD_NOT_EXISTS(                                       10001,"亲友圈不存在"),
    CLUB_CARD_NOT_TYPE(                                         10002,"该亲友圈类型不对"),    CLUB_APPLY_REJECT(                                          10003,"拒绝"),
    CLUB_APPLY_CONSENT(                                         10004,"同意"),
    CLUB_PLAYER_NOT_DIAMOND(                                    10005,"玩家房卡不足"),
    CLUB_OWNER_NOT_ARENA_VALUE(                                 10006,"竞技分不足"),
    CLUB_OWNER_NOT(                                             10007,"圈主不存在"),
    CLUB_PLAYER_CONVERT(                                        10008,"玩家以兑换过"),
    CLUB_TYPE_NOT(                                              10009,"类型不匹配"),
    CLUB_NOT_PLAYER(                                            10010,"玩家不在亲友圈中"),
    CLUB_NOT_LEAVE_CLUB(                                        10011,"已经合并俱乐部不能解散俱乐部"),
    CLUB_OWNER_NOT_CONVERT(                                     10012,"圈主不能兑换"),

    REPEAT_OPERATE(7, "重复操作"),
    FREQUENT_OPERATION(8, "频繁操作"),
    GENERATE_AUTH_CODE_FAIL(9, "生成验证码失败"),
    AUTH_CODE_INVALID(10, "验证码无效"),
    AUTH_CODE_ERROR(11, "验证码错误"),
    ALREADY_BIND_PHONE(12, "已经绑定过手机号了"),
    PHONE_INVALID(13, "手机号无效"),
    IDENTITY_CARD_INVALID(14, "身份证无效"),
    DISCARD_PROTOCOL(15, "废弃协议"),
    MONEY_NOT_EXISTS(16, "货币不存在"),
    SYSTEM_NOT_OPEN(17, "系统暂未开放"),
    PHONE_ALREADY_USED(18, "该手机号已被绑定"),
    OLD_PHONE_INVALID(19, "原手机号错误"),

    ACCOUNT_REGISTERING(100, "账号正在注册"),
    ACCOUNT_ALREADY_EXISTS(101, "账号已经存在"),
    ACCOUNT_USERNAME_OR_PASSWD_ERROR(102, "账号用户名或者密码错误"),
    ACCOUNT_NOT_EXISTS(102, "账号不存在"),
    ACCOUNT_PASSWD_ERROR(103, "密码不对"),
    ACCOUNT_PLATFORM_TOKEN_NULL(104, "平台唯一标识为空"),
    ACCOUNT_PHONE_NULL(105, "手机号为空"),
    ACCOUNT_PASSWD_NULL(106, "密码为空"),
    ACCOUNT_HAS_BEEN_BANNED(107, "账号已被封禁"),
    ACCOUNT_MODIFY_NO_NEED_PAY_PASSWORD_FAIL(108, "修改免密支付状态失败"),
    ACCOUNT_GROUP_NAME_NULL(109, "亲友圈名称不能为空"),
    ACCOUNT_GROUP_GAME_DESC_NULL(110, "亲友圈游戏描述不能为空"),
    ACCOUNT_GROUP_PLAYER_FORBIDO_LAY(111, "玩家受限，无法游戏"),
    ACCOUNT_GROUP_NAME_NOT(112, "亲友圈名称不合法"),
    ACCOUNT_GROUP_ISAPPLY(113, "玩家已经在申请列表中"),
    ACCOUNT_GROUP_NAME_LENGTH(114, "亲友圈名称长度不符合"),

    PLAYER_NO_LOGIN(200, "玩家没登陆"),
    PLAYER_ALREADY_LOGIN(201, "玩家已经登陆"),
    PLAYER_LOGINING(202, "玩家正在登陆"),
    PLAYER_BUSY(203, "玩家正忙"),
    PLAYER_ROOM_CREATEING(204, "正在创建房间中"),
    PLAYER_ROOM_LEAVEING(206, "正在离开房间中"),
    PLAYER_ROOM_JOINING(207, "正在加入房间中"),
    PLAYER_NOT_EXISTS(208, "玩家不存在"),
    PLAYER_ALREADY_OPERATE(210, "玩家已经操作过了"),
    PLAYER_ALREADY_OFFLINE(211, "玩家已经离线"),
    PLAYER_ALREADY_ONLINE(212, "玩家已经上线"),
    PLAYER_RECOMMENDED(213, "已经推荐过了"),
    PLAYER_LACK_DIAMOND(214, "房卡不足"),
    PLAYER_PRIVILEGE_NOT_ADD_FRIEND(215, "没有添加好友权限"),
    PLAYER_PRIVILEGE_NOT_CREATE_GROUP(216, "没有创建亲友圈的权限"),
    PLAYER_MUTE(217, "你被禁言"),
    PLAYER_GROUP_NAME_REPETITION(218, "亲友圈名称重复"),
    PLAYER_GROUP_CHANGE_INFO_FAIL(219,"修改亲友圈信息失败"),
    PLAYER_GROUP_CHANGE_INFO_DESC(220,"没有修改亲友圈信息权限"),
    PLAYER_GROUP_CHANGE_LIST_INFO_DESC(221,"没有申请列表权限"),
    GROUP_CHANGE_LIST_ANNOUNCEMENT(222,"公告信息未填"),
    GROUP_CREATE_DIAMOND_INSUFFICIENT(223,"房卡不足, 无法创建亲友圈"),
    GROUP_APPLY_OPERATE_REJECT(224,"您被拒绝加入亲友圈"),
    PLAYER_PRIVILEGE_NOT_CREATE_GOLD(225,"没有创建比赛场的权限"),
    PLAYER_CLUB_LIMIT_GOLD(226,"拥有比赛场上限"),
    ACCOUNT_GOLD_NAME_NULL(227,"比赛场名称不能为空"),
    ACCOUNT_GOLD_NAME_NOT(228,"比赛场名称不合法"),
    ACCOUNT_GOLD_GAME_DESC_NULL(229,"比赛场游戏描述不能为空"),
    PLAYER_GOLD_NAME_REPETITION(230,"比赛场名称重复"),
    GOLD_IS_NOT_EXISTS(231,"比赛场不存在"),
    PLAYER_SEARCH_LIMIT_TODAY(232,"达到每日搜索玩家上限"),
    CLUB_APPLY_OPERATE_REJECT_GOLD(233,"您被拒绝加入比赛场"),


    ROOM_CREATE_FAIL(300, "创建房间失败"),
    ROOM_LEAVE_FAIL(301, "离开房间失败"),
    ROOM_NOT_EXISTS(302, "房间不存在"),
    ROOM_JOIN_FAIL(303, "加入房间失败"),
    ROOM_PLAYER_FULL(304, "房间人员已满"),
    ROOM_ALREADY_START(305, "房间已经开始"),
    ROOM_NOT_OWNER(307, "不是房主"),
    ROOM_POKER_COW_ALREADY_ROB_BANKER(309, "已经抢过庄了"),
    ROOM_POKER_COW_ALREADY_REBET(310, "已经下过注了"),
    ROOM_POKER_COW_BANKER_NOT_REBET(311, "庄家无法下注"),
    ROOM_POKER_ALREADY_DISCARD(312, "已经弃牌了"),
    ROOM_POKER_ALREADY_COMPARE(313, "已经比牌了"),
    ROOM_POKER_FIRST_LOOP_CANT_LOOK(314, "首轮无法看牌"),
    ROOM_POKER_ALREADY_DISCARD_NOT_FOLLOW_NOTE(315, "已经弃牌,无法跟注"),
    ROOM_POKER_ALREADY_DISCARD_NOT_COMPARE(316, "对方已经弃牌,无法比牌"),
    ROOM_POKER_ALREADY_DISCARD_NOT_ADD_NOTE(317, "已经弃牌,无法加注"),
    ROOM_POKER_ALREADY_COMPARE_NOT_FOLLOW_NOTE(318, "已经比牌,无法跟注"),
    ROOM_POKER_ALREADY_COMPARE_NOT_COMPARE(319, "对方已经比牌,无法比牌"),
    ROOM_POKER_ALREADY_COMPARE_NOT_ADD_NOTE(320, "已经比牌,无法加注"),
    ROOM_POKER_ALREADY_LOOK(321, "已经看过牌了"),
    ROOM_LEAVE(322, "暂时离开"),
    ROOM_LEAVE_REPORT(323, "离开有战报"),
    ROOM_POKER_FGF_COLLUDER(324, "防勾手不可比牌"),
    ROOM_POKER_THIRTEEN_POUR_WATER(325, "倒水"),
    ROOM_DISSOLVE_MAX(326,"申请解散次数上限"),


    MAHJONG_NOT_CURPLAYER(401, "不是当前出牌玩家"),
    MAHJONG_INVALID_TASK_CARD(402, "出非法牌"),

    FRIEND_APPLY_LIST_NULL(501, "好友申请列表为空"),
    FRIEND_APPLY_NOT_EXISTA(502, "申请着不存在"),
    FRIEND_DONT_ADD_SELF(503, "不能添加自己为好友"),
    FRIEND_ALREADY(504, "已经是好友"),
    FRIEND_NEARBYNOTE_ALREADY_DELETE(505, "附近留言已经删除"),
    FRIEND_NEARBYNOTE_ALREADY_READ(506, "附近留言已经阅读"),
    FRIEND_OTHER_LIMIT(507, "对方好友已满"),
    FRIEND_LIMIT(508, "好友已满"),

    GROUP_NOT_EXISTS(601, "亲友圈不存在"),
    GROUP_NOT_IN(602, "不在亲友圈里"),
    GROUP_ALREADY_IN(603, "已经在亲友圈里"),
    GROUP_NOT_PRIVILEGE_ADD(604, "没有添加成员权限"),
    GROUP_NOT_PRIVILEGE_DEL(605, "没有删除成员权限"),
    GROUP_NOT_PRIVILEGE_CHANGE_DESC(606, "没有修改亲友圈描述权限"),
    GROUP_NOT_PRIVILEGE_CHANGE_ICON(607, "没有修改亲友圈图标权限"),
    GROUP_NOT_PRIVILEGE_CHANGE_NAME(608, "没有修改亲友圈名字权限"),
    GROUP_NOT_PRIVILEGE_CHANGE_PRIVILEGE(609, "没有修改权限"),
    GROUP_NOT_PRIVILEGE_ADD_MANAGER(610, "没有添加管理员权限"),
    GROUP_NOT_PRIVILEGE_DEL_MANAGER(611, "没有删除管理员权限"),
    GROUP_NOT_PRIVILEGE_TRANSFER_CHIEF(612, "没有转让亲友圈主权限"),
    GROUP_NOT_PRIVILEGE_COPY(613, "没有复制亲友圈权限"),
    GROUP_NOT_PRIVILEGE_SET_UPLINE(614, "没有设置成员上线权限"),
    GROUP_NOT_PRIVILEGE_SET_ARENA_VALUE(615, "没有设置成员竞技场积分权限"),
    GROUP_NOT_PRIVILEGE_CLEAR_ARENA_SCORE(616, "没有清理成员竞技场积分权限"),
    GROUP_NOT_PRIVILEGE_CREATE_BOX(617, "没有创建包厢权限"),
    GROUP_NOT_PRIVILEGE_CLOSE_BOX(618, "没有关闭包厢权限"),
    GROUP_NOT_PRIVILEGE_CHANGE_LIKE(619, "没有修改点赞开关权限"),
    GROUP_NOT_PRIVILEGE_CHANGE_BOX_NAME(620, "没有修改包厢名字权限"),
    GROUP_NOT_PRIVILEGE_MANAGER_SCORE(621, "没有管理战绩权限"),
    GROUP_NOT_PRIVILEGE_MANAGER_SERVICE_CHARGE(622, "没有管理返利权限"),
    GROUP_NOT_PRIVILEGE_MUTE(623, "没有禁言权限"),
    GROUP_NOT_PRIVILEGE_DEL_CHAT(624, "没有删除亲友圈聊天权限"),
    GROUP_NOT_PRIVILEGE_RANK_ARENA(625, "没有竞技场排行权限"),
    GROUP_OTHER_OPERATE(626, "其他管理员正在操作"),
    GROUP_BOX_MAX(627, "包厢已达上限"),
    GROUP_BOX_NOT_EXISTS(628, "该包间已被关闭"),
    GROUP_BOX_CLOSE(629, "包厢关闭"),
    GROUP_BOX_ROOM_EXISTS(630, "包厢房间已经存在"),
    GROUP_BOX_ALREADY_LIKE(631, "已经赞过了"),
    GROUP_BOX_WINNER_UNLIKE(632, "赢家还没有点赞"),
    GROUP_CHIEF_LACK_DIAMOND(633, "房卡不足, 请联系管理员"),
    GROUP_BOX_SCORE_NOT_EXISTS(634, "包厢战绩不存在"),
    GROUP_ALL_ARENA_LESS(635, "总竞技分必须大于500"),
    GROUP_ARENA_LESS_BANK_ARENA(636, "存入银行最大值不能大于当前竞技分"),
    GROUP_BANK_ARENA_LESS_ARENA(637, "取出银行最大值不能大于当前银行竞技分"),
    GROUP_NOT_PRIVILEGE_CHANGE_ANNOUNCEMENT(638, "没有修改亲友圈公告权限"),
    GROUP_NOT_PRIVILEGE_GET_INVITATIONS(639, "没有查看邀请列表的权限"),
    GROUP_NOT_PRIVILEGE_CHANGE_SHARE_CONF(640, "没有设置分享参数的权限"),
    GROUP_NOT_PRIVILEGE_SET_FINANCE(641, "没有设置财务权限"),
    GROUP_FINANCE_IMAGE_NOT_EXISTS(642, "财务上分截图不能为空"),
    GROUP_FINANCE_NOT_EXISTS(643, "该亲友圈未设置财务"),
    GROUP_FINANCE_RECORD_NOT_EXISTS(644, "财务上下分记录不存在"),
    GROUP_FINANCE_ARENA_VALUE_NOT_ENOUGH(645, "玩家竞技分不足，下分失败"),
    GROUP_NOT_MANAGER(646, "查询对象不是亲友圈管理"),
    GROUP_FINANCE_RECORD_USER_NOT_MATCH(647, "财务上下分记录用户不匹配"),
    GROUP_FINANCE_RECORD_FINACE_NOT_MATCH(648, "财务上下分记录财务不匹配"),
    GROUP_FINANCE_RECORD_INVALID_STATUS(649, "财务订单状态不合法"),
    GROUP_NOT_PRIVILEGE_SEND_ARENA_VALUE(650, "没有赠送竞技分权限"),
    GROUP_PLAYER_ARENA_VALUE_NOT_ENOUGH(651, "竞技分不足，请充值"),
    GROUP_PLAYER_RECEIVE_ARENA_VALUE_FAIL(652, "接收赠送的竞技分失败"),
    GROUP_FINANCE_REMAIN_LIMIT(653, "当天额度已满请联系圈主"),
    GROUP_QUEST_NOT_PRIVILEGE_MODIFY(654, "没有修改任务权限"),
    GROUP_QUEST_NOT_FIND(655, "任务已经失效"),
    GROUP_QUEST_REWARD_REPEAT(656, "任务重复领奖"),
    GROUP_CHIEF_LACK_WALLET(657, "圈主钱包余额不足"),
    GROUP_NOT_PRIVILEGE_AUTO_MODE(658, "没有自动托管权限"),
    GROUP_CUSTOM_ROOM_EXISTS(659, "已开启自由组局"),
    GROUP_LEADER_ARENA_VALUE_NOT_ENOUGH(660, "圈主竞技分不足，领取失败"),
    
    
    
    ARENA_NOT_PRIVILEGE_ADD(702, "没有创建竞技场权限"),
    ARENA_NOT_PRIVILEGE_DEL(703, "没有关闭竞技场权限"),
    ARENA_LESS_THAN_MIN_VALUE(705, "竞技分不足, 请联系圈主"),
    ARENA_LESS_THAN_ROB_BANKER(706, "竞技分不足, 不可抢庄"),
    ARENA_LESS_THAN_REBET(707, "竞技分不足, 不可推注"),
    ARENA_CLOSE(708, "竞技场关闭"),
    ARENA_ROOM_AGAIN(709, "竞技场房间还在继续打"),
    ARENA_REPORT_NOT_EXISTS(711, "竞技场战绩不存在"),
    ARENA_REPORT_NOT_CLEAR_IN_GAME(712, "游戏中无法清理竞技场战绩"),
    ARENA_PAI_GOW_TIME_CAN_NOT_SIT_DOWN(713, "加锅牌九大局未结束不能加入"),

    PLAY_FIELD_NOT_EXISTS(801, "比赛不存在"),
    PLAY_FIELD_ALREADY_START(804, "比赛已经开始"),
    PLAY_FIELD_MEMBER_FULL(805, "比赛已经满员"),
    PLAY_FIELD_FINISH(806, "比赛已经结束"),
    PLAY_FIELD_ALREADY_REWARD_GIVE(807, "奖励已经领取过了"),
    PLAY_DOWN_GOLD_ORDER_NO_EXIST(808, "下分订单不存在"),
    PLAY_NO_SET_SELF_TREASURER(809, "不能设置自己为财务"),
    PLAY_REFUSE_DOWN_GOLD_FAIL(810, "财务拒绝下分订单失败"),
    PLAY_TREASURER_NO_UP_GOLD(811, "财务自己不能使用财务上分"),
    PLAY_TREASURER_NO_DOWN_GOLD(812, "财务自己不能使用财务下分"),
    PLAY_SET_ORDER_STATE_EQUAL_CUR_STATE(813, "设置订单状态与订单当前状态一致"),
    PLAY_TRANSFER_CREATE_ORDER_FAIL(814, "保存下分订单失败"),
    PLAY_OWNER_NO_DOWN_GOLD(815, "圈主不能下分"),
    PLAY_NO_DOWN_GOLD_LESS_TIME(816, "下分间隔不能小于30分钟"),
    PLAY_NO_DOWN_GOLD_LESS_VALUE(817, "下分数值小于最低值"),

    MAIL_NOT_EXISTS(901, "邮件不存在"),
    MAIL_ALREADY_READ(902, "邮件已经读取过了"),
    MAIL_ITEM_NOT_RECEIVE(903, "邮件附件不可领取"),
    MAIL_ITEM_ALREADY_RECEIVE(904, "邮件附件已经领取"),

    CHAT_NOT_EXISTS(1004, "消息内容不存在"),
    CHAT_NOT_SELF_SAY(1005, "消息不是你发"),
    CHAT_STATE_ERROR(1006, "消息状态错误"),
    CHAT_SAY_LONG(1007, "消息发送过久"),
    CHAT_NOT_GROUP(1008, "消息错误"),

    WALLET_TRANSFER_TARGETUID_NOT_EXISTS(1101, "转账接收者ID不存在"),
    WALLET_TRANSFER_AMOUNT_NOT_VAILD(1102, "转账金额不合法"),
    WALLET_TRANSFER_AMOUNT_NOT_ENOUGH(1103, "余额不足,请充值"),
    WALLET_TRANSFER_NOT_EXISTS(1104, "转账记录不存在"),
    WALLET_TRANSFER_SAVE_STATUS_FAIL(1105, "更新转账状态失败"),
    WALLET_TRANSFER_CREATE_FAIL(1106, "保存转账记录失败"),
    WALLET_TRANSFER_ALREADY_RECEIVED(1107, "转账已经被领取"),
    WALLET_TRANSFER_ALREADY_EXPIRED(1108, "转账已超时被退还"),
    WALLET_TRANSFER_ALREADY_CALLBACK(1109, "转账已退回无法领取"),

    WALLET_RED_PACKET_GROUPUID_NOT_EXISTS(1201, "红包接收亲友圈ID不存在"),
    WALLET_RED_PACKET_AMOUNT_NOT_VAILD(1202, "红包金额不合法"),
    WALLET_RED_PACKET_AMOUNT_NOT_ENOUGH(1203, "余额不足,请充值"),
    WALLET_RED_PACKET_NOT_EXISTS(1204, "红包记录不存在"),
    WALLET_RED_PACKET_SAVE_STATUS_FAIL(1205, "更新红包状态失败"),
    WALLET_RED_PACKET_CREATE_FAIL(1206, "保存红包记录失败"),
    WALLET_RED_PACKET_ALREADY_FINISHED(1207, "红包已经被领完"),
    WALLET_RED_PACKET_ALREADY_EXPIRED(1208, "红包已过期"),
    WALLET_RED_PACKET_RECEIVED_RECORD_CREATE_FAIL(1209, "红包领取记录保存失败"),
    WALLET_RED_PACKET_RECEIVED_ALREADY(1210, "已经领取过该红包了"),
    WALLET_MONEY_NOT_ENOUGH(1211, "钱包金额不足"),
    WALLET_BANK_MONEY_NOT_ENOUGH(1212, "钱包银行金额不足"),
    WALLET_SUM_MONEY_NOT_ENOUGH(1213, "钱包累计金额必须大于500"),
    WALLET_RED_PACKET_ALREADY_REFUSED(1214, "红包已经被退还"),
    WALLET_RED_PACKET_RECEIVED_FAIL(1215, "红包领取失败"),
    WALLET_VALUE_DEC_FAIL(1216, "钱包金额扣除失败"),

    GROUP_UID_IS_NOT_EXISTS(1301, "亲友圈id不能为空"),
    GROUP_IS_NOT_EXISTS(1302, "亲友圈不存在"),
    ARENA_VALUE_RECHARGE_CREATE_ORDER_FAIL(1303, "竞技分快速上分创建订单失败"),
    ARENA_VALUE_RECHARGE_MONEY_INVALID(1304, "充值金额不合法"),
    ARENA_VALUE_RECHARGE_TYPE_INVALID(1305, "充值类型不合法"),
    OUT_HOT_ROOM_FAIL(1306,"分数不足，切锅失败"),
    STAKES_NOT_ENOUGH(1307,"筹码不足"),
    CAN_NOT_FILL_UP_STAKES_WHEN_GAMING(1308,"游戏中不能补充筹码"),

    FORBID_NOT_PRIVILEGE_GET_LIST(639, "没有查看屏蔽成员列表的权限"),
    FORBID_NOT_PRIVILEGE_MODIFY_LIST(639, "没有操作屏蔽成员列表的权限"),
    FORBID_IS_EXISTS(640, "已经存在屏蔽关系"),


    CLUB_NOT_EXISTS(5001,"亲友圈不存在"),
    CLUB_NOT_CHIEF_NO_PRIVILEGE(5002,"没有权限"),
    CLUB_IN_LOCK(5003,"锁定中，无法操作"),
    CLUB_GOLD_CREATE_CUSTOM_LIMIT(5004, "比赛场不能创建自定义玩法桌"),
    CLUB_NOT_MAIN_CLUB(5005, "此亲友圈不是主圈"),
    CLUB_NO_PRIVILEGE(5006, "没有权限操作"),
    CLUB_NOT_HAVE_PLAYER(5007, "不在亲友圈中"),

    CLUB_NOT_HAVE_MERGE(6001,"没有合并过"),
    CLUB_HAVE_PARENT_NO_MERGER(6002,"有上级亲友圈，不能进行合并操作"),
    CLUB_NOT_MAIN_NO_MERGER(6003,"不是主圈不能发起合并申请"),
    CLUB_APPLY_NOT_EXISTS(6003,"申请已经过期了"),
    CLUB_APPLY_FAIL(6004,"申请失败"),
    CLUB_MERGE_FAIL(6005,"合并失败"),
    CLUB_APPLY_MERGE_AGAIN(6006,"您有合并请求尚未处理，请等处理后再申请！"),
    CLUB_HAVE_MERGE_SELF(6007,"该圈已合并至自己圈中"),

    CLUB_NOT_SET_GOLD_NO_PRIVILEGE(6008,"没有权限上下分"),
    CLUB_NOT_SET_GOLD_ADD_NOT_ENOUGH(6009,"上分失败，竞技分不足"),
    CLUB_NOT_SET_GOLD_DEL_NOT_ENOUGH(6010,"下分失败，对方竞技分不足"),
    CLUB_NOT_PRIVILEGE_APPLY_CLOSE(6011, "没有打烊权限"),
    CLUB_CLOSE_STATUS_LIMIT(6012, "比赛场已打烊"),
    CLUB_CLOSE_STATUS_LIMIT_ROOM_CARD(6013, "亲友圈已打烊"),
    CLUB_NOT_SET_GOLD_LOCKING(6014, "您的合并申请暂未被处理，不能给自己上下分！"),
    CLUB_NOT_UP_GOLD_TREASURER_ONLINE(6015, "没有上分财务在线,请联系群主"),
    CLUB_NOT_DOWN_GOLD_TREASURER_ONLINE(6016, "没有下分财务在线,请联系群主"),
    CLUB_NOT_SET_UP_GOLD_TREASURER(6017, "本圈没有设置上分财务,请联系群主"),
    CLUB_NOT_SET_DOWN_GOLD_TREASURER(6018, "本圈没有设置下分财务,请联系群主"),
    CLUB_IN_MERGE(6019,"亲友圈申请合并中"),

    CLUB_APPLY_LEAVE_AGAIN(6020,"您有离开请求尚未处理，请等处理后再申请！"),

    CLUB_GOLD_IS_CLOSE(6030,"比赛场打烊中"),
    CLUB_NOT_CLOSE(6031,"未打烊，不能操作"),
    CLUB_CLOSE_LIMIT(6032,"比赛场不能打烊"),
    CLUB_HELPER_1(6033,"被邀请的玩家不在线"),
    CLUB_HELPER_2(6034,"被邀请的玩家游戏中"),
    CLUB_HELPER_3(6034,"你已开局，无法邀请"),
    CLUB_HELPER_4(6034,"已开局，无法进入房间"),
    CLUB_HELPER_5(6035,"该玩家已设置不接受邀请"),
    ACTIVITY_GOLD_NOT_BEGIN(6036,"活动未开启"),

    BOX_ROOM_NOT_EXIST(7000,"游戏桌已解散"),
    CLUB_GOLD_OR_CARD_NOT_EXISTS(7001,"亲友圈或比赛场不存在"),
    CLUB_GOLD_NOT_HAVE_PLAYER(7002,"不在比赛场中"),
    CLUB_GOLD_ALREADY_IN(7003, "已经在比赛场里"),
    CLUB_CARD_ALREADY_IN(7004, "已经在亲友圈里"),
    CLUB_CARD_REOMMEND(7005, "加入亲友圈失败，只能通过邀请码加入"),
    CLUB_GOLD_REOMMEND(7006, "加入比赛场失败，只能通过邀请码加入"),
    CLUB_CARD_IS_CLOSE(7007,"亲友圈打烊中"),
    ROOM_SIT_UP_LIMIT(7008, "游戏中无法站起"),
    CLUB_CARD_MEMBER_FULL(7009,"亲友圈人员已满"),
    ROOM_GOLD_MEMBER_FULL(7010, "比赛场人员已满"),
    CLUB_NOT_IS_MAINCLUB_OR_ONELEVEL(7011,"亲友圈不是主圈也不是一级圈"),
    CLUB_NOT_IS_IN_MAIN(7012,"亲友圈不在主圈中"),
    CLUB_NOT_IS_IN_ONELEVEL(7013,"亲友圈不在一级圈中"),

    // 后台相关
    API_UID_INVALID(7050,"无效ID"),
    API_UID_ALREADY_EXIST(7051,"ID已存在"),
    API_TYPE_INVALID(7052,"无效类型"),
    API_NAME_NOT_NULL(7053,"名称不能为空"),
    API_NAME_LENGTH(7054,"名称过长"),
    API_DESC_NOT_NULL(7055,"描述不能为空"),
    API_DESC_LENGTH(7056,"描述过长"),
    API_GAME_DESC_NOT_NULL(7057,"游戏描述不能为空"),
    API_GAME_DESC_LENGTH(7058,"游戏描述过长"),
    API_NAME_INVALID(7059,"无效名称"),
    API_NAME_ALREADY_EXIST(7060,"名称已存在"),
    API_NOT_TWO_DESK(7061,"没有两人场的固定模式玩法桌"),
    API_NOT_THREE_DESK(7062,"没有三人场的固定模式玩法桌"),
    API_NOT_ENOUGH_ROBOT(7063,"没有足够的机器人可使用"),

    CLUB_RECOMMEND_CODE_NOT_EXIST(8000,"错误的邀请码"),

    SET_GOLD_DEC_ERROR_IN_GAME(8020, "无法下分,玩家正在房间中"),
    SET_GOLD_INC_ERROR_IN_GAME(8021, "正在房间中无法给他人上分"),

    REWARD_VALUE_ENOUGH(8030, "奖励分不足"),

    ;

    private int ret;
    private String msg;

    ErrorCode(int ret, String msg) {
        this.ret = ret;
        this.msg = msg;
    }

    @Override
    public int getRet() {
        return ret;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
