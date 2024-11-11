package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取玩家上线一条线返回
 * @auther: luocheng
 * @date: 2020/1/5 10:42
 */
public class GetClubMemberUpLinesResp extends ErrorMsg {
    public long playerUid;
    public long clubUid;
    public List<Long> upLines = new ArrayList<>();

    @Override
    public String toString() {
        return "GetClubMemberUpLinesResp{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", upLines=" + upLines +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
