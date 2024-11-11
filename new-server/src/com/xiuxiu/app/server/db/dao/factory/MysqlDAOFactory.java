package com.xiuxiu.app.server.db.dao.factory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.xiuxiu.app.server.db.dao.*;
import com.xiuxiu.app.server.db.dao.impl.mysql.*;
import com.xiuxiu.app.server.statistics.DownLineGameRecord;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;

public class MysqlDAOFactory implements DAOFactory {
    private static class MysqlDAOFactoryHolder {
        private static MysqlDAOFactory instance = new MysqlDAOFactory();
    }

    public static MysqlDAOFactory I = MysqlDAOFactoryHolder.instance;

    private SqlSessionFactory sqlSessionFactory;
    private final AccountDAO accountDAO;
    private final PlayerDAO playerDAO;
    private final RoomDAO roomDAO;
    private final RoomScoreDAO roomScoreDAO;
    private final MailDAO mailDAO;
    private final BoxRoomScoreDAO boxRoomScoreDAO;
    private final RecommendDAO recommendDAO;
    private final IMailBoxDAO mailBoxDAO;
    private final ILogAccountDAO logAccountDAO;
    private final INicknameDAO nicknameDAO;
    private final ILogAccountRemainDAO logAccountRemainDAO;
    private final IAccountUidDAO accountUidDAO;
    private final IAssistantWeChatDAO assistantWeChatDAO;
    private final ILocationInfoDAO locationInfoDAO;
    private final IClubGoldRecordDAO clubGoldRecordDAO;
    private final IClubRewardValueRecordDAO clubRewardValueRecordDAO;
    private final ITodayStatisticsDAO todayStatisticsDAO;
    private final IRankDataDAO rankDataDAO;
    private final IMoneyExpendRecordDao moneyExpendRecordDao;
    private final IPlayerMoneyConsumeRecordDAO playerMoneyConsumeRecordDAO;
    private final IForbidDAO forbidDAO;
    private final IClubActivityDAO clubActivityDAO;
    private final IBoxDAO boxDao;
    private final IFloorDAO floorDAO;
    private final IClubMemberExtDAO clubMemberExtDAO;
    private final IClubInfoDAO clubInfoDAO;
    private final IClubMemberDAO clubMemberDAO;
    private final IClubActivityGoldRewardRecordDAO clubActivityGoldRewardRecordDAO;
    private final IBoxRoomScorePlayerIdDAO boxRoomScorePlayerIdDAO;
    private final IMoneyExpendRecordDetailDao moneyExpendRecordDetailDao;
    private final IClubUidDAO clubUidDAO;
    private final IUniqueCodeDAO uniqueCodeDAO;
    private final IUpDownGoldOrderDAO upDownGoldOrderDAO;
    private final IDownLineGameRecordDAO downLineGameRecordDAO;
    private final IBoxArenaScoreDao boxArenaScoreDao;
    private final IBoxArenaScoreInfoDao boxArenaScoreInfoDao;
    private final IBoxArenaScoreInfoPlayerIdDao boxArenaScoreInfoPlayerIdDao;
    private final IHundredRebRecordDAO hundredRebRecordDAO;
    private final IHundredBureauRecordDAO hundredBureauRecordDAO;
    
    /**********lcadd**********/
    private final INoticeDAO noticeDAO;

    private MysqlDAOFactory() {
        try (InputStream is = new FileInputStream(Config.MYSQL_CONFIG_PATH)) {
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        } catch (FileNotFoundException e) {
            Logs.DB.error(e);
        } catch (IOException e) {
            Logs.DB.error(e);
        }

        this.accountDAO = new AccountDAOImpl(this.sqlSessionFactory);
        this.playerDAO = new PlayerDAOImpl(this.sqlSessionFactory);
        this.roomDAO = new RoomDAOImpl(this.sqlSessionFactory);
        this.roomScoreDAO = new RoomScoreDAOImpl(this.sqlSessionFactory);
        this.mailDAO = new MailDAOImpl(this.sqlSessionFactory);
        this.boxRoomScoreDAO = new BoxRoomScoreDAOImpl(this.sqlSessionFactory);
        this.recommendDAO = new RecommendDAOImpl(this.sqlSessionFactory);
        this.mailBoxDAO = new MailBoxDAOImpl(this.sqlSessionFactory);
        this.logAccountDAO = new LogAccountDAOImpl(this.sqlSessionFactory);
        this.nicknameDAO = new NicknameDAOImpl(this.sqlSessionFactory);
        this.logAccountRemainDAO = new LogAccountRemainDAOImpl(this.sqlSessionFactory);
        this.accountUidDAO = new AccountUidDAOImpl(this.sqlSessionFactory);
        this.assistantWeChatDAO = new AssistantWeChatDAOImpl(this.sqlSessionFactory);
        this.locationInfoDAO = new LocationInfoDAOImpl(this.sqlSessionFactory);
        this.todayStatisticsDAO = new TodayStatisticsDAOImpl(this.sqlSessionFactory);
        this.rankDataDAO = new RankDataDAOImpl(this.sqlSessionFactory);
        this.moneyExpendRecordDao = new MoneyExpendRecordDaoImpl(this.sqlSessionFactory);
        this.playerMoneyConsumeRecordDAO = new PlayerMoneyConsumeRecordDAOImpl(sqlSessionFactory);
        this.forbidDAO = new ForbidDAOImpl(sqlSessionFactory);
        this.boxDao = new BoxDAOImpl(sqlSessionFactory);
        this.floorDAO = new FloorDAOImpl(sqlSessionFactory);
        this.clubMemberExtDAO = new ClubMemberExtDAOImpl(sqlSessionFactory);
        this.clubInfoDAO = new ClubInfoDAOImpl(sqlSessionFactory);
        this.clubMemberDAO = new ClubMemberDAOImpl(sqlSessionFactory);
        this.clubActivityDAO = new ClubActivityDAOImpl(sqlSessionFactory);
        this.clubGoldRecordDAO = new ClubGoldRecordDAOImpl(this.sqlSessionFactory);
        this.clubRewardValueRecordDAO = new ClubRewardValueRecordDAOImpl(this.sqlSessionFactory);
        this.clubActivityGoldRewardRecordDAO = new ClubActivityGoldRewardRecordDAOImpl(this.sqlSessionFactory);
        this.clubUidDAO = new ClubUidDAOImpl(sqlSessionFactory);
        this.boxRoomScorePlayerIdDAO = new BoxRoomScorePlayerIdDAOImpl(sqlSessionFactory);
        this.moneyExpendRecordDetailDao = new MoneyExpendRecordDetailDaoImpl(sqlSessionFactory);
        this.uniqueCodeDAO = new UniqueCodeDAOImpl(sqlSessionFactory);
        this.upDownGoldOrderDAO = new UpDownGoldOrderDAOImpl(sqlSessionFactory);
        this.downLineGameRecordDAO = new DownLineGameRecordDAOImpl(sqlSessionFactory);
        this.boxArenaScoreDao = new BoxArenaScoreDaoImpl(sqlSessionFactory);
        this.boxArenaScoreInfoDao = new BoxArenaScoreInfoDaoImpl(sqlSessionFactory);
        this.boxArenaScoreInfoPlayerIdDao =new BoxArenaScoreInfoPlayerIdDaoImpl(sqlSessionFactory);
        this.hundredRebRecordDAO = new HundredRebRecordDAOImpl(this.sqlSessionFactory);
        this.hundredBureauRecordDAO = new HundredBureauRecordDAOImpl(this.sqlSessionFactory);
        
        /**********lcadd**********/
        this.noticeDAO = new NoticeDAOImpl(this.sqlSessionFactory);
    }

    @Override
    public AccountDAO getAccountDAO() {
        return this.accountDAO;
    }

    @Override
    public PlayerDAO getPlayerDAO() {
        return this.playerDAO;
    }

    @Override
    public RoomDAO getRoomDAO() {
        return this.roomDAO;
    }

    @Override
    public RoomScoreDAO getRoomScoreDAO() {
        return this.roomScoreDAO;
    }

    @Override
    public MailDAO getMailDAO() {
        return this.mailDAO;
    }

    @Override
    public BoxRoomScoreDAO getBoxRoomScoreDAO() {
        return this.boxRoomScoreDAO;
    }

    @Override
    public RecommendDAO getRecommendDAO() {
        return this.recommendDAO;
    }

    @Override
    public IMailBoxDAO getMailBoxDAO() {
        return this.mailBoxDAO;
    }

    @Override
    public ILogAccountDAO getLogAccountDAO() {
        return this.logAccountDAO;
    }

    @Override
    public INicknameDAO getNicknameDAO() {
        return this.nicknameDAO;
    }

    @Override
    public ILogAccountRemainDAO getLogAccountRemainDAO() {
        return this.logAccountRemainDAO;
    }

    @Override
    public IAccountUidDAO getAccountUidDAO() {
        return this.accountUidDAO;
    }

    @Override
    public IAssistantWeChatDAO getAssistantWeChatDAO() {
        return this.assistantWeChatDAO;
    }

    @Override
    public ILocationInfoDAO getLocationInfoDAO() {
        return this.locationInfoDAO;
    }

    @Override
    public IClubGoldRecordDAO getClubGoldRecordDAO() {
        return this.clubGoldRecordDAO;
    }

    @Override
    public IClubRewardValueRecordDAO getClubRewardValueRecordDAO() {
        return this.clubRewardValueRecordDAO;
    }

    @Override
    public ITodayStatisticsDAO getTodayStatisticsDAO() {
        return this.todayStatisticsDAO;
    }

    @Override
    public IRankDataDAO getRankDataDAO() {
        return this.rankDataDAO;
    }

    @Override
    public IMoneyExpendRecordDao getMoneyExpendRecordDao() {
        return this.moneyExpendRecordDao;
    }

    @Override
    public IPlayerMoneyConsumeRecordDAO getPlayerMoneyConsumeRecordDAO() {
        return this.playerMoneyConsumeRecordDAO;
    }

    @Override
    public IForbidDAO getForbidDAO() {
        return this.forbidDAO;
    }

    @Override
    public IClubInfoDAO getClubInfoDAO() {
        return this.clubInfoDAO;
    }
    
    @Override
    public IClubMemberDAO getClubMemberDAO() {
        return this.clubMemberDAO;
    }

    @Override
    public IClubActivityDAO getClubActivityDAO() {
        return this.clubActivityDAO;
    }

    @Override
    public IBoxDAO getBoxDao() {
        return this.boxDao;
    }

    @Override
    public IFloorDAO getFloorDao() {
        return this.floorDAO;
    }

    @Override
    public IClubMemberExtDAO getClubMemberExtDAO() {
        return this.clubMemberExtDAO;
    }

    @Override
    public IClubUidDAO getClubUidDAO() {
        return this.clubUidDAO;
    }

    @Override
    public IBoxRoomScorePlayerIdDAO getBoxRoomScorePlayerIdDAO() {
        return this.boxRoomScorePlayerIdDAO;
    }

    @Override
    public IClubActivityGoldRewardRecordDAO getClubActivityGoldRewardRecordDAO() {
        return clubActivityGoldRewardRecordDAO;
    }

    @Override
    public IMoneyExpendRecordDetailDao getMoneyExpendRecordDetailDao() {
        return moneyExpendRecordDetailDao;
    }

    @Override
    public IUniqueCodeDAO getUniqueCodeDao() {
        return this.uniqueCodeDAO;
    }

    @Override
    public IUpDownGoldOrderDAO getUpDownGoldOrderDao() {
        return this.upDownGoldOrderDAO;
    }

    @Override
    public IDownLineGameRecordDAO getDownLineGameRecordDAO() {
        return this.downLineGameRecordDAO;
    }
    
    @Override
    public IBoxArenaScoreDao getBoxArenaScoreDao() {
        return this.boxArenaScoreDao;
    }

    @Override
    public IBoxArenaScoreInfoDao getBoxArenaScoreInfoDao() {
        return this.boxArenaScoreInfoDao;
    }

    @Override
    public IBoxArenaScoreInfoPlayerIdDao getBoxArenaScoreInfoPlayerIdDao() {
        return this.boxArenaScoreInfoPlayerIdDao;
    }

    @Override
    public IHundredRebRecordDAO getHundredRebRecordDAO() {
        return hundredRebRecordDAO;
    }

    @Override
    public IHundredBureauRecordDAO getHundredBureauRecordDAO() {
        return hundredBureauRecordDAO;
    }
    
    /**********lcadd**********/
    @Override
    public INoticeDAO getNoticeDAO() {
        return noticeDAO;
    }
    
}
