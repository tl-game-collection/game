package com.xiuxiu.app.server.club.impl;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfValueChange;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.club.*;
import com.xiuxiu.app.server.club.activity.ClubActivity;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldData;
import com.xiuxiu.app.server.club.constant.EClubActivityType;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EClubRVChangeType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.rank.ERankType;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 创建亲友圈_比赛场
 * @author MyPC
 *
 */
public class ClubGold extends AbstractClubBox {
    @Override
    public void divideServiceCharge(long boxUid, long playerUid, int cost, long time) {
        if (cost <= 0){
            return ;
        }

        Player player = PlayerManager.I.getPlayer(playerUid);
        if (null == player){
            return;
        }

        if (this.checkIsMainClub()) {
            long selectClubUid = this.getEnterFromClubUid(playerUid);
            //扣除大圈等级管理费
            int levelServiceCharge = this.getClubInfo().getServiceChargeMap().getOrDefault(selectClubUid,0);
            if (levelServiceCharge > 0){
                int costValue = cost * levelServiceCharge / 100;
                if (costValue > 0) {
                    cost -= costValue;
                    this.addMemberClubRewardValue(this.getOwnerId(),costValue,playerUid, EClubRVChangeType.CLUB_LEVEL_SERVICE_CHARGE_INC);
                }
            }

            if (selectClubUid != this.getClubUid()) {
                IClub selectClub = ClubManager.I.getClubByUid(selectClubUid);
                if (null == selectClub || selectClub.getFinalClubId() != this.getClubUid()) {
                    Logs.CLUB.error("查找分成亲友圈失败 selectClubUid:%d", selectClubUid);
                    return;
                }
                selectClub.divideServiceCharge(boxUid, playerUid, cost, time);
                return;
            }
        }

        ClubMember memberInfo = this.getMember(playerUid);
        if (null == memberInfo) {
            return;
        }

        // 首先扣除群管理费
        int remain = cost;
        int costValue = remain * this.clubInfo.getServiceChargeDivide() / 100;
        if (costValue > 0) {
            remain -= costValue;
            this.addMemberClubRewardValue(this.getOwnerId(),costValue,playerUid, EClubRVChangeType.CLUB_SERVICE_CHARGE_INC);
        }

        //其他抽成
        //如果自己是群主都是自己的
        if (this.getOwnerId() != playerUid){
            //抽自己的一条线,直属抽成
            remain = this.dealDivideServiceCharge(false,memberInfo,playerUid,remain);

            //抽上级一条线,直属抽成(找到最近一个直属抽成比例不为0的玩家抽直属，只抽一次)
            boolean onlyUpLine = false;
            ClubMember upLineMemberInfo = this.getMember(memberInfo.getUplinePlayerUid());
            while (upLineMemberInfo != null && upLineMemberInfo.getPlayerUid() != this.getOwnerId() && upLineMemberInfo.getPlayerUid() != playerUid) {
                remain = this.dealDivideServiceCharge(onlyUpLine,upLineMemberInfo,playerUid,remain);
                onlyUpLine = onlyUpLine ? true : (ClubActivityManager.I.getAndSetDivide(this.getClubUid(),upLineMemberInfo.getPlayerUid()) > 0 || ClubActivityManager.I.getAndSetDivide(this.getClubUid(),upLineMemberInfo.getPlayerUid()) > 0);
                upLineMemberInfo = this.getMember(upLineMemberInfo.getUplinePlayerUid());
            }
        }

        if(remain > 0){
            //扣除等二级管理费
            if (this.checkIsLevelTwoClub()) {
                IClub levelOneClub = ClubManager.I.getClubByUid(this.getClubInfo().getParentUid());
                if (null != levelOneClub) {
                    int levelServiceCharge = levelOneClub.getClubInfo().getServiceChargeMap().getOrDefault(this.getClubUid(), 0);
                    if (levelServiceCharge > 0) {
                        costValue = remain * levelServiceCharge / 100;
                        if (costValue > 0) {
                            remain -= costValue;
                            levelOneClub.addMemberClubRewardValue(levelOneClub.getOwnerId(), costValue, playerUid, EClubRVChangeType.CLUB_LEVEL_ONE_SERVICE_CHARGE_INC);
                        }
                    }
                }
            }
            if (remain > 0) {
                // 剩下的都是群主的
                this.addMemberClubRewardValue(this.getOwnerId(), remain, playerUid, EClubRVChangeType.CLUB_LEADER_INC);
            }
        }
    }

    /**
     *计算直属和一条线抽成值
     *return 返还抽成后剩余值
     *onlyUpLine ture 只计算一条线抽成 false 计算一条线并且计算直属抽成
     */
    private int dealDivideServiceCharge(boolean onlyUpLine, ClubMember upLineMemberInfo,long costPlayerUid,int remainValue){
        if (!onlyUpLine) {
            int arenaDivide = ClubActivityManager.I.getAndSetDivide(this.getClubUid(),upLineMemberInfo.getPlayerUid());
            int gained = Math.min(remainValue, remainValue * arenaDivide / 100);
            remainValue -= gained;
            this.addMemberClubRewardValue(upLineMemberInfo.getPlayerUid(), gained, costPlayerUid, EClubRVChangeType.DIRECTLY_UNDER_INC);
        }

        int arenaDivideLine = ClubActivityManager.I.getAndSetArenaDivideLine(this.getClubUid(),upLineMemberInfo.getPlayerUid());
        int gained = Math.min(remainValue, remainValue * arenaDivideLine / 100);
        remainValue -= gained;
        this.addMemberClubRewardValue(upLineMemberInfo.getPlayerUid(), gained, costPlayerUid,EClubRVChangeType.UP_LINE_INC);

        return remainValue;
    }

    @Override
    public int addMemberValueByBox(long fromClubUid, long playerUid, int value, long optPlayer){
        return addMemberValueByBox(fromClubUid,playerUid,value,optPlayer,true);
    }

    @Override
    public int addMemberValueByBox(long fromClubUid, long playerUid, int value, long optPlayer,boolean needUpdateRank) {
        IClub optClub = null;
        if (fromClubUid == this.getClubUid()){
            optClub = this;
        }else{
            optClub = ClubManager.I.getClubByUid(fromClubUid);
        }
        if (null == optClub){
            return 0;
        }
        while(!optClub.addMemberClubGold(playerUid,value,optPlayer, EClubGoldChangeType.CHANGE_BY_BOX)) {
            if (value > 0){
                value = 0;
            }else{
                int tempValue = (int)optClub.getGold(playerUid);
                value = tempValue > Math.abs(value) ? value : -tempValue;
            }
        }

        if (needUpdateRank){
            updateClubRankByBox(ERankType.CLUB_GAME_SCORE, fromClubUid, playerUid, value, System.currentTimeMillis());
        }

        return Math.abs(value);
    }

    @Override
    public boolean addMemberClubGold(long playerUid, int value, long optPlayerUid, EClubGoldChangeType changeType) {
        if (0 == value) {
            return true;
        }

        boolean isCheckValidChief = checkValidChief(this,playerUid);
        ClubMemberExt clubMemberExt = this.getMemberExt(playerUid, true);
        long oldValue = 0;
        synchronized (clubMemberExt) {
            oldValue = clubMemberExt.getGold();
            long newValue = oldValue + value;
            if (!this.checkChangeValue(oldValue,newValue,value)){
                return false;
            }

            clubMemberExt.setGold(newValue);

            //处理总上分总下分
            if (isCheckValidChief){
                if (changeType == EClubGoldChangeType.INC_FROM_NULL
                        || changeType == EClubGoldChangeType.DEC_TO_NULL
                        || changeType == EClubGoldChangeType.BACK_GROUND_CHANGE
                        || changeType == EClubGoldChangeType.BACK_GROUND_RECHARGE){
                    if (value > 0) {
                        clubMemberExt.setUpTotalScore(clubMemberExt.getUpTotalScore() + value);
                    } else {
                        clubMemberExt.setDownTotalScore(clubMemberExt.getDownTotalScore() + Math.abs(value));
                    }
                }
            }else {
                if (changeType == EClubGoldChangeType.INC_MANAGER
                        || changeType == EClubGoldChangeType.DEC_MANAGER
                        || changeType == EClubGoldChangeType.INC_MANAGER_DEC
                        || changeType == EClubGoldChangeType.DEC_MANAGER_INC
                        || changeType == EClubGoldChangeType.LEAGUE_REBACK
                        || changeType == EClubGoldChangeType.BACK_GROUND_CHANGE
                        || changeType == EClubGoldChangeType.BACK_GROUND_RECHARGE
                        || changeType == EClubGoldChangeType.INC_DOWN_TREASURER_DEC
                        || changeType == EClubGoldChangeType.DEC_DOWN_TREASURER_INC
                        || changeType == EClubGoldChangeType.GIVE_GOLD) {
                    if (value > 0) {
                        clubMemberExt.setUpTotalScore(clubMemberExt.getUpTotalScore() + value);
                    } else {
                        clubMemberExt.setDownTotalScore(clubMemberExt.getDownTotalScore() + Math.abs(value));
                    }
                }
            }

            clubMemberExt.setDirty(true);
        }

        saveClubGoldRecord(playerUid, (int) oldValue, value, optPlayerUid, this.getFinalClubId(), this.getClubUid(),  System.currentTimeMillis(), changeType);

        notifyClubMemberValueChange(clubMemberExt);
        //Logs.PLAYER.warn("玩家 %d 增加金币数 %d, 增加类型 %s, 操作人 %d", playerUid, value, changeType.getDesc(), optPlayerUid);

        //处理总上分总下分
        if (!isCheckValidChief){
            if (changeType == EClubGoldChangeType.BACK_GROUND_CHANGE
                || changeType == EClubGoldChangeType.BACK_GROUND_RECHARGE){
                //|| changeType == EClubGoldChangeType.INC_DOWN_TREASURER_DEC
                //|| changeType == EClubGoldChangeType.DEC_DOWN_TREASURER_INC) {
                ClubMemberExt ownerClubMemberExt = null;
                if (!this.checkIsJoinInMainClub() || this.checkIsMainClub()) {
                    ownerClubMemberExt = this.getMemberExt(this.getOwnerId(), true);
                } else {
                    IClub mainClub = ClubManager.I.getClubByUid(this.getFinalClubId());
                    if (null != mainClub) {
                        ownerClubMemberExt = mainClub.getMemberExt(mainClub.getOwnerId(), true);
                    }
                }

                if (null != ownerClubMemberExt){
                    synchronized (ownerClubMemberExt) {
                        if (value > 0) {
                            ownerClubMemberExt.setUpTotalScore(ownerClubMemberExt.getUpTotalScore() + value);
                        } else {
                            ownerClubMemberExt.setDownTotalScore(ownerClubMemberExt.getDownTotalScore() + Math.abs(value));
                        }
                        ownerClubMemberExt.setDirty(true);
                    }
                }
            }
        }
        return true;
    }

    /**
     * 检查在club中，playerUid不是圈主返还false,如果Club合圈过圈并且club不是主圈返还false
     * @return
     */
    private boolean checkValidChief(IClub club,long playerUid){
        if (club.getOwnerId() != playerUid){
            return false;
        }
        if (club.checkIsJoinInMainClub() && !club.checkIsMainClub()){
            return false;
        }
        return true;
    }

    private void saveClubGoldRecord(long playerUid, int oldValue, int amount, long optPlayerUid, long mainClubUid, long clubUid, long now, EClubGoldChangeType changeType) {
        Logs.CLUB.debug("mainClubUid:%d clubUid:%d playerUid:%d optPlayerUid:%d oldGoldValue:%d, addGoldValue:%d ,optType:%d",mainClubUid,clubUid,playerUid,optPlayerUid,oldValue,amount,changeType.getValue());
        ClubGoldRecord record = new ClubGoldRecord();
        record.setUid(UIDManager.I.getAndInc(UIDType.CLUB_GOLD_RECORD));
        if (amount > 0) {
            record.setInMoney(amount);
        }else {
            record.setOutMoney(-amount);
        }
        record.setMount(amount);
        record.setBeginAmount(oldValue);
        record.setPlayerUid(playerUid);
        record.setOptPlayerUid(optPlayerUid);
        record.setMainClubUid(mainClubUid);
        record.setClubUid(clubUid);
        record.setAction(changeType.getValue());
        record.setCreatedAt(TimeUtil.getZeroTimestamp(now));
        record.setOptTime(now);
        record.setDirty(true);
        record.save();
    }

    private boolean checkChangeValue(long oldValue,long newValue,int changeValue){
        if (changeValue == 0){
            return true;
        }

        if (changeValue > 0){
            if (newValue <= 0) {
                return false;
            }
        }else{
            if (newValue >= oldValue){
                return false;
            }
        }

        if (newValue < 0){
            return false;
        }
        return true;
    }

    /**
     * 增加玩家俱乐部奖励分
     */
    @Override
    public boolean addMemberClubRewardValue(long playerUid, int value, long optPlayerUid, EClubRVChangeType changeType) {
        if (0 == value) {
            return true;
        }

        ClubMemberExt clubMemberExt = this.getMemberExt(playerUid, true);
        long oldValue = 0;
        synchronized (clubMemberExt) {
            oldValue = clubMemberExt.getRewardValue();
            long newValue = oldValue + value;
            if (!this.checkChangeValue(oldValue,newValue,value)){
                return false;
            }

            clubMemberExt.setRewardValue(newValue);
            clubMemberExt.setDirty(true);
            //clubMemberExt.save();
        }

        saveClubRewardValueRecord(playerUid, oldValue, value, optPlayerUid, this.getFinalClubId(), this.getClubUid(), System.currentTimeMillis(), changeType);
        notifyClubMemberValueChange(clubMemberExt);
        //Logs.PLAYER.warn("玩家 %d 增加奖励分 %d, 增加类型 %d, 操作人 %d", playerUid, value, changeType.getType(), optPlayerUid);
        return true;
    }

    @Override
    public void clearGoldActivity() {
        Map<Long, Box> map=this.getAllBox();
        for (Map.Entry<Long, Box> entry : map.entrySet()) {
            ClubActivityManager.I.removeActivityGold(this,entry.getKey());
        }
    }

    private void saveClubRewardValueRecord(long playerUid, long oldValue, int amount, long optPlayerUid, long mainClubUid, long clubUid, long now, EClubRVChangeType changeType) {
        Logs.CLUB.debug("mainClubUid:%d clubUid:%d playerUid:%d optPlayerUid:%d oldRewardValue:%d, addRewardValue:%d ,optType:%d",mainClubUid,clubUid,playerUid,optPlayerUid,oldValue,amount,changeType.ordinal());
        ClubRewardValueRecord record = new ClubRewardValueRecord();
        record.setUid(UIDManager.I.getAndInc(UIDType.CLUB_REWARD_VALUE_RECORD));
        if (amount > 0) {
            record.setInMoney(amount);
        }else {
            record.setOutMoney(-amount);
        }
        record.setMount(amount);
        record.setBeginAmount(oldValue);
        record.setPlayerUid(playerUid);
        record.setOptPlayerUid(optPlayerUid);
        record.setMainClubUid(mainClubUid);
        record.setClubUid(clubUid);
        record.setAction(changeType.ordinal());
        record.setCreatedAt(TimeUtil.getZeroTimestamp(now));
        record.setOptTime(now);
        record.setDirty(true);
        record.save();
    }

    private void notifyClubMemberValueChange(final ClubMemberExt finalClubMemberExt){
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                PCLIClubNtfValueChange info = new PCLIClubNtfValueChange();
                info.clubUid = finalClubMemberExt.getClubUid();
                info.pUid = finalClubMemberExt.getPlayerUid();
                info.gold = finalClubMemberExt.getGold();
                info.rv = finalClubMemberExt.getRewardValue();
                Player player = PlayerManager.I.getOnlinePlayer(info.pUid);
                if (null != player) {
                    player.send(CommandId.CLI_NTF_CLUB_VALUE_CAHNGE, info);
                }
            }
        });
    }

    @Override
    public long[] getTotalGoldAndRewardValueNoChild(){
        long[] result = {0,0};
        this.memberExtForeach(new ICallback<ClubMemberExt>() {
            @Override
            public void call(ClubMemberExt... memberExt) {
                result[0] += memberExt[0].getRewardValue();
                result[1] += memberExt[0].getGold();
            }
        });
        return result;
    }

    @Override
    public long[] getTotalGoldAndRewardValue(){
        long[] result = {0,0};
        this.memberExtForeach(new ICallback<ClubMemberExt>() {
            @Override
            public void call(ClubMemberExt... memberExt) {
                result[0] += memberExt[0].getRewardValue();
                result[1] += memberExt[0].getGold();
            }
        });
        Iterator<Long> it = this.clubInfo.getChildUid().iterator();
        while (it.hasNext()){
            IClub childClub = ClubManager.I.getClubByUid(it.next());
            if (null != childClub){
                long[] childResult = childClub.getTotalGoldAndRewardValue();
                result[0] += childResult[0];
                result[1] += childResult[1];
            }
        }
        return result;
    }

    @Override
    public long[] getAllMemberTotalUpAndDownScore(){
        long[] result = {0,0};
        this.memberExtForeach(new ICallback<ClubMemberExt>() {
            @Override
            public void call(ClubMemberExt... memberExt) {
                result[0] += memberExt[0].getUpTotalScore();
                result[1] += memberExt[0].getDownTotalScore();
            }
        });
        Iterator<Long> it = this.clubInfo.getChildUid().iterator();
        while (it.hasNext()){
            IClub childClub = ClubManager.I.getClubByUid(it.next());
            if (null != childClub){
                long[] childResult = childClub.getAllMemberTotalUpAndDownScore();
                result[0] += childResult[0];
                result[1] += childResult[1];
            }
        }
        return result;
    }

    @Override
    public boolean setLockClub(long lockTime, long lockByClubUid) {
        try {
            if (this.mergeLock.tryLock(10, TimeUnit.MILLISECONDS)) {
                if (lockTime > this.clubInfo.getLockTime()) {
                    this.clubInfo.setLockTime(lockTime);
                    this.clubInfo.setLockByClubUid(lockByClubUid);
                } else {
                    if (lockByClubUid == this.clubInfo.getLockByClubUid()) {
                        this.clubInfo.setLockTime(lockTime);
                    }
                }
                this.clubInfo.setDirty(true);
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (this.mergeLock.isHeldByCurrentThread()) {
                this.mergeLock.unlock();
            }
        }
        return false;
    }

    @Override
    public ErrorCode mergeClub(IClub mergeClub) {
        ErrorCode err = super.mergeClub(mergeClub);
        if (ErrorCode.OK == err) {
            ClubMemberExt mergeMemberExt = mergeClub.getMemberExt(mergeClub.getOwnerId(), false);
            if (null != mergeMemberExt) {
                ClubMemberExt memberExt = this.getMemberExt(this.getOwnerId(), true);
                synchronized (memberExt) {
                    memberExt.setUpTotalScore(memberExt.getUpTotalScore() + mergeMemberExt.getUpTotalScore());
                    memberExt.setDownTotalScore(memberExt.getDownTotalScore() + mergeMemberExt.getDownTotalScore());
                    memberExt.setDirty(true);
                }
                mergeMemberExt.setUpTotalScore(mergeMemberExt.getGold());
                mergeMemberExt.setDownTotalScore(0);
                mergeMemberExt.setDirty(true);
            }
        }
        return err;
    }

    @Override
    public ErrorCode leaveMainClub(IClub leaveClub){
        ErrorCode err = super.leaveMainClub(leaveClub);
        if (ErrorCode.OK == err){
            //set main club
            long[] totalValues = leaveClub.getTotalGoldAndRewardValue();
            ClubMemberExt mainClubChiefMemberExt = this.getMemberExt(this.getOwnerId(),true);
            mainClubChiefMemberExt.setDownTotalScore(mainClubChiefMemberExt.getDownTotalScore() + totalValues[0] + totalValues[1]);
            mainClubChiefMemberExt.setDirty(true);
            // set leave club
            ClubMemberExt leaveClubChiefMemberExt = leaveClub.getMemberExt(leaveClub.getOwnerId(),true);
            long[] totalUpAndDownScore = leaveClub.getAllMemberTotalUpAndDownScore();
            leaveClubChiefMemberExt.setUpTotalScore(totalUpAndDownScore[0]);
            leaveClubChiefMemberExt.setDownTotalScore(totalUpAndDownScore[1]);
            leaveClubChiefMemberExt.setDirty(true);
        }
        return err;
    }

    @Override
    public void onFinishGame(long boxUid, Set<Long> playerIds) {
        for (long playerId : playerIds) {
            long fromClubUid = getEnterFromClubUid(playerId);
            ClubActivity clubActivity = ClubActivityManager.I.getAndSetActivity(fromClubUid, EClubActivityType.GOLD);
            if (null == clubActivity) {
                continue;
            }
            Map<Long, ClubActivityGoldData> goldData = clubActivity.getGoldData();
            if (!goldData.containsKey(boxUid)) {
                continue;
            }
            IClub fromClub =ClubManager.I.getClubByUid(fromClubUid);
            ClubMember member = fromClub.getMember(playerId);
            if (member == null) {
                continue;
            }
            synchronized (member) {
                Map<Long, Integer> goldActivityCount = member.getGoldActivityCount();
                Integer value = goldActivityCount.get(boxUid);
                if (value != null) {
                    value += 1;
                } else {
                    value = 1;
                }
                goldActivityCount.put(boxUid, value);
                member.setDirty(Boolean.TRUE);
            }
        }
    }
}