package com.xiuxiu.app.protocol.client.room;

public class PCLIArenaReqReportInfo {
    public long boxUid;
    //public long mainClubUid;   // 来源群
    public int page;        // 从0开始

    @Override
    public String toString() {
        return "PCLIArenaReqReportInfo{" +
                "boxUid=" + boxUid +
                ", page=" + page +
                '}';
    }
}
