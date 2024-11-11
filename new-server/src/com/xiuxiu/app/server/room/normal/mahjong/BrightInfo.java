package com.xiuxiu.app.server.room.normal.mahjong;

import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongBrightInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongHalfBrightInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrightInfo {
    public byte kou;
    public HashMap<Byte, HalfBrightInfo> tingInfo = new HashMap<Byte, HalfBrightInfo>();
    public List<BrightInfo> child = new ArrayList<BrightInfo>();

    public List<BrightInfo> copy() {
        List<BrightInfo> list = new ArrayList<>();
        for (BrightInfo temp : this.child) {
            list.add(this.copy(temp));
        }
        return list;
    }

    private BrightInfo copy(BrightInfo info) {
        BrightInfo brightInfo = new BrightInfo();
        for (Map.Entry<Byte, HalfBrightInfo> temp : info.tingInfo.entrySet()) {
            HalfBrightInfo halfBrightInfo = new HalfBrightInfo();
            halfBrightInfo.huCard.putAll(temp.getValue().huCard);
            brightInfo.tingInfo.put(temp.getKey(), halfBrightInfo);
        }
        for (BrightInfo bi : info.child) {
            brightInfo.child.add(this.copy(bi));
        }
        brightInfo.kou = info.kou;
        return brightInfo;
    }

    public List<PCLIMahjongBrightInfo> to() {
        List<PCLIMahjongBrightInfo> mahjongBrightInfos = new ArrayList<>();
        for (BrightInfo temp : this.child) {
            mahjongBrightInfos.add(this.to(temp));
        }
        return mahjongBrightInfos;
    }

    private PCLIMahjongBrightInfo to(BrightInfo info) {
        PCLIMahjongBrightInfo mahjongBrightInfo = new PCLIMahjongBrightInfo();
        for (Map.Entry<Byte, HalfBrightInfo> temp2 : info.tingInfo.entrySet()) {
            PCLIMahjongHalfBrightInfo mahjongHalfBrightInfo = new PCLIMahjongHalfBrightInfo();
            mahjongHalfBrightInfo.huCard = temp2.getValue().huCard;
            mahjongBrightInfo.tingInfo.put(temp2.getKey(), mahjongHalfBrightInfo);
        }
        for (BrightInfo c : info.child) {
            mahjongBrightInfo.child.add(this.to(c));
        }
        mahjongBrightInfo.kou = info.kou;
        return mahjongBrightInfo;
    }

    @Override
    public String toString() {
        return "BrightInfo{" +
                "kou=" + kou +
                ", tingInfo=" + tingInfo +
                ", child=" + child +
                '}';
    }
}
