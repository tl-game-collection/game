package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSStartHuRecordAction extends RecordAction {
    private class StartHuInfo {
        private long playerUid;
        private List<Byte> handCard = new ArrayList<>();
        private List<Byte> capList = new ArrayList<>();
        private List<Integer> allPx = new ArrayList<>();
        private HashMap<Long, Integer> allScore = new HashMap<>();

        public StartHuInfo() {

        }

        public StartHuInfo(long playerUid, List<Byte> handCard, List<Integer> allPx, List<Byte> capList, HashMap<Long, Integer> allScore) {
            this.playerUid = playerUid;
            this.handCard.addAll(handCard);
            this.allPx.addAll(allPx);
            this.capList.addAll(capList);
            this.allScore.putAll(allScore);
        }

        public long getPlayerUid() {
            return playerUid;
        }

        public void setPlayerUid(long playerUid) {
            this.playerUid = playerUid;
        }

        public List<Byte> getHandCard() {
            return handCard;
        }

        public void setHandCard(List<Byte> handCard) {
            this.handCard = handCard;
        }

        public List<Integer> getAllPx() {
            return allPx;
        }

        public void setAllPx(List<Integer> allPx) {
            this.allPx = allPx;
        }

        public List<Byte> getCapList() {
            return capList;
        }

        public void setCapList(List<Byte> capList) {
            this.capList = capList;
        }

        public HashMap<Long, Integer> getAllScore() {
            return allScore;
        }

        public void setAllScore(HashMap<Long, Integer> allScore) {
            this.allScore = allScore;
        }
    }

    public List<StartHuInfo> allStartHu = new ArrayList<>();

    public CSStartHuRecordAction() {
        super(EActionOp.CS_START_HU, -1);
    }

    public void addStartHu(long playerUid, List<Byte> handCard, List<Integer> allPx, List<Byte> capList, HashMap<Long, Integer> allScore) {
        this.allStartHu.add(new StartHuInfo(playerUid, handCard, allPx, capList, allScore));
    }

    public List<StartHuInfo> getAllStartHu() {
        return allStartHu;
    }

    public void setAllStartHu(List<StartHuInfo> allStartHu) {
        this.allStartHu = allStartHu;
    }
}
