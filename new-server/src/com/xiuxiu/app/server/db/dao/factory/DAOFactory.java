package com.xiuxiu.app.server.db.dao.factory;

import com.xiuxiu.app.server.db.dao.*;

public interface DAOFactory {
    static DAOFactory get() {
        return MysqlDAOFactory.I;
    }

    AccountDAO getAccountDAO();
    PlayerDAO getPlayerDAO();
    RoomDAO getRoomDAO();
    RoomScoreDAO getRoomScoreDAO();
    MailDAO getMailDAO();
    BoxRoomScoreDAO getBoxRoomScoreDAO();
    RecommendDAO getRecommendDAO();
    IMailBoxDAO getMailBoxDAO();
    ILogAccountDAO getLogAccountDAO();
    INicknameDAO getNicknameDAO();
    ILogAccountRemainDAO getLogAccountRemainDAO();
    IAccountUidDAO getAccountUidDAO();
    IAssistantWeChatDAO getAssistantWeChatDAO();
    ILocationInfoDAO getLocationInfoDAO();
    ITodayStatisticsDAO getTodayStatisticsDAO();
    IRankDataDAO getRankDataDAO();
    IMoneyExpendRecordDao getMoneyExpendRecordDao();
    IPlayerMoneyConsumeRecordDAO getPlayerMoneyConsumeRecordDAO();
    IForbidDAO getForbidDAO();
    IBoxDAO getBoxDao();
    IFloorDAO getFloorDao();
    IClubMemberExtDAO getClubMemberExtDAO();

    IClubInfoDAO getClubInfoDAO();
    IClubMemberDAO getClubMemberDAO();
    IClubActivityDAO getClubActivityDAO();
    IClubGoldRecordDAO getClubGoldRecordDAO();
    IClubRewardValueRecordDAO getClubRewardValueRecordDAO();
    IClubActivityGoldRewardRecordDAO getClubActivityGoldRewardRecordDAO();
    IClubUidDAO getClubUidDAO();
    
    IBoxRoomScorePlayerIdDAO getBoxRoomScorePlayerIdDAO();
    
    IMoneyExpendRecordDetailDao getMoneyExpendRecordDetailDao();

    IUniqueCodeDAO getUniqueCodeDao();

    IUpDownGoldOrderDAO getUpDownGoldOrderDao();

    IDownLineGameRecordDAO getDownLineGameRecordDAO();
    IBoxArenaScoreDao getBoxArenaScoreDao();

    IBoxArenaScoreInfoDao getBoxArenaScoreInfoDao();

    IBoxArenaScoreInfoPlayerIdDao getBoxArenaScoreInfoPlayerIdDao();
    IHundredRebRecordDAO getHundredRebRecordDAO();
    IHundredBureauRecordDAO getHundredBureauRecordDAO();
    
    /**********lcadd**********/
    INoticeDAO getNoticeDAO();
}
