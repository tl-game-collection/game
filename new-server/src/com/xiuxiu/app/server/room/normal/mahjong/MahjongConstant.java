package com.xiuxiu.app.server.room.normal.mahjong;

public final class MahjongConstant {
    public static final long MJ_ROOM_FLUTTER_WAIT_TIME = 12 * 1000;                 // 选飘等待时间
    public static final long MJ_ROOM_FLUTTER_DEFAULT_WAIT_TIME = 32 * 1000;         // 选飘等待时间
    public static final long MJ_ROOM_SHUKAN_WAIT_TIME = 12 * 1000;                  // 数坎等待时间
    public static final long MJ_ROOM_SHUKAN_DEFAULT_WAIT_TIME = 32 * 1000;          // 数坎等待时间
    public static final long MJ_ROOM_HUAN_PAI_DEFAULT_WAIT_TIME = 12 * 1000;        // 换牌等待时间
    public static final long MJ_ROOM_HUAN_PAI_WAIT_TIME = 32 * 1000;                // 换牌等待时间
    public static final long MJ_ROOM_SHUAI_PAI_DEFAULT_WAIT_TIME = 12 * 1000;       // 甩牌等待时间
    public static final long MJ_ROOM_SHUAI_PAI_WAIT_TIME = 32 * 1000;               // 甩牌时间
    public static final long MJ_ROOM_DING_QUE_DEFAULT_WAIT_TIME = 12 * 1000;        // 定缺等待时间
    public static final long MJ_ROOM_DING_QUE_WAIT_TIME = 32 * 1000;                // 定缺时间

    public static final int MJ_NODE_TYPE_BUMP = 0;          // 碰
    public static final int MJ_NODE_TYPE_EAT = 1;           // 吃
    public static final int MJ_NODE_TYPE_BAR = 2;           // 杠
    public static final int MJ_NODE_TYPE_BAR_DARK = 3;      // 暗杠

    public static final int MJ_BAR_TYPE_MING = 1;           // 明杠
    public static final int MJ_BAR_TYPE_AN = 2;             // 暗杠
    public static final int MJ_BAR_TYPE_FANG = 3;           // 放杠

    public static final int MJ_HU_TYPE_NONE = -1;           // 没有胡
    public static final int MJ_HU_TYPE_NORMAL = 0;          // 普通胡
    public static final int MJ_HU_TYPE_CHAO_TIAN = 1;       // 朝天胡
    public static final int MJ_HU_TYPE_BAR = 2;             // 杠上胡(杠上开花)(自摸)
    public static final int MJ_HU_TYPE_BAR_PAO = 3;         // 杠上炮(点炮)

    public static final int MJ_CARD_KINDS = 43;

    public static final byte MJ_DONG = 28;                  // 东
    public static final byte MJ_ZHONG = 32;                 // 中
    public static final byte MJ_FA = 33;                    // 发
    public static final byte MJ_BAI = 34;                   // 白
    public static final byte MJ_ONE_TIAO = 10;              // 一条
    public static final byte MJ_TWO_TIAO = 11;              // 二条
    public static final byte MJ_FIVE_TIAO = 14;             // 五条
    public static final byte MJ_EIGHT_TIAO = 17;            // 八条
    public static final byte MJ_ONE_TONG = 19;              // 一筒
    public static final byte MJ_TWO_TONG = 20;              // 二筒
    public static final byte MJ_FIVE_TONG = 23;             // 五筒
    public static final byte MJ_EIGHT_TONG = 26;            // 八筒

    public static String[] CARDS = new String[]{"",
            "一万", "二万", "三万", "四万", "五万", "六万", "七万", "八万", "九万",           /*1-9*/
            "一条", "二条", "三条", "四条", "五条", "六条", "七条", "八条", "九条",           /*10-18*/
            "一筒", "二筒", "三筒", "四筒", "五筒", "六筒", "七筒", "八筒", "九筒",           /*19-27*/
            "东  ", "南  ", "西 ", "北  ", "中 ", "发  ", "白  ",                          /*28-34*/
            "春  ", "夏  ", "秋 ", "冬  ", "梅 ", "兰  ", "菊  ", "竹 "};                  /*35-42*/
    public static byte[] HORSE_SCORE = new byte[]{0,
            1, 2, 3, 4, 5, 6, 7, 8, 9,           /*1-9*/
            1, 2, 3, 4, 5, 6, 7, 8, 9,           /*10-18*/
            1, 2, 3, 4, 5, 6, 7, 8, 9,           /*19-27*/
            10, 10, 10, 10, 10, 10, 10};         /*28-34*/

}