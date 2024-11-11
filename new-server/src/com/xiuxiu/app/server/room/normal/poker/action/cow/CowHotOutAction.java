package com.xiuxiu.app.server.room.normal.poker.action.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowHotOutInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.app.server.room.normal.poker.paigow.PaiGowHotRoom;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

public class CowHotOutAction extends BasePokerAction {
    private boolean out;
    private boolean isSet = false;

    public CowHotOutAction(PokerRoom room, long timeout) {
        super(room, EActionOp.READY, null, timeout);
    }

    public ErrorCode out(boolean out) {
        this.out = out;
        this.isSet = true;
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout || this.isSet) {
            sendHotOutInfoToClient();
            ((CowHotRoom) this.room).onHotOutOver(this.out);
            return true;
        }
        //((PokerRecord) this.room.getRecord()).addPaiGowHotOutRecordAction(this.room.getRoomPlayer(this.room.getBankerIndex()).getUid(), this.out);
        return false;

    }

    @Override
    protected void doRecover() {
        sendHotOutBeginToClient();
    }

    @Override
    public void online(IRoomPlayer player) {
        sendHotOutBeginToClient();
    }

    private void sendHotOutInfoToClient(){
        PCLIPokerNtfPaiGowHotOutInfo info = new PCLIPokerNtfPaiGowHotOutInfo();
        info.out = out;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_HOT_OUT_INFO, info);
    }

    private void sendHotOutBeginToClient(){
        PCLIPokerNtfPaiGowHotOutInfo info = new PCLIPokerNtfPaiGowHotOutInfo();
        info.out = out;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_BEGIN_HOT_OUT, info);
    }
}
