package com.xiuxiu.app.server.room.record.poker;

import java.util.List;

import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong.FlutterRecordAction;
import com.xiuxiu.app.server.room.record.poker.cow.CowReBetRecordAction;
import com.xiuxiu.app.server.room.record.poker.cow.CowReadyRecordAction;
import com.xiuxiu.app.server.room.record.poker.cow.CowResultRecordAction;
import com.xiuxiu.app.server.room.record.poker.cow.CowRobBankerRecordAction;


public class PokerRecord extends Record {
    public PokerRecord(PokerRoom room) {
        super();
        this.roomInfo = new RecordPokerRoomBriefInfo();
        this.roomInfo.setRoomId(room.getRoomId());
        this.roomInfo.setBankerIndex(room.getBankerIndex());
        this.roomInfo.setRoomType(room.getRoomType().ordinal());
        this.roomInfo.setGameType(room.getGameType());
        this.roomInfo.setGameSubType(room.getGameSubType());
        this.roomInfo.setRule(room.getRule());
    }

    public TakeRecordAction addTakeRecordAction(long playerUid, List<Byte> card) {
        TakeRecordAction action = new TakeRecordAction(playerUid, card);
        this.addAction(action);
        return action;
    }

    public PassRecordAction addPassRecordAction(long playerUid) {
        PassRecordAction action = new PassRecordAction(playerUid);
        this.addAction(action);
        return action;
    }

    public BombScoreRecordAction addBombScoreRecordAction() {
        BombScoreRecordAction action = new BombScoreRecordAction();
        this.addAction(action);
        return action;
    }

    public ResultRecordAction addResultRecordAction() {
        ResultRecordAction action = new ResultRecordAction();
        this.addAction(action);
        return action;
    }

    public BankerRecordAction addBankerRecordAction(long playerUid) {
        BankerRecordAction action = new BankerRecordAction(playerUid);
        this.addAction(action);
        return action;
    }

    //端火锅需要加入桌面筹码数 和 庄已经进行的轮数；
    public BankerRecordAction addBankerRecordAction(long playerUid,int bankerLoop,int deskNot) {
        BankerRecordAction action = new BankerRecordAction(playerUid,bankerLoop,deskNot);
        this.addAction(action);
        return action;
    }

    public LookCardRecordAction addLookCardRecordAction(long playerUid, List<Byte> card) {
        LookCardRecordAction action = new LookCardRecordAction(playerUid, card);
        this.addAction(action);
        return action;
    }

    public DiscardRecordAction addDiscardRecordAction(long playerUid) {
        DiscardRecordAction action = new DiscardRecordAction(playerUid);
        this.addAction(action);
        return action;
    }

    // 牛牛开始
    public CowReadyRecordAction addCowReadyRecordAction() {
        CowReadyRecordAction action = new CowReadyRecordAction();
        this.addAction(action);
        return action;
    }
    public CowRobBankerRecordAction addCowRobBankerRecordAction() {
        CowRobBankerRecordAction action = new CowRobBankerRecordAction();
        this.addAction(action);
        return action;
    }

    public CowReBetRecordAction addCowRebetRecordAction() {
        CowReBetRecordAction action = new CowReBetRecordAction();
        this.addAction(action);
        return action;
    }

    public CowResultRecordAction addCowResultRecordAction() {
        CowResultRecordAction action = new CowResultRecordAction();
        this.addAction(action);
        return action;
    }


    public SGRobBankerRecordAction addSGRobBankerRecordAction() {
        SGRobBankerRecordAction action = new SGRobBankerRecordAction();
        this.addAction(action);
        return action;
    }

    public SGRebetRecordAction addSGRebetRecordAction() {
        SGRebetRecordAction action = new SGRebetRecordAction();
        this.addAction(action);
        return action;
    }

    public SGResultRecordAction addSGResultRecordAction() {
        SGResultRecordAction action = new SGResultRecordAction();
        this.addAction(action);
        return action;
    }

    public RunFastBeginPrimulaRecordAction addRunFastBeginPrimulaRecordAction() {
        RunFastBeginPrimulaRecordAction action = new RunFastBeginPrimulaRecordAction();
        this.addAction(action);
        return action;
    }

    // 牛牛结束

    // 斗地主开始
    public LandLordCallScoreRecordAction addLandLordCallScoreAction() {
        LandLordCallScoreRecordAction action = new LandLordCallScoreRecordAction();
        this.addAction(action);
        return action;
    }

    public LandLordLastCardRecordAction addLandLordLastCardRecordAction(long playerUid, List<Byte> cards) {
        LandLordLastCardRecordAction action = new LandLordLastCardRecordAction(playerUid, cards);
        this.addAction(action);
        return action;
    }

    public LandLordShowAllCardRecordAction addLandLordShowAllCardRecordAction(long playerUid, List<Byte> cards) {
        LandLordShowAllCardRecordAction action = new LandLordShowAllCardRecordAction(playerUid, cards);
        this.addAction(action);
        return action;
    }

    public LandLordLaiziCardRecordAction addLanLordLaiziCardRecordAction(List<Byte> cards){
        LandLordLaiziCardRecordAction action = new LandLordLaiziCardRecordAction(cards);
        this.addAction(action);
        return action;
    }

    //斗地主结束

    // 扎金花开始
    public AddNoteRecordAction addAddNoteRecordAction(long playerUid, int note, int isFillUp) {
        AddNoteRecordAction action = new AddNoteRecordAction(playerUid, note, isFillUp);
        this.addAction(action);
        return action;
    }

    public FollowNoteRecordAction addFollowNoteRecordAction(long playerUid, int note) {
        FollowNoteRecordAction action = new FollowNoteRecordAction(playerUid, note);
        this.addAction(action);
        return action;
    }

    public CompareCardRecordAction addCompareCardRecordRecordAction(long initiatorPlayerUid, long receiverPlayerUid, long winPlayerUid, long lostPlayerUid, int note) {
        CompareCardRecordAction action = new CompareCardRecordAction(initiatorPlayerUid, receiverPlayerUid, winPlayerUid, lostPlayerUid, note);
        this.addAction(action);
        return action;
    }

    public FGFResultRecordAction addFGFResultRecordAction() {
        FGFResultRecordAction action = new FGFResultRecordAction();
        this.addAction(action);
        return action;
    }
    // 扎金花结束

    public CheckRecordAction addCheckRecordAction(long playerUid){
        CheckRecordAction action = new CheckRecordAction(playerUid);
        this.addAction(action);
        return action;
    }

    public PokerSendPublicCardAction addSendPublicCardAction(List<Byte> cards){
        PokerSendPublicCardAction action = new PokerSendPublicCardAction(cards);
        this.addAction(action);
        return action;
    }

    public AllInRecordAction addAllInRecordAction(long playerUid,int deskNot){
        AllInRecordAction action = new AllInRecordAction(playerUid,deskNot);
        this.addAction(action);
        return action;
    }


    public ThirteenResultRecordAction addThirteenResultRecordAction(){
        ThirteenResultRecordAction action = new ThirteenResultRecordAction();
        this.addAction(action);
        return action;
    }

    // 干瞪眼 摸牌；
    public GDyMoCardRecordAction addGDYMoCardRecordAction(){
        GDyMoCardRecordAction action  = new GDyMoCardRecordAction();
        this.addAction(action);
        return action;
    }

    //牌九
    public PaiGowRebetRecordAction addPaiGowRebetRecordAction() {
        PaiGowRebetRecordAction action = new PaiGowRebetRecordAction();
        this.addAction(action);
        return action;
    }

    public PaiGowOpenRecordAction addPaiGowOpenRecordAction() {
        PaiGowOpenRecordAction action = new PaiGowOpenRecordAction();
        this.addAction(action);
        return action;
    }

    public PaiGowRobBankerRecordAction addPaiGowRobBankerRecordAction() {
        PaiGowRobBankerRecordAction action = new PaiGowRobBankerRecordAction();
        this.addAction(action);
        return action;
    }

    public PaiGowReadyRecordAction addPaiGowReadyRecordAction() {
        PaiGowReadyRecordAction action = new PaiGowReadyRecordAction();
        this.addAction(action);
        return action;
    }

    public PaiGowSendCardRecordAction addPaiGowSendCardRecordAction() {
        PaiGowSendCardRecordAction action = new PaiGowSendCardRecordAction();
        this.addAction(action);
        return action;
    }

    public PaiGowHotAgainRecordAction addPaiGowHotAgainRecordAction(long playerUid, boolean again) {
        PaiGowHotAgainRecordAction action = new PaiGowHotAgainRecordAction(playerUid, again);
        this.addAction(action);
        return action;
    }

    public PaiGowHotOutRecordAction addPaiGowHotOutRecordAction(long playerUid, boolean out, boolean five) {
        PaiGowHotOutRecordAction action = new PaiGowHotOutRecordAction(playerUid, out, five);
        this.addAction(action);
        return action;
    }

    public FolieFGFResultRecordAction addFolieFGFResultRecordAction() {
        FolieFGFResultRecordAction action = new FolieFGFResultRecordAction();
        this.addAction(action);
        return action;
    }

    public BlackJackRobBankerRecordAction addBlackJackRobBankerRecordAction() {
        BlackJackRobBankerRecordAction action = new BlackJackRobBankerRecordAction();
        this.addAction(action);
        return action;
    }

    public BlackJackRebetRecordAction addBlackJackRebetRecordAction() {
        BlackJackRebetRecordAction action = new BlackJackRebetRecordAction();
        this.addAction(action);
        return action;
    }

    public BlackJackResultRecordAction addBlackJackResultRecordAction() {
        BlackJackResultRecordAction action = new BlackJackResultRecordAction();
        this.addAction(action);
        return action;
    }

    public FlutterRecordAction addFlutterAction() {
        FlutterRecordAction action = new FlutterRecordAction();
        this.addAction(action);
        return action;
    }
}
