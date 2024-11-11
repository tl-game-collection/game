package com.xiuxiu.app.server.room.handle.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.xiuxiu.app.server.room.normal.poker.paigow.PaiGowHotRoom;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.app.server.room.player.poker.PaiGowPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.app.server.score.BoxArenaScoreInfoPlayerId;
import com.xiuxiu.app.server.score.ScoreItemInfo;

public class BoxArenaPawiGowHotRoomHandle extends ServiceChargeBoxRoomHandle {

    public BoxArenaPawiGowHotRoomHandle(IRoom room, Box box) {
        super(room, box);
    }
    
    @Override
    public boolean hasPlayed(long playerUid) {
        if (this.room.getRoomState() != ERoomState.START) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
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
        itemInfo.setScore(temp.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false));
        itemInfo.setPlayerUid(temp.getUid());
        PaiGowPlayer paiGowPlayer = (PaiGowPlayer)temp;
        int[] cardTypes = paiGowPlayer.getOpenCardType();
        int[] tempCardTypes = new int[cardTypes.length];
        System.arraycopy(cardTypes, 0, tempCardTypes, 0, cardTypes.length);
        itemInfo.setCardTypes(tempCardTypes);
        // 记录开牌记录
        if (paiGowPlayer.getOpenCards() != null) {
            itemInfo.getCard().addAll(paiGowPlayer.getOpenCards());
        } 
        return itemInfo;
    }
    
    @Override
    protected int getScore(IRoomPlayer temp) {
        return temp.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false);
    }
    
    @Override
    protected void resetScore(IRoomPlayer temp, int finalScore) {
        temp.setScore(Score.POKER_PAIGOW_LOOP_SCORE, finalScore, false);
    }
    
    @Override
    protected boolean doCheckAgain(IClub mainClub, boolean killPlayer) {
        return Boolean.TRUE;
    }
    
    @Override
    protected void doServiceCharge(IBoxOwner boxOwner) {
        PaiGowHotRoom paiGowHotRoom = (PaiGowHotRoom) this.room;
        List<Long> maxPlayerUidList = new ArrayList<>();
        int maxScore = 0;
        for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
            IRoomPlayer temp = this.room.getRoomPlayer(i);
            if (null == temp) {
                continue;
            }
            
            int score = temp.getScore(Score.SCORE, false);
            if (paiGowHotRoom.getBankerIndex() == i) {
                score -= paiGowHotRoom.getBaseBottomScore()* (1 + paiGowHotRoom.getKeepCount());
            }
            if (score >= maxScore && score > 0) {
                
                if (score > maxScore) {
                    maxPlayerUidList.clear();
                }
                if (!maxPlayerUidList.contains(temp.getUid())) {
                    maxPlayerUidList.add(temp.getUid());
                }
                maxScore = score;
            }
        }
        Set<Long> wathPlayerUidList = new HashSet<>();
        Set<Long> watchList = ((Room)this.room).getWatchList();
        if (watchList != null) {
            Iterator<Long> it = watchList.iterator();
            while (it.hasNext()) {
                IRoomPlayer temp = box.getRoomPlayer(it.next());
                if (null == temp) {
                    continue;
                }
                if (isPlayedPlayer(temp.getUid())) {
                    wathPlayerUidList.add(temp.getUid());
                    int score = temp.getScore(Score.SCORE, false);
                    if (score >= maxScore && score > 0) {
                        if (score > maxScore) {
                            maxPlayerUidList.clear();
                        }
                        if (!maxPlayerUidList.contains(temp.getUid())) {
                            maxPlayerUidList.add(temp.getUid());
                        }
                        maxScore = score;
                    }
                }
            }
        }
        // 大赢家分低于x免单
        if (winExtraConditionNotServiceCHarge > 0 && winExtraConditionNotServiceCHarge > maxScore) {
            return;
        }

        long now = System.currentTimeMillis();
        IClub mainClub = (IClub) boxOwner;
        List<Long> ids = new ArrayList<>();
        for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
            IRoomPlayer temp = this.room.getRoomPlayer(i);
            if (null == temp) {
                continue;
            }
            if (!ids.contains(temp.getUid())) {
                ids.add(temp.getUid());
            }
            doServiceCharge(mainClub, temp, maxPlayerUidList, now);
        }
        for (long playerUid : wathPlayerUidList) {
            IRoomPlayer temp = box.getRoomPlayer(playerUid);
            if (null == temp) {
                continue;
            }
            if (ids.contains(playerUid)) {
                continue;
            }
            doServiceCharge(mainClub, temp, maxPlayerUidList, now);
        }
    }
    
    private void doServiceCharge(IClub mainClub, IRoomPlayer temp, List<Long> maxPlayerUidList, long now) {
        PaiGowHotRoom paiGowHotRoom = (PaiGowHotRoom) this.room;
        // 大局输分(结算分)
        int score = temp.getScore(Score.SCORE, false);
        if (paiGowHotRoom.getBankerIndex() == temp.getIndex()) {
            score -= paiGowHotRoom.getBaseBottomScore() * (1 + paiGowHotRoom.getKeepCount());
        }

        // 大赢家
        if (maxPlayerUidList.contains(temp.getUid())) {
            int serviceValue = winServiceCharge;
            if (score > winExtraCondition) {
                serviceValue += winExtraServiceCharge;
            }
            if (serviceValue > 0) {
                // 扣水，返回实际抽水值
                serviceValue = mainClub.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                        temp.getUid(), -serviceValue, 0);
                mainClub.divideServiceCharge(box.getUid(), temp.getUid(), serviceValue*100, now);
            }
        } else if (otherServiceCharge > 0) {
            int serviceValue = otherServiceCharge;
            // 扣水，返回实际抽水值
            serviceValue = mainClub.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()), temp.getUid(),
                    -serviceValue, 0);
            mainClub.divideServiceCharge(box.getUid(), temp.getUid(), serviceValue*100, now);
        }
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
