package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.PokerHandCard;

public class LandLordPlayer extends PokerPlayer {
    private int multipleValue = 1; // 加倍值
    private boolean calledMultiple = false; // 是否叫过了加倍
    
    private boolean kick = false;//是否选了踢
    private boolean kickSelected = false;// 是否选择过踢
    private boolean kickBack = false;// 是否选了回踢
    private boolean kickBackSelected = false;// 是否选择过回踢

    public LandLordPlayer(int gameType,long roomUid, int roomId) {
        super(gameType,roomUid, roomId);
        this.handCard = new PokerHandCard();
    }

    @Override
    public void clear() {
        super.clear();
        this.multipleValue = 1;
        this.calledMultiple = false;
        this.kick = false;
        this.kickSelected = false;
        this.kickBack = false;
        this.kickBackSelected = false;
    }
    
    public void callKickSelected(boolean flag) {
        this.kick = flag;
        this.kickSelected = true;
    }
    
    public void callKickBackSelected (boolean flag) {
        this.kickBack = flag;
        this.kickBackSelected = true;
    }

    public boolean isKickBackSelected() {
        return kickBackSelected;
    }

    public boolean isKickSelected() {
        return kickSelected;
    }

    public void setKickSelected(boolean kickSelected) {
        this.kickSelected = kickSelected;
    }

    public int getMultipleValue() {
        return multipleValue;
    }

    public void callMultiple(int value) {
        this.multipleValue = value;
        this.calledMultiple = true;
    }

    public boolean isCalledMultiple() {
        return calledMultiple;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    public boolean isKickBack() {
        return kickBack;
    }

    public void setKickBack(boolean kickBack) {
        this.kickBack = kickBack;
    }
}