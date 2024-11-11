package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.List;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.BrightInfo;
import com.xiuxiu.app.server.room.record.RecordAction;

public class BumpRecordAction extends RecordAction {
    protected long takePlayerUid;
    protected byte card;
    protected boolean bright;
    protected List<BrightInfo> brightInfo;

    public BumpRecordAction(long playerUid, long takePlayerUid, byte card,boolean bright, List<BrightInfo> brightInfo) {
        super(EActionOp.BUMP, playerUid);
        this.takePlayerUid = takePlayerUid;
        this.card = card;
        this.bright = bright;
        this.brightInfo = brightInfo;
    }

    public long getTakePlayerUid() {
        return takePlayerUid;
    }

    public void setTakePlayerUid(long takePlayerUid) {
        this.takePlayerUid = takePlayerUid;
    }

    public byte getCard() {
        return card;
    }

    public void setCard(byte card) {
        this.card = card;
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
}
