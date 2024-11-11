package com.xiuxiu.algorithm.mahjong;

import com.xiuxiu.algorithm.poker.EPokerCardType;

import java.util.Collections;
import java.util.List;

public final class MahjongUtil {
    public static final int MJ_CARD_KINDS = 43;
    public static final int COLOR_WANG = 0;                 // 颜色--万
    public static final int COLOR_TIAO = 1;                 // 颜色--条
    public static final int COLOR_TONG = 2;                 // 颜色--筒
    public static final int COLOR_FENG = 3;                 // 颜色--东,...
    public static final int COLOR_HUA = 4;                  // 颜色--春,...

    public static final byte MJ_1_WANG = 1;                 // 1万
    public static final byte MJ_2_WANG = 2;                 // 2万
    public static final byte MJ_3_WANG = 3;                 // 3万
    public static final byte MJ_4_WANG = 4;                 // 4万
    public static final byte MJ_5_WANG = 5;                 // 5万
    public static final byte MJ_6_WANG = 6;                 // 6万
    public static final byte MJ_7_WANG = 7;                 // 7万
    public static final byte MJ_8_WANG = 8;                 // 8万
    public static final byte MJ_9_WANG = 9;                 // 9万
    public static final byte MJ_1_TIAO = 10;                // 1条
    public static final byte MJ_2_TIAO = 11;                // 2条
    public static final byte MJ_3_TIAO = 12;                // 3条
    public static final byte MJ_4_TIAO = 13;                // 4条
    public static final byte MJ_5_TIAO = 14;                // 5条
    public static final byte MJ_6_TIAO = 15;                // 6条
    public static final byte MJ_7_TIAO = 16;                // 7条
    public static final byte MJ_8_TIAO = 17;                // 8条
    public static final byte MJ_9_TIAO = 18;                // 9条
    public static final byte MJ_1_TONG = 19;                // 1筒
    public static final byte MJ_2_TONG = 20;                // 2筒
    public static final byte MJ_3_TONG = 21;                // 3筒
    public static final byte MJ_4_TONG = 22;                // 4筒
    public static final byte MJ_5_TONG = 23;                // 5筒
    public static final byte MJ_6_TONG = 24;                // 6筒
    public static final byte MJ_7_TONG = 25;                // 7筒
    public static final byte MJ_8_TONG = 26;                // 8筒
    public static final byte MJ_9_TONG = 27;                // 9筒
    public static final byte MJ_D_FENG = 28;                // 东风
    public static final byte MJ_N_FENG = 29;                // 南风
    public static final byte MJ_X_FENG = 30;                // 西风
    public static final byte MJ_B_FENG = 31;                // 北风
    public static final byte MJ_Z_FENG = 32;                // 中
    public static final byte MJ_F_FENG = 33;                // 发财
    public static final byte MJ_BAI_FENG = 34;              // 白板
    public static final byte MJ_ZHU_HUA = 42;               // 竹--花色

    public static String[] CARDS = new String[] {"",
            "一万", "二万", "三万", "四万", "五万", "六万", "七万", "八万", "九万",           /*1-9*/
            "一条", "二条", "三条", "四条", "五条", "六条", "七条", "八条", "九条",           /*10-18*/
            "一筒", "二筒", "三筒", "四筒", "五筒", "六筒", "七筒", "八筒", "九筒",           /*19-27*/
            "东  ", "南  ", "西  ", "北  ", "中  ", "发  ", "白  ",                           /*28-34*/
            "春  ", "夏  ", "秋  ", "冬  ", "梅  ", "兰  ", "菊  ", "竹  "                    /*35-42*/
    };

    public static String getCardStr(byte card) {
        if (card < MJ_1_WANG || card > MJ_ZHU_HUA) {
            return "无  ";
        }
        return CARDS[card];
    }

    private static final ThreadLocal<byte[]> temp = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[MJ_CARD_KINDS];
        }
    };

    /**
     * 获取28筒子牌型
     * @param cards
     * @return
     */
    public static EPokerCardType getBobbinCardType(List<Byte> cards) {
        if (null != cards && cards.size() == 2) {
            byte card1 = cards.get(0);
            byte card2 = cards.get(1);
            if (card1 == card2) {
                if (card1 == MJ_BAI_FENG) {
                    return EPokerCardType.BOBBIN_ZHIZHUN;
                } else {
                    return EPokerCardType.BOBBIN_DOUBLE;
                }
            } else if (cards.contains(MJ_2_TONG) && cards.contains(MJ_8_TONG)) {
                return EPokerCardType.BOBBIN_28;
            } else {
                return EPokerCardType.BOBBIN_SINGLE;
            }
        }
        return EPokerCardType.NONE;
    }

    /**
     * 获取麻将颜色
     * @param c
     * @return
     */
    public static int getColor(byte c) {
        if (c < 1) {
            return -1;
        }
        if (c <= MahjongUtil.MJ_9_WANG) {
            return 0;
        } else if (c <= MahjongUtil.MJ_9_TIAO) {
            return 1;
        } else if (c <= MahjongUtil.MJ_9_TONG) {
            return 2;
        } else if (c <= MahjongUtil.MJ_BAI_FENG) {
            return 3;
        } else if (c <= MahjongUtil.MJ_ZHU_HUA) {
            return 4;
        }
        return -1;
    }

    /**
     * 判断胡
     * @param card  手牌
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card) {
        return isHu(card, 0);
    }

    /**
     * 判断胡
     * @param card  手牌
     * @param dep   吃,碰,杠个数
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep) {
        return isHu(card, dep, true);
    }

    /**
     * 判断胡
     * @param card          手牌
     * @param dep           吃,碰,杠个数
     * @param sevenPair     是否胡七对
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, boolean sevenPair) {
        return isHu(card, dep, sevenPair, (byte) -1);
    }

    /**
     * 判断胡
     * @param card      手牌
     * @param dep       吃,碰,杠个数
     * @param laiZi     癞子, -1: 表示没有癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, byte laiZi) {
        return isHu(card, dep, true, laiZi);
    }

    /**
     * 判断胡
     * @param card          手牌
     * @param dep           吃,碰,杠个数
     * @param laiZi         癞子, -1: 表示没有癞子
     * @param eyeUseLaiZi   将牌是否使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, byte laiZi, boolean eyeUseLaiZi) {
        return isHu(card, dep, true, laiZi, eyeUseLaiZi);
    }

    /**
     * 判断是否胡(通用)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, boolean sevenPair, byte laiZi) {
        return isHu(card, dep, sevenPair, laiZi, true);
    }

    /**
     * 判断是否胡(通用)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param eyeUseLaiZi       将牌是否使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, boolean sevenPair, byte laiZi, boolean eyeUseLaiZi) {
        return isHu(card, dep, sevenPair, laiZi, Integer.MAX_VALUE, eyeUseLaiZi, 14, false);
    }

    /**
     * 判断是否胡(通用)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, byte laiZi, int useLaiZiCnt) {
        return isHu(card, dep, laiZi, useLaiZiCnt, true);
    }

    /**
     * 判断是否胡(通用)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @param eyeUseLaiZi       将牌是否使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, byte laiZi, int useLaiZiCnt, boolean eyeUseLaiZi) {
        return isHu(card, dep, true, laiZi, useLaiZiCnt, eyeUseLaiZi, 14,false);
    }

    /**
     * 判断是否胡(通用)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, boolean sevenPair, byte laiZi, int useLaiZiCnt) {
        return isHu(card, dep, sevenPair, laiZi, useLaiZiCnt, true, 14,false);
    }
    /**
     * 判断胡
     * @param card      手牌
     * @param dep       吃,碰,杠个数
     * @param laiZi     癞子, -1: 表示没有癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHuWithSpecialShunzi(byte[] card, int dep, byte laiZi, boolean specialShunzi) {
        return isHu(card, dep, true, laiZi, Integer.MAX_VALUE, true, 14, specialShunzi);
    }
    /**
     * 判断是否胡(通用)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @param specialShunzi     是否启用风字牌顺子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, boolean sevenPair, byte laiZi, int useLaiZiCnt, boolean specialShunzi) {
        return isHu(card, dep, sevenPair, laiZi, useLaiZiCnt, true, 14, specialShunzi);
    }

    /**
     * 判断是否胡(通用)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @param eyeUseLaiZi       将牌是否使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu(byte[] card, int dep, boolean sevenPair, byte laiZi, int useLaiZiCnt, boolean eyeUseLaiZi, int cardCnt, boolean specialShunzi) {
        int laiZiCnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            temp.get()[i] = card[i];
            if (-1 != laiZi && laiZi == i) {
                laiZiCnt = card[i];
            }
        }
        if (laiZiCnt > useLaiZiCnt) {
            laiZiCnt = useLaiZiCnt;
        }
        if (laiZiCnt > 0) {
            temp.get()[laiZi] -= laiZiCnt;
        }
        if (sevenPair && isSevenPair(temp.get(), laiZiCnt)) {
            return true;
        }
        int depCnt = 4;
        if (17 == cardCnt) {
            depCnt = 5;
        }
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (temp.get()[i] < 1) {
                continue;
            }
            if (temp.get()[i] >= 2) {
                if (dep >= depCnt) {
                    return true;
                }
                temp.get()[i] -= 2;
                if (searchHu0(temp.get(), dep, laiZiCnt, depCnt, specialShunzi)) {
                    return true;
                }
                temp.get()[i] += 2;
            } else if (eyeUseLaiZi && laiZiCnt > 0) {
                if (dep >= depCnt) {
                    return true;
                }
                temp.get()[i] -= 1;
                if (searchHu0(temp.get(), dep, laiZiCnt - 1, depCnt, specialShunzi)) {
                    return true;
                }
                temp.get()[i] += 1;
            }
        }
        if (eyeUseLaiZi && laiZiCnt >= 2) {
            if (dep >= depCnt) {
                return true;
            }
            if (searchHu0(temp.get(), dep, laiZiCnt - 2, depCnt, specialShunzi)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断胡(258将)
     * @param card  手牌
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card) {
        return isHu258Eye(card, 0);
    }

    /**
     * 判断胡(258将)
     * @param card  手牌
     * @param dep   吃,碰,杠个数
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep) {
        return isHu258Eye(card, dep, true);
    }

    /**
     * 判断胡(258将)
     * @param card          手牌
     * @param dep           吃,碰,杠个数
     * @param sevenPair     是否胡七对
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, boolean sevenPair) {
        return isHu258Eye(card, dep, sevenPair, (byte) -1);
    }

    /**
     * 判断胡(258将)
     * @param card      手牌
     * @param dep       吃,碰,杠个数
     * @param laiZi     癞子, -1: 表示没有癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, byte laiZi) {
        return isHu258Eye(card, dep, true, laiZi);
    }

    /**
     * 判断胡(258将)
     * @param card          手牌
     * @param dep           吃,碰,杠个数
     * @param laiZi         癞子, -1: 表示没有癞子
     * @param eyeUseLaiZi   将牌是否使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, byte laiZi, boolean eyeUseLaiZi) {
        return isHu258Eye(card, dep, true, laiZi, eyeUseLaiZi);
    }

    /**
     * 判断是否胡(258将)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, boolean sevenPair, byte laiZi) {
        return isHu258Eye(card, dep, sevenPair, laiZi, true);
    }

    /**
     * 判断是否胡(258将)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param eyeUseLaiZi       将牌是否使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, boolean sevenPair, byte laiZi, boolean eyeUseLaiZi) {
        return isHu258Eye(card, dep, sevenPair, laiZi, Integer.MAX_VALUE, eyeUseLaiZi, 14);
    }

    /**
     * 判断是否胡(258将)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, byte laiZi, int useLaiZiCnt) {
        return isHu258Eye(card, dep, laiZi, useLaiZiCnt, true);
    }

    /**
     * 判断是否胡(258将)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @param eyeUseLaiZi       将牌是否使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, byte laiZi, int useLaiZiCnt, boolean eyeUseLaiZi) {
        return isHu258Eye(card, dep, true, laiZi, useLaiZiCnt, eyeUseLaiZi, 14);
    }

    /**
     * 判断是否胡(258将)
     * @param card              手牌
     * @param dep               吃,碰,杠个数
     * @param sevenPair         是否胡七对
     * @param laiZi             癞子牌子, -1: 表示不使用癞子
     * @param useLaiZiCnt       使用癞子牌数, 0: 表示不使用癞子
     * @return  true: 胡, false: 不胡
     */
    public static boolean isHu258Eye(byte[] card, int dep, boolean sevenPair, byte laiZi, int useLaiZiCnt) {
        return isHu258Eye(card, dep, sevenPair, laiZi, useLaiZiCnt, true, 14);
    }

    /**
     * 是否胡2,5,8将
     * @param card
     * @param dep
     * @param sevenPair
     * @param laiZi
     * @param useLaiZiCnt
     * @param eyeUseLaiZi
     * @param cardCnt
     * @return
     */
    public static boolean isHu258Eye(byte[] card, int dep, boolean sevenPair, byte laiZi, int useLaiZiCnt, boolean eyeUseLaiZi, int cardCnt) {
        int laiZiCnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            temp.get()[i] = card[i];
            if (-1 != laiZi && laiZi == i) {
                laiZiCnt = card[i];
            }
        }
        if (laiZiCnt > useLaiZiCnt) {
            laiZiCnt = useLaiZiCnt;
        }
        if (laiZiCnt > 0) {
            temp.get()[laiZi] -= laiZiCnt;
        }
        if (sevenPair && isSevenPair(temp.get(), laiZiCnt)) {
            return true;
        }
        int depCnt = 4;
        if (17 == cardCnt) {
            depCnt = 5;
        }
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (temp.get()[i] < 1) {
                continue;
            }
            if (is258((byte) i)) {
                if (temp.get()[i] >= 2) {
                    if (dep >= depCnt) {
                        return true;
                    }
                    temp.get()[i] -= 2;
                    if (searchHu0(temp.get(), dep, laiZiCnt, depCnt, false)) {
                        return true;
                    }
                    temp.get()[i] += 2;
                } else if (eyeUseLaiZi && laiZiCnt > 0) {
                    if (dep >= depCnt) {
                        return true;
                    }
                    temp.get()[i] -= 1;
                    if (searchHu0(temp.get(), dep, laiZiCnt - 1, depCnt, false)) {
                        return true;
                    }
                    temp.get()[i] += 1;
                }
            }
        }
        if (eyeUseLaiZi && laiZiCnt >= 2) {
            if (dep >= depCnt) {
                return true;
            }
            if (searchHu0(temp.get(), dep, laiZiCnt - 2, depCnt, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否胡没有将
     * @param card
     * @param dep
     * @param sevenPair
     * @return
     */
    public static boolean isHuNoEye(byte[] card, int dep, boolean sevenPair) {
        return isHu258Eye(card, dep, sevenPair, (byte) -1);
    }

    /**
     * 是否胡没有将
     * @param card
     * @param dep
     * @param sevenPair
     * @param laiZi
     * @return
     */
    public static boolean isHuNoEye(byte[] card, int dep, boolean sevenPair, byte laiZi) {
        return isHuNoEye(card, dep, sevenPair, laiZi, Integer.MAX_VALUE, 14);
    }

    /**
     * 是否胡没有将
     * @param card
     * @param dep
     * @param sevenPair
     * @param laiZi
     * @param useLaiZiCnt
     * @param cardCnt
     * @return
     */
    public static boolean isHuNoEye(byte[] card, int dep, boolean sevenPair, byte laiZi, int useLaiZiCnt, int cardCnt) {
        int laiZiCnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            temp.get()[i] = card[i];
            if (-1 != laiZi && laiZi == i) {
                laiZiCnt = card[i];
            }
        }
        if (laiZiCnt > useLaiZiCnt) {
            laiZiCnt = useLaiZiCnt;
        }
        if (laiZiCnt > 0) {
            temp.get()[laiZi] -= laiZiCnt;
        }
        if (sevenPair && isSevenPair(temp.get(), laiZiCnt)) {
            return true;
        }
        int depCnt = 4;
        if (17 == cardCnt) {
            depCnt = 5;
        }
        if (searchHu0(temp.get(), dep, laiZiCnt, depCnt, false)) {
            return true;
        }

        return false;
    }

    private static boolean searchHu0(byte[] card, int dep, int laiZiCnt, int depCnt, boolean specialShunzi) {
        // 刻字
        int needDepCnt = depCnt - 1;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (card[i] >= 3) {
                if (dep >= needDepCnt) {
                    return true;
                }
                card[i] -= 3;
                if (searchHu0(card, dep + 1, laiZiCnt, depCnt, specialShunzi)) {
                    return true;
                }
                card[i] += 3;
            } else if (2 == card[i] && laiZiCnt >= 1) {
                if (dep >= needDepCnt) {
                    return true;
                }
                card[i] -= 2;
                if (searchHu0(card, dep + 1, laiZiCnt - 1, depCnt, specialShunzi)) {
                    return true;
                }
                card[i] += 2;
            } else if (1 == card[i] && laiZiCnt >= 2) {
                if (dep >= needDepCnt) {
                    return true;
                }
                card[i] -= 1;
                if (searchHu0(card, dep + 1, laiZiCnt - 2, depCnt, specialShunzi)) {
                    return true;
                }
                card[i] += 1;
            }
        }
        // 特殊顺子处理
        if (specialShunzi) {
//            // 东南西
//            if (specialShunzi(card, dep, laiZiCnt, depCnt, specialShunzi, MJ_D_FENG, MJ_N_FENG, MJ_X_FENG)) {
//                return true;
//            }
            if(!(card[MJ_D_FENG] <= 0 && card[MJ_N_FENG] <= 0 && card[MJ_X_FENG] <= 0)) {
                boolean  use1 = card[MJ_D_FENG] >= 1;
                boolean use2 = card[MJ_N_FENG] >= 1;
                boolean use3 = card[MJ_X_FENG] >= 1;
                int  needLaiZiCnt = (card[MJ_D_FENG] <= 0 ? 1 : 0) + (card[MJ_N_FENG] <= 0 ? 1 : 0) + (card[MJ_X_FENG] <= 0 ? 1 : 0);
                if(!(needLaiZiCnt > laiZiCnt)) {
                    if (dep >= needDepCnt) {
                        return true;
                    }
                    if (use1) card[MJ_D_FENG]--;
                    if (use2) card[MJ_N_FENG]--;
                    if (use3) card[MJ_X_FENG]--;
                    
                    if (needLaiZiCnt <= laiZiCnt && searchHu0(card, dep + 1, laiZiCnt - needLaiZiCnt, depCnt, specialShunzi)) {
                        return true;
                    }
                    if (use1) card[MJ_D_FENG]++;
                    if (use2) card[MJ_N_FENG]++;
                    if (use3) card[MJ_X_FENG]++;
                }
            }
//            // 东南北
//            if (specialShunzi(card, dep, laiZiCnt, depCnt, specialShunzi, MJ_D_FENG, MJ_N_FENG, MJ_B_FENG)) {
//                return true;
//            }
            if(!(card[MJ_D_FENG] <= 0 && card[MJ_N_FENG] <= 0 && card[MJ_B_FENG] <= 0)) {
                boolean  use1 = card[MJ_D_FENG] >= 1;
                boolean use2 = card[MJ_N_FENG] >= 1;
                boolean use3 = card[MJ_B_FENG] >= 1;
                int  needLaiZiCnt = (card[MJ_D_FENG] <= 0 ? 1 : 0) + (card[MJ_N_FENG] <= 0 ? 1 : 0) + (card[MJ_B_FENG] <= 0 ? 1 : 0);
                if(!(needLaiZiCnt > laiZiCnt)) {
                    if (dep >= needDepCnt) {
                        return true;
                    }
                    if (use1) card[MJ_D_FENG]--;
                    if (use2) card[MJ_N_FENG]--;
                    if (use3) card[MJ_B_FENG]--;
                    
                    if (needLaiZiCnt <= laiZiCnt && searchHu0(card, dep + 1, laiZiCnt - needLaiZiCnt, depCnt, specialShunzi)) {
                        return true;
                    }
                    if (use1) card[MJ_D_FENG]++;
                    if (use2) card[MJ_N_FENG]++;
                    if (use3) card[MJ_B_FENG]++;
                }
            }
//            // 东西北
//            if (specialShunzi(card, dep, laiZiCnt, depCnt, specialShunzi, MJ_D_FENG, MJ_X_FENG, MJ_B_FENG)) {
//                return true;
//            }
            if(!(card[MJ_D_FENG] <= 0 && card[MJ_X_FENG] <= 0 && card[MJ_B_FENG] <= 0)) {
                boolean  use1 = card[MJ_D_FENG] >= 1;
                boolean use2 = card[MJ_X_FENG] >= 1;
                boolean use3 = card[MJ_B_FENG] >= 1;
                int  needLaiZiCnt = (card[MJ_D_FENG] <= 0 ? 1 : 0) + (card[MJ_X_FENG] <= 0 ? 1 : 0) + (card[MJ_B_FENG] <= 0 ? 1 : 0);
                if(!(needLaiZiCnt > laiZiCnt)) {
                    if (dep >= needDepCnt) {
                        return true;
                    }
                    if (use1) card[MJ_D_FENG]--;
                    if (use2) card[MJ_X_FENG]--;
                    if (use3) card[MJ_B_FENG]--;
                    
                    if (needLaiZiCnt <= laiZiCnt && searchHu0(card, dep + 1, laiZiCnt - needLaiZiCnt, depCnt, specialShunzi)) {
                        return true;
                    }
                    if (use1) card[MJ_D_FENG]++;
                    if (use2) card[MJ_X_FENG]++;
                    if (use3) card[MJ_B_FENG]++;
                }
            }
//            // 南西北
//            if (specialShunzi(card, dep, laiZiCnt, depCnt, specialShunzi, MJ_N_FENG, MJ_X_FENG, MJ_B_FENG)) {
//                return true;
//            }
            // 南西北
            if(!(card[MJ_N_FENG] <= 0 && card[MJ_X_FENG] <= 0 && card[MJ_B_FENG] <= 0)) {
                boolean  use1 = card[MJ_N_FENG] >= 1;
                boolean use2 = card[MJ_X_FENG] >= 1;
                boolean use3 = card[MJ_B_FENG] >= 1;
                int  needLaiZiCnt = (card[MJ_N_FENG] <= 0 ? 1 : 0) + (card[MJ_X_FENG] <= 0 ? 1 : 0) + (card[MJ_B_FENG] <= 0 ? 1 : 0);
                if(!(needLaiZiCnt > laiZiCnt)) {
                    if (dep >= needDepCnt) {
                        return true;
                    }
                    if (use1) card[MJ_N_FENG]--;
                    if (use2) card[MJ_X_FENG]--;
                    if (use3) card[MJ_B_FENG]--;
                    
                    if (needLaiZiCnt <= laiZiCnt && searchHu0(card, dep + 1, laiZiCnt - needLaiZiCnt, depCnt, specialShunzi)) {
                        return true;
                    }
                    if (use1) card[MJ_N_FENG]++;
                    if (use2) card[MJ_X_FENG]++;
                    if (use3) card[MJ_B_FENG]++;
                }
            }
            
//            // 中发白
//            if (specialShunzi(card, dep, laiZiCnt, depCnt, specialShunzi, MJ_Z_FENG, MJ_F_FENG, MJ_BAI_FENG)) {
//                return true;
//            }
            
            // 中发白
            if(!(card[MJ_Z_FENG] <= 0 && card[MJ_F_FENG] <= 0 && card[MJ_BAI_FENG] <= 0)) {
                boolean  use1 = card[MJ_Z_FENG] >= 1;
                boolean use2 = card[MJ_F_FENG] >= 1;
                boolean use3 = card[MJ_BAI_FENG] >= 1;
                int  needLaiZiCnt = (card[MJ_Z_FENG] <= 0 ? 1 : 0) + (card[MJ_F_FENG] <= 0 ? 1 : 0) + (card[MJ_BAI_FENG] <= 0 ? 1 : 0);
                if(!(needLaiZiCnt > laiZiCnt)) {
                    if (dep >= needDepCnt) {
                        return true;
                    }
                    if (use1) card[MJ_Z_FENG]--;
                    if (use2) card[MJ_F_FENG]--;
                    if (use3) card[MJ_BAI_FENG]--;
                    
                    if (needLaiZiCnt <= laiZiCnt && searchHu0(card, dep + 1, laiZiCnt - needLaiZiCnt, depCnt, specialShunzi)) {
                        return true;
                    }
                    if (use1) card[MJ_Z_FENG]++;
                    if (use2) card[MJ_F_FENG]++;
                    if (use3) card[MJ_BAI_FENG]++;
                }
            }
        }
        // 顺子
        for (int i = 0; i < 25; ++i) {
            if (i % 9 >= 7) {
                continue;
            }
            if (card[i + 1] <= 0 && card[i + 2] <= 0 && card[i + 3] <= 0) {
                continue;
            }
            int needLaiZiCnt = (card[i + 1] <= 0 ? 1 : 0) + (card[i + 2] <= 0 ? 1 : 0) + (card[i + 3] <= 0 ? 1 : 0);
            if (needLaiZiCnt > laiZiCnt) {
                continue;
            }
            if (dep >= needDepCnt) {
                return true;
            }
            boolean use1 = card[i + 1] >= 1;
            boolean use2 = card[i + 2] >= 1;
            boolean use3 = card[i + 3] >= 1;
            if (use1) card[i + 1]--;
            if (use2) card[i + 2]--;
            if (use3) card[i + 3]--;
            if (searchHu0(card, dep + 1, laiZiCnt - needLaiZiCnt, depCnt, specialShunzi)) {
                return true;
            }
            if (use1) card[i + 1]++;
            if (use2) card[i + 2]++;
            if (use3) card[i + 3]++;
        }
        if (laiZiCnt >= 3) {
            if (dep >= needDepCnt) {
                return true;
            }
            if (searchHu0(card, dep + 1, laiZiCnt - 3, depCnt, specialShunzi)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean specialShunzi(byte[] card, int dep, int laiZiCnt, int depCnt, boolean specialShunzi, byte card1,byte card2,byte card3) {
        boolean  use1 = card[card1] >= 1;
        boolean use2 = card[card2] >= 1;
        boolean use3 = card[card3] >= 1;
        int  needLaiZiCnt = (card[card1] <= 0 ? 1 : 0) + (card[card2] <= 0 ? 1 : 0) + (card[card3] <= 0 ? 1 : 0);
        if (use1) card[card1]--;
        if (use2) card[card2]--;
        if (use3) card[card3]--;
        
        if (needLaiZiCnt <= laiZiCnt && searchHu0(card, dep + 1, laiZiCnt - needLaiZiCnt, depCnt, specialShunzi)) {
            return true;
        }
        if (use1) card[card1]++;
        if (use2) card[card2]++;
        if (use3) card[card3]++;
        return false;
    }

    /**
     * 是否是7对
     * @param card
     * @return
     */
    public static boolean isSevenPair(byte[] card) {
        return isSevenPair(card, (byte) -1);
    }

    /**
     * 是否豪华七对
     * @param card
     * @return
     */
    public static boolean isBigSevenPair(byte[] card) {
        return isBigSevenPair(card, (byte) -1);
    }

    /**
     * 是否双豪华7对
     * @param card
     * @return
     */
    public static boolean isBigBigSevenPair(byte[] card) {
        return isBigBigSevenPair(card, (byte) -1);
    }

    /**
     * 是否三豪华7对
     * @param card
     * @return
     */
    public static boolean isBigBigBigSevenPair(byte[] card) {
        return isXSevenPair(card, (byte) -1, 3);
    }

    /**
     * 是否七对
     * @param card
     * @return
     */
    public static boolean isSevenPair(byte[] card, byte laiZi) {
        return isXSevenPair(card, laiZi, Integer.MAX_VALUE, 0);
    }

    /**
     * 是否豪华七对
     * @param card
     * @return
     */
    public static boolean isBigSevenPair(byte[] card, byte laiZi) {
        return isXSevenPair(card, laiZi, Integer.MAX_VALUE, 1);
    }

    /**
     * 是否双豪华7对
     * @param card
     * @return
     */
    public static boolean isBigBigSevenPair(byte[] card, byte laiZi) {
        return isXSevenPair(card, laiZi, Integer.MAX_VALUE, 2);
    }

    /**
     * 是否三豪华7对
     * @param card
     * @return
     */
    public static boolean isBigBigBigSevenPair(byte[] card, byte laiZi) {
        return isXSevenPair(card, laiZi, Integer.MAX_VALUE, 3);
    }

    /**
     * 是否7对
     * @param card
     * @param laiZiCnt
     * @return
     */
    private static boolean isSevenPair(byte[] card, int laiZiCnt) {
        int cnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            byte value = card[i];
            if (value < 1) {
                continue;
            }
            if (0 != value % 2) {
                if (laiZiCnt < 1) {
                    return false;
                }
                --laiZiCnt;
                value += 1;
            }
            cnt += value / 2;
        }
        if (0 != laiZiCnt % 2) {
            return false;
        }
        cnt += laiZiCnt / 2;
        return 7 == cnt;
    }

    /**
     * 是否是X7对
     * @param card
     * @param laiZi
     * @param useLaiZiCnt
     * @return
     */
    public static boolean isXSevenPair(byte[] card, byte laiZi, int useLaiZiCnt, int AAAACnt) {
        int laiZiCnt = 0;
        int cnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            temp.get()[i] = card[i];
            cnt += card[i];
            if (-1 != laiZi && laiZi == i) {
                laiZiCnt = card[i];
            }
        }
        if (14 != cnt) {
            return false;
        }
        if (laiZiCnt > useLaiZiCnt) {
            laiZiCnt = useLaiZiCnt;
        }
        if (laiZiCnt > 0) {
            temp.get()[laiZi] -= laiZiCnt;
        }
        return isXSevenPair(temp.get(), laiZiCnt, AAAACnt);
    }

    /**
     * 是否是X7对
     * @param card
     * @param laiZiCnt
     * @return
     */
    private static boolean isXSevenPair(byte[] card, int laiZiCnt, int AAAACnt) {
        // AAAA AAAA AA AA AA
        int AAAA = 0;
        int AA = 0;
        int temp = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            byte value = card[i];
            if (value < 1) {
                continue;
            }
            if (value >= 3) {
                temp = 4 - value;
                ++AAAA;
            } else {
                temp = 2 - value;
                ++AA;
            }
            if (temp > laiZiCnt) {
                return false;
            }
            laiZiCnt -= temp;
        }
        if (0 != laiZiCnt % 2) {
            return false;
        }
        int extraAAAA = laiZiCnt / 4;
        AAAA += extraAAAA;
        AA += (laiZiCnt - 4 * extraAAAA) / 2;
        if (AAAA < AAAACnt) {
            return false;
        }
        return 3 == (AAAA - 2) * 2 + AA;
    }

    /**
     * 碰碰胡
     * @param card
     * @return
     */
    public static boolean isPengPengHu(byte[] card) {
        return isPengPengHu(card, (byte) -1, Integer.MAX_VALUE);
    }

    /**
     * 碰碰胡
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean isPengPengHu(byte[] card, byte laiZi) {
        return isPengPengHu(card, laiZi, Integer.MAX_VALUE);
    }

    /**
     * 碰碰胡
     * @param card
     * @param laiZi
     * @param laiZiCnt
     * @return
     */
    public static boolean isPengPengHu(byte[] card, byte laiZi, int laiZiCnt) {
        // 碰碰胡
        int laiCnt = -1 == laiZi ? 0 : card[laiZi];
        if (laiCnt > laiZiCnt) {
            laiCnt = laiZiCnt;
        }
        int twoCnt = 0;
        int signCnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            int cnt = card[i];
            if (i == laiZi) {
                if (cnt <= laiCnt) {
                    continue;
                }
                cnt -= laiCnt;
            }
            if (cnt < 1) {
                continue;
            }
            if (3 == cnt) {
                continue;
            }
            if (2 == cnt) {
                ++twoCnt;
            } else {
                ++signCnt;
            }
        }
        int needLaiZi = 2 * signCnt + twoCnt - 1;
        if (needLaiZi < 0) {
            needLaiZi += 3;
        }
        if (needLaiZi > laiCnt) {
            return false;
        }
        return 0 == ((laiCnt - needLaiZi) % 3);
    }

    /**
     * 碰碰胡没有将
     * @param card
     * @return
     */
    public static boolean isPengPengHuNoEye(byte[] card) {
        return isPengPengHuNoEye(card, (byte) -1);
    }

    /**
     * 碰碰胡没有将
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean isPengPengHuNoEye(byte[] card, byte laiZi) {
        return isPengPengHuNoEye(card, laiZi, Integer.MAX_VALUE);
    }

    /**
     * 碰碰胡没有将
     * @param card
     * @param laiZi
     * @param laiZiCnt
     * @return
     */
    public static boolean isPengPengHuNoEye(byte[] card, byte laiZi, int laiZiCnt) {
        // 碰碰胡
        int laiCnt = -1 == laiZi ? 0 : card[laiZi];
        if (laiCnt > laiZiCnt) {
            laiCnt = laiZiCnt;
        }
        int twoCnt = 0;
        int signCnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            int cnt = card[i];
            if (i == laiZi) {
                if (cnt <= laiCnt) {
                    continue;
                }
                cnt -= laiCnt;
            }
            if (cnt < 1) {
                continue;
            }
            if (3 == cnt) {
                continue;
            }
            if (2 == cnt) {
                ++twoCnt;
            } else {
                ++signCnt;
            }
        }
        int needLaiZi = 2 * signCnt + twoCnt;
        if (needLaiZi > laiCnt) {
            return false;
        }
        return 0 == ((laiCnt - needLaiZi) % 3);
    }

    /**
     * 清一色(万, 条, 筒)
     * @param card
     * @return
     */
    public static boolean isQingYiSe(byte[] card) {
        return isQingYiSe(card, (byte) -1, Collections.EMPTY_LIST);
    }

    /**
     * 清一色(万, 条, 筒)
     * @param card
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean isQingYiSe(byte[] card, byte laiZi) {
        return isQingYiSe(card, laiZi, Collections.EMPTY_LIST);
    }

    /**
     * 清一色(万, 条, 筒)
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean isQingYiSe(byte[] card, byte laiZi, List<Byte> piList) {
        // 清一色
        int color = -1;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (laiZi == i) {
                continue;
            }
            if (card[i] < 1) {
                continue;
            }
            byte c = (byte) i;
            if (-1 != piList.indexOf(c)) {
                continue;
            }
            if (-1 == color) {
                color = getColor(c);
            } else if (color != getColor(c)) {
                return false;
            }
            if (color >= COLOR_FENG) {
                return false;
            }
        }
        return true;
    }

    /**
     * 清一色(万, 条, 筒, 风, 花)
     * @param card
     * @return
     */
    public static boolean isQingYiSeAll(byte[] card) {
        return isQingYiSeAll(card, (byte) -1, Collections.EMPTY_LIST);
    }

    /**
     * 清一色(万, 条, 筒, 风, 花)
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean isQingYiSeAll(byte[] card, byte laiZi) {
        return isQingYiSeAll(card, laiZi, Collections.EMPTY_LIST);
    }

    /**
     * 清一色(万, 条, 筒, 风, 花)
     * @param card
     * @param laiZi
     * @param piList
     * @return
     */
    public static boolean isQingYiSeAll(byte[] card, byte laiZi, List<Byte> piList) {
        // 清一色
        int color = -1;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (laiZi == i) {
                continue;
            }
            if (card[i] < 1) {
                continue;
            }
            byte c = (byte) i;
            if (-1 != piList.indexOf(c)) {
                continue;
            }
            if (-1 == color) {
                color = getColor(c);
            } else if (color != getColor(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 风一色
     * @param card
     * @return
     */
    public static boolean isFengYiSe(byte[] card) {
        return isFengYiSe(card, (byte) -1, Collections.EMPTY_LIST);
    }

    /**
     * 风一色
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean isFengYiSe(byte[] card, byte laiZi) {
        return isFengYiSe(card, laiZi, Collections.EMPTY_LIST);
    }

    /**
     * 风一色
     * @param card
     * @param laiZi
     * @param piList
     * @return
     */
    public static boolean isFengYiSe(byte[] card, byte laiZi, List<Byte> piList) {
        // 风一色
        int color = -1;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (laiZi == i) {
                continue;
            }
            if (card[i] < 1) {
                continue;
            }
            byte c = (byte) i;
            if (-1 != piList.indexOf(c)) {
                continue;
            }
            if (-1 == color) {
                color = getColor(c);
            } else if (color != getColor(c)) {
                return false;
            }
            if (color != COLOR_FENG) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将一色(2,5,8组成)
     * @param card
     * @return
     */
    public static boolean isEyeYiSe(byte[] card) {
        return isEyeYiSe(card, (byte) -1, Collections.EMPTY_LIST);
    }

    /**
     * 将一色(2,5,8组成)
     * @param card
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean isEyeYiSe(byte[] card, byte laiZi) {
        return isEyeYiSe(card, laiZi, Collections.EMPTY_LIST);
    }

    /**
     * 将一色(2,5,8组成)
     * @param card
     * @param laiZi
     * @param piList
     * @return
     */
    public static boolean isEyeYiSe(byte[] card, byte laiZi, List<Byte> piList) {
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (laiZi == i) {
                continue;
            }
            if (card[i] < 1) {
                continue;
            }
            byte c = (byte) i;
            if (-1 != piList.indexOf(c)) {
                continue;
            }
            if (!is258(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 玄九(全部用1的连牌或9点连牌并且必须以11或99为奖牌的牌型)
     * @param card
     * @return
     */
    public static boolean isXuanJiu(byte[] card) {
        return isXuanJiu(card, 0);
    }

    /**
     * 玄九(全部用1的连牌或9点连牌并且必须以11或99为奖牌的牌型)
     * @param card
     * @param dep
     * @return
     */
    public static boolean isXuanJiu(byte[] card, int dep) {
        return isXuanJiu(card, dep, (byte) -1);
    }

    /**
     * 玄九(全部用1的连牌或9点连牌并且必须以11或99为奖牌的牌型)
     * @param card
     * @param dep
     * @param laiZi
     * @return
     */
    public static boolean isXuanJiu(byte[] card, int dep, byte laiZi) {
        return isXuanJiu(card, dep, laiZi, Integer.MAX_VALUE, true);
    }

    /**
     * 玄九(全部用1的连牌或9点连牌并且必须以11或99为奖牌的牌型)
     * @param card
     * @param dep
     * @param laiZi
     * @param eyeUseLaiZi
     * @return
     */
    public static boolean isXuanJiu(byte[] card, int dep, byte laiZi, boolean eyeUseLaiZi) {
        return isXuanJiu(card, dep, laiZi, Integer.MAX_VALUE, eyeUseLaiZi);
    }
    /**
     * 玄九(全部用1的连牌或9点连牌并且必须以11或99为奖牌的牌型)
     * @param card
     * @param dep
     * @param laiZi
     * @param useLaiZiCnt
     * @return
     */
    public static boolean isXuanJiu(byte[] card, int dep, byte laiZi, int useLaiZiCnt) {
        return isXuanJiu(card, dep, laiZi, useLaiZiCnt, true);
    }

    /**
     * 玄九(全部用1的连牌或9点连牌并且必须以11或99为奖牌的牌型)
     * @param card
     * @param dep
     * @param laiZi
     * @param useLaiZiCnt
     * @param eyeUseLaiZi
     * @return
     */
    public static boolean isXuanJiu(byte[] card, int dep, byte laiZi, int useLaiZiCnt, boolean eyeUseLaiZi) {
        int laiZiCnt = 0;
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            temp.get()[i] = card[i];
            if (-1 != laiZi && laiZi == i) {
                laiZiCnt = card[i];
            }
        }
        if (laiZiCnt > useLaiZiCnt) {
            laiZiCnt = useLaiZiCnt;
        }
        if (laiZiCnt > 0) {
            temp.get()[laiZi] -= laiZiCnt;
        }
        for (int i = 1; i < MJ_CARD_KINDS; ++i) {
            if (temp.get()[i] < 1) {
                continue;
            }
            if (temp.get()[i] >= 2) {
                if (dep >= 4) {
                    return true;
                }
                if (!is19((byte) i)) {
                    continue;
                }
                temp.get()[i] -= 2;
                if (isAll19Line3(temp.get(), dep, laiZiCnt)) {
                    return true;
                }
                temp.get()[i] += 2;
            } else if (eyeUseLaiZi && laiZiCnt > 0) {
                if (dep >= 4) {
                    return true;
                }
                if (!is19((byte) i)) {
                    continue;
                }
                temp.get()[i] -= 1;
                if (isAll19Line3(temp.get(), dep, laiZiCnt - 1)) {
                    return true;
                }
                temp.get()[i] += 1;
            }
        }
        if (eyeUseLaiZi && laiZiCnt >= 2) {
            if (dep >= 4) {
                return true;
            }
            if (isAll19Line3(temp.get(), dep, laiZiCnt - 2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAll19Line3(byte[] card, int dep, int laiZiCnt) {
        // 顺子
        for (int i = 0; i < 25; ++i) {
            int temp = i % 9;
            if (0 != temp && 6 != temp) {
                continue;
            }
            if (card[i + 1] <= 0 && card[i + 2] <= 0 && card[i + 3] <= 0) {
                continue;
            }
            int needLaiZiCnt = (card[i + 1] <= 0 ? 1 : 0) + (card[i + 2] <= 0 ? 1 : 0) + (card[i + 3] <= 0 ? 1 : 0);
            if (needLaiZiCnt > laiZiCnt) {
                continue;
            }
            if (dep >= 3) {
                return true;
            }
            boolean use1 = card[i + 1] >= 1;
            boolean use2 = card[i + 2] >= 1;
            boolean use3 = card[i + 3] >= 1;
            if (use1) card[i + 1]--;
            if (use2) card[i + 2]--;
            if (use3) card[i + 3]--;
            if (isAll19Line3(card, dep + 1, laiZiCnt - needLaiZiCnt)) {
                return true;
            }
            if (use1) card[i + 1]++;
            if (use2) card[i + 2]++;
            if (use3) card[i + 3]++;
        }
        return false;
    }

    /**
     * 是否是258将牌
     * @param card
     * @return
     */
    public static boolean is258(List<Byte> card) {
        return is258(card, (byte) -1);
    }

    /**
     * 是否是258将牌
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean is258(List<Byte> card, byte laiZi) {
        for (Byte c : card) {
            if (!is258(c, laiZi)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是258将牌
     * @param card
     * @return
     */
    public static boolean is258(byte[] card) {
        return is258(card, (byte) -1);
    }

    /**
     * 是否是258将牌
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean is258(byte[] card, byte laiZi) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (card[i] < 1) {
                continue;
            }
            if (!is258((byte) i, laiZi)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是258将牌
     * @param card
     * @return
     */
    public static boolean is258(byte card) {
        return is258(card, (byte) -1);
    }

    /**
     * 是否是5将牌
     * @param card
     * @return
     */
    public static boolean is5(byte card) {
        return is5(card, (byte) -1);
    }

    /**
     * 是否是258将牌
     * @param card
     * @return
     */
    public static boolean is258(byte card, byte laiZi) {
        if (card == laiZi) {
            return true;
        }
        return MJ_2_WANG == card || MJ_2_TIAO == card || MJ_2_TONG == card
                    || MJ_5_WANG == card || MJ_5_TIAO == card || MJ_5_TONG == card
                    || MJ_8_WANG == card || MJ_8_TIAO == card || MJ_8_TONG == card;
    }

    /**
     * 是否是5将牌
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean is5(byte card, byte laiZi) {
        if (card == laiZi) {
            return true;
        }
        return MJ_5_WANG == card || MJ_5_TIAO == card || MJ_5_TONG == card;
    }

    /**
     *
     * 是否是147将牌
     * @param card
     * @param laiZi
     * @return
     */
    public static boolean is147(byte card) {
        return is147(card, (byte) -1);
    }

    /**
     * 是否是147将牌
     * @param card
     * @return
     */
    public static boolean is147(byte card, byte laiZi) {
        if (card == laiZi) {
            return true;
        }
        return MJ_1_WANG == card || MJ_1_TIAO == card || MJ_1_TONG == card
                    || MJ_4_WANG == card || MJ_4_TIAO == card || MJ_4_TONG == card
                    || MJ_7_WANG == card || MJ_7_TIAO == card || MJ_7_TONG == card;
    }

    /**
     *
     * 是否是369将牌
     * @param card
     * @return
     */
    public static boolean is369(byte card) {
        return is369(card, (byte) -1);
    }

    /**
     * 是否是369将牌
     * @param card
     * @return
     */
    public static boolean is369(byte card, byte laiZi) {
        if (card == laiZi) {
            return true;
        }
        return MJ_3_WANG == card || MJ_3_TIAO == card || MJ_3_TONG == card
                    || MJ_6_WANG == card || MJ_6_TIAO == card || MJ_6_TONG == card
                    || MJ_9_WANG == card || MJ_9_TIAO == card || MJ_9_TONG == card;
    }

    /**
     *
     * 是否是159将牌
     * @param card
     * @return
     */
    public static boolean is159(byte card) {
        return is159(card, (byte) -1);
    }

    /**
     * 是否是159将牌
     * @param card
     * @return
     */
    public static boolean is159(byte card, byte laiZi) {
        if (card == laiZi) {
            return true;
        }
        return MJ_1_WANG == card || MJ_1_TIAO == card || MJ_1_TONG == card
                    || MJ_5_WANG == card || MJ_5_TIAO == card || MJ_5_TONG == card
                    || MJ_9_WANG == card || MJ_9_TIAO == card || MJ_9_TONG == card;
    }

    /**
     *
     * 是否是19将牌
     * @param card
     * @return
     */
    public static boolean is19(byte card) {
        return is19(card, (byte) -1);
    }

    /**
     * 是否是19将牌
     * @param card
     * @return
     */
    public static boolean is19(byte card, byte laiZi) {
        if (card == laiZi) {
            return true;
        }
        return MJ_1_WANG == card || MJ_1_TIAO == card || MJ_1_TONG == card
                    || MJ_9_WANG == card || MJ_9_TIAO == card || MJ_9_TONG == card;
    }
    

    
    /**
     * 获取牌值
     * @param huCard
     * @return
     */
    public byte getCardValue(byte card) {
       
        if((card % 9)==0) {
            return 9;
        }
        return (byte)(card % 9);
    }

    /**
     * 判断牌是否为 1,2
     * <pre>
     * 是否是1或2
     * </pre>
     * @param card
     * @return
     */
    public static boolean is12(byte card){
        byte cardValue=(byte)(card % 9);
          if(card<MJ_D_FENG && (cardValue==1 || cardValue==2)) {
              return true;
          }
        return false;
    }
    /**
     * 判断牌是否为3,4,5
     * @param card
     * @return
     */
    public static boolean is345(byte card){
        byte cardValue=(byte)(card % 9);
        if(card<MJ_D_FENG && (cardValue==3 || cardValue==4 || cardValue==5)) {
            return true;
        }
      return false;
    }
    
    
    
    
    
    
    
    
    
    
}
