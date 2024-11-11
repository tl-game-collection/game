package com.xiuxiu.app.server.room.normal.mahjong2.utils;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.room.CardLibraryManager;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.List;

public class MahjongHelper {
    /**
     * 使用既定的规则填充牌堆
     *
     * @param cards    欲填充的牌堆
     * @param features @see MahjongFeatures
     */
    public static void fixWithFeaturedCards(List<Byte> cards, long features) {
        for (int i = 0; i < 4; ++i) {
            if ((features & MahjongFeatures.WITH_WAN_ZI) != 0) {
                for (int j = MahjongUtil.MJ_1_WANG; j <= MahjongUtil.MJ_9_WANG; ++j) {
                    cards.add((byte) j);
                }
            }
            if ((features & MahjongFeatures.WITH_TIAO_ZI) != 0) {
                for (int j = MahjongUtil.MJ_1_TIAO; j <= MahjongUtil.MJ_9_TIAO; ++j) {
                    cards.add((byte) j);
                }
            }
            if ((features & MahjongFeatures.WITH_TONG_ZI) != 0) {
                for (int j = MahjongUtil.MJ_1_TONG; j <= MahjongUtil.MJ_9_TONG; ++j) {
                    cards.add((byte) j);
                }
            }
            if ((features & MahjongFeatures.WITH_FENG) != 0) {
                for (int j = MahjongUtil.MJ_D_FENG; j <= MahjongUtil.MJ_B_FENG; ++j) {
                    cards.add((byte) j);
                }
            }
            if ((features & MahjongFeatures.WITH_JIAN) != 0) {
                for (int j = MahjongUtil.MJ_Z_FENG; j <= MahjongUtil.MJ_BAI_FENG; ++j) {
                    cards.add((byte) j);
                }
            }
        }
        if ((features & MahjongFeatures.WITH_HUA) != 0) {
            for (int j = MahjongUtil.MJ_ZHU_HUA; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
                cards.add((byte) j);
            }
        }
    }

    /**
     * 使用牌库填充牌堆
     */
    public static void fixWithLibrariedCards(List<Byte> cards) {
        cards.addAll(CardLibraryManager.I.getMahjongCard());
    }

    /**
     * 洗牌
     */
    public static void shuffle(List<Byte> cards) {
        ShuffleUtil.shuffle(cards);
    }

    /**
     * 检测是否序数牌（万子、饼子、条子）
     */
    public static boolean isXuShuPai(byte card) {
        return card >= MahjongUtil.MJ_1_WANG && card <= MahjongUtil.MJ_9_TONG;
    }

    /**
     * 检测是否字牌（包含风牌、箭牌）
     */
    public static boolean isZiPai(byte card) {
        return card >= MahjongUtil.MJ_D_FENG && card <= MahjongUtil.MJ_BAI_FENG;
    }

    /**
     * 检测是否风牌，东、南、西、北
     */
    public static boolean isFengPai(byte card) {
        return card >= MahjongUtil.MJ_D_FENG && card <= MahjongUtil.MJ_B_FENG;
    }

    /**
     * 检测是否箭牌，中、发、白
     */
    public static boolean isJianPai(byte card) {
        return card >= MahjongUtil.MJ_Z_FENG && card <= MahjongUtil.MJ_BAI_FENG;
    }

    /**
     * 获取赖子牌，字牌统一处理
     *
     * @param card 用于确定赖子的牌
     */
    public static byte getLaiZiCardByZi(byte card) {
        if (isXuShuPai(card)) {
            byte begin = (byte) (card - MahjongUtil.MJ_1_WANG);
            return (byte) ((begin / 9) * 9 + ((begin + 1) % 9) + MahjongUtil.MJ_1_WANG);
        }

        if (isZiPai(card)) {
            byte begin = (byte) (card - MahjongUtil.MJ_D_FENG);
            return (byte) ((begin / 7) * 7 + ((begin + 1) % 7) + MahjongUtil.MJ_D_FENG);
        }

        return -1;
    }

    /**
     * 获取赖子牌，字牌分风和箭单独处理
     *
     * @param card 用于确定赖子的牌
     */
    public static byte getLaiZiCardByFengJian(byte card) {
        if (isXuShuPai(card)) {
            byte begin = (byte) (card - MahjongUtil.MJ_1_WANG);
            return (byte) ((begin / 9) * 9 + ((begin + 1) % 9) + MahjongUtil.MJ_1_WANG);
        }

        if (isFengPai(card)) {
            byte begin = (byte) (card - MahjongUtil.MJ_D_FENG);
            return (byte) ((begin / 4) * 4 + ((begin + 1) % 4) + MahjongUtil.MJ_D_FENG);
        }
        if (isJianPai(card)) {
            byte begin = (byte) (card - MahjongUtil.MJ_Z_FENG);
            return (byte) ((begin / 3) * 3 + ((begin + 1) % 3) + MahjongUtil.MJ_Z_FENG);
        }

        return -1;
    }

    /**
     * 检测特性里面是否包含给定的牌
     *
     * @param features @see MahjongFeatures
     */
    public static boolean isCardEnabledByFeatures(byte card, long features) {
        int color = MahjongUtil.getColor(card);
        if (color == MahjongUtil.COLOR_FENG) {
            if (isFengPai(card)) {
                return (features & MahjongFeatures.WITH_FENG) != 0;
            }
            return (features & MahjongFeatures.WITH_JIAN) != 0;
        }
        if (color == MahjongUtil.COLOR_WANG) {
            return (features & MahjongFeatures.WITH_WAN_ZI) != 0;
        }
        if (color == MahjongUtil.COLOR_TIAO) {
            return (features & MahjongFeatures.WITH_TIAO_ZI) != 0;
        }
        if (color == MahjongUtil.COLOR_TONG) {
            return (features & MahjongFeatures.WITH_TONG_ZI) != 0;
        }
        if (color == MahjongUtil.COLOR_HUA) {
            return (features & MahjongFeatures.WITH_HUA) != 0;
        }

        return false;
    }
}
