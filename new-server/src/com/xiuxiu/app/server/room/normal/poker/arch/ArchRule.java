package com.xiuxiu.app.server.room.normal.poker.arch;

import com.xiuxiu.algorithm.poker.PokerUtil;
import org.apache.http.util.Asserts;

import java.util.*;

/**
 * 打拱规则相关
 */
public class ArchRule {
    public static final int NUM_PACKS_OF_CARDS = 2; // 几副牌
    public static final int NUM_CARDS_PER_PACK = 54; // 每副牌的张数
    public static final int NUM_RESERVE_CARDS = 6; // 3人模式保留底牌张数

    public static final int CONTRACT_UNDEFINED = 0; // 待定
    public static final int CONTRACT_1V2 = 31; // 3人，独庄
    public static final int CONTRACT_1V2_X2 = 32; // 3人，抄庄
    public static final int CONTRACT_1V3 = 41; // 4人，独庄
    public static final int CONTRACT_2V2 = 42; // 4人，盟友

    public static final byte HUA_PAI = (byte) (PokerUtil.KING + 1); // 花牌

    public static final int CARD_SORT_BY_DEFAULT = 1; // 默认序
    public static final int CARD_SORT_BY_5_10_K = 2; // 五十K优先序

    private static final List<Byte> LAIZI = Arrays.asList(PokerUtil.KINGLET, PokerUtil.KING, HUA_PAI); // 癞子牌
    private static final List<Byte> WSK = Arrays.asList(PokerUtil.FIVE, PokerUtil.TEN, PokerUtil.K); // 五十K牌

    public static final int FEATURE_HUA_PAI = 0x01;
    public static final int FEATURE_COMBO_BOMB = 0x02; // 连炸
    public static final int FEATURE_JOKER_BOMB = 0x04; // 天炸

    public static byte getCardValue(byte card) {
        return card == HUA_PAI ? (byte) (PokerUtil.getCardValue(PokerUtil.KING) + 1) : PokerUtil.getCardValue(card);
    }

    /**
     * 获得癞子牌
     * @return 癞子牌列表
     */
    public static List<Byte> getLAIZI() {
        return LAIZI;
    }

    /**
     * 有癞子的情况下，得到最终的牌值。
     * @param originCards 打出了哪些牌
     * @param laiziAsCards 癞子作为哪些牌
     * @return
     */
    public static List<Byte> virtualCards(List<Byte> originCards, List<Byte> laiziAsCards) {
        List<Byte> result = new ArrayList<>(originCards.size());
        if (laiziAsCards.isEmpty()) {
            result.addAll(originCards);
        } else {
            for (Byte card : originCards) {
                if (!isLaizi(card)) {
                    result.add(card);
                }
            }
            result.addAll(laiziAsCards);
        }
        PokerUtil.sort(result);
        return result;
    }

    public static boolean canTake(List<Byte> originCards, List<Byte> laiziAsCards, int features) {
        if (originCards.contains(HUA_PAI)) {
            // 花牌不能当王出
            return !laiziAsCards.contains(PokerUtil.KING) && !laiziAsCards.contains(PokerUtil.KINGLET);
        }
        return true;
    }

    /**
     * 检测扑克牌的牌型
     * @param cards 欲检测的扑克牌，不含赖子
     * @return
     */
    public static ArchCardTypeEnum detectCardType(List<Byte> cards, int features) {
        if (cards.size() == 1) {
            return ArchCardTypeEnum.SINGLE;
        }
        if (cards.size() == 2) {
            return PokerUtil.isDouble(cards) != -1 ? ArchCardTypeEnum.DOUBLE : ArchCardTypeEnum.NONE;
        }
        if (cards.size() == 3) {
            if (PokerUtil.isThree(cards) != -1) {
                return ArchCardTypeEnum.THREE;
            }
            if (ArchRule.isWuShiK(cards)) {
                return ArchRule.cardsInSameColor(cards) ? ArchCardTypeEnum.WUSHIK_WSKBIGBOMB : ArchCardTypeEnum.WUSHIK_WSKBOMB;
            }
            return ArchCardTypeEnum.NONE;
        }
        if (cards.size() == 4) {
            if ((features & FEATURE_JOKER_BOMB) != 0 && ArchRule.isJokerBomb(cards)) {
                return ArchCardTypeEnum.JOKER_BOMB;
            }
            if (ArchRule.isDoubleLine(cards)) {
                return ArchCardTypeEnum.DOUBLE_LINE;
            }
            return PokerUtil.isBomb(cards) != -1 ? ArchCardTypeEnum.BOMB : ArchCardTypeEnum.NONE;
        }

        if ((features & FEATURE_COMBO_BOMB) != 0 && ArchRule.isComboBomb(cards)) {
            return ArchCardTypeEnum.COMBO_BOMB;
        }
        if (ArchRule.isBomb(cards)) {
            return ArchCardTypeEnum.BOMB;
        }
        if (ArchRule.isDoubleLine(cards)) {
            return ArchCardTypeEnum.DOUBLE_LINE;
        }
        if (ArchRule.isThreeLine(cards)) {
            return ArchCardTypeEnum.THREE_LINE;
        }
        return ArchCardTypeEnum.NONE;
    }

    /**
     * 判断是否是五十K
     * @param cards
     * @return
     */
    public static boolean isWuShiK(List<Byte> cards) {
        return PokerUtil.isWuShiK(cards) != -1;
    }

    /**
     * 判断扑克牌是否是同色
     * @param cards
     * @return
     */
    public static boolean cardsInSameColor(List<Byte> cards) {
        Byte color = null;
        for (Byte card : cards) {
            if (color == null) {
                color = PokerUtil.getCardColor(card);
            } else if (PokerUtil.getCardColor(card) != color) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取扑克牌对应的分值
     * @param card
     * @return
     */
    public static int getCardScore(byte card) {
        if (card == HUA_PAI){ // 花牌不算分
            return 0;
        }
        byte value = PokerUtil.getCardValue(card);
        if (value == PokerUtil.FIVE) {
            return 5;
        }
        if (value == PokerUtil.TEN || value == PokerUtil.K) {
            return 10;
        }
        return 0;
    }

    /**
     * 获取扑克牌对应的分值
     */
    public static int getCardsScore(List<Byte> cards) {
        int score = 0;
        for (Byte card : cards) {
            score += getCardScore(card);
        }
        return score;
    }

    /**
     * 检测是否天炸
     */
    public static boolean isJokerBomb(List<Byte> cards) {
        if (cards.size() != 4) {
            return false;
        }
        boolean allAreJoker = true;
        for (byte card : cards) {
            if (card != PokerUtil.KING && card != PokerUtil.KINGLET) {
                allAreJoker = false;
                break;
            }
        }
        return allAreJoker;
    }

    /**
     * 检测是否是连炸
     */
    public static boolean isComboBomb(List<Byte> cards) {
        if (cards.size() < 8) {
            return false;
        }
        Map<Byte, Integer> cardCount = new HashMap<>();
        for (Byte card : cards) {
            if (!isLaizi(card)) {
                byte cardValue = getCardValue(card);
                if (cardCount.containsKey(cardValue)) {
                    cardCount.put(cardValue, cardCount.get(cardValue) + 1);
                } else {
                    cardCount.put(cardValue, 1);
                }
            }
        }
        if (cardCount.size() != 2) {
            return false;
        }
        for (Integer count : cardCount.values()) {
            if (count < 4) {
                return false;
            }
        }
        Iterator<Byte> it = cardCount.keySet().iterator();
        byte one = it.next();
        byte other = it.next();
        if (one == PokerUtil._2 || other == PokerUtil._2) {
            return false;
        }
        return Math.abs(one - other) == 1;
    }

    /**
     * 检测是否是常规炸弹
     */
    public static boolean isBomb(List<Byte> cards) {
        if (cards.size() < 4) {
            return false;
        }
        Byte bombValue = null;
        for (Byte card : cards) {
            if (bombValue == null) {
                bombValue = PokerUtil.getCardValue(card);
            } else if (PokerUtil.getCardValue(card) != bombValue) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBomb(ArchCardTypeEnum type, int features) {
        if ((features & FEATURE_JOKER_BOMB) != 0 && type == ArchCardTypeEnum.JOKER_BOMB) {
            return true;
        }
        if ((features & FEATURE_COMBO_BOMB) != 0 && type == ArchCardTypeEnum.COMBO_BOMB) {
            return true;
        }
        return type == ArchCardTypeEnum.BOMB
                || type == ArchCardTypeEnum.WUSHIK_WSKBOMB
                || type == ArchCardTypeEnum.WUSHIK_WSKBIGBOMB;
    }

    /**
     * 检测是否是连对
     */
    public static boolean isDoubleLine(List<Byte> cards) {
        return isXXLine(cards, 2);
    }

    /**
     * 检测是否是连三
     */
    public static boolean isThreeLine(List<Byte> cards) {
        return isXXLine(cards, 3);
    }

    /**
     * 检测是否是癞子牌
     */
    public static boolean isLaizi(Byte card) {
        return LAIZI.contains(card);
    }

    /**
     * 按照指定规则对扑克牌进行排序
     * @param cards 欲牌序的扑克牌
     * @param sortBy 排序规则
     * @return 重新排序后的扑克牌
     */
    public static List<Byte> sortCards(List<Byte> cards, int sortBy) {
        List<Byte> result = new ArrayList<>(cards.size());
        List<Byte> tmpCards = new ArrayList<>(cards);
        if (sortBy == CARD_SORT_BY_5_10_K) {
            PokerUtil.sort(tmpCards);

            // 5 10 K
            Iterator<Byte> it = tmpCards.iterator();
            while (it.hasNext()) {
                byte card = it.next();
                byte value = PokerUtil.getCardValue(card);
                if (value == 2 || value == 7 || value == 10) {
                    result.add(card);
                    it.remove();
                }
            }
            PokerUtil.sort(result);

            // 大小王
            it = tmpCards.iterator();
            while (it.hasNext()) {
                byte card = it.next();
                byte value = PokerUtil.getCardValue(card);
                if (value == 13 || value == 14) {
                    result.add(card);
                    it.remove();
                }
            }

            // 花牌
            it = tmpCards.iterator();
            while (it.hasNext()) {
                byte card = it.next();
                if (card == ArchRule.HUA_PAI) {
                    result.add(card);
                    it.remove();
                }
            }

            // 剩余部分按照牌的张数进行排序
            TreeMap<Byte, List<Byte>> cardMap = new TreeMap<>();
            for (Byte card : tmpCards) {
                Byte value = PokerUtil.getCardValue(card);
                if (!cardMap.containsKey(value)) {
                    cardMap.put(value, new ArrayList<>());
                }
                cardMap.get(value).add(card);
            }
            for (int i = 4 * NUM_CARDS_PER_PACK; i > 0 && !cardMap.isEmpty(); i--) {
                for (Byte key : cardMap.descendingKeySet()) {
                    List<Byte> keyedCards = cardMap.get(key);
                    if (keyedCards.size() == i) {
                        result.addAll(keyedCards);
                    }
                }
            }
        } else {
            PokerUtil.sort(result);
        }
        return result;
    }

    /**
     * 比较炸弹的大小
     * @return 1:bomb1>bomb2, 0:bomb1=bomb2, -1:bomb1<bomb2
     */
    public static int compareBomb(List<Byte> bomb1, List<Byte> laizi1, ArchCardTypeEnum type1,
                                  List<Byte> bomb2, List<Byte> laizi2, ArchCardTypeEnum type2) {
        byte maxCard1 = ArchRule.getMaxCardByValue(bomb1);
        byte maxCard2 = ArchRule.getMaxCardByValue(bomb2);
        int l = ArchRule.getBombValue(bomb1, laizi1, type1, maxCard1);
        int r = ArchRule.getBombValue(bomb2, laizi2, type2, maxCard2);
        return Integer.compare(l, r);
    }

    /**
     * 比较牌值的大小
     * @return 1:left>right, 0:left=right, -1:left<right
     */
    public static int compareCardValue(byte left, byte right) {
        byte l = ArchRule.getCardValue(left);
        byte r = ArchRule.getCardValue(right);
        return Byte.compare(l, r);
    }

    private static byte getMaxCardByValue(List<Byte> cards) {
        byte maxCard = -1;
        byte maxValue = -1;
        for (byte card : cards) {
            byte value = ArchRule.getCardValue(card);
            if (value > maxValue) {
                maxValue = value;
                maxCard = card;
            }
        }
        return maxCard;
    }

    private static int getBombValue(List<Byte> cards, List<Byte> laizi, ArchCardTypeEnum bombType, byte maxCard) {
        if (bombType == ArchCardTypeEnum.JOKER_BOMB) {
            return 1 << 30; // 1
        }
        if (bombType == ArchCardTypeEnum.BOMB) {
            if (laizi == null || laizi.isEmpty()) {
                if (cards.size() == 8) {
                    return (1 << 29) + ArchRule.getCardValue(maxCard); // 2
                }
                if (cards.size() == 7) {
                    return (1 << 27) + ArchRule.getCardValue(maxCard); // 4
                }
                return (1 << 24) + (cards.size() << 16) + ArchRule.getCardValue(maxCard); // 7
            } else {
                if (cards.size() > 6) {
                    return (1 << 25) + ArchRule.getCardValue(maxCard); // 6
                }
                return (1 << 24) + (cards.size() << 16) + ArchRule.getCardValue(maxCard); // 7
            }
        }
        if (bombType == ArchCardTypeEnum.COMBO_BOMB) {
            int points = 0;
            // 8摇摆
            if (cards.size() == 8) {
                for (byte card : cards) {
                    points += ArchRule.getCardValue(card);
                }
                return (1 << 26) + points; // 5
            }
            // 9摇摆或以上
            if (cards.size() == 9) {
                for (byte card : cards) {
                    points += ArchRule.getCardValue(card);
                }
                return (1 << 28) + (cards.size() << 16) + points; // 3
            }
            Map<Byte, Integer> cardCount = new HashMap<>();
            for (byte card : cards) {
                byte value = ArchRule.getCardValue(card);
                int count = cardCount.getOrDefault(value, 0);
                cardCount.put(value, count + 1);
            }
            for (Map.Entry<Byte, Integer> entry : cardCount.entrySet()) {
                // 10摇摆及以上，比较摇摆中最大的炸弹的大小，所以数量放在首位，其次是牌值
                points = Math.max(points, (entry.getValue() << 8) + entry.getKey());
            }
            return (1 << 28) + (10 << 16) + points; // 3
        }
        if (bombType == ArchCardTypeEnum.WUSHIK_WSKBIGBOMB) {
            return (1 << 20) + PokerUtil.getCardColor(maxCard); // 8
        }
        if (bombType == ArchCardTypeEnum.WUSHIK_WSKBOMB) {
            return 1 << 19; // 9
        }

        Asserts.check(false, "未知的炸弹类型");
        return 0;
    }

    /**
     * 检测是否连对或连三
     * @param cards 牌堆，已排序
     * @param xx 2-连对，3-连三
     */
    private static boolean isXXLine(List<Byte> cards, int xx) {
        if (cards.size() < xx * 2 || cards.size() % xx != 0) {
            return false;
        }
        byte value = -1;
        int countOfValue = 0;
        for (byte card : cards) {
            byte cardValue = getCardValue(card);
            if (cardValue == getCardValue(PokerUtil.TWO_SPADES)) {
                // 2不能作为连对或连三出
                return false;
            }
            if (value == -1) {
                value = cardValue;
                countOfValue = 1;
            } else if (cardValue == value) {
                countOfValue++;
            } else {
                if (cardValue - value != 1 || countOfValue != xx) {
                    return false;
                }
                value = cardValue;
                countOfValue = 1;
            }
        }
        return countOfValue == xx;

    }
}
