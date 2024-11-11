package com.xiuxiu.app.server.room.handle.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.AbstractBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.app.server.score.BoxArenaScoreInfoPlayerId;
import com.xiuxiu.app.server.score.ScoreItemInfo;

/**
 * 抽象的包厢竞技场业务扩展处理器
 *
 * @author Administrator
 */
public abstract class AbstractBoxArenaRoomHandle extends AbstractBoxRoomHandle {

    /** 抽水类型(1每人每局，2赢家抽，3大赢家抽) */
    protected int costModel;
    /** 抽水 */
    protected int costModelValue;
    
    public AbstractBoxArenaRoomHandle(IRoom room, Box box) {
        super(room, box);
    }
    
    @Override
    public void init() {
        super.init();
        Map<String, Integer> rule = getRoom().getRule();
        this.costModel = rule.getOrDefault(RoomRule.RR_COSTMODEL, 0);
        this.costModelValue = rule.getOrDefault(RoomRule.RR_COSTMODEL_VALUE, 0);
    }
    
    @Override
    public void start() {
        if (costModel == 1 && costModelValue > 0) {
            IBoxOwner boxOwner = this.room.getBoxOwner();
            if (null == boxOwner) {
                return;
            }
            // 每人每局抽水，每小局游戏开始后就每个人进行抽水
            long now = System.currentTimeMillis();
            IClub mainClub = (IClub)boxOwner;
            for (int i = 0, len = room.getMaxPlayerCnt(); i < len; ++i) {
                IRoomPlayer temp = (IRoomPlayer) room.getRoomPlayer(i);
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                int serviceValue = costModelValue;
                // 扣水，返回实际抽水值
                serviceValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()), temp.getUid(), -serviceValue, 0);
                boxOwner.divideServiceCharge(box.getUid(), temp.getUid(), serviceValue*100, now);
            }
        }
    }
    
    @Override
    protected void doServiceCharge(IBoxOwner boxOwner) {
        if (costModel == 2 && costModelValue > 0) {
            List<Long> playerUidList = new ArrayList<>();
            int maxScore = 0;
            for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
                IRoomPlayer temp = this.room.getRoomPlayer(i);
                if (null == temp || temp.isGuest()) {
                    continue;
                }
    
                int score = temp.getScore(Score.SCORE, false);
                /*if (score >= maxScore && score > 0) {
                    if (score > maxScore) {
                        playerUidList.clear();
                    }
                    playerUidList.add(temp.getUid());
                    maxScore = score;
                }
                */
                if (score > 0) {
                    playerUidList.add(temp.getUid());
                }
            }
            // 大赢家抽水
            doServiceCharge(boxOwner, playerUidList, maxScore);
        }
    }
    
    private void doServiceCharge(IBoxOwner boxOwner, List<Long> playerUidList, int maxScore) {
        long now = System.currentTimeMillis();
        IClub mainClub = (IClub) boxOwner;
        for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
            IRoomPlayer temp = this.room.getRoomPlayer(i);
            if (null == temp || temp.isGuest()) {
                continue;
            }
            // 大赢家
            if (playerUidList.contains(temp.getUid())) {
                // 小局输分(结算分)
                int score = temp.getScore(Score.SCORE, false);
                int serviceValue = (int) (score * (costModelValue / 10000f));
                // 扣水，返回实际抽水值
                serviceValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                            temp.getUid(), -serviceValue, 0);
                boxOwner.divideServiceCharge(box.getUid(), temp.getUid(), serviceValue*100, now);
            }
        }
    }
    
    protected abstract ScoreItemInfo buildScoreItemInfo(IRoomPlayer temp);

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
            arenaRoomPlayerHelper.record(this.room.getRecordScore(temp), scoreInfo.getUid(), now);
        }

        try {
            record.save();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPlayed(long playerUid) {
        if (this.room.getRoomState() != ERoomState.START) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public ErrorCode readyHandle(long playerUid, boolean checkContains) {
        ErrorCode code = this.hasMeetCondition(playerUid);
        if (ErrorCode.OK != code){
            return code;
        }
        return super.readyHandle(playerUid, checkContains);
    }

    @Override
    public ErrorCode sitDown(IPlayer player, int index) {
        ErrorCode code = this.hasMeetCondition(player.getUid());
        if (ErrorCode.OK != code){
            return code;
        }
        return this.box.sitDown((Player) player, index);
    }
    
    @Override
    protected void returnDiamond() {
        
    }
    
    @Override
    public void onSitup() {
        
    }
    
    @Override
    protected void calculateGoldAfter() {
        if (GameType.isArenaGame(room.getGameType())) {
            // 扣管理费用
            if (!room.checkIsDestroy()) {
                serviceCharge(false);
            }
        }
    }

}
