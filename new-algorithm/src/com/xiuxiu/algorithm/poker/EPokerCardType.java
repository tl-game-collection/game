package com.xiuxiu.algorithm.poker;

public enum EPokerCardType {
    NONE("空", 0),
    SINGLE("单牌", 1),
    DOUBLE("对子", 2),
    THREE("三张", 3),
    DOUBLE_LINE("连对", 5),
    SINGLE_LINE("顺子", 4),
    THREE_LINE("飞机", 6),
    THREE_TAKE_ONE("三带一", 7),//三带一 （1对 或者 1单）
    THREE_TAKE_TWO("三带二", 8),//三带二  (2对 或者 2单)
    FOUR_TAKE_THREE("四带三", 9),
    BOMB("炸弹", 10),
    KING_FRIED("王炸", 11),
    COW_NONE("无牛", 12),
    COW_1("牛1", 13),
    COW_2("牛2", 14),
    COW_3("牛3", 15),
    COW_4("牛4", 16),
    COW_5("牛5", 17),
    COW_6("牛6", 18),
    COW_7("牛7", 19),
    COW_8("牛8", 20),
    COW_9("牛9", 21),
    COW_10("牛牛", 22),
    COW_STRAIGHT("牛-顺子牛", 23),
    COW_SILVER("牛-银牛", 24),
    COW_SAME_COLOR("牛-同花牛", 25),
    COW_GOLD("牛-金牛", 26),
    COW_CUCURBIT("牛-葫芦牛", 27),
    COW_FIVE_SMALL("牛-五小牛", 28),
    COW_BOMB("牛-炸弹牛", 29),
    COW_DRAGON("牛-一条龙", 30),
    COW_WITH_THE_FLOWER("牛-同花顺", 31),
    FGF_NONE("扎金花-高牌", 32),
    FGF_235("扎金花-高牌", 33),
    FGF_DOUBLE("扎金花--对子", 34),
    FGF_LINE("扎金花--顺子", 35),
    FGF_SAME_COLOR("扎金花--同花", 36),
    FGF_SAME_COLOR_AND_LINE("扎金花--同花顺", 37),
    FGF_THREE("扎金花--豹子", 38),
    FOUR_TAKE_TWO_SINGLE("四带二单", 39),
    FOUR_TAKE_TWO_DOUBLE("四带二对", 40),
    //十三水-普通牌型
    ZA_PAI("十三水-杂牌", 41, 1),
    DUI_ZI("十三水-对子", 42, 1),
    ER_DUI("十三水-2对", 43, 1),
    SAN_TIAO("十三水-三条", 44, 1),
    SHUN_ZI("十三水-顺子", 45, 1),
    TONG_HUA("十三水-同花", 46, 1),
    HU_LU("十三水-葫芦", 47, 1),
    SI_TIAO("十三水-四条", 48, 4),
    TONG_HUA_SHUN("十三水-同花顺", 49, 5),
    WU_TIAO("十三水-五条", 50, 10),
    //十三水-怪物牌型
    SAN_SHUN_ZI("十三水-三顺子", 51, 4),
    SAN_TONG_HUA("十三水-三同花", 52, 4),
    LIU_DUI_BAN("十三水-六对半", 53, 6),
    WU_DUI_SAN_TIAO("十三水-五对三条", 54, 6),
    SI_TAO_SAN_TIAO("十三水-四套三条", 55, 6),
    ZHONG_YUAN_YI_DIAN_HEI("十三水-中原一点黑", 56, 8),
    ZHONG_YUAN_YI_DIAN_HONG("十三水-中原一点红", 57, 8),
    QUAN_HEI("十三水-全黑", 58, 8),
    QUAN_HONG("十三水-全红", 59, 8),
    QUAN_XIAO("十三水-全小", 60, 12),
    QUAN_DA("十三水-全大", 61, 16),
    COU_YI_SE("十三水-凑一色", 62, 18),
    SAN_TAO_ZHA_DAN("十三水-三套炸弹", 63, 20),
    SAN_TONG_HUA_SHUN("十三水-三同花顺", 64, 22),
    SHI_ER_HUANG_ZU("十三水-十二皇族", 65, 24),
    SHI_SAN_SHUI("十三水-十三水", 66, 36),
    QI_XING_LIAN_ZHU("十三水-七星连珠", 67, 40),
    BA_XIAN_GUO_HAI("十三水-八仙过海", 68, 80),
    ZHI_ZUN_QING_LONG("十三水-至尊清龙", 69, 108),
    WUSHIK_WSKBOMB("510k_510K炸", 70),
    WUSHIK_WSKBIGBOMB("510k_big510K炸", 71),
    WUSHIK_FOURUP("510k_四张相同牌及以上", 72),
    WUSHIK_THREELINE("510K_三顺",73),//由2个以上相连的数字三张组成
    WUSHIK_FOURKINGBOMB("510K_四张王 王炸",74),
    WUSHIK_THREEKINGBOMB("510K_三张王 王炸",75),
    WUSHIK_THREELINE_TAKE_ONE("510K_三顺带一",76),
    WUSHIK_THREELINE_TAKE_TWO("510K_三顺带二",77),
    WUSHIK_SINGLINE("510K_顺子",78),//最小A2345 ,K不能和2组成顺子（如QKA23不能组成）
    
    //德州
    TEXAS_HIGH_CARD("高牌",79),
    TEXAS_ONE_DOUBLE("1对",80),
    TEXAS_TWO_DOUBLE("2对",81),
    TEXAS_SAN_TIAO("三条",82),
    TEXAS_LINE("顺子",83),//不同花色连续的牌；
    TEXAS_SAME_COLOR("同花",84),//5张相同花色；
    TEXAS_HU_LU("葫芦",85),//3张相同 + 1对；
    TEXAS_SI_TIAO("四条",86),
    TEXAS_SAME_COLOR_AND_LINE("同花顺",87),
    TEXAS_BIG_SAME_COLOR_AND_LINE("皇家同花顺",88),
    
    FOUR_TAKE_TWO("四带二", 89),
    THREE_TAKE_11("三带一对", 90),
    BOBBIN_SINGLE("单",91),
    BOBBIN_28("二八杠",92, 12),
    BOBBIN_DOUBLE("一对",93, 15),
    BOBBIN_ZHIZHUN("一对白板(至尊)", 94, 20),
    ;

    private String desc;
    private byte value;
    private int mul;

    EPokerCardType(String desc, int value) {
        this.desc = desc;
        this.value = (byte) value;
    }

    EPokerCardType(String desc, int value, int mul) {
        this.desc = desc;
        this.value = (byte) value;
        this.mul = mul;
    }

    public String getDesc() {
        return this.desc;
    }

    public byte getValue() {
        return this.value;
    }

    public int getMul() {
        return mul;
    }

    public static EPokerCardType parse(byte value) {
        switch (value) {
            case 1:
                return SINGLE;
            case 2:
                return DOUBLE;
            case 3:
                return THREE;
            case 4:
                return SINGLE_LINE;
            case 5:
                return DOUBLE_LINE;
            case 6:
                return THREE_LINE;
            case 7:
                return THREE_TAKE_ONE;
            case 8:
                return THREE_TAKE_TWO;
            case 9:
                return FOUR_TAKE_THREE;
            case 10:
                return BOMB;
            case 11:
                return KING_FRIED;
            case 12:
                return COW_NONE;
            case 13:
                return COW_1;
            case 14:
                return COW_2;
            case 15:
                return COW_3;
            case 16:
                return COW_4;
            case 17:
                return COW_5;
            case 18:
                return COW_6;
            case 19:
                return COW_7;
            case 20:
                return COW_8;
            case 21:
                return COW_9;
            case 22:
                return COW_10;
            case 23:
                return COW_STRAIGHT;
            case 24:
                return COW_SILVER;
            case 25:
                return COW_SAME_COLOR;
            case 26:
                return COW_GOLD;
            case 27:
                return COW_CUCURBIT;
            case 28:
                return COW_FIVE_SMALL;
            case 29:
                return COW_BOMB;
            case 30:
                return COW_DRAGON;
            case 31:
                return COW_WITH_THE_FLOWER;
            case 39:
                return FOUR_TAKE_TWO_SINGLE;
            case 40:
                return FOUR_TAKE_TWO_DOUBLE;
            case 41:
                return ZHI_ZUN_QING_LONG;
            case 42:
                return BA_XIAN_GUO_HAI;
            case 43:
                return QI_XING_LIAN_ZHU;
            case 44:
                return SHI_SAN_SHUI;
            case 45:
                return SHI_ER_HUANG_ZU;
            case 46:
                return SAN_TONG_HUA_SHUN;
            case 47:
                return SAN_TAO_ZHA_DAN;
            case 48:
                return COU_YI_SE;
            case 49:
                return QUAN_DA;
            case 50:
                return QUAN_XIAO;
            case 51:
                return QUAN_HONG;
            case 52:
                return QUAN_HEI;
            case 53:
                return ZHONG_YUAN_YI_DIAN_HONG;
            case 54:
                return ZHONG_YUAN_YI_DIAN_HEI;
            case 55:
                return SI_TAO_SAN_TIAO;
            case 56:
                return WU_DUI_SAN_TIAO;
            case 57:
                return LIU_DUI_BAN;
            case 58:
                return SAN_TONG_HUA;
            case 59:
                return SAN_SHUN_ZI;
            case 60:
                return WU_TIAO;
            case 61:
                return TONG_HUA_SHUN;
            case 62:
                return SI_TIAO;
            case 63:
                return HU_LU;
            case 64:
                return TONG_HUA;
            case 65:
                return SHUN_ZI;
            case 66:
                return SAN_TIAO;
            case 67:
                return ER_DUI;
            case 68:
                return DUI_ZI;
            case 69:
                return ZA_PAI;
            case 70:
                return WUSHIK_WSKBOMB;
            case 71:
                return WUSHIK_WSKBIGBOMB;
            case 72:
                return WUSHIK_FOURUP;
            case 73:
                return WUSHIK_THREELINE;
            case 74:
                return WUSHIK_FOURKINGBOMB;
            case 75:
                return WUSHIK_THREEKINGBOMB;
            case 76:
                return WUSHIK_THREELINE_TAKE_ONE;
            case 77:
                return WUSHIK_THREELINE_TAKE_TWO;
            case 78:
                return WUSHIK_SINGLINE;
            case 79:
                return TEXAS_HIGH_CARD;
            case 80:
                return TEXAS_ONE_DOUBLE;
            case 81:
                return TEXAS_TWO_DOUBLE;
            case 82:
                return TEXAS_SAN_TIAO;
            case 83:
                return TEXAS_LINE;
            case 84:
                return TEXAS_SAME_COLOR;
            case 85:
                return TEXAS_HU_LU;
            case 86:
                return TEXAS_SI_TIAO;
            case 87:
                return TEXAS_SAME_COLOR_AND_LINE;
            case 88:
                return TEXAS_BIG_SAME_COLOR_AND_LINE;
            case 89:
                return FOUR_TAKE_TWO;
            case 90:
                return THREE_TAKE_11;
            case 91:
                return BOBBIN_SINGLE;
            case 92:
                return BOBBIN_28;
            case 93:
                return BOBBIN_DOUBLE;
            case 94:
                return BOBBIN_ZHIZHUN;


        }
        return NONE;
    }
}