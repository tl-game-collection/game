package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfBuHuaInfo {
    public static class BuHuaInfo {
        public List<Byte> huaCard = new ArrayList<>();
        public int huaNum;
        public List<Byte> handCard = new ArrayList();
        public long playerUid;
        public List<Byte> newCard = new ArrayList<>();


        @Override
        public String toString() {
            return "BuHuaInfo{" +
                    ",huaCard=" + huaCard +
                    ",huaNum=" + huaNum +
                    ",handCard=" + handCard +
                    ",playerUid=" + playerUid +
                    ",newCard=" + newCard +
                    '}';
        }
    }

    public List<BuHuaInfo> buHuaInfo = new ArrayList<BuHuaInfo>();

    @Override
    public String toString(){
        return "PCLIMahjongNtfBuHuaInfo{" +
                "buHuaInfo=" + buHuaInfo +
                '}';
    }
}
