package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoDao;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoMapper;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoPlayerIdDao;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoPlayerIdMapper;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.app.server.score.BoxArenaScoreInfoPlayerId;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class BoxArenaScoreInfoPlayerIdDaoImpl implements IBoxArenaScoreInfoPlayerIdDao {
    private final SqlSessionFactory factory;

    public BoxArenaScoreInfoPlayerIdDaoImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public BoxArenaScoreInfoPlayerId load(long uid) {
        return null;
    }

    @Override
    public boolean save(BoxArenaScoreInfoPlayerId value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreInfoPlayerIdMapper mapper = session.getMapper(IBoxArenaScoreInfoPlayerIdMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存竞技场战绩信息失败 arenaScoreInfo:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public List<BoxArenaScoreInfoPlayerId> loadAll(Long uid) {
//        if (null == uidList || uidList.isEmpty()) {
//            return Collections.EMPTY_LIST;
//        }
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreInfoPlayerIdMapper mapper = session.getMapper(IBoxArenaScoreInfoPlayerIdMapper.class);
            List<BoxArenaScoreInfoPlayerId> arenaScoreInfoList = mapper.loadAll(uid);
            if (null == arenaScoreInfoList) {
                return Collections.EMPTY_LIST;
            }
            return arenaScoreInfoList;
        } catch (Exception e) {
            Logs.DB.error("根据战绩uid列表竞技场战绩信息失败 uidList:%s", e, uid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
}
