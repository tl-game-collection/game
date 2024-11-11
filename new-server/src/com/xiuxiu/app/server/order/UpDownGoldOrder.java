package com.xiuxiu.app.server.order;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class UpDownGoldOrder extends BaseTable {
    public UpDownGoldOrder() {
        this.tableType = ETableType.TB_UPDOWN_GOLD_ORDER;
    }

    private long uid;
    private long clubUid;
    private long mainClubUid;
    private int value;
    private int chargeValue;
    private long createAt;          //订单创建时间(当天零点)
    private long createAtDetail;    //订单创建具体时间
    private long optAt;             //订单处理时间(当天零点)
    private long optAtDetail;       //订单处理具体时间
    private long playerUid;
    private long optPlayerUid;
    private String bankCard;
    private String bankCardHolder;
    private int state;              //订单状态 0.待处理 1.已处理 2.已拒绝

    @Override
    public long getUid() {
        return uid;
    }

    @Override
    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public long getMainClubUid() {
        return mainClubUid;
    }

    public void setMainClubUid(long mainClubUid) {
        this.mainClubUid = mainClubUid;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(int chargeValue) {
        this.chargeValue = chargeValue;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getCreateAtDetail() {
        return createAtDetail;
    }

    public void setCreateAtDetail(long createAtDetail) {
        this.createAtDetail = createAtDetail;
    }

    public long getOptAt() {
        return optAt;
    }

    public void setOptAt(long optAt) {
        this.optAt = optAt;
    }

    public long getOptAtDetail() {
        return optAtDetail;
    }

    public void setOptAtDetail(long optAtDetail) {
        this.optAtDetail = optAtDetail;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public long getOptPlayerUid() {
        return optPlayerUid;
    }

    public void setOptPlayerUid(long optPlayerUid) {
        this.optPlayerUid = optPlayerUid;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankCardHolder() {
        return bankCardHolder;
    }

    public void setBankCardHolder(String bankCardHolder) {
        this.bankCardHolder = bankCardHolder;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
