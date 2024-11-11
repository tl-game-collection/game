package com.xiuxiu.app.server.table;

import java.util.HashMap;

public class TbWHMJStartHuManager {
    private static class TbWHMJStartHuManagerHolder {
        private static final TbWHMJStartHuManager INSTANCE = new TbWHMJStartHuManager();
    }

    public static TbWHMJStartHuManager I = TbWHMJStartHuManagerHolder.INSTANCE;

    private TbWHMJStartHu whmjStartHu;

    private HashMap<Integer, Integer> allHuInfo = new HashMap<>();

    private TbWHMJStartHuManager() {
    }

    public void init(TbWHMJStartHu whmjStartHu) {
        this.whmjStartHu = whmjStartHu;
        for (TbWHMJStartHu.TbWHMJStartHuInfo info : this.whmjStartHu.list) {
            this.allHuInfo.put(this.getKey(5, info.getId() - 1, 0, 1), info.dianpao_5);
            this.allHuInfo.put(this.getKey(5, info.getId() - 1, 0, 0), info.other_5);
            this.allHuInfo.put(this.getKey(5, info.getId() - 1, 1, 1), info.zimo_5);
            this.allHuInfo.put(this.getKey(6, info.getId() - 1, 0, 1), info.dianpao_6);
            this.allHuInfo.put(this.getKey(6, info.getId() - 1, 0, 0), info.other_6);
            this.allHuInfo.put(this.getKey(6, info.getId() - 1, 1, 1), info.zimo_6);
        }
    }

    public int getStartHu(int hu, int huCnt, boolean ziMo, boolean self) {
        return this.allHuInfo.getOrDefault(this.getKey(hu, huCnt, ziMo ? 1 : 0, self ? 1 : 0), Integer.MIN_VALUE);
    }

    private int getKey(int hu, int huCnt, int ziMo, int myself) {
        return (hu & 0x0F) | ((huCnt & 0x0F) << 4) | ((ziMo & 0x01) << 8) | ((myself & 0x01) << 9);
    }
}
