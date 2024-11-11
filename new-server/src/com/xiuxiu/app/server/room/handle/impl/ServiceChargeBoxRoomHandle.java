package com.xiuxiu.app.server.room.handle.impl;

import java.util.Map;

import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.handle.AbstractBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

/**
 * 每小局返利费的包厢房间处理器
 * 
 * @author Administrator
 *
 */
public class ServiceChargeBoxRoomHandle extends AbstractBoxRoomHandle {

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

    public ServiceChargeBoxRoomHandle(IRoom room, Box box) {
        super(room, box);
    }

    @Override
    public void init() {
        super.init();
        Map<String, Integer> rule = getRoom().getRule();
        this.winServiceCharge = rule.getOrDefault(RoomRule.RR_PAIGOW_WIN_SERVICE_CHARGE, 0);
        this.otherServiceCharge = rule.getOrDefault(RoomRule.RR_PAIGOW_OTHER_SERVICE_CHARGE, 0);
        this.winExtraCondition = rule.getOrDefault(RoomRule.RR_PAIGOW_WIN_EXTRA_CONDITION, 0);
        this.winExtraServiceCharge = rule.getOrDefault(RoomRule.RR_PAIGOW_WIN_EXTRA_SERVICE_CHARGE, 0);
        this.winExtraConditionNotServiceCHarge = rule
                .getOrDefault(RoomRule.RR_PAIGOW_WIN_EXTRA_CONDITION_NOT_SERVICE_CHARGE, 0);
    }

    @Override
    protected void doServiceCharge(IBoxOwner boxOwner) {
        if (room.getGameType() != GameType.GAME_TYPE_KWX && room.getGameType() != GameType.GAME_TYPE_RUN_FAST && room.getGameType() != GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER 
                && room.getGameType() != GameType.GAME_TYPE_COW&& room.getGameType() != GameType.GAME_TYPE_THIRTEEN && room.getGameType() != GameType.GAME_TYPE_PAIGOW
            && room.getGameType() != GameType.GAME_TYPE_WHMJ && room.getGameType() != GameType.GAME_TYPE_HSMJ && room.getGameType() != GameType.GAME_TYPE_YXMJ 
            && room.getGameType() != GameType.GAME_TYPE_LYKD && room.getGameType() != GameType.GAME_TYPE_LANDLORD && room.getGameType() !=  GameType.GAME_TYPE_SG
            && room.getGameType() != GameType.GAME_TYPE_HZMJ){
            return;
        }
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
    }
    /**
    @Override
    protected void doServiceCharge(IBoxOwner boxOwner, List<Long> maxPlayerUidList, int maxScore) {
        // 大赢家分低于x免单
        if (winExtraConditionNotServiceCHarge > 0 && winExtraConditionNotServiceCHarge > maxScore) {
            return;
        }

        long now = System.currentTimeMillis();
        IClub mainClub = (IClub) boxOwner;
        for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
            IRoomPlayer temp = this.room.getRoomPlayer(i);
            if (null == temp || temp.isGuest()) {
                continue;
            }
            // 小局输分(结算分)
            int score = temp.getScore(Score.SCORE, false);

            // 大赢家
            if (maxPlayerUidList.contains(temp.getUid())) {
                int serviceValue = winServiceCharge;
                if (score > winExtraCondition) {
                    serviceValue += winExtraServiceCharge;
                }
                if (serviceValue > 0) {
                    // 扣水，返回实际抽水值
                    serviceValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                            temp.getUid(), -serviceValue, 0);
                    boxOwner.divideServiceCharge(box.getUid(), temp.getUid(), serviceValue, now);
                }
            } else if (otherServiceCharge > 0) {
                int serviceValue = otherServiceCharge;
                // 扣水，返回实际抽水值
                serviceValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()), temp.getUid(),
                        -serviceValue, 0);
                boxOwner.divideServiceCharge(box.getUid(), temp.getUid(), serviceValue, now);
            }
        }
    }
    */
}
