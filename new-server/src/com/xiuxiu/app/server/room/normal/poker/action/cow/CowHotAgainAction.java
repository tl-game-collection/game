package com.xiuxiu.app.server.room.normal.poker.action.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowHotAgainInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.app.server.room.normal.poker.paigow.PaiGowHotRoom;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

/**
 * 续锅action
 */
public class CowHotAgainAction extends BasePokerAction {
    protected boolean again = false;
    private boolean isSet = false;

    public CowHotAgainAction(PokerRoom room, long timeout) {
        super(room, EActionOp.HOT_AGAIN, null, timeout);
    }

    public ErrorCode again(boolean again) {
        this.again = again;
        this.isSet = true;
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout || this.isSet) {
            sendHotAgainInfoToClient();
            ((CowHotRoom) this.room).onHotAgainOver(this.again);
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_BEGIN_HOT_AGAIN, null);
    }

    @Override
    public void online(IRoomPlayer player) {
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_BEGIN_HOT_AGAIN, null);
    }

    private void sendHotAgainInfoToClient(){
        PCLIPokerNtfPaiGowHotAgainInfo info = new PCLIPokerNtfPaiGowHotAgainInfo();
        info.again = this.again;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_HOT_AGAIN_INFO, info);
    }
}
