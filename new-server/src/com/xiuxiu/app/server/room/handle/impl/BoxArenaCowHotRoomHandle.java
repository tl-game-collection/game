package com.xiuxiu.app.server.room.handle.impl;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.poker.cow.AbstractCowRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.app.server.score.BoxArenaScoreInfoPlayerId;
import com.xiuxiu.app.server.score.ScoreItemInfo;

import java.util.*;

public class BoxArenaCowHotRoomHandle extends MutilServiceChargeBoxRoomHandle {

    public BoxArenaCowHotRoomHandle(IRoom room, Box box) {
        super(room, box);
    }

    @Override
    public boolean hasPlayed(long playerUid) {
        return room.isStart();
    }

    @Override
    public void record() {
        Record record = this.room.getRecord();

        long now = System.currentTimeMillis();

        BoxArenaScoreInfo scoreInfo = new BoxArenaScoreInfo();
        scoreInfo.setTime(now);
        scoreInfo.setUid(record.getUid());
        scoreInfo.setBoxUid(this.getBoxUid());
        scoreInfo.setDirty(true);
        IRoomPlayer[] allPlayer = this.room.getAllPlayer();
        for (int i = 0; i < this.room.getPlayerNum(); ++i) {
            IRoomPlayer temp = allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            BoxArenaScoreInfoPlayerId score = new BoxArenaScoreInfoPlayerId();
            score.setUid(UIDManager.I.getAndInc(UIDType.BOX_ARENA_SCORE_INFO_PLAYER_ID));
            score.setPlayerUid(temp.getUid());
            score.setScoreUid(record.getUid());
            score.setDirty(true);
            score.save();

            ScoreItemInfo itemInfo = buildScoreItemInfo(temp);
            if (itemInfo != null) {
                scoreInfo.getScore().add(itemInfo);
            }
        }

        scoreInfo.save();


        for (int i = 0; i < this.room.getPlayerNum(); ++i) {
            IRoomPlayer temp = allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            IArenaRoomPlayerHelper arenaRoomPlayerHelper = (IArenaRoomPlayerHelper) temp.getRoomPlayerHelper();
            arenaRoomPlayerHelper.record(temp.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false), scoreInfo.getUid(), now);
        }

        try {
            record.save();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private ScoreItemInfo buildScoreItemInfo(IRoomPlayer temp) {
        ScoreItemInfo itemInfo = new ScoreItemInfo();
        itemInfo.setScore(temp.getScore(Score.POKER_COW_LOOP_SCORE, false));
        itemInfo.setPlayerUid(temp.getUid());
        CowPlayer cowPlayer = (CowPlayer)temp;
        itemInfo.setCardType(cowPlayer.getCurCardType().getValue());
        itemInfo.setLastCard(cowPlayer.getLastCard());
        itemInfo.setBnaker(this.room.getBankerIndex()==cowPlayer.getIndex());
        itemInfo.setBankerMul(cowPlayer.getScore(Score.POKER_COW_ROB_BANKER_MUL,false));
        itemInfo.setPushMul(cowPlayer.getScore(Score.POKER_COW_REBET,false));
        if (cowPlayer.getResultCard() != null) {
            itemInfo.getCard().addAll(cowPlayer.getResultCard());
            itemInfo.getTailCard().addAll(cowPlayer.getHandCard());
        }
        itemInfo.setMonsterType(((AbstractCowRoom)this.room).getCowInfo().getLaiZiCard());
        return itemInfo;
    }

    @Override
    protected int getScore(IRoomPlayer temp) {
        return temp.getScore(Score.POKER_COW_LOOP_SCORE, false);
    }

    @Override
    protected void resetScore(IRoomPlayer temp, int finalScore) {
        temp.setScore(Score.POKER_COW_LOOP_SCORE, finalScore, false);
    }

    @Override
    protected boolean doCheckAgain(IClub mainClub, boolean killPlayer) {
        return Boolean.TRUE;
    }

    @Override
    protected boolean isBankerPlayer(int bankerPlayerIndex) {
        return this.room.getBankerIndex() == bankerPlayerIndex;
    }

    @Override
    public ErrorCode sitDown(IPlayer player, int index) {
        ErrorCode code = hasMeetCondition(player.getUid());
        if (ErrorCode.OK != code){
            return code;
        }
        return this.box.sitDown((Player) player, index);
    }

}
