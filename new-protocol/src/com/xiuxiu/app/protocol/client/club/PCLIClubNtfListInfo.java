package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfListInfo {
    public List<PCLIClubInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIClubNtfListInfo{" +
                "list=" + list +
                '}';
    }
}
