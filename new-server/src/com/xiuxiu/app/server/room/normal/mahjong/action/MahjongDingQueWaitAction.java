package com.xiuxiu.app.server.room.normal.mahjong.action;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfBeginDingQue;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong.DingQueRecordAction;
import com.xiuxiu.app.server.room.record.mahjong.MahjongRecord;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MahjongDingQueWaitAction extends BaseMahjongAction {
    private ConcurrentHashMap<Long, Integer> playerOp = new ConcurrentHashMap<>();
    private DingQueRecordAction dingQueRecordAction;
    private int cnt = 0;
    private byte[] tempHandCard = new byte[MahjongUtil.MJ_CARD_KINDS];

    public MahjongDingQueWaitAction(IMahjongRoom room, MahjongPlayer roomPlayer, long timeout) {
        super(room, EActionOp.DING_QUE, roomPlayer, timeout);
        this.dingQueRecordAction = ((MahjongRecord) room.getRecord()).addDingQueAction();
    }

    public void addPlayer(long uid) {
        this.playerOp.put(uid, -1);
    }

    public ErrorCode dingQue(long uid, int color) {
        int v = this.playerOp.get(uid);
        if (-1 != v) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        MahjongPlayer player = (MahjongPlayer) this.room.getRoomPlayer(uid);
        if (null == player) {
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (!player.hasColor(color)) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        player.setDingQueColor(color);
        this.playerOp.put(uid, color);
        this.dingQueRecordAction.addDingQue(uid, color);
        ++this.cnt;
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout || this.cnt >= this.playerOp.size()) {
            this.room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_END_DING_QUE, null);
            ((MahjongRoom) this.room).endDingQue();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, Integer>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            PCLIMahjongNtfBeginDingQue info = new PCLIMahjongNtfBeginDingQue();
            info.color = entry.getValue();
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_MAHJONG_BEGIN_DING_QUE, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        PCLIMahjongNtfBeginDingQue info = new PCLIMahjongNtfBeginDingQue();
        info.color = this.playerOp.getOrDefault(player.getUid(), -1);
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_DING_QUE, info);
    }
}
