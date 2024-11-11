package com.xiuxiu.app.server.db;

import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.*;
import com.xiuxiu.app.server.db.dao.factory.DAOFactory;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.thread.ConsumeThread;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DBManager extends BaseManager {
    private static class DBManagerHolder {
        private static DBManager instance = new DBManager();
    }

    public static DBManager I = DBManagerHolder.instance;

    private DBSaveThread saveThread = new DBSaveThread();

    private DAOFactory daoFactory = DAOFactory.get();
    private RedissonClient redissonClient;

    private DBManager() {
    }

    public void init(RedissonClient client) {
        this.redissonClient = client;
        this.saveThread.start();
    }

    public AccountDAO getAccountDao() {
        return this.daoFactory.getAccountDAO();
    }

    public PlayerDAO getPlayerDao() {
        return this.daoFactory.getPlayerDAO();
    }

    public RoomDAO getRoomDao() {
        return this.daoFactory.getRoomDAO();
    }

    public RoomScoreDAO getRoomScoreDao() {
        return this.daoFactory.getRoomScoreDAO();
    }

    public MailDAO getMailDao() {
        return this.daoFactory.getMailDAO();
    }

    public BoxRoomScoreDAO getBoxRoomScoreDao() {
        return this.daoFactory.getBoxRoomScoreDAO();
    }

    public RecommendDAO getRecommendDao() {
        return this.daoFactory.getRecommendDAO();
    }

    public IMailBoxDAO getMailBoxDao() {
        return this.daoFactory.getMailBoxDAO();
    }

    public ILogAccountDAO getLogAccountDAO() {
        return this.daoFactory.getLogAccountDAO();
    }

    public INicknameDAO getNicknameDAO() {
        return this.daoFactory.getNicknameDAO();
    }

    public ILogAccountRemainDAO getLogAccountRemainDAO(){
        return this.daoFactory.getLogAccountRemainDAO();
    }

    public IAccountUidDAO getAccountUidDao() {
        return this.daoFactory.getAccountUidDAO();
    }

    public IAssistantWeChatDAO getAssistantWeChatDAO() {
        return this.daoFactory.getAssistantWeChatDAO();
    }

    public ILocationInfoDAO getLocationInfoDAO() {
        return this.daoFactory.getLocationInfoDAO();
    }

    public IClubGoldRecordDAO getClubGoldRecordDAO() {
        return this.daoFactory.getClubGoldRecordDAO();
    }

    public IClubRewardValueRecordDAO getClubRewardValueRecordDAO() {
        return this.daoFactory.getClubRewardValueRecordDAO();
    }

    public ITodayStatisticsDAO getTodayStatisticsDao(){
        return this.daoFactory.getTodayStatisticsDAO();
    }

    public IRankDataDAO getRankDataDao(){
        return this.daoFactory.getRankDataDAO();
    }

    public IMoneyExpendRecordDao getMoneyExpendRecordDao(){
        return this.daoFactory.getMoneyExpendRecordDao();
    }
    
    public IMoneyExpendRecordDetailDao getMoneyExpendRecordDetailDao(){
        return this.daoFactory.getMoneyExpendRecordDetailDao();
    }
    
    public IPlayerMoneyConsumeRecordDAO getPlayerMoneyConsumeRecordDAO() {
        return this.daoFactory.getPlayerMoneyConsumeRecordDAO();
    }

    public IForbidDAO getForbidDAO() {
        return this.daoFactory.getForbidDAO();
    }
    
    public IClubInfoDAO getClubInfoDAO() {
        return this.daoFactory.getClubInfoDAO();
    }

    public IClubMemberDAO getClubMemberDAO() {
        return this.daoFactory.getClubMemberDAO();
    }
    
    public IClubActivityDAO getClubActivityDAO() {
        return this.daoFactory.getClubActivityDAO();
    }
    public IBoxDAO getBoxDAO() {
        return this.daoFactory.getBoxDao();
    }

    public IFloorDAO getFloorDAO() {
        return this.daoFactory.getFloorDao();
    }

    public IClubMemberExtDAO getClubMemberExtDAO() {
        return this.daoFactory.getClubMemberExtDAO();
    }
    
    public IClubActivityGoldRewardRecordDAO getClubActivityGoldRewardRecordDAO() {
        return this.daoFactory.getClubActivityGoldRewardRecordDAO();
    }

    public IClubUidDAO getClubUidDao() {
        return this.daoFactory.getClubUidDAO();
    }

    public IUniqueCodeDAO getUniqueCodeDao() {
        return this.daoFactory.getUniqueCodeDao();
    }

    public IUpDownGoldOrderDAO getUpDownGoldOrderDao() {
        return this.daoFactory.getUpDownGoldOrderDao();
    }

    public IDownLineGameRecordDAO getDownLineGameRecordDAO() {
        return this.daoFactory.getDownLineGameRecordDAO();

    }
    public IBoxArenaScoreDao getBoxArenaScoreDao() {
        return this.daoFactory.getBoxArenaScoreDao();
    }

    public IBoxArenaScoreInfoDao getBoxArenaScoreInfoDao() {
        return this.daoFactory.getBoxArenaScoreInfoDao();
    }
    public IBoxArenaScoreInfoPlayerIdDao getBoxArenaScoreInfoPlayerIdDao() {
        return this.daoFactory.getBoxArenaScoreInfoPlayerIdDao();
    }
    
    public IHundredRebRecordDAO getHundredRebRecordDao() {
        return this.daoFactory.getHundredRebRecordDAO();
    }

    /**********lcadd**********/
    public INoticeDAO getNoticeDao() {
        return this.daoFactory.getNoticeDAO();
    }
    
    public void save(Task task) {
        if (this.saveThread.isStart()) {
            this.saveThread.add(task);
        } else {
            task.run();
        }
    }

    public boolean setExpire(BaseTable table, String redisKey,long expire, TimeUnit timeUnit){
        try {
            RBucket<BaseTable> value = this.redissonClient.getBucket(redisKey);
            if (null != value) {
                value.set(table, expire, timeUnit);
                return true;
            }
        } catch (Throwable t) {
            Logs.DB.error("set: %s key:%s expire redis error", t, table, redisKey);
        }
        return false;
    }

    public <T extends BaseTable> List<T> loadBatch(ETableType type, IDBLoad<T> func) {
        List<T> value = null;
        RBucket<List<T>> bValue = null;
        if (type.isCache()) {
            String redisKey = func.getRedisKey();
            bValue = this.redissonClient.getBucket(redisKey);
            value = bValue.get();
        }

        IBaseDAO<T> dao = this.getDaoByTableType(type);
        if (null == value && null != dao) {
            value = func.load(dao);
            if (null != value && type.isCache()) {
                if (type.getExpire() > 0) {
                    bValue.set(value, type.getExpire(), type.getTimeUnit());
                } else {
                    bValue.set(value);
                }
            }
        }
        return value;
    }

    public <T extends BaseTable> T load(ETableType type, IDBLoad<T> func) {
        T value = null;
        RBucket<T> bValue = null;
        if (type.isCache()) {
            String redisKey = func.getRedisKey();
            bValue = this.redissonClient.getBucket(redisKey);
            value = bValue.get();
        }

        IBaseDAO<T> dao = this.getDaoByTableType(type);
        if (null == value && null != dao) {
            value = func.loadOne(dao);
            if (null != value && type.isCache()) {
                if (type.getExpire() > 0) {
                    bValue.set(value, type.getExpire(), type.getTimeUnit());
                } else {
                    bValue.set(value);
                }
            }
        }
        return value;
    }

    public <T extends BaseTable> T load(long key, ETableType type) {
        T value = null;
        RBucket<T> bValue = null;
        if (type.isCache()) {
            String redisKey = type.getRedisKey() + key;
            try {
                bValue = this.redissonClient.getBucket(redisKey);
                value = bValue.get();
            } catch (Throwable t) {
                Logs.DB.error("load %s redis error", t, key);
            }
        }

        try {
            IBaseDAO<T> dao = this.getDaoByTableType(type);
            if (null == value && null != dao) {
                value = dao.load(key);
                if (null != value && null != bValue && type.isCache()) {
                    if (type.getExpire() > 0) {
                        bValue.set(value, type.getExpire(), type.getTimeUnit());
                    } else {
                        bValue.set(value);
                    }
                }
            }
        } catch (Throwable t) {
            Logs.DB.error("load %s db or redis error", t, key);
        }

        return value;
    }

    public boolean update(BaseTable table) {
        if (!table.isDirty()) {
            return false;
        }
        if (table.getTableType().isCache()) {
            String redisKey = table.getTableType().getRedisKey() + table.getUid();
            try {
                RBucket<BaseTable> value = this.redissonClient.getBucket(redisKey);
                if (table.getTableType().getExpire() > 0) {
                    value.set(table, table.getTableType().getExpire(), table.getTableType().getTimeUnit());
                } else {
                    value.set(table);
                }
            } catch (Throwable t) {
                Logs.DB.error("update: %s key:%s redis error", t, table, redisKey);
            }
        }

        try {
            IBaseDAO<BaseTable> dao = this.getDaoByTableType(table.getTableType());
            if (null != dao) {
                table.setDirty(false);
                if (dao.save(table)) {
                    return true;
                }
                table.setDirty(true);
            }
        } catch (Throwable t) {
            Logs.DB.error("update: %s db error", t, table);
        }

        return false;
    }

    public boolean update(BaseTable table, List<String> redisKey) {
        if (!table.isDirty()) {
            return false;
        }
        if (table.getTableType().isCache()) {
            for (String key : redisKey) {
                try {
                    RBucket<BaseTable> value = this.redissonClient.getBucket(key);
                    if (table.getTableType().getExpire() > 0) {
                        value.set(table, table.getTableType().getExpire(), table.getTableType().getTimeUnit());
                    } else {
                        value.set(table);
                    }
                } catch (Throwable t) {
                    Logs.DB.error("update: %s key:%s redis error", t, table, key);
                }
            }
        }

        try {
            IBaseDAO<BaseTable> dao = this.getDaoByTableType(table.getTableType());
            if (null != dao) {
                table.setDirty(false);
                if (dao.save(table)) {
                    return true;
                }
                table.setDirty(true);
            }
        } catch (Throwable t) {
            Logs.DB.error("update: %s db error", t, table);
        }

        return false;
    }

    protected <T extends BaseTable> IBaseDAO<T> getDaoByTableType(ETableType type) {
        IBaseDAO<T> dao = null;
        switch (type) {
            case TB_PLAYER:
                dao = (IBaseDAO<T>) this.daoFactory.getPlayerDAO();
                break;
            case TB_ACCOUNT:
                dao = (IBaseDAO<T>) this.daoFactory.getAccountDAO();
                break;
            case TB_ROOM_SCORE:
                dao = (IBaseDAO<T>) this.daoFactory.getRoomScoreDAO();
                break;
            case TB_BOX_SCORE:
                dao = (IBaseDAO<T>) this.daoFactory.getBoxRoomScoreDAO();
                break;
            case TB_LOG_ACCOUNT:
                dao = (IBaseDAO<T>) this.daoFactory.getLogAccountDAO();
                break;
            case TB_LOG_ACCOUNT_REMAIN:
                dao = (IBaseDAO<T>) this.daoFactory.getLogAccountRemainDAO();
                break;
            case TB_ACCOUNT_UID:
                dao = (IBaseDAO<T>) this.daoFactory.getAccountUidDAO();
                break;
            case TB_ASSISTANT_WECHAT:
                dao = (IBaseDAO<T>) this.daoFactory.getAssistantWeChatDAO();
                break;
            case TB_LOCATION_INFO:
                dao = (IBaseDAO<T>) this.daoFactory.getLocationInfoDAO();
                break;
            case TB_CLUB_GOLD_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getClubGoldRecordDAO();
                break;
            case TB_CLUB_REWARD_VALUE_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getClubRewardValueRecordDAO();
                break;
            case TB_TODAY_STATISTICS:
                dao = (IBaseDAO<T>) this.daoFactory.getTodayStatisticsDAO();
                break;
            case TB_RANK_DATA:
                dao = (IBaseDAO<T>) this.daoFactory.getRankDataDAO();
                break;
            case TB_MONEY_EXPEND_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getMoneyExpendRecordDao();
                break;
            case TB_PLAYER_MONEY_CONSUME_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getPlayerMoneyConsumeRecordDAO();
                break;
            case TB_FORBID:
                dao = (IBaseDAO<T>) this.daoFactory.getForbidDAO();
                break;
            case TB_CLUB_INFO:
                dao = (IBaseDAO<T>) this.daoFactory.getClubInfoDAO();
                break;
            case TB_CLUE_MEMBER:
                dao = (IBaseDAO<T>) this.daoFactory.getClubMemberDAO();
                break;
            case TB_CLUB_ACTIVITY:
                dao = (IBaseDAO<T>) this.daoFactory.getClubActivityDAO();
                break;
            case TB_BOX:
                dao = (IBaseDAO<T>) this.daoFactory.getBoxDao();
                break;
            case TB_FLOOR:
                dao = (IBaseDAO<T>) this.daoFactory.getFloorDao();
                break;
            case TB_CLUB_MEMBER_EXT:
                dao = (IBaseDAO<T>) this.daoFactory.getClubMemberExtDAO();
                break;
            case TB_BOX_SCORE_PLAYER:
                dao = (IBaseDAO<T>) this.daoFactory.getBoxRoomScorePlayerIdDAO();
                break;
            case TB_MONEY_EXPEND_RECORD_DETAILS:
                dao = (IBaseDAO<T>) this.daoFactory.getMoneyExpendRecordDetailDao();
                break;
            case TB_CLUB_ACTIVITY_GOLD_REWARD_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getClubActivityGoldRewardRecordDAO();
                break;
            case TB_CLUB_UID:
                dao = (IBaseDAO<T>) this.daoFactory.getClubUidDAO();
                break;
            case TB_UNIQUE_CODE:
                dao = (IBaseDAO<T>) this.daoFactory.getUniqueCodeDao();
                break;
            case TB_UPDOWN_GOLD_ORDER:
                dao = (IBaseDAO<T>) this.daoFactory.getUpDownGoldOrderDao();
                break;
            case TB_DOWN_LINE_GAME_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getDownLineGameRecordDAO();
                break;
            case TB_BOX_ARENA_SCORE:
                dao = (IBaseDAO<T>) this.daoFactory.getBoxArenaScoreDao();
                break;
            case TB_BOX_ARENA_SCORE_INFO:
                dao = (IBaseDAO<T>) this.daoFactory.getBoxArenaScoreInfoDao();
                break;
            case TB_BOX_ARENA_SCORE_INFO_PLAYER_ID:
                dao = (IBaseDAO<T>) this.daoFactory.getBoxArenaScoreInfoPlayerIdDao();
                break;
            case TB_HUNDRED_REB_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getHundredRebRecordDAO();
                break;
            case TB_HUNDRED_BUREAU_RECORD:
                dao = (IBaseDAO<T>) this.daoFactory.getHundredBureauRecordDAO();
                break;
                
            /**********lcadd**********/
            case TB_NOTICE:
                dao = (IBaseDAO<T>) this.daoFactory.getNoticeDAO();
                break;
            default:
        }
        return dao;
    }

    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        try {
            this.saveThread.stop();
        } catch (Throwable e) {
            Logs.CORE.error(e);
        }
        return 0;
    }

    private static class DBSaveThread extends ConsumeThread<Task> {
        public DBSaveThread() {
            super("DBSaveThread", false);
        }

        @Override
        protected void exec(Task task) {
            task.run();
        }
    }
}
