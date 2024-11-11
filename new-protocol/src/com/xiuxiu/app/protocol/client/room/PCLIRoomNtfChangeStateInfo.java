package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfChangeStateInfo {
    public long groupUid;
    public int roomId;
    public int op;                  // 房间状态, 1:add, 2:del, 3: change state, 4: start
    public int state;               // 1:可加入，2:已结束，3:已满，4:已开始
    public PCLIRoomBriefInfo roomBriefInfo;     // 只有在op为add时才有数据


    @Override
    public String toString() {
        return "PCLIRoomNtfChangeStateInfo{" +
                "groupUid=" + groupUid +
                ", roomId=" + roomId +
                ", op=" + op +
                ", state=" + state +
                ", roomBriefInfo=" + roomBriefInfo +
                '}';
    }
}
