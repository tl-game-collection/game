package com.xiuxiu.app.server.db.dao.impl.mysql;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IBoxRoomScorePlayerIdDAO;
import com.xiuxiu.app.server.db.dao.IBoxRoomScorePlayerIdMapper;
import com.xiuxiu.app.server.score.BoxRoomScorePlayerId;

public class BoxRoomScorePlayerIdDAOImpl implements IBoxRoomScorePlayerIdDAO {
    private final SqlSessionFactory factory;

    public BoxRoomScorePlayerIdDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean save(BoxRoomScorePlayerId value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxRoomScorePlayerIdMapper mapper = session.getMapper(IBoxRoomScorePlayerIdMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存信息失败 league:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public BoxRoomScorePlayerId load(long uid) {
        return null;
    }

}
