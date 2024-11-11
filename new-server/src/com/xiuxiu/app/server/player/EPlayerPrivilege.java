package com.xiuxiu.app.server.player;

public enum EPlayerPrivilege {
    NONE(                   0x00000000, -1, "无"),
    FRIEND_NUM(             0x00000001, 0, "好友数量"),
    GROUP_NUM(              0x00000002, 1, "群数量"),
    GROUP_MEMBER_NUM(       0x00000004, 2, "群成员数量"),
    ARENA_NUM(              0x00000008, 3, "竞技场数量"),
    SEARCH_PLAYER(          0x00000010, 4, "搜索玩家数量"),
    LEAGUE_NUM(             0x00000020, 5, "盟数量"),
    ;

    private String desc;
    private long value;
    private int index;

    EPlayerPrivilege(long value, int index, String desc) {
        this.value = value;
        this.index = index;
        this.desc = desc;
    }

    public long getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }
}
