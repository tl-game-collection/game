package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.RecommendDAO;
import com.xiuxiu.app.server.db.dao.RecommendMapper;
import com.xiuxiu.app.server.player.Recommend;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class RecommendDAOImpl implements RecommendDAO {
    private SqlSessionFactory factory;

    public RecommendDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean save(Recommend recommend) {
        try (SqlSession session = this.factory.openSession(true)) {
            RecommendMapper mapper = session.getMapper(RecommendMapper.class);
            return 1 == mapper.create(recommend);
        } catch (Exception e) {
            Logs.DB.error("创建推荐信息失败 recommend:%s", e, recommend);
        } finally {

        }
        return false;
    }

    @Override
    public List<Recommend> load(long recommendPlayerUid, int begin, int page) {
        try (SqlSession session = this.factory.openSession(true)) {
            RecommendMapper mapper = session.getMapper(RecommendMapper.class);
            return mapper.loadByRecommendPlayerUid(recommendPlayerUid, begin, page);
        } catch (Exception e) {
            Logs.DB.error("创建推荐信息失败 recommendPlayerUid:%d, begin:%d, page:%d", e, recommendPlayerUid, begin, page);
        } finally {

        }
        return null;
    }

    @Override
    public List<Recommend> load(long recommendPlayerUid, long groupUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            RecommendMapper mapper = session.getMapper(RecommendMapper.class);
            return mapper.loadByRecommendPlayerUidAndGroupUid(recommendPlayerUid, groupUid);
        } catch (Exception e) {
            Logs.DB.error("获取邀请信息失败 recommendPlayerUid:%d, groupUid:%d", e, recommendPlayerUid, groupUid);
        } finally {

        }
        return null;
    }

    @Override
    public List<Recommend> load(long groupUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            RecommendMapper mapper = session.getMapper(RecommendMapper.class);
            return mapper.loadByGroupUid(groupUid);
        } catch (Exception e) {
            Logs.DB.error("获取邀请信息失败 groupUid:%d", e, groupUid);
        } finally {

        }
        return null;
    }

    @Override
    public boolean changeState(long recommendPlayerUid, long groupUid, int oldState, int newState) {
        try (SqlSession session = this.factory.openSession(true)) {
            RecommendMapper mapper = session.getMapper(RecommendMapper.class);
            mapper.changeState(recommendPlayerUid, groupUid, oldState, newState);
            return true;
        } catch (Exception e) {
            Logs.DB.error("更新邀请信息失败 groupUid:%d", e, groupUid);
        }
        return false;
    }

    @Override
    public List<Long> loadByReferrerUid(long rUid, int begin, int page) {
        try (SqlSession session = this.factory.openSession(true)) {
            RecommendMapper mapper = session.getMapper(RecommendMapper.class);
            return mapper.loadByReferrerUid(rUid, begin, page);
        } catch (Exception e) {
            Logs.DB.error("获取邀请信息失败 rUid:%d", e, rUid);
        } finally {

        }
        return null;
    }
}
