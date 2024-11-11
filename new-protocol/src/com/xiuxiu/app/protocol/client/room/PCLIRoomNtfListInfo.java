package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomNtfListInfo {
    public long groupUid;
    public List<PCLIRoomBriefInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIRoomNtfListInfo{" +
                "groupUid=" + groupUid +
                ", list=" + list +
                '}';
    }
}
