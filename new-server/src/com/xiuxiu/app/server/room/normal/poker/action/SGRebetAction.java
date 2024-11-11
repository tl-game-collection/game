package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowAllReBetInfo;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowReBetInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.sg.SGRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.SGRebetRecordAction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SGRebetAction  extends BasePokerAction {
    protected HashMap<Long, Integer> allPushNote = new HashMap<>();
    protected HashMap<Long, Integer> allSelectRebet = new HashMap<>();
    protected HashMap<Long,Boolean> allDoubling = new HashMap<>();
    protected int selectCnt = 0;
    protected int base = 1;

    protected SGRebetRecordAction action;

    public SGRebetAction(PokerRoom room, long timeout) {
        super(room, EActionOp.ROB_BANKER, null, timeout);
        this.action = ((PokerRecord) this.room.getRecord()).addSGRebetRecordAction();
    }

    public void addPushNote(long playerUid, int pushNote) {
        this.allPushNote.putIfAbsent(playerUid, pushNote);
    }

    public void setBase(int base) {
        this.base = base;
    }

    public void setDoubling(long playerUid,boolean doubling) {
        this.allDoubling.putIfAbsent(playerUid, doubling);
    }

    public ErrorCode rebet(long playerUid, int rebet) {
        if (this.allSelectRebet.containsKey(playerUid)) {
            Logs.ROOM.warn("%s playerUid:%d 已经下过注了", this, playerUid);
            return ErrorCode.ROOM_POKER_COW_ALREADY_REBET;
        }

//      if (0 != rebet && (rebet == this.base || rebet == this.base * 2 || (this.allDoubling.getOrDefault(playerUid,false) ? (rebet == 4 * this.base) : false) || ((0 != this.allPushNote.getOrDefault(playerUid, 0)) ? rebet == this.allPushNote.get(playerUid) : false))) {
        if(((SGRoom)this.room).checkRebValue((IPokerPlayer) this.room.getRoomPlayer(playerUid),rebet,this.allPushNote.getOrDefault(playerUid,0))){
            this.allSelectRebet.put(playerUid, rebet);
            ++this.selectCnt;
            PCLIPokerNtfCowReBetInfo info = new PCLIPokerNtfCowReBetInfo();
            info.playerUid = playerUid;
            info.rebet = rebet;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_SG_REBET, info);

            this.action.addRebet(playerUid, rebet);
        } else {
            Logs.ROOM.warn("%s playerUid:%d 无效下注:%d", this, playerUid, rebet);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPushNote.entrySet()) {
                if (!this.allSelectRebet.containsKey(entry.getKey())) {
                    IPokerPlayer player = (IPokerPlayer) this.room.getRoomPlayer(entry.getKey());
                    if (null != player) {
                        player.setScore(Score.POKER_SG_REBET, ((SGRoom)this.room).getMinReb(player), false);
                        PCLIPokerNtfCowReBetInfo info = new PCLIPokerNtfCowReBetInfo();
                        info.playerUid = player.getUid();
                        info.rebet = ((SGRoom)this.room).getMinReb(player);
                        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_SG_REBET, info);

                        this.action.addRebet(player.getUid(), this.base);
                    }
                    ++this.selectCnt;
                }
            }
        }
        if (this.selectCnt == this.allPushNote.size()) {
            ((SGRoom) this.room).onDealCard();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, Integer>> it = this.allPushNote.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            long playerUid = entry.getKey();
            PCLIPokerNtfCowAllReBetInfo info = new PCLIPokerNtfCowAllReBetInfo();
            info.allInfo.putAll(this.allSelectRebet);
            info.baseRebet = this.base;
            info.doubling = this.allDoubling.getOrDefault(playerUid,false);
            info.pushNote = entry.getValue();
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_SG_REBET_INFO, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (!this.allPushNote.containsKey(player.getUid())) {
            return;
        }
        PCLIPokerNtfCowAllReBetInfo info = new PCLIPokerNtfCowAllReBetInfo();
        info.allInfo.putAll(this.allSelectRebet);
        info.baseRebet = this.base;
        info.doubling = this.allDoubling.getOrDefault(player.getUid(),false);
        info.pushNote = this.allPushNote.getOrDefault(player.getUid(), 0);
        player.send(CommandId.CLI_NTF_POKER_SG_REBET_INFO, info);
    }
}
