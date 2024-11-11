package com.xiuxiu.app.server.room.normal.mahjong;

import com.xiuxiu.core.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class HalfBrightInfo {
    public HashMap<Byte, Integer> huCard = new HashMap<>();
    public transient HashMap<Byte, Boolean> huKwx = new HashMap<>();
    public transient HashMap<Byte, HashSet<Pair>> split = new HashMap<>();
    public transient HashSet<Byte> bright = new HashSet<>();

    @Override
    public String toString() {
        return "HalfBrightInfo{" +
                "huCard=" + huCard +
                ", split=" + split +
                '}';
    }
}
