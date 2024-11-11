package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.server.room.normal.mahjong.BrightInfo;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;

public abstract class MahjongRecord extends Record {
    public MahjongRecord() {
        super();
    }

    public void addPlayer(IMahjongPlayer player, int index, int bureau) {
        this.playerInfo.add(new RecordMahjongPlayerBriefInfo(player, index, bureau));
    }

    //public ShuKanRecordAction addShuKanRecordAction() {
    //    ShuKanRecordAction action = new ShuKanRecordAction();
    //    this.addAction(action);
    //    return action;
    //}

    //public FlutterRecordAction addFlutterAction() {
    //    FlutterRecordAction action = new FlutterRecordAction();
    //    this.addAction(action);
    //    return action;
    //}

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

    public XuanZengRecordAction addXuanZengAction() {
        XuanZengRecordAction action = new XuanZengRecordAction();
        this.addAction(action);
        return action;
    }

    public XuanPiaoRecordAction addXuanPiaoAction() {
        XuanPiaoRecordAction action = new XuanPiaoRecordAction();
        this.addAction(action);
        return action;
    }

    public StartHuRecordAction addStartHuAction() {
        StartHuRecordAction action = new StartHuRecordAction();
        this.addAction(action);
        return action;
    }

    public CSStartHuRecordAction addCSStartHuAction() {
        CSStartHuRecordAction action = new CSStartHuRecordAction();
        this.addAction(action);
        return action;
    }

    public OpenBarRecordAction addCSOpenBarAction(long playerUid) {
        OpenBarRecordAction action = new OpenBarRecordAction(playerUid);
        this.addAction(action);
        return action;
    }

    public LastCardRecordAction addLastCardAction(long playerUid, byte card) {
        LastCardRecordAction action = new LastCardRecordAction(playerUid, card);
        this.addAction(action);
        return action;
    }

    public FumbleRecordAction addFumbleRecordAction(long playerUid, byte card) {
        FumbleRecordAction action = new FumbleRecordAction(playerUid, card);
        this.addAction(action);
        return action;
    }

    public TakeRecordAction addTakeRecordAction(long playerUid, byte card) {
        TakeRecordAction action = new TakeRecordAction(playerUid, card);
        this.addAction(action);
        return action;
    }

    public YangPaiRecordAction addYangPaiRecordAction(long playerUid,List<Byte> card){
        YangPaiRecordAction action = new YangPaiRecordAction();
        action.addYangPai(playerUid,card);
        this.addAction(action);
        return action;
    }


    //public WaitRecordAction addWaitRecordAction() {
    //    WaitRecordAction action = new WaitRecordAction();
    //    this.addAction(action);
    //    return action;
    //}

    //public WaitSelectRecordAction addWaitSelectRecordAction() {
    //    WaitSelectRecordAction action = new WaitSelectRecordAction();
    //    this.addAction(action);
    //    return action;
    //}

    public BumpRecordAction addBumpRecordAction(long playerUid, long takePlayerUid, byte card,boolean bright, List<BrightInfo> brightInfo) {
        BumpRecordAction action = new BumpRecordAction(playerUid, takePlayerUid, card, bright, brightInfo);
        this.addAction(action);
        return action;
    }

    public BarRecordAction addBarRecordAction(long playerUid, long takePlayerUid, byte card) {
        BarRecordAction action = new BarRecordAction(playerUid, takePlayerUid, card);
        this.addAction(action);
        return action;
    }

    public BarScoreRecordAction addBarScoreRecordAction() {
        BarScoreRecordAction action = new BarScoreRecordAction();
        this.addAction(action);
        return action;
    }

    public EatRecordAction addEatRecordAction(long playerUid, long takePlayerUid, byte card, int type) {
        EatRecordAction action = new EatRecordAction(playerUid, takePlayerUid, card, type);
        this.addAction(action);
        return action;
    }

    public BrightRecordAction addBrightRecordAction(long playerUid, Collection<Byte> kou, Collection<Byte> bright, Map<Byte, Integer> ting, byte takeCard, byte takeCardIndex) {
        BrightRecordAction action = new BrightRecordAction(playerUid, kou, bright, ting, takeCard, takeCardIndex);
        this.addAction(action);
        return action;
    }

    public HuRecordAction addHuRecordAction(long takePlayerUid) {
        HuRecordAction action = new HuRecordAction(takePlayerUid);
        this.addAction(action);
        return action;
    }

    public ResultRecordAction addResultRecordAction() {
        ResultRecordAction action = new ResultRecordAction();
        this.addAction(action);
        return action;
    }

    public DeskShowRecordAction addDeskShowRecordAction() {
        DeskShowRecordAction action = new DeskShowRecordAction();
        this.addAction(action);
        return action;
    }
}
