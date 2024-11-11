package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.EPaiXing;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class HuRecordAction extends RecordAction {
    public static class HuInfo {
        protected boolean ziMo;
        protected int paiXing = EPaiXing.NONE.getClientValue();
        protected int fang;
        protected byte huCard;

        public HuInfo() {
        }

        public HuInfo(boolean ziMo, EPaiXing paiXing, byte huCard, int fang) {
            this.ziMo = ziMo;
            this.paiXing = paiXing.getClientValue();
            this.huCard = huCard;
            this.fang = fang;
        }
        
        public boolean isZiMo() {
            return ziMo;
        }

        public void setZiMo(boolean ziMo) {
            this.ziMo = ziMo;
        }

        public int getPaiXing() {
            return paiXing;
        }

        public void setPaiXing(int paiXing) {
            this.paiXing = paiXing;
        }

        public int getFang() {
            return fang;
        }

        public void setFang(int fang) {
            this.fang = fang;
        }

        public byte getHuCard() {
            return huCard;
        }

        public void setHuCard(byte huCard) {
            this.huCard = huCard;
        }
    }

    protected long takePlayerUid;
    protected HashMap<Long, HuInfo> allHu = new HashMap<>();
    protected HashMap<Long, Integer> allScore = new HashMap<>();

    public HuRecordAction(long takePlayerUid) {
        super(EActionOp.HU, -1);
        this.takePlayerUid = takePlayerUid;
    }

    public void addHu(long playerUid, boolean ziMo, EPaiXing px, byte huCard, int value) {
        this.allHu.put(playerUid, new HuInfo(ziMo, px, huCard, value));
    }

    public void addScore(long playerUid, int score) {
        this.allScore.put(playerUid, this.allScore.getOrDefault(playerUid, 0) + score);
    }

    public long getTakePlayerUid() {
        return takePlayerUid;
    }

    public void setTakePlayerUid(long takePlayerUid) {
        this.takePlayerUid = takePlayerUid;
    }

    public HashMap<Long, HuInfo> getAllHu() {
        return allHu;
    }

    public void setAllHu(HashMap<Long, HuInfo> allHu) {
        this.allHu = allHu;
    }

    public HashMap<Long, Integer> getAllScore() {
        return allScore;
    }

    public void setAllScore(HashMap<Long, Integer> allScore) {
        this.allScore = allScore;
    }
}
