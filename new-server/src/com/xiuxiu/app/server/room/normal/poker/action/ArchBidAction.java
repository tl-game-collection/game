package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfArchBidInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfArchBidResultInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.arch.ArchRoom;
import com.xiuxiu.app.server.room.normal.poker.arch.ArchRule;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;
import com.xiuxiu.app.server.room.record.poker.ArchBidRecordAction;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class ArchBidAction extends BasePokerAction {
    private int round = 1; // 轮次
    private List<KeyValue<Long, Integer>> roundRecords = new ArrayList<>(); // 当前轮叫牌记录<player, contract>
    private long lastBidderUid = -1; // 最后一个叫牌人的UID
    private int lastContract = ArchRule.CONTRACT_UNDEFINED; // 最后一次叫的定约
    private long bankerUid = -1; // 庄家UID
    private int finalContract = ArchRule.CONTRACT_UNDEFINED; // 最终定约

    private ArchBidRecordAction recordAction;
    private int indexOfNextBidder = -1; // 下一个叫牌人在房间中的位次

    private int rules;
    private final int RULE_2KINGS = 0x01; // 双王强制性要求叫牌
    private final int RULE_DOUBLE = 0x02; // 是否允许抄牌

    public ArchBidAction(ArchRoom room, PokerPlayer player, long timeout, ArchBidRecordAction recordAction) {
        super(room, EActionOp.CALL_SCORE, player, timeout);
        this.recordAction = recordAction;

        this.rules = this.room.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        for (int i = 0; i < this.room.getMaxPlayerCnt(); i++) {
            IRoomPlayer roomPlayer = this.room.getRoomPlayer(i);
            if (null == roomPlayer || roomPlayer.isGuest()){
                continue;
            }
            if (roomPlayer.getUid() == player.getUid()) {
                this.indexOfNextBidder = i;
                break;
            }
        }
        this.resetTimeout(10 * 1000);
    }

    public void begin(long bidderUid) {
        PCLIPokerNtfArchBidInfo info = new PCLIPokerNtfArchBidInfo();
        info.round = 1;
        info.contract = 0;
        info.bidderUid = -1;
        info.nextBidderUid = bidderUid;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_ARCH_BID, info);
    }

    public ErrorCode bid(long playerUid, int contract) {
        if (this.hasFinished()) {
            Logs.ROOM.warn("%s 叫牌已结束，不能再叫 playerUid:%d", this, playerUid);
            return ErrorCode.REQUEST_INVALID;
        }
        IPokerPlayer bidder = (IPokerPlayer) this.room.getRoomPlayer(this.indexOfNextBidder);
        if (bidder.getUid() != playerUid) {
            Logs.ROOM.warn("%s 当前轮叫牌人是:%d 而不是你:%d", this, bidder.getUid(), playerUid);
            return ErrorCode.REQUEST_INVALID;
        }

        boolean startNewRound = false;
        int countOfPlayers = this.room.getCurPlayerCnt();
        if (countOfPlayers == 3) {
            if (this.round == 1) { // 第一轮，选择是否独庄
                if (this.roundRecords.size() == countOfPlayers - 1) { // 最后一人，强制独庄，叫牌结束
                    this.bankerUid = playerUid;
                    this.finalContract = ArchRule.CONTRACT_1V2;
                } else if (contract > 0
                        || ((this.rules & RULE_2KINGS) != 0 && this.countOfKings(bidder.getHandCard()) >= 2)) {
                    this.bankerUid = playerUid;
                    if ((this.rules & RULE_DOUBLE) != 0) { // 允许抄牌
                        startNewRound = true;
                    } else {
                        this.finalContract = ArchRule.CONTRACT_1V2;
                    }
                }
            } else if (this.round == 2) { // 第2轮，选择是否抄庄
                if (contract > 0) {
                    this.finalContract = ArchRule.CONTRACT_1V2_X2;
                    this.bankerUid = playerUid;
                } else if (this.roundRecords.size() == countOfPlayers - 2) {
                    this.finalContract = ArchRule.CONTRACT_1V2;
                }
            } else {
                return ErrorCode.REQUEST_INVALID;
            }
        } else if (countOfPlayers == 4) {
            if (this.round > 1) {
                return ErrorCode.REQUEST_INVALID;
            }

            // 有玩家选择独庄或者全部玩家放弃独庄，则叫牌结束
            if (this.roundRecords.isEmpty()) {
                this.bankerUid = playerUid;
            }
            if (contract > 0 || this.roundRecords.size() == countOfPlayers - 1) {
                this.finalContract = contract > 0 ? ArchRule.CONTRACT_1V3 : ArchRule.CONTRACT_2V2;
                this.bankerUid = contract > 0 ? playerUid : this.bankerUid;
            }
        } else {
            return ErrorCode.REQUEST_INVALID;
        }

        this.lastBidderUid = playerUid;
        this.lastContract = contract;
        this.recordAction.addRecord(playerUid, contract);
        this.roundRecords.add(new KeyValue<>(playerUid, contract));

        PCLIPokerNtfArchBidInfo info = new PCLIPokerNtfArchBidInfo();
        info.contract = contract;
        info.bidderUid = playerUid;
        if (!this.hasFinished()) {
            // 下一个叫牌的玩家
            IRoomPlayer nextBidder = null;
            while (nextBidder == null || nextBidder.isGuest()) {
                this.indexOfNextBidder = (this.indexOfNextBidder + 1) % this.room.getMaxPlayerCnt();
                nextBidder = this.room.getRoomPlayer(this.indexOfNextBidder);
            }
            boolean is3PlayersRound1 = this.room.getCurPlayerCnt() == 3 && this.round == 1;
            if (is3PlayersRound1
                    && this.bankerUid <= 0
                    && this.roundRecords.size() == this.room.getCurPlayerCnt() - 1) {
                // 玩家将被强制叫牌
                info.nextBidderUid = -1;
            } else {
                info.nextBidderUid = nextBidder.getUid();
            }

            if (startNewRound) {
                this.newRound();
            }
        } else {
            info.nextBidderUid = -1;
        }
        info.round = this.round;

        // 通告叫牌过程
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_ARCH_BID, info);
        if (!this.hasFinished()) {
            // info.nextBidderUid小于0时，玩家被强制叫牌
            this.resetTimeout(info.nextBidderUid < 0 ? 10 : 10 * 1000);
        } else {
            // 通告叫牌结果
            PCLIPokerNtfArchBidResultInfo result = new PCLIPokerNtfArchBidResultInfo();
            result.bankerUid = this.bankerUid;
            result.contract = this.finalContract;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_ARCH_BID_RESULT, result);
            this.resetTimeout(100);
        }
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (this.hasFinished()) {
            ArchRoom archRoom = (ArchRoom) this.room;
            this.recordAction.setFinalContract(this.finalContract);
            archRoom.onBidFinish(this.bankerUid, this.finalContract);
            return true;
        }
        if (timeout) {
            PokerPlayer player = (PokerPlayer) this.room.getRoomPlayer(this.indexOfNextBidder);
            // 第一轮，持有双王或者是最后一个叫牌的玩家，必须叫牌
            boolean is3PlayersRound1 = this.room.getCurPlayerCnt() == 3 && this.round == 1;
            if (is3PlayersRound1
                    && ((this.bankerUid <= 0 && this.roundRecords.size() == this.room.getCurPlayerCnt() - 1)
                        || ((this.rules & RULE_2KINGS) != 0 && this.countOfKings(player.getHandCard()) >= 2))) {
                Logs.ROOM.debug("玩家按照规则叫牌独庄 player:%d", player.getUid());
                this.bid(player.getUid(), 1);
            } else {
                this.bid(player.getUid(), 0);
            }
        }
        return false;
    }

    @Override
    protected void doRecover() {
        PCLIPokerNtfArchBidInfo info = new PCLIPokerNtfArchBidInfo();
        info.round = this.round;
        info.contract = this.lastContract;
        info.bidderUid = this.lastBidderUid;
        info.nextBidderUid = this.room.getRoomPlayer(this.indexOfNextBidder).getUid();
        this.player.send(CommandId.CLI_NTF_POKER_ARCH_BID, info);
    }

    private int countOfKings(List<Byte> cards) {
        int count = 0;
        for (byte card : cards) {
            if (card == PokerUtil.KINGLET || card == PokerUtil.KING) {
                count++;
            }
        }
        return count;
    }

    private void newRound() {
        this.round++;
        this.roundRecords.clear();
    }

    private boolean hasFinished() {
        return this.finalContract != ArchRule.CONTRACT_UNDEFINED;
    }
}
