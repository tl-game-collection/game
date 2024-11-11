package com.xiuxiu.app.server.account;

/**
 *  登录类型
 */
public enum EAccount {
    TOURIST_LOGIN(          0, "游客登陆"),
    PHONE_LOGIN(            1, "手机号登陆"),
    RAPID_LOGIN(            2, "快速登陆"),
    WE_CHAT_LOGIN(          3, "微信登陆"),
    DING_DING_LOGIN(        4, "钉钉登陆"),
    WEI_ZHI_5_LOGIN(        5, "未知登陆"),
    WEI_ZHI_6_LOGIN(        6, "未知登陆"),
    XIAN_LIAO_LOGIN(        7, "闲聊登陆"),
    PWD_LOGIN(              8, "密码登陆"),
            ;
    private String desc;
    private int value;

    EAccount(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return "EMoneyExpendType{" +
                "desc='" + desc + '\'' +
                ", value=" + value +
                '}';
    }
}
