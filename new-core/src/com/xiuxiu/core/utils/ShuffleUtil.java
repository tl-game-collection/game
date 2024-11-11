package com.xiuxiu.core.utils;

import java.util.Collections;
import java.util.List;

public class ShuffleUtil {
    public static void shuffle(List card) {
        Collections.shuffle(card);
        int rand = RandomUtil.random(10, 1000);
        for (int i = 0; i < rand; ++i) {
            shuffle(card, card.size() / 2);
        }
    }

    public static void shuffle(List card, int n) {
        shuffle0(card, n);
    }

    private static void shuffle0(List card, int n) {
        int n2, m, i, k, t, b = 0;
        for (; n > 1;) {
            n2 = n * 2;
            for (k = 0, m = 1; n2 / m >= 3; ++k, m *= 3);
            m /= 2;
            rightRotate(card, b + m, m, n);
            for (i = 0, t = 1; i < k; ++i, t *= 3) {
                cycleLeader(card, b, t, m * 2 + 1);
            }
            b += m * 2;
            n -= m;
        }
        Object temp = card.get(0);
        card.set(0, card.get(1));
        card.set(1, temp);
    }

    private static void rightRotate(List card, int b, int m, int n) {
        reverse(card, b, 1, n - m);
        reverse(card, b, n - m + 1, n);
        reverse(card, b, 1, n);
    }

    private static void reverse(List card, int b, int from, int to) {
        for (Object t; from < to; ++from, --to) {
            t = card.get(b + from - 1);
            card.set(b + from - 1, card.get(b + to - 1));
            card.set(b + to - 1, t);
        }
    }

    private static void cycleLeader(List card, int b, int from, int mod) {
        Object t;
        for (int i = from * 2 % mod; i != from; i = i * 2 % mod) {
            t = card.get(b + i - 1);
            card.set(b + i - 1, card.get(b + from - 1));
            card.set(b + from - 1, t);
        }
    }
}
