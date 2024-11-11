package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.ITodayStatisticsDAO;
import com.xiuxiu.app.server.db.dao.ITodayStatisticsMapper;
import com.xiuxiu.app.server.statistics.TodayStatistics;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class TodayStatisticsDAOImpl implements ITodayStatisticsDAO {
    private SqlSessionFactory factory;

    public TodayStatisticsDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TodayStatistics load(long uid) {
        return null;
    }

    @Override
    public boolean save(TodayStatistics value) {
        try (SqlSession session = this.factory.openSession(true)) {
            ITodayStatisticsMapper mapper = session.getMapper(ITodayStatisticsMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics:%s", e,value);
        } finally {

        }
        return false;
    }

    @Override
    public List<TodayStatistics> loadByFromUid(long fromUid, int statisticsType) {
        try (SqlSession session = this.factory.openSession(true)) {
            ITodayStatisticsMapper mapper = session.getMapper(ITodayStatisticsMapper.class);
            List<TodayStatistics> list = mapper.loadByFromUid(fromUid,statisticsType);
            if (null == list) {
                return Collections.EMPTY_LIST;
            }
            return list;
        } catch (Exception e) {
            Logs.DB.error("加载当天统计数据失败 TodayStatistics %d", e,fromUid);
        } finally {

        }
        return null;
    }
}
