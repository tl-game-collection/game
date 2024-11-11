package com.xiuxiu.app.server.table;

import com.xiuxiu.app.server.room.normal.mahjong.EHuType;

public class TbKWXFangManager {
    private static class TbKWXFangManagerHolder {
        private static TbKWXFangManager instance = new TbKWXFangManager();
    }

    public static TbKWXFangManager I = TbKWXFangManagerHolder.instance;

    protected TbKWXFang tbKWXFang;

    private TbKWXFangManager() {
    }

    public void init(TbKWXFang tbKWXFang) {
        this.tbKWXFang = tbKWXFang;
    }

    public int getFang(int gameSubType, EHuType huType) {
        TbKWXFang.TbKWXFangInfo info = this.tbKWXFang.getMap().get(huType.getValue());
        if (null == info) {
            return 0;
        }
        switch (gameSubType) {
            case 1:
                return info.getXG();
            case 2:
                return info.getXY();
            case 3:
                return info.getSY();
            case 4:
                return info.getSZ();
            case 5:
                return info.getYC();
        }
        return 0;
    }
}
