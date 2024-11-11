package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomGameOverInfo {
    public int bureau;                                              // 剩余局数
    public int roomType;                                            // 房间类型, 0: 普通房间, 1: 竞技场房间, 2: 比赛场房间, 3: 包厢
    public boolean next;                                            // 是否下一局
}