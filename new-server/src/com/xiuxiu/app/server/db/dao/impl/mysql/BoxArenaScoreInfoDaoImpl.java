package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoDao;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoMapper;
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
public class BoxArenaScoreInfoDaoImpl implements IBoxArenaScoreInfoDao {
    private final SqlSessionFactory factory;

    public BoxArenaScoreInfoDaoImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public BoxArenaScoreInfo load(long uid) {
        return null;
    }

    @Override
    public boolean save(BoxArenaScoreInfo value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreInfoMapper mapper = session.getMapper(IBoxArenaScoreInfoMapper.class);
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
    public List<BoxArenaScoreInfo> loadAll(List<Long> uidList) {
        if (null == uidList || uidList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreInfoMapper mapper = session.getMapper(IBoxArenaScoreInfoMapper.class);
            List<BoxArenaScoreInfo> arenaScoreInfoList = mapper.loadAll(uidList);
            if (null == arenaScoreInfoList) {
                return Collections.EMPTY_LIST;
            }
            return arenaScoreInfoList;
        } catch (Exception e) {
            Logs.DB.error("根据战绩uid列表竞技场战绩信息失败 uidList:%s", e, uidList);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxArenaScoreInfo> loadAllByBoxUid(Long playerUid,long boxUid,int page, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreInfoMapper mapper = session.getMapper(IBoxArenaScoreInfoMapper.class);
            List<BoxArenaScoreInfo> arenaScoreInfoList = mapper.loadAllByBoxUid(playerUid, boxUid,page * pageSize, pageSize);
            if (null == arenaScoreInfoList) {
                return Collections.EMPTY_LIST;
            }
            return arenaScoreInfoList;
        } catch (Exception e) {
            Logs.DB.error("根据战绩uid列表竞技场战绩信息失败 uidList:%s", e, boxUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

	@Override
	public List<BoxArenaScoreInfo> loadAllByPlayerUid(Long playerUid, int page, int pageSize) {
		try (SqlSession session = this.factory.openSession(true)) {
            IBoxArenaScoreInfoMapper mapper = session.getMapper(IBoxArenaScoreInfoMapper.class);
            List<BoxArenaScoreInfo> arenaScoreInfoList = mapper.loadAllByPlayerUid(playerUid,page * pageSize, pageSize);
            if (null == arenaScoreInfoList) {
                return Collections.EMPTY_LIST;
            }
            return arenaScoreInfoList;
        } catch (Exception e) {
            Logs.DB.error("根据战绩uid列表竞技场战绩信息失败 uidList:%s", e, playerUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
	}
}
