package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqChatPlayerList {
    public long clubUid;
    public int page;

    @Override
    public String toString() {
        return "PCLIClubReqChatPlayerList{" +
                "clubUid=" + clubUid +
                ", page=" + page +
                '}';
    }
}
