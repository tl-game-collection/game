package com.xiuxiu.app.server.table;

import java.util.HashMap;

public class SharkMultipleManager {
    private static class SharkMultipleManagerHolder {
        private static SharkMultipleManager instance = new SharkMultipleManager();
    }

    public static SharkMultipleManager I = SharkMultipleManagerHolder.instance;

    private SharkMultiple sharkMultiple;

    private HashMap<Integer, SharkMultiple.SharkMultipleInfo> allInfo = new HashMap<>();
    private HashMap<Integer, Double> rates = new HashMap<>();

    private SharkMultipleManager() {
    }

    public void init(SharkMultiple sharkMultiple) {
        this.sharkMultiple = sharkMultiple;
        for (SharkMultiple.SharkMultipleInfo info : sharkMultiple.list) {
            this.allInfo.put(info.id, info);
            if(info.getRate() > 0){
                this.rates.put(info.id,info.rate);
            }
        }
    }

    public int getMultiple(int id) {
        SharkMultiple.SharkMultipleInfo info = this.allInfo.get(id);
        if (null == info) {
            return 1;
        }
        return info.getMul();
    }

    public double getRate(int id) {
        double rate = this.rates.getOrDefault(id,0D);
        return rate;
    }

}
