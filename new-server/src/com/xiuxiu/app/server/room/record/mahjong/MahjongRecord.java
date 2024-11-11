package com.xiuxiu.app.server.room.record.mahjong;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.server.room.normal.mahjong.BrightInfo;
import com.xiuxiu.app.server.room.normal.mahjong.EBarType;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.record.Record;

public class MahjongRecord extends Record {
    public MahjongRecord(MahjongRoom room) {
        super();
        this.roomInfo = new RecordMahjongRoomBriefInfo();
        this.roomInfo.setRoomId(room.getRoomId());
        this.roomInfo.setBankerIndex(room.getBankerIndex());
        this.roomInfo.setRoomType(room.getRoomType().ordinal());
        this.roomInfo.setGameType(room.getGameType());
        this.roomInfo.setGameSubType(room.getGameSubType());
        this.roomInfo.setRule(room.getRule());
        ((RecordMahjongRoomBriefInfo) this.roomInfo).setCrap1(room.getCrap1());
        ((RecordMahjongRoomBriefInfo) this.roomInfo).setCrap2(room.getCrap2());
    }

    public ShuKanRecordAction addShuKanRecordAction() {
        ShuKanRecordAction action = new ShuKanRecordAction();
        this.addAction(action);
        return action;
    }

    public FlutterRecordAction addFlutterAction() {
        FlutterRecordAction action = new FlutterRecordAction();
        this.addAction(action);
        return action;
    }

    public HuanPaiRecordAction addHuanPaiAction() {
        HuanPaiRecordAction action = new HuanPaiRecordAction();
        this.addAction(action);
        return action;
    }

    public ShuaiPaiRecordAction addShuaiPaiAction() {
        ShuaiPaiRecordAction action = new ShuaiPaiRecordAction();
        this.addAction(action);
        return action;
    }

    public DingQueRecordAction addDingQueAction() {
        DingQueRecordAction action = new DingQueRecordAction();
        this.addAction(action);
        return action;
    }

    public FumbleRecordAction addFumbleRecordAction(long playerUid, byte card, boolean auto, boolean hu, boolean bar, boolean bright, List<BrightInfo> brightInfo) {
        FumbleRecordAction action = new FumbleRecordAction(playerUid, card, auto, hu, bar, bright, brightInfo);
        this.addAction(action);
        return action;
    }

    public TakeRecordAction addTakeRecordAction(long playerUid, byte card, byte last, byte cardIndex, byte outputCardIndex, int length, boolean auto) {
        TakeRecordAction action = new TakeRecordAction(playerUid, card, last, cardIndex, outputCardIndex, length, auto);
        this.addAction(action);
        return action;
    }

    public WaitRecordAction addWaitRecordAction() {
        WaitRecordAction action = new WaitRecordAction();
        this.addAction(action);
        return action;
    }

    public WaitSelectRecordAction addWaitSelectRecordAction() {
        WaitSelectRecordAction action = new WaitSelectRecordAction();
        this.addAction(action);
        return action;
    }

    public BumpRecordAction addBumpRecordAction(long playerUid, long takePlayerUid, byte card, byte cardIndex, boolean bright, List<BrightInfo> brightInfo) {
        BumpRecordAction action = new BumpRecordAction(playerUid, takePlayerUid, card, cardIndex, bright, brightInfo);
        this.addAction(action);
        return action;
    }

    public BarRecordAction addBarRecordAction(long playerUid, long takePlayerUid, byte card, EBarType barType, byte startIndex, byte endIndex, byte insertIndex) {
        BarRecordAction action = new BarRecordAction(playerUid, takePlayerUid, card, barType, startIndex, endIndex, insertIndex);
        this.addAction(action);
        return action;
    }

    public BarScoreRecordAction addBarScoreRecordAction() {
        BarScoreRecordAction action = new BarScoreRecordAction();
        this.addAction(action);
        return action;
    }

    public BrightRecordAction addBrightRecordAction(long playerUid, Collection<Byte> kou, Collection<Byte> bright, Map<Byte, Integer> ting, byte takeCard, byte takeCardIndex) {
        BrightRecordAction action = new BrightRecordAction(playerUid, kou, bright, ting, takeCard, takeCardIndex);
        this.addAction(action);
        return action;
    }

    public HuRecordAction addHuRecordAction() {
        HuRecordAction action = new HuRecordAction();
        this.addAction(action);
        return action;
    }

    public ResultRecordAction addResultRecordAction() {
        ResultRecordAction action = new ResultRecordAction();
        this.addAction(action);
        return action;
    }
}
