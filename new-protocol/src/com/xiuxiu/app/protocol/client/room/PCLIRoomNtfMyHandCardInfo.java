package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIRoomNtfMyHandCardInfo {
    public List<Byte> handCard = new ArrayList<>();
    public HashMap<Long, List<Byte>> ohc = new HashMap<>();
    public List<Byte> rc = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIRoomNtfMyHandCardInfo{" +
                "handCard=" + handCard +
                ", ohc=" + ohc +
                ", rc=" + rc +
                '}';
    }
}
