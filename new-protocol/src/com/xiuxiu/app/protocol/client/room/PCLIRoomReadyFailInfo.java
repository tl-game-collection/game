package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomReadyFailInfo {
    public String error;
    public List<String> diamondFail = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIRoomReadyFailInfo{" +
                "error=" + error +
                ", diamondFail=" + diamondFail +
                '}';
    }
}
