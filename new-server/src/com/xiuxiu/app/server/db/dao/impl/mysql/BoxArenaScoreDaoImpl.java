package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreDao;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoMapper;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreMapper;
import com.xiuxiu.app.server.score.BoxArenaScore;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 17:54
 * @comment:
 */
public class BoxArenaScoreDaoImpl implements IBoxArenaScoreDao {
    private final SqlSessionFactory factory;

    public BoxArenaScoreDaoImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public BoxArenaScore load(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreMapper mapper = session.getMapper(IBoxArenaScoreMapper.class);
            return mapper.loadByUid(uid);
        } catch (Exception e) {
            Logs.DB.error("根据包厢竞技场战绩Uid加载竞技场战绩信息失败 arenaScoreUid:%s", e, uid);
        } finally {

        }
        return null;
    }

    @Override
    public boolean save(BoxArenaScore value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreMapper mapper = session.getMapper(IBoxArenaScoreMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存包厢竞技场战绩信息失败 arenaScore:%s", e, value);
        } finally {

        }
        return false;
    }
}
