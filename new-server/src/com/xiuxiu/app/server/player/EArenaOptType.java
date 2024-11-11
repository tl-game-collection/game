package com.xiuxiu.app.server.player;

public enum EArenaOptType {
    UNKNOWN(0, "未知"),
    //  管理修改（管理上分）
    INC_MANAGER(1, "[管理上分]"),
    //  管理修改（管理下分）
    DEC_MANAGER(2, "[管理下分]"),
    //  财务修改（财务上分）
    INC_FINANCE(3, "[财务上分]"),
    //  财务修改（申请财务下分）
    DEC_FINANCE(4, "[申请财务下分]"),
    //  财务修改（财务下分拒绝）
    REJ_FINANCE(5, "[财务下分拒绝]"),
    //  系统上分（微信）
    INC_SYS_WECHAT(6, "[微信上分]"),
    //  系统上分（支付宝）
    INC_SYS_ALIPAY(7, "[支付宝上分]"),
    //  系统上分（钱包）
    INC_SYS_WALLET(8, "[钱包转金币]"),
    //  系统下分（支付宝）
    DEC_SYS_ALIPAY(9, "[支付宝下分]"),
    INC_SHARE(10, "[每日分享]"),
    INC_SERVICE_TO_ARENA(11, "[奖券转竞技值]"),
    INC_SYS_UNION(12, "[银联上分]"),
    INC_PLAYER_SEND(13, "[收到赠送]"),
    DEC_PLAYER_SEND(14, "[赠送出]"),
    // 系统上分（转账）
    INC_SYS_TRANSFER(15, "[转账上分]"),
    // 群组任务（转账）
    GROUP_QUEST(16, "[群组任务]"),
    // 钱包转竞技值
    DEC_ARENA_TO_WALLET(17, "[金币转钱包]"),
    // 钱包转竞技值
    TEXAX_INSURANCE(18, "[德州保险赔付]"),
    CHANGE_MONEY(19, "[货币转换]"),
    INC_LEAGUE_MANAGER(20, "[盟主上分]"),
    DEC_LEAGUE_MANAGER(21, "[盟主下分]"),
    INC_MANAGER_DEC(22, "[管理给人上分自身消耗]"),
    DEC_MANAGER_INC(23, "[管理给人下分自身获得]"),
    LEAGUE_REBACK(24, "[下分失败返回]"),
    ;
    private int value;
    private String desc;

    EArenaOptType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
