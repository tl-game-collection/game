package com.xiuxiu.core.utils;

public final class NumberUtils {
    public static final int _1K = 1024;
    public static final int _1M = 1024 * 1024;
    public static final int _1G = 1024 * 1024 * 1024;
    public static final int _1T = 1024 * 1024 * 1024 * 1024;
    public static final int _500M = 500 * 1024 * 1024;

    public static String get0Decimals(int value) {
        return String.valueOf(value / 100);
    }

    public static String get0Decimals(long value) {
        return String.valueOf(value / 100);
    }

    public static String get2Decimals(int value) {
        int temp = value / 100;
        if (0 == temp && value < 0) {
            return String.format("-%d.%02d", temp, (Math.abs(value - temp * 100)));
        } else {
            return String.format("%d.%02d", temp, (Math.abs(value - temp * 100)));
        }
    }

    public static String get2Decimals(long value) {
        long temp = value / 100;
        if (0 == temp && value < 0) {
            return String.format("-%d.%02d", temp, (Math.abs(value - temp * 100)));
        } else {
            return String.format("%d.%02d", temp, (Math.abs(value - temp * 100)));
        }
    }

    public static boolean isGoodNumber(long value) {
        long temp = value;
        while (0 != temp) {
            long k = temp % 10;
            long temp2 = temp / 10;
            if (0 == temp2) {
                break;
            }
            long model = 0;
            int i = 0;
            for (i = 0; i < 2; ++i) {
                long k1 = temp2 % 10;
                long model1 = k1 - k;
                if (model1 < -1 || model1 > 1) {
                    break;
                }
                if (0 == i) {
                    model = model1;
                }
                if (model != model1) {
                    break;
                }
                temp2 = temp2 / 10;
                k = k1;
            }
            if (2 == i) {
                return true;
            }
            temp = temp / 10;
        }
        return false;
    }

    public static String getFileSizeFmt(long bytes) {
        if (bytes < _1K) {
            return String.format("%dB", bytes);
        } else if (bytes < _1M) {
            return String.format("%.2fKB", bytes * 1.0 / _1K);
        } else if (bytes < _1G) {
            return String.format("%.2fMB", bytes * 1.0 / _1M);
        } else if (bytes < _1T){
            return String.format("%.2fGB", bytes * 1.0 / _1G);
        } else {
            return String.format("%.2fTB", bytes * 1.0 / _1T);
        }
    }
}
