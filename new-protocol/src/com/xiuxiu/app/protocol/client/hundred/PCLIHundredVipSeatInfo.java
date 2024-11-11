package com.xiuxiu.app.protocol.client.hundred;

/**
 * 百人场VIP座位信息
 */
public class PCLIHundredVipSeatInfo {
    public long uid;
    public String name;
    public String icon;
    public long arenaValue;
    public int index;

    public PCLIHundredVipSeatInfo() {
    }

    public PCLIHundredVipSeatInfo(long uid, String name, String icon, long arenaValue, int index) {
        this.uid = uid;
        this.name = name;
        this.icon = icon;
        this.arenaValue = arenaValue;
        this.index = index;
    }

    @Override
    public String toString() {
        return "PCLIHundredVipSeatInfo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", arenaValue=" + arenaValue +
                ", index=" + index +
                '}';
    }
}
