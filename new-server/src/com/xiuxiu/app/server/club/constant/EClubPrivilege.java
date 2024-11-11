package com.xiuxiu.app.server.club.constant;

public enum EClubPrivilege {
    ADD(                            0x00001, "添加成员"),
    DEL(                            0x00002, "删除成员"),
    SET_MANAGER(                    0x00004, "设置管理"),
    SET_MEMBER_UPLINE(              0x00008, "设置成员上线"),
    SET_MEMBER_ARENA(               0x00010, "设置成员竞技值"),
    MANAGER_MEMBER_SCORE(           0x00020, "管理成员竞技场战绩"),
    MANAGER_ARENA_SERVICE_CHARGE(   0x00040, "管理竞技场奖券"),
    MUTE(                           0x00080, "禁言"),
    DEL_CHAT(                       0x00100, "删除聊天消息"),
    RANK_ARENA(                     0x00200, "竞技场排行"),
    ANNOUNCEMENT(                   0x00400, "发布群公告"),
    SET_SHARE(                      0x00800, "设置群分享"),
    SET_FINANCE(                    0x01000, "财务管理"),
    FINANCE_UP(                     0x02000, "财务管理，上分"),
    FINANCE_DOWN(                   0x04000, "财务管理，下分"),
    FINANCE_CHAT(                   0x08000, "财务管理，聊天"),
    PLAY_AUTO_MODE(                 0x10000, "游戏托管"),
    SEND_ARENA_VALUE(               0x20000, "赠送竞技值"),
    ALL_PRIVILEGE(                  0xFFFFFFFF, "所有权限")
    ;

    private String desc;
    private long value;

    EClubPrivilege(long value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public long getValue() {
        return value;
    }
}
