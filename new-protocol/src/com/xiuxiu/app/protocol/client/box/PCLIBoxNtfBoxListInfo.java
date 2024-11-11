package com.xiuxiu.app.protocol.client.box;

import java.util.ArrayList;
import java.util.List;

public class PCLIBoxNtfBoxListInfo {
    public List<PCLIBoxInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIBoxNtfBoxListInfo{" +
                "list=" + list +
                '}';
    }
}
