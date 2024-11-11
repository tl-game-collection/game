package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfCanTakeInfo;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;

import java.util.List;

public class PokerTakeAction extends BasePokerAction {
    protected EActionOp op = EActionOp.NORMAL;
    protected List<Byte> cards;
    protected List<Byte> laiZiCards;
    protected EPokerCardType cardType;
    protected byte takeMaxCard;
    protected byte takeCnt;
    protected boolean canPass;
    private boolean isAuto = false;

    public PokerTakeAction(PokerRoom room, PokerPlayer player, long timeout) {
        super(room, EActionOp.TAKE, player, timeout);
    }


    @Override
    public boolean canAction(long curTime) {
        if (!this.active) {
            return false;
        }
        if (this.room.getRule().getOrDefault(RoomRule.RR_RF_OUT_TIME, 30) == -1 && this.room.getGameType() == GameType.GAME_TYPE_RUN_FAST && !isAuto) {
            return false;
        } else {
            //if (-1 == this.timeout) {
            if (curTime - this.startTime + this.useTime >= Constant.ROOM_TAKE_TIMEOUT) {
                this.operationTimeout();
                if (-1 == this.timeout) {
                    return true;
                }
            }
            //}
            if (-1 == curTime) {
                return true;
            }
            if (-1 == this.timeout || (curTime - this.startTime + this.useTime) < this.timeout) {
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean action(boolean timeout) {
        if (EActionOp.NORMAL == this.op) {
            if (this.canPass) {
                this.op = EActionOp.PASS;
            } else if (null == this.cards) {
                this.op = EActionOp.PASS;
            } else {
                this.op = EActionOp.TAKE;
            }
        }
        if (EActionOp.PASS == this.op) {
            ((PokerRoom) this.room).onPass(this.player);
        } else {
            ((PokerRoom) this.room).onTake(this.player, this.cards, this.laiZiCards, this.cardType, this.takeMaxCard, this.takeCnt);
        }
        return true;
    }

    public EActionOp getOp() {
        return op;
    }

    public void setOp(EActionOp op) {
        this.op = op;
    }

    public boolean isCanPass() {
        return canPass;
    }

    public void setCanPass(boolean canPass) {
        this.canPass = canPass;
    }

    public List<Byte> getCards() {
        return cards;
    }

    public void setCards(List<Byte> cards) {
        this.cards = cards;
    }

    public EPokerCardType getCardType() {
        return cardType;
    }

    public void setCardType(EPokerCardType cardType) {
        this.cardType = cardType;
    }

    public byte getTakeMaxCard() {
        return takeMaxCard;
    }

    public void setTakeMaxCard(byte takeMaxCard) {
        this.takeMaxCard = takeMaxCard;
    }

    public byte getTakeCnt() {
        return takeCnt;
    }

    public void setTakeCnt(byte takeCnt) {
        this.takeCnt = takeCnt;
    }

    public void setLaiZiCards(List<Byte> laiZiCards) {
        this.laiZiCards = laiZiCards;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    @Override
    protected void doRecover() {
        this.player.send(CommandId.CLI_NTF_POKER_CAN_TAKE, new PCLIPokerNtfCanTakeInfo(this.player.getUid()));
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.player.getUid() != player.getUid()) {
            return;
        }
        player.send(CommandId.CLI_NTF_POKER_CAN_TAKE, new PCLIPokerNtfCanTakeInfo(player.getUid()));
    }

    @Override
    public void offline(IRoomPlayer player) {
        if (this.player.getUid() != player.getUid()) {
            return;
        }
    }
}
