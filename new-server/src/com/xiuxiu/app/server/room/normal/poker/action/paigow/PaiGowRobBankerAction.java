package com.xiuxiu.app.server.room.normal.poker.action.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowAllRobBankerInfo;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowRobBankerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.paigow.IPaiGowRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.record.poker.PaiGowRobBankerRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抢庄action
 */
public class PaiGowRobBankerAction extends BasePokerAction {
    protected ConcurrentHashMap<Long, Integer> robBanker = new ConcurrentHashMap<>();
    protected int selectCnt = 0;

    protected PaiGowRobBankerRecordAction action;

    public PaiGowRobBankerAction(PokerRoom room, long timeout) {
        super(room, EActionOp.ROB_BANKER, null, timeout);
        action = ((PokerRecord) this.room.getRecord()).addPaiGowRobBankerRecordAction();
    }

    /**
     * 添加可以抢庄的玩家
     * @param playerUid
     */
    public void addRobBanker(long playerUid) {
        this.robBanker.putIfAbsent(playerUid, -1);
    }

    /**
     * 执行抢庄
     * @param playerUid 抢庄玩家
     * @param mul 抢庄倍数
     * @return
     */
    public ErrorCode selectRobBaker(long playerUid, int mul) {
        Integer temp = this.robBanker.get(playerUid);
        if (null == temp) {
            Logs.ROOM.warn("%s 不在该房间内", playerUid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (-1 != temp) {
            Logs.ROOM.warn("%s 已经抢过了 mul:%d", playerUid, temp);
            return ErrorCode.ROOM_POKER_COW_ALREADY_ROB_BANKER;
        }
        this.robBanker.put(playerUid, mul);
        ++this.selectCnt;

        PCLIPokerNtfCowRobBankerInfo info = new PCLIPokerNtfCowRobBankerInfo();
        info.playerUid = playerUid;
        info.mul = mul;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_ROB_BANKER, info);

        this.action.addRobBanker(playerUid, mul);

        return ErrorCode.OK;
    }


    @Override
    public boolean action(boolean timeout) {
        if(timeout){
            for (Map.Entry<Long, Integer> entry : this.robBanker.entrySet()) {
                if (-1 == entry.getValue()) {
                    IPokerPlayer player = (IPokerPlayer) this.room.getRoomPlayer(entry.getKey());
                    if (null != player) {
//                        PCLIPokerNtfCowRobBankerInfo info = new PCLIPokerNtfCowRobBankerInfo();
//                        info.playerUid = player.getUid();
//                        info.mul = 0;
//                        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_ROB_BANKER, info);
                        entry.setValue(-2);
                        this.action.addRobBanker(player.getUid(), -2);
                    }
                    ++this.selectCnt;
                }
            }
        }
        //所有玩家都抢过庄后
        if (this.selectCnt == this.robBanker.size()) {
            Long[] maxPlayer = new Long[this.robBanker.size()];
            int index = 0;
            int max = Integer.MIN_VALUE;
            //找出抢庄倍数最大的玩家
            for (Map.Entry<Long, Integer> entry : this.robBanker.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    for (int i = 0; i < index; ++i) {
                        maxPlayer[i] = null;
                    }
                    index = 0;
                    maxPlayer[index++] = entry.getKey();
                } else if (entry.getValue() == max) {
                    maxPlayer[index++] = entry.getKey();
                }
            }
            ((IPaiGowRoom) this.room).setMaxRobBanker(maxPlayer, index);
            return true;
        }
        return false;
    }

    /**
     * 通知所有玩家抢庄信息
     */
    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, Integer>> it = this.robBanker.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            PCLIPokerNtfCowAllRobBankerInfo info = new PCLIPokerNtfCowAllRobBankerInfo();
            info.allInfo.putAll(this.robBanker);
            info.allInfo.put(entry.getKey(), entry.getValue());
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_PAI_GOW_ROB_BANKER_INFO, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (!this.robBanker.containsKey(player.getUid())) {
            return;
        }
        PCLIPokerNtfCowAllRobBankerInfo info = new PCLIPokerNtfCowAllRobBankerInfo();
        info.allInfo.putAll(this.robBanker);
        info.allInfo.put(player.getUid(), this.robBanker.getOrDefault(player.getUid(),-1));
        player.send(CommandId.CLI_NTF_POKER_PAI_GOW_ROB_BANKER_INFO, info);
    }
}
