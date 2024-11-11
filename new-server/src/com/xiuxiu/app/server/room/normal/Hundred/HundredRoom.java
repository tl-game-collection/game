package com.xiuxiu.app.server.room.normal.Hundred;

import java.util.LinkedList;
import java.util.Map;

import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.RoomInfo;

public abstract class HundredRoom extends Room {

    public HundredRoom(RoomInfo info) {
        this(info, ERoomType.BOX);
    }

    public HundredRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public IRoomPlayer getRoomPlayer(long playerUid) {
        IHundredHandle hundredHandle = (IHundredHandle) getRoomHandle();
        return hundredHandle.getRoomPlayer(playerUid);
    }

    @Override
    public void broadcast2Client(int commandId, Object msg) {
        IHundredHandle hundredHandle = (IHundredHandle) getRoomHandle();
        hundredHandle.broadcast(commandId, msg);
    }
    
    @Override
    protected void doShuffle() {

    }

    @Override
    protected void doDeal() {

    }

    @Override
    protected void doStart1() {

    }

    @Override
    protected void doFinish(boolean isNormal, boolean isNewBureau) {
    }

    @Override
    public void syncDeskInfo(IPlayer player) {

    }

    @Override
    protected void doSendGameStart() {

    }

    @Override
    protected void doSendGameOver(boolean next) {

    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }
}
