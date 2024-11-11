package com.xiuxiu.app.protocol.client.player;

/**
 * 获取club成员在本圈中可再次下分的剩余时间
 * @date 2020/1/19 14:41
 * @author luocheng
 */
public class PCLIPlayerReqGetDownGoldTime {
    public long clubUid;

    @Override
    public String toString() {
        return "PCLIPlayerReqGetDownGoldTime{" +
                "clubUid=" + clubUid +
                '}';
    }
}
