package com.xiuxiu.app.protocol.client.floor;

public class PCLIFloorReqCreate {
    public long clubUid;
    public int floorType;   // 楼层类型, 1: 包厢, 2: 竞技场
    public int ownerType = 1;   // 拥有着类型, 1: 群, 2: 联盟
    public String name;
    public int layoutType;//布局类型

    @Override
    public String toString() {
        return "PCLIFloorReqCreate{" +
                "clubUid=" + clubUid +
                ", floorType=" + floorType +
                ", ownerType=" + ownerType +
                ", layoutType=" + layoutType +
                ", name='" + name + '\'' +
                '}';
    }
}
