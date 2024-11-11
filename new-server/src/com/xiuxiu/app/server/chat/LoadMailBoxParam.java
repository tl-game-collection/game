package com.xiuxiu.app.server.chat;

import java.util.ArrayList;
import java.util.List;

public class LoadMailBoxParam {
    protected long opaque;
    protected long clientLastMsgUid;
    protected long lastMsgUid;
    protected List<Long> recallMsgUid = new ArrayList<>();
    protected long time = System.currentTimeMillis();

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public long getClientLastMsgUid() {
        return clientLastMsgUid;
    }

    public void setClientLastMsgUid(long clientLastMsgUid) {
        this.clientLastMsgUid = clientLastMsgUid;
    }

    public long getLastMsgUid() {
        return lastMsgUid;
    }

    public void setLastMsgUid(long lastMsgUid) {
        this.lastMsgUid = lastMsgUid;
    }

    public List<Long> getRecallMsgUid() {
        return recallMsgUid;
    }

    public void setRecallMsgUid(List<Long> recallMsgUid) {
        this.recallMsgUid = recallMsgUid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
