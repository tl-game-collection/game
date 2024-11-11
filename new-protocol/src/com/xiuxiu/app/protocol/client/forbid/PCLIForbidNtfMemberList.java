package com.xiuxiu.app.protocol.client.forbid;

import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIForbidNtfMemberList {
    public int page;
    public boolean hasNext;
    public List<PCLIPlayerSmallInfo> list = new ArrayList<>();
    @Override
    public String toString() {
        return "PCLIForbidNtfList{" +
                "page=" + page +
                ", hasNext=" + hasNext +
                ", list=" + list +
                '}';
    }
}
