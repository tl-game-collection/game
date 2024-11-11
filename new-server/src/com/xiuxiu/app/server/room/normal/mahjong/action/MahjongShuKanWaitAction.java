package com.xiuxiu.app.server.room.normal.mahjong.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfSelectShuKanInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfShuKanValueInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong.ShuKanRecordAction;

public class MahjongShuKanWaitAction extends BaseMahjongAction {
    private ConcurrentHashMap<Long, List<Integer>> playerOp = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Long> playerTimeout = new ConcurrentHashMap<>();
    private ShuKanRecordAction shuKanRecordAction;

    public MahjongShuKanWaitAction(IMahjongRoom room, MahjongPlayer roomPlayer, long timeout) {
        super(room, EActionOp.SHUKAN, roomPlayer, timeout);
        this.shuKanRecordAction = ((MahjongRecord) room.getRecord()).addShuKanRecordAction();
    }

    public void addPlayer(long uid) {
        List<Integer> point=new ArrayList<>();
        point.add(-1);
        point.add(-1);
      this.playerOp.put(uid,point);
        if (-1 == this.timeout) {
            this.playerTimeout.put(uid, this.timeout);
        } else {
            this.playerTimeout.put(uid, this.startTime + this.timeout);
        }
    }

    public ErrorCode playerSelect(long uid, List<Integer> point) {
      List<Integer> v = this.playerOp.get(uid);
        if (-1 != v.get(0)||-1!=v.get(1)) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        this.playerOp.put(uid, point);
        this.playerTimeout.remove(uid);
        this.shuKanRecordAction.addShuKan(this.room.getRoomPlayer(uid).getUid(), point);
        return ErrorCode.OK;
    }

    public List<Integer> getShuKan(long uid) {
        return this.playerOp.get(uid);
    }

    @Override
    public boolean action(boolean timeout) {
        if (this.playerTimeout.isEmpty()) {
            ((MahjongRoom) this.room).endShuKan();
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
            MahjongPlayer temp = (MahjongPlayer) this.room.getRoomPlayer(entry.getKey());
            this.shuKanRecordAction.addShuKan(temp.getUid(), temp.getShuKanPoint());
            this.playerOp.put(temp.getUid(), temp.getShuKanPoint());
            PCLIMahjongNtfShuKanValueInfo valueInfo = new PCLIMahjongNtfShuKanValueInfo();
            valueInfo.playerUid = temp.getUid();
            valueInfo.value = temp.getShuKanPoint();
            this.room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SHU_KAN_VALUE, valueInfo);
            it.remove();
        }
        return allTimeout;
    }

    @Override
    protected void doRecover() {
        PCLIMahjongNtfSelectShuKanInfo info = new PCLIMahjongNtfSelectShuKanInfo();
        Iterator<Map.Entry<Long, List<Integer>>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Integer>> entry = it.next();
            info.allShuKan.put(entry.getKey(), entry.getValue());
        }
        this.room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SHU_KAN_INFO, info);
    }

    @Override
    public void online(IRoomPlayer player) {
        if(this.playerTimeout.containsKey(player.getUid())) {
            if (-1 == this.timeout) {
                this.playerTimeout.put(player.getUid(), this.timeout);
            }
        }
        PCLIMahjongNtfSelectShuKanInfo info = new PCLIMahjongNtfSelectShuKanInfo();
        Iterator<Map.Entry<Long, List<Integer>>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Integer>> entry = it.next();
            info.allShuKan.put(entry.getKey(), entry.getValue());
        }
        player.send(CommandId.CLI_NTF_MAHJONG_SHU_KAN_INFO, info);
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
