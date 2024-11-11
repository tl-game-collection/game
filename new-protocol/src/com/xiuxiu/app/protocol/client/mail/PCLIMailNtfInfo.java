package com.xiuxiu.app.protocol.client.mail;

import java.util.ArrayList;
import java.util.List;

public class PCLIMailNtfInfo {
    public List<PCLIMailInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMailNtfInfo{" +
                "list=" + list +
                '}';
    }
}
