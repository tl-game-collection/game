package com.xiuxiu.app.protocol.client.mail;

import java.util.List;

public class PCLIMailNtfDelInfo {
    public List<Long> delMailUids;

    @Override
    public String toString() {
        return "PCLIMailNtfDelInfo{" +
                "delMailUids=" + delMailUids +
                '}';
    }
}
