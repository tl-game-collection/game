package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfThirteenDeskInfo extends PCLIRoomDeskInfo {
    public List<Byte> card = new ArrayList<>();                     // 自己的牌
    public int remain = 0;                                          // 操作剩余时间
    public List<Long> allTakeCard = new ArrayList<>();              // 已出牌玩家
    public long bankerPlayerid = 0;                                  //庄
    public HashMap<Long, Integer> allRobBank = new HashMap();       //抢庄下注
    public HashMap<Long, Integer> allRebetMul = new HashMap();      //下注数量；
    public HashMap<Long, Boolean> allOnlineState = new HashMap();   //在线状态
    public HashMap<Long, String> allScore = new HashMap();          //得分
    public int thirteenRoomType;                                    // 房间类型；

    @Override
    public String toString() {
        return "PCLIPokerNtfThirteenDeskInfo{" +
                "card=" + card +
                ", remain=" + remain +
                ", allTakeCard=" + allTakeCard +
                ", bankerPlayerid=" + bankerPlayerid +
                ", allRobBank=" + allRobBank +
                ", allRebetMul=" + allRebetMul +
                ", allOnlineState=" + allOnlineState +
                ", allScore=" + allScore +
                ", thirteenRoomType=" + thirteenRoomType +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
