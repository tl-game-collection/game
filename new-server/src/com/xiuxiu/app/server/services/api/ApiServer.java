
package com.xiuxiu.app.server.services.api;

import java.util.concurrent.Executors;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.services.api.announcements.ClearAnnouncementsHandler;
import com.xiuxiu.app.server.services.api.announcements.GetAnnouncementsHandler;
import com.xiuxiu.app.server.services.api.announcements.PostAnnouncementHandler;
import com.xiuxiu.app.server.services.api.diamond.GetDiamondCostDailyListHandler;
import com.xiuxiu.app.server.services.api.old.WeChatInfoHandler;
import com.xiuxiu.app.server.services.api.old.ForceLeaveArenaHandler;
import com.xiuxiu.app.server.services.api.old.GetPlayerMoneyExpendRecordHandler;
import com.xiuxiu.app.server.services.api.old.ModifyUserPrivilegeHandler;
import com.xiuxiu.app.server.services.api.old.MoneyExpendRecordListHandler;
import com.xiuxiu.app.server.services.api.player.*;
import com.xiuxiu.app.server.services.api.robot.*;
import com.xiuxiu.app.server.services.api.auth.PhoneAuthHandler;
import com.xiuxiu.app.server.services.api.club.*;
import com.xiuxiu.app.server.services.api.game.*;
import com.xiuxiu.app.server.services.api.notice.GetNoticesHandler;
import com.xiuxiu.app.server.services.api.trade.*;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.Server;
import com.xiuxiu.core.service.BaseService;
import com.xiuxiu.core.service.FutureListener;
import com.xiuxiu.core.thread.NameThreadFactory;

public class ApiServer extends BaseService implements Server {
    private com.sun.net.httpserver.HttpServer httpServer;

    private String ip;
    private int port;

    public ApiServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        Logs.API.debug(" ApiServer :%s", ip+":"+port);
    }

    @Override
    public void init() {
        this.httpServer = HttpServer.create(this.ip, this.port, false);
        this.httpServer.setExecutor(Executors.newSingleThreadExecutor(new NameThreadFactory("ApiServer")));

        UseMahjongCardLibHandler useMahjongCardLibHandler = new UseMahjongCardLibHandler();
        SetMahjongCardLibHandler setMahjongCardLibHandler = new SetMahjongCardLibHandler();
        UsePokerCardLibHandler usePokerCardLibHandler = new UsePokerCardLibHandler();
        SetPokerCardLibHandler setPokerCardLibHandler = new SetPokerCardLibHandler();
        UseSameCrapHandler useSameCrapHandler = new UseSameCrapHandler();
        PhoneAuthHandler phoneAuthHandler = new PhoneAuthHandler();

        this.httpServer.createContext("/useMahjongCardLib", useMahjongCardLibHandler);
        this.httpServer.createContext("/setMahjongCardLib", setMahjongCardLibHandler);
        this.httpServer.createContext("/usePokerCardLib", usePokerCardLibHandler);
        this.httpServer.createContext("/setPokerCardLib", setPokerCardLibHandler);
        this.httpServer.createContext("/useSameCrap", useSameCrapHandler);
        this.httpServer.createContext("/phoneAuth", phoneAuthHandler);

//        this.httpServer.createContext("/mail", mailHandler);
//        this.httpServer.createContext("/whistleBlowing", whistleBlowingHandler);
//        this.httpServer.createContext("/v1/chatTest", chatTestHandler);
//        this.httpServer.createContext("/v1/addIp", new AddIpHandler());

        //this.httpServer.createContext("/v1/getUserDiamondRecord", );
        //this.httpServer.createContext("/v1/getMyGroupDiamondRecord", );
//        this.httpServer.createContext("/v1/getMyGroupsInfo", new GetMyGroupsInfoHandler());
//        this.httpServer.createContext("/v1/addWhite", new AddWhiteHandler());
//        this.httpServer.createContext("/v1/getWhiteList", new GetWhiteListHandler());
//        this.httpServer.createContext("/v1/getStatusInfo", new GetStatusInfoHandler());
//        this.httpServer.createContext("/v1/addUserArena", new AddUserArenaHandler());
//        this.httpServer.createContext("/v1/getGroupInfoByGid", new GetGroupInfoByGidHandler());
//        this.httpServer.createContext("/v1/getGroupInfoByArenaUid", new GetGroupInfoByGidHandler());
//        this.httpServer.createContext("/v1/getGroupCostDetail", new GetGroupCostDetailHandler());
//        this.httpServer.createContext("/v1/getMyGroupCostSum", new GetMyGroupCostSumHandler());
//        this.httpServer.createContext("/v1/getGroupInfo", new GetGroupInfoHandler());
//        this.httpServer.createContext("/v1/getMyGroupCostSumByGroupId", new GetMyGroupCostSumByGroupIdHandler());
//        this.httpServer.createContext("/v1/getMyGroupCostDetailByGroupId", new GetMyGroupCostDetailByGroupIdHandler());
//        this.httpServer.createContext("/v1/playerWalletRecharge", new PlayerWalletRechargeHandler());
//        this.httpServer.createContext("/v1/createAccounts", new CreateAccountsHandler());
//        this.httpServer.createContext("/v1/getAccounts", new GetAccountsHandler());
//        this.httpServer.createContext("/v1/getDailyActiveCount", new GetDailyActiveCountHandler());
//        this.httpServer.createContext("/v1/getLogAccountRemain", new GetLogAccountRemainHandler());
//        this.httpServer.createContext("/v1/dailyShareAward", new GetDailyShareAwardHandler());
//        this.httpServer.createContext("/v1/getDailyServiceCharge", new GetServiceChargeDailyListHandler());
//        this.httpServer.createContext("/v1/getGroupMemberDownLine", new GetGroupMemberDownLineHandler());
//        this.httpServer.createContext("/v1/setGroupMemberUpLine", new SetGroupMemberUpLineHandler());
//        this.httpServer.createContext("/v1/getGroupList", new GetGroupListHandler());
//        this.httpServer.createContext("/v1/setAssistantWeChat", new SaveAssistantWeChatHandler());
//        this.httpServer.createContext("/v1/getAssistantWeChatList", new GetAssistantWeChatListHandler());
//        this.httpServer.createContext("/v1/modifyAssistantWeChat", new ModifyAssistantWeChatHandler());
//
//        this.httpServer.createContext("/v1/getApiUserLoginInfo", new GetApiUserLoginInfoHandler());
//        this.httpServer.createContext("/v1/apiUserLogin", new ApiUserLoginHandler());
//        this.httpServer.createContext("/v1/apiUserLogout", new ApiUserLogoutHandler());
//        this.httpServer.createContext("/v1/getApiUserInfo", new GetApiUserInfoHandler());
//        this.httpServer.createContext("/v1/bindBizChannel", new BindBizChannel());
//        this.httpServer.createContext("/v1/unBindBizChannel", new UnBindBizChannel());
//
//        this.httpServer.createContext("/v1/removeAssistantWeChat", new RemoveAssistantWeChatHandler());
//        this.httpServer.createContext("/v1/addPush", new AddPushHandler());
//        this.httpServer.createContext("/v1/setPlayerPrivilege", new SetPlayerPrivilegeHandler());
//
//        this.httpServer.createContext("/v1/getGroupMembersByGroupUid", new GetGroupMembersHandler());

        /**
         * 强制退出竞技场
         */
        this.httpServer.createContext("/v1/forceLeaveArena", new ForceLeaveArenaHandler());
//
        this.httpServer.createContext("/v1/wechatinfo", new WeChatInfoHandler());
        /**
         * 新增账号
         */
        this.httpServer.createContext("/v1/addAccount", new AddAccountHandler());
        /**
         * 创建俱乐部
         */
        this.httpServer.createContext("/v1/addCreateClub", new AddCreateClubHandler());
        this.httpServer.createContext("/v1/getPlayerList", new GetPlayerListHandler());
        this.httpServer.createContext("/v1/getOnlineCount", new GetOnlineCountHandler());
        this.httpServer.createContext("/v1/getOnlinePlayerUids", new GetOnlinePlayerHandler());

        this.httpServer.createContext("/v1/modifyUserPrivilege", new ModifyUserPrivilegeHandler());
        this.httpServer.createContext("/v1/moneyExpendRecordList", new MoneyExpendRecordListHandler());
        this.httpServer.createContext("/v1/getPlayerMoneyTotalConsumeCount", new GetPlayerMoneyExpendRecordHandler());
        /**
         * 发布公告
         */
        this.httpServer.createContext("/v1/postAnnouncement", new PostAnnouncementHandler());
        /**
         * 获取公告
         */
        this.httpServer.createContext("/v1/getAnnouncements", new GetAnnouncementsHandler());
        this.httpServer.createContext("/v1/clearAnnouncements", new ClearAnnouncementsHandler());
        this.httpServer.createContext("/v1/statAccountActions", new StatAccountActionsHandler());
        this.httpServer.createContext("/v1/banAccount", new BanAccountHandler());
        this.httpServer.createContext("/v1/modifyUserRecommend", new ModifyUserRecommendHandler());
        this.httpServer.createContext("/v1/getUserInfo", new GetUserInfoHandler());
        this.httpServer.createContext("/v1/addUserMoneyByDaTang", new AddUserMoneyByDATangHandler());
        this.httpServer.createContext("/v1/getDailyDiamondCost", new GetDiamondCostDailyListHandler());
        /**
         * 俱乐部列表
         */
        this.httpServer.createContext("/v1/getClubList", new GetClubListHandler());
        /**
         * 运营后台圈主 邀请玩家
         */
        this.httpServer.createContext("/v1/addClubMember", new ClubAddMemberHandler());
        this.httpServer.createContext("/v1/getClubRobotNum", new GetClubRobotNumHandler());
        
        
        
        this.httpServer.createContext("/v1/getClubRelations", new GetClubRelationsHandler());
        this.httpServer.createContext("/v1/getClubExpend", new GetClubExpendHandler());
        /**
         * 查询群管理下线成员
         */
        this.httpServer.createContext("/v1/getClubMemberDownLine", new GetClubMemberDownLineHandler());
        this.httpServer.createContext("/v1/getOwnerClubInfo", new GetOwnerClubInfoHandler());
        this.httpServer.createContext("/v1/addClubGameDesk", new AddClubGameDeskHandler());
        /**
         * 群成员列表
         */
        this.httpServer.createContext("/v1/getClubMemberList", new GetClubMemberListHandler());
        /**
         * 修改群玩家昵称头像接口
         */
        this.httpServer.createContext("/v1/clubMemberManager", new ClubMemberManagertHandler());
        /**
         * 后台解散房间
         */
        this.httpServer.createContext("/v1/clubDissolveRoom", new ClubDissolveRoomHandler());
        /**
         * 根据群id查询群成员列表
         */
        this.httpServer.createContext("/v1/getClubMemberRelationList", new GetClubMemberRelationListHandler());
        /**
         * 踢出俱乐部
         */
        this.httpServer.createContext("/v1/kickOutClub", new KickOutClubHandler());

        this.httpServer.createContext("/v1/addWhiteList", new AddWhiteListHandler());
        this.httpServer.createContext("/v1/delWhiteList", new DelWhiteListHandler());
        this.httpServer.createContext("/v1/addNoRankList", new AddNoRankListHandler());
        this.httpServer.createContext("/v1/delNoRankList", new DelNoRankListHandler());
        this.httpServer.createContext("/v1/setHundredWin", new SetHundredWinHandler());

        /**
         * 修改群玩家上线接口
         */
        this.httpServer.createContext("/v1/clubModifyUpLine", new ClubModifyUpLineHandler());
        /**
         * 修改群玩家竞技分
         */
        this.httpServer.createContext("/v1/clubModifyMemberGold", new ClubModifyMemberGoldHandler());
        /**
         * 查询群玩家竞技分
         */
        this.httpServer.createContext("/v1/getClubMemberGold", new GetClubMemberGoldHandler());
        /**
         * 查询成员上线*N
         */
        this.httpServer.createContext("/v1/getClubMemberUpLines", new GetClubMemberUpLinesHandler());
        /**
         * 修改绑定手机号
         */
        this.httpServer.createContext("/v1/changeAccountPhone", new ChangeAccountPhoneHandler());
        /**
         * 获取玩家财务下分记录
         */
        this.httpServer.createContext("/v1/getPlayerDownGoldRecord", new GetPlayerDownGoldRecordHandler());
        /**
         * 获取亲友圈所有玩家列表
         */
        this.httpServer.createContext("/v1/getClubAllMemberList", new GetClubAllMemberListHandler());
        /**
         * 修改玩家昵称
         */
        this.httpServer.createContext("/v1/setPlayerName", new SetPlayerNameHandler());
        /**
         * 添加或删除营商
         */
        this.httpServer.createContext("/v1/addOrDelTreasurer", new AddOrDelTreasurerHandler());
        /**
         * 修改营商描述
         */
        this.httpServer.createContext("/v1/changeTreasurer", new ChangeTreasurerHandler());
        /**
         * 搜索银商
         */
        this.httpServer.createContext("/v1/searchTreasurer", new SearchTreasurerHandler());
        /**
         * 修改银商配置信息
         */
        this.httpServer.createContext("/v1/changeTreasurerInfo", new ChangeTreasureInfoHandler());
        /**
         * 获取银商配置信息
         */
        this.httpServer.createContext("/v1/searchTreasurerData", new SearchTreasurerDataHandler());
        
        /**
         * 根据playerUid获取所属俱乐部列表
         */
        this.httpServer.createContext("/v1/getClubs", new GetClubsHandler());
        /**
         * 根据clubUid获取所属玩法列表
         */
        this.httpServer.createContext("/v1/getPlays", new GetPlaysHandler());
        /**
         * 根据roomId和playerUid获取房间并退出房间
         */
        this.httpServer.createContext("/v1/roomLeave", new RoomLeaveHandler());
        /**
         * 根据pageNum和size获取所有机器人列表
         */
//        this.httpServer.createContext("/v1/roomLeave", new GetRobotsHandler());
        /**
         * 根据playerUid和clubId删除俱乐部机器人
         */
        this.httpServer.createContext("/v1/delClubRobot", new ClubDelMemberHandler());
        
        /**
         * 获取大厅公告
         */
        this.httpServer.createContext("/v1/getNotices", new GetNoticesHandler());
    }

    @Override
    protected void doStart(FutureListener serviceListener) {
        this.httpServer.start();
        serviceListener.onSucc();
        Logs.NET.info("========================================================");
        Logs.NET.info("=============API SERVER START SUCCESS===================");
        Logs.NET.info("========================================================");
    }

    @Override
    protected void doStop(FutureListener serviceListener) {
        this.httpServer.stop(0);
        serviceListener.onSucc();
        Logs.NET.info("========================================================");
        Logs.NET.info("=============API SERVER STOP SUCCESS====================");
        Logs.NET.info("========================================================");
    }
}
