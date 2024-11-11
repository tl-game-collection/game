package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SGResultRecordAction extends RecordAction {
    public static class GameOverInfo {
        protected List<Byte> card = new ArrayList<>();                 // 剩余手牌
        protected String score;                                        // 本局积分
        protected String totalScore;                                   // 总积分
        protected int cardType;                                         // 牌类型

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

        public int getCardType() {
            return cardType;
        }

        public void setCardType(int cardType) {
            this.cardType = cardType;
        }
    }

    protected HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();

    public SGResultRecordAction() {

        super(EActionOp.RESULT, -1);
    }

    public void addResult(long playerUid, List<Byte> card, String score, String totalScore, int cardType) {
        GameOverInfo info = new GameOverInfo();
        info.card.addAll(card);
        info.score = score;
        info.totalScore = totalScore;
        info.cardType = cardType;
        this.allGameOverInfo.put(playerUid, info);
    }

    public HashMap<Long, GameOverInfo> getAllGameOverInfo() {
        return allGameOverInfo;
    }

    public void setAllGameOverInfo(HashMap<Long, GameOverInfo> allGameOverInfo) {
        this.allGameOverInfo = allGameOverInfo;
    }
}
