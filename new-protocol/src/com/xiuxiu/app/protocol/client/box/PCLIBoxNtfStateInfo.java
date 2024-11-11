package com.xiuxiu.app.protocol.client.box;

import java.util.List;

public class PCLIBoxNtfStateInfo {
    /** 亲友圈id */
    public long clubUid;
    /** 楼层id */
    public long floorUid;
    /** 分页 */
    public int page;
    /** 总页数 */
    public int totalPage;
    
    public List<PCLIBoxRoomStateInfo> list;

    public int allPlayerCnt;

    /** 客户端请求的gameType */
    public int gameType;


    @Override
    public String toString() {
        return "PCLIBoxNtfStateInfo{" +
                "clubUid=" + clubUid +
                ", floorUid=" + floorUid +
                ", page=" + page +
                ", totalPage=" + totalPage +
                ", allPlayerCnt=" + allPlayerCnt +
                ", list=" + list +
                '}';
    }
}
