package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class BrightRecordAction extends RecordAction {
    protected List<Byte> bright = new ArrayList<>();
    protected HashMap<Byte, Integer> ting = new HashMap<>();
    protected List<Byte> kou = new ArrayList<>();
    protected byte takeCard;
    protected byte takeCardIndex;

    public BrightRecordAction(long playerUid, Collection<Byte> kou, Collection<Byte> bright, Map<Byte, Integer> ting, byte takeCard, byte takeCardIndex) {
        super(EActionOp.BRIGHT, playerUid);
        this.kou.addAll(kou);
        this.bright.addAll(bright);
        this.ting.putAll(ting);
        this.takeCard = takeCard;
        this.takeCardIndex = takeCardIndex;
    }

    public List<Byte> getBright() {
        return bright;
    }

    public void setBright(List<Byte> bright) {
        this.bright = bright;
    }

    public HashMap<Byte, Integer> getTing() {
        return ting;
    }

    public void setTing(HashMap<Byte, Integer> ting) {
        this.ting = ting;
    }

    public List<Byte> getKou() {
        return kou;
    }

    public void setKou(List<Byte> kou) {
        this.kou = kou;
    }

    public byte getTakeCard() {
        return takeCard;
    }

    public void setTakeCard(byte takeCard) {
        this.takeCard = takeCard;
    }

    public byte getTakeCardIndex() {
        return takeCardIndex;
    }

    public void setTakeCardIndex(byte takeCardIndex) {
        this.takeCardIndex = takeCardIndex;
    }
}