package com.xiuxiu.core.utils;

public final class IdentityCardUtil {
    private static final int[] weight = new int[] {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};        // 十七位数字本体码权重
    private static final char[] valid = new char[] {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};     // mod11,对应校验码字符值

    public static boolean validate(String id) {
        if (StringUtil.isEmptyOrNull(id)) {
            return false;
        }
        if (id.length() < 18) {
            return false;
        }
        int sum = 0;
        int mode = 0;
        for(int i = 0, len = id.length() - 1; i < len; ++i) {
            sum += Integer.parseInt(String.valueOf(id.charAt(i))) * weight[i];
        }
        mode = sum % 11;
        return id.charAt(id.length() - 1) == valid[mode];
    }
}
