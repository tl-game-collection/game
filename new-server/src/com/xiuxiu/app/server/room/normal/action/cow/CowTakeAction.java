package com.xiuxiu.app.server.room.normal.action.cow;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.ICowRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 9:46
 * @comment:
 */
public class CowTakeAction extends BasePokerAction {
    private ConcurrentHashMap<Long, Boolean> lookPlayers = new ConcurrentHashMap<>();

    public CowTakeAction(PokerRoom room, long timeout) {
        super(room, EActionOp.ROB_BANKER, null, timeout);
    }

    public void addLookPlayer(long playerUid, boolean isLook) {
        lookPlayers.put(playerUid, isLook);
    }

    public boolean getLookPlayer(long playerUid) {
        return lookPlayers.getOrDefault(playerUid, false);
    }

    @Override
    protected void doRecover() {

    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            ((ICowRoom) this.room).onOver();
            return true;
        }
        for (Map.Entry<Long, Boolean> entry : this.lookPlayers.entrySet()) {
            if (!entry.getValue()) {
                return false;
            }
        }
        ((ICowRoom) this.room).onOver();
        return true;
    }
}
