package com.xiuxiu.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtil {
//    private static Random RANDOM = new Random();

//    public static void setSeed(long seed) {
//        RANDOM.setSeed(seed);
//    }

    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static int random(int x) {
        return ThreadLocalRandom.current().nextInt(x);
    }

    public static int random(int min, int max) {
        if (max - min <= 0) {
            return min;
        }
        return min + ThreadLocalRandom.current().nextInt(max - min + 1);
    }
}
