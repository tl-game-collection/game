package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfFGFOperatorInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.IDiscard;
import com.xiuxiu.app.server.room.normal.poker.fgf.IFGFRoom;

public class FgfFAction extends BasePokerAction {
    protected boolean finish = false;
    protected int curOpIndex = -1;

    public FgfFAction(IFGFRoom room, long timeout) {
        super(room, EActionOp.PASS, null, timeout);
    }

    public void setCurOpIndex(int curOpIndex) {
        this.curOpIndex = curOpIndex;
    }

    public int getCurOpIndex() {
        return this.curOpIndex;
    }

    public void next(int curOpIndex) {
        this.curOpIndex = curOpIndex;
        this.resetTimeout(this.timeout);
    }

    public void reset() {
        this.resetTimeout(this.timeout);
    }

    public void finish() {
        this.finish = true;
    }

    public int getRemainTime() {
        return  (int) (this.timeout - (System.currentTimeMillis() - this.startTime + this.useTime));
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            ((IDiscard) this.room).discard(this.room.getRoomPlayer(this.curOpIndex));
        }
        if (this.finish) {
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        //this.room.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_OPERATE, new PCLIPokerNtfFGFOperatorInfo(this.room.getRoomPlayer(this.curOpIndex).getUid()));
    }

    @Override
    public void online(IRoomPlayer player) {
        //if (player.getIndex() == this.curOpIndex) {
            //this.room.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_OPERATE, new PCLIPokerNtfFGFOperatorInfo(player.getUid(),getRemainTime()));
            player.send(CommandId.CLI_NTF_POKER_FGF_OPERATE,new PCLIPokerNtfFGFOperatorInfo(this.room.getRoomPlayer(this.curOpIndex).getUid(),getRemainTime()));
        //}
    }
}
