package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.EShowFlag;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultRecordAction extends RecordAction {
    public static class ScoreInfo {
        protected int barCnt;              // 杠次数
        protected int gangScore;           // 杠分
        protected int fangScore;           // 牌型分
        protected int huScore;             // 胡分
        protected int piaoScore;           // 飘分
        protected String score;            // 总分
        protected String totalScore;       // 目前为止总分

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

        public int getHuScore() {
            return huScore;
        }

        public void setHuScore(int huScore) {
            this.huScore = huScore;
        }

        public int getPiaoScore() {
            return piaoScore;
        }

        public void setPiaoScore(int piaoScore) {
            this.piaoScore = piaoScore;
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

    public static class HuInfo {
        protected boolean ziMo;            // 是否自摸
        protected byte huCard;             // 胡牌
        protected int paiXing;             // 牌型
        protected int fang;                // 番数
        protected long takePlayerUid;      // 打牌玩家uid

        public boolean isZiMo() {
            return ziMo;
        }

        public void setZiMo(boolean ziMo) {
            this.ziMo = ziMo;
        }

        public byte getHuCard() {
            return huCard;
        }

        public void setHuCard(byte huCard) {
            this.huCard = huCard;
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

        public long getTakePlayerUid() {
            return takePlayerUid;
        }

        public void setTakePlayerUid(long takePlayerUid) {
            this.takePlayerUid = takePlayerUid;
        }
    }

    public static class PlayerInfo {
        protected ScoreInfo scoreInfo;
        protected List<HuInfo> allHuInfo = new ArrayList<>();           // 所有胡
        protected List<String> allShow = new ArrayList<>();             // 所有显示牌型

        public void addHuInfo(HuInfo huInfo) {
            this.allHuInfo.add(huInfo);
        }

        public void addAllClientShow(List<EShowFlag> allShow) {
            for (EShowFlag flag : allShow) {
                this.allShow.add(flag.getDesc());
            }
        }

        public ScoreInfo getScoreInfo() {
            return scoreInfo;
        }

        public void setScoreInfo(ScoreInfo scoreInfo) {
            this.scoreInfo = scoreInfo;
        }

        public List<HuInfo> getAllHuInfo() {
            return allHuInfo;
        }

        public void setAllHuInfo(List<HuInfo> allHuInfo) {
            this.allHuInfo = allHuInfo;
        }

        public List<String> getAllShow() {
            return allShow;
        }

        public void setAllShow(List<String> allShow) {
            this.allShow = allShow;
        }
    }

    protected HashMap<Long, PlayerInfo> allPlayer = new HashMap<>();   // 所有玩家信息

    public ResultRecordAction() {
        super(EActionOp.RESULT, -1);
    }

    public void addResult(long playerUid, PlayerInfo playerInfo) {
        this.allPlayer.put(playerUid, playerInfo);
    }

    public HashMap<Long, PlayerInfo> getAllPlayer() {
        return allPlayer;
    }

    public void setAllPlayer(HashMap<Long, PlayerInfo> allPlayer) {
        this.allPlayer = allPlayer;
    }
}
