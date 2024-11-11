package com.xiuxiu.app.protocol.client.forbid;

import java.util.ArrayList;
import java.util.List;

public class PCLIForbidNtfList {
    public int page;
    public boolean hasNext;
    public List<PCLIForbidNtfInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIForbidNtfList{" +
                "page=" + page +
                ", hasNext=" + hasNext +
                ", list=" + list +
                '}';
    }
}
