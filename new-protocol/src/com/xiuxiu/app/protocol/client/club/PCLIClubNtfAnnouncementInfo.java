package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfAnnouncementInfo {
    public long clubUid;
    public String content;
    public long expireSeconds;

    @Override
    public String toString() {
        return "PCLIClubNtfAnnouncementInfo{" +
                "clubUid=" + clubUid +
                ", content='" + content + '\'' +
                ", expireAfter=" + expireSeconds +
                '}';
    }
}
