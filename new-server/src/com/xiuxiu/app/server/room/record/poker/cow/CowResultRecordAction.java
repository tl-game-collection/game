package com.xiuxiu.app.server.room.record.poker.cow;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther: yuyunfei
 * @date: 2020/1/6 18:25
 * @comment:
 */
public class CowResultRecordAction extends RecordAction {
    public static class GameOverInfo {
        private List<Byte> card = new ArrayList<>();                 // 剩余手牌
        private String score;                                        // 本局积分
        private String totalScore;                                   // 总积分
        private int cardType;                                        // 牌类型

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

    private ConcurrentHashMap<Long, GameOverInfo> allGameOverInfo = new ConcurrentHashMap<>();

    private int hotDeskNote;                                          //端火锅 桌面上的筹码；

    public int getHotDeskNote() {
        return hotDeskNote;
    }

    public void setHotDeskNote(int hotDeskNote) {
        this.hotDeskNote = hotDeskNote;
    }

    public CowResultRecordAction() {

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

    public ConcurrentHashMap<Long, GameOverInfo> getAllGameOverInfo() {
        return allGameOverInfo;
    }

    public void setAllGameOverInfo(ConcurrentHashMap<Long, GameOverInfo> allGameOverInfo) {
        this.allGameOverInfo = allGameOverInfo;
    }
}
