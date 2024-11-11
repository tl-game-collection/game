package com.xiuxiu.app.protocol.client.club;

public class PCLIClubCloseStatus {
    /** 打烊状态,0未打烊1打烊中2已打烊 */
    public int status;
    /** 亲友圈uid */
    public long clubUid;

    @Override
    public String toString() {
        return "PCLIClubCloseStatus{" + "status=" + status + '}';
    }
}
