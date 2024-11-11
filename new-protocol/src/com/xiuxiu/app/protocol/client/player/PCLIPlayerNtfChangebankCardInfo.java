package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfChangebankCardInfo {
    /**
     * 类型（0修改银行卡持有人1修改银行卡号）
     */
    public Integer type;

    public String value;

    public PCLIPlayerNtfChangebankCardInfo() {

    }

    public PCLIPlayerNtfChangebankCardInfo(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfChangebankCardInfo{ type=" + type + " value='" + value + '\'' + '}';
    }
}
