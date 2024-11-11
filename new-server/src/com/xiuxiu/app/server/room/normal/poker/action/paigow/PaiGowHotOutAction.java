package com.xiuxiu.app.server.room.normal.poker.action.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowHotOutInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.paigow.PaiGowHotRoom;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

public class PaiGowHotOutAction extends BasePokerAction {
    private boolean out;
    private boolean five;

    public PaiGowHotOutAction(PokerRoom room, long timeout) {
        super(room, EActionOp.READY, null, timeout);
    }

    public void setFive(boolean five) {
        this.five = false;
    }

    public ErrorCode out(boolean out) {
        this.out = out;
        PCLIPokerNtfPaiGowHotOutInfo info = new PCLIPokerNtfPaiGowHotOutInfo();
        info.out = out;
        info.five = this.five;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_OUT_INFO, info);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            PCLIPokerNtfPaiGowHotOutInfo info = new PCLIPokerNtfPaiGowHotOutInfo();
            info.out = this.out;
            info.five = this.five;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_OUT_INFO, info);
        }
        ((PokerRecord) this.room.getRecord()).addPaiGowHotOutRecordAction(this.room.getRoomPlayer(this.room.getBankerIndex()).getUid(), this.out, this.five);
        ((PaiGowHotRoom) this.room).onHotOutOver(this.out);
        return true;
    }

    @Override
    protected void doRecover() {
        PCLIPokerNtfPaiGowHotOutInfo info = new PCLIPokerNtfPaiGowHotOutInfo();
        info.out = out;
        info.five = this.five;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_OUT, info);
    }

    @Override
    public void online(IRoomPlayer player) {
        PCLIPokerNtfPaiGowHotOutInfo info = new PCLIPokerNtfPaiGowHotOutInfo();
        info.out = out;
        info.five = this.five;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_OUT, info);
    }
}
