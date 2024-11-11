package com.xiuxiu.app.server.table;

import java.util.HashMap;

public class CowMultipleManager {
    private static class CowMultipleManagerHolder {
        private static CowMultipleManager instance = new CowMultipleManager();
    }

    public static CowMultipleManager I = CowMultipleManagerHolder.instance;

    private CowMultiple cowMultiple;

    private HashMap<String, CowMultiple.CowMultipleInfo> allInfo = new HashMap<>();

    private CowMultipleManager() {
    }

    public void init(CowMultiple cowMultiple) {
        this.cowMultiple = cowMultiple;
        for (CowMultiple.CowMultipleInfo info : cowMultiple.list) {
            this.allInfo.put(info.getCity() + "_" + info.getWtype(), info);
        }
    }

    public int getMultiple(int type, int playType) {
        CowMultiple.CowMultipleInfo info = this.allInfo.get(type + "_" + playType);
        if (null == info) {
            return 1;
        }
        return info.getNum();
    }
}
