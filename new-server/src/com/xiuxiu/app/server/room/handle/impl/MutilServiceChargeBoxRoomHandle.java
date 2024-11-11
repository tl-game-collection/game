package com.xiuxiu.app.server.room.handle.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.AbstractBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

/**
 * 每小局返利费的包厢房间处理器
 * 
 * @author Administrator
 *
 */
public class MutilServiceChargeBoxRoomHandle extends AbstractBoxRoomHandle {

    /** 抽水 */
    protected int costModelValue;
    
    /** 大赢家抽成 */
    protected int winServiceCharge;
    /** 其他人抽成 */
    protected int otherServiceCharge;
    /** 大赢家额外高于分 */
    protected int winExtraCondition;
    /** 大赢家额外高于分抽成x */
    protected int winExtraServiceCharge;
    /** 大赢家分低于x免单 */
    protected int winExtraConditionNotServiceCHarge;

    public MutilServiceChargeBoxRoomHandle(IRoom room, Box box) {
        super(room, box);
    }

    @Override
    public void init() {
        super.init();
        Map<String, Integer> rule = getRoom().getRule();
        this.costModel = rule.getOrDefault(RoomRule.RR_COSTMODEL, 0);
        this.costModelValue = rule.getOrDefault(RoomRule.RR_COSTMODEL_VALUE, 0);
        if (this.costModel == 3) {
            this.winServiceCharge = rule.getOrDefault(RoomRule.RR_PAIGOW_WIN_SERVICE_CHARGE, 0);
            this.otherServiceCharge = rule.getOrDefault(RoomRule.RR_PAIGOW_OTHER_SERVICE_CHARGE, 0);
            this.winExtraCondition = rule.getOrDefault(RoomRule.RR_PAIGOW_WIN_EXTRA_CONDITION, 0);
            this.winExtraServiceCharge = rule.getOrDefault(RoomRule.RR_PAIGOW_WIN_EXTRA_SERVICE_CHARGE, 0);
            this.winExtraConditionNotServiceCHarge = rule
                    .getOrDefault(RoomRule.RR_PAIGOW_WIN_EXTRA_CONDITION_NOT_SERVICE_CHARGE, 0);
        }
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
        if (this.costModel == 3) {
            long now = System.currentTimeMillis();
            IClub mainClub = (IClub)boxOwner;
            for (int i = 0, len = room.getMaxPlayerCnt(); i < len; ++i) {
                IRoomPlayer temp = (IRoomPlayer) room.getRoomPlayer(i);
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                if (otherServiceCharge > 0) {
                    int serviceValue = otherServiceCharge;
                    // 扣水，返回实际抽水值
                    serviceValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()), temp.getUid(), -serviceValue, 0);
                    boxOwner.divideServiceCharge(box.getUid(), temp.getUid(), serviceValue*100, now);
                }
            }
        } else if (costModel == 2 && costModelValue > 0) {
            List<Long> playerUidList = new ArrayList<>();
            int maxScore = 0;
            for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
                IRoomPlayer temp = this.room.getRoomPlayer(i);
                if (null == temp || temp.isGuest()) {
                    continue;
                }
    
                int score = temp.getScore(Score.SCORE, false);
                if (score > 0) {
                    playerUidList.add(temp.getUid());
                }
            }
            // 赢家抽水
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
            // 赢家
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
   
}
