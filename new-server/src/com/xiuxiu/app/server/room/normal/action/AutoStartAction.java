package com.xiuxiu.app.server.room.normal.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginBeforeInfo;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public class AutoStartAction extends BaseAction {
    public AutoStartAction(IRoom room, long timeout) {
        super(room, EActionOp.AUTO_START, timeout);
    }

    @Override
    public boolean action(boolean timeout) {
        this.room.start();
        return true;
    }

    @Override
    protected void doRecover() {
        PCLIRoomNtfBeginBeforeInfo beginBeforeInfo = new PCLIRoomNtfBeginBeforeInfo();
        beginBeforeInfo.beginRemain = (int) ((this.timeout - (System.currentTimeMillis() - this.startTime + this.useTime)) / 1000);
        this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_BEGIN_BEFORE, beginBeforeInfo);
    }

    @Override
    public void online(IRoomPlayer player) {
        PCLIRoomNtfBeginBeforeInfo beginBeforeInfo = new PCLIRoomNtfBeginBeforeInfo();
        beginBeforeInfo.beginRemain = (int) ((this.timeout - (System.currentTimeMillis() - this.startTime + this.useTime)) / 1000);
        player.send(CommandId.CLI_NTF_ROOM_BEGIN_BEFORE, beginBeforeInfo);
    }

    @Override
    public void offline(IRoomPlayer player) {
    }

    @Override
    protected void operationTimeout() {
    }
}
