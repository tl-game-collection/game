package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 11:48
 * @comment:
 */
public class PCLIPokerReqGetCowRobot {
    public long arenaUid;
    public int roomId;

    @Override
    public String toString() {
        return "PCLIPokerReqGetCowRobot{" +
                "arenaUid=" + arenaUid +
                ", roomId=" + roomId +
                '}';
    }
}
