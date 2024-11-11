package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongBrightInfo {
    public byte kou;
    public HashMap<Byte, PCLIMahjongHalfBrightInfo> tingInfo = new HashMap<>();
    public List<PCLIMahjongBrightInfo> child = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMahjongBrightInfo{" +
                "kou=" + kou +
                ", tingInfo=" + tingInfo +
                ", child=" + child +
                '}';
    }
}
