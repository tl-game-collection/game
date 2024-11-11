package com.xiuxiu.app.server.player;

public enum EPayType {
    NORMAL("无", 0),              // 无
    WECHAT("微信", 1),            // 微信
    ALI("支付宝", 2)              // 支付宝
    ;

    private String desc;
    private int value;

    EPayType(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return "EPayType{" +
                "desc='" + desc + '\'' +
                ", value=" + value +
                '}';
    }
}
