package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomReqDissolveInfo {
    public int force;           // 1:强制解散 0:普通

    @Override
    public String toString() {
        return "PCLIRoomReqDissolveInfo{" +
                "force=" + force +
                '}';
    }
}
