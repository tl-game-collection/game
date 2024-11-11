package com.xiuxiu.app.server.system;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class LocationInfo extends BaseTable {
    private String location;                        // 地址经纬度
    private String info;                            // 地址信息

    public LocationInfo() {
        this.tableType = ETableType.TB_LOCATION_INFO;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "location='" + location + '\'' +
                ", info='" + info + '\'' +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
