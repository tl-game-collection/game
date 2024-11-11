package com.xiuxiu.app.server.services.gateway;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Main;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongXuanPiaoHandler;
import com.xiuxiu.app.server.services.gateway.handler.SystemGameInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxAllWatchPlayerHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxChangeNameHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxCloseHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxCreateCustomRoomHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxCreateHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxJoinHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxJoinOrLeaveBoxHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxListHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxModifyRuleHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxScoreHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxSitDownHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxSitUpHandler;
import com.xiuxiu.app.server.services.gateway.handler.box.BoxStateHandler;
import com.xiuxiu.app.server.services.gateway.handler.chat.ChatDelMessageHandler;
import com.xiuxiu.app.server.services.gateway.handler.chat.ChatDelRecallMessageHandler;
import com.xiuxiu.app.server.services.gateway.handler.chat.ChatGetMessageAckHandler;
import com.xiuxiu.app.server.services.gateway.handler.chat.ChatGetMessageHandler;
import com.xiuxiu.app.server.services.gateway.handler.chat.ChatRecallMessageHandler;
import com.xiuxiu.app.server.services.gateway.handler.chat.ChatSayHandler;
import com.xiuxiu.app.server.services.gateway.handler.chat.ChatUpdateMessageAckHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubActivityDivideChangeHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubActivityDivideInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubActivityGoldInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubActivityGoldModifyHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubActivityGoldRemoveHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubActivityGoldRewardHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubAddMemberHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubApplyCloseHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubApplyJoinHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubApplyLeaveHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubApplyLeaveOptHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubApplyMergeHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubApplyMergeOptHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubConsumeCardDayListHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubConsumeCardListHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubCreateHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubCreatePrivilegeHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubDelMemberHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubExchangeRewardValueHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetActivityRewardValueRcordHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetAllListInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetAnnouncementInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetApplyListHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetChatPlayerListHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetClubInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetClubLevelChargeHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetGoldRecordHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetMangerHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetMemberCountHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetMemberListByParamHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetMemberListHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetPlayerInfoHandle;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetRecommendCodeHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetRewardValueRcordHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGetWarSituationHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubGiveGoldHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubInviteJoinHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubJoinByRecommendCodeHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubLeaveHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubListInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubMemberDownGoldHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubMemberUpGoldHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubOpApplyListHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubRoomCardConvertGoldHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubRoomCardConvertGoldRecordHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubRoomDismissHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSearchHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetAnnouncementInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetClubInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetClubLevelChargeHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetDivideLineHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetGoldHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetLikeGameHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetManagerHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetMemberJobHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetOnlyUpLineSetGoldHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetProhibitHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetServiceChargeDivideHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetTreasurerHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetWarSituationMarkHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubTotalGoldRewardValueGetHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.PlayerEnterClubdHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.helper.ClubHelperInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.helper.ClubHelperInviteAnswerHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.helper.ClubHelperInviteHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.helper.ClubHelperInvitePlayersHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.helper.ClubHelperInviteSetHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.helper.ClubHelperInviteSetInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.club.helper.HallHelperInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.floor.FloorCloseHandler;
import com.xiuxiu.app.server.services.gateway.handler.floor.FloorCreateHandler;
import com.xiuxiu.app.server.services.gateway.handler.floor.FloorListHandler;
import com.xiuxiu.app.server.services.gateway.handler.forbid.ForbidAddHandler;
import com.xiuxiu.app.server.services.gateway.handler.forbid.ForbidDelHandler;
import com.xiuxiu.app.server.services.gateway.handler.forbid.ForbidGetListHandler;
import com.xiuxiu.app.server.services.gateway.handler.forbid.ForbidGetMemberListHandler;
import com.xiuxiu.app.server.services.gateway.handler.forbid.ForbidSearchListHandler;
import com.xiuxiu.app.server.services.gateway.handler.login.GetAccountInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.login.LoginHandler;
import com.xiuxiu.app.server.services.gateway.handler.login.ReLoginHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongBarHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongBrightHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongBumpHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongDingQueHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongEatHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongFumbleHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongHuHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongHuanPaiHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongPassHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongSelectHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongSelectPiaoHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongShuKanHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongShuaiPaiHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongTakeHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongTingHandler;
import com.xiuxiu.app.server.services.gateway.handler.mahjong.MahjongYangPaiHandler;
import com.xiuxiu.app.server.services.gateway.handler.mail.MailDelHandler;
import com.xiuxiu.app.server.services.gateway.handler.mail.MailQuickDelHandler;
import com.xiuxiu.app.server.services.gateway.handler.mail.MailQuickReceiveItemHandler;
import com.xiuxiu.app.server.services.gateway.handler.mail.MailReadHandler;
import com.xiuxiu.app.server.services.gateway.handler.mail.MailReceiveItemHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerAddShowImageHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerAddVisitCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeBankCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeBornHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeEmotionHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeIconHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeLocationHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeNameHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangePayPassWordHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeSexHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeSignatureHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerChangeWechatHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerDelShowImageHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerExchangeShowImageHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerGetDownGoldOrderHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerGetDownGoldTimeHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerGetNoNeedPayPasswordHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerGetVisitCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerGetWaitDownGoldOrderHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerGetWeChatAssistantHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerIsSetPayPassWordHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerModifyNoNeedPayPasswordHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerReplaceShowImageHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerSearchHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerSetDownGoldOrderHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerSyncGpsHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerUploadIconSuccHandler;
import com.xiuxiu.app.server.services.gateway.handler.player.PlayerVerifyPayPasswordHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerAutoModeHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerDiscardHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerFGFAddNoteHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerFGFCompareHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerFGFFollowNoteHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerLookCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerPassHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerPlayerPrimulaHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.PokerTakeHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.cow.*;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredBankerListHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredBankerRecordHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredDownBankerHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredLeaveHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredPlayerListHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredRebHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredRecordHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredSelfBankerListHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredUpBankerHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.hundred.HundredVipSeatOpHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.landlord.PokerLandLordCallScoreHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.landlord.PokerLandLordKickBackSelectHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.landlord.PokerLandLordKickSelectHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.landlord.PokerLandLordMultipleHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.landlord.PokerLandLordShowCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.paigow.PaiGowHotAgainHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.paigow.PaiGowHotOutHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.paigow.PaiGowHotReadyHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.paigow.PaiGowOpenCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.paigow.PaiGowOutHotHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.paigow.PaiGowRebetHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.paigow.PokerPaiGowRobBankerHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.sg.PokerSGDealCardOkHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.sg.PokerSGRebetHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.sg.PokerSGRobBankerMulHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.sg.PokerSGSelectLordBankerHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.thirteen.PokerThirteenSortCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.poker.thirteen.PokerThirteenTakeHandler;
import com.xiuxiu.app.server.services.gateway.handler.rank.RankListGetHandler;
import com.xiuxiu.app.server.services.gateway.handler.recommend.PlayerRecommendHandler;
import com.xiuxiu.app.server.services.gateway.handler.recommend.PlayerRecommendInfoHandler;
import com.xiuxiu.app.server.services.gateway.handler.recommend.PlayerRecommendListHandler;
import com.xiuxiu.app.server.services.gateway.handler.robot.RobotCardHandler;
import com.xiuxiu.app.server.services.gateway.handler.robot.RobotJoinHandler;
import com.xiuxiu.app.server.services.gateway.handler.room.*;
import com.xiuxiu.core.net.SessionContext;
import com.xiuxiu.core.net.SessionContextFactory;
import com.xiuxiu.core.net.websocket.NettyWebSocketServer;
import com.xiuxiu.core.service.FutureListener;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class GatewayServer extends NettyWebSocketServer {
    public GatewayServer(String host, int port) {
        super("game", host, port, new GatewayServerConnectionManager(), new GatewayServerMessageDispatch());

        this.messageReceive.register(CommandId.CLI_REQ_LOGIN, new LoginHandler(), Main.I.getLoginMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_RELOGIN, new ReLoginHandler(), Main.I.getLoginMessageProcess());
        // chat
        this.messageReceive.register(CommandId.CLI_REQ_CHAT_SAY, new ChatSayHandler(), Main.I.getChatMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CHAT_GET_MSG, new ChatGetMessageHandler(), Main.I.getChatMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CHAT_GET_MSG_ACK, new ChatGetMessageAckHandler(), Main.I.getChatMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CHAT_RECALL, new ChatRecallMessageHandler(), Main.I.getChatMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CHAT_UPDATE_MSG_ACK, new ChatUpdateMessageAckHandler(), Main.I.getChatMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CHAT_DEL, new ChatDelMessageHandler(), Main.I.getChatMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CHAT_DEL_RECALL, new ChatDelRecallMessageHandler(), Main.I.getChatMessageProcess());

        // room
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_CREATE, new RoomCreateHandler(), Main.I.getPlayerMessageProcess());
        //大厅输入房间号加入房间接口
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_JOIN, new RoomJoinHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_CHANGE_SEATS, new RoomChangeSeat(), Main.I.getRoomMessageProcess());
        //离开房间
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_LEAVE_V2, new RoomLeaveV2Handler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_READY, new RoomReadyHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_KILL, new RoomKillHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_DISSOLVE, new RoomDissolveHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_DISSOLVE_OP, new RoomDissolveOpHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_SCORE_RECORD, new RoomScoreRecordHandler(), Main.I.getAsyncMessageProcess());
        // 获取竞技场战报
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_REPORT, new ArenaScoreRecordHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_MAGIC_FACE, new RoomMagicFaceHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_LOCATION_RELATION, new RoomLocationRelationHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_DESK_INFO, new RoomDeskInfoHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_SHORTTALK, new RoomShortTalkHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_SITDOWN, new RoomSitDownHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_SITUP, new RoomSitUpHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_LOOK_DESK_INFO, new RoomLookDeskInfoHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_NEXT_CARD, new RoomNextCardHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROOM_SHOW_OFF, new RoomShowOffHandler(), Main.I.getRoomMessageProcess());

        // mahjong
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_TAKE, new MahjongTakeHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_FUMBLE, new MahjongFumbleHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_BUMP, new MahjongBumpHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_BAR, new MahjongBarHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_EAT, new MahjongEatHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_HU, new MahjongHuHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_PASS, new MahjongPassHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_SELECT, new MahjongSelectHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_SELECT_PIAO, new MahjongSelectPiaoHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_BRIGHT, new MahjongBrightHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_SHU_KAN, new MahjongShuKanHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_HUAN_PAI, new MahjongHuanPaiHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_SHUAI_PAI, new MahjongShuaiPaiHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_DING_QUE, new MahjongDingQueHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_YANG_PAI, new MahjongYangPaiHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_TING, new MahjongTingHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAHJONG_XUAN_PIAO, new MahjongXuanPiaoHandler(), Main.I.getRoomMessageProcess());
        // player
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_ICON, new PlayerChangeIconHandler(), Main.I.getPlayerMessageProcess());
        /**
         * 通知服务器上传icon成功
         */
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_UPLOAD_ICON_SUCC, new PlayerUploadIconSuccHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_NAME, new PlayerChangeNameHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_LOCATION, new PlayerChangeLocationHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_SEX, new PlayerChangeSexHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_RECOMMEND, new PlayerRecommendHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_RECOMMEND_LIST, new PlayerRecommendListHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_VISIT_CARD, new PlayerAddVisitCardHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_VISIT_CARD_GET, new PlayerGetVisitCardHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_RECOMMEND_INFO, new PlayerRecommendInfoHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_BORN, new PlayerChangeBornHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_SIGNATURE, new PlayerChangeSignatureHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_EMOTION, new PlayerChangeEmotionHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_ADD_SHOW_IMAGE, new PlayerAddShowImageHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_DEL_SHOW_IMAGE, new PlayerDelShowImageHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_REPLACE_SHOW_IMAGE, new PlayerReplaceShowImageHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_EXCHANGE_SHOW_IMAGE, new PlayerExchangeShowImageHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_SYNC_GPS, new PlayerSyncGpsHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_PAY_PASSWORD, new PlayerChangePayPassWordHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_IS_SET_PAY_PASSWORD, new PlayerIsSetPayPassWordHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_GET_INFO, new GetAccountInfoHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_VERIFY_PAY_PASSWORD, new PlayerVerifyPayPasswordHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_WECHAT, new PlayerChangeWechatHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE, new PlayerGetNoNeedPayPasswordHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE, new PlayerModifyNoNeedPayPasswordHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_CHANGE_BANKCARD, new PlayerChangeBankCardHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_GET_WECHAT_ASSISTANT, new PlayerGetWeChatAssistantHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_SEARCH, new PlayerSearchHandler(), Main.I.getPlayerMessageProcess());

        // mail
        this.messageReceive.register(CommandId.CLI_REQ_MAIL_READ, new MailReadHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAIL_GIVE_ITEM, new MailReceiveItemHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAIL_QUICK_GIVE_ITEM, new MailQuickReceiveItemHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAIL_DEL, new MailDelHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_MAIL_QUICK_DEL, new MailQuickDelHandler(), Main.I.getAsyncMessageProcess());

        // system
        this.messageReceive.register(CommandId.CLI_REQ_SYSTEM_GAME_INFO, new SystemGameInfoHandler(), Main.I.getAsyncMessageProcess());

        // tdk
        // 创建包厢
        this.messageReceive.register(CommandId.CLI_REQ_BOX_CREATE, new BoxCreateHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_BOX_LIST, new BoxListHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_BOX_CLOSE, new BoxCloseHandler(), Main.I.getBoxMessageProcess());
        //请求加入包厢 创建包厢房
        this.messageReceive.register(CommandId.CLI_REQ_BOX_JOIN, new BoxJoinHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_BOX_CREATE_CUSTOM_ROOM, new BoxCreateCustomRoomHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_BOX_MODIFY_RULE, new BoxModifyRuleHandler(), Main.I.getBoxMessageProcess());
        /**
         * 获取包厢状态信息
         */
        this.messageReceive.register(CommandId.CLI_REQ_BOX_STATE_INFO, new BoxStateHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_BOX_JOIN_OR_LEAVE_BOX, new BoxJoinOrLeaveBoxHandler(), Main.I.getBoxMessageProcess());
        /**
         * 请求包厢观战的玩家id列表
         */
        this.messageReceive.register(CommandId.CLI_REQ_BOX_ALL_WATCH_PLAYER, new BoxAllWatchPlayerHandler(), Main.I.getBoxMessageProcess());
        // 请求包厢战绩
        this.messageReceive.register(CommandId.CLI_REQ_BOX_SCORE, new BoxScoreHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_BOX_CHANGE_NAME, new BoxChangeNameHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_BOX_SIT_DOWN, new BoxSitDownHandler(), Main.I.getRoomMessageProcess());
        /**
         * 请求包厢可少人模式-站起
         */
        this.messageReceive.register(CommandId.CLI_REQ_BOX_SIT_UP, new BoxSitUpHandler(), Main.I.getRoomMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FLOOR_CREATE, new FloorCreateHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FLOOR_LIST, new FloorListHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FLOOR_CLOSE, new FloorCloseHandler(), Main.I.getBoxMessageProcess());

        // poker
        this.messageReceive.register(CommandId.CLI_REQ_POKER_TAKE, new PokerTakeHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PASS, new PokerPassHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_DISCARD, new PokerDiscardHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_AUTO_MODE, new PokerAutoModeHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PLAYER_PRIMULA, new PokerPlayerPrimulaHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_LOOK, new PokerLookCardHandler(), Main.I.getRoomMessageProcess());

        // cow 牛牛
        //抢庄
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_ROB_BANKER, new PokerCowRobBankerMulHandler(), Main.I.getRoomMessageProcess());
        //下注
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_REBET, new PokerCowRebetHandler(), Main.I.getRoomMessageProcess());
        //霸王庄 通知玩家选择开始庄（只在第一局的时候推送）
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_LORD_BANKER_STATE, new PokerCowSelectLordBankerHandler(), Main.I.getRoomMessageProcess());
        //抽庄
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_LEVEL_HOT_BANKER, new PokerHotCowLeaveBankerHandler(), Main.I.getRoomMessageProcess());
        //定庄
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_GIVE_BANKER, new PokerCowGiveBankerHandler(), Main.I.getRoomMessageProcess());
        //客服端通知前端发牌已经完成
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_DEALCARD_OVER, new PokerCowDealCardOkHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_SELECT_REBET, new PokerCowSelectRebetHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_ROBOT, new PokerCowGetRobotHandler(), Main.I.getRoomMessageProcess());

        //准备
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_READY, new CowHotReadyHandler(), Main.I.getRoomMessageProcess());
        //续锅
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_HOT_AGAIN, new CowHotAgainHandler(), Main.I.getRoomMessageProcess());
        //揭锅
        this.messageReceive.register(CommandId.CLI_REQ_POKER_COW_OUT_HOT, new CowHotOutHandler(), Main.I.getRoomMessageProcess());

        // 三公
        this.messageReceive.register(CommandId.CLI_REQ_POKER_SG_ROB_BANKER, new PokerSGRobBankerMulHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_SG_REBET, new PokerSGRebetHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_SG_LORD_BANKER_STATE, new PokerSGSelectLordBankerHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_SG_DEALCARD_OVER, new PokerSGDealCardOkHandler(), Main.I.getRoomMessageProcess());

        //排行榜
        this.messageReceive.register(CommandId.CLI_REQ_RANK_LIST, new RankListGetHandler(), Main.I.getAsyncMessageProcess());

        // 亲友圈
        /**
         * 获取亲友圈战况
         */
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_WARSITUATION, new ClubGetWarSituationHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_MARK_WARSITUATION, new ClubSetWarSituationMarkHandler(), Main.I.getPlayerMessageProcess());

        // 新版大唐
        /**
         * 创建俱乐部
         */
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_CREATE_CLUB, new ClubCreateHandler(), Main.I.getClubMessageProcess());
        //加入俱乐部
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_JOIN_CLUB, new ClubApplyJoinHandler(), Main.I.getClubMessageProcess());
        //俱乐部合并申请
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_APPLY_LIST, new ClubGetApplyListHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_OP_APPLY_LIST, new ClubOpApplyListHandler(), Main.I.getClubMessageProcess());
        //解散俱乐部
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_LEAVE_CLUB, new ClubLeaveHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_INVITE_JION_CLUB, new ClubInviteJoinHandler(), Main.I.getClubMessageProcess());
        //创建群
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_CREATE_PRIVILEGE, new ClubCreatePrivilegeHandler(), Main.I.getClubMessageProcess());
        //搜索俱乐部
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SEARCH, new ClubSearchHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_LIST_INFO, new ClubListInfoHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_REWARD_VALUE_RCORD, new ClubGetRewardValueRcordHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_MEMBER_COUNT, new ClubGetMemberCountHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ROOM_CARD_CONVERT_GOLD, new ClubRoomCardConvertGoldHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD, new ClubRoomCardConvertGoldRecordHandler(), Main.I.getClubMessageProcess());
        /**
         * 请求获取任务获取奖励分
         */
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD, new ClubGetActivityRewardValueRcordHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_TOTAL_GOLD_REWARD_VALUE_GET, new ClubTotalGoldRewardValueGetHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ROOM_DISMISS, new ClubRoomDismissHandler(), Main.I.getClubMessageProcess());


        //-- 请求club添加成员
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ADDMEMBER, new ClubAddMemberHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_DELMEMBER, new ClubDelMemberHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_MEMBERJOB, new ClubSetMemberJobHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_PROHIBIT, new ClubSetProhibitHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_CLUBINFO, new ClubSetClubInfoHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_CLUBINFO, new ClubGetClubInfoHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_CHANGE_ANNOUNCEMENT, new ClubSetAnnouncementInfoHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_ANNOUNCEMENT, new ClubGetAnnouncementInfoHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_CLUBMEMBER, new ClubGetMemberListHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_CLUBMEMBER_BY_PARAM, new ClubGetMemberListByParamHandler(), Main.I.getPlayerMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_CHAT_PLAYERLIST, new ClubGetChatPlayerListHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_GET_CLUB_GOLDRECORD, new ClubGetGoldRecordHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_PLAYER_INFO, new ClubGetPlayerInfoHandle(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_MANAGER_INFO, new ClubSetManagerHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_MANAGER_INFO, new ClubGetMangerHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_TREASURER_INFO, new ClubSetTreasurerHandler(), Main.I.getClubMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_APPLY_MERGE, new ClubApplyMergeHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_APPLY_MERGE_OPT, new ClubApplyMergeOptHandler(), Main.I.getClubMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_APPLY_LEAVE, new ClubApplyLeaveHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_APPLY_LEAVE_OPT, new ClubApplyLeaveOptHandler(), Main.I.getClubMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_GOLD, new ClubSetGoldHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SERVICE_CHARGE_DIVIDE, new ClubSetServiceChargeDivideHandler(), Main.I.getClubMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_DIVIDE_LINE, new ClubSetDivideLineHandler(), Main.I.getClubMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FORBID_LIST, new ForbidGetListHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FORBID_LIST_ADD, new ForbidAddHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FORBID_LIST_DEL, new ForbidDelHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FORBID_LIST_ADD_MEMBERLIST, new ForbidGetMemberListHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_FORBID_SEARCH, new ForbidSearchListHandler(), Main.I.getAsyncMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ACTIVITY_DIVIDE_CHANGE, new ClubActivityDivideChangeHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ACTIVITY_DIVIDE_INFO, new ClubActivityDivideInfoHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ACTIVITY_GOLD_INFO, new ClubActivityGoldInfoHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ACTIVITY_GOLD_MODIFY, new ClubActivityGoldModifyHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ACTIVITY_GOLD_REMOVE, new ClubActivityGoldRemoveHandler(), Main.I.getAsyncMessageProcess());
        /**
         * 获取亲友圈任务奖励
         */
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ACTIVITY_GOLD_REWARD, new ClubActivityGoldRewardHandler(), Main.I.getAsyncMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ALL_LIST_INFO, new ClubGetAllListInfoHandler(), Main.I.getClubMessageProcess());

        /**
         * 请求财务上分
         */
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_UP_GOLD, new ClubMemberUpGoldHandler(), Main.I.getClubMessageProcess());
        /**
         * 请求财务下分
         */
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_DOWN_GOLD, new ClubMemberDownGoldHandler(), Main.I.getClubMessageProcess());
        /**
         * 获取下分订单
         */
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_GET_DOWN_GOLD_ORDER, new PlayerGetDownGoldOrderHandler(), Main.I.getPlayerMessageProcess());
        /**
         * 财务操作下分订单
         */
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_SET_DOWN_GOLD_ORDER, new PlayerSetDownGoldOrderHandler(), Main.I.getPlayerMessageProcess());
        /**
         * 获取财务是否有未审核的下分订单
         */
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_GET_WAIT_DOWN_GOLD_ORDER, new PlayerGetWaitDownGoldOrderHandler(), Main.I.getPlayerMessageProcess());
        /**
         * 获取club成员在本圈中可再次下分的剩余时间
         */
        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_GET_DOWN_GOLD_TIME, new PlayerGetDownGoldTimeHandler(), Main.I.getPlayerMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_PLAYER_ENTER_CLUB, new PlayerEnterClubdHandler(), Main.I.getClubMessageProcess());
        /**
         * 奖励分兑换成竞技分
         */
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_EXCHANGE_REWARD_VALUE, new ClubExchangeRewardValueHandler(), Main.I.getClubMessageProcess());
        
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_CONSUME_CARD_LIST, new ClubConsumeCardListHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_CONSUME_CARD_DAY_LIST, new ClubConsumeCardDayListHandler(), Main.I.getAsyncMessageProcess());
        
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_APPLY_CLOSE, new ClubApplyCloseHandler(), Main.I.getClubMessageProcess());
        
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_HELPER_INVITE_PLAYERS, new ClubHelperInvitePlayersHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_HELPER_INVITE, new ClubHelperInviteHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_HELPER_INVITE_SET_INFO, new ClubHelperInviteSetInfoHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_HELPER_INVITE_SET, new ClubHelperInviteSetHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_HELPER_INVITE_ANSWER, new ClubHelperInviteAnswerHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_HELPER_INFO, new ClubHelperInfoHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_HALL_HELPER_INFO, new HallHelperInfoHandler(), Main.I.getAsyncMessageProcess());
        
        
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_ONLYUPLINESETGOLD, new ClubSetOnlyUpLineSetGoldHandler(), Main.I.getClubMessageProcess());
        //-- 请求群推荐码
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_RECOMMEND_CODE, new ClubGetRecommendCodeHandler(), Main.I.getClubMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_JOIN_BY_RECOMMEND_CODE, new ClubJoinByRecommendCodeHandler(), Main.I.getClubMessageProcess());
        
        this.messageReceive.register(CommandId.CLI_REQ_CLUB_LIKE_GAME, new ClubSetLikeGameHandler(), Main.I.getClubMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_GET_MAINCHARGE, new ClubGetClubLevelChargeHandler(), Main.I.getClubMessageProcess());

        this.messageReceive.register(CommandId.CLI_REQ_CLUB_SET_MAINCHARGE, new ClubSetClubLevelChargeHandler(), Main.I.getClubMessageProcess());

        
        // LandLord
        this.messageReceive.register(CommandId.CLI_REQ_POKER_LAND_LORD_CALL_SCORE, new PokerLandLordCallScoreHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_LAND_LORD_SHOW_CARD, new PokerLandLordShowCardHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_LAND_LORD_MULTIPLE, new PokerLandLordMultipleHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_LAND_LORD_KICK_BACK_SELECT, new PokerLandLordKickBackSelectHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_LAND_LORD_KICK_SELECT, new PokerLandLordKickSelectHandler(), Main.I.getRoomMessageProcess());
        // fgf
        this.messageReceive.register(CommandId.CLI_REQ_POKER_FGF_ADD_NOTE, new PokerFGFAddNoteHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_FGF_FOLLOW_NOTE, new PokerFGFFollowNoteHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_FGF_COMPARE, new PokerFGFCompareHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_GIVE_GOLD, new ClubGiveGoldHandler(), Main.I.getClubMessageProcess());

        // 牌九
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PAI_GOW_REBET, new PaiGowRebetHandler(), Main.I.getRoomMessageProcess());//牌九下注
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PAI_GOW_OPEN, new PaiGowOpenCardHandler(), Main.I.getRoomMessageProcess());//牌九开牌
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PAI_GOW_ROB_BANKER, new PokerPaiGowRobBankerHandler(), Main.I.getRoomMessageProcess());//牌九选庄
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PAI_GOW_OUT_HOT, new PaiGowOutHotHandler(), Main.I.getRoomMessageProcess());//加锅牌九切锅
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PAI_GOW_READY, new PaiGowHotReadyHandler(), Main.I.getRoomMessageProcess());//加锅牌九准备
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PAI_GOW_HOT_AGAIN, new PaiGowHotAgainHandler(), Main.I.getRoomMessageProcess());//加锅牌九续锅
        this.messageReceive.register(CommandId.CLI_REQ_POKER_PAI_GOW_HOT_OUT, new PaiGowHotOutHandler(), Main.I.getRoomMessageProcess());


        // 获取扎金花牛牛战绩
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_SCORE_RECORD, new BoxArenaScoreRecordHandler(), Main.I.getClubMessageProcess());

        //thirteen
        this.messageReceive.register(CommandId.CLI_REQ_POKER_THIRTEEN_TAKE, new PokerThirteenTakeHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_POKER_THIRTEEN_SORT_CARD, new PokerThirteenSortCardHandler(), Main.I.getRoomMessageProcess());

        //百人场
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_LEAVE, new HundredLeaveHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_UP_BANKER, new HundredUpBankerHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_DOWN_BANKER, new HundredDownBankerHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_BANKER_LIST, new HundredBankerListHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_SELFBANKER_LIST, new HundredSelfBankerListHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_PLAYER_LIST, new HundredPlayerListHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_RECORD, new HundredRecordHandler(), Main.I.getAsyncMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_REB, new HundredRebHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_VIP_SEAT_OP, new HundredVipSeatOpHandler(), Main.I.getRoomMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ARENA_HUNDRED_BANKER_RECORD, new HundredBankerRecordHandler(), Main.I.getAsyncMessageProcess());
    
        //机器人
        this.messageReceive.register(CommandId.CLI_REQ_ROBOT_ROOM_JOIN, new RobotJoinHandler(), Main.I.getBoxMessageProcess());
        this.messageReceive.register(CommandId.CLI_REQ_ROBOT_GOOD_CARD, new RobotCardHandler(), Main.I.getBoxMessageProcess());
    }

    @Override
    protected ChannelHandler getChannelHandler() {
        if (null == this.factory) {
            this.factory = new WebSocketServerHandshakerFactory("ws://" + this.host + ":" + this.port + "/" + this.PATH, null, false);
        }
        return new GatewayServerChannelHandler(this.connectionManager, this.messageReceive, new SessionContextFactory() {
            @Override
            public SessionContext create() {
                return new GatewaySessionContext();
            }
        }, this.factory, "/" + this.PATH);
    }

    @Override
    protected void doStart(FutureListener serviceListener) {
        super.doStart(serviceListener);

        Logs.NET.info("========================================================");
        Logs.NET.info("============GATEWAY SERVER START SUCCESS================");
        Logs.NET.info("========================================================");
    }

    @Override
    protected void doStop(FutureListener serviceListener) {
        super.doStop(serviceListener);
        Logs.NET.info("========================================================");
        Logs.NET.info("============GATEWAY SERVER STOP SUCCESS=================");
        Logs.NET.info("========================================================");
    }
}
