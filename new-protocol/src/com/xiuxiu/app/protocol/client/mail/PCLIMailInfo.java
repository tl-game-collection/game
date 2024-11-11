package com.xiuxiu.app.protocol.client.mail;

import java.util.HashMap;

public class PCLIMailInfo {
    public long mailUid;
    public long sendUid;
    public String title;
    public String content;
    public int state;                        // 0: 正常, 1: 已读, 2: 删除
    public int itemState;                    // 0: 不可领取, 1: 可领取, 2: 已领取
    public HashMap<Integer, Integer> item = new HashMap<>();
    public long sendTime;

    @Override
    public String toString() {
        return "PCLIMailInfo{" +
                "mailUid=" + mailUid +
                ", sendUid=" + sendUid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", state=" + state +
                ", itemState=" + itemState +
                ", item=" + item +
                ", sendTime=" + sendTime +
                '}';
    }
}
