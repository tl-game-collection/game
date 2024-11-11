package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.RoomDAO;
import com.xiuxiu.app.server.db.dao.RoomMapper;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class RoomDAOImpl implements RoomDAO {
    private final SqlSessionFactory factory;

    public RoomDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean create(RoomInfo room) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomMapper mapper = session.getMapper(RoomMapper.class);
            return 1 == mapper.create(room);
        } catch (Exception e) {
            Logs.DB.error("创建房间信息失败 room:%s", e, room);
        } finally {

        }
        return false;
    }

    @Override
    public boolean save(RoomInfo room) {
        try (SqlSession session = this.factory.openSession(true)) {
            RoomMapper mapper = session.getMapper(RoomMapper.class);
            if (1 != mapper.save(room)) {
                return 1 == mapper.create(room);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存房间信息失败 room:%s", e, room);
        } finally {

        }
        return false;
    }
}
