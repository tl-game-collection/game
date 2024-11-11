package com.xiuxiu.app.server.room.normal.mahjong2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TingInfo {
    protected HashMap<Byte/*takeCard*/, HashMap<Byte/*huCard*/, Integer>> ting = new HashMap<>();
    protected boolean build = false;
    protected byte takeCard = -1;

    public TingInfo() {

    }

    public boolean isTing() {
        return !this.ting.isEmpty();
    }

    /**
     * 判断是否胡
     * @param takeCard
     * @param huCard
     * @return
     */
    public boolean isHuCard(byte takeCard, byte huCard) {
        HashMap<Byte, Integer> temp = this.ting.get(takeCard);
        if (null == temp) {
            return false;
        }
        return null != temp.get(huCard);
    }

    public Set<Byte> getHuCard(byte takeCard) {
        HashMap<Byte, Integer> temp = this.ting.get(takeCard);
        if (null == temp) {
            return null;
        }
        return temp.keySet();
    }

    /**
     * 获取最大番数, 0: 表示没有听
     * @return
     */
    public int getMaxFang() {
        int fang = 0;
        for (Map.Entry<Byte, HashMap<Byte, Integer>> entry : this.ting.entrySet()) {
            HashMap<Byte, Integer> ting = entry.getValue();
            for (Map.Entry<Byte, Integer> entry1 : ting.entrySet()) {
                int temp = entry1.getValue() >> 16;
                if (temp > fang) {
                    fang = temp;
                }
            }
        }
        return fang;
    }

    public void add(byte takeCard, byte huCard, int fang, int remainCnt) {
        HashMap<Byte, Integer> temp = this.ting.get(takeCard);
        if (null == temp) {
            temp = new HashMap<>();
            this.ting.putIfAbsent(takeCard, temp);
            temp = this.ting.get(takeCard);
        }
        temp.put(huCard, fang << 16 | remainCnt);
    }

    public void setBuild(boolean build) {
        this.build = build;
    }

    public boolean isBuild() {
        return build;
    }

    public void setTakeCard(byte takeCard) {
        this.takeCard = takeCard;
    }

    public HashMap<Byte, HashMap<Byte, Integer>> getTing() {
        return ting;
    }

    public void clear() {
        for (Map.Entry<Byte, HashMap<Byte, Integer>> entry : this.ting.entrySet()) {
            entry.getValue().clear();
        }
        this.ting.clear();
        this.build = false;
        this.takeCard = -1;
    }
}
