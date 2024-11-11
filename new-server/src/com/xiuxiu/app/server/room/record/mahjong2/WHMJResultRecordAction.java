package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.ArrayList;
import java.util.List;

public class WHMJResultRecordAction extends ResultRecordAction {
    public static class ScoreInfo extends ResultRecordAction.ScoreInfo {
    }

    public static class PlayerInfo extends ResultRecordAction.PlayerInfo {
    }

    public boolean isSmallGold = false;             // 小金顶
    public int smallGoldPoint1 = 0;                 // 小金顶点数1
    public int smallGoldPoint2 = 0;                 // 小金顶点数2
    public List<Byte> haiDiLaoCard = new ArrayList<>(); // 海底捞摸的牌
    public long haiDiLaoStartPlayerUid = -1;        // 海底捞开始摸牌的玩家uid


    public WHMJResultRecordAction() {
        super();
    }

    public void addHaiDiLaoCard(List<Byte> card) {
        this.haiDiLaoCard.addAll(card);
    }

    public boolean isSmallGold() {
        return isSmallGold;
    }

    public void setSmallGold(boolean smallGold) {
        isSmallGold = smallGold;
    }

    public int getSmallGoldPoint1() {
        return smallGoldPoint1;
    }

    public void setSmallGoldPoint1(int smallGoldPoint1) {
        this.smallGoldPoint1 = smallGoldPoint1;
    }

    public int getSmallGoldPoint2() {
        return smallGoldPoint2;
    }

    public void setSmallGoldPoint2(int smallGoldPoint2) {
        this.smallGoldPoint2 = smallGoldPoint2;
    }

    public List<Byte> getHaiDiLaoCard() {
        return haiDiLaoCard;
    }

    public void setHaiDiLaoCard(List<Byte> haiDiLaoCard) {
        this.haiDiLaoCard = haiDiLaoCard;
    }

    public long getHaiDiLaoStartPlayerUid() {
        return haiDiLaoStartPlayerUid;
    }

    public void setHaiDiLaoStartPlayerUid(long haiDiLaoStartPlayerUid) {
        this.haiDiLaoStartPlayerUid = haiDiLaoStartPlayerUid;
    }
}
