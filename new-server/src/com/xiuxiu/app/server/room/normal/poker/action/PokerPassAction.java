package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfCanPassInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;

public class PokerPassAction extends BasePokerAction {
    public PokerPassAction(PokerRoom room, PokerPlayer player, long timeout) {
        super(room, EActionOp.PASS, player, timeout);
    }

    @Override
    public boolean action(boolean timeout) {
        ((PokerRoom) this.room).onPass(this.player);
        return true;
    }

    @Override
    protected void doRecover() {
        this.player.send(CommandId.CLI_NTF_POKER_CAN_PASS, new PCLIPokerNtfCanPassInfo(this.player.getUid()));
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.player.getUid() != player.getUid()) {
            return;
        }
        player.send(CommandId.CLI_NTF_POKER_CAN_PASS, new PCLIPokerNtfCanPassInfo(player.getUid()));
    }

    @Override
    public void offline(IRoomPlayer player) {
        if (this.player.getUid() != player.getUid()) {
            return;
        }
    }

}
