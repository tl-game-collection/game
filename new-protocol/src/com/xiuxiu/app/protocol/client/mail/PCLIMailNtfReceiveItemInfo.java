package com.xiuxiu.app.protocol.client.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMailNtfReceiveItemInfo {
    public HashMap<Integer, Integer> items = new HashMap<>();
    public List<Long> receiveMailList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMailNtfReceiveItemInfo{" +
                "items=" + items +
                ", receiveMailList=" + receiveMailList +
                '}';
    }
}
