package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfSGAllRobBankerInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfSGRobBankerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.sg.SGRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.SGRobBankerRecordAction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SGRobBankerAction  extends BasePokerAction {
    protected HashMap<Long, Integer> robBanker = new HashMap<>();
    protected int selectCnt = 0;
    protected boolean darkRob = false;      // 暗抢

    protected SGRobBankerRecordAction action;

    public SGRobBankerAction(PokerRoom room, long timeout) {
        super(room, EActionOp.ROB_BANKER, null, timeout);
        action = ((PokerRecord) this.room.getRecord()).addSGRobBankerRecordAction();
    }

    public void addRobBanker(long playerUid) {
        this.robBanker.putIfAbsent(playerUid, -1);
    }

    public void setDarkRob(boolean darkRob) {
        this.darkRob = darkRob;
    }

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

        if (!this.darkRob) {
            PCLIPokerNtfSGRobBankerInfo info = new PCLIPokerNtfSGRobBankerInfo();
            info.playerUid = playerUid;
            info.mul = mul;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_SG_ROB_BANKER, info);
        }

        this.action.addRobBanker(playerUid, mul);

        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.robBanker.entrySet()) {
                if (-1 == entry.getValue()) {
                    IPokerPlayer player = (IPokerPlayer) this.room.getRoomPlayer(entry.getKey());
                    if (null != player) {
                        player.setScore(Score.POKER_COW_ROB_BANKER_MUL, 0, false);
                        if (!this.darkRob) {
                            PCLIPokerNtfSGRobBankerInfo info = new PCLIPokerNtfSGRobBankerInfo();
                            info.playerUid = player.getUid();
                            info.mul = 0;
                            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_SG_ROB_BANKER, info);
                        }
                        this.action.addRobBanker(player.getUid(), 0);
                    }
                    ++this.selectCnt;
                }
            }
        }

        if (this.selectCnt == this.robBanker.size()) {
            Long[] maxPlayer = new Long[this.robBanker.size()];
            int index = 0;
            int max = -1;
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

            ((SGRoom) this.room).setMaxRobBanker(maxPlayer, index, this.darkRob);
            ((SGRoom) this.room).onRebet();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, Integer>> it = this.robBanker.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            PCLIPokerNtfSGAllRobBankerInfo info = new PCLIPokerNtfSGAllRobBankerInfo();
            if (!this.darkRob) {
                info.allInfo.putAll(this.robBanker);
            }
            info.allInfo.put(entry.getKey(), entry.getValue());
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_SG_ROB_BANKER_INFO, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (!this.robBanker.containsKey(player.getUid())) {
            return;
        }
        PCLIPokerNtfSGAllRobBankerInfo info = new PCLIPokerNtfSGAllRobBankerInfo();
        if (!this.darkRob) {
            info.allInfo.putAll(this.robBanker);
        }
        info.allInfo.put(player.getUid(), this.robBanker.getOrDefault(player.getUid(),-1));
        player.send(CommandId.CLI_NTF_POKER_SG_ROB_BANKER_INFO, info);
    }
}
