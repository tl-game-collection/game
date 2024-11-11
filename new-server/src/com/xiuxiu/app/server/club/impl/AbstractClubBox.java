package com.xiuxiu.app.server.club.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.rank.ERankType;
import com.xiuxiu.app.server.score.BoxArenaScore;

import javax.swing.*;

/**
 * 包厢抽象实现
 * 
 * @author Administrator
 *
 */
public abstract class AbstractClubBox extends AbstractClubFloor implements IBoxOwner {

    /**
     * 该亲友圈所有包厢
     */
    private ConcurrentHashMap<Long, Box> boxMap = new ConcurrentHashMap<>();

    @Override
    public Box getBox(long boxUid) {
        return BoxManager.I.getBox(boxUid);
    }
    
    @Override
    public Map<Long, Box> getAllBox(){
        return boxMap;
    }

    @Override
    public int getBoxSize() {
        return boxMap.size();
    }

    @Override
    public void addBox(Box box) {
        // 关联亲友圈
        this.boxMap.put(box.getUid(), box);
    }

    @Override
    public void destroyBox(long boxUid) {
        // 从亲友圈关联关系中删除
        this.boxMap.remove(boxUid);
    }

    @Override
    public void addBoxFinalWinner(Long finalWinClubUid, Long finalWinPlayerUid) {
        updateClubRankByBox(ERankType.CLUB_GAME_WINNER,finalWinClubUid,finalWinPlayerUid,1,System.currentTimeMillis());
    }

    @Override
    public void addBoxScoreAndBureau(long fromClubUid, long playerUid, int score, int bureau, long now) {
    }

    @Override
    public int addMemberValueByBox(long fromClubUid, long playerUid, int value, long optPlayer){
        return 0;
    }

    @Override
    public int addMemberValueByBox(long fromClubUid, long playerUid, int value, long optPlayer, boolean needUpdateRank) {
        return  0;
    }

    protected void updateClubRankByBox(ERankType rankType, long fromClubUid, long playerUid, int value, long nowTime){
        if (fromClubUid != this.getClubUid()){
            IClub optClub = ClubManager.I.getClubByUid(fromClubUid);
            if (null != optClub) {
                optClub.updateClubRank(rankType, this.getClubUid(), playerUid, value, nowTime);
            }
        }else{
            if (this.checkIsJoinInMainClub()){
                this.updateClubRank(rankType, this.getClubUid(), playerUid, value, nowTime);
            }else{
                this.updateClubRank(rankType, 0, playerUid, value, nowTime);
            }
        }
    }

    @Override
    public void divideServiceCharge(long boxUid, long playerUid, int cost, long time) {
    }

    @Override
    public void onFinishGame(long boxUid, Set<Long> playerIds) {
    }

    @Override
    public void onFinishAllBureau(long fromClubUid, long playerUid, long now) {
        updateClubRankByBox(ERankType.CLUB_GAME_NUM,fromClubUid,playerUid,1,now);
    }
    
    @Override
    public BoxArenaScore getBoxArenaScoreIfCreate(long clubUid, long boxUid, long playerUid) {
        IClub fromClub = this;
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (club != null) {
            fromClub = club;
        }
        ClubMember memberInfo = fromClub.getMember(playerUid);
        if (null == memberInfo) {
            Logs.GROUP.warn("playerUid:%d 获取竞技场战绩失败, 玩家不在群:%s里 竞技场uid:%d", playerUid, fromClub, boxUid);
            return null;
        }
        Box box = BoxManager.I.getBox(boxUid);
        if (null == box) {
            Logs.GROUP.warn("playerUid:%d 获取竞技场战绩失败, 竞技场不存在 竞技场uid:%d", playerUid, boxUid);
            return null;
        }
        BoxArenaScore arenaScore = new BoxArenaScore();
        arenaScore.setUid(UIDManager.I.getAndInc(UIDType.BOX_ARENA_SCORE));
        arenaScore.setBoxUid(boxUid);
        arenaScore.setGameType(box.getGameType());
        arenaScore.setGameSubType(box.getGameSubType());
        arenaScore.setClubUid(clubUid);
        arenaScore.setBeginTime(System.currentTimeMillis());
        arenaScore.setPlayerUid(playerUid);
        arenaScore.setDirty(true);
        arenaScore.save();
        return arenaScore;
    }
}
