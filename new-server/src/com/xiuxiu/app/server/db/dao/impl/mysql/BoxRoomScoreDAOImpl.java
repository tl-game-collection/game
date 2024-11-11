package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.BoxRoomScoreDAO;
import com.xiuxiu.app.server.db.dao.BoxRoomScoreMapper;
import com.xiuxiu.app.server.score.BoxRoomScore;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class BoxRoomScoreDAOImpl implements BoxRoomScoreDAO {
    private final SqlSessionFactory factory;

    public BoxRoomScoreDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean create(BoxRoomScore roomScore) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            return 1 == mapper.create(roomScore);
        } catch (Exception e) {
            Logs.DB.error("创建包厢房间战绩信息失败 roomScore:%s", e, roomScore);
        } finally {

        }
        return false;
    }

    @Override
    public BoxRoomScore load(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            return mapper.loadByUid(uid);
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 uid:%d", e, uid);
        } finally {

        }
        return null;
    }

    @Override
    public boolean save(BoxRoomScore roomScore) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            if (1 != mapper.save(roomScore)) {
                return 1 == mapper.create(roomScore);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存包厢房间战绩信息失败 roomScore:%s", e, roomScore);
        } finally {

        }
        return false;
    }

    @Override
    public List<BoxRoomScore> loadByGroupUid(long groupUid, long playerUid, long beginTime, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            if (-1 == groupUid) {
                if (-1 == playerUid) {
                    return Collections.EMPTY_LIST;
                }
                return mapper.loadByPlayerUid(playerUid, beginTime, begin, pageSize);
            } else {
                if (-1 == playerUid) {
                    return mapper.loadByGroupUid(groupUid, beginTime, begin, pageSize);
                }
                return mapper.loadByGroupUidAndPlayerUid(groupUid, playerUid, beginTime, begin, pageSize);
            }
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, begin, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxRoomScore> loadByGroupUid(long groupUid, long playerUid, long beginTime, long endTime, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            if (-1 == groupUid) {
                if (-1 == playerUid) {
                    return Collections.EMPTY_LIST;
                }
                return mapper.loadByPlayerUidWithTimeRange(playerUid, beginTime, endTime, begin, pageSize);
            } else {
                if (-1 == playerUid) {
                    return mapper.loadByGroupUidWithTimeRange(groupUid, beginTime, endTime, begin, pageSize);
                }
                return mapper.loadByGroupUidAndPlayerUidWithTimeRange(groupUid, playerUid, beginTime, endTime, begin, pageSize);
            }
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, begin, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxRoomScore> loadByGroupUidAndRoomId(long groupUid, long playerUid, long beginTime, int begin,
            int pageSize, int roomId) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            if (-1 == groupUid) {
                if (-1 == playerUid) {
                    return Collections.EMPTY_LIST;
                }
                return mapper.loadByPlayerUid(playerUid, beginTime, begin, pageSize);
            } else {
                if (-1 == playerUid) {
                    return mapper.loadByGroupUid(groupUid, beginTime, begin, pageSize);
                }
                return mapper.loadByClubUidAndPlayerUid(groupUid, playerUid, beginTime, begin, pageSize, roomId);
            }
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, begin, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxRoomScore> loadByGroupUidAndRoomId(long groupUid, long playerUid, long beginTime, long endTime,
            int begin, int pageSize, int roomId) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            if (-1 == groupUid) {
                if (-1 == playerUid) {
                    return Collections.EMPTY_LIST;
                }
                return mapper.loadByPlayerUidWithTimeRange(playerUid, beginTime, endTime, begin, pageSize);
            } else {
                if (-1 == playerUid) {
                    return mapper.loadByGroupUidWithTimeRange(groupUid, beginTime, endTime, begin, pageSize);
                }
                return mapper.loadByClubUidAndPlayerUidWithTimeRange(groupUid, playerUid, beginTime, endTime, begin, pageSize, roomId);
            }
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, begin, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxRoomScore> loadByRoomAndGameType(long groupUid, long beginTime, long endTime,
            int page, int pageSize, int roomId, int gameType, int gameSubType, long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            if (-1 == groupUid) {
                return Collections.EMPTY_LIST;
            } else {
                return mapper.loadByRoomAndGameTypeWithEndTime(groupUid, beginTime, endTime, page, pageSize, roomId, gameType, gameSubType, playerUid);
            }
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, page, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxRoomScore> loadByRoomAndGameType(long groupUid, long beginTime, int page, int pageSize, int roomId,
            int gameType, int gameSubType, long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            if (-1 == groupUid) {
                return Collections.EMPTY_LIST;
            } else {
                return mapper.loadByRoomAndGameType(groupUid, beginTime, page, pageSize, roomId, gameType, gameSubType, playerUid);
            }
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, page, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxRoomScore> loadByGameType(long groupUid, long beginTime, int page, int pageSize, int gameType,
            int gameSubType, long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            return mapper.loadByGameType(groupUid, beginTime, page, pageSize, gameType, gameSubType, playerUid);
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, pageSize, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BoxRoomScore> loadByGameType(long groupUid, long beginTime, long endTime, int page, int pageSize,
            int gameType, int gameSubType, long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            BoxRoomScoreMapper mapper = session.getMapper(BoxRoomScoreMapper.class);
            return mapper.loadByGameTypeWithEndTime(groupUid, beginTime, endTime, page, pageSize, gameType, gameSubType, playerUid);
        } catch (Exception e) {
            Logs.DB.error("根据群Uid和玩家Uid分页加载房间战绩信息失败 groupUid:%d, begin:%d, pageSize:%d", e, groupUid, pageSize, pageSize);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
}
