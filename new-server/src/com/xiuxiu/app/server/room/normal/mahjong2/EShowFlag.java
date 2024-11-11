package com.xiuxiu.app.server.room.normal.mahjong2;

public enum EShowFlag {
    WHMJ_CHENG_BAO("承包"),
    WHMJ_FAN_BAO("反包"),
    WHMJ_PEI_BAO("陪包"),
    WHMJ_FA_KUAN("罚款"),
    WHMJ_TUI_JIN("推金"),
    WHMJ_JJIN_DING("金顶"),
    WHMJ_LIAN_JIN("连金"),
    WHMJ_FAN_JIN("反金"),
    WHMJ_XIAO_JIN_DING("小金顶"),
    WHMJ_FENG_DING("封顶"),
    WHMJ_YANG_GUANG_DING("阳光顶"),
    WHMJ_SAN_YANG_KAI_TAI("三阳开泰"),

    HZMJ_QGH_BP("包赔"),

    MCMJ_JIN_DING("金顶"),
    MCMJ_YIN_DING("银顶"),
    MCMJ_BI("闭"),

    YXMJ_CHENG_BAO("承包"),
    YXMJ_JJIN_DING("金顶"),

    HSMJ_JJIN_DING("金顶"),

    DYMJ_XIAO_JIN_DING("小金顶"),
    DYMJ_FENG_DING("封顶"),
    DYMJ_JJIN_DING("金顶"),
    DYMJ_YANG_GUANG_DING("阳光顶"),
    DYMJ_SAN_YANG_KAI_TAI("三阳开泰"),
    DYMJ_FA_KUAN("罚款"),
    DYMJ_LIAN_JIN("连金"),
    DYMJ_FAN_JIN("反金"),
    DYMJ_CHENG_BAO("承包"),
    DYMJ_PEI_BAO("陪包"),
    DYMJ_TUI_JIN("推金"),
    DYMJ_FAN_BAO("反包");

    private String desc;

    EShowFlag(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
