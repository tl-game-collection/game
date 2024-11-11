package com.xiuxiu.app.protocol.client.floor;

import com.xiuxiu.app.protocol.client.box.PCLIBoxInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIFloorNtfInfo {
    public long clubUid;
    public long uid;
    public int floorType;   // 楼层类型, 1: 包厢, 2: 竞技场
    public int ownerType = 1;   // 拥有着类型, 1: 群, 2: 联盟
    public String name;
    public List<PCLIBoxInfo> boxList = new ArrayList<>();       // 包厢列表
    public int layoutType;//布局类型
    public int allPlayerCnt;
    @Override
    public String toString() {
        return "PCLIFloorNtfInfo{" +
                "clubUid=" + clubUid +
                ", uid=" + uid +
                ", floorType=" + floorType +
                ", ownerType=" + ownerType +
                ", name='" + name + '\'' +
                ", boxList=" + boxList +
                ", layoutType=" + layoutType +
                ", allPlayerCnt=" + allPlayerCnt +
                '}';
    }
}
