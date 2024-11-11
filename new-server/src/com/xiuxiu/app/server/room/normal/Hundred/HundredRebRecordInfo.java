package com.xiuxiu.app.server.room.normal.Hundred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

public class HundredRebRecordInfo extends BaseTable {
    public static class RebInfo {
        protected int rebValue;
        protected int winValue;

        public int getRebValue() {
            return rebValue;
        }

        public void setRebValue(int rebValue) {
            this.rebValue = rebValue;
        }

        public int getWinValue() {
            return winValue;
        }

        public void setWinValue(int winValue) {
            this.winValue = winValue;
        }

        @Override
        public String toString() {
            return "RebInfo{" +
                    "rebValue=" + rebValue +
                    ", winValue=" + winValue +
                    '}';
        }
    }

    public static class AllRebInfo {
        protected HashMap<EHundredArenaRebType, RebInfo> allReb = new HashMap<>();
        protected int cardType;
        protected List<Byte> cards = new ArrayList<>();
        
        public void addRebInfo(EHundredArenaRebType type, RebInfo info) {
            allReb.put(type, info);
        }

        public HashMap<EHundredArenaRebType, RebInfo> getAllReb() {
            return allReb;
        }

        public void setAllReb(HashMap<EHundredArenaRebType, RebInfo> allReb) {
            this.allReb = allReb;
        }

        public int getCardType() {
            return cardType;
        }

        public void setCardType(int cardType) {
            this.cardType = cardType;
        }

        public List<Byte> getCards() {
            return cards;
        }

        public void setCards(List<Byte> cards) {
            this.cards = cards;
        }

        @Override
        public String toString() {
            return "AllRebInfo{" +
                    "allReb=" + allReb +
                    ", cardType=" + cardType +
                    ", cards=" + cards +
                    '}';
        }
    }

    protected long roomUid;
    protected int roomId;
    protected long clubUid;
    protected long rebPlayerUid;
    protected long time;
    protected List<AllRebInfo> rebInfo = new ArrayList<>();
    protected int bankerCardType;
    protected int rebValue;
    protected int winValue;
    protected int fanliValue = 0;//返利值
    protected int gameType = 0;//对应游戏类型

    public HundredRebRecordInfo() {
        this.tableType = ETableType.TB_HUNDRED_REB_RECORD;
    }

    public long getRebPlayerUid() {
        return rebPlayerUid;
    }

    public void setRebPlayerUid(long rebPlayerUid) {
        this.rebPlayerUid = rebPlayerUid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<AllRebInfo> getRebInfo() {
        return rebInfo;
    }

    public void setRebInfo(List<AllRebInfo> rebInfo) {
        this.rebInfo = rebInfo;
    }

    public String getRebInfoDb() {
        return JsonUtil.toJson(this.rebInfo);
    }

    public void setRebInfoDb(String rebInfo) {
        if (StringUtil.isEmptyOrNull(rebInfo)) {
            return;
        }
        List<AllRebInfo> temp = JsonUtil.fromJson(rebInfo, new TypeReference<ArrayList<AllRebInfo>>() {});
        if (null != temp) {
            this.rebInfo = temp;
        }
    }

    public int getBankerCardType() {
        return bankerCardType;
    }

    public void setBankerCardType(int bankerCardType) {
        this.bankerCardType = bankerCardType;
    }

    public int getRebValue() {
        return rebValue;
    }

    public void setRebValue(int rebValue) {
        this.rebValue = rebValue;
    }

    public int getWinValue() {
        return winValue;
    }

    public void setWinValue(int winValue) {
        this.winValue = winValue;
    }

    public int getFanliValue() {
        return this.fanliValue;
    }

    public void setFanliValue(int fanliValue) {
        this.fanliValue = fanliValue;
    }
    
    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public int getGameType() {
        return this.gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public long getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(long roomUid) {
        this.roomUid = roomUid;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "HundredRebRecordInfo{" +
                "roomUid=" + roomUid +
                ", roomId=" + roomId +
                ", clubUid=" + clubUid +
                ", rebPlayerUid=" + rebPlayerUid +
                ", time=" + time +
                ", rebInfo=" + rebInfo +
                ", bankerCardType=" + bankerCardType +
                ", fanliValue=" + fanliValue +
                ", gameType=" + gameType +
                ", rebValue=" + rebValue +
                ", winValue=" + winValue +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
