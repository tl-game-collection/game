package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.ILogAccountDAO;
import com.xiuxiu.app.server.db.dao.ILogAccountMapper;
import com.xiuxiu.app.server.statistics.LogAccount;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LogAccountDAOImpl implements ILogAccountDAO {
    private final SqlSessionFactory factory;

    public LogAccountDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void createMultiple(List<LogAccount> logs) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            mapper.createMultiple(logs);
        } catch (Exception e) {
            Logs.DB.error("创建LogAccount失败", e);
        }
    }

    @Override
    public List<LogAccount> load(long targetUid, int action, long timeBegin, long timeEnd, long limitOffset, int limitCount) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            return mapper.load(targetUid, action, timeBegin, timeEnd, limitOffset, limitCount);
        } catch (Exception e) {
            Logs.DB.error("加载LogAccount失败", e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public long count(long targetUid, int action, long timeBegin, long timeEnd) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            return mapper.count(targetUid, action, timeBegin, timeEnd);
        } catch (Exception e) {
            Logs.DB.error("计数LogAccount失败", e);
        }
        return 0;
    }

    @Override
    public List<Long> loadTimeByAction(int action, long timeBegin, long timeEnd) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            return mapper.loadTimeByAction(action, timeBegin, timeEnd);
        } catch (Exception e) {
            Logs.DB.error("加载LogAccount时间失败", e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public LogAccount getLastRecordByAction(int action) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            return mapper.getLastRecordByAction(action);
        } catch (Exception e) {
            Logs.DB.error("加载LogAccount的accountUid最大值失败，action:%d", action);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getDailyActive(long timeBegin, long timeEnd) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            return mapper.getDailyActive(timeBegin, timeEnd);
        } catch (Exception e) {
            Logs.DB.error("加载LogAccount 每日活跃人数失败，开始时间:%d，结束时间:%d", timeBegin, timeEnd);
        }
        return null;
    }

    @Override
    public int loadLoginByTimeAndTargetUids(long timeBegin, long timeEnd, List<Long> targetUidList) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            Integer result = mapper.loadLoginByTimeAndTargetUids(timeBegin, timeEnd, targetUidList);
            if (null != result) {
                return result;
            }
            return 0;
        } catch (Exception e) {
            Logs.DB.error("加载LogAccount 留存人数失败，开始时间:%d，结束时间:%d", timeBegin, timeEnd);
        }
        return 0;
    }

    @Override
    public List<Long> loadYesterdayRemain(long timeBegin, long timeEnd) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            return mapper.loadYesterdayRemain(timeBegin, timeEnd);
        } catch (Exception e) {
            Logs.DB.error("加载LogAccount 昨日留存人数失败，开始时间:%d，结束时间:%d", timeBegin, timeEnd);
        }
        return null;
    }

    @Override
    public int loadRegisterNumByTime(long timeBegin, long timeEnd) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            Integer result = mapper.loadRegisterNumByTime(timeBegin, timeEnd);
            if (null != result) {
                return result;
            }
            return 0;
        } catch (Exception e) {
            Logs.DB.error("加载LogAccount 留存人数失败，开始时间:%d，结束时间:%d", timeBegin, timeEnd);
        }
        return 0;
    }

    @Override
    public boolean save(LogAccount log) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountMapper mapper = session.getMapper(ILogAccountMapper.class);
            mapper.create(log);
            return true;
        } catch (Exception e) {
            Logs.DB.error("创建LogAccount失败", e);
        }
        return false;
    }

    @Override
    public LogAccount load(long uid) {
        return null;
    }
}
