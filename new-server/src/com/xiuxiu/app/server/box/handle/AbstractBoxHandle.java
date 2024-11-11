package com.xiuxiu.app.server.box.handle;

import java.util.Map;

import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.room.normal.IRoom;

public abstract class AbstractBoxHandle implements IBoxHandle {

    /**
     * 包厢
     */
    protected Box box;
    /**
     * 包厢房间
     */
    protected IRoom room;
    
    public AbstractBoxHandle(Box box, IRoom room) {
        this.box = box;
        this.room = room;
    }
    
    @Override
    public Map<Long, Long> allSitDownPlayer() {
        return null;
    }
}
