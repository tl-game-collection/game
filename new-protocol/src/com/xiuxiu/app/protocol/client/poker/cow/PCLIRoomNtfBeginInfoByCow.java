package com.xiuxiu.app.protocol.client.poker.cow;

import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfo;

import java.util.HashMap;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 9:55
 * @comment:
 */
public class PCLIRoomNtfBeginInfoByCow extends PCLIRoomNtfBeginInfo {
    public HashMap<Long, Integer> pushNoteScore = new HashMap<>();  // 所有文件推注倍数
    public int roundCount;//对局数
    public int laiZiCard = -1; //赖子牌
    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByCow{" +
                "pushNoteScore=" + pushNoteScore +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                ", roundCount=" + roundCount +
                '}';
    }
}
