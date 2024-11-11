package com.xiuxiu.app.protocol.client.poker.cow;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 9:56
 * @comment:
 */
public class PCLIPokerNtfCowDeskInfo extends PCLIRoomDeskInfo {
    public HashMap<Long, String> allScore = new HashMap<>();        // 所有人的分数
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态
    public HashMap<Long, Integer> allRebet = new HashMap<>();       // 所有人的下注
    public HashMap<Long, Integer> allRobBank = new HashMap<>();     // 所有人的抢庄
    public HashMap<Long, Boolean> isLookCard = new HashMap<>();     // 当前玩家是否已经看牌
    public HashMap<Long, Integer> pushNoteScore = new HashMap<>();  // 所有文件推注倍数
    public List<Byte> card = new ArrayList<>();                     // 自己的牌
    /** 先发牌的信息 */
    public List<Byte> firstShowCard = new ArrayList<>();
    public int sendCardCount;
    public int hotBankerLoop;                                       //端火锅 当前庄进行的轮数
    public int hotDeskNote;                                         //端火锅，当前桌面的筹码数
    public int keepCount = 0;                                       // 当前庄续了几次
    public int totalLoop = 0;                                       //总局数
    public int curBankCnt = 0;                                      //选庄次数
    public byte laiZiCard = -1;                                    //赖子牌

    public List<Long> lookCardPlayers=new ArrayList<>();            //看牌玩家ID
    public long readyTime;                                           //准备时间
    public String toString() {
        return "PCLIPokerNtfCowDeskInfo{" +
                "roomInfo=" + roomInfo +
                ", lookCardPlayers=" + lookCardPlayers +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", allRebet=" + allRebet +
                ", allRobBank=" + allRobBank +
                ", isLookCard=" + isLookCard +
                ", pushNoteScore=" + pushNoteScore +
                ", card=" + card +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                ", bankerIndex=" + bankerIndex +
                ", sendCardCount=" + sendCardCount +
                ", hotBankerLoop=" + hotBankerLoop +
                ", hotDeskNote=" + hotDeskNote +
                ", readyTime=" + readyTime +
                '}';
    }
}
