package com.xiuxiu.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class IPv4Util {
    public static int ip2int(String host) {
        try {
            byte[] ip = InetAddress.getByName(host).getAddress();
            int value = ip[3] & 0xFF;
            value |= ((ip[2] << 8) & 0xFF00);
            value |= ((ip[1] << 16) & 0xFF0000);
            value |= ((ip[0] << 24) & 0xFF000000);
            return value;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String int2Ip(int ip) {
        StringBuilder sb = new StringBuilder();
        sb.append((ip >> 24) & 0xFF);
        sb.append(".");
        sb.append((ip >> 16) & 0xFF);
        sb.append(".");
        sb.append((ip >> 8) & 0xFF);
        sb.append(".");
        sb.append((ip >> 0) & 0xFF);
        return sb.toString();
    }
}
