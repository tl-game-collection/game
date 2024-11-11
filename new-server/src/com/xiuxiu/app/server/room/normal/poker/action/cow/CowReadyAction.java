package com.xiuxiu.app.server.room.normal.poker.action.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowReadyInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.app.server.room.normal.poker.paigow.PaiGowHotRoom;
import com.xiuxiu.app.server.room.record.poker.PaiGowReadyRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.cow.CowReadyRecordAction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 牛牛准备action
 */
public class CowReadyAction extends BasePokerAction {
    protected ConcurrentHashMap<Long, Boolean> allReady = new ConcurrentHashMap<>();
    protected CowReadyRecordAction action;
    protected long readyCnt = 0;

    /**
     * 构造函数
     * @param room
     * @param timeout
     */
    public CowReadyAction(PokerRoom room, long timeout) {
        super(room, EActionOp.READY, null, timeout);
        this.action = ((PokerRecord) this.room.getRecord()).addCowReadyRecordAction();
    }

    /**
     * 添加可以准备的玩家
     * @param playerUid
     */
    public void addPlayer(long playerUid) {
        this.allReady.put(playerUid, false);
    }

    /**
     * 执行准备
     * @param playerUid
     * @return
     */
    public ErrorCode ready(long playerUid) {
        Boolean ready = this.allReady.get(playerUid);
        if (null == ready) {
            return ErrorCode.ROOM_NOT_IN;
        }
        if (ready) {
            return ErrorCode.ROOM_ALREADY_READY;
        }
        this.allReady.put(playerUid, true);
        this.action.addReady(playerUid);
        ++this.readyCnt;

        PCLIPokerNtfPaiGowReadyInfo info = new PCLIPokerNtfPaiGowReadyInfo();
        info.playerUid = playerUid;
        info.ready = true;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_READY, info);

        return ErrorCode.OK;
    }

    @Override
    public boolean canAction(long curTime){
        if (this.readyCnt >= this.allReady.size()){
            return true;
        }
        return super.canAction(curTime);
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Boolean> entry : this.allReady.entrySet()) {
                if (entry.getValue()) {
                    continue;
                }
                this.action.addReady(entry.getKey());
                PCLIPokerNtfPaiGowReadyInfo info = new PCLIPokerNtfPaiGowReadyInfo();
                info.playerUid = entry.getKey();
                info.ready = true;
                this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_READY, info);
            }
            this.readyCnt = this.allReady.size();
        }
        if (this.readyCnt == this.allReady.size()) {
            ((CowHotRoom) this.room).onReadyOver();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Boolean> entry : this.allReady.entrySet()) {
            PCLIPokerNtfPaiGowReadyInfo info = new PCLIPokerNtfPaiGowReadyInfo();
            info.playerUid = entry.getKey();
            info.ready = entry.getValue();
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_COW_READY_INFO, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (!this.allReady.containsKey(player.getUid())) {
            return;
        }
        PCLIPokerNtfPaiGowReadyInfo info = new PCLIPokerNtfPaiGowReadyInfo();
        info.playerUid = player.getUid();
        info.ready = this.allReady.getOrDefault(player.getUid(), false);
        player.send(CommandId.CLI_NTF_POKER_COW_READY_INFO, info);
    }
}
