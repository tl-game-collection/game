package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomReqDissolveOpInfo {
    public int op;       // 1: 同意 2:拒绝

    @Override
    public String toString() {
        return "PCLIRoomReqDissolveOpInfo{" +
                "op=" + op +
                '}';
    }
}
