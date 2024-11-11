package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IMoneyExpendRecordDao;
import com.xiuxiu.app.server.db.dao.IMoneyExpendRecordMapper;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendEveryDayRecord;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 *
 */
public class MoneyExpendRecordDaoImpl implements IMoneyExpendRecordDao {
    private SqlSessionFactory factory;

    public MoneyExpendRecordDaoImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public MoneyExpendRecord load(long uid) {
        return null;
    }

    @Override
    public boolean save(MoneyExpendRecord value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public int loadMoneyExpendRecord(int startExpendType, int endExpendType, long startTime, long endTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            Integer value = mapper.loadMoneyExpendRecord(startExpendType, endExpendType, startTime, endTime);
            return null == value ? 0 : value;
        } catch (Exception e) {
            Logs.DB.error("获取房卡消耗失败 ", e);
        } finally {

        }
        return 0;
    }

    @Override
    public int loadMoneyExpendRecordByFromUid(long fromUid, int startExpendType, int endExpendType, long startTime, long endTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            Integer value = mapper.loadMoneyExpendRecordByFromUid(fromUid, startExpendType, endExpendType, startTime, endTime);
            return null == value ? 0 : value;
        } catch (Exception e) {
            Logs.DB.error("获取房卡消耗失败 ", e);
        } finally {

        }
        return 0;
    }

    @Override
    public List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecord(long playerUid, int roomType, int beginPag, int endPag) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            return mapper.loadMoneyExpendEveryDayRecord(playerUid, roomType, beginPag, endPag);
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:", e);
        } finally {

        }
        return null;
    }

    @Override
    public List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecord2(long startTime, long endTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            return mapper.loadMoneyExpendEveryDayRecord2(startTime, endTime);
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:", e);
        } finally {

        }
        return null;
    }

    @Override
    public List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByPlayerUid(long playerUid, int beginPag, int endPag) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            return mapper.loadMoneyExpendEveryDayRecordByPlayerUid(playerUid, beginPag, endPag);
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:", e);
        } finally {

        }
        return null;
    }

    @Override
    public List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByClubUid(long fromUid, int beginPag, int endPag) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            return mapper.loadMoneyExpendEveryDayRecordByClubUid(fromUid, beginPag, endPag);
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:", e);
        } finally {

        }
        return null;
    }

    @Override
    public List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByClubUid2(long fromUid, long startTime, long endTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            return mapper.loadMoneyExpendEveryDayRecordByClubUid2(fromUid, startTime, endTime);
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:", e);
        } finally {

        }
        return null;
    }

    @Override
    public List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByGameType(int gameType, long startTime, long endTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            return mapper.loadMoneyExpendEveryDayRecordByGameType(gameType, startTime, endTime);
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:", e);
        } finally {

        }
        return null;
    }

    @Override
    public List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByGameTypeAndClubUid(int gameType, long clubUid, long startTime, long endTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordMapper mapper = session.getMapper(IMoneyExpendRecordMapper.class);
            return mapper.loadMoneyExpendEveryDayRecordByGameTypeAndClubUid(gameType, clubUid, startTime, endTime);
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:", e);
        } finally {

        }
        return null;
    }
}
