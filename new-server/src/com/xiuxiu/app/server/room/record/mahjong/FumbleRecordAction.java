package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.BrightInfo;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.List;

public class FumbleRecordAction extends RecordAction {
    protected byte card;
    protected boolean auto;
    protected boolean bright;
    protected List<BrightInfo> brightInfo;
    protected boolean hu;
    protected boolean bar;

    public FumbleRecordAction(long playerUid, byte card, boolean auto, boolean hu, boolean bar, boolean bright, List<BrightInfo> brightInfo) {
        super(EActionOp.FUMBLE, playerUid);
        this.card = card;
        this.auto = auto;
        this.bright = bright;
        this.brightInfo = brightInfo;
        this.hu = hu;
        this.bar = bar;
    }

    public byte getCard() {
        return card;
    }

    public void setCard(byte card) {
        this.card = card;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
    }

    public List<BrightInfo> getBrightInfo() {
        return brightInfo;
    }

    public void setBrightInfo(List<BrightInfo> brightInfo) {
        this.brightInfo = brightInfo;
    }

    public boolean isHu() {
        return hu;
    }

    public void setHu(boolean hu) {
        this.hu = hu;
    }

    public boolean isBar() {
        return bar;
    }

    public void setBar(boolean bar) {
        this.bar = bar;
    }
}
