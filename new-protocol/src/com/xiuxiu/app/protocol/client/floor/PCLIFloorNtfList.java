package com.xiuxiu.app.protocol.client.floor;

import java.util.ArrayList;
import java.util.List;

public class PCLIFloorNtfList {
    public long clubUid;
    public List<PCLIFloorNtfInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIFloorNtfList{" +
                "clubUid=" + clubUid +
                ", list=" + list +
                '}';
    }
}
