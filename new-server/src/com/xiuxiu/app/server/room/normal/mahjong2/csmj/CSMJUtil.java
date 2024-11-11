package com.xiuxiu.app.server.room.normal.mahjong2.csmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;

import java.util.List;

public final class CSMJUtil {
    public static int calc(int index, int curIndex, int[] cnt, int[] type, int[][] useCardCnt, List<Integer> card, long flag, int jtyn, int dsx, int jjg, int st, int lls, long[] min) {
        if (index > curIndex) {
            for (int i = 0, len = cnt[curIndex]; i < len; ++i) {
                long tempFlag = 0;
                if (1 == type[curIndex]) {
                    // 金童玉女
                    useCardCnt[curIndex][card.get(0) >> 8] = card.get(0) & 0xFF;
                    useCardCnt[curIndex][card.get(1) >> 8] = card.get(1) & 0xFF;
                    tempFlag = i << (8 * type[curIndex]);
                } else if (2 == type[curIndex]) {
                    // 大四喜
                    useCardCnt[curIndex][card.get(jtyn + i) >> 8] = 4;
                    tempFlag = i << (8 * type[curIndex]);
                } else if (3 == type[curIndex]) {
                    // 节节高
                    int c = card.get(jtyn + dsx + i * 3) >> 8;
                    useCardCnt[curIndex][c] = 2;
                    useCardCnt[curIndex][c + 1] = 2;
                    useCardCnt[curIndex][c + 2] = 2;
                    tempFlag = i << (8 * type[curIndex]);
                } else if (4 == type[curIndex]) {
                    // 三同
                    int c = card.get(jtyn + dsx + jjg + i * 3) >> 8;
                    useCardCnt[curIndex][c] = 2;
                    useCardCnt[curIndex][c + MahjongUtil.MJ_9_WANG] = 2;
                    useCardCnt[curIndex][c + MahjongUtil.MJ_9_TIAO] = 2;
                    tempFlag = i << (8 * type[curIndex]);
                } else if (5 == type[curIndex]) {
                    int begin = jtyn + dsx + jjg + st;

                    int min1 = Integer.MAX_VALUE;
                    long minIndex1 = -1;
                    int min2 = Integer.MAX_VALUE;
                    long minIndex2 = -1;
                    for (int j = 0; j < lls; ++j) {
                        int c = card.get(begin + j) >> 8;
                        int max = Integer.MIN_VALUE;
                        for (int k = 0; k < 5; ++k) {
                            if (max < useCardCnt[k][c]) {
                                max = useCardCnt[k][c];
                            }
                        }
                        int needCnt = 3 - max;
                        if (Integer.MAX_VALUE == min2) {
                            min2 = needCnt;
                            minIndex2 = j;
                        } else if (Integer.MAX_VALUE == min1) {
                            min1 = needCnt;
                            minIndex1 = j;
                        } else if (min2 > needCnt) {
                            min2 = needCnt;
                            minIndex2 = j;
                        }
                        if (min2 < min1) {
                            min1 = min1 ^ min2;
                            min2 = min1 ^ min2;
                            min1 = min1 ^ min2;
                            minIndex1 = minIndex1 ^ minIndex2;
                            minIndex2 = minIndex1 ^ minIndex2;
                            minIndex1 = minIndex1 ^ minIndex2;
                        }
                    }

                    int c = card.get((int) (begin + minIndex1)) >> 8;
                    useCardCnt[curIndex][c] = 3;
                    c = card.get((int) (begin + minIndex2)) >> 8;
                    useCardCnt[curIndex][c] = 3;

                    tempFlag = (minIndex1 << (8 * type[curIndex] + 4)) | (minIndex2 << (8 * type[curIndex]));
                }
                calc(index, curIndex + 1, cnt, type, useCardCnt, card, flag | tempFlag, jtyn, dsx, jjg, st, lls, min);
                if (1 == type[curIndex]) {
                    // 金童玉女
                    useCardCnt[curIndex][card.get(0) >> 8] = 0;
                    useCardCnt[curIndex][card.get(1) >> 8] = 0;
                } else if (2 == type[curIndex]) {
                    // 大四喜
                    useCardCnt[curIndex][card.get(jtyn + i) >> 8] = 0;
                } else if (3 == type[curIndex]) {
                    // 节节高
                    int c = card.get(jtyn + dsx + i * 3) >> 8;
                    useCardCnt[curIndex][c] = 0;
                    useCardCnt[curIndex][c + 1] = 0;
                    useCardCnt[curIndex][c + 2] = 0;
                } else if (4 == type[curIndex]) {
                    // 三同
                    int c = card.get(jtyn + dsx + jjg + i * 3) >> 8;
                    useCardCnt[curIndex][c] = 0;
                    useCardCnt[curIndex][c + MahjongUtil.MJ_9_WANG] = 0;
                    useCardCnt[curIndex][c + MahjongUtil.MJ_9_TIAO] = 0;
                } else if (5 == type[curIndex]) {
                    // 六六顺
                    int begin = jtyn + dsx + jjg + st;
                    long temp = tempFlag >> (8 * type[curIndex]);
                    int c = card.get((int) (begin + (temp >> 4))) >> 8;
                    useCardCnt[curIndex][c] = 0;
                    c = card.get((int) (begin + (temp & 0xF))) >> 8;
                    useCardCnt[curIndex][c] = 0;
                }
            }
            return 0;
        }

        long tempCnt = 0;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            int max = Integer.MIN_VALUE;
            for (int j = 0; j < 5; ++j) {
                if (max < useCardCnt[j][i]) {
                    max = useCardCnt[j][i];
                }
            }
            tempCnt += max;
        }
        if (0 == min[0]) {
            min[0] = (tempCnt << 48)| flag;
        } else if ((min[0] >> 48) > tempCnt) {
            min[0] = (tempCnt << 48)| flag;
        }
        return 0;
    }

    public static boolean isQYS(byte[] card) {
        // 缺一色
        int colorCnt = 0;
        int curColor = -1;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (card[i] > 0) {
                int tempColor = MahjongUtil.getColor((byte) i);
                if (curColor != tempColor) {
                    ++colorCnt;
                    curColor = tempColor;
                }
            }
        }
        return colorCnt < 3;
    }

    public static int isLLS(byte[] card, List<Integer> temp) {
        // 六六顺: 2个AAA
        int AAACnt = 0;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (card[i] >= 3) {
                ++AAACnt;
                temp.add(i << 8 | 3);
            }
        }
        if (AAACnt == 1) {
            temp.remove(temp.size() - 1);
            return 0;
        }
        return AAACnt;
    }

    public static int isDSX(byte[] card, List<Integer> temp) {
        // 大四喜: 1个AAAA
        int AAAACnt = 0;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (card[i] >= 4) {
                ++AAAACnt;
                temp.add(i << 8 | 4);
            }
        }
        return AAAACnt;
    }

    public static boolean isBBH(byte[] card) {
        // 板板胡: 没有258
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (card[i] > 0) {
                if (MahjongUtil.is258((byte) i)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isYZH(byte[] card) {
        // 一枝花: 1) 只有一张5 2) 某一花色只要5
        int eyeCnt = 0;
        byte eyeCard = -1;
        int colorCnt = 0;
        byte tempCard = -1;
        for (int c = MahjongUtil.COLOR_WANG; c <= MahjongUtil.COLOR_TONG; ++c) {
            colorCnt = 0;
            for (int i = c * 9 + 1, len = i + 8; i <= len; ++i) {
                if (card[i] < 1) {
                    continue;
                }
                tempCard = (byte) i;
                if (MahjongUtil.is258(tempCard)) {
                    eyeCnt += card[i];
                    eyeCard = tempCard;
                }
                colorCnt += card[i];
            }
            if (1 == colorCnt && MahjongUtil.is5(tempCard)) {
                return true;
            }
        }
        if (1 == eyeCnt && MahjongUtil.is5(eyeCard)) {
            return true;
        }
        return false;
    }

    public static int isJJG(byte[] card, List<Integer> temp) {
        // 节节高: AABBCC
        int cnt = 0;
        for (int c = MahjongUtil.COLOR_WANG; c <= MahjongUtil.COLOR_TONG; ++c) {
            for (int i = c * 9 + 1, len = i + 8 - 2; i <= len; ++i) {
                if (card[i] < 2) {
                    continue;
                }
                if (card[i + 1] >= 2 && card[i + 2] >= 2) {
                    temp.add(i << 8 | 2);
                    temp.add((i + 1) << 8 | 2);
                    temp.add((i + 2) << 8 | 2);
                    ++cnt;
                }
            }
        }
        return 3 * cnt;
    }

    public static int isST(byte[] card, List<Integer> temp) {
        // 三同: AAA(A+9)(A+9+9)
        int cnt = 0;
        for (int i = MahjongUtil.MJ_1_WANG; i <= MahjongUtil.MJ_9_WANG; ++i) {
            if (card[i] < 2) {
                continue;
            }
            if (card[i + MahjongUtil.MJ_9_WANG] < 2) {
                continue;
            }
            if (card[i + MahjongUtil.MJ_9_TIAO] < 2) {
                continue;
            }
            temp.add(i << 8 | 2);
            temp.add((i + MahjongUtil.MJ_9_WANG) << 8 | 2);
            temp.add((i + MahjongUtil.MJ_9_TIAO) << 8 | 2);
            ++cnt;
        }
        return cnt * 3;
    }

    public static int isJTYN(byte[] card, List<Integer> temp) {
        // 金童玉女:
        if (card[MahjongUtil.MJ_2_TONG] >= 2 && card[MahjongUtil.MJ_2_TIAO] >= 2) {
            temp.add(MahjongUtil.MJ_2_TIAO << 8 | 2);
            temp.add(MahjongUtil.MJ_2_TONG << 8 | 2);
            return 2;
        }
        return 0;
    }
}
