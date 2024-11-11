package com.xiuxiu.app.server.room.normal.mahjong2.utils;

public interface MahjongFeatures {
    // 配牌
    long WITH_WAN_ZI        = 0x00000001L; // 有万子牌
    long WITH_TONG_ZI       = 0x00000002L; // 有筒子（饼子）牌
    long WITH_TIAO_ZI       = 0x00000004L; // 有条子牌
    long WITH_FENG          = 0x00000008L; // 有风牌：东、南、西、北
    long WITH_JIAN          = 0x00000010L; // 有箭牌：中、发、白
    long WITH_HUA           = 0x00000020L; // 有花牌：春、夏、秋、冬、梅、兰、竹、菊

    // 玩法
    long ENABLE_CHI         = 0x00000100L; // 允许吃
    long ENABLE_PENG        = 0x00000200L; // 允许碰
    long ENABLE_MING_GANG   = 0x00000400L; // 允许明杠
    long ENABLE_AN_GANG     = 0x00000800L; // 允许暗杠
    long ENABLE_HUA_GANG    = 0x00001000L; // 允许花杠/花补
    long ENABLE_TING        = 0x00002000L; // 允许听
}
