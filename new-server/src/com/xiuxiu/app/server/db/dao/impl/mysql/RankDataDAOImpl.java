package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IRankDataDAO;
import com.xiuxiu.app.server.db.dao.IRankDataMapper;
import com.xiuxiu.app.server.db.dao.ITodayStatisticsMapper;
import com.xiuxiu.app.server.rank.RankData;
import com.xiuxiu.app.server.statistics.TodayStatistics;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class RankDataDAOImpl implements IRankDataDAO {
    private SqlSessionFactory factory;

    public RankDataDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public RankData load(long uid) {
        return null;
    }

    @Override
    public boolean save(RankData value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IRankDataMapper mapper = session.getMapper(IRankDataMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存当天统计数据失败 TodayStatistics", e);
        } finally {

        }
        return false;
    }

    @Override
    public List<RankData> loadAll() {
        try (SqlSession session = this.factory.openSession(true)) {
            IRankDataMapper mapper = session.getMapper(IRankDataMapper.class);
            return mapper.loadAll();
        } catch (Exception e) {
            Logs.DB.error("加载当天统计数据失败 TodayStatistics", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
}
