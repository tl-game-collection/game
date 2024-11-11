package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;

public abstract class BasePokerAction extends BaseAction {
    protected final PokerPlayer player;

    public BasePokerAction(IPokerRoom room, EActionOp op, PokerPlayer player, long timeout) {
        super(room, op, timeout);
        this.player = player;
    }

    public PokerPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void online(IRoomPlayer player) {
    }

    @Override
    public void offline(IRoomPlayer player) {
    }

    @Override
    protected void operationTimeout() {
        if (null != this.player) {
            this.player.operationTimeout();
        }
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
        this.sourceTimeout = timeout;
    }

    public void resetTimeout(long timeout) {
        this.timeout = timeout;
        this.sourceTimeout = timeout;
        this.startTime = System.currentTimeMillis();
        this.useTime = 0;
    }
}
