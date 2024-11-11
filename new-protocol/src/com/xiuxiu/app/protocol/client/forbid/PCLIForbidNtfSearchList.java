package com.xiuxiu.app.protocol.client.forbid;

import java.util.List;

import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;

public class PCLIForbidNtfSearchList {
    
    public List<PCLIPlayerSmallInfo> players;

    @Override
    public String toString() {
        return "PCLIForbidNtfSearchList{" +
                ", players=" + players +
                '}';
    }
}
