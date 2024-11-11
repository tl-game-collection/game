package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomDeskInfo {
    public PCLIRoomInfo roomInfo;
    public long bankerPlayerUid;    // 庄家玩家uid, -1:表示没有庄家
    public int bankerIndex;         // 庄家索引, -1:表示没有庄家
    public int curBureau;              // 当前局数
    public boolean gameing = false; // 是否在游戏中
    public int curPhase;// 当前阶段
    public boolean robBank = false;// 是否抢庄状态
    
}
