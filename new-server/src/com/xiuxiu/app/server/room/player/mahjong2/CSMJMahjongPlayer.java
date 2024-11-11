package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.EPaiXing;

import java.util.ArrayList;
import java.util.List;

public class CSMJMahjongPlayer extends MahjongPlayer implements ICSMJMahjongPlayer {
    private boolean passHu = false;
    private boolean openBar = false;
    private int zeng = -1;
    private List<EPaiXing> startHuPx = new ArrayList<>();
    private List<EPaiXing> middleHuPx = new ArrayList<>();

    public CSMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public boolean isAutoTake() {
        return this.openBar;
    }

    @Override
    public boolean isOpenBar() {
        return this.openBar;
    }

    @Override
    public void setOpenBar(boolean value) {
        this.openBar = value;
    }

    @Override
    public void addPassCard(EActionOp op, byte passCard) {
        super.addPassCard(op, passCard);
        if (EActionOp.HU == op) {
            this.passHu = true;
        }
    }

    @Override
    public boolean isPassHu() {
        return this.passHu;
    }

    @Override
    public void addStartHuPaiXing(EPaiXing paiXing) {
        this.startHuPx.add(paiXing);
    }

    @Override
    public boolean hasStartHuPaiXing(EPaiXing paiXing) {
        return -1 != this.startHuPx.indexOf(paiXing);
    }

    @Override
    public void addStartHuPaiXingTo(List<Integer> toStartHuPaiXing) {
        for (EPaiXing px : this.startHuPx) {
            toStartHuPaiXing.add(px.getClientValue());
        }
    }

    @Override
    public void clearMiddleHuPaiXing() {
        this.middleHuPx.clear();
    }

    @Override
    public void addMiddleHuPaiXing(EPaiXing paiXing) {
        this.middleHuPx.add(paiXing);
        this.startHuPx.add(paiXing);
    }

    @Override
    public void addMiddleHuPaiXingTo(List<Integer> toMiddleHuPaiXing) {
        for (EPaiXing px : this.middleHuPx) {
            toMiddleHuPaiXing.add(px.getClientValue());
        }
    }

    @Override
    public void setZeng(int value) {
        this.zeng = value;
    }

    @Override
    public int getZeng() {
        return this.zeng;
    }

    @Override
    public void clear() {
        super.clear();
        this.zeng = -1;
        this.passHu = false;
        this.openBar = false;
        this.startHuPx.clear();
        this.middleHuPx.clear();
    }
}
