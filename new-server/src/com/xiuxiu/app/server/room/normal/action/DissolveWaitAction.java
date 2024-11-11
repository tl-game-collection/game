package com.xiuxiu.app.server.room.normal.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfDissolveAgreeInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfDissolveRejectInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfDissolveWaitInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DissolveWaitAction extends BaseAction {
    private HashMap<Long, Integer> playerOp = new HashMap<>();

    public DissolveWaitAction(IRoom room, long timeout) {
        super(room, EActionOp.DISSOLVE_WAIT, timeout);
    }

    public void addPlayer(long uid) {
        this.playerOp.putIfAbsent(uid, 0);
    }

    public void addPlayerDissolve(long uid) {
        this.playerOp.putIfAbsent(uid, 1);
    }

    public ErrorCode playerSelect(long uid, EActionOp op) {
        Integer value = this.playerOp.get(uid);
        if (null == value) {
            Logs.ROOM.warn("%s %d 无效操作, 不在房间里", this.room, uid);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (0 != value) {
            Logs.ROOM.warn("%s %d 无效操作, 已经选择了 op:%d", this.room, uid, value);
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        if (EActionOp.DISSOLVE_AGREE == op) {
            this.playerOp.put(uid, 1);
            PCLIRoomNtfDissolveAgreeInfo info = new PCLIRoomNtfDissolveAgreeInfo();
            info.roomId = this.room.getRoomId();
            info.opPlayerUid = uid;
            this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_DISSOLVE_AGREE, info);
        } else if (EActionOp.DISSOLVE_REJECT == op) {
            this.playerOp.put(uid, 2);
            PCLIRoomNtfDissolveRejectInfo info = new PCLIRoomNtfDissolveRejectInfo();
            info.roomId = this.room.getRoomId();
            info.rejectPlayerUid = uid;
            this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_DISSOLVE_REJECT, info);
        } else {
            Logs.ROOM.warn("%s %d 无效操作 op:%s", this.room, uid, op);
            return ErrorCode.REQUEST_INVALID;
        }
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        boolean dissolve = true;
        Iterator<Map.Entry<Long, Integer>> it = this.playerOp.entrySet().iterator();
        boolean reject = false;
        boolean wait = false;
        while (it.hasNext()) {
            int op = it.next().getValue();
            if (2 == op) {
                dissolve = false;
                reject = true;
            } else if (0 == op && !timeout) {
                wait = true;
            }
        }
        if (!wait && dissolve) {
            this.room.destroy();
        }
        return reject ? true : (wait ? false : true);
    }

    @Override
    protected void doRecover() {
        PCLIRoomNtfDissolveWaitInfo waitInfo = new PCLIRoomNtfDissolveWaitInfo();
        waitInfo.remain = (int) (this.timeout - (System.currentTimeMillis() - this.startTime + this.useTime)) / 1000;
        waitInfo.roomId = this.room.getRoomId();
        Iterator<Map.Entry<Long, Integer>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            int op = entry.getValue();
            if (1 == op) {
                waitInfo.agreePlayerUid.add(entry.getKey());
            }
        }
        this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_DISSOLVE_WAIT_INFO, waitInfo);
    }

    @Override
    public void online(IRoomPlayer player) {
        if (!player.isGuest()) {
            PCLIRoomNtfDissolveWaitInfo waitInfo = new PCLIRoomNtfDissolveWaitInfo();
            waitInfo.remain = (int) (this.timeout - (System.currentTimeMillis() - this.startTime + this.useTime)) / 1000;
            waitInfo.roomId = this.room.getRoomId();
            Iterator<Map.Entry<Long, Integer>> it = this.playerOp.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, Integer> entry = it.next();
                int op = entry.getValue();
                if (1 == op) {
                    waitInfo.agreePlayerUid.add(entry.getKey());
                }
            }
            player.send(CommandId.CLI_NTF_ROOM_DISSOLVE_WAIT_INFO, waitInfo);
        }
    }

    @Override
    public void offline(IRoomPlayer player) {

    }

    @Override
    protected void operationTimeout() {
    }
}
