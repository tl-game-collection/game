package com.xiuxiu.app.server.room.normal.action.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;

import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowAllReBetInfo;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowReBetInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.cow.AbstractCowRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.ICowRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.cow.CowReBetRecordAction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 9:43
 * @comment:
 */
public class CowReBetAction extends BasePokerAction {
    private ConcurrentHashMap<Long, Integer> allPushNote = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Integer> allSelectRebet = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Boolean> allDoubling = new ConcurrentHashMap<>();
    private int selectCnt = 0;
    private int base = 1;
    private CowReBetRecordAction action;

    public CowReBetAction(PokerRoom room, long timeout) {
        super(room, EActionOp.ROB_BANKER, null, timeout);
        this.action = ((PokerRecord) this.room.getRecord()).addCowRebetRecordAction();
    }

    public void addPushNote(long playerUid, int pushNote) {
        this.allPushNote.putIfAbsent(playerUid, pushNote);
    }

    public void setDoubling(long playerUid, boolean doubling) {
        this.allDoubling.putIfAbsent(playerUid, doubling);
    }

    public void setBase(int base) {
        this.base = base;
    }

    public ErrorCode rebet(long playerUid, int rebet) {
        if (this.allSelectRebet.containsKey(playerUid)) {
            Logs.ROOM.warn("%s playerUid:%d 已经下过注了", this, playerUid);
            return ErrorCode.ROOM_POKER_COW_ALREADY_REBET;
        }
        IPokerPlayer pokerPlayer = (IPokerPlayer) this.room.getRoomPlayer(playerUid);
        Integer pushNoteValue = this.allPushNote.getOrDefault(playerUid,0);
        Boolean isDoubling = this.allDoubling.getOrDefault(playerUid,false);
        AbstractCowRoom abstractCowRoom = (AbstractCowRoom)this.room;
        //下注检查
        boolean canReb = abstractCowRoom.checkRebValue(pokerPlayer,rebet,pushNoteValue,isDoubling);
        if (canReb){
        	//放入所有下注map
            this.allSelectRebet.put(playerUid, rebet);
            ++this.selectCnt;
            //放入下注玩家下注值
            this.room.getRoomPlayer(playerUid).setScore(Score.POKER_COW_REBET,rebet, false);
            PCLIPokerNtfCowReBetInfo info = new PCLIPokerNtfCowReBetInfo();
            info.playerUid = playerUid;
            info.rebet = rebet;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_REBET, info);
            this.action.addRebet(playerUid, rebet);
        } else {
            Logs.ROOM.warn("%s playerUid:%d 无效下注:%d", this, playerUid, rebet);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        return ErrorCode.OK;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Integer> entry : this.allPushNote.entrySet()) {
            long playerUid = entry.getKey();
            PCLIPokerNtfCowAllReBetInfo info = new PCLIPokerNtfCowAllReBetInfo();
            info.allInfo.putAll(this.allSelectRebet);
            info.baseRebet = this.base;
            info.doubling = this.allDoubling.getOrDefault(playerUid, false);
            info.pushNote = entry.getValue();
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_COW_REBET_INFO, info);
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
        info.doubling = this.allDoubling.getOrDefault(player.getUid(), false);
        info.pushNote = this.allPushNote.getOrDefault(player.getUid(), 0);
        player.send(CommandId.CLI_NTF_POKER_COW_REBET_INFO, info);
    }

    @Override
    public boolean canAction(long curTime) {
        if (this.room.getGameType() == GameType.GAME_TYPE_COW && this.room.getGameSubType() == 1 && this.selectCnt >=  this.allPushNote.size()){
            return true;
        }
        return super.canAction(curTime);
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPushNote.entrySet()) {
                if (!this.allSelectRebet.containsKey(entry.getKey())) {
                    IPokerPlayer player = (IPokerPlayer) this.room.getRoomPlayer(entry.getKey());
                    if (null != player) {
                        int rebValue = this.base;
                        if (this.room.getGameType() == GameType.GAME_TYPE_COW && this.room.getGameSubType() == 1){
                            rebValue = ((CowHotRoom)this.room).getMinReb(player);
                        }
                        player.setScore(Score.POKER_COW_REBET,rebValue, false);
                        PCLIPokerNtfCowReBetInfo info = new PCLIPokerNtfCowReBetInfo();
                        info.playerUid = player.getUid();
                        info.rebet = rebValue;
                        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_REBET, info);

                        this.action.addRebet(player.getUid(),rebValue);
                    }
                }
            }
            this.selectCnt = this.allPushNote.size();
        }
        if (this.selectCnt == this.allPushNote.size()) {
            try {
                ((ICowRoom) this.room).onDealCard();
            }catch(Exception e) {
                e.getStackTrace();
            }
            return true;
        }
        return false;
    }
}
