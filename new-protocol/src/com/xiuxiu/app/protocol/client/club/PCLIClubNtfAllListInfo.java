package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfAllListInfo {
    /**
     * 俱乐部id，俱乐部名称
     */
    public List<PCLIClubListInfo> list=new ArrayList<PCLIClubListInfo>();

    @Override
    public String toString() {
        return "PCLIClubNtfAllListInfo{" +
                "list=" + list +
                '}';
    }
}
