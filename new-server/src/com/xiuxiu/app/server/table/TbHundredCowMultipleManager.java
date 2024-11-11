package com.xiuxiu.app.server.table;

import java.util.HashMap;

public class TbHundredCowMultipleManager {
    private static class TbHundredCowMultipleManagerHolder {
        private static TbHundredCowMultipleManager instance = new TbHundredCowMultipleManager();
    }

    public static TbHundredCowMultipleManager I = TbHundredCowMultipleManagerHolder.instance;

    private TbHundredCowMultiple tbHundredCowMultiple;

    private HashMap<String, TbHundredCowMultiple.TbHundredCowMultipleInfo> allInfo = new HashMap<>();
    private HashMap<Integer, Integer> maxMultiple = new HashMap<>();

    private TbHundredCowMultipleManager() {
    }

    public void init(TbHundredCowMultiple tbHundredCowMultiple) {
        this.tbHundredCowMultiple = tbHundredCowMultiple;

        for (TbHundredCowMultiple.TbHundredCowMultipleInfo info : tbHundredCowMultiple.list) {
            this.allInfo.put(info.getCity() + "_" + info.getWtype(), info);
            int old = this.maxMultiple.getOrDefault(info.getCity(), 0);
            if (info.getNum() > old) {
                this.maxMultiple.put(info.getCity(), info.getNum());
            }
        }
    }


    public int getMultiple(int type, int playType) {
        TbHundredCowMultiple.TbHundredCowMultipleInfo info = this.allInfo.get(type + "_" + playType);
        if (null == info) {
            return 1;
        }
        return info.getNum();
    }

    public int getMaxMultiple(int type) {
        return this.maxMultiple.getOrDefault(type, 1);
    }
}
