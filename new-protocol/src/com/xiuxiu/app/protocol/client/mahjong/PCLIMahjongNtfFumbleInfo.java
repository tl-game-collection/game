package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfFumbleInfo {
    public long uid;
    public int index;
    public byte value;
    public int remainCard;
    public boolean auto;
    public List<Byte> handCard = new ArrayList<>();
    public HashMap<Byte, HashMap<Byte, Integer>> tingInfo = new HashMap<>();
    /** 是否听 */
    public boolean ting;
    
    public PCLIMahjongNtfFumbleInfo() {

    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfFumbleInfo{" +
                "uid=" + uid +
                ", index=" + index +
                ", value=" + value +
                ", remainCard=" + remainCard +
                ", auto=" + auto +
                ", handCard=" + handCard +
                ", tingInfo=" + tingInfo +
                '}';
    }
}
