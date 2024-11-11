package com.xiuxiu.app.server.rank;

import java.io.Serializable;

public class NewRankItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6004854406886617182L;
    protected long pUid; //玩家uid
    protected int ve; //排行榜数值
    protected long lT; //时间

    public long getpUid() {
        return pUid;
    }

    public void setpUid(long pUid) {
        this.pUid = pUid;
    }

    public int getVe() {
        return ve;
    }

    public void setVe(int ve) {
        this.ve = ve;
    }

    public long getlT() {
        return lT;
    }

    public void setlT(long lT) {
        this.lT = lT;
    }

    @Override
    public String toString() {
        return "NewRankItem{" +
                ", pUid=" + pUid +
                ", ve=" + ve +
                ", lT=" + lT +
                '}';
    }

    public static NewRankItem create(long playerUid, int value, long lastTime){
        NewRankItem item = new NewRankItem();
        item.pUid = playerUid;
        item.ve = value;
        item.lT = lastTime;
        return item;
    }
}
