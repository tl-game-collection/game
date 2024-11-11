package com.xiuxiu.app.protocol.api;

public class AddIpInfo {
    protected int ip;
    protected String sign;

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "AddIpInfo{" +
                "ip=" + ip +
                ", sign='" + sign + '\'' +
                '}';
    }
}
