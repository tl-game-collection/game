package com.xiuxiu.app.protocol.api;

public class ChatTestInfo {
    protected long fromPlayerUid;
    protected long toPlayerUid;
    protected String say;
    protected int cnt;

    public long getFromPlayerUid() {
        return fromPlayerUid;
    }

    public void setFromPlayerUid(long fromPlayerUid) {
        this.fromPlayerUid = fromPlayerUid;
    }

    public long getToPlayerUid() {
        return toPlayerUid;
    }

    public void setToPlayerUid(long toPlayerUid) {
        this.toPlayerUid = toPlayerUid;
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "ChatTestInfo{" +
                "fromPlayerUid=" + fromPlayerUid +
                ", toPlayerUid=" + toPlayerUid +
                ", say='" + say + '\'' +
                ", cnt=" + cnt +
                '}';
    }
}
