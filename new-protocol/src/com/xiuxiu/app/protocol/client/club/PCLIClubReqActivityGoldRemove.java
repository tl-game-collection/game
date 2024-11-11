package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqActivityGoldRemove {
    
    /**
     * 亲友圈id
     */
    public long id;
    /**
     * 包厢id
     */
    public long boxUid;

    @Override
    public String toString() {
        return "PCLIClubReqActivityGoldRemove{" + ", boxUid=" + boxUid + '}';
    }
}
