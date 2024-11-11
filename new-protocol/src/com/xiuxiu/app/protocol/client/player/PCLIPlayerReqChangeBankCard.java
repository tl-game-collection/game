package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqChangeBankCard {
    /**
     * 类型（0修改银行卡持有人1修改银行卡号）
     */
    public Integer type;

    public String value;

    @Override
    public String toString() {
        return "PCLIPlayerReqChangeBankCard{" + "type=" + type + "value=" + value + '}';
    }
}
