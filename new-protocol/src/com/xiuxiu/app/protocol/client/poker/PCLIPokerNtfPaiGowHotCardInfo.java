package com.xiuxiu.app.protocol.client.poker;


import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfPaiGowHotCardInfo {
    public List<Byte> myCard = new ArrayList<>();
    public List<Byte> bankerCard = new ArrayList<>();
    public int crap1;
    public int crap2;

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowHotCardInfo{" +
                "myCard=" + myCard +
                ", bankerCard=" + bankerCard +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                '}';
    }
}
