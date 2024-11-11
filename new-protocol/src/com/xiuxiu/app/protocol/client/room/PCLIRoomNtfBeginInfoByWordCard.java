package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomNtfBeginInfoByWordCard extends PCLIRoomNtfBeginInfo{
    public int myIndex = 0;//玩家自己下标
    public List<List<Byte>> myCards = new ArrayList<>();//玩家手牌
    public int fanpai;//翻牌
    public List<Integer> jiangpai = new ArrayList<>();//将牌

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByWordCard{" +
                "fanpai = " + fanpai +
                ", jiangpai = " + jiangpai +
                ", myIndex=" + myIndex +
                ", myCards=" + myCards +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
