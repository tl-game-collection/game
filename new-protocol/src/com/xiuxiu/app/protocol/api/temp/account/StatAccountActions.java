package com.xiuxiu.app.protocol.api.temp.account;

public class StatAccountActions {
    public String action; // 统计类型：register, login, logout, online
    public long timeBegin; // 起始时间戳，秒为单位
    public long timeEnd; // 截止时间戳，秒为单位
    public int period; // 单个周期间隔，秒为单位

    @Override
    public String toString() {
        return "StatAccountActions{" +
                "action='" + action + '\'' +
                ", timeBegin=" + timeBegin +
                ", timeEnd=" + timeEnd +
                ", period=" + period +
                '}';
    }
}
