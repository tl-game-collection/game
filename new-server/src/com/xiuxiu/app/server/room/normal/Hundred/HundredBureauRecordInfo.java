package com.xiuxiu.app.server.room.normal.Hundred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;
/**
 * 百人场局数记录
 * @author Administrator
 *
 */
public class HundredBureauRecordInfo extends BaseTable {

    private static final long serialVersionUID = 3358429468560206134L;

    /**
     * 牌信息
     * @author Administrator
     *
     */
    public static class CardInfo {
        protected int cardType;
        protected boolean win;
        protected int value;//庄的输赢 输为负数
        protected Map<Integer,Integer> rebs=new HashMap<>();
        protected List<Byte> cards = new ArrayList<>();
        public int getCardType() {
            return cardType;
        }
        public void setCardType(int cardType) {
            this.cardType = cardType;
        }
		public boolean isWin() {
            return win;
        }

        public void setWin(boolean win) {
            this.win = win;
        }

        public Map<Integer, Integer> getRebs() {
			return rebs;
		}
		public void setRebs(Map<Integer, Integer> rebs) {
			this.rebs = rebs;
		}
		public List<Byte> getCards() {
            return cards;
        }

        public void setCards(List<Byte> cards) {
            this.cards = cards;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "CardInfo{" +
                    "cardType=" + cardType +
                    ", win=" + win +
                    ", value=" + value +
                    ", cards=" + cards +
                    ", rebs=" + rebs +
                    '}';
        }
    }

    protected long roomUid;
    protected int roomId;
    protected long time;
    protected long endTime;
    protected List<CardInfo> cardInfo = new ArrayList<>();
    protected long bankerPlayerUid;
    protected int bankerWinValue;

    public HundredBureauRecordInfo() {
        this.tableType = ETableType.TB_HUNDRED_BUREAU_RECORD;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<CardInfo> getCardInfo() {
        return this.cardInfo;
    }

    public void setCardInfo(List<CardInfo> cardInfo) {
        this.cardInfo = cardInfo;
    }

    public String getCardInfoDb() {
        return JsonUtil.toJson(this.cardInfo);
    }

    public void setCardInfoDb(String cardInfo) {
        if (StringUtil.isEmptyOrNull(cardInfo)) {
            return;
        }
        List<CardInfo> temp = JsonUtil.fromJson(cardInfo, new TypeReference<ArrayList<CardInfo>>() {});
        if (null != temp) {
            this.cardInfo = temp;
        }
    }

    public long getBankerPlayerUid() {
        return bankerPlayerUid;
    }

    public void setBankerPlayerUid(long bankerPlayerUid) {
        this.bankerPlayerUid = bankerPlayerUid;
    }

    public int getBankerWinValue() {
        return bankerWinValue;
    }

    public void setBankerWinValue(int bankerWinValue) {
        this.bankerWinValue = bankerWinValue;
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
        return "HundredBureauRecordInfo{" +
                "roomUid=" + roomUid +
                ", roomId=" + roomId +
                ", time=" + time +
                ", endTime=" + endTime +
                ", cardInfo=" + cardInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerWinValue=" + bankerWinValue +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
