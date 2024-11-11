package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.player.IHundredPlayer;

public class HundredLhdPlayer extends PokerPlayer implements IHundredPlayer {

    /**
     * 上庄次数
     */
    private int bankerCnt = 0;
    /**
     * vip座位索引
     */
    private int vipSeatIndex = -1;
    /**
     * 连续没下注的轮数
     */
    private int noRebRound = 0;
    /**
     * 返利值(VIP座位上下注才有)
     */
    private int vipSeatFanliValue = 0;
    
    public HundredLhdPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public void incBankerCnt() {
        ++this.bankerCnt;
    }

    @Override
    public void decBankerCnt() {
        --this.bankerCnt;
    }

    @Override
    public boolean isBanker() {
        return this.bankerCnt > 0;
    }

    @Override
    public void setVipSeatIndex(int index) {
        this.vipSeatIndex = index;
    }

    @Override
    public int getVipSeatIndex() {
        return this.vipSeatIndex;
    }

    @Override
    public void setNoRebRound(int round) {
        this.noRebRound = round;
    }

    @Override
    public int getNoRebRound() {
        return this.noRebRound;
    }

    @Override
    public void setVipSeatFanliValue(int value) {
        this.vipSeatFanliValue = value;
    }

    @Override
    public int getVipSeatFanliValue() {
        return this.vipSeatFanliValue;
    }
    /**
     * 获取玩家竞技值
     * @param room
     * @return
     */
    @Override
    public long getGold(IRoom room) {
        IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
        if (club != null) {
            long tempClubUid = club.getEnterFromClubUid(this.playerUid);
            if (tempClubUid != club.getClubUid()) {
                IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                if (tempClub != null) {
                    club = tempClub;
                }
            }
            return club.getGold(this.playerUid);
        }
        return 0;
    }
    /**
     * 检查玩家竞技值是否足够
     * @param room
     * @param value 实际值*100
     * @return
     */
    @Override
    public boolean checkEnoughGold(IRoom room, long value) {
        IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
        if (club != null) {
            long tempClubUid = club.getEnterFromClubUid(this.playerUid);
            if (tempClubUid != club.getClubUid()) {
                IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                if (tempClub != null) {
                    club = tempClub;
                }
            }
            return club.getGold(this.playerUid) >= value;
        }
        return false;
    }

    @Override
    public void clear() {
        this.operationTimeoutCnt = 0;
        this.isAutoMode = false;
        this.score.clear();
        this.over = false;


    }
}
