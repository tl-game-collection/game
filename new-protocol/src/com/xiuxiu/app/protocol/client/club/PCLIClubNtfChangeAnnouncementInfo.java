package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubNtfChangeAnnouncementInfo {
    public long clubUid;
    public String content;
    public int expireSeconds;

    @Override
    public String toString() {
        return "PCLIClubNtfChangeAnnouncementInfo{" +
                "clubUid=" + clubUid +
                ", content='" + content + '\'' +
                ", expireSeconds=" + expireSeconds +
                '}';
    }
}
