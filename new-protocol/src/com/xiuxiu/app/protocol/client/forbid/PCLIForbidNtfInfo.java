package com.xiuxiu.app.protocol.client.forbid;

import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIForbidNtfInfo {
    public long uid;
    public List<PCLIPlayerSmallInfo> players = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIForbidNtfInfo{" +
                ", uid=" + uid +
                ", players=" + players +
                '}';
    }
}
