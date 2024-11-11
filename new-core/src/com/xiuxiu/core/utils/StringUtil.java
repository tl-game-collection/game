package com.xiuxiu.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public final class StringUtil {
    public static final String HEX = "0123456789abcdef";

    public static boolean isEmptyOrNull(String value) {
        return null == value || 0 == value.length();
    }

    public static String exception2String(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static String bytes2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = bytes.length; i < len; ++i) {
            sb.append(HEX.charAt((bytes[i] >> 4) & 0x0F));
            sb.append(HEX.charAt(bytes[i] & 0x0F));
        }
        return sb.toString();
    }

    public static HashMap<String, String> queryString2HashMap(String qs) {
        String[] allPatten = qs.split("&");
        HashMap<String, String> value = new HashMap<>();
        for (int i = 0, len = allPatten.length; i < len; ++i) {
            String patten = allPatten[i];
            String[] pair = patten.split("=");
            if (2 != pair.length) {
                continue;
            }
            value.put(pair[0], pair[1]);
        }
        return value;
    }

    public static String hashMap2QueryString(HashMap<String, String> data) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s", entry.getKey(), entry.getValue()));
            first = false;
        }
        return sb.toString();
    }

    public static String toUpperCaseFirst(String s){
        return Character.isUpperCase(s.charAt(0)) ? s : (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
