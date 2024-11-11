package com.xiuxiu.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5Util {
    public static String getMD5(String value) {
        if (StringUtil.isEmptyOrNull(value)) {
            return null;
        }
        return StringUtil.bytes2Hex(getMD50(value.getBytes()));
    }

    public static String getMD5(byte[] value) {
        return StringUtil.bytes2Hex(getMD50(value));
    }

    private static byte[] getMD50(byte[] value) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(value);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5(Object... args) {
        String desc = "";
        for (int i = 0, len = args.length; i < len; ++i) {
            if (null == args[i]) {
                continue;
            }
            desc += args[i].toString();
        }
        if (StringUtil.isEmptyOrNull(desc)) {
            return null;
        }
        return getMD5(desc);
    }
}
