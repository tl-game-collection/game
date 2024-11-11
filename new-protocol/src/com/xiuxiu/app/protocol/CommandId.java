package com.xiuxiu.app.protocol;

import com.xiuxiu.app.protocol.client.PCLILoginInfo;
import com.xiuxiu.app.protocol.client.box.*;
import com.xiuxiu.app.protocol.client.chat.*;
import com.xiuxiu.app.protocol.client.club.*;
import com.xiuxiu.app.protocol.client.club.helper.*;
import com.xiuxiu.app.protocol.client.floor.*;
import com.xiuxiu.app.protocol.client.forbid.*;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfBankerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfBankerRecord;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfDownBanker;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfPlayerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfReady;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfReb;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfRecord;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfSelfBankerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfTouzhurenRebInfoByLhd;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqBankerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqBankerRecord;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqDownBanker;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqLeave;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqPlayerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqReb;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqRecord;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqSelfBankerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqUpBanker;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqVipSeatOp;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredVipSeatInfo;
import com.xiuxiu.app.protocol.client.mahjong.*;
import com.xiuxiu.app.protocol.client.mail.*;
import com.xiuxiu.app.protocol.client.player.*;
import com.xiuxiu.app.protocol.client.poker.*;
import com.xiuxiu.app.protocol.client.poker.cow.*;
import com.xiuxiu.app.protocol.client.poker.cow.PCIPokerNtfSelectRebetInfo;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowSelectBankerInfo;
import com.xiuxiu.app.protocol.client.rank.PCLIRankNtfRankList;
import com.xiuxiu.app.protocol.client.rank.PCLIRankReqRankList;
import com.xiuxiu.app.protocol.client.robot.PCLIRobotReqJoinInfo;
import com.xiuxiu.app.protocol.client.room.*;
import com.xiuxiu.app.protocol.client.system.PCLISystemNtfAnnouncement;
import com.xiuxiu.app.protocol.client.system.PCLISystemNtfGameInfo;
import com.xiuxiu.app.protocol.client.system.PCLISystemReqGameInfo;
import com.xiuxiu.app.protocol.login.PLGetAccountInfo;
import com.xiuxiu.app.protocol.login.PLGetAccountRespInfo;
import com.xiuxiu.core.net.codec.JsonDecoder;
import com.xiuxiu.core.net.protocol.ErrorMsg;


public interface CommandId extends com.xiuxiu.core.net.message.CommandId {
    // 客户端相关
    int CLI_REQ_LOGIN                           = 0x00010001;                                                   // 客户端登陆
    int CLI_NTF_LOGIN_OK                        = 0x00010002;                                                   // 通知登陆成功
    int CLI_NTF_LOGIN_FAIL                      = 0x00010003;                                                   // 通知登陆失败

    int CLI_REQ_RELOGIN                         = 0x00010004;                                                   // 重新登陆
    int CLI_NTF_RELOGIN_OK                      = 0x00010005;                                                   // 重新登陆成功
    int CLI_NTF_RELOGIN_FAIL                    = 0x00010006;                                                   // 重新登陆失败

    // 玩家信息通知
    int CLI_NTF_PLAYER_INFO                     = 0x00011001;

    // 自己相关
    int CLI_REQ_PLAYER_CHANGE_ICON              = 0x00080001;                                                   // 请求修改图标
    int CLI_NTF_PLAYER_CHANGE_ICON_OK           = 0x00080002;                                                   // 修改图标成功
    int CLI_NTF_PLAYER_CHANGE_ICON_FAIL         = 0x00080003;                                                   // 修改图标失败

    // 通知服务器上传icon成功
    int CLI_REQ_PLAYER_UPLOAD_ICON_SUCC         = 0x00080004;
    int CLI_NTF_PLAYER_UPLOAD_ICON_SUCC_OK      = 0x00080005;                                                   // 通知服务器上传icon成功成功
    int CLI_NTF_PLAYER_UPLOAD_ICON_SUCC_FAIL    = 0x00080006;                                                   // 通知服务器上传icon成功失败

    int CLI_REQ_PLAYER_CHANGE_NAME              = 0x00080007;                                                   // 请求修改姓名
    int CLI_NTF_PLAYER_CHANGE_NAME_OK           = 0x00080008;                                                   // 修改姓名成功
    int CLI_NTF_PLAYER_CHANGE_NAME_FAIL         = 0x00080009;                                                   // 修改姓名失败

    int CLI_REQ_PLAYER_CHANGE_SEX               = 0x0008000d;                                                   // 修改性别
    int CLI_NTF_PLAYER_CHANGE_SEX_OK            = 0x0008000e;                                                   // 修改性别成功
    int CLI_NTF_PLAYER_CHANGE_SEX_FAIL          = 0x0008000f;                                                   // 修改性别失败

    int CLI_REQ_PLAYER_RECOMMEND                = 0x00080010;                                                   // 请求推荐用户
    int CLI_NTF_PLAYER_RECOMMEND_OK             = 0x00080011;                                                   // 推荐用户成功
    int CLI_NTF_PLAYER_RECOMMEND_FAIL           = 0x00080012;                                                   // 推荐用户失败

    int CLI_REQ_PLAYER_RECOMMEND_INFO           = 0x0008001c;                                                   // 获取推荐信息
    int CLI_NTF_PLAYER_RECOMMEND_INFO_OK        = 0x0008001d;                                                   // 获取推荐信息成功
    int CLI_NTF_PLAYER_RECOMMEND_INFO_FAIL      = 0x0008001e;                                                   // 获取推荐信息失败

    int CLI_REQ_PLAYER_RECOMMEND_LIST           = 0x00080013;                                                   // 获取推荐用户列表
    int CLI_NTF_PLAYER_RECOMMEND_LIST_OK        = 0x00080014;                                                   // 获取推荐用户成功
    int CLI_NTF_PLAYER_RECOMMEND_LIST_FAIL      = 0x00080015;                                                   // 获取推荐用户失败

    int CLI_REQ_PLAYER_VISIT_CARD               = 0x00080016;                                                   // 设置名片
    int CLI_NTF_PLAYER_VISIT_CARD_OK            = 0x00080017;                                                   // 设置名片成功
    int CLI_NTF_PLAYER_VISIT_CARD_FAIL          = 0x00080018;                                                   // 设置名片失败

    int CLI_REQ_PLAYER_VISIT_CARD_GET           = 0x00080019;                                                   // 获取名片
    int CLI_NTF_PLAYER_VISIT_CARD_GET_OK        = 0x0008001a;                                                   // 获取名片成功
    int CLI_NTF_PLAYER_VISIT_CARD_GET_FAIL      = 0x0008001b;                                                   // 获取名片失败

    int CLI_REQ_PLAYER_CHANGE_BORN              = 0x0008001f;                                                   // 修改出生年月
    int CLI_NTF_PLAYER_CHANGE_BORN_OK           = 0x00080020;                                                   // 修改出生年月成功
    int CLI_NTF_PLAYER_CHANGE_BORN_FAIL         = 0x00080021;                                                   // 修改出生年月失败

    int CLI_REQ_PLAYER_CHANGE_SIGNATURE         = 0x00080022;                                                   // 修改个性签名
    int CLI_NTF_PLAYER_CHANGE_SIGNATURE_OK      = 0x00080023;                                                   // 修改个性签名成功
    int CLI_NTF_PLAYER_CHANGE_SIGNATURE_FAIL    = 0x00080024;                                                   // 修改个性签名失败

    int CLI_REQ_PLAYER_CHANGE_EMOTION           = 0x00080025;                                                   // 修改情感
    int CLI_NTF_PLAYER_CHANGE_EMOTION_OK        = 0x00080026;                                                   // 修改情感成功
    int CLI_NTF_PLAYER_CHANGE_EMOTION_FAIL      = 0x00080027;                                                   // 修改情感失败

    int CLI_REQ_PLAYER_ADD_SHOW_IMAGE           = 0x00080028;                                                   // 添加展示图片
    int CLI_NTF_PLAYER_ADD_SHOW_IMAGE_OK        = 0x00080029;                                                   // 添加展示图片成功
    int CLI_NTF_PLAYER_ADD_SHOW_IMAGE_FAIL      = 0x0008002a;                                                   // 添加展示图片失败

    int CLI_REQ_PLAYER_DEL_SHOW_IMAGE           = 0x0008002b;                                                   // 删除展示图片
    int CLI_NTF_PLAYER_DEL_SHOW_IMAGE_OK        = 0x0008002c;                                                   // 删除展示图片成功
    int CLI_NTF_PLAYER_DEL_SHOW_IMAGE_FAIL      = 0x0008002d;                                                   // 删除展示图片失败

    int CLI_REQ_PLAYER_REPLACE_SHOW_IMAGE       = 0x0008002e;                                                   // 替换展示图片
    int CLI_NTF_PLAYER_REPLACE_SHOW_IMAGE_OK    = 0x0008002f;                                                   // 替换展示图片成功
    int CLI_NTF_PLAYER_REPLACE_SHOW_IMAGE_FAIL  = 0x00080030;                                                   // 替换展示图片失败

    int CLI_REQ_PLAYER_EXCHANGE_SHOW_IMAGE      = 0x00080031;                                                   // 交换展示图片
    int CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_OK   = 0x00080032;                                                   // 交换展示图片成功
    int CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_FAIL = 0x00080033;                                                   // 交换展示图片失败

    int CLI_REQ_PLAYER_CHANGE_COVER             = 0x00080034;                                                   // 修改封面图片
    int CLI_NTF_PLAYER_CHANGE_COVER_OK          = 0x00080035;                                                   // 修改封面图片成功
    int CLI_NTF_PLAYER_CHANGE_COVER_FAIL        = 0x00080036;                                                   // 修改封面图片失败

    int CLI_REQ_PLAYER_CHANGE_LOCATION          = 0x0008000a;                                                   // 请求修改位置
    int CLI_NTF_PLAYER_CHANGE_LOCATION_OK       = 0x0008000b;                                                   // 修改位置成功
    int CLI_NTF_PLAYER_CHANGE_LOCATION_FAIL     = 0x0008000c;                                                   // 修改位置失败

    int CLI_REQ_PLAYER_SYNC_GPS                 = 0x0008004c;                                                   // 同步gps
    int CLI_NTF_PLAYER_SYNC_GPS_OK              = 0x0008004d;                                                   // 同步gps成功
    int CLI_NTF_PLAYER_SYNC_GPS_FAIL            = 0x0008004f;                                                   // 同步gps失败

    int CLI_REQ_PLAYER_CHANGE_PAY_PASSWORD      = 0x00080056;                                                   // 设置提现密码
    int CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_OK   = 0x00080057;                                                   // 设置提现密码成功
    int CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_FAIL = 0x00080058;                                                   // 设置提现密码失败

    int CLI_REQ_PLAYER_IS_SET_PAY_PASSWORD      = 0x00080059;                                                   // 查询是否设置提现密码
    int CLI_NTF_PLAYER_IS_SET_PAY_PASSWORD_OK   = 0x0008005a;                                                   // 查询是否设置提现密码成功
    int CLI_NTF_PLAYER_IS_SET_PAY_PASSWORD_FAIL = 0x0008005b;                                                   // 查询是否设置提现密码失败

    int CLI_REQ_PLAYER_GET_INFO                 = 0x00080070;                                                   // 查询玩家信息
    int CLI_NTF_PLAYER_GET_INFO_OK              = 0x00080071;                                                   // 查询玩家信息成功
    int CLI_NTF_PLAYER_GET_INFO_FAIL            = 0x00080072;                                                   // 查询玩家信息失败


    int CLI_REQ_PLAYER_VERIFY_PAY_PASSWORD             = 0x0008011b;                                            // 验证玩家支付密码
    int CLI_NTF_PLAYER_VERIFY_PAY_PASSWORD_OK          = 0x0008011c;                                            // 验证玩家支付密码成功
    int CLI_NTF_PLAYER_VERIFY_PAY_PASSWORD_FAIL        = 0x0008011d;                                            // 验证玩家支付密码失败

    int CLI_REQ_PLAYER_CHANGE_WECHAT                   = 0x00080121;                                            // 请求修改微信
    int CLI_NTF_PLAYER_CHANGE_WECHAT_OK                = 0x00080122;                                            // 修改微信成功
    int CLI_NTF_PLAYER_CHANGE_WECHAT_FAIL              = 0x00080123;                                            // 修改微信失败

    int CLI_REQ_PLAYER_GET_WECHAT_ASSISTANT            = 0x00081005;                                            // 获取玩家就近微信客服
    int CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_OK         = 0x00081006;                                            // 获取玩家就近微信客服成功
    int CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_FAIL       = 0x00081007;                                            // 获取玩家就近微信客服失败

    int CLI_REQ_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE       = 0x0008100e;                                       // 获取免密支付状态
    int CLI_NTF_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE_OK    = 0x0008100e;                                       // 获取免密支付状态成功
    int CLI_NTF_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE_FAIL  = 0x0008100f;                                       // 获取免密支付状态失败

    int CLI_REQ_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE       = 0x00081010;                                    // 修改免密支付状态
    int CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_OK    = 0x00081011;                                    // 修改免密支付状态成功
    int CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_FAIL  = 0x00081012;                                    // 修改免密支付状态失败

    int CLI_REQ_PLAYER_CHANGE_BANKCARD                  = 0x00081028;                                    // 请求修改银行卡号相关信息
    int CLI_NTF_PLAYER_CHANGE_BANKCARD_OK               = 0x00081029;                                    // 修改姓名银行卡号相关信息成功
    int CLI_NTF_PLAYER_CHANGE_BANKCARD_FAIL             = 0x0008102a;                                    // 修改姓名银行卡号相关信息失败

    int CLI_REQ_PLAYER_SEARCH                            = 11000;                                                       // 请求获取俱乐部成员数量
    int CLI_NTF_PLAYER_SEARCH_OK                         = 11001;                                                       // 通知获取俱乐部成员数量成功
    int CLI_NTF_PLAYER_SEARCH_FAIL                       = 11002;                                                       // 通知获取俱乐部成员数量失败

    int CLI_NTF_PLAYER_CHANGE_PROPERTY                 = 0x00081001;                                            // 通知属性改变
    int CLI_NTF_PLAYER_REMOTE_LOGIN                    = 0x00081002;                                            // 通知玩家异地登陆
    int CLI_NTF_PLAYER_KILL                            = 0x00081003;                                            // 通知玩家被踢
    int CLI_NTF_PLAYER_LOGIN_FINISH                    = 0x00081004;                                            // 通知登陆完成

    // 聊天相关
    int CLI_REQ_CHAT_SAY                        = 0x00030001;                                                   // 请求发送聊天
    int CLI_NTF_CHAT_SAY_OK                     = 0x00030002;                                                   // 通知发送聊天成功
    int CLI_NTF_CHAT_SAY_FAIL                   = 0x00030003;                                                   // 通知发送聊天失败

    int CLI_REQ_CHAT_GET_MSG                    = 0x00030004;                                                   // 请求获取聊天信息
    int CLI_NTF_CHAT_GET_MSG_OK                 = 0x00030005;                                                   // 通知获取聊天信息成功
    int CLI_NTF_CHAT_GET_MSG_FAIL               = 0x00030006;                                                   // 通知获取聊天信息失败

    int CLI_REQ_CHAT_GET_MSG_ACK                = 0x00030007;                                                   // 请求获取聊天成功应答
    int CLI_NTF_CHAT_GET_MSG_ACK_OK             = 0x00030008;                                                   // 通知获取聊天成功应答成功
    int CLI_NTF_CHAT_GET_MSG_ACK_FAIL           = 0x00030009;                                                   // 通知获取聊天成功应答失败

    int CLI_REQ_CHAT_RECALL                     = 0x0003000a;                                                   // 请求撤回
    int CLI_NTF_CHAT_RECALL_OK                  = 0x0003000b;                                                   // 请求撤回成功
    int CLI_NTF_CHAT_RECALL_FAIL                = 0x0003000c;                                                   // 请求撤回失败

    int CLI_REQ_CHAT_UPDATE_MSG_ACK             = 0x0003000e;                                                   // 请求更新消息成功确认
    int CLI_NTF_CHAT_UPDATE_MSG_ACK_OK          = 0x0003000f;                                                   // 通知更新消息成功确认成功
    int CLI_NTF_CHAT_UPDATE_MSG_ACK_FAIL        = 0x00030010;                                                   // 通知更新消息成功确认失败

    int CLI_REQ_CHAT_DEL                        = 0x00030011;                                                   // 请求删除消息
    int CLI_NTF_CHAT_DEL_OK                     = 0x00030012;                                                   // 删除消息成功
    int CLI_NTF_CHAT_DEL_FAIL                   = 0x00030013;                                                   // 删除消息失败

    int CLI_REQ_CHAT_DEL_RECALL                 = 0x00030014;                                                   // 请求删除测回消息
    int CLI_NTF_CHAT_DEL_RECALL_OK              = 0x00030015;                                                   // 删除测回消息成功
    int CLI_NTF_CHAT_DEL_RECALL_FAIL            = 0x00030016;                                                   // 删除测回消息失败

    int CLI_NTF_CHAT_MSG                        = 0x00031001;                                                   // 聊天信息通知
    int CLI_NTF_CHAT_LAST_MSG_ID                = 0x00031002;                                                   // 通知最后一条消息id
    int CLI_NTF_CHAT_UPDATE_MSG                 = 0x00031003;                                                   // 通知更新消息
    int CLI_NTF_GROUP_CHAT_MSG                  = 0x00031004;                                                   // 群系统信息通知

    // 邮件相关
    int CLI_REQ_MAIL_READ                       = 0x000a0001;                                                   // 读取邮件
    int CLI_NTF_MAIL_READ_OK                    = 0x000a0002;                                                   // 读取邮件成功
    int CLI_NTF_MAIL_READ_FAIL                  = 0x000a0003;                                                   // 读取邮件失败

    int CLI_REQ_MAIL_GIVE_ITEM                  = 0x000a0004;                                                   // 领取邮件附件
    int CLI_NTF_MAIL_GIVE_ITEM_OK               = 0x000a0005;                                                   // 领取邮件附件成功
    int CLI_NTF_MAIL_GIVE_ITEM_FAIL             = 0x000a0006;                                                   // 领取邮件附件失败

    int CLI_REQ_MAIL_QUICK_GIVE_ITEM            = 0x000a0007;                                                   // 快速领取附件
    int CLI_NTF_MAIL_QUICK_GIVE_ITEM_OK         = 0x000a0008;                                                   // 快速领取附件成功
    int CLI_NTF_MAIL_QUICK_GIVE_ITEM_FAIL       = 0x000a0009;                                                   // 快速领取附件失败

    int CLI_REQ_MAIL_DEL                        = 0x000a000a;                                                   // 删除邮件
    int CLI_NTF_MAIL_DEL_OK                     = 0x000a000b;                                                   // 删除邮件成功
    int CLI_NTF_MAIL_DEL_FAIL                   = 0x000a000c;                                                   // 删除邮件失败

    int CLI_REQ_MAIL_QUICK_DEL                  = 0x000a000d;                                                   // 快速删除
    int CLI_NTF_MAIL_QUICK_DEL_OK               = 0x000a000e;                                                   // 快速删除成功
    int CLI_NTF_MAIL_QUICK_DEL_FAIL             = 0x000a000f;                                                   // 快速删除失败

    int CLI_NTF_MAIL_INFO                       = 0x000a1001;                                                   // 通知邮件信息
    int CLI_NTF_MAIL_ADD                        = 0x000a1002;                                                   // 通知新邮件信息

    // 系统相关
    int CLI_REQ_SYSTEM_GAME_INFO                = 0x00150001;                                                   // 请求获取游戏信息
    int CLI_NTF_SYSTEM_GAME_INFO_OK             = 0x00150002;                                                   // 获取游戏信息成功
    int CLI_NTF_SYSTEM_GAME_INFO_FAIL           = 0x00150003;                                                   // 获取游戏信息失败
    int CLI_NTF_SYSTEM_ANNOUNCEMENT             = 0x00151001;                                                   // 系统公告

    // 麻将相关
    int CLI_REQ_MAHJONG_FUMBLE                  = 0x00060001;                                                   // 摸牌
    int CLI_NTF_MAHJONG_FUMBLE_OK               = 0x00060002;                                                   // 通知摸牌成功
    int CLI_NTF_MAHJONG_FUMBLE_FAIL             = 0x00060003;                                                   // 通知摸牌失败

    int CLI_REQ_MAHJONG_TAKE                    = 0x00060004;                                                   // 出牌
    int CLI_NTF_MAHJONG_TAKE_OK                 = 0x00060005;                                                   // 通知出牌成功
    int CLI_NTF_MAHJONG_TAKE_FAIL               = 0x00060006;                                                   // 通知出牌失败

    int CLI_REQ_MAHJONG_BUMP                    = 0x00060007;                                                   // 碰
    int CLI_NTF_MAHJONG_BUMP_OK                 = 0x00060008;                                                   // 通知碰成功
    int CLI_NTF_MAHJONG_BUMP_FAIL               = 0x00060009;                                                   // 通知碰失败

    int CLI_REQ_MAHJONG_BAR                     = 0x0006000a;                                                   // 杠
    int CLI_NTF_MAHJONG_BAR_OK                  = 0x0006000b;                                                   // 通知杠成功
    int CLI_NTF_MAHJONG_BAR_FAIL                = 0x0006000c;                                                   // 通知杠失败

    int CLI_REQ_MAHJONG_HU                      = 0x0006000d;                                                   // 胡
    int CLI_NTF_MAHJONG_HU_OK                   = 0x0006000e;                                                   // 通知胡牌成功
    int CLI_NTF_MAHJONG_HU_FAIL                 = 0x0006000f;                                                   // 通知胡牌失败

    int CLI_REQ_MAHJONG_PASS                    = 0x00060010;                                                   // 跳过
    int CLI_NTF_MAHJONG_PASS_OK                 = 0x00060011;                                                   // 通知跳过成功
    int CLI_NTF_MAHJONG_PASS_FAIL               = 0x00060012;                                                   // 通知跳过失败

    int CLI_REQ_MAHJONG_SELECT                  = 0x00060013;                                                   // 选择牌

    int CLI_REQ_MAHJONG_SELECT_PIAO             = 0x00060014;                                                   // 请求选飘
    int CLI_NTF_MAHJONG_SELECT_PIAO_OK          = 0x00060015;                                                   // 通知选飘成功
    int CLI_NTF_MAHJONG_SELECT_PIAO_FAIL        = 0x00060016;                                                   // 通知选飘失败

    int CLI_REQ_MAHJONG_BRIGHT                  = 0x00060017;                                                   // 请求亮牌
    int CLI_NTF_MAHJONG_BRIGHT_OK               = 0x00060018;                                                   // 通知亮牌成功
    int CLI_NTF_MAHJONG_BRIGHT_FAIL             = 0x00060019;                                                   // 通知亮牌失败

    int CLI_REQ_MAHJONG_SHU_KAN                 = 0x0006001a;                                                   // 请求数坎
    int CLI_NTF_MAHJONG_SHU_KAN_OK              = 0x0006001b;                                                   // 请求数坎成功
    int CLI_NTF_MAHJONG_SHU_KAN_FAIL            = 0x0006001c;                                                   // 请求数坎失败

    int CLI_REQ_MAHJONG_HUAN_PAI                = 0x0006001e;                                                   // 请求换牌
    int CLI_NTF_MAHJONG_HUAN_PAI_OK             = 0x0006001f;                                                   // 请求换牌成功
    int CLI_NTF_MAHJONG_HUAN_PAI_FAIL           = 0x00060020;                                                   // 请求换牌失败

    int CLI_REQ_MAHJONG_SHUAI_PAI               = 0x00060021;                                                   // 请求甩牌
    int CLI_NTF_MAHJONG_SHUAI_PAI_OK            = 0x00060022;                                                   // 请求甩牌成功
    int CLI_NTF_MAHJONG_SHUAI_PAI_FAIL          = 0x00060023;                                                   // 请求甩牌失败

    int CLI_REQ_MAHJONG_DING_QUE                = 0x00060024;                                                   // 请求定缺
    int CLI_NTF_MAHJONG_DING_QUE_OK             = 0x00060025;                                                   // 请求定缺成功
    int CLI_NTF_MAHJONG_DING_QUE_FAIL           = 0x00060026;                                                   // 请求定缺失败

    int CLI_REQ_MAHJONG_EAT                     = 0x00060027;                                                   // 吃
    int CLI_NTF_MAHJONG_EAT_OK                  = 0x00060028;                                                   // 吃成功
    int CLI_NTF_MAHJONG_EAT_FAIL                = 0x00060029;                                                   // 吃失败

    int CLI_REQ_MAHJONG_XUAN_ZENG               = 0x0006002a;                                                   // 请求选增
    int CLI_NTF_MAHJONG_XUAN_ZENG_OK            = 0x0006002b;                                                   // 请求选增成功
    int CLI_NTF_MAHJONG_XUAN_ZENG_FAIL          = 0x0006002c;                                                   // 请求选增失败

    int CLI_REQ_MAHJONG_XUAN_PIAO               = 0x0006002d;                                                   // 请求选票
    int CLI_NTF_MAHJONG_XUAN_PIAO_OK            = 0x0006002e;                                                   // 请求选票成功
    int CLI_NTF_MAHJONG_XUAN_PIAO_FAIL          = 0x0006002f;                                                   // 请求选票失败

    int CLI_REQ_MAHJONG_START_HU                = 0x00060030;                                                   // 请求起手胡
    int CLI_NTF_MAHJONG_START_HU_OK             = 0x00060031;                                                   // 请求起手胡成功
    int CLI_NTF_MAHJONG_START_HU_FAIL           = 0x00060032;                                                   // 请求起手胡失败

    int CLI_REQ_MAHJONG_YANG_PAI                = 0x00060033;                                                   // 请求仰
    int CLI_NTF_MAHJONG_YANG_PAI_OK             = 0x00060034;                                                   // 请求仰成功
    int CLI_NTF_MAHJONG_YANG_PAI_FAIL           = 0x00060035;                                                   // 请求仰失败

    int CLI_REQ_MAHJONG_SELECT_OPEN_BAR         = 0x00060036;                                                   // 请求选择开杠
    int CLI_NTF_MAHJONG_SELECT_OPEN_BAR_OK      = 0x00060037;                                                   // 请求选择开杠成功
    int CLI_NTF_MAHJONG_SELECT_OPEN_BAR_FAIL    = 0x00060038;                                                   // 请求选择开杠失败

    int CLI_REQ_MAHJONG_TING                    = 0x0006003c;                                                   // 报听
    int CLI_NTF_MAHJONG_TING_OK                 = 0x0006003d;                                                   // 报听成功
    int CLI_NTF_MAHJONG_TING_FAIL               = 0x0006003e;                                                   // 报听失败

    int CLI_NTF_MAHJONG_FUMBLE                  = 0x00061001;                                                   // 通知摸牌值
    int CLI_NTF_MAHJONG_TAKE                    = 0x00061002;                                                   // 通知出牌值
    int CLI_NTF_MAHJONG_BUMP                    = 0x00061003;                                                   // 通知碰牌
    int CLI_NTF_MAHJONG_BAR                     = 0x00061004;                                                   // 通知杠牌
    int CLI_NTF_MAHJONG_HU                      = 0x00061005;                                                   // 通知胡牌
    int CLI_NTF_MAHJONG_CAN_FUMBLE              = 0x00061006;                                                   // 通知可以摸牌
    int CLI_NTF_MAHJONG_CAN_OPERATE             = 0x0006100a;                                                   // 通知可以操作
    int CLI_NTF_MAHJONG_SELECT                  = 0x0006100c;                                                   // 通知选择牌
    int CLI_NTF_MAHJONG_SELECT_PIAO             = 0x0006100d;                                                   // 通知选飘模式
    int CLI_NTF_MAHJONG_SELECT_PIAO_VALUE       = 0x0006100e;                                                   // 通知选飘值
    int CLI_NTF_MAHJONG_HU_INFO                 = 0x0006100f;                                                   // 通知胡牌信息
    int CLI_NTF_MAHJONG_BUY_HORSE_INFO          = 0x00061010;                                                   // 通知买马信息
    int CLI_NTF_MAHJONG_GANG_SCORE_INFO         = 0x00061011;                                                   // 通知杠分
    int CLI_NTF_MAHJONG_BRIGHT                  = 0x00061012;                                                   // 通知亮牌
    int CLI_NTF_MAHJONG_CAN_TAKE                = 0x00061014;                                                   // 通知可以打牌
    int CLI_NTF_MAHJONG_SELECT_PIAO_INFO        = 0x00061015;                                                   // 通知选飘信息
    int CLI_NTF_MAHJONG_START_TAKE              = 0x00061016;                                                   // 通知开始打牌
    int CLI_NTF_MAHJONG_CHANGEPAO_CNT           = 0x00061017;                                                   // 通知炮子数量改变
    int CLI_NTF_MAHJONG_SHU_KAN_INFO            = 0x00061018;                                                   // 通知数坎信息
    int CLI_NTF_MAHJONG_SHU_KAN_VALUE           = 0x00061019;                                                   // 通知数坎值
    int CLI_NTF_MAHJONG_SHU_KAN                 = 0x0006101a;                                                   // 通知数坎
    int CLI_NTF_MAHJONG_BEGIN_HUAN_PAI          = 0x0006101b;                                                   // 通知开始换牌
    int CLI_NTF_MAHJONG_END_HUAN_PAI            = 0x0006101c;                                                   // 通知结束换牌
    int CLI_NTF_MAHJONG_HUAN_PAI_INFO           = 0x0006101d;                                                   // 通知换牌信息
    int CLI_NTF_MAHJONG_BEGIN_SHUAI_PAI         = 0x0006101e;                                                   // 通知开始甩牌
    int CLI_NTF_MAHJONG_END_SHUAI_PAI           = 0x0006101f;                                                   // 通知结束甩牌
    int CLI_NTF_MAHJONG_SHUAI_PAI_INFO          = 0x00061020;                                                   // 通知甩牌信息
    int CLI_NTF_MAHJONG_BEGIN_DING_QUE          = 0x00061021;                                                   // 通知开始定缺
    int CLI_NTF_MAHJONG_END_DING_QUE            = 0x00061022;                                                   // 通知结束定缺
    int CLI_NTF_MAHJONG_DING_QUE_INFO           = 0x00061023;                                                   // 通知定缺信息
    int CLI_NTF_MAHJONG_EAT                     = 0x00061024;                                                   // 通知吃信息
    int CLI_NTF_MAHJONG_BEGIN_XUAN_ZENG         = 0x00061025;                                                   // 通知开始选增
    int CLI_NTF_MAHJONG_END_XUAN_ZENG           = 0x00061026;                                                   // 通知结束选增
    int CLI_NTF_MAHJONG_XUAN_ZENG_INFO          = 0x00061027;                                                   // 通知选增信息
    int CLI_NTF_MAHJONG_DESK_SHOW_INFO          = 0x00061028;                                                   // 通知牌桌显示信息
    int CLI_NTF_MAHJONG_BEGIN_XUAN_PIAO         = 0x00061029;                                                   // 通知开始选票
    int CLI_NTF_MAHJONG_END_XUAN_PIAO           = 0x0006102a;                                                   // 通知结束选票
    int CLI_NTF_MAHJONG_XUAN_PIAO_INFO          = 0x0006102b;                                                   // 通知选票信息
    int CLI_NTF_MAHJONG_BEGIN_START_HU          = 0x0006102c;                                                   // 通知开始起手胡
    int CLI_NTF_MAHJONG_END_START_HU            = 0x0006102d;                                                   // 通知结束起手胡
    int CLI_NTF_MAHJONG_START_HU_INFO           = 0x0006102e;                                                   // 通知起手胡信息
    int CLI_NTF_MAHJONG_BEGIN_YANG_PAI          = 0x0006102f;                                                   // 通知开始仰牌
    int CLI_NTF_MAHJONG_END_YANG_PAI            = 0x00061030;                                                   // 通知结束仰牌
    int CLI_NTF_MAHJONG_YANG_PAI                = 0x00061031;                                                   // 通知仰牌信息
    int CLI_NTF_MAHJONG_LAI_PI                  = 0x00061032;                                                   // 通知赖子皮子信息
    int CLI_NTF_MAHJONG_LAST_CARD               = 0x00061033;                                                   // 通知最后一张牌
    int CLI_NTF_MAHJONG_BEGIN_LAST_CARD         = 0x00061034;                                                   // 通知选择最后一张牌
    int CLI_NTF_MAHJONG_BEGIN_CS_OPEN_BAR       = 0x00061035;                                                   // 通知开始长沙开杠
    int CLI_NTF_MAHJONG_BEGIN_CS_OPEN_BAR_INFO  = 0x00061036;                                                   // 通知开始长沙开杠消息
    int CLI_NTF_MAHJONG_CRAP_AND_CARD           = 0x00061037;                                                   // 通知骰子和牌信息
    int CLI_NTF_MAHJONG_TING                    = 0x00061038;                                                   // 通知报听信息
    int CLI_NTF_MAHJONG_CAN_TING                = 0x00061039;                                                   // 通知可以报听
    int CLI_NTF_MAHJONG_BEGIN_BU_HUA            = 0x00061040;                                                   // 通知开始补花
    int CLI_NTF_MAHJONG_BU_HUA_INFO             = 0x00061041;                                                   // 通知补花信息
    int CLI_NTF_FIXED_PIAO_VALUE                = 0x00061042;                                                   // 通知固定定漂值

    // 扑克
    int CLI_REQ_POKER_TAKE                      = 0x000c0001;                                                   // 扑克打牌
    int CLI_NTF_POKER_TAKE_OK                   = 0x000c0002;                                                   // 扑克打牌成功
    int CLI_NTF_POKER_TAKE_FAIL                 = 0x000c0003;                                                   // 扑克打牌失败

    int CLI_REQ_POKER_PASS                      = 0x000c0004;                                                   // 扑克跳过
    int CLI_NTF_POKER_PASS_OK                   = 0x000c0005;                                                   // 扑克跳过成功
    int CLI_NTF_POKER_PASS_FAIL                 = 0x000c0006;                                                   // 扑克跳过失败

    int CLI_REQ_POKER_DISCARD                   = 0x000c0007;                                                   // 请求弃牌
    int CLI_NTF_POKER_DISCARD_OK                = 0x000c0008;                                                   // 通知弃牌成功
    int CLI_NTF_POKER_DISCARD_FAIL              = 0x000c0009;                                                   // 通知弃牌失败

    int CLI_REQ_POKER_LOOK                      = 0x000c000a;                                                   // 请求看牌--搓牌
    int CLI_NTF_POKER_LOOK_OK                   = 0x000c000b;                                                   // 通知看牌成功
    int CLI_NTF_POKER_LOOK_FAIL                 = 0x000c000c;                                                   // 通知看牌失败

    int CLI_REQ_POKER_ADDMUL                    = 0x000c000d;                                                   // 加倍
    int CLI_NTF_POKER_ADDMUL_OK                 = 0x000c000e;                                                   // 加倍成功
    int CLI_NTF_POKER_ADDMUL_FAIL               = 0x000c000f;                                                   // 加倍失败

    int CLI_REQ_POKER_AUTO_MODE                 = 0x000c0010;                                                   // 请求托管
    int CLI_NTF_POKER_AUTO_MODE_OK              = 0x000c0011;                                                   // 托管成功
    int CLI_NTF_POKER_AUTO_MODE_FAIL            = 0x000c0012;                                                   // 托管失败

    int CLI_NTF_POKER_AUTO_MODE                 = 0x000c0013;                                                   // 通知托管状态
    int CLI_NTF_POKER_TAKE                      = 0x000c1001;                                                   // 扑克通知打牌
    int CLI_NTF_POKER_PASS                      = 0x000c1002;                                                   // 扑克通知跳过
    int CLI_NTF_POKER_CAN_TAKE                  = 0x000c1003;                                                   // 扑克通知可以打牌
    int CLI_NTF_POKER_CAN_PASS                  = 0x000c1004;                                                   // 扑克通知可以跳过
    int CLI_NTF_POKER_BOMB_SOURCE               = 0x000c1008;                                                   // 扑克炸弹分数通知
    int CLI_NTF_POKER_DISCARD                   = 0x000c1009;                                                   // 通知弃牌
    int CLI_NTF_POKER_ADDMUL                    = 0x000c100b;                                                   // 通知开始加倍
    int CLI_NTF_POKER_PLAYER_LOOK_CARD          = 0x000c100c;                                       			// 通知玩家看牌
    int CLI_NTF_POKER_PLAYER_BEGIN_PRIMULA      = 0x000c1010;                                       			// 通知开始叫春
    int CLI_REQ_POKER_PLAYER_PRIMULA            = 0x000c1011;                                       			// 请求叫春
    int CLI_NTF_POKER_PLAYER_PRIMULA_OK         = 0x000c1012;                                       			// 通知请求叫春成功
    int CLI_NTF_POKER_PLAYER_PRIMULA_FAIL       = 0x000c1013;                                       			// 通知请求叫春失败
    int CLI_NTF_POKER_PLAYER_PRIMULA_INFO       = 0x000e1014;                                                  	// 通知叫春信息
    int CLI_NTF_POKER_PLAYER_PRIMULA_RESULT     = 0x000c1015;                                       			// 通知叫春结果

    // 扑克--斗地主
    int CLI_REQ_POKER_LAND_LORD_CALL_SCORE      = 0x000d0001;                                                   // 请求叫分
    int CLI_NTF_POKER_LAND_LORD_CALL_SCORE_OK   = 0x000d0002;                                                   // 请求叫分成功
    int CLI_NTF_POKER_LAND_LORD_CALL_SCORE_FAIL = 0x000d0003;                                                   // 请求叫分失败
    int CLI_REQ_POKER_LAND_LORD_SHOW_CARD       = 0x000d0004;                                                   // 请求明牌
    int CLI_NTF_POKER_LAND_LORD_SHOW_CARD_OK    = 0x000d0005;                                                   // 请求明牌成功
    int CLI_NTF_POKER_LAND_LORD_SHOW_CARD_FAIL  = 0x000d0006;                                                   // 请求明牌失败
    int CLI_REQ_POKER_LAND_LORD_MULTIPLE        = 0x000d0007;                                                   // 请求加倍
    int CLI_NTF_POKER_LAND_LORD_MULTIPLE_OK     = 0x000d0008;                                                   // 加倍成功
    int CLI_NTF_POKER_LAND_LORD_MULTIPLE_FAIL   = 0x000d0009;                                                   // 加倍失败
    int CLI_NTF_POKER_LAND_LORD_CALL_SCORE      = 0x000d1001;                                                   // 通知叫分信息
    int CLI_NTF_POKER_LAND_LORD_SHOW_CARD_INFO  = 0x000d1002;                                                   // 通知明牌信息
    int CLI_NTF_POKER_LAND_LORD_LAST_CARD       = 0x000d1003;                                                   // 通知底牌
    int CLI_NTF_POKER_LAND_LORD_RECALL_SCORE    = 0x000d1004;                                                   // 通知重新叫分
    int CLI_NTF_POKER_LAND_LORD_MULTIPLE_BEGIN  = 0x000d1005;                                                   // 通知开始加倍
    int CLI_NTF_POKER_LAND_LORD_MULTIPLE        = 0x000d1006;                                                   // 通知加倍信息
    
    int CLI_NTF_POKER_LAND_LORD_KICK            = 0x000d1007;                                                   // 通知选择踢
    int CLI_NTF_POKER_LAND_LORD_KICK_BACK       = 0x000d1008;                                                   // 通知地主选择回踢
    int CLI_REQ_POKER_LAND_LORD_KICK_SELECT        = 0x000d1009;                                                // 请求选择踢
    int CLI_NTF_POKER_LAND_LORD_KICK_SELECT_OK     = 0x000d100a;                                                // 选择踢成功
    int CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL   = 0x000d100b;                                                // 选择踢失败
    int CLI_REQ_POKER_LAND_LORD_KICK_BACK_SELECT        = 0x000d100c;                                           // 请求选择踢
    int CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_OK     = 0x000d100d;                                           // 选择踢成功
    int CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL   = 0x000d100e;                                           // 选择踢失败
    int CLI_NTF_POKER_LAND_LORD_KICK_RESULT        = 0x000d100f;                                                // 通知踢/回踢信息
    
    // 扑克--三公
    int CLI_REQ_POKER_SG_ROB_BANKER            = 0x001e0001;                                                    // 请求三公抢庄
    int CLI_NTF_POKER_SG_ROB_BANKER_OK         = 0x001e0002;                                                    // 三公抢庄成功
    int CLI_NTF_POKER_SG_ROB_BANKER_FAIL       = 0x001e0003;                                                    // 三公抢庄失败
    int CLI_REQ_POKER_SG_REBET                 = 0x001e0004;                                                    // 请求三公下注
    int CLI_NTF_POKER_SG_REBET_OK              = 0x001e0005;                                                    // 三公下注成功
    int CLI_REQ_POKER_SG_LORD_BANKER_STATE     = 0x001e0007;                                                    // 三公下注失败
    int CLI_REQ_POKER_SG_DEALCARD_OVER         = 0x001e0010;                                                    // 客服端通知前端发牌已经完成
    int CLI_NTF_POKER_SG_DEALCARD_OVER_OK      = 0x001e0011;                                                    // 客服端通知前端发牌已经完成成功
    int CLI_NTF_POKER_SG_DEALCARD_OVER_FAIL    = 0x001e0012;                                                    // 客服端通知前端发牌已经完成失败
    int CLI_NTF_POKER_SG_ROB_BANKER_BEGIN      = 0x001e1001;                                                    // 通知三公开始抢庄
    int CLI_NTF_POKER_SG_ROB_BANKER            = 0x001e1002;                                                    // 通知三公抢庄
    int CLI_NTF_POKER_SG_ROB_BANKER_INFO       = 0x001e1003;                                                    // 通知三公抢庄信息
    int CLI_NTF_POKER_SG_REBET_BEGIN           = 0x001e1004;                                                    // 通知三公开始下注
    int CLI_NTF_POKER_SG_REBET                 = 0x001e1005;                                                    // 通知三公下注
    int CLI_NTF_POKER_SG_REBET_INFO            = 0x001e1006;                                                    // 通知三公下注信息
    int CLI_NTF_POKER_SG_DEAL_CARD             = 0x001e1007;                                                    // 通知三公发牌
    int CLI_NTF_POKER_SG_ROB_BANKER_RESULT     = 0x001e1008;                                                    // 通知三公抢庄结果
    
    //牛牛
    int CLI_REQ_POKER_COW_ROB_BANKER            = 0x000e0001;                                                   // 请求牛牛抢庄
    int CLI_NTF_POKER_COW_ROB_BANKER_OK         = 0x000e0002;                                                   // 牛牛抢庄成功
    int CLI_NTF_POKER_COW_ROB_BANKER_FAIL       = 0x000e0003;                                                   // 牛牛抢庄失败
    int CLI_REQ_POKER_COW_REBET                 = 0x000e0004;                                                   // 请求牛牛下注
    int CLI_NTF_POKER_COW_REBET_OK              = 0x000e0005;                                                   // 牛牛下注成功
    int CLI_NTF_POKER_COW_REBET_FAIL            = 0x000e0006;                                                   // 牛牛下注失败
    int CLI_REQ_POKER_COW_LORD_BANKER_STATE     = 0x000e0007;                                                   // 霸王庄 通知玩家选择开始庄（只在第一局的时候推送）
    int CLI_NTF_POKER_COW_LORD_BANKER_STATE_OK  = 0x000e0008;                                                   // 霸王庄 通知玩家选择开始庄（只在第一局的时候推送）请求成功
    int CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL= 0x000e0009;                                                   // 霸王庄 通知玩家选择开始庄（只在第一局的时候推送）请求失败
    int CLI_REQ_POKER_COW_LEVEL_HOT_BANKER      = 0x000e000a;                                                   // 端火锅庄家主动选择抽庄；
    int CLI_NTF_POKER_COW_LEVEL_HOT_BANKER_OK   = 0x000e000b;                                                   // 端火锅庄家抽庄成功
    int CLI_NTF_POKER_COW_LEVEL_HOT_BANKER_FAIL = 0x000e000c;                                                   // 端火锅庄家抽庄失败
    int CLI_REQ_POKER_COW_GIVE_BANKER           = 0x000e000d;                                                   // 选择让庄
    int CLI_NTF_POKER_COW_GIVE_BANKER_OK        = 0x000e000e;                                                   // 让庄成功
    int CLI_NTF_POKER_COW_GIVE_BANKER_FAIL      = 0x000e000f;                                                   // 让庄失败
    int CLI_REQ_POKER_COW_DEALCARD_OVER         = 0x000e0010;                                                   // 客服端通知前端发牌已经完成
    int CLI_NTF_POKER_COW_DEALCARD_OVER_OK      = 0x000e0011;                                                   // 客服端通知前端发牌已经完成成功
    int CLI_NTF_POKER_COW_DEALCARD_OVER_FAIL    = 0x000e0012;                                                   // 客服端通知前端发牌已经完成失败
    int CLI_NTF_POKER_COW_ROB_BANKER_BEGIN      = 0x000e1001;                                                   // 通知牛牛开始抢庄
    int CLI_NTF_POKER_COW_ROB_BANKER            = 0x000e1002;                                                   // 通知牛牛抢庄
    int CLI_NTF_POKER_COW_ROB_BANKER_INFO       = 0x000e1003;                                                   // 通知牛牛抢庄信息
    int CLI_NTF_POKER_COW_REBET_BEGIN           = 0x000e1004;                                                   // 通知牛牛开始下注
    int CLI_NTF_POKER_COW_REBET                 = 0x000e1005;                                                   // 通知牛牛下注
    int CLI_NTF_POKER_COW_REBET_INFO            = 0x000e1006;                                                   // 通知牛牛下注信息
    int CLI_NTF_POKER_COW_DEAL_CARD             = 0x000e1007;                                                   // 通知牛牛发牌
    int CLI_NTF_POKER_COW_ROB_BANKER_RESULT     = 0x000e1008;                                                   // 通知牛牛抢庄结果
    int CLI_NTF_POKER_COW_LORD_BANKER_SELECT    = 0x000e1009;                                                   // 霸王庄 选择庄的状态 （要庄还是弃庄）0弃庄  1要庄
    int CLI_REQ_SELECT_REBET                    = 0x000e100a;                                                   // 选择推注
    int CLI_NTF_SELECT_REBET_OK            		= 0x000e100b;                                                   // 选择推注成功
    int CLI_NTF_SELECT_REBET_FAIL          		= 0x000e100c;                                                   // 选择推注失败
    int CLI_REQ_POKER_COW_ROBOT                 = 0x000c2004;                                                   // 请求牛牛机器人数据
    int CLI_NTF_POKER_COW_ROBOT_OK              = 0x000c2005;                                                   // 请求牛牛机器人数据成功
    int CLI_NTF_POKER_COW_ROBOT_FAIL            = 0x000c2006;                                                   // 请求牛牛机器人数据失败


    int CLI_NTF_POKER_COW_LOOP                  = 0x000c2007;                                                    // 通知轮数信息
    int CLI_NTF_POKER_COW_BEGIN_READY           = 0x000c2008;                                                    // 通知开始准备
    int CLI_NTF_POKER_COW_READY                 = 0x000c2009;                                                    // 广播某个玩家准备
    int CLI_NTF_POKER_COW_READY_INFO            = 0x000c200a;                                                    // 通知准备信息
    int CLI_NTF_POKER_COW_GAME_OVER_LOOP        = 0x000c200b;                                                    // 通知小局结束信息
    int CLI_NTF_POKER_COW_BEGIN_HOT_AGAIN       = 0x000c200c;                                                    // 通知开始续锅信息
    int CLI_NTF_POKER_COW_HOT_AGAIN_INFO        = 0x000c200d;                                                    // 通知续锅信息
    int CLI_NTF_POKER_COW_HOT_OUT_INFO          = 0x000c200e;                                                    // 通知揭锅信息
    int CLI_NTF_POKER_COW_BEGIN_HOT_OUT         = 0x000c200f;                                                    // 通知开始揭锅信息
    int CLI_NTF_POKER_COW_NEXT_BANKER_INFO      = 0x000c2010;                                                    // 通知下局庄家信息
    int CLI_NTF_POKER_COW_READY_OVER_INFO       = 0x000c2011;                                                    // 通知所有参与本轮游戏的玩家信息

    int CLI_REQ_POKER_COW_READY                 = 0x000c2051;                                                    // 请求准备
    int CLI_NTF_POKER_COW_READY_OK              = 0x000c2052;                                                    // 请求准备成功
    int CLI_NTF_POKER_COW_READY_FAIL            = 0x000c2053;                                                    // 请求准备失败

    int CLI_REQ_POKER_COW_OUT_HOT           = 0x000c2054;                                                        // 切锅
    int CLI_NTF_POKER_COW_OUT_HOT_OK        = 0x000c2055;                                                        // 切锅成功
    int CLI_NTF_POKER_COW_OUT_HOT_FAIL      = 0x000c2056;                                                        // 切锅失败

    int CLI_REQ_POKER_COW_HOT_AGAIN         = 0x000c2057;                                                        // 请求续锅
    int CLI_NTF_POKER_COW_HOT_AGAIN_OK      = 0x000c2058;                                                        // 请求续锅成功
    int CLI_NTF_POKER_COW_HOT_AGAIN_FAIL    = 0x000c2059;                                                        // 请求续锅失败

    // 房间相关
    int CLI_REQ_ROOM_CREATE                     = 0x00050001;                                                   // 创建房间
    int CLI_NTF_ROOM_CREATE_OK                  = 0x00050002;                                                   // 通知创建房间成功
    int CLI_NTF_ROOM_CREATE_FAIL                = 0x00050003;                                                   // 通知创建房间失败

    int CLI_REQ_ROOM_JOIN                       = 0x00050004;                                                   // 加入房间
    int CLI_NTF_ROOM_JOIN_OK                    = 0x00050005;                                                   // 通知加入房间成功
    int CLI_NTF_ROOM_JOIN_FAIL                  = 0x00050006;                                                   // 通知加入房间失败

    int CLI_REQ_ROOM_READY                      = 0x0005000d;                                                   // 准备
    int CLI_NTF_ROOM_READY_OK                   = 0x0005000e;                                                   // 准备成功
    int CLI_NTF_ROOM_READY_FAIL                 = 0x0005000f;                                                   // 准备失败

    int CLI_NTF_GOLD_FAIL                       = 0x00071004;                                                   //竞技分不足

    int CLI_REQ_ROOM_KILL                       = 0x00050010;                                                   // 请求踢人
    int CLI_NTF_ROOM_KILL_OK                    = 0x00050011;                                                   // 踢人成功
    int CLI_NTF_ROOM_KILL_FAIL                  = 0x00050012;                                                   // 踢人失败

    int CLI_REQ_ROOM_DISSOLVE                   = 0x00050013;                                                   // 请求解散
    int CLI_NTF_ROOM_DISSOLVE_OK                = 0x00050014;                                                   // 请求解散成功
    int CLI_NTF_ROOM_DISSOLVE_FAIL              = 0x00050015;                                                   // 请求解散失败

    int CLI_REQ_CLUB_ROOM_DISMISS                        = 10303;                                                       // 请求解散某一个房间
    int CLI_NTF_CLUB_ROOM_DISMISS_OK                     = 10304;                                                       // 请求解散某一个房间成功
    int CLI_NTF_CLUB_ROOM_DISMISS_FAIL                   = 10305;                                                       // 请求解散某一个房间失败

    int CLI_REQ_ROOM_DISSOLVE_OP                = 0x00050016;                                                   // 请求解散操作
    int CLI_NTF_ROOM_DISSOLVE_OP_OK             = 0x00050017;                                                   // 请求解散操作成功
    int CLI_NTF_ROOM_DISSOLVE_OP_FAIL           = 0x00050018;                                                   // 请求解散操作失败

    int CLI_REQ_ROOM_SCORE_RECORD               = 0x00050019;                                                   // 获取房间战绩记录
    int CLI_NTF_ROOM_SCORE_RECORD_OK            = 0x0005001a;                                                   // 获取房间战绩记录成功
    int CLI_NTF_ROOM_SCORE_RECORD_FAIL          = 0x0005001b;                                                   // 获取房间战绩记录失败

	// 获取竞技场战报
    int CLI_REQ_ARENA_REPORT                    = 0x00070020;
    int CLI_NTF_ARENA_REPORT_OK                 = 0x00070021;                                                   // 获取竞技场战报成功
    int CLI_NTF_ARENA_REPORT_FAIL               = 0x00070022;                                                   // 获取竞技场战报失败

    int CLI_REQ_ROOM_MAGIC_FACE                 = 0x0005001c;                                                   // 请求魔法表情
    int CLI_NTF_ROOM_MAGIC_FACE_OK              = 0x0005001d;                                                   // 请求魔法表情成功
    int CLI_NTF_ROOM_MAGIC_FACE_FAIL            = 0x0005001e;                                                   // 请求魔法表情失败

    int CLI_REQ_ROOM_LOCATION_RELATION          = 0x0005001f;                                                   // 获取位置关系
    int CLI_NTF_ROOM_LOCATION_RELATION_OK       = 0x00050020;                                                   // 获取位置关系成功
    int CLI_NTF_ROOM_LOCATION_RELATION_FAIL     = 0x00050021;                                                   // 获取位置关系失败

    int CLI_REQ_ROOM_LEAVE_V2                   = 0x00050022;                                                   // 请求离开
    int CLI_NTF_ROOM_LEAVE_V2_OK                = 0x00050023;                                                   // 请求离开成功
    int CLI_NTF_ROOM_LEAVE_V2_FAIL              = 0x00050024;                                                   // 请求离开失败

    int CLI_REQ_ROOM_DESK_INFO                  = 0x00050025;                                                   // 请求房间牌桌信息
    int CLI_NTF_ROOM_DESK_INFO_OK               = 0x00050026;                                                   // 请求房间牌桌信息成功
    int CLI_NTF_ROOM_DESK_INFO_FAIL             = 0x00050027;                                                   // 请求房间牌桌信息失败

    int CLI_REQ_ROOM_SHORTTALK                  = 0x00050028;                                                   // 通知快捷语广播成功
    int CLI_NTF_ROOM_SHORTTALK_OK               = 0x00050029;                                                   // 通知快捷语
    int CLI_NTF_ROOM_SHORTTALK_FAIL             = 0x0005002a;                                                   // 通知快捷语失败

    int CLI_REQ_ROOM_SITDOWN                    = 0x0005002b;                                                   // 请求坐下
    int CLI_NTF_ROOM_SITDOWN_OK                 = 0x0005002c;                                                   // 坐下成功
    int CLI_NTF_ROOM_SITDOWN_FAIL               = 0x0005002d;                                                   // 坐下失败

    int CLI_REQ_ROOM_SITUP                      = 0x0005002e;                                                   // 请求站起
    int CLI_NTF_ROOM_SITUP_OK                   = 0x0005002f;                                                   // 站起失败
    int CLI_NTF_ROOM_SITUP_FAIL                 = 0x00050030;                                                   // 站起失败

    int CLI_REQ_ROOM_LOOK_DESK_INFO             = 0x00050031;                                                   // 请求查看牌桌信息
    int CLI_NTF_ROOM_LOOK_DESK_INFO_OK          = 0x00050032;                                                   // 通知牌桌信息成功
    int CLI_NTF_ROOM_LOOK_DESK_INFO_FAIL        = 0x00050032;                                                   // 通知牌桌信息失败

    int CLI_REQ_ROOM_NEXT_CARD                  = 0x00050033;                                                   // 请求下一次牌
    int CLI_NTF_ROOM_NEXT_CARD_OK               = 0x00050034;                                                   // 下一次牌成功
    int CLI_NTF_ROOM_NEXT_CARD_FAIL             = 0x00050035;                                                   // 下一次牌失败

    int CLI_REQ_ROOM_SHOW_OFF                   = 0x00050036;                                                   // 请求炫耀
    int CLI_NTF_ROOM_SHOW_OFF_OK                = 0x00050037;                                                   // 请求炫耀成功
    int CLI_NTF_ROOM_SHOW_OFF_FAIL              = 0x00050038;                                                   // 请求炫耀失败

    int CLI_REQ_ROOM_CHANGE_SEATS               = 0x00050039;                                                   // 请求换座位
    int CLI_NTF_ROOM_CHANGE_SEATS_OK            = 0x0005003a;                                                   // 请求换座位成功
    int CLI_NTF_ROOM_CHANGE_SEATS_FAIL          = 0x0005003b;                                                   // 请求换座位失败


    int CLI_NTF_ROOM_LEAVE                      = 0x00051002;                                                   // 通知离开房间
    int CLI_NTF_ROOM_INFO                       = 0x00051003;                                                   // 通知房间信息
    int CLI_NTF_ROOM_BEGIN                      = 0x00051004;                                                   // 通知房间开始
    int CLI_NTF_ROOM_MEMBER_STATE               = 0x00051005;                                                   // 通知成员状态
    int CLI_NTF_ROOM_KILL                       = 0x00051006;                                                   // 通知成员被踢
    int CLI_NTF_ROOM_DISSOLVE_AGREE             = 0x00051007;                                                   // 通知成员有人解散同意
    int CLI_NTF_ROOM_DISSOLVE_REJECT            = 0x00051008;                                                   // 通知成员有人解散失败
    int CLI_NTF_ROOM_CHANGE_STATE               = 0x00051009;                                                   // 通知房间状态改变
    int CLI_NTF_ROOM_DISSOLVE                   = 0x0005100a;                                                   // 通知房间解散
    int CLI_NTF_ROOM_DISSOLVE_WAIT_INFO         = 0x0005100b;                                                   // 通知解散等待信息
    int CLI_NTF_ROOM_MAGIC_FACE                 = 0x0005100c;                                                   // 通知魔法表情信息
    int CLI_NTF_ROOM_OFFLINE                    = 0x0005100e;                                                   // 通知成员离线
    int CLI_NTF_ROOM_ONLINE                     = 0x0005100f;                                                   // 通知成员上线
    int CLI_NTF_ROOM_BEGIN_BEFORE               = 0x00051010;                                                   // 通知房间开始前信息
    int CLI_NTF_ROOM_DESK_INFO                  = 0x00051011;                                                   // 通知牌桌信息
    // 通知牌局结果信息
    int CLI_NTF_ROOM_GAMEOVER                   = 0x00051012;
    int CLI_NTF_ROOM_MEMBER_NOT_GUEST           = 0x00051013;                                                   // 通知成员是非游客
    int CLI_NTF_ROOM_MY_CARD                    = 0x00051014;                                                   // 通知自己牌
    int CLI_NTF_ROOM_SHOW_OFF                   = 0x00051015;                                                   // 通知炫耀信息
    int CLI_NTF_ROOM_GAME_FINISH                = 0x00051016;                                                   // 通知牌局完成
    int CLI_NTF_ROOM_LACK_DIAMOND               = 0x00051017;                                                   // 通知钻石不足
    int CLI_NTF_ROOM_DISSOLVE_COUNT_DOWN        = 0x00051018;                                                   // 通知30不准备超时弹解散框


    // 包厢相关
    // 创建包厢
    int CLI_REQ_BOX_CREATE                      = 0x000b0001;
    int CLI_NTF_BOX_CREATE_OK                   = 0x000b0002;                                                   // 创建包厢成功
    int CLI_NTF_BOX_CREATE_FAIL                 = 0x000b0003;                                                   // 创建包厢失败

    int CLI_REQ_BOX_LIST                        = 0x000b0004;                                                   // 获取包厢列表
    int CLI_NTF_BOX_LIST_OK                     = 0x000b0005;                                                   // 获取包厢列表成功
    int CLI_NTF_BOX_LIST_FAIL                   = 0x000b0006;                                                   // 获取包厢列表失败

    int CLI_REQ_BOX_CLOSE                       = 0x000b0007;                                                   // 请求关闭包厢
    int CLI_NTF_BOX_CLOSE_OK                    = 0x000b0008;                                                   // 关闭包厢成功
    int CLI_NTF_BOX_CLOSE_FAIL                  = 0x000b0009;                                                   // 关闭包厢失败

    int CLI_REQ_BOX_JOIN                        = 0x000b000a;                                                   // 请求加入包厢
    int CLI_NTF_BOX_JOIN_OK                     = 0x000b000b;                                                   // 加入包厢成功
    int CLI_NTF_BOX_JOIN_FAIL                   = 0x000b000c;                                                   // 加入包厢失败

    int CLI_REQ_BOX_CREATE_CUSTOM_ROOM          = 0x000b000d;                                                   // 请求创建自定义房间
    int CLI_NTF_BOX_CREATE_CUSTOM_ROOM_OK       = 0x000b000e;                                                   // 创建自定义房间成功
    int CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL     = 0x000b000f;                                                   // 创建自定义房间失败

    int CLI_REQ_BOX_MODIFY_RULE                 = 0x000b0010;                                                   // 修改包厢规则
    int CLI_NTF_BOX_MODIFY_RULE_OK              = 0x000b0011;                                                   // 修改包厢规则成功
    int CLI_NTF_BOX_MODIFY_RULE_FAIL            = 0x000b0012;                                                   // 修改包厢规则失败

    // 获取包厢状态信息
    int CLI_REQ_BOX_STATE_INFO                  = 0x000b0013;
    int CLI_NTF_BOX_STATE_INFO_OK               = 0x000b0014;                                                   // 获取包厢状态信息成功
    int CLI_NTF_BOX_STATE_INFO_FAIL             = 0x000b0015;                                                   // 获取包厢状态信息失败

    int CLI_REQ_BOX_JOIN_OR_LEAVE_BOX           = 0x000b0016;                                                   // 请求加入/离开包厢
    int CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_OK        = 0x000b0017;                                                   // 请求加入/离开包厢成功
    int CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_FAIL      = 0x000b0018;                                                   // 请求加入/离开包厢失败

    int CLI_NTF_BOX_JOIN_FAST_FAIL              = 0x000b0019;                                                   // 请求快速加入包厢失败

	// 请求包厢战绩
    int CLI_REQ_BOX_SCORE                       = 0x000b001c;
    int CLI_NTF_BOX_SCORE_OK                    = 0x000b001d;                                                   // 请求包厢战绩成功
    int CLI_NTF_BOX_SCORE_FAIL                  = 0x000b001e;                                                   // 请求包厢战绩失败

    int CLI_REQ_BOX_CHANGE_NAME                 = 0x000b0022;                                                   // 请求包厢修改名字
    int CLI_NTF_BOX_CHANGE_NAME_OK              = 0x000b0023;                                                   // 通知包厢修改名字成功
    int CLI_NTF_BOX_CHANGE_NAME_FAIL            = 0x000b0024;                                                   // 通知包厢修改名字失败

    // 请求包厢观战的玩家id列表
    int CLI_REQ_BOX_ALL_WATCH_PLAYER            = 0x000b0026;
    int CLI_REQ_BOX_ALL_WATCH_PLAYER_OK         = 0x000b0027;
    int CLI_REQ_BOX_ALL_WATCH_PLAYER_FAIL       = 0x000b0028;
    
    int CLI_REQ_BOX_SIT_DOWN                    = 0x000b1009;                                                   // 请求包厢可少人模式-坐下
    int CLI_NTF_BOX_SIT_DOWN_OK                 = 0x000b100a;                                                   // 通知包厢可少人模式-坐下成功
    int CLI_NTF_BOX_SIT_DOWN_FAIL               = 0x000b100b;                                                   // 通知包厢可少人模式-坐下失败

    // 请求包厢可少人模式-站起
    int CLI_REQ_BOX_SIT_UP                      = 0x000b100c;
    int CLI_NTF_BOX_SIT_UP_OK                   = 0x000b100d;                                                   // 通知包厢可少人模式-站起成功
    int CLI_NTF_BOX_SIT_UP_FAIL                 = 0x000b100e;                                                   // 通知包厢可少人模式-站起失败

    int CLI_NTF_BOX_ADD                         = 0x000b1001;                                                   // 通知添加包厢信息
    int CLI_NTF_BOX_DEL                         = 0x000b1002;                                                   // 通知移除包厢信息
    int CLI_NTF_BOX_RULE                        = 0x000b1003;                                                   // 通知包厢规则作废
    int CLI_NTF_BOX_JOIN                        = 0x000b1004;                                                   // 通知包厢有人加入
    int CLI_NTF_BOX_LEAVE                       = 0x000b1005;                                                   // 通知包厢有人离开
    int CLI_NTF_BOX_ROOM_CHANGE_STATE           = 0x000b1006;                                                   // 通知包厢房间状态改变
    int CLI_NTF_BOX_CHANGE_RULE                 = 0x000b1007;                                                   // 通知包厢修改规则
    int CLI_NTF_BOX_CHANGE_NAME                 = 0x000b1008;                                                   // 通知包厢修改名字

    int CLI_NTF_ARENA_SITUP_FAIL                = 0x0007003a;                                                   // 请求站起竞技场牌桌失败
    int CLI_NTF_ARENA_MATCH_CHANGE_STATE        = 0x00071007;                                                   // 通知竞技场匹配信息改变

    // 楼层
    int CLI_REQ_CLUB_FLOOR_CREATE                            = 100113;                                                   // 请求创建楼层
    int CLI_NTF_CLUB_FLOOR_CREATE_OK                         = 100114;                                                   // 创建楼层成功
    int CLI_NTF_CLUB_FLOOR_CREATE_FAIL                       = 100115;                                                   // 创建楼层失败

    int CLI_REQ_CLUB_FLOOR_CLOSE                             = 100116;                                                   // 请求关闭楼层
    int CLI_NTF_CLUB_FLOOR_CLOSE_OK                          = 100117;                                                   // 关闭楼层成功
    int CLI_NTF_CLUB_FLOOR_CLOSE_FAIL                        = 100118;                                                   // 关闭楼层失败

    int CLI_REQ_CLUB_FLOOR_LIST                              = 100119;                                                   // 请求获取楼层列表
    int CLI_NTF_CLUB_FLOOR_LIST_OK                           = 100120;                                                   // 获取楼层列表成功
    int CLI_NTF_CLUB_FLOOR_LIST_FAIL                         = 100121;                                                   // 获取楼层列表失败

    // 俱乐部
    //通知客户端俱乐部名单信息
    int CLI_NTF_CLUB_LIST_INFO                               = 100112;                                                  // 通知亲友圈列表信息
    //俱乐部创建加入离开
    int CLI_REQ_CLUB_CREATE_CLUB                         = 10000;                                                       // 请求创建俱乐部
    int CLI_NTF_CLUB_CREATE_CLUB_OK                      = 10001;                                                       // 通知创建俱乐部成功
    int CLI_NTF_CLUB_CREATE_CLUB_FAIL                    = 10002;                                                       // 通知创建俱乐部失败

    int CLI_REQ_CLUB_JOIN_CLUB                           = 10007;                                                       // 请求加入俱乐部
    int CLI_NTF_CLUB_JOIN_CLUB_OK                        = 10008;                                                       // 通知加入俱乐部成功
    int CLI_NTF_CLUB_JOIN_CLUB_FAIL                      = 10009;                                                       // 通知加入俱乐部失败

    int CLI_NTF_CLUB_JOIN_CLUB                           = 10010;                                                       // 通知加入俱乐部
    int CLI_NTF_CLUB_APPLY_JOIN_CLUB                     = 10011;                                                       // 通知有人申请加入俱乐部

    int CLI_REQ_CLUB_GET_APPLY_LIST                      = 10012;                                                       // 请求获取申请列表
    int CLI_NTF_CLUB_GET_APPLY_LIST_OK                   = 10013;                                                       // 通知获取申请列表成功
    int CLI_NTF_CLUB_GET_APPLY_LIST_FAIL                 = 10014;                                                       // 通知获取申请列表失败

    int CLI_REQ_CLUB_OP_APPLY_LIST                       = 10015;                                                       // 请求操作申请列表
    int CLI_NTF_CLUB_OP_APPLY_LIST_OK                    = 10016;                                                       // 通知操作申请列表成功
    int CLI_NTF_CLUB_OP_APPLY_LIST_FAIL                  = 10017;                                                       // 通知操作申请列表失败

    int CLI_NTF_CLUB_OP_APPLY_LIST_RESULT                = 10018;                                                       // 通知操作申请列表结果

    int CLI_REQ_CLUB_INVITE_JION_CLUB                    = 10019;                                                       // 请求邀请加入俱乐部
    int CLI_NTF_CLUB_INVITE_JION_CLUB_OK                 = 10020;                                                       // 通知邀请加入俱乐部成功
    int CLI_NTF_CLUB_INVITE_JION_CLUB_FAIL               = 10021;                                                       // 通知邀请加入俱乐部失败

    int CLI_REQ_CLUB_LEAVE_CLUB                          = 10022;                                                       // 请求离开俱乐部
    int CLI_NTF_CLUB_LEAVE_CLUB_OK                       = 10023;                                                       // 通知离开俱乐部成功
    int CLI_NTF_CLUB_LEAVE_CLUB_FAIL                     = 10024;                                                       // 通知离开俱乐部失败

    int CLI_REQ_CLUB_LIST_INFO                           = 10031;                                                       // 通知搜索俱乐部失败
    int CLI_NTF_CLUB_ADD_INFO                            = 10032;                                                       // 通知俱乐部添加信息
    int CLI_NTF_CLUB_DEL_INFO                            = 10033;                                                       // 通知离开俱乐部消息

    int CLI_REQ_CLUB_ADDMEMBER                           = 10050;                                                        // 请求club邀请成员进club
    int CLI_NTF_CLUB_ADDMEMBER_OK                        = 10051;                                                        // 请求club邀请成员进club成功
    int CLI_NTF_CLUB_ADDMEMBER_FAIL                      = 10052;                                                        // 请求club邀请成员进club失败

    int CLI_REQ_CLUB_DELMEMBER                           = 10053;                                                        // 请求club踢出成员
    int CLI_NTF_CLUB_DELMEMBER_OK                        = 10054;                                                        // 请求club踢出成员成功
    int CLI_NTF_CLUB_DELMEMBER_FAIL                      = 10055;                                                        // 请求club踢出成员失败

    int CLI_REQ_CLUB_SEARCH                              = 10028;                                                       // 请求搜索俱乐部
    int CLI_NTF_CLUB_SEARCH_OK                           = 10029;                                                       // 通知搜索俱乐部成功
    int CLI_NTF_CLUB_SEARCH_FAIL                         = 10030;                                                       // 通知搜索俱乐部失败

    int CLI_REQ_CLUB_GET_MEMBER_COUNT                    = 10046;                                                       // 请求获取俱乐部成员数量
    int CLI_NTF_CLUB_GET_MEMBER_COUNT_OK                 = 10047;                                                       // 通知获取俱乐部成员数量成功
    int CLI_NTF_CLUB_GET_MEMBER_COUNT_FAIL               = 10048;                                                       // 通知获取俱乐部成员数量失败

    int CLI_REQ_CLUB_GET_CLUBMEMBER                      = 10074;                                                        // 获取club成员列表
    int CLI_NTF_CLUB_GET_CLUBMEMBER_OK                   = 10075;                                                        // 获取club成员列表成功
    int CLI_NTF_CLUB_GET_CLUBMEMBER_FAIL                 = 10076;                                                        // 获取club成员列表失败

    int CLI_REQ_CLUB_GET_CLUBMEMBER_BY_PARAM             = 10077;                                                        // 获取club成员列表(通过条件参数)
    int CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_OK          = 10078;                                                        // 获取club成员列表成功(通过条件参数)
    int CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_FAIL        = 10079;                                                        // 获取club成员列表失败(通过条件参数)

    int CLI_REQ_CLUB_ALL_LIST_INFO                           = 100122;                                                   // 请求获取总圈列表信息
    int CLI_NTF_CLUB_ALL_LIST_INFO_OK                        = 100123;                                                   // 获取总圈列表信息成功
    int CLI_NTF_CLUB_ALL_LIST_INFO_FAIL                      = 100124;                                                   // 获取总圈列表信息失败

    //俱乐部权限
    int CLI_REQ_CLUB_CREATE_PRIVILEGE                    = 10025;                                                       // 请求获取创建俱乐部的权限
    int CLI_NTF_CLUB_CREATE_PRIVILEGE_OK                 = 10026;                                                       // 通知获取创建俱乐部的权限成功
    int CLI_NTF_CLUB_CREATE_PRIVILEGE_FAIL               = 10027;                                                       // 通知获取创建俱乐部的权限失败

    int CLI_REQ_CLUB_SET_MEMBERJOB                       = 10056;                                                        // 请求club设置成员职位
    int CLI_NTF_CLUB_SET_MEMBERJOB_OK                    = 10057;                                                        // 请求club设置成员职位成功
    int CLI_NTF_CLUB_SET_MEMBERJOB_FAIL                  = 10058;                                                        // 请求club设置成员职位失败

    int CLI_REQ_CLUB_SET_PROHIBIT                        = 10059;                                                        // 请求club设置禁玩
    int CLI_NTF_CLUB_SET_PROHIBIT_OK                     = 10060;                                                        // 请求club设置禁玩成功
    int CLI_NTF_CLUB_SET_PROHIBIT_FAIL                   = 10061;                                                        // 请求club设置禁玩失败

    int CLI_REQ_CLUB_SET_CLUBINFO                        = 10062;                                                        // 请求club设置信息
    int CLI_NTF_CLUB_SET_CLUBINFO_OK                     = 10063;                                                        // 请求club设置信息成功
    int CLI_NTF_CLUB_SET_CLUBINFO_FAIL                   = 10064;                                                        // 请求club设置信息失败

    int CLI_REQ_CLUB_GET_CLUBINFO                        = 10065;                                                        // 请求club获取信息
    int CLI_NTF_CLUB_GET_CLUBINFO_OK                     = 10066;                                                        // 请求club获取信息成功
    int CLI_NTF_CLUB_GET_CLUBINFO_FAIL                   = 10067;                                                        // 请求club获取信息失败

    int CLI_REQ_CLUB_GET_CHAT_PLAYERLIST                 = 10080;                                                        // 获取club能聊天的成员列表
    int CLI_NTF_CLUB_GET_CHAT_PLAYERLIST_OK              = 10081;                                                        // 获取club能聊天的成员列表成功
    int CLI_NTF_CLUB_GET_CHAT_PLAYERLIST_FAIL            = 10082;                                                        // 获取club能聊天的成员列表失败

    int CLI_REQ_CLUB_GET_PLAYER_INFO                     = 10086;                                                        // 获取club玩家个体信息
    int CLI_NTF_CLUB_GET_PLAYER_INFO_OK                  = 10087;                                                        // 获取club玩家个体信息成功
    int CLI_NTF_CLUB_GET_PLAYER_INFO_FAIL                = 10088;                                                        // 获取club玩家个体信息失败

    int CLI_REQ_CLUB_SET_MANAGER_INFO                    = 10089;                                                        // 设置club管理员(合圈后才有)
    int CLI_NTF_CLUB_SET_MANAGER_INFO_OK                 = 10090;                                                        // 设置club管理员成功(合圈后才有)
    int CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL               = 10091;                                                        // 设置club管理员失败(合圈后才有)

    int CLI_REQ_CLUB_GET_MANAGER_INFO                    = 10092;                                                        // 获取club管理员(主圈)
    int CLI_NTF_CLUB_GET_MANAGER_INFO_OK                 = 10093;                                                        // 获取club管理员成功(主圈)
    int CLI_NTF_CLUB_GET_MANAGER_INFO_FAIL               = 10094;                                                        // 获取club管理员失败(主圈)

    int CLI_REQ_CLUB_SET_TREASURER_INFO                  = 10095;                                                        // 设置群财务信息
    int CLI_NTF_CLUB_SET_TREASURER_INFO_OK               = 10096;                                                        // 设置群财务信息成功
    int CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL             = 10097;                                                        // 设置群财务信息失败
    int CLI_NTF_CLUB_SET_TREASURER_INFO                  = 10098;                                                        // 通知群财务信息
    
    int CLI_NTF_CLUB_ADDMEMBER                           = 10100;                                                        // 通知玩家加入club
    int CLI_NTF_CLUB_DELMEMBER                           = 10101;                                                        // 通知玩家离开club
    int CLI_NTF_CLUB_CHANGEMEMBERJOB                     = 10102;                                                        // 通知玩家职位变更
    int CLI_NTF_CLUB_PROHIBITMEMBER                      = 10103;                                                        // 通知玩家禁玩状态
    int CLI_NTF_CLUB_CHANGECLUBINFO                      = 10104;                                                        // 通知修改群信息
    int CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT                 = 10105;                                                        // 通知修改club公告
    int CLI_NTF_CLUB_SET_MANAGER_INFO                    = 10106;                                                        // 通知设置club管理员(合圈后才有)

    //财务上分
    int CLI_REQ_PLAYER_UP_GOLD                           = 10107;                                                        // 请求财务上分
    int CLI_NTF_PLAYER_UP_GOLD_OK                        = 10108;                                                        // 请求财务上分成功
    int CLI_NTF_PLAYER_UP_GOLD_FAIL                      = 10109;                                                        // 请求财务上分失败
    //财务下分
    int CLI_REQ_PLAYER_DOWN_GOLD                         = 10110;                                                        // 请求财务下分
    int CLI_NTF_PLAYER_DOWN_GOLD_OK                      = 10111;                                                        // 请求财务下分成功
    int CLI_NTF_PLAYER_DOWN_GOLD_FAIL                    = 10112;                                                        // 请求财务下分失败
    //获取下分订单
    int CLI_REQ_PLAYER_GET_DOWN_GOLD_ORDER               = 10113;                                                        // 获取下分订单
    int CLI_NTF_PLAYER_GET_DOWN_GOLD_ORDER_OK            = 10114;                                                        // 获取下分订单成功
    int CLI_NTF_PLAYER_GET_DOWN_GOLD_ORDER_FAIL          = 10115;                                                        // 获取下分订单失败
    //财务操作下分订单
    int CLI_REQ_PLAYER_SET_DOWN_GOLD_ORDER               = 10116;                                                        // 财务操作下分订单
    int CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER_OK            = 10117;                                                        // 财务操作下分订单成功
    int CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER_FAIL          = 10118;                                                        // 财务操作下分订单失败
    int CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER               = 10119;                                                        // 通知财务操作下分订单信息
    //通知财务有新的下分订单
    int CLI_NTF_PLAYER_NEW_DOWN_GOLD_ORDER               = 10120;                                                        // 通知财务有新的下分订单
    //获取财务是否有未审核的下分订单
    int CLI_REQ_PLAYER_GET_WAIT_DOWN_GOLD_ORDER               = 10121;                                                   // 获取财务是否有未审核的下分订单
    int CLI_NTF_PLAYER_GET_WAIT_DOWN_GOLD_ORDER_OK            = 10122;                                                   // 获取财务是否有未审核的下分订单成功
    int CLI_NTF_PLAYER_GET_WAIT_DOWN_GOLD_ORDER_FAIL          = 10123;                                                   // 获取财务是否有未审核的下分订单失败
    //获取club成员在本圈中可再次下分的剩余时间
    int CLI_REQ_PLAYER_GET_DOWN_GOLD_TIME                = 10124;                                                        // 获取club成员在本圈中可再次下分的剩余时间
    int CLI_NTF_PLAYER_GET_DOWN_GOLD_TIME_OK             = 10125;                                                        // 获取club成员在本圈中可再次下分的剩余时间成功
    int CLI_NTF_PLAYER_GET_DOWN_GOLD_TIME_FAIL           = 10126;                                                        // 获取club成员在本圈中可再次下分的剩余时间失败

    int CLI_REQ_PLAYER_ENTER_CLUB                            = 100125;                                                   // 请求切换主亲友圈
    int CLI_NTF_PLAYER_ENTER_CLUB_OK                         = 100126;                                                   // 请求切换主亲友圈成功
    int CLI_NTF_PLAYER_ENTER_CLUB_FAIL                       = 100127;                                                   // 请求切换主亲友圈失败

    //房卡消耗
    int CLI_REQ_CLUB_CONSUME_CARD_LIST                       = 100135;                                                   // 请求房卡消耗统计列表
    int CLI_NTF_CLUB_CONSUME_CARD_LIST_OK                    = 100136;                                                   // 请求房卡消耗统计列表成功
    int CLI_NTF_CLUB_CONSUME_CARD_LIST_FAIL                  = 100137;                                                   // 请求房卡消耗统计列表失败

    int CLI_REQ_CLUB_CONSUME_CARD_DAY_LIST                   = 100138;                                                   // 请求房卡消耗统计每天列表
    int CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_OK                = 100139;                                                   // 请求房卡消耗统计每天列表成功
    int CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL              = 100140;                                                   // 请求房卡消耗统计每天列表失败

    int CLI_REQ_CLUB_ROOM_CARD_CONVERT_GOLD              = 10040;                                                       // 请求房卡兑换金币
    int CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_OK           = 10041;                                                       // 通知房卡兑换金币成功
    int CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL         = 10042;                                                       // 通知房卡兑换金币失败

    int CLI_REQ_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD       = 10043;                                                       // 请求获取房卡兑换金币记录
    int CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_OK    = 10044;                                                       // 通知获取房卡兑换金币录成功
    int CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_FAIL  = 10045;                                                       // 通知获取房卡兑换金币记录失败

    //竞技分
    int CLI_NTF_CLUB_VALUE_CAHNGE                       = 10500;                                                        // 通知圈成员竞技值或奖励分变化
    int CLI_REQ_CLUB_SET_GOLD                           = 0x002c100c;                                                   // 请求设置圈竞技场积分
    int CLI_NTF_CLUB_SET_GOLD_OK                        = 0x002c100d;                                                   // 通知设置圈竞技场积分成功
    int CLI_NTF_CLUB_SET_GOLD_FAIL                      = 0x002c100e;                                                   // 通知设置圈竞技场积分失败

    int CLI_REQ_PLAYER_GET_CLUB_GOLDRECORD               = 10083;                                                        // 获取club自己相关金币记录
    int CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_OK            = 10084;                                                        // 获取club自己相关金币记录成功
    int CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_FAIL          = 10085;                                                        // 获取club自己相关金币记录失败

    int CLI_REQ_CLUB_TOTAL_GOLD_REWARD_VALUE_GET         = 10300;                                                       // 请求获取俱乐部竞技分和奖励值
    int CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_OK      = 10301;                                                       // 通知获取俱乐部竞技分和奖励值成功
    int CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_FAIL    = 10302;                                                       // 通知获取俱乐部竞技分和奖励值失败

    int CLI_REQ_GIVE_GOLD                               = 10507; // 赠送竞技分
    int CLI_NTF_GIVE_GOLD_OK                            = 10508;
    int CLI_NTF_GIVE_GOLD_FAIL                          = 10509;

    //奖励分
    //  奖励分兑换成竞技分
    int CLI_REQ_CLUB_EXCHANGE_REWARD_VALUE                   = 100128;
    int CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_OK                = 100129;                                                   //  奖励分兑换成竞技分
    int CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_FALL              = 100130;                                                   //  奖励分兑换成竞技分
    int CLI_NTF_CLUB_ACTIVITY_DIVIDE_OPEN                    = 100134;                                                   // 通知开启奖励分成比例成功

    int CLI_REQ_CLUB_SERVICE_CHARGE_DIVIDE          = 0x00040112;                                               // 修改群管理费比例
    int CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_OK       = 0x00040113;                                               // 修改群管理费比例成功
    int CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_FAIL     = 0x00040114;                                               // 修改群管理费比例失败

    int CLI_REQ_CLUB_SET_DIVIDE_LINE = 0x0004004a;                                                              // 修改群主竞技场分成
    int CLI_NTF_CLUB_SET_DIVIDE_LINE_OK = 0x0004004b;                                                           // 修改群主竞技场分成成功
    int CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL = 0x0004004c;                                                         // 修改群主竞技场分成失败

    int CLI_REQ_CLUB_GET_REWARD_VALUE_RCORD              = 10034;                                                       // 请求获取俱乐部奖励分
    int CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_OK           = 10035;                                                       // 通知获取俱乐部奖励分成功
    int CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_FAIL         = 10036;                                                       // 通知获取俱乐部奖励分失败

    //战绩战况
    // 获取亲友圈战况
    int CLI_REQ_CLUB_GET_WARSITUATION             = 10268;
    int CLI_NTF_CLUB_GET_WARSITUATION_OK          = 10269;                                                   // 获取亲友圈战况成功
    int CLI_NTF_CLUB_GET_WARSITUATION_FAIL        = 10270;                                                   // 获取亲友圈战况失败

    int CLI_REQ_CLUB_MARK_WARSITUATION            = 10271;                                                   // 请求标记某一条战况
    int CLI_NTF_CLUB_MARK_WARSITUATION_OK         = 10272;                                                   // 请求标记某一条战况成功
    int CLI_NTF_CLUB_MARK_WARSITUATION_FAIL       = 10273;                                                   // 请求标记某一条战况失败

    //公告
    int CLI_REQ_CLUB_CHANGE_ANNOUNCEMENT                 = 10068;                                                        // 修改club公告
    int CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_OK              = 10069;                                                        // 修改club公告成功
    int CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_FAIL            = 10070;                                                        // 修改club公告失败

    int CLI_REQ_CLUB_GET_ANNOUNCEMENT                    = 10071;                                                        // 获取club公告
    int CLI_NTF_CLUB_GET_ANNOUNCEMENT_OK                 = 10072;                                                        // 获取club公告成功
    int CLI_NTF_CLUB_GET_ANNOUNCEMENT_FAIL               = 10073;                                                        // 获取club公告失败

    //合圈操作
    int CLI_REQ_CLUB_APPLY_MERGE                         = 10260;                                                       //请求合并亲友圈
    int CLI_NTF_CLUB_APPLY_MERGE_OK                      = 10261;                                                       //请求合并亲友圈成功
    int CLI_NTF_CLUB_APPLY_MERGE_FAIL                    = 10262;                                                       //请求合并亲友圈失败

    int CLI_REQ_CLUB_APPLY_MERGE_OPT                         = 10263;                                                   //处理合并请求
    int CLI_NTF_CLUB_APPLY_MERGE_OPT_OK                      = 10264;                                                   //处理合并请求成功
    int CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL                    = 10265;                                                   //处理合并请求失败
    int CLI_NTF_CLUB_UP_MAIN_CLUB_INFO = 10266;                                                                         //俱乐部合并信息()
    int CLI_NTF_CLUB_MERGE_NEW_CLUB_INFO                     = 10267;                                                   //俱乐部合并信息()

    int CLI_REQ_CLUB_APPLY_LEAVE                         = 10280;                                                       //请求离开主圈
    int CLI_NTF_CLUB_APPLY_LEAVE_OK                      = 10281;                                                       //请求离开主圈成功
    int CLI_NTF_CLUB_APPLY_LEAVE_FAIL                    = 10282;                                                       //请求离开主圈失败

    int CLI_REQ_CLUB_APPLY_LEAVE_OPT                         = 10283;                                                   //处理离开主圈请求
    int CLI_NTF_CLUB_APPLY_LEAVE_OPT_OK                      = 10284;                                                   //处理离开主圈请求成功
    int CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL                    = 10285;                                                   //处理离开主圈请求失败
    int CLI_NTF_CLUB_APPLY_LEAVE_NOTIFY                      = 10286;                                                   //广播通知离开主圈的亲友圈

    //打烊
    int CLI_REQ_CLUB_APPLY_CLOSE                            = 100141;                                                       // 请求亲友圈打烊
    int CLI_NTF_CLUB_APPLY_CLOSE_OK                         = 100142;                                                       // 通知亲友圈打烊成功
    int CLI_NTF_CLUB_APPLY_CLOSE_FAIL                       = 100143;                                                       // 通知亲友圈打烊失败
    int CLI_NTF_CLUB_CLOSE_STATUS                           = 100144;                                                       // 通知亲友圈打烊状态变更

    //小助手
    int CLI_REQ_CLUB_HELPER_INVITE_PLAYERS                  = 100145;                                                       // 请求亲友圈小助手-获取邀请信息列表
    int CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_OK               = 100146;                                                       // 通知亲友圈小助手-获取邀请信息列表成功
    int CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_FAIL             = 100147;                                                       // 通知亲友圈小助手-获取邀请信息列表失败

    int CLI_REQ_CLUB_HELPER_INVITE                          = 100148;                                                       // 请求亲友圈小助手-邀请在线玩家一起游戏
    int CLI_NTF_CLUB_HELPER_INVITE_OK                       = 100149;                                                       // 通知亲友圈小助手-邀请在线玩家一起游戏成功
    int CLI_NTF_CLUB_HELPER_INVITE_FAIL                     = 100150;                                                       // 通知亲友圈小助手-邀请在线玩家一起游戏失败
    int CLI_NTF_CLUB_HELPER_INVITE_NOTICE                   = 100151;                                                       // 通知被邀请人

    int CLI_REQ_CLUB_HELPER_INVITE_SET_INFO                 = 100152;                                                       // 请求亲友圈小助手-获取今日是否接爱邀请状态
    int CLI_NTF_CLUB_HELPER_INVITE_SET_INFO_OK              = 100153;                                                       // 通知亲友圈小助手-获取今日是否接爱邀请状态成功
    int CLI_NTF_CLUB_HELPER_INVITE_SET_INFO_FAIL            = 100154;                                                       // 通知亲友圈小助手-获取今日是否接爱邀请状态失败

    int CLI_REQ_CLUB_HELPER_INVITE_SET                      = 100155;                                                       // 请求亲友圈小助手-设置今日不接受邀请
    int CLI_NTF_CLUB_HELPER_INVITE_SET_OK                   = 100156;                                                       // 通知亲友圈小助手-设置今日不接受邀请成功
    int CLI_NTF_CLUB_HELPER_INVITE_SET_FAIL                 = 100157;                                                       // 通知亲友圈小助手-设置今日不接受邀请失败

    int CLI_REQ_CLUB_HELPER_INVITE_ANSWER                    = 100158;                                                       // 请求亲友圈小助手-响应游戏邀请
    int CLI_NTF_CLUB_HELPER_INVITE_ANSWER_OK                 = 100159;                                                       // 通知亲友圈小助手-响应游戏邀请成功
    int CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL               = 100160;                                                       // 通知亲友圈小助手-响应游戏邀请失败

    int CLI_REQ_CLUB_HELPER_INFO                            = 100161;                                                       // 请求亲友圈小助手-获取小桌面信息
    int CLI_NTF_CLUB_HELPER_INFO_OK                         = 100162;                                                       // 通知亲友圈小助手-获取小桌面信息成功
    int CLI_NTF_CLUB_HELPER_INFO_FAIL                       = 100163;                                                       // 通知亲友圈小助手获取小桌面信息失败
    
    int CLI_REQ_HALL_HELPER_INFO                            = 100164;
     
    //防作弊功能
    int CLI_REQ_CLUB_FORBID_LIST                             = 100100;                                                   // 请求屏蔽列表
    int CLI_NTF_CLUB_FORBID_LIST_OK                          = 100101;                                                   // 请求屏蔽列表成功
    int CLI_NTF_CLUB_FORBID_LIST_FAIL                        = 100102;                                                   // 请求屏蔽列表失败

    int CLI_REQ_CLUB_FORBID_LIST_ADD                         = 100103;                                                   // 添加防作弊
    int CLI_NTF_CLUB_FORBID_LIST_ADD_OK                      = 100104;                                                   // 添加防作弊成功
    int CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL                    = 100105;                                                   // 添加防作弊失败

    int CLI_REQ_CLUB_FORBID_LIST_DEL                         = 100106;                                                   // 删除防作弊
    int CLI_NTF_CLUB_FORBID_LIST_DEL_OK                      = 100107;                                                   // 删除防作弊成功
    int CLI_NTF_CLUB_FORBID_LIST_DEL_FAIL                    = 100108;                                                   // 删除防作弊失败

    int CLI_REQ_CLUB_FORBID_LIST_ADD_MEMBERLIST              = 100109;                                                   // 获取防作弊成员列表
    int CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_OK           = 100110;                                                   // 获取防作弊成员列表成功
    int CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_FAIL         = 100111;                                                   // 获取防作弊成员列表失败

    int CLI_REQ_CLUB_FORBID_SEARCH                           = 100131;                                                   // 请求屏蔽查询列表
    int CLI_NTF_CLUB_FORBID_SEARCH_OK                        = 100132;                                                   // 请求屏蔽查询列表成功
    int CLI_NTF_CLUB_FORBID_SEARCH_FAIL                      = 100133;                                                   // 请求屏蔽查询列表失败

    //排行榜功能
    int CLI_REQ_RANK_LIST                               = 0x002c1041;                                                   // 请求排行榜数据
    int CLI_NTF_RANK_LIST_OK                            = 0x002c1042;                                                   // 请求排行榜数据成功
    int CLI_NTF_RANK_LIST_FAIL                          = 0x002c1043;                                                   // 请求排行榜数据失败

    //活动
    int CLI_REQ_CLUB_ACTIVITY_DIVIDE_INFO                = 10150;                                                        // 请求获取亲友圈奖励分成获取比例
    int CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_OK             = 10151;                                                        // 请求获取亲友圈奖励分成获取比例成功
    int CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_FAIL           = 10152;                                                        // 请求获取亲友圈奖励分成获取比例失败

    int CLI_REQ_CLUB_ACTIVITY_DIVIDE_CHANGE              = 10153;                                                        // 请求修改亲友圈奖励分成获取比例
    int CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_OK           = 10154;                                                        // 请求修改亲友圈奖励分成获取比例成功
    int CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL         = 10155;                                                        // 请求修改亲友圈奖励分成获取比例失败

    // 获取亲友圈任务列表
    int CLI_REQ_CLUB_ACTIVITY_GOLD_INFO                  = 10156;
    int CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_OK               = 10157;                                                       // 获取亲友圈任务列表成功
    int CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_FAIL             = 10158;                                                       // 获取亲友圈任务列表失败

    int CLI_REQ_CLUB_ACTIVITY_GOLD_MODIFY                = 10159;                                                       // 请求修改任务
    int CLI_NTF_CLUB_ACTIVITY_GOLD_MODIFY_OK             = 10160;                                                       // 请求修改任务成功
    int CLI_NTF_CLUB_ACTIVITY_GOLD_MODIFY_FAIL           = 10161;                                                       // 请求修改任务失败

    int CLI_REQ_CLUB_ACTIVITY_GOLD_REMOVE                = 10162;                                                       // 请求删除任务
    int CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_OK             = 10163;                                                       // 请求删除任务成功
    int CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_FAIL           = 10164;                                                       // 请求删除任务失败

    // 获取亲友圈任务奖励
    int CLI_REQ_CLUB_ACTIVITY_GOLD_REWARD                = 10165;
    int CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_OK             = 10166;                                                       // 获取亲友圈任务奖励成功
    int CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL           = 10167;                                                       // 获取亲友圈任务奖励失败

    // 请求获取任务获取奖励分
    int CLI_REQ_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD     = 10037;
    int CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_OK  = 10038;											            // 通知获取任务获取奖励分成功
    int CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_FAIL= 10039;											            // 通知获取任务获取奖励分失败

    int CLI_REQ_CLUB_ONLYUPLINESETGOLD                    = 10168;                                                        // 请求修改亲友圈奖励分成获取比例
    int CLI_NTF_CLUB_ONLYUPLINESETGOLD_OK                 = 10169;                                                        // 请求修改亲友圈奖励分成获取比例成功
    int CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL               = 10170;                                                        // 请求修改亲友圈奖励分成获取比例失败
    int CLI_NTF_CLUB_ONLYUPLINESETGOLD_NOTICE             = 10171;                                                        // 请求修改亲友圈奖励分成获取比例失败

    int CLI_REQ_CLUB_LIKE_GAME                            = 10172;                                                        // 请求修改设置亲友圈玩家喜欢的游戏
    int CLI_NTF_CLUB_LIKE_GAME_OK                         = 10173;                                                        // 请求修改设置亲友圈玩家喜欢的游戏成功
    int CLI_NTF_CLUB_LIKE_GAME_FAIL                       = 10174;                                                        // 请求修改设置亲友圈玩家喜欢的游戏失败

    //获取群邀请码

    int CLI_REQ_CLUB_GET_RECOMMEND_CODE                   = 10200;                                                        // 请求群推荐码

    int CLI_NTF_CLUB_GET_RECOMMEND_CODE_OK                = 10201;                                                        //  请求群推荐码成功

    int CLI_NTF_CLUB_GET_RECOMMEND_CODE_FAIL              = 10202;                                                        //  请求群推荐码失败


    //邀请码进群

    int CLI_REQ_CLUB_JOIN_BY_RECOMMEND_CODE                   = 10205;                                                        // 请求通过群推荐码进群

    int CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_OK                = 10206;                                                        //  请求通过群推荐码进群成功

    int CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_FAIL              = 10207;                                                        //  请求通过群推荐码进群失败


    //获取下级圈的管理费
    int CLI_REQ_CLUB_GET_MAINCHARGE                           = 10208;                                                        // 请求获取下级圈管理费
    int CLI_NTF_CLUB_GET_MAINCHARGE_OK                        = 10209;                                                        // 请求获取下级圈管理费成功
    int CLI_NTF_CLUB_GET_MAINCHARGE_FAIL                      = 10210;                                                        // 请求获取下级圈管理费失败

    //修改下级圈的管理费
    int CLI_REQ_CLUB_SET_MAINCHARGE                           = 10211;                                                        // 请求修改下级圈管理费
    int CLI_NTF_CLUB_SET_MAINCHARGE_OK                        = 10212;                                                        // 请求修改下级圈管理费成功
    int CLI_NTF_CLUB_SET_MAINCHARGE_FAIL                      = 10213;                                                        // 请求修改下级圈管理费失败


    // 扑克--扎金花
    int CLI_REQ_POKER_FGF_COMPARE               = 0x000f0001;                                                   // 请求扎金花比牌
    int CLI_NTF_POKER_FGF_COMPARE_OK            = 0x000f0002;                                                   // 通知扎金花比牌成功
    int CLI_NTF_POKER_FGF_COMPARE_FAIL          = 0x000f0003;                                                   // 通知扎金花比牌失败
    int CLI_REQ_POKER_FGF_ADD_NOTE              = 0x000f0004;                                                   // 请求扎金花加注
    int CLI_NTF_POKER_FGF_ADD_NOTE_OK           = 0x000f0005;                                                   // 通知扎金花加注成功
    int CLI_NTF_POKER_FGF_ADD_NOTE_FAIL         = 0x000f0006;                                                   // 通知扎金花加注失败
    int CLI_REQ_POKER_FGF_FOLLOW_NOTE           = 0x000f0007;                                                   // 请求扎金花跟注
    int CLI_NTF_POKER_FGF_FOLLOW_NOTE_OK        = 0x000f0008;                                                   // 通知扎金花跟注成功
    int CLI_NTF_POKER_FGF_FOLLOW_NOTE_FAIL      = 0x000f0009;                                                   // 通知扎金花跟注失败
    int CLI_NTF_POKER_FGF_COMPARE_RESULT        = 0x000f1001;                                                   // 通知扎金花比牌结果
    int CLI_NTF_POKER_FGF_NOTE                  = 0x000f1002;                                                   // 通知扎金花下注
    int CLI_NTF_POKER_FGF_LOOP                  = 0x000f1003;                                                   // 通知扎金花回合
    int CLI_NTF_POKER_FGF_OPERATE               = 0x000f1004;                                                   // 通知扎金花可操作
    int CLI_NTF_POKER_FGF_LOOK                  = 0x000f1005;                                                   // 通知扎金花看牌
    int CLI_NTF_POKER_FGF_AUTO_COMPARE_RESULT   = 0x000f1006;                                                   // 通知扎金花自动比牌结果

    // 获取扎金花牛牛战绩
    int CLI_REQ_ARENA_SCORE_RECORD                    = 10600;
	// 获取扎金花牛牛战绩成功
    int CLI_NTF_ARENA_SCORE_RECORD_OK                 = 10601;
	// 获取大厅里扎金花牛牛战绩成功
    int CLI_NTF_ARENA_SCORE_RECORD_OK_1               = 10603;
    int CLI_NTF_ARENA_SCORE_RECORD_FAIL               = 10602;                                                   // 获取扎金花牛牛战绩失败


    //十三水
    int CLI_REQ_POKER_THIRTEEN_TAKE             = 0x00110001;                                                   // 请求十三水出牌
    int CLI_NTF_POKER_THIRTEEN_TAKE_OK          = 0x00110002;                                                   // 十三水出牌成功
    int CLI_NTF_POKER_THIRTEEN_TAKE_FAIL        = 0x00110003;                                                   // 十三水出牌失败
    int CLI_REQ_POKER_THIRTEEN_SORT_CARD        = 0x00110004;                                                   // 请求十三水理牌
    int CLI_NTF_POKER_THIRTEEN_SORT_CARD_OK     = 0x00110005;                                                   // 十三水理牌成功
    int CLI_NTF_POKER_THIRTEEN_SORT_CARD_FAIL   = 0x00110006;                                                   // 十三水理牌失败
    int CLI_NTF_POKER_THIRTEEN_TAKE             = 0x00111001;                                                   // 通知十三水出牌完成
    int CLI_NTF_POKER_THIRTEEN_DEAL_CARD        = 0x00111005;                                                   // 通知十三水发牌
    int CLI_NTF_POKER_THIRTEEN_LIPAI_BEGIN      = 0x00111011;                                                   // 通知十三水理牌开始
    // 打拱
    int CLI_REQ_POKER_ARCH_BID                  = 0x00170001;                                                   // 打拱请求叫牌
    int CLI_NTF_POKER_ARCH_BID_OK               = 0x00170002;                                                   // 打拱叫牌成功
    int CLI_NTF_POKER_ARCH_BID_FAIL             = 0x00170003;                                                   // 打拱叫牌失败
    int CLI_REQ_POKER_ARCH_OB                   = 0x00170004;                                                   // 打拱请求查看盟友的牌
    int CLI_NTF_POKER_ARCH_OB_OK                = 0x00170005;                                                   // 打拱查看盟友的牌成功
    int CLI_NTF_POKER_ARCH_OB_FAIL              = 0x00170006;                                                   // 打拱查看盟友的牌失败
    int CLI_REQ_POKER_ARCH_SORT_CARD            = 0x00170007;                                                   // 打拱请求理牌
    int CLI_NTF_POKER_ARCH_SORT_CARD_OK         = 0x00170008;                                                   // 打拱理牌成功
    int CLI_NTF_POKER_ARCH_SORT_CARD_FAIL       = 0x00170009;                                                   // 打拱理牌失败
    int CLI_NTF_POKER_ARCH_BID                  = 0x00171001;                                                   // 打拱通知叫牌
    int CLI_NTF_POKER_ARCH_BID_RESULT           = 0x00171002;                                                   // 打拱通知叫牌结果
    int CLI_NTF_POKER_ARCH_SCORE                = 0x00171003;                                                   // 打拱玩家得分

    //牌九
    int CLI_NTF_POKER_PAI_GOW_PREDEALCARD_INFO  = 10700;                                                        // 通知上一轮的牌
    int CLI_NTF_POKER_PAI_GOW_ROB_BANKER_BEGIN  = 10701;                                                        // 通知开始抢庄
    int CLI_NTF_POKER_PAI_GOW_ROB_BANKER        = 10702;                                                        // 通知玩家抢庄信息
    int CLI_NTF_POKER_PAI_GOW_ROB_BANKER_INFO   = 10703;                                                        // 通知所有玩家抢庄信息
    int CLI_NTF_POKER_PAI_GOW_REBET_BEGIN       = 10704;                                                        // 通知开始下注
    int CLI_NTF_POKER_PAI_GOW_REBET             = 10705;                                                        // 广播某个玩家的下注数据
    int CLI_NTF_POKER_PAI_GOW_REBET_INFO        = 10706;                                                        // 通知玩家下注数据
    int CLI_NTF_POKER_PAI_GOW_OPNE_CARD_BEGIN   = 10707;                                                        // 通知开始开牌
    int CLI_NTF_POKER_PAI_GOW_OPEN_CARD_INFO    = 10708;                                                        // 通知开牌信息
    int CLI_NTF_POKER_PAI_GOW_OPNE_CARD         = 10709;                                                        // 广播每个玩家的开牌数据
    int CLI_NTF_POKER_PAI_GOW_ROB_BANKER_RESULT = 10710;                                                        // 广播抢庄结果
    int CLI_NTF_POKER_PAI_GOW_OUTHOT_INFO       = 10711;                                                        // 广播切锅信息
    int CLI_NTF_POKER_PAI_GOW_KEEP_INFO         = 10712;                                                        // 广播续锅信息
    int CLI_NTF_POKER_PAI_GOW_CARD_INFO         = 10713;                                                        // 通知牌信息
    int CLI_NTF_POKER_PAI_GOW_LOOP              = 10714;                                                        // 通知轮数信息
    int CLI_NTF_POKER_PAI_GOW_BEGIN_READY       = 10715;                                                        // 通知开始准备
    int CLI_NTF_POKER_PAI_GOW_READY             = 10716;                                                        // 广播某个玩家准备
    int CLI_NTF_POKER_PAI_GOW_READY_INFO        = 10717;                                                        // 通知准备信息
    int CLI_NTF_POKER_PAI_GOW_GAME_OVER_LOOP    = 10718;                                                        // 通知小局结束信息
    int CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_INFO    = 10719;                                                        // 通知续锅信息
    int CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_AGAIN   = 10720;                                                        // 通知开始续锅信息
    int CLI_NTF_POKER_PAI_GOW_HOT_OUT_INFO      = 10721;                                                        // 通知揭锅信息
    int CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_OUT     = 10722;                                                        // 通知开始揭锅信息
    int CLI_REQ_POKER_PAI_GOW_READY             = 10723;                                                        // 请求准备
    int CLI_NTF_POKER_PAI_GOW_READY_OK          = 10724;                                                        // 请求准备成功
    int CLI_NTF_POKER_PAI_GOW_READY_FAIL        = 10725;                                                        // 请求准备失败
    int CLI_REQ_POKER_PAI_GOW_REBET             = 10726;                                                        // 请求牌九下注
    int CLI_NTF_POKER_PAI_GOW_REBET_OK          = 10727;                                                        // 牌九下注成功
    int CLI_NTF_POKER_PAI_GOW_REBET_FAIL        = 10728;                                                        // 牌九下注失败
    int CLI_REQ_POKER_PAI_GOW_OPEN              = 10729;                                                        // 牌九请求开牌
    int CLI_NTF_POKER_PAI_GOW_OPEN_OK           = 10730;                                                        // 牌九开牌成功
    int CLI_NTF_POKER_PAI_GOW_OPEN_FAIL         = 10731;                                                        // 牌九开牌失败
    int CLI_REQ_POKER_PAI_GOW_ROB_BANKER        = 10732;                                                        // 请求抢庄
    int CLI_REQ_POKER_PAI_GOW_OUT_HOT           = 10733;                                                        // 切锅
    int CLI_NTF_POKER_PAI_GOW_OUT_HOT_OK        = 10734;                                                        // 切锅成功
    int CLI_NTF_POKER_PAI_GOW_OUT_HOT_FAIL      = 10735;                                                        // 切锅失败
    int CLI_REQ_POKER_PAI_GOW_HOT_AGAIN         = 10739;                                                        // 请求续锅
    int CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_OK      = 10740;                                                        // 请求续锅成功
    int CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_FAIL    = 10741;                                                        // 请求续锅失败
    int CLI_REQ_POKER_PAI_GOW_HOT_OUT           = 10742;                                                        // 请求揭锅
    int CLI_NTF_POKER_PAI_GOW_HOT_OUT_OK        = 10743;                                                        // 请求揭锅成功
    int CLI_NTF_POKER_PAI_GOW_HOT_OUT_FAIL      = 10744;                                                        // 请求揭锅失败

    //百人场
    int CLI_NTF_ARENA_HUNDRED_JOIN_OK           = 12000;                                                        // 加入百人场成功
    int CLI_NTF_ARENA_HUNDRED_JOIN_FAIL         = 12001;                                                        // 加入百人场失败
    int CLI_REQ_ARENA_HUNDRED_LEAVE             = 0x0007004d;                                                   // 请求离开百人场
    int CLI_NTF_ARENA_HUNDRED_LEAVE_OK          = 0x0007004e;                                                   // 离开百人场成功
    int CLI_NTF_ARENA_HUNDRED_LEAVE_FAIL        = 0x0007004f;                                                   // 离开百人场失败
    int CLI_REQ_ARENA_HUNDRED_UP_BANKER         = 0x0007003b;                                                   // 请求上庄
    int CLI_NTF_ARENA_HUNDRED_UP_BANKER_OK      = 0x0007003c;                                                   // 上庄成功
    int CLI_NTF_ARENA_HUNDRED_UP_BANKER_FAIL    = 0x0007003d;                                                   // 上庄失败
    int CLI_REQ_ARENA_HUNDRED_DOWN_BANKER       = 0x00070050;                                                   // 请求下庄
    int CLI_NTF_ARENA_HUNDRED_DOWN_BANKER_OK    = 0x00070051;                                                   // 下庄成功
    int CLI_NTF_ARENA_HUNDRED_DOWN_BANKER_FAIL  = 0x00070052;                                                   // 下庄失败
    int CLI_REQ_ARENA_HUNDRED_BANKER_LIST       = 0x00070041;                                                   // 获取庄家列表
    int CLI_NTF_ARENA_HUNDRED_BANKER_LIST_OK    = 0x00070042;                                                   // 获取庄家列表成功
    int CLI_NTF_ARENA_HUNDRED_BANKER_LIST_FAIL  = 0x00070043;                                                   // 获取庄家列表失败
    int CLI_REQ_ARENA_HUNDRED_SELFBANKER_LIST   = 0x00070062;                                                   // 获取自己上庄列表
    int CLI_NTF_ARENA_HUNDRED_SELFBANKER_LIST_OK= 0x00070063;                                                   // 获取自己上庄列表成功
    int CLI_NTF_ARENA_HUNDRED_SELFBANKER_LIST_FAIL= 0x00070064;                                                 // 获取自己上庄列表失败
    int CLI_REQ_ARENA_HUNDRED_PLAYER_LIST       = 0x00070044;                                                   // 获取玩家列表
    int CLI_NTF_ARENA_HUNDRED_PLAYER_LIST_OK    = 0x00070045;                                                   // 获取玩家列表成功
    int CLI_NTF_ARENA_HUNDRED_PLAYER_LIST_FAIL  = 0x00070046;                                                   // 获取玩家列表失败
    int CLI_REQ_ARENA_HUNDRED_RECORD            = 0x00070047;                                                   // 获取记录
    int CLI_NTF_ARENA_HUNDRED_RECORD_OK         = 0x00070048;                                                   // 获取记录成功
    int CLI_NTF_ARENA_HUNDRED_RECORD_FAIL       = 0x00070049;                                                   // 获取记录失败
    int CLI_REQ_ARENA_HUNDRED_REB               = 0x0007003e;                                                   // 请求下注
    int CLI_NTF_ARENA_HUNDRED_REB_OK            = 0x0007003f;                                                   // 下注成功
    int CLI_NTF_ARENA_HUNDRED_REB_FAIL          = 0x00070040;                                                   // 下注失败
    int CLI_REQ_ARENA_HUNDRED_VIP_SEAT_OP       = 0x0007005c;                                                   // 请求百人场vip座位坐下/站起
    int CLI_NTF_ARENA_HUNDRED_VIP_SEAT_OP_OK    = 0x0007005d;                                                   // 请求百人场vip座位坐下/站起成功
    int CLI_NTF_ARENA_HUNDRED_VIP_SEAT_OP_FAIL  = 0x0007005e;                                                   // 请求百人场vip座位坐下/站起失败
    int CLI_NTF_ARENA_HUNDRED_READY             = 0x0007100a;                                                   // 通知百人开始准备
    int CLI_NTF_ARENA_HUNDRED_REB               = 0x0007100b;                                                   // 通知百人开始下注
    int CLI_NTF_ARENA_HUNDRED_OPEN_CARD         = 0x0007100c;                                                   // 通知百人开牌
    int CLI_NTF_ARENA_HUNDRED_OVER              = 0x0007100d;                                                   // 通知百人结束
    int CLI_NTF_ARENA_HUNDRED_REB_INFO          = 0x0007100e;                                                   // 通知百人下注信息
    int CLI_NTF_ARENA_HUNDRED_VIP_SEAT_INFO     = 0x00071014;                                                   // 通知百人场VIP座位信息
    int CLI_REQ_ARENA_HUNDRED_BANKER_RECORD            = 0x00071016;                                            // 获取上庄记录
    int CLI_NTF_ARENA_HUNDRED_BANKER_RECORD_OK         = 0x00071017;                                            // 获取上庄记录成功
    int CLI_NTF_ARENA_HUNDRED_BANKER_RECORD_FAIL       = 0x00071018; 											// 获取上庄记录失败
    int CLI_NTF_ARENA_HUNDRED_REB_ALL_INFO          = 0x00071025;                                      // 通知投注人下注信息

    //机器人
    int CLI_REQ_ROBOT_ROOM_JOIN                       = 0x00100000;
    int CLI_REQ_ROBOT_GOOD_CARD                       = 0x00100001;
    
    static void init() {
        /** json **/
        JsonDecoder.registerCommand(ERROR, ErrorMsg.class);

        //客户端请求登录
        JsonDecoder.registerCommand(CLI_REQ_LOGIN, PCLILoginInfo.class);
        //通知客户端登录OK
        JsonDecoder.registerCommand(CLI_NTF_LOGIN_OK, null);
        //通知客户端登录失败
        JsonDecoder.registerCommand(CLI_NTF_LOGIN_FAIL, ErrorCode.class);
        //客户端请求重新登录
        JsonDecoder.registerCommand(CLI_REQ_RELOGIN, PCLILoginInfo.class);
        //客户端请求重新登录OK
        JsonDecoder.registerCommand(CLI_NTF_RELOGIN_OK, null);
        //客户端请求重新登录失败
        JsonDecoder.registerCommand(CLI_NTF_RELOGIN_FAIL, ErrorCode.class);

        /** 聊天相关 **/
        //客户端请求聊天说话
        JsonDecoder.registerCommand(CLI_REQ_CHAT_SAY, PCLIChatReqSayInfo.class);
        //通知客户端聊天说话OK
        JsonDecoder.registerCommand(CLI_NTF_CHAT_SAY_OK, PCLIChatNtfSayOkInfo.class);
        //通知客户端聊天说话失败
        JsonDecoder.registerCommand(CLI_NTF_CHAT_SAY_FAIL, ErrorCode.class);
        //客户端请求聊天，得到消息
        JsonDecoder.registerCommand(CLI_REQ_CHAT_GET_MSG, PCLIChatReqGetMsgInfo.class);
        //通知客户端请求聊天，得到消息OK
        JsonDecoder.registerCommand(CLI_NTF_CHAT_GET_MSG_OK, PCLIChatNtfGetMsgOkInfo.class);
        //通知客户端请求聊天，得到消息失败
        JsonDecoder.registerCommand(CLI_NTF_CHAT_GET_MSG_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CHAT_GET_MSG_ACK, PCLIChatReqGetMsgAckInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_GET_MSG_ACK_OK, PCLIChatNtfGetMsgAckOkInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_GET_MSG_ACK_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CHAT_RECALL, PCLIChatReqRecallMsgInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_RECALL_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_RECALL_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CHAT_UPDATE_MSG_ACK, PCLIChatReqUpdateMsgAckInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_UPDATE_MSG_ACK_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_UPDATE_MSG_ACK_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CHAT_DEL, PCLIChatReqDelMsgInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_DEL_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_DEL_FAIL, ErrorMsg.class);
        JsonDecoder.registerCommand(CLI_REQ_CHAT_DEL_RECALL, PCLIChatReqDelRecallMsgInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_DEL_RECALL_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_DEL_RECALL_FAIL, ErrorMsg.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_MSG, PCLIChatMsg.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_LAST_MSG_ID, PCLIChatNtfLastMsgUidInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CHAT_UPDATE_MSG, PCLIChatMsg.class);




        // 房间相关
        JsonDecoder.registerCommand(CLI_REQ_ROOM_CREATE, PCLIRoomReqCreateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_CREATE_OK, PCLIRoomBriefInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_CREATE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_JOIN, PCLIRoomReqJoinInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_JOIN_OK, PCLIRoomBriefInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_JOIN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_READY, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_READY_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_READY_FAIL, PCLIRoomReadyFailInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_KILL, PCLIRoomReqKillInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_KILL_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_KILL_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_DISSOLVE, PCLIRoomReqDissolveInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_DISSOLVE_OP, PCLIRoomReqDissolveOpInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_OP_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_OP_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_SCORE_RECORD, PCLIRoomReqScoreRecordInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SCORE_RECORD_OK, PCLIRoomNtfScoreRecordInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SCORE_RECORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_REPORT, PCLIArenaReqReportInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_REPORT_OK, PCLIArenaNtfReportInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_REPORT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_MAGIC_FACE, PCLIRoomReqMagicFaceInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_MAGIC_FACE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_MAGIC_FACE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_LOCATION_RELATION, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LOCATION_RELATION_OK, PCLIRoomNtfLocationRelationInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LOCATION_RELATION_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_LEAVE_V2, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LEAVE_V2_OK, PCLIRoomNtfLeaveStateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LEAVE_V2_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_DESK_INFO, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DESK_INFO_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DESK_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_SHORTTALK, PCLIPokerReqShortTalkInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SHORTTALK_OK, PCLIPokerNTFShortTalkInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SHORTTALK_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_SITDOWN, PCLIRoomReqSitDown.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SITDOWN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SITDOWN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_SITUP, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SITUP_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SITUP_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_LOOK_DESK_INFO, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LOOK_DESK_INFO_OK, PCLIRoomNtfDeskInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LOOK_DESK_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_NEXT_CARD, PCLIRoomReqNextCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_NEXT_CARD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_NEXT_CARD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_SHOW_OFF, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SHOW_OFF_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SHOW_OFF_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ROOM_CHANGE_SEATS, PCLIRoomReqChangeSeat.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_CHANGE_SEATS_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_CHANGE_SEATS_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LEAVE, PCLIRoomNtfLeaveInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_INFO, PCLIRoomInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_BEGIN, PCLIRoomNtfBeginInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_MEMBER_STATE, PCLIRoomNtfMemberStateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_KILL, PCLIRoomNtfKillInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_AGREE, PCLIRoomNtfDissolveAgreeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_REJECT, PCLIRoomNtfDissolveRejectInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_CHANGE_STATE, PCLIRoomNtfChangeStateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE, PCLIRoomNtfDissolveInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_WAIT_INFO, PCLIRoomNtfDissolveWaitInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_MAGIC_FACE, PCLIRoomNtfMagicFaceInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_OFFLINE, PCLIRoomNtfOfflineInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_ONLINE, PCLIRoomNtfOnlineInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_BEGIN_BEFORE, PCLIRoomNtfBeginBeforeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DESK_INFO, PCLIRoomDeskInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_GAMEOVER, PCLIRoomGameOverInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_MEMBER_NOT_GUEST, PCLIRoomNtfMemberNotGuest.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_MY_CARD, PCLIRoomNtfMyHandCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_SHOW_OFF, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_GAME_FINISH, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_LACK_DIAMOND, null);
        JsonDecoder.registerCommand(CLI_NTF_ROOM_DISSOLVE_COUNT_DOWN, PCLIRoomNtfDissolveCountDown.class);

        // 麻将相关
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_FUMBLE, PCLIMahjongReqFumbleInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_FUMBLE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_FUMBLE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_TAKE, PCLIMahjongReqTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_TAKE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_TAKE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_BUMP, PCLIMahjongReqBumpInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BUMP_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BUMP_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_BAR, PCLIMahjongReqBarInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BAR_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BAR_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_HU, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_HU_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_HU_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_PASS, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_PASS_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_PASS_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_SELECT, PCLIMahjongReqSelectInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_SELECT_PIAO, PCLIMahjongReqSelectPiaoInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_PIAO_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_PIAO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_BRIGHT, PCLIMahjongReqBrightInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BRIGHT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BRIGHT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_SHU_KAN, PCLIMahjongReqShuKanInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHU_KAN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHU_KAN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_HUAN_PAI, PCLIMahjongReqHuanPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_HUAN_PAI_OK, PCLIMahjongNtfHuanPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_HUAN_PAI_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_SHUAI_PAI, PCLIMahjongReqShuaiPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHUAI_PAI_OK, PCLIMahjongNtfShuaiPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHUAI_PAI_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_DING_QUE, PCLIMahjongReqDingQue.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_DING_QUE_OK, PCLIMahjongNtfDingQue.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_DING_QUE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_EAT, PCLIMahjongReqEatInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_EAT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_EAT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_XUAN_ZENG, PCLIMahjongReqXuanZeng.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_XUAN_ZENG_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_XUAN_ZENG_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_XUAN_PIAO, PCLIMahjongReqXuanPiao.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_XUAN_PIAO_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_XUAN_PIAO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_START_HU, PCLIMahjongReqStartHu.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_START_HU_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_START_HU_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_YANG_PAI, PCLIMahjongReqYangPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_YANG_PAI_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_YANG_PAI_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_SELECT_OPEN_BAR, PCLIMahjongReqSelectOpenBar.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_OPEN_BAR_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_OPEN_BAR_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_OPEN_BAR_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_OPEN_BAR_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAHJONG_TING, PCLIMahjongReqTingInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_TING_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_TING_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_FUMBLE, PCLIMahjongNtfFumbleInfoByKWX.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_TAKE, PCLIMahjongNtfTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BUMP, PCLIMahjongNtfBumpInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BAR, PCLIMahjongNtfBarInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_HU, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_CAN_FUMBLE, PCLIMahjongNtfCanFumbleInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_CAN_OPERATE, PCLIMahjongNtfCanOperateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT, PCLIMahjongNtfSelectInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_PIAO, PCLIMahjongNtfSelectPiaoInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_PIAO_VALUE, PCLIMahjongNtfSelectPiaoValueInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_HU_INFO, PCLIMahjongNtfHuInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BUY_HORSE_INFO, PCLIMahjongNtfBuyHorseInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_GANG_SCORE_INFO, PCLIMahjongNtfGangScoreInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BRIGHT, PCLIMahjongNtfBrightInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_CAN_TAKE, PCLIMahjongNtfCanTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SELECT_PIAO_INFO, PCLIMahjongNtfSelectFlutterInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_START_TAKE, PCLIMahjongNtfStartTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_CHANGEPAO_CNT, PCLIMahjongNtfChangePaoCntInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHU_KAN_INFO, PCLIMahjongNtfSelectShuKanInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHU_KAN_VALUE, PCLIMahjongNtfShuKanValueInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHU_KAN, PCLIMahjongNtfShuKanInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_HUAN_PAI, PCLIMahjongNtfBeginHuanPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_END_HUAN_PAI, PCLIMahjongNtfEndHuanPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_HUAN_PAI_INFO, PCLIMahjongNtfHuanPaiInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_SHUAI_PAI, PCLIMahjongNtfBeginShuaiPai.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_END_SHUAI_PAI, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_SHUAI_PAI_INFO, PCLIMahjongNtfShuaiPaiInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_DING_QUE, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_END_DING_QUE, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_DING_QUE_INFO, PCLIMahjongNtfDingQueInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_EAT, PCLIMahjongNtfEatInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_XUAN_ZENG, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_END_XUAN_ZENG, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_XUAN_ZENG_INFO, PCLIMahjongNtfXuanZengInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_DESK_SHOW_INFO, PCLIMahjongNtfDeskShowInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_XUAN_PIAO, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_END_XUAN_PIAO, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_XUAN_PIAO_INFO, PCLIMahjongNtfXuanPiaoInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_START_HU, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_END_START_HU, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_START_HU_INFO, PCLIMahjongNtfStartHuInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_YANG_PAI, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_END_YANG_PAI, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_YANG_PAI, PCLIMahjongNtfYangPaiInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_LAI_PI, PCLIMahjongNtfLaiPiInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_LAST_CARD, PCLIMahjongNtfLastCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_LAST_CARD, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_CS_OPEN_BAR, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_CS_OPEN_BAR_INFO, PCLIMahjongNtfOpenBarInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_CRAP_AND_CARD, PCLIMahjongNtfCrapAndCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_TING, PCLIMahjongNtfTingInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_CAN_TING, PCLIMahjongNtfCanTingInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BEGIN_BU_HUA, null);
        JsonDecoder.registerCommand(CLI_NTF_MAHJONG_BU_HUA_INFO, PCLIMahjongNtfBuHuaInfo.class);



        // 玩家相关
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_ICON, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_ICON_OK, PCLIPlayerNtfChangeIcon.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_ICON_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_UPLOAD_ICON_SUCC, PCLIPlayerReqUploadIconSucc.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_UPLOAD_ICON_SUCC_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_UPLOAD_ICON_SUCC_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_NAME, PCLIPlayerReqChangeName.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_NAME_OK, PCLIPlayerNtfChangeNameInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_NAME_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_LOCATION, PCLIPlayerReqChangeLocation.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_LOCATION_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_LOCATION_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_SEX, PCLIPlayerReqChangeSex.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_SEX_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_SEX_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_RECOMMEND, PCLIPlayerReqRecommendInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_RECOMMEND_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_RECOMMEND_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_RECOMMEND_LIST, PCLIPlayerReqRecommendListInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_RECOMMEND_LIST_OK, PCLIPlayerNtfRecommendListInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_RECOMMEND_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_VISIT_CARD, PCLIPlayerReqSetVisitCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_VISIT_CARD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_VISIT_CARD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_VISIT_CARD_GET, PCLIPlayerReqVisitCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_VISIT_CARD_GET_OK, PCLIPlayerNtfVisitCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_VISIT_CARD_GET_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_RECOMMEND_INFO, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_RECOMMEND_INFO_OK, PCLIPlayerNtfRecommendInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_RECOMMEND_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_BORN, PCLIPlayerReqChangeBorn.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_BORN_OK, PCLIPlayerNtfChangeBornInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_BORN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_SIGNATURE, PCLIPlayerReqChangeSignature.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_SIGNATURE_OK, PCLIPlayerNtfChangeSignatureInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_SIGNATURE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_EMOTION, PCLIPlayerReqChangeEmotion.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_EMOTION_OK, PCLIPlayerNtfChangeEmotionInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_EMOTION_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_ADD_SHOW_IMAGE, PCLIPlayerNtfAddShowImageInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_ADD_SHOW_IMAGE_OK, PCLIPlayerNtfAddShowImageInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_ADD_SHOW_IMAGE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_DEL_SHOW_IMAGE, PCLIPlayerReqDelShowImage.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_DEL_SHOW_IMAGE_OK, PCLIPlayerNtfDelShowImageInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_DEL_SHOW_IMAGE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_REPLACE_SHOW_IMAGE, PCLIPlayerReqReplaceShowImage.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_REPLACE_SHOW_IMAGE_OK, PCLIPlayerNtfReplaceShowImageInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_REPLACE_SHOW_IMAGE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_EXCHANGE_SHOW_IMAGE, PCLIPlayerReqExchangeShowImage.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_OK, PCLIPlayerNtfExchangeShowImageInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_COVER, PCLIPlayerReqChangeCover.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_COVER_OK, PCLIPlayerNtfChangeCoverInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_COVER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_SYNC_GPS, PCLIPlayerReqSyncGps.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_SYNC_GPS_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_SYNC_GPS_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_PAY_PASSWORD, PCLIPlayerReqChangePayPassWord.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_IS_SET_PAY_PASSWORD, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_IS_SET_PAY_PASSWORD_OK, PCLIPlayerNtfIsSetPayPassWordInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_IS_SET_PAY_PASSWORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_GET_INFO, PLGetAccountInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_INFO_OK, PLGetAccountRespInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_INFO_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_PLAYER_VERIFY_PAY_PASSWORD, PCLIPlayerReqVerifyPayPassword.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_VERIFY_PAY_PASSWORD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_VERIFY_PAY_PASSWORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_PROPERTY, PCLIPlayerNtfChangePropertyInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_REMOTE_LOGIN, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_KILL, PCLIPlayerNtfKill.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_LOGIN_FINISH, null);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_GET_WECHAT_ASSISTANT, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_OK, PCLIPlayerNtfGetWechatAssistant.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_WECHAT, PCLIPlayerReqChangeWechat.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_WECHAT_OK, PCLIPlayerNtfChangeWechat.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_WECHAT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE_OK, PCLIPlayerNtfGetNoNeedPayPassword.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE, PCLIPlayerReqModifyNoNeedPayPassword.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_OK, PCLIPlayerNtfModifyNoNeedPayPassword.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_CHANGE_BANKCARD, PCLIPlayerReqChangeBankCard.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_BANKCARD_OK, PCLIPlayerNtfChangebankCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_CHANGE_BANKCARD_FAIL, ErrorCode.class);


        // 邮件相关
        JsonDecoder.registerCommand(CLI_REQ_MAIL_READ, PCLIMailReqReadInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_READ_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_READ_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAIL_GIVE_ITEM, PCLIMailReqReceiveItemInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_GIVE_ITEM_OK, PCLIMailNtfReceiveItemInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_GIVE_ITEM_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAIL_QUICK_GIVE_ITEM, null);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_QUICK_GIVE_ITEM_OK, PCLIMailNtfReceiveItemInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_QUICK_GIVE_ITEM_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAIL_DEL, PCLIMailReqDelInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_DEL_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_DEL_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_MAIL_QUICK_DEL, null);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_QUICK_DEL_OK, PCLIMailNtfDelInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_QUICK_DEL_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_INFO, PCLIMailNtfInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_MAIL_ADD, PCLIMailNtfAddInfo.class);

        // 包厢相关
        JsonDecoder.registerCommand(CLI_REQ_BOX_CREATE, PCLIBoxReqCreateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CREATE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CREATE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_LIST, PCLIBoxReqListInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_LIST_OK, PCLIBoxNtfBoxListInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_CLOSE, PCLIBoxReqCloseInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CLOSE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_JOIN, PCLIBoxReqJoinInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_JOIN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_JOIN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_CREATE_CUSTOM_ROOM, PCLIBoxReqCreateCustomRoomInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CREATE_CUSTOM_ROOM_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_MODIFY_RULE, PCLIBoxReqModifyBoxRuleInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_MODIFY_RULE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_MODIFY_RULE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_STATE_INFO, PCLIBoxReqStateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_STATE_INFO_OK, PCLIBoxNtfStateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_JOIN_OR_LEAVE_BOX, PCLIBoxReqJoinOrLeaveBoxInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_SCORE, PCLIBoxReqScoreInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_SCORE_OK, PCLIBoxNtfScoreInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_SCORE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_CHANGE_NAME, PCLIBoxReqChangeNameInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CHANGE_NAME_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CHANGE_NAME_FAIL, ErrorCode.class);
        
        JsonDecoder.registerCommand(CLI_REQ_BOX_ALL_WATCH_PLAYER, PCLIBoxReqCloseInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_ALL_WATCH_PLAYER_OK, null);
        JsonDecoder.registerCommand(CLI_REQ_BOX_ALL_WATCH_PLAYER_FAIL, ErrorCode.class);
        
        JsonDecoder.registerCommand(CLI_NTF_BOX_ADD, PCLIBoxNtfAddBoxInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_DEL, PCLIBoxNtfDelInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_RULE, PCLIBoxNtfRuleInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_JOIN, PCLIBoxNtfJoinInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_LEAVE, PCLIBoxNtfLeaveInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_ROOM_CHANGE_STATE, PCLIBoxNtfChangeStateInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CHANGE_RULE, PCLIBoxNtfChangeRuleInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_CHANGE_NAME, PCLIBoxNtfNameInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_SIT_DOWN, PCLIBoxReqSitDown.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_SIT_DOWN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_SIT_DOWN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_BOX_SIT_UP, PCLIBoxReqSitUp.class);
        JsonDecoder.registerCommand(CLI_NTF_BOX_SIT_UP_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_SITUP_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_MATCH_CHANGE_STATE, PCLIArenaNtfChangeMatchState.class);

        // 扑克
        JsonDecoder.registerCommand(CLI_REQ_POKER_TAKE, PCLIPokerReqTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_TAKE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_TAKE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PASS, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PASS_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PASS_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_DISCARD, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_DISCARD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_DISCARD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_LOOK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LOOK_OK, PCLIPokerNtfCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LOOK_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_TAKE, PCLIPokerNtfTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PASS, PCLIPokerNtfPassInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_CAN_TAKE, PCLIPokerNtfCanTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_CAN_PASS, PCLIPokerNtfCanPassInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_BOMB_SOURCE, PCLIPokerNtfBombScoreInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_DISCARD, PCLIPokerNtfDiscardInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_ADDMUL, PCLIPokerReqGDYCallMulInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ADDMUL_OK, PCLIPokerNtfGDYCallMulInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ADDMUL_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ADDMUL, null);
        JsonDecoder.registerCommand(CLI_REQ_POKER_AUTO_MODE, PCLIPokerReqAutoMode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_AUTO_MODE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_AUTO_MODE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_AUTO_MODE, PCLIPokerNetAutoMode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PLAYER_LOOK_CARD, PCLIPokerNetPlayerLookCard.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PLAYER_BEGIN_PRIMULA, null);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PLAYER_PRIMULA, PCLIPokerReqPlayerPrimula.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PLAYER_PRIMULA_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PLAYER_PRIMULA_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PLAYER_PRIMULA_INFO, PCLIPokerNtfPlayerPrimulaInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PLAYER_PRIMULA_RESULT, PCLIPokerNtfPlayerPrimulaResult.class);

        // 扑克--斗地主
        JsonDecoder.registerCommand(CLI_REQ_POKER_LAND_LORD_CALL_SCORE, PCLIPokerReqLandCallScoreInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_CALL_SCORE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_CALL_SCORE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_LAND_LORD_SHOW_CARD, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_SHOW_CARD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_SHOW_CARD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_CALL_SCORE, PCLIPokerNtfCallScoreInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_SHOW_CARD_INFO, PCLIPokerNtfShowCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_LAST_CARD, PCLIPokerNtfLastCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_RECALL_SCORE, PCLIPokerNtfReCallScoreInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_LAND_LORD_MULTIPLE, PCLIPokerReqLandLordMultiple.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_MULTIPLE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_MULTIPLE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_MULTIPLE_BEGIN, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_MULTIPLE, PCLIPokerNtfLandLordMultiple.class);
        
        JsonDecoder.registerCommand(CLI_REQ_POKER_LAND_LORD_KICK_SELECT, PCLIPokerReqLandLordKickSelect.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_KICK_SELECT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_LAND_LORD_KICK_BACK_SELECT, PCLIPokerReqLandLordKickSelect.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_KICK_RESULT, PCLIPokerNtfLandLordKickResult.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_KICK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_LAND_LORD_KICK_BACK, null);
        
        
        // 扑克--三公
        JsonDecoder.registerCommand(CLI_REQ_POKER_SG_ROB_BANKER, PCLIPokerReqSGRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_ROB_BANKER_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_ROB_BANKER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_SG_REBET, PCLIPokerReqSGRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_REBET_OK, null);
        JsonDecoder.registerCommand(CLI_REQ_POKER_SG_LORD_BANKER_STATE, PCLIPokerReqSGSelectBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_ROB_BANKER_BEGIN, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_ROB_BANKER, PCLIPokerNtfSGRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_ROB_BANKER_INFO, PCLIPokerNtfSGAllRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_REBET_BEGIN, PCLIPokerNtfSGRebetBeginInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_REBET, PCLIPokerNtfSGRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_REBET_INFO, PCLIPokerNtfSGAllRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_DEAL_CARD, PCLIPokerNtfSGHandCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_SG_ROB_BANKER_RESULT, PCLIPokerNtfSGRobBankerResultInfo.class);

        //牛牛
        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_ROB_BANKER, PCLIPokerReqCowRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROB_BANKER_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_REBET, PCLIPokerReqCowReBetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_REBET_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_LORD_BANKER_STATE, PCLIPokerReqCowSelectBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_LORD_BANKER_STATE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_LEVEL_HOT_BANKER, PCLIPokerReqHotCowLeaveBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_LEVEL_HOT_BANKER_OK, PCLIPokerNtfHotCowLeaveBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_LEVEL_HOT_BANKER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_GIVE_BANKER, PCIPokerReqGiveBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_GIVE_BANKER_OK, PCIPokerNtfGiveBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_GIVE_BANKER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROB_BANKER_BEGIN, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROB_BANKER, PCLIPokerNtfCowRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROB_BANKER_INFO, PCLIPokerNtfCowAllRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_REBET_BEGIN, PCLIPokerNtfCowReBetBeginInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_REBET, PCLIPokerNtfCowReBetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_REBET_INFO, PCLIPokerNtfCowAllReBetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_DEAL_CARD, PCLIPokerNtfCowHandCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROB_BANKER_RESULT, PCLIPokerNtfCowRobBankerResultInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_LORD_BANKER_SELECT, PCLIPokerNtfCowSelectBankerInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_SELECT_REBET, PCIPokerReqSelectRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_SELECT_REBET_OK, PCIPokerNtfSelectRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_SELECT_REBET_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_ROBOT, PCLIPokerReqGetCowRobot.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROBOT_OK, PCLIPokerNtfGetCowRobotInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_ROBOT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_LOOP, PCLIPokerNtfCowHotLoopInfo.class);

        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_READY, PCLIPokerNtfPaiGowReadyInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_READY_INFO, PCLIPokerNtfPaiGowReadyInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_BEGIN_READY, null);

        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_GAME_OVER_LOOP, PCLIPokerNtfCowGameOverInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_BEGIN_HOT_AGAIN, null);

        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_NEXT_BANKER_INFO, PCLIPokerNtfCowNextBankInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_READY_OVER_INFO, PCLIPokerNtfCowReadyInfo.class);


        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_READY, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_READY_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_READY_FAIL, ErrorCode.class);


        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_OUT_HOT, PCLIPokerReqCowHotOutInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_OUT_HOT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_OUT_HOT_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_POKER_COW_HOT_AGAIN, PCLIPokerReqPaiGowHotAgainInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_HOT_AGAIN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_COW_HOT_AGAIN_FAIL, ErrorCode.class);


        // 系统相关
        JsonDecoder.registerCommand(CLI_REQ_SYSTEM_GAME_INFO, PCLISystemReqGameInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_SYSTEM_GAME_INFO_OK, PCLISystemNtfGameInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_SYSTEM_GAME_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_SYSTEM_ANNOUNCEMENT, PCLISystemNtfAnnouncement.class);

        JsonDecoder.registerCommand(CLI_REQ_RANK_LIST, PCLIRankReqRankList.class);
        JsonDecoder.registerCommand(CLI_NTF_RANK_LIST_OK, PCLIRankNtfRankList.class);
        JsonDecoder.registerCommand(CLI_NTF_RANK_LIST_FAIL, ErrorCode.class);


        JsonDecoder.registerCommand(CLI_REQ_PLAYER_SEARCH, PCLIPlayerReqSearch.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_SEARCH_OK, PCLIPlayerNtfSearch.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_SEARCH_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_CREATE_CLUB, PCLIClubReqClubCaeateClub.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CREATE_CLUB_OK, PCLIClubInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CREATE_CLUB_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_JOIN_CLUB, PCLIClubReqClubJoinClub.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_JOIN_CLUB_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_JOIN_CLUB_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_JOIN_CLUB, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_JOIN_CLUB, PCLIClubNtfApplyJoinClub.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_APPLY_LIST, PCLIClubReqClubGetApplyList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_APPLY_LIST_OK, PCLIClubNtfClubGetApplyListInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_APPLY_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_OP_APPLY_LIST, PCLIClubReqClubOpApplyList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_OP_APPLY_LIST_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_OP_APPLY_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_OP_APPLY_LIST_RESULT, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_INVITE_JION_CLUB, PCLIClubReqClubInviteJoin.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_INVITE_JION_CLUB_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_INVITE_JION_CLUB_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_LEAVE_CLUB, PCLIClubReqClubLeaveClub.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_LEAVE_CLUB_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_LEAVE_CLUB_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_CREATE_PRIVILEGE, PCLIClubReqCreatePrivilege.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CREATE_PRIVILEGE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CREATE_PRIVILEGE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SEARCH, PCLIClubReqSearchInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SEARCH_OK, PCLIClubBriefInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SEARCH_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_LIST_INFO, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ADD_INFO, PCLIClubInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_DEL_INFO, PCLIClubNtfDelInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_REWARD_VALUE_RCORD, PCLIClubReqGetRewardValueRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_OK, PCLIClubNtfGetRewardValueRecordInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD, PCLIClubReqGetActivityRewardValueRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_OK, PCLIClubNtfGetActivityRewardValueInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_ROOM_CARD_CONVERT_GOLD, PCLIClubReqRoomCardConvertGold.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD, PCLIClubReqRoomCardConvertGoldRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_OK, PCLIClubReqRoomCardConvertGoldInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_MEMBER_COUNT, PCLIClubReqMemberCount.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_MEMBER_COUNT_OK, PCLIClubNtfMemberCount.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_MEMBER_COUNT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_TOTAL_GOLD_REWARD_VALUE_GET, PCLIClubReqTotalGoldRewardValueGet.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_OK, PCLIClubNtfTotalGoldRewardValueGet.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_ROOM_DISMISS, PCLIClubReqRoomDismiss.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ROOM_DISMISS_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ROOM_DISMISS_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_FORBID_LIST, PCLIForbidReqList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_OK, PCLIForbidNtfList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_FORBID_LIST_ADD, PCLIForbidReqAdd.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_ADD_OK, PCLIForbidNtfAdd.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_FORBID_LIST_DEL, PCLIForbidReqDel.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_DEL_OK, PCLIForbidNtfDel.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_DEL_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_FORBID_LIST_ADD_MEMBERLIST, PCLIForbidReqMemberList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_OK, PCLIForbidNtfMemberList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_FORBID_SEARCH, PCLIForbidReqSearchList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_SEARCH_OK, PCLIForbidNtfSearchList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FORBID_SEARCH_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_LIST_INFO, PCLIClubNtfListInfo.class);


        JsonDecoder.registerCommand(CLI_REQ_CLUB_ADDMEMBER, PCLIClubReqAddMember.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ADDMEMBER_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ADDMEMBER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_DELMEMBER, PCLIClubReqDelMember.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_DELMEMBER_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_DELMEMBER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_MEMBERJOB, PCLIClubReqSetMemberJob.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_MEMBERJOB_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_MEMBERJOB_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_PROHIBIT, PCLIClubReqSetProhibit.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_PROHIBIT_OK, PCLIClubNtfSetProhibit.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_PROHIBIT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_CLUBINFO, PCLIClubReqSetClubInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_CLUBINFO_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_CLUBINFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_CLUBINFO, PCLIClubReqGetClubInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CLUBINFO_OK, PCLIClubNtfGetClubInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CLUBINFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_CHANGE_ANNOUNCEMENT, PCLIClubReqChangeAnnouncementInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_OK, PCLIClubNtfChangeAnnouncementInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_ANNOUNCEMENT, PCLIClubReqGetAnnouncementInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_ANNOUNCEMENT_OK, PCLIClubNtfAnnouncementInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_ANNOUNCEMENT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_CLUBMEMBER, PCLIClubReqGetMemberList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CLUBMEMBER_OK, PCLIClubNtfGetMemberList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CLUBMEMBER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_CLUBMEMBER_BY_PARAM, PCLIClubReqGetMemberListByParam.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_OK, PCLIClubNtfGetMemberListByParam.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_CHAT_PLAYERLIST, PCLIClubReqChatPlayerList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CHAT_PLAYERLIST_OK, PCLClubNtfChatPlayerList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_CHAT_PLAYERLIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_GET_CLUB_GOLDRECORD, PCLIPlayerReqGetClubGoldRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_OK, PCLIPlayerNtfGetClubGoldRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_PLAYER_INFO, PCLIClubReqPlayerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_PLAYER_INFO_OK, PCLIClubNtfPlayerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_PLAYER_INFO_FAIL, PCLIClubNtfPlayerFailInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_MANAGER_INFO, PCLIClubReqSetManager.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_MANAGER_INFO_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_MANAGER_INFO, PCLIClubReqGetManager.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_MANAGER_INFO_OK, PCLIClubNtfGetManager.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_MANAGER_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_TREASURER_INFO, PCLIClubReqSetTreasurer.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_TREASURER_INFO_OK, PCLIClubNtfSetTreasurer.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_TREASURER_INFO, PCLIClubNtfSetTreasurer.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ADDMEMBER, PCLIClubNtfAddMember.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_DELMEMBER, PCLIClubNtfDelMember.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CHANGEMEMBERJOB, PCLIClubNtfSetMemberJob.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_PROHIBITMEMBER, PCLIClubNtfProhibitInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CHANGECLUBINFO, PCLIClubNtfSetClubInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT, PCLIClubNtfAnnouncementInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_MANAGER_INFO, PCLIClubNtfSetManager.class);

        JsonDecoder.registerCommand(CLI_REQ_PLAYER_UP_GOLD, PCLIPlayerReqUpGold.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_UP_GOLD_OK, PCLIPlayerNtfUpGold.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_UP_GOLD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_DOWN_GOLD, PCLIPlayerReqDownGold.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_DOWN_GOLD_OK, PCLIPlayerNtfDownGold.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_GET_DOWN_GOLD_ORDER, PCLIPlayerReqGetDownGoldOrder.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_DOWN_GOLD_ORDER_OK, PCLIPlayerNtfGetDownGoldOrder.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_DOWN_GOLD_ORDER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_SET_DOWN_GOLD_ORDER, PCLIPlayerReqSetDownGoldOrder.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER, PCLIPlayerNtfSetDownGoldOrder.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_NEW_DOWN_GOLD_ORDER, PCLIPlayerNtfNewDownGoldOrder.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_GET_WAIT_DOWN_GOLD_ORDER, PCLIPlayerReqGetWaitDownGoldOrder.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_WAIT_DOWN_GOLD_ORDER_OK, PCLIPlayerNtfGetWaitDownGoldOrder.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_WAIT_DOWN_GOLD_ORDER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_PLAYER_GET_DOWN_GOLD_TIME, PCLIPlayerReqGetDownGoldTime.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_DOWN_GOLD_TIME_OK, PCLIPlayerNtfGetDownGoldTime.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_GET_DOWN_GOLD_TIME_FAIL, ErrorCode.class);

        
        JsonDecoder.registerCommand(CLI_REQ_CLUB_ACTIVITY_DIVIDE_INFO, PCLIClubReqActivityDivideInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_OK, PCLIClubNtfActivityDivideInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_ACTIVITY_DIVIDE_CHANGE, PCLIClubReqActivityDivideChange.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_OK,null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_ACTIVITY_GOLD_INFO, PCLIClubReqActivityGoldInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_OK, PCLIClubNtfActivityDivideInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_ACTIVITY_GOLD_MODIFY, PCLIClubReqActivityGoldModify.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_MODIFY_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_MODIFY_FAIL, ErrorCode.class);


        JsonDecoder.registerCommand(CLI_REQ_CLUB_ACTIVITY_GOLD_REMOVE, PCLIClubReqActivityGoldRemove.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_OK,null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_ACTIVITY_GOLD_REWARD, PCLIPlayerGroupReqQuestGetReward.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL, ErrorCode.class);


        JsonDecoder.registerCommand(CLI_REQ_CLUB_APPLY_MERGE, PCLIClubReqApplyMerge.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_MERGE_OK, PCLIClubNtfApplyMerge.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_APPLY_MERGE_OPT, PCLIClubReqApplyMergeOpt.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_MERGE_OPT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_UP_MAIN_CLUB_INFO, PCLIClubNtfUpdateMainClubInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_MERGE_NEW_CLUB_INFO, PCLIClubNtfMergeNewClubInfo.class);

        JsonDecoder.registerCommand(CLI_NTF_CLUB_VALUE_CAHNGE, PCLIClubNtfValueChange.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_DIVIDE_LINE, PCLIClubReqSetDivideLine.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_DIVIDE_LINE_OK, PCLIClubNtfSetDivideLineInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SERVICE_CHARGE_DIVIDE, PCLIClubReqSetServiceChargeDivide.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_OK, PCLIClubReqSetServiceChargeDivide.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_GOLD, PCLIClubReqSetGoldInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_GOLD_OK, PCLIClubNtfValueChange.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_GOLD_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_APPLY_LEAVE, PCLIClubReqApplyLeave.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_LEAVE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_APPLY_LEAVE_OPT, PCLIClubReqApplyLeaveOpt.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_LEAVE_OPT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_LEAVE_NOTIFY, PCLIClubReqApplyLeave.class);


        // 楼层
        JsonDecoder.registerCommand(CLI_REQ_CLUB_FLOOR_CREATE, PCLIFloorReqCreate.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FLOOR_CREATE_OK,PCLIFloorNtfInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FLOOR_CREATE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_FLOOR_CLOSE, PCLIFloorReqClose.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FLOOR_CLOSE_OK,PCLIFloorNtfDel.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_FLOOR_LIST, PCLIFloorReqList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FLOOR_LIST_OK, PCLIFloorNtfList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_FLOOR_LIST_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_ALL_LIST_INFO, PCLIClubReqAllListInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ALL_LIST_INFO_OK, PCLIClubNtfAllListInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ALL_LIST_INFO_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_PLAYER_ENTER_CLUB, PCLIPlayerReqEnterClub.class);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_ENTER_CLUB_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_WARSITUATION, PCLIClubReqWarSituation.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_WARSITUATION_OK, PCLIClubNtfWarSituationInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_WARSITUATION_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_MARK_WARSITUATION, PCLIClubReqMarkWarSituationInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_MARK_WARSITUATION_OK, PCLIClubNtfMarkWarSituationInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_MARK_WARSITUATION_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_GIVE_GOLD, PCLIClubReqSetGoldInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_GIVE_GOLD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_EXCHANGE_REWARD_VALUE, PCLIClubReqExchangeRewardValue.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_OK, PCLIClubNtfValueChange.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_FALL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ACTIVITY_DIVIDE_OPEN, PCLIClubNtfActivityDivideOpenNotice.class);


        JsonDecoder.registerCommand(CLI_REQ_CLUB_CONSUME_CARD_LIST, PCLIClubReqConsumeCardList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CONSUME_CARD_LIST_OK, PCLIClubConsumeCardList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CONSUME_CARD_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_CLUB_CONSUME_CARD_DAY_LIST, PCLIClubReqConsumeCardDayList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_OK, PCLIClubConsumeCardDayList.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_APPLY_CLOSE, PCLIClubReqApplyClose.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_CLOSE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_HELPER_INVITE_PLAYERS, PCLIClubReqHelperInvitePlayers.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_OK, PCLIClubNtfHelperInvitePlayers.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_HELPER_INVITE, PCLIClubReqHelperInvite.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_NOTICE, PCLIClubNtfHelperInviteNotice.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_HELPER_INVITE_SET_INFO, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_SET_INFO_OK, PCLIClubNtfInviteSetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_SET_INFO_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_HELPER_INVITE_SET, PCLIClubReqHelperInviteSet.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_SET_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_SET_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_HELPER_INVITE_ANSWER, PCLIClubReqHelperInviteAnswer.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_ANSWER_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_HELPER_INFO, null);
        JsonDecoder.registerCommand(CLI_REQ_HALL_HELPER_INFO, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INFO_OK, PCLIClubNtfHelperInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_HELPER_INFO_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_ONLYUPLINESETGOLD, PCLIClubReqSetGoldUpLine.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ONLYUPLINESETGOLD_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_ONLYUPLINESETGOLD_NOTICE, PCLIClubNtfSetOnlyUpLineSetGoldNotice.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_LIKE_GAME, PCLIClubReqSetLikeGame.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_LIKE_GAME_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_LIKE_GAME_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_RECOMMEND_CODE, PCLIClubReqGetRecommendCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_RECOMMEND_CODE_OK, PCLIClubNtfGetRecommendCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_RECOMMEND_CODE_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_JOIN_BY_RECOMMEND_CODE, PCLIClubReqJoinByRecommendCode.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_GET_MAINCHARGE, PCLIClubReqGetClubLevelCharge.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_MAINCHARGE_OK, PCLIClubNtfGetClubLevelCharge.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_GET_MAINCHARGE_FAIL, ErrorCode.class);

        JsonDecoder.registerCommand(CLI_REQ_CLUB_SET_MAINCHARGE, PCLIClubReqSetClubLevelCharge.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_MAINCHARGE_OK, PCLIClubNtfSetClubLevelCharge.class);
        JsonDecoder.registerCommand(CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.class);

        // 扑克--扎金花
        JsonDecoder.registerCommand(CLI_REQ_POKER_FGF_COMPARE, PCLIPokerReqFGFCompareInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_COMPARE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_COMPARE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_FGF_ADD_NOTE, PCLIPokerReqFGFAddNoteInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_ADD_NOTE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_ADD_NOTE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_FGF_FOLLOW_NOTE, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_FOLLOW_NOTE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_FOLLOW_NOTE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_COMPARE_RESULT, PCLIPokerNtfFGFCompareResultInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_NOTE, PCLIPokerNtfFGFNoteInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_LOOP, PCLIPokerNtfFGFLoopInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_OPERATE, PCLIPokerNtfFGFOperatorInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_LOOK, PCLIPokerNtfFGFLookInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_FGF_AUTO_COMPARE_RESULT, PCLIPokerNtfFGFAutoCompareResultInfo.class);

        JsonDecoder.registerCommand(CLI_REQ_ARENA_SCORE_RECORD, PCLIArenaReqRecordInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_SCORE_RECORD_OK, PCLIArenaNtfFgfRecordtInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_SCORE_RECORD_OK, PCLIArenaNtfCowRecordtInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_SCORE_RECORD_FAIL, ErrorCode.class);

        //十三水
        JsonDecoder.registerCommand(CLI_REQ_POKER_THIRTEEN_TAKE, PCLIPokerReqThirteenTakeInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_THIRTEEN_TAKE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_THIRTEEN_TAKE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_THIRTEEN_SORT_CARD, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_THIRTEEN_SORT_CARD_OK, PCLIPokerNtfThirteenSortCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_THIRTEEN_SORT_CARD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_THIRTEEN_TAKE, PCLIPokerNtfThirteenTake.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_THIRTEEN_DEAL_CARD, PCLIPokerNtfThirteenHandCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_THIRTEEN_LIPAI_BEGIN, PCLIPokerNtfThirteenTakeInfo.class);

        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_PREDEALCARD_INFO, PCLIPokerNtfPaiGowPreDealCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_ROB_BANKER_BEGIN,null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_ROB_BANKER,PCLIPokerNtfCowRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_ROB_BANKER_INFO, PCLIPokerNtfCowAllRobBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_REBET_BEGIN, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_REBET,PCLIPokerNtfPaiGowRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_REBET_INFO, PCLIPokerNtfPaiGowAllRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OPNE_CARD_BEGIN, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OPEN_CARD_INFO, PCLIPokerNtfPaiGowAllOpenInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OPNE_CARD, PCLIPokerNtfPaiGowOpenInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_ROB_BANKER_RESULT, PCLIPokerNtfCowRobBankerResultInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OUTHOT_INFO, PCLIPokerNtfHotPaiGowLeaveBankerInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_KEEP_INFO, PCLIPokerNtfHotPaiGowKeepHotInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_CARD_INFO, PCLIPokerNtfPaiGowHotCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_LOOP, PCLIPokerNtfPaiGowHotLoopInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_BEGIN_READY, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_READY, PCLIPokerNtfPaiGowReadyInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_READY_INFO, PCLIPokerNtfPaiGowReadyInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_GAME_OVER_LOOP, PCLIPokerNtfGameOverInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_INFO, PCLIPokerNtfPaiGowHotAgainInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_AGAIN, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_HOT_OUT_INFO, PCLIPokerNtfPaiGowHotOutInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_OUT, null);

        JsonDecoder.registerCommand(CLI_REQ_POKER_PAI_GOW_READY, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_READY_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_READY_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PAI_GOW_REBET, PCLIPokerReqPaiGowRebetInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_REBET_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_REBET_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PAI_GOW_OPEN, PCLIPokerReqPaiGowOpenInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OPEN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OPEN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PAI_GOW_ROB_BANKER, PCLIPokerNtfPaiGowRebetMulInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PAI_GOW_OUT_HOT, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OUT_HOT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_OUT_HOT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PAI_GOW_HOT_AGAIN, PCLIPokerReqPaiGowHotAgainInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_PAI_GOW_HOT_OUT, PCLIPokerReqPaiGowHotOutInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_HOT_OUT_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_PAI_GOW_HOT_OUT_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_JOIN_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_JOIN_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_LEAVE, PCLIHundredReqLeave.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_LEAVE_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_LEAVE_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_UP_BANKER, PCLIHundredReqUpBanker.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_UP_BANKER_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_UP_BANKER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_DOWN_BANKER, PCLIHundredReqDownBanker.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_DOWN_BANKER_OK, PCLIHundredNtfDownBanker.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_DOWN_BANKER_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_BANKER_LIST, PCLIHundredReqBankerList.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_BANKER_LIST_OK, PCLIHundredNtfBankerList.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_BANKER_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_SELFBANKER_LIST, PCLIHundredReqSelfBankerList.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_SELFBANKER_LIST_OK, PCLIHundredNtfSelfBankerList.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_SELFBANKER_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_PLAYER_LIST, PCLIHundredReqPlayerList.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_PLAYER_LIST_OK, PCLIHundredNtfPlayerList.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_PLAYER_LIST_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_RECORD, PCLIHundredReqRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_RECORD_OK, PCLIHundredNtfRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_RECORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_REB, PCLIHundredReqReb.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_REB_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_REB_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_VIP_SEAT_OP, PCLIHundredReqVipSeatOp.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_VIP_SEAT_OP_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_VIP_SEAT_OP_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_READY, PCLIHundredNtfReady.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_REB, PCLIHundredNtfReb.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_OPEN_CARD, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_OVER, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_REB_INFO, null);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_VIP_SEAT_INFO, PCLIHundredVipSeatInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_ARENA_HUNDRED_BANKER_RECORD, PCLIHundredReqBankerRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_BANKER_RECORD_OK, PCLIHundredNtfBankerRecord.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_BANKER_RECORD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_ARENA_HUNDRED_REB_ALL_INFO, PCLIHundredNtfTouzhurenRebInfoByLhd.class);
        
        //打拱
        JsonDecoder.registerCommand(CLI_REQ_POKER_ARCH_BID, PCLIPokerReqArchBidInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_BID_OK, null);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_BID_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_ARCH_OB, PCLIPokerReqArchObInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_OB_OK, PCLIPokerNtfArchObInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_OB_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_REQ_POKER_ARCH_SORT_CARD, PCLIPokerReqArchSortCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_SORT_CARD_OK, PCLIPokerNtfArchSortCardInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_SORT_CARD_FAIL, ErrorCode.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_BID, PCLIPokerNtfArchBidInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_BID_RESULT, PCLIPokerNtfArchBidResultInfo.class);
        JsonDecoder.registerCommand(CLI_NTF_POKER_ARCH_SCORE, PCLIPokerNtfArchScoreInfo.class);

        //机器人
        JsonDecoder.registerCommand(CLI_REQ_ROBOT_ROOM_JOIN, PCLIRobotReqJoinInfo.class);
        JsonDecoder.registerCommand(CLI_REQ_ROBOT_GOOD_CARD, null);
    }
}
