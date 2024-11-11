package com.xiuxiu.app.server.box.handle;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public class BoxHandle extends AbstractBoxHandle {

    public BoxHandle(Box box, IRoom room) {
        super(box, room);
    }

    @Override
    public ErrorCode sitDown(IRoomPlayer roomPlayer, int sitIndex) {
        return null;
    }

    @Override
    public ErrorCode sitUp(IRoomPlayer roomPlayer) {
        return null;
    }

    @Override
    public ErrorCode ready(Player player, IRoomPlayer roomPlayer) {
        return this.room.ready(player);
    }

    @Override
    public ErrorCode onJoin(IRoomPlayer roomPlayer) {
        return null;
    }

    @Override
    public void killAll(Box box) {
        
    }

    @Override
    public void level(long playerUid) {
        
    }

    @Override
    public void resetSitUpTime() {
        
    }

    @Override
    public void tick() {
    }

	@Override
	public IRoom getRoom() {
		return this.room;
	}

	@Override
	public ErrorCode getAllWatchPlayer(IRoomPlayer roomPlayer) {
		return null;
	}
}
