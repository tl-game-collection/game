package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqChangeAnnouncementInfo {
    public long clubUid;
    public String content;
    public int expireSeconds;

    @Override
    public String toString() {
        return "PCLIClubReqChangeAnnouncementInfo{" +
                "clubUid=" + clubUid +
                ", content='" + content + '\'' +
                ", expireSeconds=" + expireSeconds +
                '}';
    }
}
