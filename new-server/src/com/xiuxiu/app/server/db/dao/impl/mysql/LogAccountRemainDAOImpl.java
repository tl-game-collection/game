package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.ILogAccountRemainDAO;
import com.xiuxiu.app.server.db.dao.ILogAccountRemainMapper;
import com.xiuxiu.app.server.statistics.LogAccountRemain;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class LogAccountRemainDAOImpl implements ILogAccountRemainDAO {
    private final SqlSessionFactory factory;

    public LogAccountRemainDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public List<LogAccountRemain> load(long timeBegin, long timeEnd) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountRemainMapper mapper = session.getMapper(ILogAccountRemainMapper.class);
            return mapper.load(timeBegin, timeEnd);
        } catch (Exception e) {
            Logs.DB.error("加载LogAccountRemain失败", e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean save(LogAccountRemain log) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILogAccountRemainMapper mapper = session.getMapper(ILogAccountRemainMapper.class);
            mapper.create(log);
            return true;
        } catch (Exception e) {
            Logs.DB.error("创建LogAccountRemain失败", e);
        }
        return false;
    }

    @Override
    public LogAccountRemain load(long uid) {
        return null;
    }
}
