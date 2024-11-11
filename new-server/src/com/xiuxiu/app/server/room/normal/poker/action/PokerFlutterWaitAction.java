package com.xiuxiu.app.server.room.normal.poker.action;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfSelectFlutterInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfSelectPiaoValueInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.IPokerXuanPiao;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.player.IPokerXuanPiaoPlayer;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;
import com.xiuxiu.app.server.room.record.mahjong.FlutterRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

public class PokerFlutterWaitAction extends BasePokerAction {
    private ConcurrentHashMap<Long, Integer> playerOp = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Long> playerTimeout = new ConcurrentHashMap<>();
    private FlutterRecordAction flutterRecordAction;

    public PokerFlutterWaitAction(IPokerRoom room, PokerPlayer roomPlayer, long timeout) {
        super(room, EActionOp.FLUTTER, roomPlayer, timeout);
        this.flutterRecordAction = ((PokerRecord) room.getRecord()).addFlutterAction();
    }

    public void addPlayer(long uid) {
        this.playerOp.put(uid, -1);
        this.playerTimeout.put(uid, -1 == this.timeout ? -1 : this.startTime + this.timeout);
    }

    public ErrorCode playerSelect(long uid, int value) {
        int v = this.playerOp.get(uid);
        if (-1 != v) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        this.playerTimeout.remove(uid);
        this.playerOp.put(uid, value);
        this.flutterRecordAction.addFlutter(this.room.getRoomPlayer(uid).getUid(), value);
        return ErrorCode.OK;
    }

    public int getFlutter(long uid) {
        return this.playerOp.get(uid);
    }

    @Override
    public boolean action(boolean timeout) {
        if (this.playerTimeout.isEmpty()) {
            ((IPokerXuanPiao) this.room).endXuanPiao();
            return true;
        }
        return false;
    }

    @Override
    public boolean canAction(long curTime) {
        if (!this.active) {
            return false;
        }
        return this.checkTimeout(curTime);
    }

    protected boolean checkTimeout(long curTime) {
        boolean allTimeout = true;
        Iterator<Map.Entry<Long, Long>> it = this.playerTimeout.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Long> entry = it.next();
            if (-1 == entry.getValue()) {
                allTimeout = false;
                continue;
            }
            if (curTime < entry.getValue()) {
                allTimeout = false;
                continue;
            }
            IRoomPlayer temp = this.room.getRoomPlayer(entry.getKey());
            ((IPokerXuanPiaoPlayer)temp).setPiaoScore(0);
            this.playerOp.put(temp.getUid(), 0);
            ((PokerRoom) this.room).setTemporaryProperty(temp.getUid());
            this.flutterRecordAction.addFlutter(temp.getUid(), 0);
            PCLIMahjongNtfSelectPiaoValueInfo valueInfo = new PCLIMahjongNtfSelectPiaoValueInfo();
            valueInfo.playerUid = temp.getUid();
            valueInfo.value = 0;
            this.room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO_VALUE, valueInfo);
            it.remove();
        }
        return allTimeout;
    }

    @Override
    protected void doRecover() {
        PCLIMahjongNtfSelectFlutterInfo info = new PCLIMahjongNtfSelectFlutterInfo();
        Iterator<Map.Entry<Long, Integer>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            info.allFlutter.put(entry.getKey(), entry.getValue());
        }
        this.room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO_INFO, info);
    }

    @Override
    public void online(IRoomPlayer player) {
        if(this.playerTimeout.containsKey(player.getUid())) {
            if (-1 == this.timeout) {
                this.playerTimeout.put(player.getUid(), this.timeout);
            }
        }
        PCLIMahjongNtfSelectFlutterInfo info = new PCLIMahjongNtfSelectFlutterInfo();
        Iterator<Map.Entry<Long, Integer>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            info.allFlutter.put(entry.getKey(), entry.getValue());
        }
        player.send(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO_INFO, info);
    }

    @Override
    public void offline(IRoomPlayer player) {
        if (this.playerTimeout.containsKey(player.getUid())) {
            if (-1 == this.timeout) {
                this.playerTimeout.put(player.getUid(), System.currentTimeMillis() + 10000L);
            }
        }
    }
}
