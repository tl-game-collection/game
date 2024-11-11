package com.xiuxiu.app.server.room.normal.poker.action.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowHotAgainInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.paigow.PaiGowHotRoom;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

/**
 * 续锅action
 */
public class PaiGowHotAgainAction extends BasePokerAction {
    protected boolean again = false;

    public PaiGowHotAgainAction(PokerRoom room, long timeout) {
        super(room, EActionOp.HOT_AGAIN, null, timeout);
    }

    public ErrorCode again(boolean again) {
        this.again = again;
        PCLIPokerNtfPaiGowHotAgainInfo info = new PCLIPokerNtfPaiGowHotAgainInfo();
        info.again = again;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_INFO, info);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            PCLIPokerNtfPaiGowHotAgainInfo info = new PCLIPokerNtfPaiGowHotAgainInfo();
            info.again = this.again;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_INFO, info);
        }
        ((PokerRecord) this.room.getRecord()).addPaiGowHotAgainRecordAction(this.room.getRoomPlayer(this.room.getBankerIndex()).getUid(), this.again);
        ((PaiGowHotRoom) this.room).onHotAgainOver(this.again);
        return true;
    }

    @Override
    protected void doRecover() {
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_AGAIN, null);
    }

    @Override
    public void online(IRoomPlayer player) {
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_AGAIN, null);
    }
}
