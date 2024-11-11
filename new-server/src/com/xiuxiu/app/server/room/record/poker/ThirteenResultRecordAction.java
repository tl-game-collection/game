package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThirteenResultRecordAction extends RecordAction {

    public ThirteenResultRecordAction() {
        super(EActionOp.RESULT, -1);
    }

    public static class GameOverInfo {
        protected List<Byte> card = new ArrayList<>();                 // 剩余手牌
        protected String score;                                        // 本局积分
        protected String totalScore;                                   // 总积分
        protected String headScore;                                    // 牌类型
        protected String medScore;
        protected String tailScore;

        public List<Byte> getCard() {
            return card;
        }

        public void setCard(List<Byte> card) {
            this.card = card;
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

        public String getHeadScore() {
            return headScore;
        }

        public void setHeadScore(String headScore) {
            this.headScore = headScore;
        }

        public String getMedScore() {
            return medScore;
        }

        public void setMedScore(String medScore) {
            this.medScore = medScore;
        }

        public String getTailScore() {
            return tailScore;
        }

        public void setTailScore(String tailScore) {
            this.tailScore = tailScore;
        }

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "card=" + card +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", headScore=" + headScore +
                    ", medScore=" + medScore +
                    ", tailScore=" + tailScore +
                    '}';
        }
    }

    protected HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();

    protected int hotDeskNote;                                          //端火锅 桌面上的筹码；

    public int getHotDeskNote() {
        return hotDeskNote;
    }

    public void setHotDeskNote(int hotDeskNote) {
        this.hotDeskNote = hotDeskNote;
    }

    public void addResult(long playerUid, List<Byte> card, String score, String totalScore,String hCardType,String mCardType,String tCardType) {
        GameOverInfo info = new GameOverInfo();
        info.card.addAll(card);
        info.score = score;
        info.totalScore = totalScore;
        info.headScore = hCardType;
        info.medScore = mCardType;
        info.tailScore = tCardType;
        this.allGameOverInfo.put(playerUid, info);
    }


    public HashMap<Long, GameOverInfo> getAllGameOverInfo() {
        return allGameOverInfo;
    }

    public void setAllGameOverInfo(HashMap<Long, GameOverInfo> allGameOverInfo) {
        this.allGameOverInfo = allGameOverInfo;
    }

    @Override
    public String toString() {
        return "ThirteenResultRecordAction{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", hotDeskNote=" + hotDeskNote +
                '}';
    }
}
