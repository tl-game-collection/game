package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.sg.SGRoom;

import java.util.HashMap;
import java.util.Map;

public class SGTakeAction extends BasePokerAction {

    protected HashMap<Long, Boolean> lookPlayers = new HashMap<>();

    public SGTakeAction(PokerRoom room, long timeout) {
        super(room, EActionOp.ROB_BANKER, null, timeout);
    }

    public void addLookPlayer(long playerUid,boolean isLook){
        lookPlayers.put(playerUid,isLook);
    }

    public boolean getLookPlayer(long playerUid){
        return lookPlayers.getOrDefault(playerUid,false);
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            ((SGRoom) this.room).onOver();
            return true;
        }

        for(Map.Entry<Long,Boolean> entry :this.lookPlayers.entrySet()){
            if(!entry.getValue()){
                return false;
            }
        }

        ((SGRoom) this.room).onOver();
        return true;
    }

    @Override
    protected void doRecover() {

    }
}
