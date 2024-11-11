package com.xiuxiu.app.server.mail;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.client.mail.PCLIMailInfo;
import com.xiuxiu.core.BaseObject;
import com.xiuxiu.core.utils.JsonUtil;

import java.util.HashMap;

public class Mail extends BaseObject {
    protected long senderPlayerUid;             // 发送玩家uid
    protected long receivePlayerUid;            // 接收者玩家uid
    protected String title;
    protected String content;
    protected HashMap<Integer, Integer> item = new HashMap<>();
    protected int state;                        // 0: 正常, 1: 已读, 2: 删除
    protected int itemState;                    // 0: 不可领取, 1: 可领取, 2: 已领取
    protected long sendTime;

    public long getSenderPlayerUid() {
        return senderPlayerUid;
    }

    public void setSenderPlayerUid(long senderPlayerUid) {
        this.senderPlayerUid = senderPlayerUid;
    }

    public long getReceivePlayerUid() {
        return receivePlayerUid;
    }

    public void setReceivePlayerUid(long receivePlayerUid) {
        this.receivePlayerUid = receivePlayerUid;
    }

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

    public HashMap<Integer, Integer> getItem() {
        return item;
    }

    public void setItem(HashMap<Integer, Integer> item) {
        this.item = item;
    }

    public String getItemDb() {
        return JsonUtil.toJson(this.item);
    }

    public void setItemDb(String item) {
        this.item = JsonUtil.fromJson(item, new TypeReference<HashMap<Integer, Integer>>() {});
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public PCLIMailInfo toProtocol() {
        PCLIMailInfo info = new PCLIMailInfo();
        info.mailUid = this.uid;
        info.sendUid = this.senderPlayerUid;
        info.title = this.title;
        info.content = this.content;
        info.state = this.state;
        info.item = this.item;
        info.itemState = this.itemState;
        info.sendTime = this.sendTime;
        return info;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "senderPlayerUid=" + senderPlayerUid +
                ", receivePlayerUid=" + receivePlayerUid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", item=" + item +
                ", state=" + state +
                ", itemState=" + itemState +
                ", sendTime=" + sendTime +
                ", uid=" + uid +
                '}';
    }
}
