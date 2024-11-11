package com.xiuxiu.app.server.system;

public class Announcement {
    private long id;
    private String content;
    private long announceAt;
    private long expireAt;
    private long repeatInterval;

    public Announcement() {
        
    }

    public Announcement(long id, String content, long announceAt, long expireAt, long repeatInterval) {
        this.id = id;
        this.content = content;
        this.announceAt = announceAt;
        this.expireAt = expireAt;
        this.repeatInterval = repeatInterval;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getAnnounceAt() {
        return announceAt;
    }

    public void setAnnounceAt(long announceAt) {
        this.announceAt = announceAt;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
