package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfBuyHorseInfo {
    public List<Integer> buyHorse = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfBuyHorseInfo{" +
                "buyHorse=" + buyHorse +
                '}';
    }
}
