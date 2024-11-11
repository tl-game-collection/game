package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfThirteenTakeInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.thirteen.ThirteenRoom;

public class ThirteenTakeAction extends BasePokerAction {
    protected boolean finish = false;

    public ThirteenTakeAction(PokerRoom room, long timeout) {
        super(room, EActionOp.TAKE, null, timeout);
    }

    public void finish() {
        this.finish = true;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout || this.finish) {
            ((ThirteenRoom) this.room).onOver();
            return true;
        }
        return false;
    }
    @Override
    protected void doRecover() {
        PCLIPokerNtfThirteenTakeInfo takeInfo = new PCLIPokerNtfThirteenTakeInfo();
        takeInfo.remain = (int) (this.getRemain() / 1000);
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_THIRTEEN_LIPAI_BEGIN, takeInfo);
    }

    @Override
    public void online(IRoomPlayer player) {
        PCLIPokerNtfThirteenTakeInfo takeInfo = new PCLIPokerNtfThirteenTakeInfo();
        takeInfo.remain = (int) (this.getRemain() / 1000);
        player.send(CommandId.CLI_NTF_POKER_THIRTEEN_LIPAI_BEGIN, takeInfo);
    }
}
