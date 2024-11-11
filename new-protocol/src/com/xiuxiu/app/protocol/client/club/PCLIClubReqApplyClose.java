package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqApplyClose {
    public long id; // 俱乐部Uid
    public int status;//状态(0开放1打烊)
    
    @Override
    public String toString() {
        return "PCLIClubReqClubJoinClub{" + "id=" + id + '}';
    }
}
