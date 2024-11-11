package com.xiuxiu.app.protocol.api.game;

/**
 * @auther: yuyunfei
 * @date: 2019/12/30 10:58
 * @comment:
 */
public class ClubDissolveRoom {
    public int roomUid;         // 房间UID
    public String sign; // md5(roomUid, key)

    @Override
    public String toString() {
        return "ClubDissolveRoom{" +
                "roomUid=" + roomUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
