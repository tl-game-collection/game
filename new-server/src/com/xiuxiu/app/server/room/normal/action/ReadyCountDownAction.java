package com.xiuxiu.app.server.room.normal.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfDissolveCountDown;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther: yuyunfei
 * @date: 2019/12/30 11:32
 * @comment:
 */
public class ReadyCountDownAction extends BaseAction {
    private HashMap<Long, Integer> readyPlayer = new HashMap<>();

    public ReadyCountDownAction(IRoom room, long timeout) {
        super(room, EActionOp.TIMEOUT_DISSOLVE, timeout);
    }

    public void addReadyPlayer(long playerUid) {
        this.readyPlayer.putIfAbsent(playerUid, -1);
    }

    @Override
    protected void doRecover() {

    }

    @Override
    protected void operationTimeout() {

    }

    @Override
    public void online(IRoomPlayer player) {
        PCLIRoomNtfDissolveCountDown beginBeforeInfo = new PCLIRoomNtfDissolveCountDown();
        beginBeforeInfo.beginRemain = (int) ((this.timeout - (System.currentTimeMillis() - this.startTime + this.useTime)) / 1000);
        player.send(CommandId.CLI_NTF_ROOM_DISSOLVE_COUNT_DOWN, beginBeforeInfo);
    }

    @Override
    public void offline(IRoomPlayer player) {

    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.readyPlayer.entrySet()) {
                if (-1 == entry.getValue()) {
                    IRoomPlayer roomPlayer = this.room.getRoomPlayer(entry.getKey());
                    if (null == roomPlayer || roomPlayer.isGuest()) {
                        continue;
                    }
                    Player player = PlayerManager.I.getPlayer(roomPlayer.getUid());
                    if (null == player) {
                        continue;
                    }
                    if (player.getRoomId() == room.getRoomId()) {
                        room.getRoomHandle().readyHandle(player.getUid(), Boolean.TRUE);
                    } else {
                        System.err.println("ReadyCountDownAction异常，playerId="+player.getUid()+",roomId="+room.getRoomId()+",player.getRoomId()="+player.getRoomId());
                    }
                }
            }
        }
        return true;
    }

    public void selectReadyPlayer(long playerUid, int mul) {
        Integer temp = this.readyPlayer.get(playerUid);
        if (null == temp) {
            Logs.ROOM.warn("%s 不在该房间内", playerUid);
            return;
        }
        if (-1 != temp) {
            Logs.ROOM.warn("%s 已经准备过了 mul:%d", playerUid, temp);
            return;
        }
        this.readyPlayer.put(playerUid, mul);
    }
}
