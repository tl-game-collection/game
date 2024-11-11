package com.xiuxiu.app.protocol.api.temp.account;

public class GetAccountActions {
    public long targetUid;  // 目标账号的UID，为0时表示所有账号
    public String action;   // 账号动作，register, login, logout, online
    public long timeBegin;  // 统计的起始时间
    public long timeEnd;    // 统计的结束时间
    public int page;       // 记录分页页数，从1开始
    public int perPage;     // 记录分页每页容量，1到100

    @Override
    public String toString() {
        return "GetAccountActions{" +
                "targetUid=" + targetUid +
                ", action=" + action +
                ", timeBegin=" + timeBegin +
                ", timeEnd=" + timeEnd +
                ", page=" + page +
                ", perPage=" + perPage +
                '}';
    }
}
