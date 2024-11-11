package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.room.normal.RoomInfo;

public interface RoomDAO {
    boolean create(RoomInfo room);
    boolean save(RoomInfo room);
}
