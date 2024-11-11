package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.ArrayList;
import java.util.List;

public class FZMJResultRecordAction extends ResultRecordAction {
    public static class ScoreInfo extends ResultRecordAction.ScoreInfo {
    }

    public static class PlayerInfo extends ResultRecordAction.PlayerInfo {
    }

    public List<Byte> haiDiLaoCard = new ArrayList<>(); // 海底捞摸的牌
    public long haiDiLaoStartPlayerUid = -1;        // 海底捞开始摸牌的玩家uid

    public FZMJResultRecordAction() {
        super();
    }

    public void addHaiDiLaoCard(List<Byte> card) {
        this.haiDiLaoCard.addAll(card);
    }

    public void setHaiDiLaoCard(List<Byte> haiDiLaoCard) {
        this.haiDiLaoCard = haiDiLaoCard;
    }

    public List<Byte> getHaiDiLaoCard() {
        return haiDiLaoCard;
    }

    public void setHaiDiLaoStartPlayerUid(long haiDiLaoStartPlayerUid) {
        this.haiDiLaoStartPlayerUid = haiDiLaoStartPlayerUid;
    }

    public long getHaiDiLaoStartPlayerUid() {
        return haiDiLaoStartPlayerUid;
    }
}
