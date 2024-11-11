package com.xiuxiu.app.server.player;

import com.xiuxiu.app.server.Switch;

public enum EPlayerPrivilegeLevel {
    ZERO(0,                     0x00000000, EPlayerPrivilege.NONE),
    FRIEND_NUM_0(0,             0x00000001, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_1(10,            0x00000002, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_2(20,            0x00000003, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_3(50,            0x00000004, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_4(100,           0x00000005, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_5(500,           0x00000006, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_6(2000,          0x00000007, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_7(10000,         0x00000008, EPlayerPrivilege.FRIEND_NUM),
    FRIEND_NUM_8(999999,        0x00000009, EPlayerPrivilege.FRIEND_NUM),
    GROUP_NUM_0(0,              0x00000010, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_1(3,              0x00000020, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_2(5,              0x00000030, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_3(10,             0x00000040, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_4(20,             0x00000050, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_5(50,             0x00000060, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_6(100,            0x00000070, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_7(500,            0x00000080, EPlayerPrivilege.GROUP_NUM),
    GROUP_NUM_8(999999,         0x00000090, EPlayerPrivilege.GROUP_NUM),
    GROUP_MEMBER_0(1,           0x00000100, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_1(50,          0x00000200, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_2(100,         0x00000300, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_3(200,         0x00000400, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_4(500,         0x00000500, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_5(1000,        0x00000600, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_6(5000,        0x00000700, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_7(10000,       0x00000800, EPlayerPrivilege.GROUP_MEMBER_NUM),
    GROUP_MEMBER_8(999999,      0x00000900, EPlayerPrivilege.GROUP_MEMBER_NUM),
    ARENA_NUM_0(0,              0x00001000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_1(3,              0x00002000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_2(5,              0x00003000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_3(10,             0x00004000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_4(20,             0x00005000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_5(50,             0x00006000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_6(100,            0x00007000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_7(500,            0x00008000, EPlayerPrivilege.ARENA_NUM),
    ARENA_NUM_8(999999,         0x00009000, EPlayerPrivilege.ARENA_NUM),
    SEARCH_PLAYER_NUM_0(5,      0x00010000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_1(10,     0x00020000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_2(20,     0x00030000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_3(50,     0x00040000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_4(100,    0x00050000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_5(200,    0x00060000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_6(500,    0x00070000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_7(1000,   0x00080000, EPlayerPrivilege.SEARCH_PLAYER),
    SEARCH_PLAYER_NUM_8(999999, 0x00090000, EPlayerPrivilege.SEARCH_PLAYER),
    LEAGUE_NUM_0(0,             0x00100000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_1(1,             0x00200000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_2(5,             0x00300000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_3(10,            0x00400000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_4(20,            0x00500000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_5(50,            0x00600000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_6(100,           0x00700000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_7(500,           0x00800000, EPlayerPrivilege.LEAGUE_NUM),
    LEAGUE_NUM_8(999999,        0x00900000, EPlayerPrivilege.LEAGUE_NUM),
    ;

    private int value;
    private int privilegeValue;
    private EPlayerPrivilege privilege;

    EPlayerPrivilegeLevel(int value, int privilegeValue, EPlayerPrivilege privilege) {
        this.value = value;
        this.privilegeValue = privilegeValue;
        this.privilege = privilege;
    }

    public int getValue() {
        return this.value;
    }

    public EPlayerPrivilege getPrivilege() {
        return privilege;
    }

    public int getPrivilegeValue() {
        return privilegeValue;
    }

    public static long getAllPrivilege(int level) {
        long value = 0;
        int temp = level;
        int index = 0;
        while (0 != temp) {
            int l = temp & 0x000f;
            if (l > 1) {
                value |= EPlayerPrivilegeLevel.values()[index * 9 + 1 - index + l].getPrivilege().getValue();
            }
            ++index;
            temp >>= 4;
        }
        return value;
    }

    public static int getValue(int level, EPlayerPrivilege privilege) {
        if (EPlayerPrivilege.NONE == privilege) {
            return 0;
        }
        if ((Switch.PLAYER_PRIVILEGE_VERIFY & privilege.getValue()) > 0) {
            return 999999;
        }
        int temp = level & (0x0f << (privilege.getIndex() * 4));
        temp >>= (privilege.getIndex() * 4);
        if (0 == temp) {
            temp = 1;
        }
        temp = 9 * privilege.getIndex() + temp;
        return EPlayerPrivilegeLevel.values()[temp].getValue();
    }
}
