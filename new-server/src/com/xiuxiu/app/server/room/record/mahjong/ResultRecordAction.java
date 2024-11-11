package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultRecordAction extends RecordAction {
    public static class ScoreInfo {
        protected int barCnt;              // 杠次数
        protected int gangScore;           // 杠分
        protected int fangScore;           // 牌型分
        protected int horseScore;          // 马分
        protected int piaoScore;           // 飘分
        protected int huScore;             // 胡分
        protected String score;            // 总分
        protected String totalScore;       // 目前为止总分

        public ScoreInfo() {

        }

        public ScoreInfo(int barCnt, int gangScore, int fangScore, int horseScore, int piaoScore, int huScore, String score, String totalScore) {
            this.barCnt = barCnt;
            this.gangScore = gangScore;
            this.fangScore = fangScore;
            this.horseScore = horseScore;
            this.piaoScore = piaoScore;
            this.huScore = huScore;
            this.score = score;
            this.totalScore = totalScore;
        }

        public int getBarCnt() {
            return barCnt;
        }

        public void setBarCnt(int barCnt) {
            this.barCnt = barCnt;
        }

        public int getGangScore() {
            return gangScore;
        }

        public void setGangScore(int gangScore) {
            this.gangScore = gangScore;
        }

        public int getFangScore() {
            return fangScore;
        }

        public void setFangScore(int fangScore) {
            this.fangScore = fangScore;
        }

        public int getHorseScore() {
            return horseScore;
        }

        public void setHorseScore(int horseScore) {
            this.horseScore = horseScore;
        }

        public int getPiaoScore() {
            return piaoScore;
        }

        public void setPiaoScore(int piaoScore) {
            this.piaoScore = piaoScore;
        }

        public int getHuScore() {
            return huScore;
        }

        public void setHuScore(int huScore) {
            this.huScore = huScore;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(String totalScore) {
            this.totalScore = totalScore;
        }
    }

    protected HashMap<Long, List<Byte>> allCard = new HashMap<>();
    protected HashMap<Long, List<Byte>> allCardKou = new HashMap<>();
    protected HashMap<Long, HashMap<Integer, Integer>> allPaiXing = new HashMap<>();
    protected HashMap<Long, ScoreInfo> allScore = new HashMap<Long, ScoreInfo>();
    protected List<Long> huPlayerUids = new ArrayList<>();
    protected List<Integer> buyHorse = new ArrayList<>();
    protected int buyHorseScore;
    protected long fangPaoUid = -1;
    protected long ziMoUid = -1;
    protected byte huCard = -1;
    protected boolean huangZhuang = false;
    protected boolean chaDaJiao = false;
    protected boolean huangZhuangPeiFu = false;
    protected List<Long> chaDaJiaoList = new ArrayList<>();
    protected long destroyUid = -1;
    protected boolean isDestroy = false;

    public ResultRecordAction() {
        super(EActionOp.RESULT, -1);
    }

    public void addCard(long playerUid, List<Byte> card) {
        this.allCard.put(playerUid, card);
    }

    public void addCardKou(long playerUid, List<Byte> kou) {
        this.allCardKou.put(playerUid, kou);
    }

    public void addPaiXing(long playerUid, HashMap<Integer, Integer> paiXing) {
        this.allPaiXing.put(playerUid, paiXing);
    }

    public void addScore(long playerUid, ScoreInfo scoreInfo) {
        this.allScore.put(playerUid, scoreInfo);
    }

    public void addHu(List<Long> huList) {
        if (null != huList) {
            this.huPlayerUids.addAll(huList);
        }
    }

    public void addBuyHorse(List<Integer> horseList) {
        if (null != horseList) {
            this.buyHorse.addAll(horseList);
        }
    }

    public void addCahDaJiao(List<Long> chaDaJiaoList) {
        if (null != chaDaJiaoList) {
            this.chaDaJiaoList.addAll(chaDaJiaoList);
        }
    }

    public HashMap<Long, List<Byte>> getAllCard() {
        return allCard;
    }

    public void setAllCard(HashMap<Long, List<Byte>> allCard) {
        this.allCard = allCard;
    }

    public HashMap<Long, List<Byte>> getAllCardKou() {
        return allCardKou;
    }

    public void setAllCardKou(HashMap<Long, List<Byte>> allCardKou) {
        this.allCardKou = allCardKou;
    }

    public HashMap<Long, HashMap<Integer, Integer>> getAllPaiXing() {
        return allPaiXing;
    }

    public void setAllPaiXing(HashMap<Long, HashMap<Integer, Integer>> allPaiXing) {
        this.allPaiXing = allPaiXing;
    }

    public HashMap<Long, ScoreInfo> getAllScore() {
        return allScore;
    }

    public void setAllScore(HashMap<Long, ScoreInfo> allScore) {
        this.allScore = allScore;
    }

    public List<Long> getHuPlayerUids() {
        return huPlayerUids;
    }

    public void setHuPlayerUids(List<Long> huPlayerUids) {
        this.huPlayerUids = huPlayerUids;
    }

    public List<Integer> getBuyHorse() {
        return buyHorse;
    }

    public void setBuyHorse(List<Integer> buyHorse) {
        this.buyHorse = buyHorse;
    }

    public int getBuyHorseScore() {
        return buyHorseScore;
    }

    public void setBuyHorseScore(int buyHorseScore) {
        this.buyHorseScore = buyHorseScore;
    }

    public long getFangPaoUid() {
        return fangPaoUid;
    }

    public void setFangPaoUid(long fangPaoUid) {
        this.fangPaoUid = fangPaoUid;
    }

    public long getZiMoUid() {
        return ziMoUid;
    }

    public void setZiMoUid(long ziMoUid) {
        this.ziMoUid = ziMoUid;
    }

    public byte getHuCard() {
        return huCard;
    }

    public void setHuCard(byte huCard) {
        this.huCard = huCard;
    }

    public boolean isHuangZhuang() {
        return huangZhuang;
    }

    public void setHuangZhuang(boolean huangZhuang) {
        this.huangZhuang = huangZhuang;
    }

    public boolean isChaDaJiao() {
        return chaDaJiao;
    }

    public void setChaDaJiao(boolean chaDaJiao) {
        this.chaDaJiao = chaDaJiao;
    }

    public boolean isHuangZhuangPeiFu() {
        return huangZhuangPeiFu;
    }

    public void setHuangZhuangPeiFu(boolean huangZhuangPeiFu) {
        this.huangZhuangPeiFu = huangZhuangPeiFu;
    }

    public List<Long> getChaDaJiaoList() {
        return chaDaJiaoList;
    }

    public void setChaDaJiaoList(List<Long> chaDaJiaoList) {
        this.chaDaJiaoList = chaDaJiaoList;
    }

    public long getDestroyUid() {
        return destroyUid;
    }

    public void setDestroyUid(long destroyUid) {
        this.destroyUid = destroyUid;
    }

    public boolean isDestroy() {
        return isDestroy;
    }

    public void setDestroy(boolean destroy) {
        isDestroy = destroy;
    }
}
