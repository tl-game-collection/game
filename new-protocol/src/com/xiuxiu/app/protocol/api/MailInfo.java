package com.xiuxiu.app.protocol.api;

import java.util.HashMap;

public class MailInfo {
    protected String title;
    protected String content;
    protected long receivePlayerUid;
    protected boolean server = false;
    protected HashMap<Integer, Integer> item;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getReceivePlayerUid() {
        return receivePlayerUid;
    }

    public void setReceivePlayerUid(long receivePlayerUid) {
        this.receivePlayerUid = receivePlayerUid;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public HashMap<Integer, Integer> getItem() {
        return item;
    }

    public void setItem(HashMap<Integer, Integer> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "MailInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", receivePlayerUid=" + receivePlayerUid +
                ", server=" + server +
                ", item=" + item +
                '}';
    }
}
