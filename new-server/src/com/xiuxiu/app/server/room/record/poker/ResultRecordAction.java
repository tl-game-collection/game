package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultRecordAction extends RecordAction {
    public static class GameOverInfo {
        protected List<Byte> card = new ArrayList<>();                 // 剩余手牌
        protected String score;                                        // 本局积分
        protected String totalScore;                                   // 总积分
        protected TotalCnt totalCnt;                                   // 大结算计数
        protected boolean isCloseDoor;                                 // 是否关门
        protected HashMap<Long,Integer> bombScore;                     // 炸弹分数
        protected Integer valueB;                                      // 是否加倍
        protected Integer playerScore;                                 // 玩家叫的分数
        protected Integer bankerIndex;                                 // 庄的索引
        protected int curHotDeskNote;                                  // 当前锅底(加锅牌九)
        protected int curLoop;                                         // 当前轮数(加锅牌九)
        protected boolean isDissolve;                                  // 是否是解散房间的
        protected long dissolveUid;                                    // 请求解散房间的玩家id

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

        public TotalCnt getTotalCnt() {
            return totalCnt;
        }

        public void setTotalCnt(TotalCnt totalCnt) {
            this.totalCnt = totalCnt;
        }

        public boolean isCloseDoor() {
            return isCloseDoor;
        }

        public void setCloseDoor(boolean closeDoor) {
            isCloseDoor = closeDoor;
        }

        public Integer getValueB() {
            return valueB;
        }

        public void setValueB(Integer valueB) {
            this.valueB = valueB;
        }

        public HashMap<Long, Integer> getBombScore() {
            return bombScore;
        }

        public void setBombScore(HashMap<Long, Integer> bombScore) {
            this.bombScore = bombScore;
        }

        public Integer getPlayerScore() {
            return playerScore;
        }

        public void setPlayerScore(Integer playerScore) {
            this.playerScore = playerScore;
        }

        public Integer getBankerIndex() {
            return bankerIndex;
        }

        public void setBankerIndex(Integer bankerIndex) {
            this.bankerIndex = bankerIndex;
        }

        public int getCurHotDeskNote() {
            return curHotDeskNote;
        }

        public void setCurHotDeskNote(int curHotDeskNote) {
            this.curHotDeskNote = curHotDeskNote;
        }

        public int getCurLoop() {
            return curLoop;
        }

        public void setCurLoop(int curLoop) {
            this.curLoop = curLoop;
        }

        public boolean isDissolve() {
            return isDissolve;
        }

        public void setDissolve(boolean dissolve) {
            isDissolve = dissolve;
        }

        public long getDissolveUid() {
            return dissolveUid;
        }

        public void setDissolveUid(long dissolveUid) {
            this.dissolveUid = dissolveUid;
        }
    }

    public static class TotalCnt {
        protected int winCnt;
        protected int lostCnt;
        protected int bombCnt;

        public int getWinCnt() {
            return winCnt;
        }

        public void setWinCnt(int winCnt) {
            this.winCnt = winCnt;
        }

        public int getLostCnt() {
            return lostCnt;
        }

        public void setLostCnt(int lostCnt) {
            this.lostCnt = lostCnt;
        }

        public int getBombCnt() {
            return bombCnt;
        }

        public void setBombCnt(int bombCnt) {
            this.bombCnt = bombCnt;
        }
    }

    protected HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();

    public ResultRecordAction() {
        super(EActionOp.RESULT, -1);
    }

    public HashMap<Long, GameOverInfo> getAllGameOverInfo() {
        return allGameOverInfo;
    }

    public void setAllGameOverInfo(HashMap<Long, GameOverInfo> allGameOverInfo) {
        this.allGameOverInfo = allGameOverInfo;
    }
}
