package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.RoomScoreDAO;
import com.xiuxiu.app.server.db.dao.RoomScoreMapper;
import com.xiuxiu.app.server.score.RoomScore;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class RoomScoreDAOImpl implements RoomScoreDAO {
    private final SqlSessionFactory factory;

    public RoomScoreDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean create(RoomScore roomScore) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomScoreMapper mapper = session.getMapper(RoomScoreMapper.class);
            return 1 == mapper.create(roomScore);
        } catch (Exception e) {
            Logs.DB.error("创建房间战绩信息失败 roomScore:%s", e, roomScore);
        } finally {

        }
        return false;
    }

    @Override
    public RoomScore load(long uid) {
        return null;
    }

    @Override
    public boolean save(RoomScore roomScore) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomScoreMapper mapper = session.getMapper(RoomScoreMapper.class);
            if (1 != mapper.save(roomScore)) {
                return 1 == mapper.create(roomScore);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存房间战绩信息失败 roomScore:%s", e, roomScore);
        } finally {

        }
        return false;
    }

    @Override
    public List<RoomScore> loadByPlayerUid(long playerUid, long beginTime, int page, int pageSize,int gameType,int gameSubType) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomScoreMapper mapper = session.getMapper(RoomScoreMapper.class);
            return mapper.loadByPlayerUid(playerUid, beginTime, page * pageSize, pageSize,gameType,gameSubType);
        } catch (Exception e) {
            Logs.DB.error("根据玩家Uid分页加载房间战绩信息失败 playerUid:%d, page:%d, pageSize:%d", e, playerUid, page, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<RoomScore> loadByPlayerUid(long playerUid, long beginTime, long endTime, int page, int pageSize,int gameType,int gameSubType) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomScoreMapper mapper = session.getMapper(RoomScoreMapper.class);
            return mapper.loadByPlayerUidWithTimeRange(playerUid, beginTime, endTime, page * pageSize, pageSize,gameType,gameSubType);
        } catch (Exception e) {
            Logs.DB.error("根据玩家Uid分页加载房间战绩信息失败 playerUid:%d, page:%d, pageSize:%d", e, playerUid, page, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<RoomScore> loadByGroupUidWithPlayerUid(long groupUid, long playerUid, long beginTime, int page, int pageSize,int gameType,int gameSubType) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomScoreMapper mapper = session.getMapper(RoomScoreMapper.class);
            return mapper.loadByGroupUidWithPlayerUid(groupUid, playerUid, beginTime, page * pageSize, pageSize,gameType,gameSubType);
        } catch (Exception e) {
            Logs.DB.error("根据群组Uid和玩家Uid分页加载房间战绩信息失败 groupUid%d, playerUid:%d, page:%d, pageSize:%d", e, groupUid, playerUid, page, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<RoomScore> loadByGroupUidWithPlayerUid(long groupUid, long playerUid, long beginTime, long endTime, int page, int pageSize,int gameType,int gameSubType) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomScoreMapper mapper = session.getMapper(RoomScoreMapper.class);
            return mapper.loadByGroupUidWithPlayerUidAndTimeRange(groupUid, playerUid, beginTime, endTime, page * pageSize, pageSize,gameType,gameSubType);
        } catch (Exception e) {
            Logs.DB.error("根据群组Uid和玩家Uid分页加载房间战绩信息失败 groupUid%d, playerUid:%d, page:%d, pageSize:%d", e, groupUid, playerUid, page, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
}
