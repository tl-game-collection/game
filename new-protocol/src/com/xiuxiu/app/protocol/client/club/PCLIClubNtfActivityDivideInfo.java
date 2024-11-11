package com.xiuxiu.app.protocol.client.club;

import java.util.List;

import com.alibaba.fastjson.JSON;

public class PCLIClubNtfActivityDivideInfo {
    /**
     * 是否开启(true开启false关闭)
     */
    public boolean open;
    /**
     * 基本获取比例
     */
    public PCLIClubNtfActivityDivideInfoItem base;
    /**
     * 每档获取比例列表
     */
    public List<PCLIClubNtfActivityDivideInfoItem> items;

    @Override
    public String toString() {
        return "PCLIClubNtfActivityDivideInfo{" + ",open=" + open + ", base=" + JSON.toJSONString(base) + ", items="
                + JSON.toJSONString(items) + '}';
    }
}
