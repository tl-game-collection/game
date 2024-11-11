package com.xiuxiu.app.server.room.normal.mahjong.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanTakeInfoByKWX;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongConstant;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;

import java.util.List;

public class MahjongTakeAction extends BaseMahjongAction {
    private byte cardValue;
    private byte last;
    private byte index;
    private byte outputCardIndex;
    private int length;
    private List<Byte> bar;
    private boolean hu;
    private boolean ting;
    private boolean bright;
    private EActionOp op = EActionOp.TAKE;
    private byte startIndex;
    private byte endIndex;
    private byte insertIndex;
    private boolean fumble;
    private List<Byte> kou;

    public MahjongTakeAction(MahjongRoom room, MahjongPlayer roomPlayer, long timeout) {
        super(room, EActionOp.TAKE, roomPlayer, timeout);
    }

    @Override
    public boolean action(boolean timeout) {
        switch (this.op) {
            case TAKE:
                if (timeout) {
                    this.initAutoTakCardParam();
                    ((MahjongRoom) this.room).onTake(this.roomPlayer, this.cardValue, this.last, this.index, this.outputCardIndex, this.length, true);
                } else {
                    ((MahjongRoom) this.room).onTake(this.roomPlayer, this.cardValue, this.last, this.index, this.outputCardIndex, this.length, false);
                }
                break;
            case BAR:
            case MUST_BAR:
                ((MahjongRoom) this.room).onBar(this.roomPlayer, this.roomPlayer, this.cardValue, this.startIndex, this.endIndex, this.insertIndex, this.bar);
                break;
            case HU:
                ((MahjongRoom) this.room).onHu(this.roomPlayer, this.roomPlayer, null, null, this.cardValue, MahjongConstant.MJ_HU_TYPE_NORMAL);
                break;
            case BRIGHT:
                ((MahjongRoom) this.room).onBright(this.roomPlayer, this.kou, this.cardValue, this.index);
                break;
        }
        return true;
    }

    protected void initAutoTakCardParam() {
        byte takeCardValue = -1;
        try {
            if (!((MahjongRoom) this.room).checkCanTake(this.roomPlayer, this.cardValue) || !this.fumble) {
                for (int i = MahjongConstant.MJ_CARD_KINDS - 1; i >= 0; --i) {
                    byte cnt = this.roomPlayer.getHandCard()[i];
                    if (cnt < 1) {
                        continue;
                    }
                    if (0 == ((MahjongRoom) this.room).getPaoZi()[i]) {
                        takeCardValue = (byte) i;
                        break;
                    }
                    if (/*-1 == this.cardValue && */null != ((MahjongRoom) this.room).getLiangPaiPlayer().get(0).getHalfBrightInfo().huCard.get((byte) i)) {
                        takeCardValue = (byte) i;
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        if (-1 == takeCardValue && this.fumble) {
            this.last = 1;
            this.index = -1;
            this.outputCardIndex = -1;
            return;
        }

        if (-1 == takeCardValue) {
            for (int i = MahjongConstant.MJ_CARD_KINDS - 1; i >= 0; --i) {
                byte cnt = this.roomPlayer.getHandCard()[i];
                if (cnt < 1) {
                    continue;
                }
                takeCardValue = (byte) i;
            }
        }

        this.cardValue = takeCardValue;
        this.last = 0;
        this.index = -1;
        this.outputCardIndex = 0;

        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (this.cardValue == i) {
                ++this.outputCardIndex;
                if (this.fumble) {
                    this.index = (byte) (this.outputCardIndex + 1);
                }
                break;
            }
            this.outputCardIndex += this.roomPlayer.getHandCard()[i];
        }
    }

    public void setOp(EActionOp op) {
        this.op = op;
    }

    public EActionOp getOp() {
        return op;
    }

    public byte getCardValue() {
        return cardValue;
    }

    public void setCardValue(byte cardValue) {
        this.cardValue = cardValue;
    }

    public byte getLast() {
        return last;
    }

    public void setLast(byte last) {
        this.last = last;
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public byte getOutputCardIndex() {
        return outputCardIndex;
    }

    public void setOutputCardIndex(byte outputCardIndex) {
        this.outputCardIndex = outputCardIndex;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<Byte> getBar() {
        return bar;
    }

    public void setBar(List<Byte> bar) {
        this.bar = bar;
    }

    public boolean isHu() {
        return hu;
    }

    public void setHu(boolean hu) {
        this.hu = hu;
    }

    public byte getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(byte startIndex) {
        this.startIndex = startIndex;
    }

    public byte getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(byte endIndex) {
        this.endIndex = endIndex;
    }

    public byte getInsertIndex() {
        return insertIndex;
    }

    public void setInsertIndex(byte insertIndex) {
        this.insertIndex = insertIndex;
    }

    public boolean isTing() {
        return ting;
    }

    public void setTing(boolean ting) {
        this.ting = ting;
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
    }

    public boolean isFumble() {
        return fumble;
    }

    public void setFumble(boolean fumble) {
        this.fumble = fumble;
    }

    public List<Byte> getKou() {
        return kou;
    }

    public void setKou(List<Byte> kou) {
        this.kou = kou;
    }

    @Override
    protected void doRecover() {
        PCLIMahjongNtfCanTakeInfoByKWX canTakeInfo = new PCLIMahjongNtfCanTakeInfoByKWX();
        canTakeInfo.uid = this.roomPlayer.getUid();
        canTakeInfo.liangPai = this.bright;
        if (this.ting) {
            canTakeInfo.brightInfo = this.roomPlayer.getBrightInfo().to();
        }
        this.roomPlayer.send(CommandId.CLI_NTF_MAHJONG_CAN_TAKE, canTakeInfo);
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.roomPlayer.getUid() != player.getUid()) {
            return;
        }
        PCLIMahjongNtfCanTakeInfoByKWX canTakeInfo = new PCLIMahjongNtfCanTakeInfoByKWX();
        canTakeInfo.uid = this.roomPlayer.getUid();
        canTakeInfo.liangPai = this.bright;
        if (this.ting) {
            canTakeInfo.brightInfo = this.roomPlayer.getBrightInfo().to();
        }
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_TAKE, canTakeInfo);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
        this.sourceTimeout = timeout;
    }
}