package com.xiuxiu.algorithm.poker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PokerUtil {
	private static final String[] POKER_CARD = new String[]{"   3", "   4", "   5", "   6", "   7", "   8", "   9", "  10", "   J", "   Q",
			"   K", "   A", "   2", "小王", "大王"};
	private static final String[] POKER_COLOR = new String[]{"方块", "梅花", "红桃", "黑桃"};

	public static final int THREE_TAKE_TYPE_0 = 0x0001; // 三带
	public static final int THREE_TAKE_TYPE_1 = 0x0002; // 三带一
	public static final int THREE_TAKE_TYPE_2 = 0x0004; // 三代二
	public static final int THREE_TAKE_TYPE_11 = 0x0008; // 三带一对

	public static final int FOUR_TAKE_TYPE_2 = 0x0001; // 四代二
	public static final int FOUR_TAKE_TYPE_3 = 0x0002; // 四代三
	public static final int FOUR_TAKE_TYPE_11_22 = 0x0004; // 四代两队

	// Box Plum Red Peach Spades
	public static final Byte THREE_SPADES = 39; // 黑桃3
	public static final Byte THREE_BOX = 0; // 方块3
	public static final Byte THREE_PEACH = 26; // 红桃3
	public static final Byte THREE_PLUM = 13; // 梅花3
	public static final Byte A_BOX = 11; // 方块A
	public static final Byte A_PLUM = 24; // 梅花A
	public static final Byte A_RED_PEACH = 37; // 红桃A
	public static final Byte A_SPADES = 50; // 黑桃A
	public static final Byte TWO_BOX = 12; // 方块2
	public static final Byte TWO_PLUM = 25; // 梅花2
	public static final Byte TWO_RED_PEACH = 38; // 红桃2
	public static final Byte TWO_SPADES = 51; // 黑桃2
	public static final Byte K_BOX = 10; // 方块k
	public static final Byte K_PLUM = 23; // 梅花k
	public static final Byte K_RED_PEACH = 36; // 红桃k
	public static final Byte K_SPADES = 49; // 黑桃k
	public static final Byte TEN_RED_PEACH = 33; // 红桃10
	public static final Byte KINGLET = 52; // 小王
	public static final Byte KING = 53; // 大王
	public static final Byte J = 8; // J
	public static final Byte K = 10; // K
	public static final Byte FIVE = 2; // 5
	public static final Byte EIGHT = 5; // 8
	public static final Byte TEN = 7; // 10
	public static final Byte A = 11; // A
	public static final Byte TWO = 12; // 2
	
	public static final Byte ANPAI = 54;

	public static final Byte _3 = 0; // 3
	public static final Byte _4 = 1; // 4
	public static final Byte _5 = 2; // 5
	public static final Byte _6 = 3; // 6
	public static final Byte _7 = 4; // 7
	public static final Byte _8 = 5; // 8
	public static final Byte _9 = 6; // 9
	public static final Byte _10 = 7; // 10
	public static final Byte _J = 8; // J
	public static final Byte _Q = 9; // Q
	public static final Byte _K = 10; // K
	public static final Byte _A = 11; // A
	public static final Byte _2 = 12; // 2

	private static final byte[] NONE = new byte[]{EPokerCardType.NONE.getValue(), -1};
	private static final byte[] NONECARD = new byte[]{EPokerCardType.NONE.getValue(), -1, -1};

	private static final ThreadLocal<byte[]> temp = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[15];
		}
	};

	private static final ThreadLocal<byte[]> single = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[15];
		}
	};

	private static final ThreadLocal<byte[]> two = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[15];
		}
	};

	private static final ThreadLocal<byte[]> three = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[15];
		}
	};

	private static final ThreadLocal<byte[]> four = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[15];
		}
	};

	public static byte getCardValue(byte card) {
		if (KINGLET == card) {
			return 13;
		}
		if (KING == card) {
			return 14;
		}
		return (byte) (card % 13);
	}

	public static byte getCardValueByTexas(byte card) {
		//yyf_2-A
		if (KINGLET == card) {
			return 13;
		}
		if (KING == card) {
			return 14;
		}
		return (byte)((card+1)%13+2);
	}
	public static byte getCardValueByCow(byte card) {
		if (KINGLET == card) {
			return -1;
		}
		if (KING == card) {
			return -1;
		}
		byte temp = (byte) ((byte) (card % 13) + 3);
		if (temp >= 10 && temp <= 13) {
			return 10;
		}
		if (14 == temp) {
			return 1;
		}
		if (15 == temp) {
			return 2;
		}
		return temp;
	}

	public static byte getCardValueByCow2(byte card) {
		if (KINGLET == card) {
			return -1;
		}
		if (KING == card) {
			return -1;
		}
		byte temp = (byte) ((byte) (card % 13) + 3);
		if (14 == temp) {
			return 1;
		}
		if (15 == temp) {
			return 2;
		}
		return temp;
	}

	public static int generateCardValueByCow2WithColor(byte value, byte color) {
		if (-1 == value) {
			return 0;
		}
		return (value << 3) | color;
	}

	public static byte getCardValueByFriedGoldenFlower(byte card) {
		if (KINGLET == card) {
			return -1;
		}
		if (KING == card) {
			return -1;
		}
		byte temp = (byte) ((byte) (card % 13) + 3);
		if (15 == temp) {
			return 2;
		}
		return temp;
	}

	public static byte getCardColor(byte card) {
		if (KINGLET == card) {
			return 0;
		}
		if (KING == card) {
			return 1;
		}
		return (byte) (card / 13); // 0: 方块, 1: 梅花 2: 红桃 3: 黑桃
	}

	public static void sort(List<Byte> cardList) {
		cardList.sort(new Comparator<Byte>() {
			@Override
			public int compare(Byte o1, Byte o2) {
				byte c1 = getCardValue(o1);
                if (o1 == ANPAI) {
                    c1 = 15;
                }
				byte c2 = getCardValue(o2);
				if (o2 == ANPAI) {
				     c2 = 15;
	            }
				if (c1 == c2) {
					return getCardColor(o2) - getCardColor(o1);
				}
				return c1 - c2;
			}
		});
	}
	public static void sort1(List<byte[]> cardList) {
		cardList.sort(new Comparator<byte[]>() {
			@Override
			public int compare(byte[] o1, byte[] o2) {
				byte c1 = getCardValue(o1[0]);
				byte c2 = getCardValue(o2[0]);
				if (c1 == c2) {
					return getCardColor(o2[0]) - getCardColor(o1[0]);
				}
				return c1 - c2;
			}
		});
	}

	public static void sortByCow(List<Byte> cardList) {
		cardList.sort(new Comparator<Byte>() {
			@Override
			public int compare(Byte o1, Byte o2) {
				byte c1 = getCardValueByCow2(o1);
				byte c2 = getCardValueByCow2(o2);
				if (c1 == c2) {
					return getCardColor(o1) - getCardColor(o2);
				}
				return c1 - c2;
			}
		});
	}

	public static void sortByFGF(List<Byte> cardList) {
		cardList.sort(new Comparator<Byte>() {
			@Override
			public int compare(Byte o1, Byte o2) {
				byte c1 = getCardValueByFriedGoldenFlower(o1);
				byte c2 = getCardValueByFriedGoldenFlower(o2);
				if (c1 == c2) {
					return getCardColor(o1) - getCardColor(o2);
				}
				return c1 - c2;
			}
		});
	}

	private static void clear() {
		for (int i = 0; i < 15; ++i) {
			temp.get()[i] = 0;
			single.get()[i] = 0;
			two.get()[i] = 0;
			three.get()[i] = 0;
			four.get()[i] = 0;
		}
	}

	public static String getCardStr(byte card) {
		return POKER_COLOR[getCardColor(card)] + "  " + POKER_CARD[getCardValue(card)];
	}

	public static byte[] findLargerThan(List<Byte> card, byte[] cardCnt, byte minCard, EPokerCardType cardType, byte cnt,
                                        boolean bombTarget, boolean threeABomb) {
		if (EPokerCardType.KING_FRIED == cardType) {
			return NONE;
		}
		byte[] rs = NONE;
		switch (cardType) {
			case SINGLE :
				rs = findSingleThan(card, cardCnt, minCard, bombTarget);
				break;
			case DOUBLE :
				rs = findDoubleThan(card, cardCnt, minCard, bombTarget);
				break;
			case THREE :
				rs = findThreeThan(card, cardCnt, minCard, bombTarget);
				break;
			case SINGLE_LINE :
				rs = findSingleLineThan(card, cardCnt, minCard, cnt, bombTarget);
				break;
			case DOUBLE_LINE :
				rs = findDoubleLineThan(card, cardCnt, minCard, cnt, bombTarget);
				break;
			case THREE_LINE :
				rs = findThreeLineThan(card, cardCnt, minCard, cnt, bombTarget);
				break;
			case THREE_TAKE_ONE :
				rs = findThreeTakeOneThan(card, cardCnt, minCard, bombTarget);
				break;
			case THREE_TAKE_TWO :
				rs = findThreeTakeTwoThan(card, cardCnt, minCard, bombTarget);
				break;
			case THREE_TAKE_11 :
				rs = findThreeTake11Than(card, cardCnt, minCard, bombTarget);
				break;
			case FOUR_TAKE_THREE :
				rs = findFourTakeThreeThan(card, cardCnt, minCard, bombTarget);
				break;
			case FOUR_TAKE_TWO_SINGLE :
				rs = findFourTakeTwoSingleThan(card, cardCnt, minCard, bombTarget);
				break;
			case FOUR_TAKE_TWO_DOUBLE :
				rs = findFourTakeTwoDoubleThan(card, cardCnt, minCard, bombTarget);
				break;
			case BOMB :
				rs = findBombThan(card, cardCnt, minCard, threeABomb);
				break;
		}
		if (NONE == rs && EPokerCardType.BOMB != cardType) {
			rs = findBombThan(card, cardCnt, (byte) -1, threeABomb);
		}
		if (NONE == rs) {
			rs = findKingFried(card, cardCnt);
		}
		return rs;
	}
	public static byte checkCardType(List<Byte> card, EPokerCardType cardType) {
		switch (cardType) {
			case SINGLE :
				return isSingle(card);
			case DOUBLE :
				return isDouble(card);
			case THREE :
				return isThree(card);
			case SINGLE_LINE :
				return isSingleLine(card);
			case DOUBLE_LINE :
				return isDoubleLine(card);
			case THREE_LINE :
				return isThreeLine(card);
			case THREE_TAKE_ONE :
				return isThreeTakeOne(card);
			case THREE_TAKE_TWO :
				return isThreeTakeTwo(card);
			case FOUR_TAKE_THREE :
				return isFourTakeThree(card);
			case FOUR_TAKE_TWO_SINGLE :
				return isFourTakeTwoSingle(card);
			case FOUR_TAKE_TWO_DOUBLE :
				return isFourTakeTwoDouble(card);
			case BOMB :
				return isBomb(card);
			case KING_FRIED :
				return isKingFried(card);
			case WUSHIK_WSKBOMB :
				return isWuShiK(card);
			case WUSHIK_WSKBIGBOMB :
				return isBigWuShiK(card);
		}
		return -1;
	}

	public static EPokerCardType getCardTypeByCards(List<Byte> card) {
		if (1 == card.size()) {
			return EPokerCardType.SINGLE;
		} else if (2 == card.size()) {
			if (-1 != isKingFried(card)) {
				return EPokerCardType.KING_FRIED;
			}
			if (-1 != isDouble(card)) {
				return EPokerCardType.DOUBLE;
			}
			return EPokerCardType.NONE;
		} else if (3 == card.size()) {
			if (-1 != isThree(card)) {
				return EPokerCardType.THREE;
			}
			return EPokerCardType.NONE;
		} else if (4 == card.size()) {
			if (-1 != isBomb(card)) {
				return EPokerCardType.BOMB;
			}
			if (-1 != isThreeTakeOne(card)) {
				return EPokerCardType.THREE_TAKE_ONE;
			}
			if (-1 != isDoubleLine(card)) {
				return EPokerCardType.DOUBLE_LINE;
			}
			return EPokerCardType.NONE;
		} else {
			if (-1 != isSingleLine(card)) {
				return EPokerCardType.SINGLE_LINE;
			}
			if (-1 != isDoubleLine(card)) {
				return EPokerCardType.DOUBLE_LINE;
			}
			if (-1 != isThreeTakeTwo(card)) {
				return EPokerCardType.THREE_TAKE_TWO;
			}
			if (-1 != isFourTakeThree(card)) {
				return EPokerCardType.FOUR_TAKE_THREE;
			}
			if (-1 != isFourTakeTwoSingle(card)) {
				return EPokerCardType.FOUR_TAKE_TWO_SINGLE;
			}
			if (-1 != isFourTakeTwoDouble(card)) {
				return EPokerCardType.FOUR_TAKE_TWO_DOUBLE;
			}
			if (-1 != isThreeLine(card)) {
				return EPokerCardType.THREE_LINE;
			}
		}

		return EPokerCardType.NONE;
	}

	public static EPokerCardType getWuShiKCardTypeByCards(List<Byte> card) {
		EPokerCardType cardType = getCardTypeByCards(card);
		if (cardType != EPokerCardType.NONE) {
			return cardType;
		}

		if (3 == card.size()) {
			if (-1 != isBigWuShiK(card)) {
				return EPokerCardType.WUSHIK_WSKBIGBOMB;
			}
			if (-1 != isWuShiK(card)) {
				return EPokerCardType.WUSHIK_WSKBOMB;
			}
			if (-1 != isWuShiKThressKingBomb(card)) {
				return EPokerCardType.WUSHIK_THREEKINGBOMB;
			}
			return EPokerCardType.NONE;
		} else if (4 == card.size()) {
			if (-1 != isWuShikFourKingBomb(card)) {
				return EPokerCardType.WUSHIK_FOURKINGBOMB;
			}
			return EPokerCardType.NONE;
		} else {
			if (-1 != isWuShikFourUp(card)) {
				return EPokerCardType.WUSHIK_FOURUP;
			}
			if (-1 != isWuShiKThreeLineTakeOne(card)) {
				return EPokerCardType.WUSHIK_THREELINE_TAKE_ONE;
			}
			if (-1 != isWuShiKThreeLineTakeTwo(card)) {
				return EPokerCardType.WUSHIK_THREELINE_TAKE_TWO;
			}
			if (-1 != isWuShiKSingLine(card)) {
				return EPokerCardType.WUSHIK_SINGLINE;
			}
			if (-1 != isWuShikThreeline(card)) {
				return EPokerCardType.WUSHIK_THREELINE;
			}
		}

		return EPokerCardType.NONE;
	}

	public static byte isSingle(List<Byte> card) {
		if (1 == card.size()) {
			return getCardValue(card.get(0));
		}
		return -1;
	}

	public static byte isDouble(List<Byte> card) {
		if (2 != card.size()) {
			return -1;
		}
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		if (card1 == card2) {
			return card1;
		}
		return -1;
	}

	public static byte isThree(List<Byte> card) {
		if (3 != card.size()) {
			return -1;
		}
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		if (card1 != card2 || card2 != card3 || card3 != card1) {
			return -1;
		}
		return card1;
	}

	public static byte isThreeTakeOne(List<Byte> card) {
		if (4 != card.size()) {
			return -1;
		}
		clear();
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		byte card4 = getCardValue(card.get(3));
		++temp.get()[card1];
		++temp.get()[card2];
		++temp.get()[card3];
		++temp.get()[card4];
		for (int i = 0; i < 15; ++i) {
			if (3 == temp.get()[i]) {
				return (byte) i;
			}
		}
		return -1;
	}

	public static byte isThreeTakeTwo(List<Byte> card) {
		if (5 != card.size()) {
			return -1;
		}
		clear();
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		byte card4 = getCardValue(card.get(3));
		byte card5 = getCardValue(card.get(4));
		++temp.get()[card1];
		++temp.get()[card2];
		++temp.get()[card3];
		++temp.get()[card4];
		++temp.get()[card5];
		for (int i = 0; i < 15; ++i) {
			if (3 == temp.get()[i]) {
				return (byte) i;
			}
		}
		return -1;
	}

	public static byte isFourTakeThree(List<Byte> card) {
		if (7 != card.size()) {
			return -1;
		}
		clear();
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		byte card4 = getCardValue(card.get(3));
		byte card5 = getCardValue(card.get(4));
		byte card6 = getCardValue(card.get(5));
		byte card7 = getCardValue(card.get(6));
		++temp.get()[card1];
		++temp.get()[card2];
		++temp.get()[card3];
		++temp.get()[card4];
		++temp.get()[card5];
		++temp.get()[card6];
		++temp.get()[card7];
		for (int i = 0; i < 15; ++i) {
			if (4 == temp.get()[i]) {
				return (byte) i;
			}
		}
		return -1;
	}

	public static byte isFourTakeTwoSingle(List<Byte> card) {
		if (6 != card.size()) {
			return -1;
		}
		clear();
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		byte card4 = getCardValue(card.get(3));
		byte card5 = getCardValue(card.get(4));
		byte card6 = getCardValue(card.get(5));
		++temp.get()[card1];
		++temp.get()[card2];
		++temp.get()[card3];
		++temp.get()[card4];
		++temp.get()[card5];
		++temp.get()[card6];
		for (int i = 0; i < 15; ++i) {
			if (4 == temp.get()[i]) {
				return (byte) i;
			}
		}
		return -1;
	}

	public static byte isFourTakeTwoDouble(List<Byte> card) {
		if (8 != card.size()) {
			return -1;
		}
		int doubleCnt = 0;
		int fourValue = -1;
		clear();
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		byte card4 = getCardValue(card.get(3));
		byte card5 = getCardValue(card.get(4));
		byte card6 = getCardValue(card.get(5));
		byte card7 = getCardValue(card.get(6));
		byte card8 = getCardValue(card.get(7));
		++temp.get()[card1];
		++temp.get()[card2];
		++temp.get()[card3];
		++temp.get()[card4];
		++temp.get()[card5];
		++temp.get()[card6];
		++temp.get()[card7];
		++temp.get()[card8];
		for (int i = 0; i < 15; ++i) {
			if (4 == temp.get()[i]) {
				// return (byte) i;
				fourValue = i;
			} else if (2 == temp.get()[i]) {
				++doubleCnt;
			}
		}
		if ((-1 != fourValue) && (2 == doubleCnt)) {
			return (byte) fourValue;
		} else {
			return -1;
		}
	}

	public static byte isSingleLine(List<Byte> card) {
		if (card.size() < 5) {
			return -1;
		}
		sort(card);
		byte temp = getCardValue(card.get(0));
		for (int i = 1, len = card.size(); i < len; ++i) {
			byte temp2 = getCardValue(card.get(i));
			if (temp2 > 11) {
				return -1;
			}
			if (temp2 - temp != 1) {
				return -1;
			}
			temp = temp2;
		}
		return temp;
	}

	public static byte isDoubleLine(List<Byte> card) {
		if (card.size() < 4 || (0 != card.size() % 2)) {
			return -1;
		}
		sort(card);
		byte temp = -1;
		for (int i = 0, len = card.size(); i < len; i += 2) {
			byte temp1 = getCardValue(card.get(i));
			byte temp2 = getCardValue(card.get(i + 1));
			if (temp1 != temp2) {
				return -1;
			}
			if (-1 == temp) {
				temp = temp1;
			} else if ((temp1 - temp) != 1) {
				return -1;
			} else {
				temp = temp1;
			}
		}
		return temp;
	}

	public static byte isThreeLine(List<Byte> card) {
		if (card.size() < 6 || (0 != card.size() % 3)) {
			return -1;
		}
		sort(card);
		byte temp = -1;
		for (int i = 0, len = card.size(); i < len; i += 3) {
			byte temp1 = getCardValue(card.get(i));
			byte temp2 = getCardValue(card.get(i + 1));
			byte temp3 = getCardValue(card.get(i + 2));
			if (temp1 != temp2 || temp2 != temp3 || temp3 != temp1) {
				return -1;
			}
			if (-1 == temp) {
				temp = temp1;
			} else if ((temp1 - temp) != 1) {
				return -1;
			} else {
				temp = temp1;
			}
		}
		return temp;
	}

	public static byte isBomb(List<Byte> card) {
		if (4 != card.size()) {
			return -1;
		}
		clear();
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		byte card4 = getCardValue(card.get(3));
		++temp.get()[card1];
		++temp.get()[card2];
		++temp.get()[card3];
		++temp.get()[card4];
		for (int i = 0; i < 15; ++i) {
			if (4 == temp.get()[i]) {
				return (byte) i;
			}
		}
		return -1;
	}

	public static byte isKingFried(List<Byte> card) {
		byte c1 = card.get(0);
		byte c2 = card.get(1);
		if ((KINGLET == c1 && KING == c2) || (KINGLET == c2 && KING == c1)) {
			return EPokerCardType.KING_FRIED.getValue();
		}
		return -1;
	}

	public static byte isWuShiK(List<Byte> card) {
		if (3 != card.size()) {
			return -1;
		}
		sort(card);
		byte card1 = getCardValue(card.get(0));
		byte card2 = getCardValue(card.get(1));
		byte card3 = getCardValue(card.get(2));
		if (card1 == FIVE && card2 == TEN && card3 == K) {
			return card1;
		}
		return -1;
	}

	public static byte isBigWuShiK(List<Byte> card) {
		if (-1 != isWuShiK(card)) {
			byte cardColor1 = getCardColor(getCardValue(card.get(0)));
			byte cardColor2 = getCardColor(getCardValue(card.get(1)));
			byte cardColor3 = getCardColor(getCardValue(card.get(2)));
			if (cardColor1 == cardColor2 && cardColor2 == cardColor3) {
				return EPokerCardType.WUSHIK_WSKBIGBOMB.getValue();
			}
		}
		return -1;
	}

	// 四张以及以上：如：3333；55555；999999；KKKKKKK；22222222
	public static byte isWuShikFourUp(List<Byte> card) {
		byte cardSize = (byte) card.size();
		if (cardSize >= 4) {
			byte temp1 = getCardValue(card.get(0));
			for (int i = 1; i < cardSize; ++i) {
				byte cardValue = getCardValue(card.get(i));
				if (cardValue != temp1) {
					return -1;
				}
			}
			return EPokerCardType.WUSHIK_FOURUP.getValue();
		}
		return -1;
	}

	public static byte isWuShikThreeline(List<Byte> card) {
		byte cardSize = (byte) card.size();
		if (cardSize < 6 || (0 != cardSize % 3)) {
			return -1;
		}
		sort(card);
		byte temp = -1;
		for (int i = 0; i < cardSize; i += 3) {
			byte temp1 = getCardValue(card.get(i));
			byte temp2 = getCardValue(card.get(i + 1));
			byte temp3 = getCardValue(card.get(i + 2));
			if (!(temp1 == temp2 && temp2 == temp3)) {
				return -1;
			}
			if (-1 == temp) {
				temp = temp1;
			} else if ((temp1 - temp) != 1) {
				return -1;
			} else {
				temp = temp1;
			}
		}
		return EPokerCardType.WUSHIK_THREELINE.getValue();
	}

	public static byte isWuShikFourKingBomb(List<Byte> card) {
		byte cardSize = (byte) card.size();
		if (cardSize != 4) {
			return -1;
		}
		for (int i = 0; i < cardSize; i++) {
			byte temp = getCardValue(card.get(i));
			if (!(temp == KING || temp == KINGLET)) {
				return -1;
			}
		}

		return EPokerCardType.WUSHIK_FOURKINGBOMB.getValue();
	}

	public static byte isWuShiKThressKingBomb(List<Byte> card) {
		byte cardSize = (byte) card.size();
		if (cardSize != 3) {
			return -1;
		}
		for (int i = 0; i < cardSize; i++) {
			byte temp = getCardValue(card.get(i));
			if (!(temp == KING || temp == KINGLET)) {
				return -1;
			}
		}
		return EPokerCardType.WUSHIK_THREEKINGBOMB.getValue();
	}

	// 三顺+数量相同的，几个三顺点数带几张牌，如：555666+89
	public static byte isWuShiKThreeLineTakeOne(List<Byte> card) {
		if (card.size() < 8) {
			return -1;
		}
		clear();
		byte threeLineCnt = 0;
		for (int i = 0; i < card.size(); i++) {
			byte cardValue = getCardValue(card.get((byte) i));
			++temp.get()[cardValue];
		}

		for (int i = 0; i < 15; ++i) {
			if (3 == temp.get()[i]) {
				++threeLineCnt;
			}
		}
		if (threeLineCnt * 3 + threeLineCnt == card.size()) {
			return EPokerCardType.WUSHIK_THREELINE_TAKE_ONE.getValue();
		}
		return -1;
	}

	// 三顺＋数量相同的对子，几个三顺点数带几个对牌
	public static byte isWuShiKThreeLineTakeTwo(List<Byte> card) {
		if (card.size() < 10) {
			return -1;
		}

		clear();
		byte threeLineCnt = 0;
		byte doubleCnt = 0;
		for (int i = 0; i < card.size(); i++) {
			byte cardValue = getCardValue(card.get((byte) i));
			++temp.get()[cardValue];
		}

		for (int i = 0; i < 15; ++i) {
			if (3 == temp.get()[i]) {
				++threeLineCnt;
			} else if (2 == temp.get()[i]) {
				++doubleCnt;
			}
		}

		if ((threeLineCnt * 3 + doubleCnt * 2) == card.size()) {
			return EPokerCardType.WUSHIK_THREELINE_TAKE_TWO.getValue();
		}

		return -1;
	}

	// 可以出现A2345
	public static byte isWuShiKSingLine(List<Byte> card) {
		if (-1 != isSingleLine(card)) {
			return EPokerCardType.WUSHIK_SINGLINE.getValue();
		}
		sort(card);
		if (card.size() >= 5) {
			byte temp = 0;
			for (Byte cardNum : card) {
				byte cardValue = getCardValue(cardNum);
				if (cardValue == K) {
					temp = cardValue;
				}
				if (cardValue == TWO && temp == K) {// 含有2 和 K 直接返回；
					return -1;
				}
			}

			byte cardValue1 = getCardValue(card.get(card.size() - 2));
			byte cardValue2 = getCardValue(card.get(card.size() - 1));
			if (cardValue1 == A && cardValue2 == TWO) {
				byte temp1 = getCardValue(card.get(0));
				for (byte i = 1; i < card.size() - 2; i++) {
					byte temp2 = getCardValue(card.get(i));
					if (temp2 - temp1 != 1) {
						return -1;
					}
					temp1 = temp2;
				}
			}

			if (cardValue2 == TWO) {
				byte temp1 = getCardValue(card.get(0));
				for (byte i = 1; i < card.size() - 1; i++) {
					byte temp2 = getCardValue(card.get(i));
					if (temp2 - temp1 != 1) {
						return -1;
					}
					temp1 = temp2;
				}
			}
		}
		return EPokerCardType.WUSHIK_SINGLINE.getValue();
	}

	public static byte[] findSingleThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte single = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (i <= minCard) {
				continue;
			}
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (1 == cardCnt[i]) {
				single = (byte) i;
				break;
			}
			if (-1 == single) {
				single = (byte) i;
			}
		}
		if (-1 != single)

			for (Byte c : card) {
				if (single == getCardValue(c)) {
					return new byte[]{EPokerCardType.SINGLE.getValue(), c, single};
				}
			}
		return NONE;
	}

	public static byte[] findDoubleThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte _double = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (i <= minCard) {
				continue;
			}
			if (cardCnt[i] < 2) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (2 == cardCnt[i]) {
				_double = (byte) i;
				break;
			}
			if (-1 == _double) {
				_double = (byte) i;
			}
		}
		if (-1 != _double) {
			byte[] rs = new byte[4];
			rs[0] = EPokerCardType.DOUBLE.getValue();
			int index = 1;
			for (Byte c : card) {
				if (_double == getCardValue(c)) {
					rs[index++] = c;
					if (index == rs.length - 1) {
						break;
					}
				}
			}
			rs[index] = _double;
			return rs;
		}
		return NONE;
	}

	public static byte[] findThreeThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte three = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (i <= minCard) {
				continue;
			}
			if (cardCnt[i] < 3) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (3 == cardCnt[i]) {
				three = (byte) i;
				break;
			}
			if (-1 == cardCnt[i]) {
				three = (byte) i;
			}
		}
		if (-1 != three) {
			byte[] rs = new byte[5];
			rs[0] = EPokerCardType.THREE.getValue();
			int index = 1;
			for (Byte c : card) {
				if (three == getCardValue(c)) {
					rs[index++] = c;
					if (index == rs.length - 1) {
						break;
					}
				}
			}
			rs[index] = three;
			return rs;
		}
		return NONE;
	}

	public static byte[] findThreeTakeOneThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte single = -1;
		byte three = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (i <= minCard) {
				if (-1 == single || (1 == cardCnt[i] && cardCnt[single] > 1)) {
					single = (byte) i;
				}
				continue;
			}
			if (-1 == three && cardCnt[i] > 2) {
				three = (byte) i;
			} else if (-1 == single || (1 == cardCnt[i] && cardCnt[single] > 1)) {
				single = (byte) i;
			}
		}
		if (-1 != three && -1 != single) {
			byte[] rs = new byte[6];
			rs[0] = EPokerCardType.THREE_TAKE_ONE.getValue();
			int index = 1;
			boolean hasAddSingle = false;
			for (Byte c : card) {
				byte temp = getCardValue(c);
				if (single == temp && !hasAddSingle) {
					rs[index++] = c;
					hasAddSingle = true;
				} else if (three == temp) {
					rs[index++] = c;
				}
				if (5 == index) {
					break;
				}
			}
			rs[index] = three;
			return rs;
		}
		return NONE;
	}

	public static byte[] findThreeTakeTwoThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte three = -1;
		int two1 = -1;
		int two2 = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (i <= minCard) {
				if (-1 == two1) {
					two1 = (byte) i;
				} else if (-1 == two2) {
					two2 = (byte) i;
					if (cardCnt[two1] > cardCnt[two2]) {
						two2 = two1 ^ two2;
						two1 = two1 ^ two2;
						two2 = two1 ^ two2;
					}
				} else {
					if (cardCnt[i] < cardCnt[two2]) {
						two2 = i;
					} else if (cardCnt[i] < cardCnt[two1]) {
						two1 = i;
					}
				}
				continue;
			}
			if (-1 == three && cardCnt[i] > 2) {
				three = (byte) i;
			} else {
				if (-1 == two1) {
					two1 = (byte) i;
				} else if (-1 == two2) {
					two2 = (byte) i;
					if (cardCnt[two1] > cardCnt[two2]) {
						two2 = two1 ^ two2;
						two1 = two1 ^ two2;
						two2 = two1 ^ two2;
					}
				} else {
					if (cardCnt[i] < cardCnt[two2]) {
						two2 = i;
					} else if (cardCnt[i] < cardCnt[two1]) {
						two1 = i;
					}
				}
			}
			if (-1 != three && (-1 != two1 && (cardCnt[two1] + (-1 == two2 ? 0 : cardCnt[two2])) > 1)) {
				byte[] rs = new byte[7];
				rs[0] = EPokerCardType.THREE_TAKE_TWO.getValue();
				int index = 1;
				int addThree = 3;
				int addOther1 = cardCnt[two1] > 2 ? 2 : cardCnt[two1];
				int addOther2 = 2 - addOther1;
				for (Byte c : card) {
					byte temp = getCardValue(c);
					if (two1 == temp && addOther1 > 0) {
						rs[index++] = c;
						--addOther1;
					} else if (two2 == temp && addOther2 > 0) {
						rs[index++] = c;
						--addOther2;
					} else if (three == temp && addThree > 0) {
						rs[index++] = c;
						--addThree;
					}
					if (0 == addThree && 0 == addOther1 && 0 == addOther2) {
						break;
					}
				}
				rs[index] = three;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findThreeTake11Than(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte three = -1;
		byte pair = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (i <= minCard) {
				if (cardCnt[i] > 1 && (-1 == pair || (cardCnt[i] < cardCnt[pair]))) {
					pair = (byte) i;
				}
				continue;
			}
			if (-1 == three && cardCnt[i] > 2) {
				three = (byte) i;
			} else if (cardCnt[i] > 1 && (-1 == pair || (cardCnt[i] < cardCnt[pair]))) {
				pair = (byte) i;
			}
			if (-1 != three && -1 != pair) {
				byte[] rs = new byte[7];
				rs[0] = EPokerCardType.THREE_TAKE_TWO.getValue();
				int index = 1;
				int addThree = 3;
				int addOther = 2;
				for (Byte c : card) {
					byte temp = getCardValue(c);
					if (pair == temp && addOther > 0) {
						rs[index++] = c;
						--addOther;
					} else if (three == temp && addThree > 0) {
						rs[index++] = c;
						--addThree;
					}
					if (0 == addOther && 0 == addThree) {
						break;
					}
				}
				rs[index] = three;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findFourTakeThreeThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte three = -1;
		byte four = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (i <= minCard) {
				if (cardCnt[i] > 2) {
					three = (byte) i;
				}
				continue;
			}
			if (-1 == four && cardCnt[i] > 3) {
				four = (byte) i;
			} else if (-1 == three && cardCnt[i] > 2) {
				three = (byte) i;
			}
			if (-1 != four && -1 != three) {
				byte[] rs = new byte[9];
				rs[0] = EPokerCardType.FOUR_TAKE_THREE.getValue();
				int index = 1;
				int threeCnt = 0;
				for (Byte c : card) {
					byte temp = getCardValue(c);
					if (three == temp && threeCnt < 3) {
						rs[index++] = c;
						++threeCnt;
					} else if (four == temp) {
						rs[index++] = c;
					}
					if (8 == index) {
						break;
					}
				}
				rs[index] = four;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findFourTakeTwoSingleThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte four = -1;
		byte two1 = -1;
		byte two2 = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (i <= minCard) {
				if (cardCnt[i] > 0) {
					two1 = (byte) i;
				}
				continue;
			}
			if (-1 == four && cardCnt[i] > 3) {
				four = (byte) i;
			} else if (cardCnt[i] > 0) {
				if (-1 == two1) {
					two1 = (byte) i;
				} else if (-1 == two2) {
					two2 = (byte) i;
				}
			}
			if (-1 != four && -1 != two1 && -1 != two2) {
				byte[] rs = new byte[8];
				rs[0] = EPokerCardType.FOUR_TAKE_TWO_SINGLE.getValue();
				int index = 1;
				boolean hasAddTwo1 = false;
				boolean hasAddTwo2 = false;
				for (Byte c : card) {
					byte temp = getCardValue(c);
					if (two1 == temp && !hasAddTwo1) {
						rs[index++] = c;
						hasAddTwo1 = true;
					} else if (two2 == temp && !hasAddTwo2) {
						rs[index++] = c;
						hasAddTwo2 = true;
					} else if (four == temp) {
						rs[index++] = c;
					}
					if (7 == index) {
						break;
					}
				}
				rs[index] = four;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findFourTakeTwoDoubleThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean bombTarget) {
		byte four = -1;
		byte two1 = -1;
		byte two2 = -1;
		for (int i = 0, len = cardCnt.length; i < len; ++i) {
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (i <= minCard) {
				if (cardCnt[i] > 1) {
					two1 = (byte) i;
				}
				continue;
			}
			if (-1 == four && cardCnt[i] > 3) {
				four = (byte) i;
			} else if (cardCnt[i] > 1) {
				if (-1 == two1) {
					two1 = (byte) i;
				} else if (-1 == two2) {
					two2 = (byte) i;
				}
			}
			if (-1 != four) {
				byte[] rs = new byte[10];
				rs[0] = EPokerCardType.FOUR_TAKE_TWO_DOUBLE.getValue();
				int index = 1;
				int addTwo1Cnt = 0;
				int addTwo2Cnt = 0;
				for (Byte c : card) {
					byte temp = getCardValue(c);
					if (two1 == temp && addTwo1Cnt < 2) {
						rs[index++] = c;
						++addTwo1Cnt;
					} else if (two2 == temp && addTwo2Cnt < 2) {
						rs[index++] = c;
						++addTwo2Cnt;
					} else if (four == temp) {
						rs[index++] = c;
					}
					if (9 == index) {
						break;
					}
				}
				rs[index] = four;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findSingleLineThan(List<Byte> card, byte[] cardCnt, byte minCard, byte cnt, boolean bombTarget) {
		int j = 0;
		int min = 0;
		for (int i = minCard + 1; i < 12; ++i) {
			j = i;
			min = i - cnt;
			for (; j > min && j >= 0; --j) {
				if (cardCnt[j] < 1) {
					break;
				}
				if (bombTarget && 4 == cardCnt[j]) {
					break;
				}
			}
			if (j == min) {
				byte[] rs = new byte[cnt + 1 + 1];
				rs[0] = EPokerCardType.SINGLE_LINE.getValue();
				int index = 1;
				for (Byte c : card) {
					if ((j + index) == getCardValue(c)) {
						rs[index++] = c;
						if (index == cnt + 1) {
							break;
						}
					}
				}
				rs[index] = (byte) i;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findDoubleLineThan(List<Byte> card, byte[] cardCnt, byte minCard, byte cnt, boolean bombTarget) {
		int j = 0;
		int min = 0;
		for (int i = minCard + 1; i < 12; ++i) {
			j = i;
			min = i - cnt;
			for (; j > min && j >= 0; --j) {
				if (cardCnt[j] < 2) {
					break;
				}
				if (bombTarget && 4 == cardCnt[j]) {
					break;
				}
			}
			if (j == min) {
				byte[] rs = new byte[cnt * 2 + 1 + 1];
				rs[0] = EPokerCardType.DOUBLE_LINE.getValue();
				int temp = 0;
				int index = 1;
				int len = rs.length - 1;
				for (Byte c : card) {
					byte value = getCardValue(c);
					if (value <= i && value > min) {
						int tempValue = (temp >> (value - min - 1) * 2) & 0x03;
						if (tempValue < 2) {
							rs[index++] = c;
							if (index >= len) {
								break;
							}
							temp &= ~(3 << (value - min - 1) * 2);
							temp |= (tempValue + 1) << ((value - min - 1) * 2);
						}
					}
				}
				rs[index] = (byte) i;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findThreeLineThan(List<Byte> card, byte[] cardCnt, byte minCard, byte cnt, boolean bombTarget) {
		int j = 0;
		clear();
		byte singleCnt = (byte) (2 * cnt);
		int minValue = -1;
		for (int i = 0; i < 12; ++i) {
			if (cardCnt[i] < 1) {
				continue;
			}
			if (bombTarget && 4 == cardCnt[i]) {
				continue;
			}
			if (i < (minCard - cnt + 2)) {
				if (singleCnt > 0) {
					temp.get()[i] = cardCnt[i] < singleCnt ? cardCnt[i] : singleCnt;
					singleCnt -= cardCnt[i];
				}
				continue;
			}
			if (-1 == minValue) {
				for (j = 0; j < cnt; ++j) {
					if (cardCnt[i + j] < 3) {
						break;
					}
					if (bombTarget && 4 == cardCnt[i + j]) {
						break;
					}
				}
				if (j == cnt) {
					minValue = i;
					i += cnt - 1;
					for (int k = 0; k < cnt; ++k) {
						if (cardCnt[minValue + k] > 3) {
							if (singleCnt < 1) {
								break;
							}
							temp.get()[minCard + k] = 1;
							singleCnt -= 1;
						}
					}
					continue;
				}
			}
			if (singleCnt > 0) {
				temp.get()[i] = cardCnt[i] < singleCnt ? cardCnt[i] : singleCnt;
				singleCnt -= cardCnt[i];
			}
			if (minValue > 0 && singleCnt < 1) {
				break;
			}
		}
		if (-1 != minValue && singleCnt < 1) {
			if (minValue + cnt - 1 > A) {
				return NONE;
			}
			byte[] rs = new byte[cnt * 5 + 1 + 1];
			rs[0] = EPokerCardType.THREE_LINE.getValue();
			int temp1 = 0;
			int index = 1;
			int len = rs.length - 1;
			for (Byte c : card) {
				byte value = getCardValue(c);
				if (temp.get()[value] > 0) {
					--temp.get()[value];
					rs[index++] = c;
				} else if (value >= minValue && value < (minValue + cnt)) {
					int tempValue = (temp1 >> (value - minValue) * 2) & 0x03;
					if (tempValue < 3) {
						rs[index++] = c;
						if (index >= len) {
							break;
						}
						temp1 &= ~(3 << (value - minValue) * 2);
						temp1 |= (tempValue + 1) << ((value - minValue) * 2);
					}
				}
			}
			rs[index] = (byte) (minValue + cnt - 1);
			return rs;
		}
		return NONE;
	}

	public static byte[] findBombThan(List<Byte> card, byte[] cardCnt, byte minCard, boolean threeABomb) {
		for (int i = minCard + 1, len = cardCnt.length; i < len; ++i) {
			if (threeABomb && 3 == cardCnt[i] && A == i) {
				byte[] rs = new byte[5];
				rs[0] = EPokerCardType.BOMB.getValue();
				int index = 1;
				for (Byte c : card) {
					if (i == getCardValue(c)) {
						rs[index++] = c;
						if (4 == index) {
							break;
						}
					}
				}
				rs[index] = (byte) i;
				return rs;
			}
			if (4 == cardCnt[i]) {
				byte[] rs = new byte[6];
				rs[0] = EPokerCardType.BOMB.getValue();
				int index = 1;
				for (Byte c : card) {
					if (i == getCardValue(c)) {
						rs[index++] = c;
						if (5 == index) {
							break;
						}
					}
				}
				rs[index] = (byte) i;
				return rs;
			}
		}
		return NONE;
	}

	public static byte[] findKingFried(List<Byte> card, byte[] cardCnt) {
		byte kinglet = getCardValue(KINGLET);
		byte king = getCardValue(KING);
		if (cardCnt.length > king && 0 != cardCnt[kinglet] && 0 != cardCnt[king]) {
			return new byte[]{EPokerCardType.KING_FRIED.getValue(), kinglet, king, kinglet};
		}
		return NONE;
	}

	public static byte[] getCardType(List<Byte> card, boolean bombTarget, int threeTake, int fourTake, boolean canLessThreeTake,
			int minDoubleLineCnt, boolean canLessThreeLink, boolean threeABomb) {
		if (null == card || card.isEmpty()) {
			return NONECARD;
		}
		if (1 == card.size()) {
			return new byte[]{EPokerCardType.SINGLE.getValue(), getCardValue(card.get(0)), 1};
		} else if (2 == card.size()) {
			byte c1 = card.get(0);
			byte c2 = card.get(1);
			if ((KINGLET == c1 && KING == c2) || (KINGLET == c2 && KING == c1)) {
				return new byte[]{EPokerCardType.KING_FRIED.getValue(), -1, -1};
			}

			c1 = getCardValue(card.get(0));
			c2 = getCardValue(card.get(1));
			if (c1 == c2) {
				return new byte[]{EPokerCardType.DOUBLE.getValue(), c1, 1};
			}
		} else if (3 == card.size()) {
			sort(card);
			byte c1 = getCardValue(card.get(0));
			byte c2 = getCardValue(card.get(1));
			byte c3 = getCardValue(card.get(2));
			if (c1 == c2 && c1 == c3 && c2 == c3) {
				if (threeABomb && A == c1) {
					return new byte[]{EPokerCardType.BOMB.getValue(), c1, 1};
				}
				if (0 == (threeTake & PokerUtil.THREE_TAKE_TYPE_0) && !canLessThreeTake) {
					return NONECARD;
				}
				return new byte[]{EPokerCardType.THREE.getValue(), c1, 1};
			}
			if (c1 == FIVE && c2 == TEN && c3 == K) {
				return new byte[]{EPokerCardType.WUSHIK_WSKBOMB.getValue(), c1, 1};
			}
			if (canLessThreeLink) {
				if (TWO != c3 && c3 - c2 == 1 && c2 - c1 == 1) {
					return new byte[]{EPokerCardType.SINGLE_LINE.getValue(), c3, 3};
				}
			}
		} else {
			clear();
			for (Byte b : card) {
				++temp.get()[getCardValue(b)];
			}
			int _upFour = 0;
			int _four = 0;
			int _three = 0;
			int _two = 0;
			int _single = 0;
			boolean AAA = false;
			for (Byte i = 0; i < 15; ++i) {
				if (temp.get()[i] < 0) {
					continue;
				}
				if (1 == temp.get()[i]) {
					single.get()[_single] = i;
					++_single;
				} else if (2 == temp.get()[i]) {
					two.get()[_two] = i;
					++_two;
				} else if (3 == temp.get()[i]) {
					if (threeABomb && A == i) {
						four.get()[_four] = i;
						++_four;
						AAA = true;
						if (!bombTarget) {
							three.get()[_three] = i;
							++_three;
							single.get()[_single] = i;
							++_single;
							two.get()[_two] = i;
							++_two;
						}
					} else {
						three.get()[_three] = i;
						++_three;
					}
				} else if (4 == temp.get()[i]) {
					four.get()[_four] = i;
					++_four;
					if (!bombTarget) {
						three.get()[_three] = i;
						++_three;
						single.get()[_single] = i;
						++_single;
						two.get()[_two] = i;
						++_two;
						two.get()[_two] = i;
						++_two;
					}
				} else if (temp.get()[i] > 4) {
					++_upFour;
				}
			}
			if (_upFour > 0) {
				if (1 == _upFour) {
					if (0 == _four && 0 == _three && 0 == _two && 0 == _single) {
						return new byte[]{EPokerCardType.BOMB.getValue(), card.get(0), 1};
					}
				}
				return NONECARD;
			}
			if (_four > 0) {
				if (1 == _four) {
					if (AAA) {
						// 3A
						if (3 == card.size()) {
							// 炸弹
							return new byte[]{EPokerCardType.BOMB.getValue(), four.get()[0], 1};
						} else if (5 == card.size()) {
							// 四代二
							if (0 != (fourTake & PokerUtil.FOUR_TAKE_TYPE_2)) {
								return new byte[]{EPokerCardType.FOUR_TAKE_TWO.getValue(), four.get()[0], 1};
							}
						} else if (6 == card.size()) {
							// 四代三
							if (0 != (fourTake & PokerUtil.FOUR_TAKE_TYPE_3)) {
								return new byte[]{EPokerCardType.FOUR_TAKE_THREE.getValue(), four.get()[0], 1};
							}
						} else if (7 == card.size()) {
							// 四代2对
							if (2 == _two && (0 != (fourTake & PokerUtil.FOUR_TAKE_TYPE_11_22))) {
								return new byte[]{EPokerCardType.FOUR_TAKE_TWO_DOUBLE.getValue(), four.get()[0], 1};
							}
						}
					} else {
						if (4 == card.size()) {
							// 炸弹
							return new byte[]{EPokerCardType.BOMB.getValue(), four.get()[0], 1};
						} else if (6 == card.size()) {
							// 四代二
							if (0 != (fourTake & PokerUtil.FOUR_TAKE_TYPE_2)) {
								return new byte[]{EPokerCardType.FOUR_TAKE_TWO.getValue(), four.get()[0], 1};
							}
						} else if (7 == card.size()) {
							// 四代三
							if (0 != (fourTake & PokerUtil.FOUR_TAKE_TYPE_3)) {
								return new byte[]{EPokerCardType.FOUR_TAKE_THREE.getValue(), four.get()[0], 1};
							}
						} else if (8 == card.size()) {
							// 四代2对
							if (2 == _two && (0 != (fourTake & PokerUtil.FOUR_TAKE_TYPE_11_22))) {
								return new byte[]{EPokerCardType.FOUR_TAKE_TWO_DOUBLE.getValue(), four.get()[0], 1};
							}
						}
					}
				} else if (2 == _four) {
					if ((2 == _two && !bombTarget) && (0 != (fourTake & PokerUtil.FOUR_TAKE_TYPE_11_22))) {
						return new byte[]{EPokerCardType.FOUR_TAKE_TWO_DOUBLE.getValue(), four.get()[_four - 1], 1};
					}
				}
			}
			if (_three > 0) {
				if (1 == _three) {
					// 三带
					if (4 == card.size()) {
						// 三带一
						if (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_1)
								|| (canLessThreeTake && ((0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_2))
										|| (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_11))))) {
							return new byte[]{EPokerCardType.THREE_TAKE_ONE.getValue(), three.get()[0], 1};
						}
					} else if (5 == card.size()) {
						// 三代二/三代一对
						if (1 == _two && (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_11))) {
							return new byte[]{EPokerCardType.THREE_TAKE_11.getValue(), three.get()[0], 1};
						}
						if (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_2)) {
							return new byte[]{EPokerCardType.THREE_TAKE_TWO.getValue(), three.get()[0], 1};
						}
					}
					return NONECARD;
				}
				if (_three > 1) {
					int begin = 0;
					int end = -1;
					boolean isBeginLine = false;
					byte first = three.get()[0];
					for (int i = 1; i < _three; ++i) {
						if (three.get()[i] >= TWO) {
							break;
						}
						if (1 != (three.get()[i] - first)) {
							if (!isBeginLine) {
								++begin;
								first = three.get()[i];
								continue;
							}
							break;
						}
						first = three.get()[i];
						end = i;
						isBeginLine = true;
					}

					if (-1 != end) {
						int cnt = end - begin + 1;
						while (cnt >= 2) {
							int totalTake = card.size() - 3 * cnt;
							int maxTake = 0;
							if (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_0)) {
								maxTake = 0;
								if (totalTake == maxTake) {
									return new byte[]{EPokerCardType.THREE_LINE.getValue(), three.get()[end], (byte) cnt};
								}
							}
							if (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_1)) {
								maxTake = cnt;
								if (totalTake == maxTake || (canLessThreeTake && totalTake < maxTake)) {
									return new byte[]{EPokerCardType.THREE_LINE.getValue(), three.get()[end], (byte) cnt};
								}
							}
							if (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_2)) {
								maxTake = 2 * cnt;
								if (totalTake == maxTake || (canLessThreeTake && totalTake < maxTake)) {
									return new byte[]{EPokerCardType.THREE_LINE.getValue(), three.get()[end], (byte) cnt};
								}
							}
							if (0 != (threeTake & PokerUtil.THREE_TAKE_TYPE_11)) {
								maxTake = 2 * cnt;
								if ((totalTake == maxTake && _two == cnt && 0 == _single)
										|| (canLessThreeTake && totalTake < maxTake && ((_two + _single <= cnt)))) {
									return new byte[]{EPokerCardType.THREE_LINE.getValue(), three.get()[end], (byte) cnt};
								}
							}
							--cnt;
							--end;
						}
					}
				}
			} else if (_two > 0) {
				if (0 == _single && _two >= minDoubleLineCnt) {
					byte first = two.get()[0];
					byte i = 1;
					for (; i < _two; ++i) {
						if (two.get()[i] >= TWO) {
							break;
						}
						if (1 != (two.get()[i] - first)) {
							break;
						}
						first = two.get()[i];
					}
					if (i == _two) {
						return new byte[]{EPokerCardType.DOUBLE_LINE.getValue(), two.get()[_two - 1], (byte) _two};
					}
				}
			} else if (_single > 2) {
				if (_single < 5 && !canLessThreeLink) {
					return NONECARD;
				}
				byte first = single.get()[0];
				byte i = 1;
				for (; i < _single; ++i) {
					if (single.get()[i] >= TWO) {
						break;
					}
					if (1 != (single.get()[i] - first)) {
						break;
					}
					first = single.get()[i];
				}
				if (i == _single) {
					return new byte[]{EPokerCardType.SINGLE_LINE.getValue(), single.get()[_single - 1], (byte) _single};
				}
			}
		}
		return NONECARD;
	}

	/**
	 * 牛牛--查找牛牛
	 *
	 * @param card
	 * @return
	 */
	public static EPokerCardType findCow(List<Byte> card, List<Byte> rs) {
		clear();
		byte sum = 0;
		int laizi1 = -1;
		int laizi2 = -1;
		for (int i = 0; i < 5; ++i) {
			byte t = getCardValueByCow(card.get(i));
			sum += t;
			temp.get()[i] = t;
			if (-1 == t) {
				if (-1 == laizi1) {
					laizi1 = i;
				} else if (-1 == laizi2) {
					laizi2 = i;
				}
			}
		}
		System.out.println(laizi1+",3_"+laizi2);
		if (-1 != laizi1 && -1 != laizi2) {
			// 2个癞子牛10
			int index = 0;
			for (int i = 0; i < 2;) {
				if (index != laizi1 && index != laizi2) {
					rs.add(card.get(index));
					++i;
				}
				++index;
			}
			rs.add(card.get(laizi1));
			rs.add(card.get(laizi2));
			for (int i = 0; i < 1;) {
				if (index != laizi1 && index != laizi2) {
					rs.add(card.get(index));
					++i;
				}
				++index;
			}
			return EPokerCardType.COW_10;
		}
		if (-1 != laizi1) {
			// 找出大牛
			byte max = Byte.MIN_VALUE;
			int maxI = -1, maxJ = -1, maxK = -1;
			for (int i = 0; i < 4; ++i) {
				if (-1 != maxK) {
					break;
				}
				if (-1 == temp.get()[i]) {
					continue;
				}
				for (int j = i + 1; j < 5; ++j) {
					if (-1 != maxK) {
						break;
					}
					if (-1 == temp.get()[j]) {
						continue;
					}
					for (int k = j + 1; k < 5; ++k) {
						if (0 == ((temp.get()[i] + temp.get()[j] + temp.get()[k]) % 10)) {
							max = 10;
							maxI = i;
							maxJ = j;
							maxK = k;
							break;
						}
					}
					if (-1 != maxK) {
						break;
					}
					byte curNiu = (byte) ((temp.get()[i] + temp.get()[j]) % 10);
					if (0 == curNiu) {
						curNiu = 10;
					}
					if (max < curNiu) {
						max = curNiu;
						maxI = i;
						maxJ = j;
					}
				}
			}
			for (int i = 0; i < 5; ++i) {
				if (-1 != maxK) {
					if (i == maxI || i == maxJ || i == maxK) {
						rs.add(card.get(i));
					}
				} else {
					if (i != maxI && i != maxJ) {
						rs.add(card.get(i));
					}
				}
			}
			if (-1 != maxK) {
				for (int i = 0; i < 5; ++i) {
					if (i != maxI && i != maxJ && i != maxK) {
						rs.add(card.get(i));
					}
				}
			} else {
				rs.add(card.get(maxI));
				rs.add(card.get(maxJ));
			}
			return EPokerCardType.parse((byte) (13 + max - 1));
		}
		// 无癞子
		for (int i = 0; i < 4; ++i) {
			int sumTemp = sum - temp.get()[i];
			for (int j = i + 1; j < 5; ++j) {
				if (0 == ((sumTemp - temp.get()[j]) % 10)) {
					for (int k = 0; k < 5; ++k) {
						if (k != i && k != j) {
							rs.add(card.get(k));
						}
					}
					rs.add(card.get(i));
					rs.add(card.get(j));
					byte niu = (byte) ((temp.get()[i] + temp.get()[j]) % 10);
					if (0 == niu) {
						niu = 10;
					}
					return EPokerCardType.parse((byte) (13 + niu - 1));
				}
			}
		}
		return EPokerCardType.COW_NONE;
	}

	/**
	 * 牛牛--同花顺
	 *
	 * @param card
	 * @return
	 */
	public static double isCowWithTheFlower(List<Byte> card, List<Byte> rs) {
		byte color = -1;
		byte value = -1;
		byte laiZiCnt = 0;
		byte useLaiZiCnt = 0;
		boolean ok = true;
		for (int i = 0; i < 5; ++i) {
			byte temp = getCardValueByCow2(card.get(i));
			if (-1 == temp) {
				++laiZiCnt;
				continue;
			}
			if (temp == value) {
				ok = false;
				break;
			}
			if (-1 == color) {
				color = getCardColor(card.get(i));
			} else if (color != getCardColor(card.get(i))) {
				ok = false;
				break;
			}
			if (-1 == value || (1 == (temp - value))) {
				value = temp;
				rs.add(card.get(i));
			} else if ((temp - value - 1) > laiZiCnt) {
				ok = false;
				break;
			} else {
				byte tempUseLai = (byte) (temp - value - 1);
				for (int j = 0; j < tempUseLai; ++j) {
					rs.add(card.get(useLaiZiCnt + j));
				}
				rs.add(card.get(i));
				useLaiZiCnt += tempUseLai;
				laiZiCnt -= tempUseLai;
				value = temp;
			}
		}
		double rsValue = -1;
		if (ok) {
			if (laiZiCnt > 0) {
				byte useLaiZi = 0;
				for (int j = 0; j < laiZiCnt; ++j) {
					if (value >= (K + 3)) {
						break;
					}
					++value;
					++useLaiZi;
					rs.add(card.get(useLaiZiCnt + j));
				}
				for (int j = useLaiZi; j < laiZiCnt; ++j) {
					rs.add(0, card.get(useLaiZiCnt + j));
				}
				value = card.get(4);
				rsValue = generateCardValueByCow2WithColor((byte) (getCardValueByCow2(value) + useLaiZi),
						useLaiZi > 0 ? (byte) 0 : (byte) (getCardColor(value) + 1)) * Math.pow(128, 10);
			} else {
				value = card.get(4);
				rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(value), (byte) (getCardColor(value) + 1)) * Math.pow(128, 10);
			}
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--是否是一条龙
	 *
	 * @param card
	 * @return
	 */
	public static double isCowDragon(List<Byte> card, List<Byte> rs) {
		byte value = -1;
		byte laiZiCnt = 0;
		byte useLaiZiCnt = 0;
		boolean ok = true;
		for (int i = 0; i < 5; ++i) {
			byte temp = getCardValueByCow2(card.get(i));
			if (-1 == temp) {
				++laiZiCnt;
				continue;
			}
			if (temp > 5) {
				ok = false;
				break;
			}
			if (temp == value) {
				ok = false;
				break;
			}
			if (-1 == value || (1 == (value - temp))) {
				value = temp;
				rs.add(card.get(i));
			} else if ((temp - value - 1) > laiZiCnt) {
				ok = false;
				break;
			} else {
				byte tempUseLai = (byte) (temp - value - 1);
				for (int j = 0; j < tempUseLai; ++j) {
					rs.add(card.get(useLaiZiCnt + j));
				}
				rs.add(card.get(i));
				useLaiZiCnt += tempUseLai;
				laiZiCnt -= tempUseLai;
				value = temp;
			}
		}
		double rsValue = -1;
		if (ok) {
			if (laiZiCnt > 0) {
				byte useLaiZi = 0;
				for (int j = 0; j < laiZiCnt; ++j) {
					if (value >= (FIVE + 3)) {
						break;
					}
					++value;
					++useLaiZi;
					rs.add(card.get(useLaiZiCnt + j));
				}
				for (int j = useLaiZi; j < laiZiCnt; ++j) {
					rs.add(0, card.get(useLaiZiCnt + j));
				}
				value = card.get(4);
				rsValue = generateCardValueByCow2WithColor((byte) (getCardValueByCow2(value) + useLaiZi),
						useLaiZi > 0 ? (byte) 0 : (byte) (getCardColor(value) + 1)) * Math.pow(128, 9);
			} else {
				value = card.get(4);
				rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(value), (byte) (getCardColor(value) + 1)) * Math.pow(128, 9);
			}
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--炸弹牛
	 *
	 * @param card
	 * @return
	 */
	public static double isCowBomb(List<Byte> card, List<Byte> rs) {
		clear();
		byte laiZi = 0;
		byte bomb = -1;
		boolean ok = false;
		for (int i = 0; i < 5; ++i) {
			byte value = getCardValueByCow2(card.get(i));
			if (-1 == value) {
				++laiZi;
				continue;
			}
			++temp.get()[value];
		}
		for (int i = 14; i >= 0; --i) {
			if ((temp.get()[i] + laiZi) >= 4) {
				ok = true;
				bomb = (byte) i;
				break;
			}
		}
		double rsValue = -1;
		if (ok) {
			byte maxBomb = -1;
			for (int i = 0; i < 5; ++i) {
				byte value = getCardValueByCow2(card.get(i));
				if (-1 == value || bomb == value) {
					rs.add(card.get(i));
					maxBomb = card.get(i);
				}
			}
			for (int i = 0; i < 5; ++i) {
				byte value = getCardValueByCow2(card.get(i));
				if (-1 != value && bomb != value) {
					rs.add(card.get(i));
				}
			}
			rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(maxBomb), (byte) (getCardColor(maxBomb) + 1)) * Math.pow(128, 8);
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--五小牛
	 *
	 * @param card
	 * @return
	 */
	public static double isCowFiveSmall(List<Byte> card, List<Byte> rs) {
		byte same = 0;
		boolean ok = true;
		for (int i = 0; i < 5; ++i) {
			byte value = getCardValueByCow2(card.get(i));
			if (value > 5) {
				ok = false;
				break;
			}
			if (-1 == value) {
				value = 1;
			}
			same += value;
			if (same > 10) {
				ok = false;
				break;
			}
		}
		double rsValue = -1;
		if (ok) {
			rs.addAll(card);
			byte value = card.get(4);
			rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(value), (byte) (getCardColor(value) + 1)) * Math.pow(128, 7);
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--葫芦牛
	 *
	 * @param card
	 * @return
	 */
	public static double isCowCucurbit(List<Byte> card, List<Byte> rs) {
		clear();
		byte laiZi = 0;
		boolean ok = false;
		for (int i = 0; i < 5; ++i) {
			byte value = getCardValueByCow2(card.get(i));
			if (-1 == value) {
				++laiZi;
				continue;
			}
			++temp.get()[value];
		}
		byte three = -1;
		byte threeUseLaiZi = 0;
		byte two = -1;
		byte twoUseLaiZi = 0;
		for (int i = 14; i >= 0; --i) {
			if ((temp.get()[i] + laiZi) >= 3) {
				three = (byte) i;
				if (temp.get()[i] < 3) {
					threeUseLaiZi = (byte) (3 - temp.get()[i]);
					laiZi -= threeUseLaiZi;
				}
			} else if (temp.get()[i] + laiZi >= 2) {
				two = (byte) i;
				if (temp.get()[i] < 2) {
					twoUseLaiZi = (byte) (2 - temp.get()[i]);
					laiZi -= twoUseLaiZi;
				}
			}
			if (-1 != three && -1 != two) {
				ok = true;
				break;
			}
		}
		double rsValue = -1;
		if (ok) {
			byte maxThree = -1;
			for (int i = 0; i < 5; ++i) {
				if (i < threeUseLaiZi) {
					rs.add(card.get(i));
				} else {
					byte value = getCardValueByCow2(card.get(i));
					if (-1 != value && three == value) {
						rs.add(card.get(i));
						maxThree = card.get(i);
					}
				}
			}
			for (int i = 0; i < 5; ++i) {
				if (i < threeUseLaiZi) {
					continue;
				}
				byte value = getCardValueByCow2(card.get(i));
				if (three != value) {
					rs.add(card.get(i));
				}
			}
			rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(maxThree), (byte) (getCardColor(maxThree) + 1))
					* Math.pow(128, 6);
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--金牛
	 *
	 * @param card
	 * @return
	 */
	public static double isCowGold(List<Byte> card, List<Byte> rs) {
		boolean ok = true;
		for (int i = 0; i < 5; ++i) {
			byte value = getCardValueByCow2(card.get(i));
			if (-1 == value) {
				continue;
			}
			if (value < 11 || value > 13) {
				ok = false;
				break;
			}
		}
		double rsValue = -1;
		if (ok) {
			rs.addAll(card);
			byte value = card.get(4);
			rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(value), (byte) (getCardColor(value) + 1)) * Math.pow(128, 5);
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--同花牛
	 *
	 * @param card
	 * @return
	 */
	public static double isCowSameColor(List<Byte> card, List<Byte> rs) {
		byte color = -1;
		boolean ok = true;
		for (int i = 0; i < 5; ++i) {
			byte value = getCardValueByCow2(card.get(i));
			if (-1 == value) {
				continue;
			}
			if (-1 == color) {
				color = getCardColor(card.get(i));
			} else if (color != getCardColor(card.get(i))) {
				ok = false;
				break;
			}
		}
		double rsValue = -1;
		if (ok) {
			rs.addAll(card);
			byte value = card.get(4);
			rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(value), (byte) (getCardColor(value) + 1)) * Math.pow(128, 4);
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--银牛
	 *
	 * @param card
	 * @return
	 */
	public static double isCowSilver(List<Byte> card, List<Byte> rs) {
		byte ten = 0;
		byte laiZiCnt = 0;
		boolean ok = true;
		for (int i = 0; i < 5; ++i) {
			byte value = getCardValueByCow2(card.get(i));
			if (-1 == value) {
				++laiZiCnt;
				continue;
			}
			if (value < 10 || value > 13) {
				ok = false;
				break;
			}
			if (10 == value) {
				++ten;
			}
		}
		if (ten + laiZiCnt < 1) {
			ok = false;
		}
		double rsValue = -1;
		if (ok) {
			rs.addAll(card);
			byte value = card.get(4);
			rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(value), (byte) (getCardColor(value) + 1)) * Math.pow(128, 3);
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 牛牛--顺子牛
	 *
	 * @param card
	 * @return
	 */
	public static double isCowStraight(List<Byte> card, List<Byte> rs) {
		byte value = -1;
		byte laiZiCnt = 0;
		byte useLaiZiCnt = 0;
		boolean ok = true;
		for (int i = 0; i < 5; ++i) {
			byte temp = getCardValueByCow2(card.get(i));
			if (-1 == temp) {
				++laiZiCnt;
				continue;
			}
			if (temp == value) {
				ok = false;
				break;
			}
			if (-1 == value || (1 == (value - temp))) {
				value = temp;
				rs.add(card.get(i));
			} else if ((temp - value - 1) > laiZiCnt) {
				ok = false;
				break;
			} else {
				byte tempUseLai = (byte) (temp - value - 1);
				for (int j = 0; j < tempUseLai; ++j) {
					rs.add(card.get(useLaiZiCnt + j));
				}
				rs.add(card.get(i));
				useLaiZiCnt += tempUseLai;
				laiZiCnt -= tempUseLai;
				value = temp;
			}
		}
		double rsValue = -1;
		if (ok) {
			if (laiZiCnt > 0) {
				byte useLaiZi = 0;
				for (int j = 0; j < laiZiCnt; ++j) {
					if (value >= (K + 3)) {
						break;
					}
					++value;
					++useLaiZi;
					rs.add(card.get(useLaiZiCnt + j));
				}
				for (int j = useLaiZi; j < laiZiCnt; ++j) {
					rs.add(0, card.get(useLaiZiCnt + j));
				}
				value = card.get(4);
				rsValue = generateCardValueByCow2WithColor((byte) (getCardValueByCow2(value) + useLaiZi),
						useLaiZi > 0 ? (byte) 0 : (byte) (getCardColor(value) + 1)) * Math.pow(128, 2);
			} else {
				value = card.get(4);
				rsValue = generateCardValueByCow2WithColor(getCardValueByCow2(value), (byte) (getCardColor(value) + 1)) * Math.pow(128, 2);
			}
		}
		if (!ok) {
			rs.clear();
		}
		return rsValue;
	}

	/**
	 * 扎金花--是否是三张
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean isFriedGoldenFlowerThree(byte a, byte b, byte c) {
		byte a1 = PokerUtil.getCardValueByFriedGoldenFlower(a);
		byte b1 = PokerUtil.getCardValueByFriedGoldenFlower(b);
		byte c1 = PokerUtil.getCardValueByFriedGoldenFlower(c);
		return a1 != b1 || a1 != c1 || b1 != c1 ? false : true;
	}

	/**
	 * 扎金花--是否是同花顺
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean isFriedGoldenFlowerSameColorLine(byte a, byte b, byte c) {
		if (!isFriedGoldenFlowerSameColor(a, b, c)) {
			return false;
		}
		return isFriedGoldenFlowerLine(a, b, c);
	}

	/**
	 * 炸金话--是否是同花
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean isFriedGoldenFlowerSameColor(byte a, byte b, byte c) {
		byte a2 = PokerUtil.getCardColor(a);
		byte b2 = PokerUtil.getCardColor(b);
		byte c2 = PokerUtil.getCardColor(c);
		if (a2 != b2 || a2 != c2 || b2 != c2) {
			return false;
		}
		return true;
	}

	/**
	 * 炸金话--是否是顺子
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean isFriedGoldenFlowerLine(byte a, byte b, byte c) {
		byte a1 = PokerUtil.getCardValueByFriedGoldenFlower(a);
		byte b1 = PokerUtil.getCardValueByFriedGoldenFlower(b);
		byte c1 = PokerUtil.getCardValueByFriedGoldenFlower(c);
		byte temp;
		if (b1 > a1) {
			temp = a1;
			a1 = b1;
			b1 = temp;
		}
		if (c1 > a1) {
			temp = c1;
			c1 = b1;
			b1 = a1;
			a1 = temp;
		} else if (c1 > b1) {
			temp = c1;
			c1 = b1;
			b1 = temp;
		}
		// 判断特殊情况:A 2 3
		if (14 == a1 && 3 == b1 && 2 == c1) {
			return true;
		}

		if ((a1 - b1) != 1 || (b1 - c1) != 1) {
			return false;
		}
		return true;
	}

	/**
	 * 扎金花--是否是对子
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean isFriedGoldenFlowerDouble(byte a, byte b, byte c) {
		byte a1 = PokerUtil.getCardValueByFriedGoldenFlower(a);
		byte b1 = PokerUtil.getCardValueByFriedGoldenFlower(b);
		byte c1 = PokerUtil.getCardValueByFriedGoldenFlower(c);
		byte temp;
		if (b1 > a1) {
			temp = a1;
			a1 = b1;
			b1 = temp;
		}
		if (c1 > a1) {
			temp = c1;
			c1 = b1;
			b1 = a1;
			a1 = temp;
		} else if (c1 > b1) {
			temp = c1;
			c1 = b1;
			b1 = temp;
		}
		if (a1 == b1 || b1 == c1) {
			return true;
		}
		return false;
	}

	/**
	 * 扎金花--高牌235
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean isFriedGoldenFlower235(byte a, byte b, byte c) {
		byte a1 = PokerUtil.getCardValueByFriedGoldenFlower(a);
		byte b1 = PokerUtil.getCardValueByFriedGoldenFlower(b);
		byte c1 = PokerUtil.getCardValueByFriedGoldenFlower(c);
		byte temp;
		if (b1 > a1) {
			temp = a1;
			a1 = b1;
			b1 = temp;
		}
		if (c1 > a1) {
			temp = c1;
			c1 = b1;
			b1 = a1;
			a1 = temp;
		} else if (c1 > b1) {
			temp = c1;
			c1 = b1;
			b1 = temp;
		}
		return 5 == a1 && 3 == b1 && 2 == c1;
	}

	/**
	 * 获取炸金花绝对牌值
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @param type
	 * @param useColor
	 * @return
	 */
	public static long getFriedGoldenFlowerValue(byte a, byte b, byte c, EPokerCardType type, boolean useColor) {
		byte a1 = PokerUtil.getCardValueByFriedGoldenFlower(a);
		byte b1 = PokerUtil.getCardValueByFriedGoldenFlower(b);
		byte c1 = PokerUtil.getCardValueByFriedGoldenFlower(c);
		byte temp1;
		byte temp2;
		if (b1 > a1 || (b1 == a1 && b > a)) {
			temp1 = a1;
			temp2 = a;
			a1 = b1;
			a = b;
			b1 = temp1;
			b = temp2;
		}
		if (c1 > a1 || (c1 == a1 && c > a)) {
			temp1 = c1;
			temp2 = c;
			c1 = b1;
			c = b;
			b1 = a1;
			b = a;
			a1 = temp1;
			a = temp2;
		} else if (c1 > b1 || (c1 == b1 && c > b)) {
			temp1 = c1;
			temp2 = c;
			c1 = b1;
			c = b;
			b1 = temp1;
			b = temp2;
		}
		if (EPokerCardType.FGF_DOUBLE == type) {
			if (a1 != b1) {
				c1 = a1;
				a1 = b1;
			}
		}
		byte a2 = useColor ? PokerUtil.getCardColor(a) : 0;
		byte b2 = useColor ? PokerUtil.getCardColor(b) : 0;
		byte c2 = useColor ? PokerUtil.getCardColor(c) : 0;
		long a3 = (a1 << 2) | a2;
		long b3 = (b1 << 2) | b2;
		long c3 = (c1 << 2) | c2;
		long value = 0;
		if (EPokerCardType.FGF_NONE == type || EPokerCardType.FGF_235 == type) {
			value = a3 * 64 * 64 + b3 * 64 + c3;
		} else if (EPokerCardType.FGF_DOUBLE == type) {
			value = a3 * 64 * 64 * 64 + b3 * 64 * 64 + c3 * 64;
		} else if (EPokerCardType.FGF_LINE == type) {
			value = a3 * 64 * 64 * 64 * 64 + b3 * 64 * 64 * 64 + c3 * 64 * 64;
		} else if (EPokerCardType.FGF_SAME_COLOR == type) {
			value = a3 * 64 * 64 * 64 * 64 * 64 + b3 * 64 * 64 * 64 * 64 + c3 * 64 * 64 * 64;
		} else if (EPokerCardType.FGF_SAME_COLOR_AND_LINE == type) {
			value = a3 * 64 * 64 * 64 * 64 * 64 * 64 + b3 * 64 * 64 * 64 * 64 * 64 + c3 * 64 * 64 * 64 * 64;
		} else if (EPokerCardType.FGF_THREE == type) {
			value = a3 * 64 * 64 * 64 * 64 * 64 * 64 * 64 + b3 * 64 * 64 * 64 * 64 * 64 * 64 + c3 * 64 * 64 * 64 * 64 * 64;
		}
		return value;
	}

	/**
	 * 十三水--判断是否是至尊清龙
	 *
	 * @param
	 * @return
	 */
	public static boolean isThirteenZhiZunQingLong(List<Byte> cardList) {
		if (cardList.size() != 13) {
			return false;
		}

		int color = getCardColor(cardList.get(0));
		for (Byte c : cardList) {
			if (color != getCardColor(c)) {
				return false;
			}
		}

		byte cur = cardList.get(0);
		for (int i = 1; i < cardList.size(); i++) {
			if (cur != cardList.get(i) - 1) {
				return false;
			} else {
				cur = cardList.get(i);
			}
		}

		return true;
	}

	/**
	 * 十三水--判断是否是八仙过海
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenBaXianGuoHai(List<Byte> card) {
		if (13 != card.size()) {
			return false;
		}
		clear();

		for (int i = 0, len = card.size(); i < len; ++i) {
			++temp.get()[getCardValue(card.get(i))];
		}

		for (int i = 0; i < 15; ++i) {
			if (8 == temp.get()[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 十三水--判断是否是七星连珠
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenQiXingLianZhu(List<Byte> card) {
		if (13 != card.size()) {
			return false;
		}
		clear();

		for (int i = 0, len = card.size(); i < len; ++i) {
			++temp.get()[getCardValue(card.get(i))];
		}

		for (int i = 0; i < 15; ++i) {
			if (7 == temp.get()[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 十三水--判断是否是十三水(一条龙)
	 *
	 * @param
	 * @return
	 */
	public static boolean isThirteenShiSanShui(List<Byte> cardList) {
		if (cardList.size() != 13) {
			return false;
		}

		byte cur = getCardValue(cardList.get(0));
		for (int i = 1; i < cardList.size(); i++) {
			if (cur != getCardValue(cardList.get(i)) - 1) {
				return false;
			} else {
				cur = getCardValue(cardList.get(i));
			}
		}

		return true;
	}

	/**
	 * 十三水--判断是否是十二皇族, J, Q, K, A
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenShiErHuangZu(List<Byte> card) {
		if (card.size() != 13) {
			return false;
		}

		for (int i = 0, len = card.size(); i < len; ++i) {
			byte temp = getCardValue(card.get(i));
			if (temp < J || temp > A) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 十三水--判断是否是三同花顺
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenSanTongHuaShun(List<Byte> card) {
		byte[] colorCnt = new byte[4];
		int tenColor = -1;
		int eightColor = -1;
		int fiveColorOne = -1;
		int fiveColorTwo = -1;
		int threeColor = -1;

		for (Byte c : card) {
			++colorCnt[getCardColor(c)];
		}
		for (int i = 0; i < 4; ++i) {
			if (10 == colorCnt[i]) {
				tenColor = i;
			}
			if (8 == colorCnt[i]) {
				eightColor = i;
			}
			if (5 == colorCnt[i]) {
				if (-1 == fiveColorOne) {
					fiveColorOne = i;
				} else {
					fiveColorTwo = i;
				}
			}
			if (3 == colorCnt[i]) {
				threeColor = i;
			}
		}
		if (-1 != fiveColorOne && -1 != threeColor && -1 != fiveColorTwo) {
			List<Byte> fiveOne = new ArrayList<>(5);
			List<Byte> fiveTwo = new ArrayList<>(5);
			List<Byte> three = new ArrayList<>(3);
			for (Byte c : card) {
				if (fiveColorOne == getCardColor(c)) {
					fiveOne.add(c);
				} else if (threeColor == getCardColor(c)) {
					three.add(c);
				} else if (fiveColorTwo == getCardColor(c)) {
					fiveTwo.add(c);
				}
			}
			if (isShunZi(fiveOne) && isShunZi(three) && isShunZi(fiveTwo)) {
				return true;
			}
		}
		if (-1 != eightColor && -1 != fiveColorOne) {
			List<Byte> fiveOne = new ArrayList<>(5);
			List<Byte> eight = new ArrayList<>(8);
			for (Byte c : card) {
				if (fiveColorOne == getCardColor(c)) {
					fiveOne.add(c);
				} else if (eightColor == getCardColor(c)) {
					eight.add(c);
				}
			}
			if (isShunZi(fiveOne) && isShunZi(eight.subList(0, 2)) && isShunZi(eight.subList(3, 7))) {
				return true;
			}
			if (isShunZi(fiveOne) && isShunZi(eight.subList(0, 4)) && isShunZi(eight.subList(5, 7))) {
				return true;
			}
		}
		if (-1 != tenColor && -1 != threeColor) {
			List<Byte> ten = new ArrayList<>(10);
			List<Byte> three = new ArrayList<>(3);
			for (Byte c : card) {
				if (tenColor == getCardColor(c)) {
					ten.add(c);
				} else if (threeColor == getCardColor(c)) {
					three.add(c);
				}
			}
			if (isShunZi(three) && isShunZi(ten.subList(0, 4)) && isShunZi(ten.subList(5, 9))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 十三水--判断是否是三套炸弹
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenSanTaoZhaDan(List<Byte> card) {
		if (13 != card.size()) {
			return false;
		}
		int fourCnt = 0;
		clear();

		for (int i = 0, len = card.size(); i < len; ++i) {
			++temp.get()[getCardValue(card.get(i))];
		}

		for (int i = 0; i < 15; ++i) {
			if (temp.get()[i] >= 4) {
				++fourCnt;
			}
		}
		return 3 == fourCnt;
	}

	/**
	 * 十三水--判断是否是凑一色
	 *
	 * @param card
	 * @return
	 */
	public static int isCouYiSe(List<Byte> card) {
		if (13 != card.size()) {
			return -1;
		}
		clear();
		byte color = getCardColor(card.get(0));

		for (int i = 1, len = card.size(); i < len; ++i) {
			if (color != getCardColor(card.get(i))) {
				return -1;
			}
		}
		return 0;
	}

	/**
	 * 十三水--判断是否是全大, 8, 9, 10, J, Q, K , A
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenQuanDa(List<Byte> card) {
		if (card.size() != 13) {
			return false;
		}

		for (int i = 0, len = card.size(); i < len; ++i) {
			byte temp = getCardValue(card.get(i));
			if (temp < EIGHT || temp > A) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 十三水--判断是否是全小 2, 3, 4, 5, 6, 7, 8
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenQuanXiao(List<Byte> card) {
		if (card.size() != 13) {
			return false;
		}
		for (int i = 0, len = card.size(); i < len; ++i) {
			byte temp = getCardValue(card.get(i));
			if (temp == TWO) {
				continue;
			}
			if (temp > EIGHT) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 十三水--判断是否是全红, 全方块红桃
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenQuanHong(List<Byte> card) {
		if (card.size() != 13) {
			return false;
		}
		for (int i = 0, len = card.size(); i < len; ++i) {
			byte color = getCardColor(card.get(i));
			if (color == 1 || color == 3) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 十三水--判断是否是全黑, 全黑桃梅花
	 * 
	 * @param card
	 *
	 * @return
	 */
	public static boolean isThirteenQuanHei(List<Byte> card) {
		if (card.size() != 13) {
			return false;
		}

		for (int i = 0, len = card.size(); i < len; ++i) {
			byte color = getCardColor(card.get(i));
			if (color == 0 || color == 2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 十三水--判断是否是中原一点红, 全黑桃梅花中加入一个红桃方块
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenZhongYuanYiDianHong(List<Byte> card) {
		if (card.size() != 13) {
			return false;
		}

		int blackCnt = 0;
		int redCnt = 0;

		for (int i = 0, len = card.size(); i < len; ++i) {
			byte color = getCardColor(card.get(i));
			if (color == 0 || color == 2) {
				++redCnt;
			}
			if (color == 1 || color == 3) {
				++blackCnt;
			}
		}

		return redCnt == 1 && blackCnt == 12;
	}

	/**
	 * 十三水--判断是否是中原一点黑, 全红桃方块中加入一个黑桃梅花
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenZhongYuanYiDianHei(List<Byte> card) {
		if (card.size() != 13) {
			return false;
		}

		int blackCnt = 0;
		int redCnt = 0;

		for (int i = 0, len = card.size(); i < len; ++i) {
			byte color = getCardColor(card.get(i));
			if (color == 0 || color == 2) {
				++redCnt;
			}
			if (color == 1 || color == 3) {
				++blackCnt;
			}
		}

		return redCnt == 12 && blackCnt == 1;
	}

	/**
	 * 十三水--判断是否是四套三条, 四个三张相同余一张
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenSiTaoSanTiao(List<Byte> card) {
		if (13 != card.size()) {
			return false;
		}
		int threeCnt = 0;
		clear();

		for (int i = 0, len = card.size(); i < len; ++i) {
			++temp.get()[getCardValue(card.get(i))];
		}

		for (int i = 0; i < 15; ++i) {
			if (temp.get()[i] >= 3) {
				++threeCnt;
				if (temp.get()[i] >= 6){
					++threeCnt;
				}
			}
		}
		return 4 == threeCnt;
	}

	/**
	 * 十三水--判断是否是五对三条, 五个对子，其余三张相同
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenWuDuiSanTiao(List<Byte> card) {
		if (13 != card.size()) {
			return false;
		}
		int doubleCnt = 0;
		int threeCnt = 0;
		clear();

		for (int i = 0, len = card.size(); i < len; ++i) {
			++temp.get()[getCardValue(card.get(i))];
		}

		int fiveNum = 0;
		for (int i = 0; i < 15; ++i) {
			if (2 == temp.get()[i]) {
				++doubleCnt;
			} else if (3 == temp.get()[i]) {
				++threeCnt;
			} else if (4 == temp.get()[i]) {
				doubleCnt += 2;
			} else if (6 == temp.get()[i]){
				doubleCnt += 3;
			} else if (5 == temp.get()[i]){
				doubleCnt += 1;
				threeCnt += 1;
			}
		}
		return (5 == doubleCnt) && (1 == threeCnt);
	}

	/**
	 * 十三水--判断是否是六对半, 六个对子余一张
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenLiuDuiBan(List<Byte> card) {
		if (13 != card.size()) {
			return false;
		}
		int doubleCnt = 0;
		clear();

		for (int i = 0, len = card.size(); i < len; ++i) {
			++temp.get()[getCardValue(card.get(i))];
		}

		for (int i = 0; i < 15; ++i) {
			if (temp.get()[i] >= 2) {
				++doubleCnt;
				if (temp.get()[i] >= 4) {
					++doubleCnt;
					if (temp.get()[i] == 6){
						++doubleCnt;
					}
				}
			}
		}
		return 6 == doubleCnt;
	}

	/**
	 * 十三水--判断是否是三同花
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenSanTongHua(List<Byte> card) {
		clear();
		int tenNum = 0;
		int eightNum = 0;
		int fiveNum = 0;
		int threeNum = 0;

		for (Byte c : card) {
			++temp.get()[getCardColor(c)];
		}
		for (int i = 0; i < 4; ++i) {
			if (10 == temp.get()[i]) {
				tenNum++;
			} else if (8 == temp.get()[i]) {
				eightNum++;
			} else if (5 == temp.get()[i]) {
				fiveNum++;
			} else if (3 == temp.get()[i]) {
				threeNum++;
			}
		}
		return (1 == tenNum && 1 == threeNum) || (1 == eightNum && 1 == fiveNum) || (2 == fiveNum && 1 == threeNum);
	}

	/**
	 * 十三水--判断是否是順子
	 *
	 * @param card
	 * @return
	 */
	public static boolean isShunZi(List<Byte> card) {
		if (card.isEmpty()) {
			return false;
		}
		clear();
		int laiZiCnt = 0;
		for (Byte c : card) {
			byte v = getCardValueByFriedGoldenFlower(c);
			if (-1 == v) {
				++laiZiCnt;
			} else {
				++temp.get()[v];
			}
		}
		boolean flg = true;
		int begin = -1;
		int end = -1;
		for (int i = 2; i < 15; ++i) {
			if (temp.get()[i] != 1) {
				if (-1 != begin) {
					if (-1 == end) {
						end = i;
					}
				}
				continue;
			}
			if (-1 != end) {
				flg = false;
				break;
			}
			if (-1 == begin) {
				begin = i;
			}
		}
		if (flg) {
			return true;
		}
		if (3 == card.size()) {
			// A, 2, 3
			int i1 = 14;
			int i2 = 2;
			int i3 = 3;
			return 1 == temp.get()[i1] && 1 == temp.get()[i2] && 1 == temp.get()[i3];
		} else if (5 == card.size()) {
			// A, 2, 3, 4, 5
			int i1 = 14;
			int i2 = 2;
			int i3 = 3;
			int i4 = 4;
			int i5 = 5;
			return 1 == temp.get()[i1] && 1 == temp.get()[i2] && 1 == temp.get()[i3] && 1 == temp.get()[i4] && 1 == temp.get()[i5];
		}
		return false;
	}

	/**
	 * 十三水--判断是否是三順子
	 *
	 * @param card
	 * @return
	 */
	public static boolean isThirteenSanShunZi(List<Byte> card) {
		clear();
		int laiZiCnt = 0;
		for (Byte c : card) {
			byte v = getCardValueByFriedGoldenFlower(c);
			if (-1 == v) {
				++laiZiCnt;
			} else {
				++temp.get()[v];
			}
		}
		for (int i = 2; i < 13; ++i) {
			int i1 = i;
			int i2 = i + 1;
			int i3 = i + 2;
			if (temp.get()[i1] > 0 && temp.get()[i2] > 0 && temp.get()[i3] > 0) {
				--temp.get()[i1];
				--temp.get()[i2];
				--temp.get()[i3];
				if (isThirteenSanShunZi0(temp.get(), 0)) {
					return true;
				}
				++temp.get()[i1];
				++temp.get()[i2];
				++temp.get()[i3];
			}
		}
		// A, 2, 3
		int i1 = 14;
		int i2 = 2;
		int i3 = 3;
		if (temp.get()[i1] > 0 && temp.get()[i2] > 0 && temp.get()[i3] > 0) {
			--temp.get()[i1];
			--temp.get()[i2];
			--temp.get()[i3];
			if (isThirteenSanShunZi0(temp.get(), 0)) {
				return true;
			}
			++temp.get()[i1];
			++temp.get()[i2];
			++temp.get()[i3];
		}
		return false;
	}

	private static boolean isThirteenSanShunZi0(byte[] card, int dep) {
		if (dep >= 2) {
			return true;
		}
		for (int i = 2; i < 11; ++i) {
			int i1 = i;
			int i2 = i + 1;
			int i3 = i + 2;
			int i4 = i + 3;
			int i5 = i + 4;
			if (card[i1] > 0 && card[i2] > 0 && card[i3] > 0 && card[i4] > 0 && card[i5] > 0) {
				--card[i1];
				--card[i2];
				--card[i3];
				--card[i4];
				--card[i5];
				if (isThirteenSanShunZi0(card, dep + 1)) {
					return true;
				}
				++card[i1];
				++card[i2];
				++card[i3];
				++card[i4];
				++card[i5];
			}
		}
		// A, 2, 3, 4, 5
		int i1 = 14;
		int i2 = 2;
		int i3 = 3;
		int i4 = 4;
		int i5 = 5;
		if (card[i1] > 0 && card[i2] > 0 && card[i3] > 0 && card[i4] > 0 && card[i5] > 0) {
			--card[i1];
			--card[i2];
			--card[i3];
			--card[i4];
			--card[i5];
			if (isThirteenSanShunZi0(card, dep + 1)) {
				return true;
			}
			++card[i1];
			++card[i2];
			++card[i3];
			++card[i4];
			++card[i5];
		}
		return false;
	}

	/**
	 * 十三水--获取判断十三水普通牌型
	 *
	 * @param
	 * @return
	 */
	public static EPokerCardType getThirteenNormalType(List<Byte> cards) {
		EPokerCardType type = EPokerCardType.NONE;
		byte[] cardCnt = new byte[15];
		byte[] colorCnt = new byte[4];
		int fiveNum = 0;
		int fourNum = 0;
		int threeNum = 0;
		int twoNum = 0;
		int oneNum = 0;
		int fiveColor = 0;

		for (int i = 0; i < 15; ++i) {
			cardCnt[i] = 0;
		}
		for (Byte c : cards) {
			++cardCnt[getCardValue(c)];
			++colorCnt[getCardColor(c)];
		}
		for (Byte c : cards) {
			// num
			if (cardCnt[getCardValue(c)] == 1) {
				oneNum++;
			}
			if (cardCnt[getCardValue(c)] == 2) {
				twoNum++;
			}
			if (cardCnt[getCardValue(c)] == 3) {
				threeNum++;
			}
			if (cardCnt[getCardValue(c)] == 4) {
				fourNum++;
			}
			if (cardCnt[getCardValue(c)] == 5) {
				fiveNum++;
			}
			// color
			if (colorCnt[getCardColor(c)] == 5) {
				fiveColor++;
			}
		}

		PokerUtil.sort(cards);
		do {
			// 五条
			if (5 == cards.size() && 5 == fiveNum) {
				type = EPokerCardType.WU_TIAO;
				break;
			}

			// 同花顺
			if (5 == cards.size() && 5 == fiveColor) {
				List<Byte> card = new ArrayList<>();
				for (int i = 0; i < cards.size(); i++) {
					byte cardValue = getCardValue(cards.get(i));
					card.add(cardValue);
				}
				sort(card);
				if (0 == card.get(0) && 1 == card.get(1) && 2 == card.get(2) && 11 == card.get(3) && 12 == card.get(4)) {
					// A 2 3 4 5 特殊处理
					type = EPokerCardType.TONG_HUA_SHUN;
					break;
				}
				if (0 == card.get(0) && 1 == card.get(1) && 2 == card.get(2) && 3 == card.get(3) && 12 == card.get(4)) {
					// 2 3 4 5 6 特殊处理
					type = EPokerCardType.TONG_HUA_SHUN;
					break;
				}

				boolean tag = true;
				byte c1 = card.get(0);
				for (int i = 1; i < card.size(); i++) {
					byte c2 = card.get(i);
					if (c2 - 1 == c1) {
						c1 = c2;
					} else {
						tag = false;
					}
				}
				if (8 == card.get(0) && 9 == card.get(1) && 10 == card.get(2) && 11 == card.get(3) && 12 == card.get(4)) {
					// J Q K A 2 排除
					tag = false;
				}
				if (tag) {
					type = EPokerCardType.TONG_HUA_SHUN;
					break;
				}
			}

			// 四条
			if (5 == cards.size() && 4 == fourNum && 1 == oneNum) {
				type = EPokerCardType.SI_TIAO;
				break;
			}

			// 葫芦
			if (5 == cards.size() && 3 == threeNum && 2 == twoNum) {
				type = EPokerCardType.HU_LU;
				break;
			}

			// 同花
			if (5 == cards.size() && 5 == fiveColor) {
				type = EPokerCardType.TONG_HUA;
				break;
			}

			// 顺子
			if (5 == cards.size() && 5 != fiveColor && 5 == oneNum) {
				List<Byte> card = new ArrayList<>();
				for (int i = 0; i < cards.size(); i++) {
					byte cardValue = getCardValue(cards.get(i));
					card.add(cardValue);
				}
				sort(card);
				if (0 == card.get(0) && 1 == card.get(1) && 2 == card.get(2) && 11 == card.get(3) && 12 == card.get(4)) {
					// A 2 3 4 5 特殊处理
					type = EPokerCardType.SHUN_ZI;
					break;
				}
				if (0 == card.get(0) && 1 == card.get(1) && 2 == card.get(2) && 3 == card.get(3) && 12 == card.get(4)) {
					// 2 3 4 5 6 特殊处理
					type = EPokerCardType.SHUN_ZI;
					break;
				}
				boolean tag = true;
				byte c1 = card.get(0);
				for (int i = 1; i < card.size(); i++) {
					byte c2 = card.get(i);
					if (c2 - 1 == c1) {
						c1 = c2;
					} else {
						tag = false;
					}
				}
				if (8 == card.get(0) && 9 == card.get(1) && 10 == card.get(2) && 11 == card.get(3) && 12 == card.get(4)) {
					// J Q K A 2 排除
					tag = false;
				}
				if (tag) {
					type = EPokerCardType.SHUN_ZI;
					break;
				}
			}

			// 三条
			if ((5 == cards.size() && 3 == threeNum && 2 == oneNum) || (3 == cards.size() && 3 == threeNum)) {
				type = EPokerCardType.SAN_TIAO;
				break;
			}

			// 2对
			if (5 == cards.size() && 4 == twoNum && 1 == oneNum) {
				type = EPokerCardType.ER_DUI;
				break;
			}

			// 对子
			if ((5 == cards.size() && 3 == oneNum && 2 == twoNum) || (3 == cards.size() && 2 == twoNum && 1 == oneNum)) {
				type = EPokerCardType.DUI_ZI;
				break;
			}

			// 杂牌
			if ((5 == cards.size() && 5 == oneNum) || (3 == cards.size() && 3 == oneNum)) {
				type = EPokerCardType.ZA_PAI;
				break;
			}
		} while (false);

		return type;
	}

	/**
	 * 十三水-- 从大到小排序
	 *
	 * @param
	 * @return
	 */
	public static void sortByMaxToMin(List<Byte> cardList) {
		cardList.sort(new Comparator<Byte>() {
			@Override
			public int compare(Byte o1, Byte o2) {
				byte c1 = getCardValue(o1);
				byte c2 = getCardValue(o2);
				if (c1 == c2) {
					return getCardColor(o2) - getCardColor(o1);
				}
				return c2 - c1;
			}
		});
	}

	public static void removeSubList(List<Byte> cardList, List<Byte> subList) {
		for (Byte c : subList) {
			cardList.remove(c);
		}
	}

	public static void verificationStraightFlush(List<TypeCard> typeCardList, List<Byte> cardList, EPokerCardType cardType, int a, int b,
                                                 int c, int d, int e) {
		if (cardList.contains((byte) a) && cardList.contains((byte) b) && cardList.contains((byte) c) && cardList.contains((byte) d)
				&& cardList.contains((byte) e)) {
			List<Byte> tempList = new ArrayList<>();
			tempList.add((byte) a);
			tempList.add((byte) b);
			tempList.add((byte) c);
			tempList.add((byte) d);
			tempList.add((byte) e);
			TypeCard typeCard = new TypeCard();
			typeCard.cardList.addAll(tempList);
			typeCard.cardType = cardType;
			typeCardList.add(typeCard);
		}
	}

	public static List<TypeCard> getMaxCardType(List<Byte> cardList) {
		List<TypeCard> typeCardList = new ArrayList<>();
		byte[] cardCnt = new byte[15];
		byte[] colorCnt = new byte[4];
		List<Byte> fiveNum = new ArrayList<>();
		List<Byte> fourNum = new ArrayList<>();
		List<Byte> threeNum = new ArrayList<>();
		List<Byte> twoNum = new ArrayList<>();
		List<Byte> fiveColor = new ArrayList<>();

		sortByMaxToMin(cardList);// 从大到小排序

		for (Byte c : cardList) {
			++cardCnt[getCardValue(c)];
			++colorCnt[getCardColor(c)];
		}

		for (Byte i = 14; i >= 0; --i) {
			// num
			if (cardCnt[i] >= 2) {
				twoNum.add(i);
			}
			if (cardCnt[i] >= 3) {
				threeNum.add(i);
			}
			if (cardCnt[i] >= 4) {
				fourNum.add(i);
			}
			if (cardCnt[i] >= 5) {
				fiveNum.add(i);
			}
		}

		for (Byte i = 3; i >= 0; --i) {
			if (colorCnt[i] >= 5) {
				fiveColor.add(i);
			}
		}

		// 5条
		for (Byte five : fiveNum) {
			List<Byte> tempList = new ArrayList<>();
			for (Byte c : cardList) {
				if (five == getCardValue(c)) {
					tempList.add(c);
				}
				if (tempList.size() >= 5) {
					TypeCard typeCard = new TypeCard();
					typeCard.cardList.addAll(tempList);
					typeCard.cardType = EPokerCardType.WU_TIAO;
					typeCardList.add(typeCard);
					break;
				}
			}
		}

		// 同花顺
		// 特殊处理
		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 11, 12, 0, 1, 2);
		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 3, 12, 0, 1, 2);

		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 24, 25, 13, 14, 15);
		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 25, 13, 14, 15, 16);

		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 37, 38, 26, 27, 28);
		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 38, 26, 27, 28, 29);

		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 50, 51, 39, 40, 41);
		verificationStraightFlush(typeCardList, cardList, EPokerCardType.TONG_HUA_SHUN, 51, 39, 40, 41, 42);
		for (Byte c : cardList) {
			List<Byte> tempList = new ArrayList<>();
			tempList.add(c);
			for (Byte c2 : cardList) {
				if (c2 - 1 == tempList.get(tempList.size() - 1)) {
					tempList.add(c2);
				}

				if (tempList.size() >= 5) {
					TypeCard typeCard = new TypeCard();
					typeCard.cardList.addAll(tempList);
					typeCard.cardType = EPokerCardType.TONG_HUA_SHUN;
					typeCardList.add(typeCard);
					break;
				}
			}
		}

		// 铁支
		for (Byte four : fourNum) {
			List<Byte> tempList = new ArrayList<>();
			for (Byte c : cardList) {
				if (four == getCardValue(c)) {
					tempList.add(c);
				}
				if (tempList.size() >= 4) {
					TypeCard typeCard = new TypeCard();
					typeCard.cardList.addAll(tempList);
					typeCard.cardType = EPokerCardType.SI_TIAO;
					typeCardList.add(typeCard);
					break;
				}
			}
		}

		// 葫芦
		for (Byte three : threeNum) {
			List<Byte> tempList = new ArrayList<>();
			for (Byte c : cardList) {
				if (three == getCardValue(c)) {
					tempList.add(c);
				}
				if (tempList.size() >= 3) {
					// 找对子
					for (Byte two : twoNum) {
						if (two != three) {
							for (Byte c2 : cardList) {
								if (three != getCardValue(c2) && two == getCardValue(c2)) {
									tempList.add(c2);
								}
								if (tempList.size() >= 5) {
									TypeCard typeCard = new TypeCard();
									typeCard.cardList.addAll(tempList);
									typeCard.cardType = EPokerCardType.HU_LU;
									typeCardList.add(typeCard);
									tempList = getRange(typeCard.cardList, 0, 3);
									break;
								}
							}
						}
					}
					break;
				}
			}
		}

		// 同花
		for (Byte color : fiveColor) {
			List<Byte> tempList = new ArrayList<>();
			for (Byte c : cardList) {
				if (color == getCardColor(c)) {
					tempList.add(0, c);
				}
				while (true) {
					if (tempList.size() >= 5) {
						TypeCard typeCard = new TypeCard();
						typeCard.cardList.addAll(getRange(tempList, 0, 5));
						typeCard.cardType = EPokerCardType.TONG_HUA;
						typeCardList.add(typeCard);
						tempList.remove(0);
					} else {
						break;
					}
				}
			}
		}

		// 顺子
		for (Byte c : cardList) {
			List<Byte> tempList = new ArrayList<>();
			tempList.add(c);
			while (tempList.size() < 5) {
				boolean flag = false;
				for (Byte c2 : cardList) {
					if ((getCardValue(c2) + 1 == getCardValue(tempList.get(tempList.size() - 1))) ||
					// 特殊处理2 A K Q J 10 9 8 7 6 5 4 3
							(12 == getCardValue(tempList.get(tempList.size() - 1)) && 3 == getCardValue(c2))
							|| (11 == getCardValue(tempList.get(tempList.size() - 1)) && 2 == getCardValue(c2))) {
						if (2 == tempList.size() && 10 == getCardValue(c2)) {
							// 排除 2 A K
							continue;
						}
						tempList.add(c2);
						flag = true;
						break;
					}
				}
				if (!flag) {
					break;
				}
			}
			if (tempList.size() >= 5) {
				TypeCard typeCard = new TypeCard();
				typeCard.cardList.addAll(tempList);
				typeCard.cardType = EPokerCardType.SHUN_ZI;
				typeCardList.add(typeCard);
			}
		}

		// 三张
		for (Byte three : threeNum) {
			List<Byte> tempList = new ArrayList<>();
			for (Byte c : cardList) {
				if (three == getCardValue(c)) {
					tempList.add(c);
				}

				if (tempList.size() >= 3) {
					TypeCard typeCard = new TypeCard();
					typeCard.cardList.addAll(tempList);
					typeCard.cardType = EPokerCardType.SAN_TIAO;
					typeCardList.add(typeCard);
					break;
				}
			}
		}

		// 两对
		for (int i = 0, len = twoNum.size(); i < len; i++) {
			List<Byte> tempList = new ArrayList<>();
			for (Byte c : cardList) {
				if (twoNum.get(i) == getCardValue(c)) {
					tempList.add(c);
				}
			}

			while (tempList.size() >= 2) {
				for (int j = i + 1; j < twoNum.size(); j++) {
					List<Byte> tempList2 = new ArrayList<>();
					for (Byte c2 : cardList) {
						if (twoNum.get(j) == getCardValue(c2)) {
							tempList2.add(c2);
						}
					}

					while (tempList2.size() >= 2) {
						TypeCard typeCard = new TypeCard();
						typeCard.cardList.addAll(getRange(tempList, 0, 2));
						typeCard.cardList.addAll(getRange(tempList2, 0, 2));
						typeCard.cardType = EPokerCardType.ER_DUI;
						typeCardList.add(typeCard);
						tempList2.remove(0);
					}
				}
				tempList.remove(0);
			}
		}

		// 对子
		for (Byte two : twoNum) {
			List<Byte> tempList = new ArrayList<>();
			for (Byte c : cardList) {
				if (two == getCardValue(c)) {
					tempList.add(c);
				}
				if (tempList.size() >= 2) {
					TypeCard typeCard = new TypeCard();
					typeCard.cardList.addAll(getRange(tempList, 0, 2));
					typeCard.cardType = EPokerCardType.DUI_ZI;
					typeCardList.add(typeCard);
					tempList.remove(0);
				}
			}
		}

		// 乌龙 杂牌
		do {
			if (cardList.size() < 3) {

			} else {
				List<Byte> tempList = new ArrayList<>();
				tempList.addAll(getRange(cardList, 0, 3));
				TypeCard typeCard = new TypeCard();
				typeCard.cardList.addAll(tempList);
				typeCard.cardType = EPokerCardType.ZA_PAI;
				typeCardList.add(typeCard);
			}
		} while (false);

		return typeCardList;
	}

	public static byte getCardValueByThirteen(byte card) {
		if (KINGLET == card) {
			return -1;
		}
		if (KING == card) {
			return -1;
		}
		byte temp = (byte) ((byte) (card % 13) + 2);
		if (14 == temp) {
			return 1;
		}
		return temp;
	}

	public static void sortByThirteen(List<Byte> cardList) {
		cardList.sort(new Comparator<Byte>() {
			@Override
			public int compare(Byte o1, Byte o2) {
				byte c1 = getCardValueByThirteen(o1);
				byte c2 = getCardValueByThirteen(o2);
				if (c1 == c2) {
					return getCardColor(o1) - getCardColor(o2);
				}
				return c1 - c2;
			}
		});
	}

	public static List<Byte> getRange(List<Byte> card, int begin, int num) {
		List<Byte> tempList = new ArrayList<>();
		for (int i = begin, len = num; i < len; i++) {
			tempList.add(card.get(i));
		}
		return tempList;
	}

	public static int compareThirteenCardList(List<Byte> cards, List<Byte> otherCards, boolean color) {
		if (cards.size() != otherCards.size()) {
			return -1;
		}
		int res = 0;
		int len = cards.size() - 1;
		sortByThirteen(cards);
		sortByThirteen(otherCards);
		for (int i = len; i >= 0; i--) {
			byte c1 = getCardValueByThirteen(cards.get(i));
			byte c2 = getCardValueByThirteen(otherCards.get(i));
			// res = compareThirteenCard(cards.get(i), otherCards.get(i),
			// color);
			res = c1 - c2;
			if (0 != res) {
				return res;
			}
		}
		if (color) {
			return PokerUtil.getCardColor(cards.get(len)) - PokerUtil.getCardColor(otherCards.get(len));
		}
		return res;
	}

	public static int compareThirteenCard(Byte card, Byte otherCard, boolean color) {
		byte c1 = getCardValueByThirteen(card);
		byte c2 = getCardValueByThirteen(otherCard);
		if (color) {
			// 使用花色
			if (c1 == c2) {
				return PokerUtil.getCardColor(card) - PokerUtil.getCardColor(otherCard);
			}
			return c1 - c2;
		} else {
			// 未使用花色
			return c1 - c2;
		}
	}

	public static List<CardModel> getThirteenResult(List<CardModel> cardModelList, List<Byte> cardList, boolean color) {
		CardModel cm = new CardModel();
		List<TypeCard> typeCardList; // = new ArrayList<>();
		List<Byte> newCardList = new ArrayList<>();
		// copy
		newCardList.addAll(cardList);

		if (0 == cardModelList.size()) {
			while (true) {
				typeCardList = getMaxCardType(newCardList);
				// 首道只能有三张
				if (2 == cm.getTypeCardList().size() && typeCardList.get(0).getCardList().size() > 3) {
					typeCardList = getMaxCardType(getRange(typeCardList.get(0).getCardList(), 0, 3));
				}
				cm.typeCardList.add(typeCardList.get(0));
				removeSubList(newCardList, typeCardList.get(0).getCardList());

				// 补全少的牌
				if (cm.typeCardList.size() >= 3) {
					for (int i = 0, len = cm.typeCardList.size(); i < len; i++) {
						if (2 == i) {
							while (cm.typeCardList.get(i).getCardList().size() < 3) {
								cm.typeCardList.get(i).getCardList().add(newCardList.get(0));
								newCardList.remove(0);
							}
						} else {
							while (cm.typeCardList.get(i).getCardList().size() < 5) {
								cm.typeCardList.get(i).getCardList().add(newCardList.get(0));
								newCardList.remove(0);
							}
						}
					}
					break;
				}
			}

			cardModelList.add(cm);
			getThirteenResult(cardModelList, cardList, color);
		}

		// 其他
		if (cardModelList.size() < 4) {
			int singleCount = 0;// 乌龙次数
			while (true) {
				typeCardList = getMaxCardType(newCardList);
				int index = cardModelList.size();
				int typeCardListIndex = 0;

				if (3 == cm.getTypeCardList().size()) {

				} else if (0 == cm.getTypeCardList().size()) {
					if (typeCardList.size() < index) {
						return cardModelList;
					}
					cm.getTypeCardList().add(typeCardList.get(index));
					removeSubList(newCardList, typeCardList.get(index).getCardList());
				} else {
					// 前道不能大于后道
					while (true) {
						if (typeCardList.get(typeCardListIndex).getCardType().getValue() > cm.getTypeCardList()
								.get(cm.getTypeCardList().size() - 1).getCardType().getValue()) {
							typeCardListIndex++;
							continue;
						}
						if (typeCardList.get(typeCardListIndex).getCardType().getValue() == cm.getTypeCardList()
								.get(cm.getTypeCardList().size() - 1).getCardType().getValue()) {
							if (compareThirteenCardList(typeCardList.get(typeCardListIndex).getCardList(),
									cm.getTypeCardList().get(cm.getTypeCardList().size() - 1).getCardList(), color) > 0) {
								// if
								// (Collections.max(typeCardList.get(typeCardListIndex).getCardList())
								// >=
								// Collections.max(cm.getTypeCardList().get(cm.getTypeCardList().size()
								// - 1).getCardList())) {
								return cardModelList;
							}
						}
						break;
					}
					// 首道只能有三张牌
					if (2 == cm.getTypeCardList().size() && typeCardList.get(typeCardListIndex).getCardList().size() > 3) {
						typeCardList = getMaxCardType(getRange(typeCardList.get(typeCardListIndex).getCardList(), 0, 3));
						cm.getTypeCardList().add(typeCardList.get(0));
						removeSubList(newCardList, typeCardList.get(0).getCardList());
					} else {
						cm.getTypeCardList().add(typeCardList.get(typeCardListIndex));
						removeSubList(newCardList, typeCardList.get(typeCardListIndex).getCardList());
					}
				}
				// 补全不够牌
				if (cm.getTypeCardList().size() >= 3) {
					for (int i = 0, len = cm.getTypeCardList().size(); i < len; i++) {
						if (2 == i) {
							while (cm.getTypeCardList().get(i).getCardList().size() < 3) {
								cm.getTypeCardList().get(i).getCardList().add(newCardList.get(0));
								newCardList.remove(0);
							}
						} else {
							while (cm.getTypeCardList().get(i).getCardList().size() < 5) {
								//
								cm.getTypeCardList().get(i).getCardList().add(newCardList.get(0));
								newCardList.remove(0);
							}
						}

						if (cm.getTypeCardList().get(i).getCardType() == EPokerCardType.ZA_PAI) {
							singleCount++;
						}
					}
					break;
				}
			}
			cardModelList.add(cm);

			if (singleCount >= 2) {
				return cardModelList;
			}
			getThirteenResult(cardModelList, cardList, color);
		}
		return cardModelList;
	}

	
	// 检查德州是否同花顺(包含皇家同花顺)
	public static EPokerCardType checkTexasSameColorAndLine(byte colorIndex, List<Byte> sameColorCard, List<Byte> result) {
		if (colorIndex > -1) {// 是否是同花顺
			result.clear();
			// 获取同花最大的牌
			byte maxCard = sameColorCard.get(sameColorCard.size() - 1);
			// 获取长度
			byte len = (byte) (sameColorCard.size() - 1);
			byte temp = maxCard;

			// 牌值
			if (getCardValue(maxCard) == _2) {
				len -= 1;// 把2排除掉
				temp = sameColorCard.get(len);
			}
			for (byte i = len; i >= 0;) {
				// 当前值不是最大值(排除掉最后一个)
				if (getCardValue(temp) != getCardValue(sameColorCard.get(i))) {
					// 后面一个和前面一个是连着的(判断顺子)
					if (getCardValue(temp) - getCardValue(sameColorCard.get(i)) == 1) {
						if (result.size() == 0)
							result.add(temp);
						result.add(sameColorCard.get(i));
					} else {
						result.clear();
					}
					temp = sameColorCard.get(i);
				}
				--i;
				if (result.size() == 5)
					break;
			}
			// 2种顺子特殊牌型
			List<Byte> special1 = new ArrayList<>();
			List<Byte> special2 = new ArrayList<>();
			// 特殊牌型 A2345
			for (byte spCard : sameColorCard) {
				byte tm = getCardValue(spCard);
				boolean isExists = false;
				// 是否有重复牌
				for (byte s : special1) {
					if (getCardValue(s) == tm) {
						isExists = true;
						break;
					}
				}
				if (isExists)
					continue;
				if (tm == 11 || tm == 12 || tm == 0 || tm == 1 || tm == 2)
					special1.add(spCard);
				if (special1.size() >= 5)
					break;
			}
			if (special1.size() < 5)
				special1.clear();
			// 特殊牌型 23456
			for (byte spCard : sameColorCard) {
				byte tm = getCardValue(spCard);
				boolean isExists = false;
				// 是否有重复牌
				for (byte s : special2) {
					if (getCardValue(s) == tm) {
						isExists = true;
						break;
					}
				}
				if (isExists)
					continue;
				if (tm == 12 || tm == 3 || tm == 0 || tm == 1 || tm == 2)
					special2.add(spCard);
				if (special2.size() >= 5)
					break;
			}
			if (special2.size() < 5)
				special2.clear();

			if (result.size() == 5) {
				sort(result);
				if (getCardValue(result.get(4)) == A) {
					return EPokerCardType.TEXAS_BIG_SAME_COLOR_AND_LINE;
				} else {
//					// a2345第二大
//					if (special1.size()>0) {
//						result.clear();
//						result.addAll(special1);
//					}
					return EPokerCardType.TEXAS_SAME_COLOR_AND_LINE;
				}
			} else {
				result.clear();
				if (special1.size()>0) {
					result.addAll(special1);
					return EPokerCardType.TEXAS_SAME_COLOR_AND_LINE;
				} else if (special2.size()>0) {
					result.addAll(special2);
					return EPokerCardType.TEXAS_SAME_COLOR_AND_LINE;
				}
			}
		}
		return EPokerCardType.NONE;
	}
	// 检查德州四条
	public static EPokerCardType checkTexasSitiao(byte _four, List<Byte> card, List<Byte> result) {
		// 是否有四条；
		if (_four == 1) {
			byte a = getCardValue(four.get()[0]);
			byte twoCard = -1;
			List<byte[]> list = new ArrayList<>();
			for(byte i =0;i<card.size();i++)
			{
				if (getCardValue(card.get(i)) == a) {
					result.add(card.get(i));
				} else
				list.add(new byte[] {getCardValue(card.get(i)),i});
			}
			sort1(list);
			for (int i = list.size() - 1; i >= 0; --i) {
				if (list.get(i)[0] != TWO) {
					result.add(card.get(list.get(i)[1]));
				}else
					if(twoCard==-1)
						twoCard=card.get(list.get(i)[1]);
				if (result.size() == 5)
					break;
			}
			if(result.size()==4&&twoCard>=0)
				result.add(twoCard);
			return EPokerCardType.TEXAS_SI_TIAO;
		}
		return EPokerCardType.NONE;
	}

	// 检查德州葫芦
	public static EPokerCardType checkTexasHulu(byte _two, byte _three, List<Byte> card, List<Byte> result) {
		// 葫芦
		if (_three == 2 || (_three == 1 && _two > 0)) {
			// 最大的三条
			byte maxSanTiao = three.get()[_three - 1];
			byte nextSanTiao = -1;
			byte maxDouble = -1;
			if (_three > 1) {
				nextSanTiao = three.get()[0];
			}
			if (_two > 0)
				maxDouble = two.get()[_two - 1];
			byte len = (byte) (card.size() - 1);
			if (maxSanTiao == TWO) {
				len = (byte) (card.size() - 4);
			}
			// 2的数量
			int twoCount = 0;
			// 找出对应的牌填充
			for (byte i = len; i >= 0; --i) {
				byte b = getCardValue(card.get(i));
				// 说明有2个三条
				if (nextSanTiao != -1) {
					if (maxSanTiao == TWO) {
						if (twoCount < 2) {
							result.add(card.get(i));
							++twoCount;
						} else {
							if (b == nextSanTiao) {
								result.add(card.get(i));
							}
						}
					} else {
						if (b == maxSanTiao || b == nextSanTiao) {
							result.add(card.get(i));
						}
					}
				} else {
					if (b == maxSanTiao || b == maxDouble) {
						result.add(card.get(i));
					}
				}
				if (result.size() == 5)
					break;
			}
			return EPokerCardType.TEXAS_HU_LU;
		}
		return EPokerCardType.NONE;
	}
	// 检查德州同花
	public static EPokerCardType checkTexasSameColor(byte colorIndex, List<Byte> sameColorCard, List<Byte> card, List<Byte> result) {
		// 同花
		if (sameColorCard.size() >= 5) {
			byte twoCard = -1;
			byte len = (byte) (sameColorCard.size() - 1);
			for (byte i = len; i >= 0; --i) {
				if (getCardValue(sameColorCard.get(i)) == TWO) {
					twoCard = sameColorCard.get(i);
				} else {
					result.add(sameColorCard.get(i));
				}
				if (result.size() == 5)
					break;
			}
			if (result.size() < 5&&twoCard>=0)
				result.add(twoCard);
			return EPokerCardType.TEXAS_SAME_COLOR;
		}
		return EPokerCardType.NONE;
	}

	// 检查德州顺子
	public static EPokerCardType checkTexasLine(byte _single, byte _two, byte _three, byte _four, List<Byte> card, List<Byte> result) {
		// 没有3条 没有炸弹 允许有一对
		if ((_three == 0 && _four == 0 && (_two == 1 || _two == 0)) || (_three == 1 && _single == 4)) {
			byte maxCard = card.get(card.size() - 1);
			byte len = (byte) (card.size() - 1);
			if (getCardValue(maxCard) == TWO) {
				if( (_two > 0 && two.get()[0] == TWO))
					len -= 2;// 含有2就先把2排除
				else
					len -= 1;// 含有2就先把2排除
			}
			byte temp = card.get(len);
			for (byte i = (byte) len; i >= 0; --i) {
				if (getCardValue(temp) != getCardValue(card.get(i))) {
					if (getCardValue(temp) - getCardValue(card.get(i)) == 1) {
						if (result.size() == 0) {
							result.add(temp);
						}
						result.add(card.get(i));
					} else {
						result.clear();
					}
					temp = card.get(i);
				}
				if (result.size() == 5)
					break;
			}
			// 2种顺子特殊牌型
			List<Byte> special1 = new ArrayList<>();
			List<Byte> special2 = new ArrayList<>();
			// 特殊牌型 A2345
			for (byte spCard : card) {
				byte tm = getCardValue(spCard);
				boolean isExists = false;
				// 是否有重复牌
				for (byte s : special1) {
					if (getCardValue(s) == tm) {
						isExists = true;
						break;
					}
				}
				if (isExists)
					continue;
				if (tm == 11 || tm == 12 || tm == 0 || tm == 1 || tm == 2)
					special1.add(spCard);
				if (special1.size() >= 5)
					break;
			}
			if (special1.size() < 5)
				special1.clear();
			// 特殊牌型 23456
			for (byte spCard : card) {
				byte tm = getCardValue(spCard);
				boolean isExists = false;
				// 是否有重复牌
				for (byte s : special2) {
					if (getCardValue(s) == tm) {
						isExists = true;
						break;
					}
				}
				if (isExists)
					continue;
				if (tm == 12 || tm == 3 || tm == 0 || tm == 1 || tm == 2)
					special2.add(spCard);
				if (special2.size() >= 5)
					break;
			}
			if (special2.size() < 5)
				special2.clear();
			// 表示找到了
			if (result.size() == 5) {
				sort(result);
				if (getCardValue(result.get(4)) == A) {
					return EPokerCardType.TEXAS_LINE;
				} else {
//					// 特殊牌型
//					if (special1.size() > 0) {
//						result.clear();
//						result.addAll(special1);
//					}
					return EPokerCardType.TEXAS_LINE;
				}
			} else {
				result.clear();
				if (special1.size() > 0) {
					result.addAll(special1);
					return EPokerCardType.TEXAS_LINE;
				} else if (special2.size() > 0) {
					result.addAll(special2);
					return EPokerCardType.TEXAS_LINE;
				}
			}
		}
		return EPokerCardType.NONE;
	}

	// 检查德州三条
	public static EPokerCardType checkTexasSanTiao(byte _two, byte _three, List<Byte> card, List<Byte> result) {
		if (_three == 1 && _two == 0) {// 三条
			byte a = getCardValue(three.get()[0]);
			List<byte[]> list = new ArrayList<>();
			for(byte i =0;i<card.size();i++)
			{
				byte v =getCardValue(card.get(i));
				if(v==a)
					result.add(card.get(i));
				else
					list.add(new byte[] {v,i});
			}
			sort1(list);
			byte twoCardValue=-1;
			for (int i = list.size() - 1; i >= 0; --i) {
				if (list.get(i)[0] != TWO) {
					result.add(card.get(list.get(i)[1]));
				}else
					twoCardValue=card.get(list.get(i)[1]);
				if (result.size() == 5)
					break;
			}
			if(result.size()<5&&twoCardValue>=0)
				result.add(twoCardValue);
			return EPokerCardType.TEXAS_SAN_TIAO;
		}
		return EPokerCardType.NONE;
	}
	// 检查德州对子
	public static EPokerCardType checkTexasTwoDouble(byte _two, List<Byte> card, List<Byte> result) {
		if (_two > 1) {// 是否有2对
			byte maxDoubleValue = getCardValue(two.get()[_two - 1]);
			byte nextDoubleValue = getCardValue(two.get()[_two - 2]);
			if (maxDoubleValue == TWO) {
				if (_two == 2) {
					maxDoubleValue = nextDoubleValue;
					nextDoubleValue = getCardValue(two.get()[_two - 1]);
				} else if (_two == 3) {
					maxDoubleValue = getCardValue(two.get()[1]);
					nextDoubleValue = getCardValue(two.get()[0]);
				}
			}
			List<byte[]> list = new ArrayList<>();
			for(byte i =0;i<card.size();i++)
			{
				if (getCardValue(card.get(i)) == maxDoubleValue||getCardValue(card.get(i)) == nextDoubleValue) {
					result.add(card.get(i));
				} else
				list.add(new byte[] {getCardValue(card.get(i)),i});
			}
			sort1(list);
			byte twoCardValue=-1;
			for (int i = list.size() - 1; i >= 0; --i) {
					if (list.get(i)[0] != TWO) {
						result.add(card.get(list.get(i)[1]));
					}else 
						twoCardValue=card.get(list.get(i)[1]);
				if (result.size() == 5)
					break;
			}
			if(result.size()<5&&twoCardValue>=0)
				result.add(twoCardValue);
			return EPokerCardType.TEXAS_TWO_DOUBLE;
		}
		return EPokerCardType.NONE;
	}
	// 检查德州对子
	public static EPokerCardType checkTexasOneDouble(byte _two, List<Byte> card, List<Byte> result) {
		if (_two == 1) {// 是否是1对；
			byte a = getCardValue(two.get()[0]);
			List<byte[]> list = new ArrayList<>();
			for(byte i =0;i<card.size();i++)
			{
				if (getCardValue(card.get(i)) == a) {
					result.add(card.get(i));
				} else
				list.add(new byte[] {getCardValue(card.get(i)),i});
			}
			sort1(list);
			byte twoCardValue=-1;
			for (int i = list.size() - 1; i >= 0; --i) {
					if (list.get(i)[0] != TWO) {
						result.add(card.get(list.get(i)[1]));
					}else
						twoCardValue = card.get(list.get(i)[1]);
				if (result.size() == 5)
					break;
			}
			if(result.size()<5&&twoCardValue>=0)
				result.add(twoCardValue);
			return EPokerCardType.TEXAS_ONE_DOUBLE;
		}
 		return EPokerCardType.NONE;
	}

	// 检查德州高牌
	public static EPokerCardType checkTexasHighCard(List<Byte> card, List<Byte> result) {
		List<byte[]> list = new ArrayList<>();
		for(byte i =0;i<card.size();i++)
		{
			list.add(new byte[] {getCardValue(card.get(i)),i});
		}
		sort1(list);
		byte twoCardValue=-1;
		for (int i = list.size() - 1; i >= 0; --i) {
			if (list.get(i)[0]!= TWO) {
				result.add(card.get(list.get(i)[1]));
			}else
				twoCardValue=card.get(list.get(i)[1]);
			if (result.size() == 5)
				break;
		}
		if(result.size()<5&&twoCardValue>=0)
			result.add(twoCardValue);
		return EPokerCardType.TEXAS_HIGH_CARD;
	}
	// 德州获取最大牌型
	public static EPokerCardType getMaxTexasCardType(List<Byte> card, List<Byte> result) {
		result.clear();
		if (card == null || card.size() < 5 || card.size() > 7) {
			return null;
		}
  		sort(card);
		byte[] colors = new byte[4];
		clear();
		for (Byte b : card) {
			++temp.get()[getCardValue(b)];
			byte color = getCardColor(b); // 0: 方块, 1: 梅花 2: 红桃 3: 黑桃
			++colors[color];
		}
		byte _single = 0;// 单牌
		byte _two = 0;// 对牌
		byte _three = 0;// 三张牌
		byte _four = 0;// 4张牌
		for (byte i = 0; i < 15; ++i) {
			if (1 == temp.get()[i]) {
				single.get()[_single] = i;
				++_single;
			} else if (2 == temp.get()[i]) {
				two.get()[_two] = i;
				++_two;
			} else if (3 == temp.get()[i]) {
				three.get()[_three] = i;
				++_three;
			} else if (4 == temp.get()[i]) {
				four.get()[_four] = i;
				++_four;
			}
		}
		byte colorIndex = -1;// 是否有同花；
		// 4种花色每种有多少个
		for (byte i = 0; i < colors.length; ++i) {
			if (colors[i] >= 5) {
				colorIndex = i;
				break;
			}
		}
		List<Byte> sameColorCard = new ArrayList<>();
		if (colorIndex > -1) {// 是否是同花顺
			// 找出手牌中相同的花色的牌
			for (byte i = 0; i < card.size(); i++) {
				if (getCardColor(card.get(i)) == colorIndex) {
					sameColorCard.add(card.get(i));
				}
			}
		}
		EPokerCardType type = checkTexasSameColorAndLine(colorIndex, sameColorCard, result);
		if (type != EPokerCardType.NONE)
			return type;
		type = checkTexasSitiao(_four, card, result);
		if (type != EPokerCardType.NONE)
			return type;
		type = checkTexasHulu(_two, _three, card, result);
		if (type != EPokerCardType.NONE)
			return type;
		type = checkTexasSameColor(colorIndex, sameColorCard, card, result);
		if (type != EPokerCardType.NONE)
			return type;
		type = checkTexasLine(_single, _two, _three, _four, card, result);
		if (type != EPokerCardType.NONE)
			return type;
		type = checkTexasSanTiao(_two, _three, card, result);
		if (type != EPokerCardType.NONE)
			return type;
		type = checkTexasTwoDouble(_two, card, result);
		if (type != EPokerCardType.NONE)
			return type;
		type = checkTexasOneDouble(_two, card, result);
		if (type != EPokerCardType.NONE)
			return type;
		return checkTexasHighCard(card, result);

	}

	// isRecommend是否推荐牌型(只供十三水推荐牌型使用)
	public static int thirteenRecommend(List<Byte> cards, EPokerCardType cardType, List<Byte> otherCards, EPokerCardType otherCardType,
                                        boolean useColor) {
		int result = 0;
		byte[] cardCnt = new byte[15];
		List<Byte> fourNum = new ArrayList<>();
		List<Byte> threeNum = new ArrayList<>();
		List<Byte> twoNum = new ArrayList<>();
		for (int i = 0; i < 15; ++i) {
			cardCnt[i] = 0;
		}
		for (Byte c : cards) {
			++cardCnt[PokerUtil.getCardValue(c)];
			// num
			if (cardCnt[PokerUtil.getCardValue(c)] >= 2) {
				twoNum.add(PokerUtil.getCardValue(c));
			}
			if (cardCnt[PokerUtil.getCardValue(c)] >= 3) {
				threeNum.add(PokerUtil.getCardValue(c));
			}
			if (cardCnt[PokerUtil.getCardValue(c)] >= 4) {
				fourNum.add(PokerUtil.getCardValue(c));
			}
		}

		byte[] otherCardCnt = new byte[15];
		List<Byte> otherFourNum = new ArrayList<>();
		List<Byte> otherThreeNum = new ArrayList<>();
		List<Byte> otherTwoNum = new ArrayList<>();
		for (int i = 0; i < 15; ++i) {
			otherCardCnt[i] = 0;
		}
		for (Byte c : otherCards) {
			++otherCardCnt[PokerUtil.getCardValue(c)];
			// num
			if (otherCardCnt[PokerUtil.getCardValue(c)] >= 2) {
				otherTwoNum.add(PokerUtil.getCardValue(c));
			}
			if (otherCardCnt[PokerUtil.getCardValue(c)] >= 3) {
				otherThreeNum.add(PokerUtil.getCardValue(c));
			}
			if (otherCardCnt[PokerUtil.getCardValue(c)] >= 4) {
				otherFourNum.add(PokerUtil.getCardValue(c));
			}
		}

		PokerUtil.sortByThirteen(cards);
		PokerUtil.sortByThirteen(otherCards);

		if (cardType != otherCardType) {
			return cardType.getValue() - otherCardType.getValue();
		} else {
			switch (cardType) {
				case ZA_PAI :
					if (otherCards.size() == 3)
						return 1;
					result = compareZAPAI(cards, otherCards, useColor);
					break;
				case DUI_ZI :
					result = compareDUIZI(cards, otherCards, useColor);
					break;
				case ER_DUI :
					result = compareERDUI(cards, twoNum, otherCards, otherTwoNum, useColor);
					break;
				case SAN_TIAO :
					result = compareSANTIAO(cards, threeNum, otherCards, otherThreeNum, useColor);
					break;
				case SHUN_ZI :
					result = compareSHUNZI(cards, otherCards, useColor);
					break;
				case TONG_HUA :
					result = compareTONGHUA(cards, otherCards, useColor);
					break;
				case HU_LU :
					result = compareHULU(cards, threeNum, otherCards, otherThreeNum, useColor);
					break;
				case SI_TIAO :
					result = compareSITIAO(cards, fourNum, otherCards, otherFourNum, useColor);
					break;
				case TONG_HUA_SHUN :
					result = compareTONGHUASHUN(cards, otherCards, useColor);
					break;
				case WU_TIAO :
					result = compareWUTIAO(cards, otherCards, useColor);
					break;
			}
			return result;
		}
	}

	public static int compareNormalCardType(List<Byte> cards, EPokerCardType cardType, List<Byte> otherCards, EPokerCardType otherCardType,
                                            boolean useColor) {
		int result = 0;
		byte[] cardCnt = new byte[15];
		List<Byte> fourNum = new ArrayList<>();
		List<Byte> threeNum = new ArrayList<>();
		List<Byte> twoNum = new ArrayList<>();
		for (int i = 0; i < 15; ++i) {
			cardCnt[i] = 0;
		}
		for (Byte c : cards) {
			++cardCnt[PokerUtil.getCardValue(c)];
			// num
			if (cardCnt[PokerUtil.getCardValue(c)] >= 2) {
				twoNum.add(PokerUtil.getCardValue(c));
			}
			if (cardCnt[PokerUtil.getCardValue(c)] >= 3) {
				threeNum.add(PokerUtil.getCardValue(c));
			}
			if (cardCnt[PokerUtil.getCardValue(c)] >= 4) {
				fourNum.add(PokerUtil.getCardValue(c));
			}
		}

		byte[] otherCardCnt = new byte[15];
		List<Byte> otherFourNum = new ArrayList<>();
		List<Byte> otherThreeNum = new ArrayList<>();
		List<Byte> otherTwoNum = new ArrayList<>();
		for (int i = 0; i < 15; ++i) {
			otherCardCnt[i] = 0;
		}
		for (Byte c : otherCards) {
			++otherCardCnt[PokerUtil.getCardValue(c)];
			// num
			if (otherCardCnt[PokerUtil.getCardValue(c)] >= 2) {
				otherTwoNum.add(PokerUtil.getCardValue(c));
			}
			if (otherCardCnt[PokerUtil.getCardValue(c)] >= 3) {
				otherThreeNum.add(PokerUtil.getCardValue(c));
			}
			if (otherCardCnt[PokerUtil.getCardValue(c)] >= 4) {
				otherFourNum.add(PokerUtil.getCardValue(c));
			}
		}

		PokerUtil.sortByThirteen(cards);
		PokerUtil.sortByThirteen(otherCards);

		if (cardType != otherCardType) {
			return cardType.getValue() - otherCardType.getValue();
		} else {
			switch (cardType) {
				case ZA_PAI :
					result = compareZAPAI(cards, otherCards, useColor);
					break;
				case DUI_ZI :
					result = compareDUIZI(cards, otherCards, useColor);
					break;
				case ER_DUI :
					result = compareERDUI(cards, twoNum, otherCards, otherTwoNum, useColor);
					break;
				case SAN_TIAO :
					result = compareSANTIAO(cards, threeNum, otherCards, otherThreeNum, useColor);
					break;
				case SHUN_ZI :
					result = compareSHUNZI(cards, otherCards, useColor);
					break;
				case TONG_HUA :
					result = compareTONGHUA(cards, otherCards, useColor);
					break;
				case HU_LU :
					result = compareHULU(cards, threeNum, otherCards, otherThreeNum, useColor);
					break;
				case SI_TIAO :
					result = compareSITIAO(cards, fourNum, otherCards, otherFourNum, useColor);
					break;
				case TONG_HUA_SHUN :
					result = compareTONGHUASHUN(cards, otherCards, useColor);
					break;
				case WU_TIAO :
					result = compareWUTIAO(cards, otherCards, useColor);
					break;
			}
			return result;
		}
	}

	public static int compareZAPAI(List<Byte> cards, List<Byte> otherCards, boolean useColor) {
		return PokerUtil.compareThirteenCardList(cards, otherCards, useColor);
	}

	public static int compareSinglePoker(Byte card, Byte otherCard, boolean useColor) {
		return PokerUtil.compareThirteenCard(card, otherCard, useColor);
	}

	public static int compareDUIZI(List<Byte> cards, List<Byte> otherCards, boolean useColor) {
		int res = 0;
		byte[] cardCnt = new byte[15];
		byte[] otherCnt = new byte[15];
		Byte two1 = 0;
		Byte two2 = 0;

		for (int i = 0; i < 15; ++i) {
			cardCnt[i] = 0;
			otherCnt[i] = 0;
		}
		for (Byte c : cards) {
			++cardCnt[PokerUtil.getCardValue(c)];
			if (2 == cardCnt[PokerUtil.getCardValue(c)]) {
				two1 = PokerUtil.getCardValue(c);
			}
		}
		for (Byte c : otherCards) {
			++otherCnt[PokerUtil.getCardValue(c)];
			if (2 == otherCnt[PokerUtil.getCardValue(c)]) {
				two2 = PokerUtil.getCardValue(c);
			}
		}
		res = compareSinglePoker(two1, two2, false);
		if (0 != res) {
			return res;
		} else {
			List<Byte> tempCards = new ArrayList<>();
			List<Byte> tempOtherCards = new ArrayList<>();
			for (Byte c : cards) {
				if (two1 != PokerUtil.getCardValue(c)) {
					tempCards.add(c);
				}
			}
			for (Byte c : otherCards) {
				if (two2 != PokerUtil.getCardValue(c)) {
					tempOtherCards.add(c);
				}
			}
			return compareZAPAI(tempCards, tempOtherCards, useColor);
		}
	}

	public static int compareERDUI(List<Byte> cards, List<Byte> twoNum, List<Byte> otherCards, List<Byte> otherTwoNum, boolean useColor) {
		int res = 0;
		Byte two1 = 0;
		Byte two2 = 0;
		List<Byte> tempCards = new ArrayList<>();
		List<Byte> tempOtherCards = new ArrayList<>();

		for (Byte c : cards) {
			if (twoNum.contains(PokerUtil.getCardValue(c))) {
				tempCards.add(c);
			} else {
				two1 = c;
			}
		}

		for (Byte c : otherCards) {
			if (otherTwoNum.contains(PokerUtil.getCardValue(c))) {
				tempOtherCards.add(c);
			} else {
				two2 = c;
			}
		}

		res = compareZAPAI(tempCards, tempOtherCards, false);

		if (0 != res) {
			return res;
		} else {
			return compareSinglePoker(two1, two2, useColor);
		}
	}

	public static int compareSANTIAO(List<Byte> cards, List<Byte> threeNum, List<Byte> otherCards, List<Byte> otherThreeNum,
			boolean useColor) {
		int res = 0;
		List<Byte> twoCards = new ArrayList<>();
		List<Byte> twoOtherCards = new ArrayList<>();
		List<Byte> threeCards = new ArrayList<>();
		List<Byte> otherThreeCards = new ArrayList<>();

		for (int i = 0, len = threeNum.size(); i < len; i++) {
			for (Byte c : cards) {
				if (threeNum.get(i) == PokerUtil.getCardValue(c)) {
					threeCards.add(c);
				} else {
					twoCards.add(c);
				}
			}
		}
		for (int i = 0, len = otherThreeNum.size(); i < len; i++) {
			for (Byte c : otherCards) {
				if (otherThreeNum.get(i) == PokerUtil.getCardValue(c)) {
					otherThreeCards.add(c);
				} else {
					twoOtherCards.add(c);
				}
			}
		}

		res = compareZAPAI(threeCards, otherThreeCards, false);

		if (0 != res) {
			return res;
		} else {
			return compareZAPAI(twoCards, twoOtherCards, useColor);
		}
	}

	public static int compareTONGHUASHUN(List<Byte> cards, List<Byte> otherCards, boolean useColor) {
		return compareSinglePoker(cards.get(cards.size() - 1), otherCards.get(otherCards.size() - 1), useColor);
	}

	public static int compareTONGHUA(List<Byte> cards, List<Byte> otherCards, boolean useColor) {
		return compareZAPAI(cards, otherCards, useColor);
	}

	public static int compareHULU(List<Byte> cards, List<Byte> threeNum, List<Byte> otherCards, List<Byte> otherThreeNum,
			boolean useColor) {
		int res = 0;
		List<Byte> twoCards = new ArrayList<>();
		List<Byte> twoOtherCards = new ArrayList<>();
		List<Byte> threeCards = new ArrayList<>();
		List<Byte> otherThreeCards = new ArrayList<>();

		for (int i = 0, len = threeNum.size(); i < len; i++) {
			for (Byte c : cards) {
				if (threeNum.get(i) == PokerUtil.getCardValue(c)) {
					threeCards.add(c);
				} else {
					twoCards.add(c);
				}
			}
		}
		for (int i = 0, len = otherThreeNum.size(); i < len; i++) {
			for (Byte c : otherCards) {
				if (otherThreeNum.get(i) == PokerUtil.getCardValue(c)) {
					otherThreeCards.add(c);
				} else {
					twoOtherCards.add(c);
				}
			}
		}

		res = compareZAPAI(threeCards, otherThreeCards, useColor);

		if (0 != res) {
			return res;
		} else {
			return compareZAPAI(twoCards, twoOtherCards, useColor);
		}
	}

	public static int compareSITIAO(List<Byte> cards, List<Byte> fourNum, List<Byte> otherCards, List<Byte> otherFourNum,
			boolean useColor) {
		int res = 0;
		byte four1 = fourNum.get(0);
		byte four2 = otherFourNum.get(0);
		byte single1 = -1;
		byte single2 = -1;
		for (Byte c : cards) {
			if (fourNum.get(0) != PokerUtil.getCardValue(c)) {
				single1 = c;
				break;
			}
		}
		for (Byte c : otherCards) {
			if (otherFourNum.get(0) != PokerUtil.getCardValue(c)) {
				single2 = c;
			}
		}

		// List<Byte> oneCards = new ArrayList<>();
		// List<Byte> oneOtherCards = new ArrayList<>();
		// List<Byte> fourCards = new ArrayList<>();
		// List<Byte> otherFourCards = new ArrayList<>();

		// for (int i = 0, len = fourNum.size(); i < len; i++) {
		// for (Byte c : cards) {
		// if (fourNum.get(i) == PokerUtil.getCardValue(c)) {
		// fourCards.add(c);
		// } else {
		// oneCards.add(c);
		// }
		// }
		// }
		// for (int i = 0, len = otherFourNum.size(); i < len; i++) {
		// for (Byte c : otherCards) {
		// if (otherFourNum.get(i) == PokerUtil.getCardValue(c)) {
		// otherFourCards.add(c);
		// } else {
		// oneOtherCards.add(c);
		// }
		// }
		// }

		res = compareSinglePoker(four1, four2, false);

		if (0 != res) {
			return res;
		} else {
			return compareSinglePoker(single1, single2, useColor);
		}
	}

	public static int compareSHUNZI(List<Byte> cards, List<Byte> otherCards, boolean useColor) {
		return compareZAPAI(cards, otherCards, useColor);
	}

	public static int compareWUTIAO(List<Byte> cards, List<Byte> otherCards, boolean useColor) {
		return compareZAPAI(cards, otherCards, useColor);
	}
}
