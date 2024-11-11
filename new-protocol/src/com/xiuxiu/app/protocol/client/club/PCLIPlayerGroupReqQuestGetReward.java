package com.xiuxiu.app.protocol.client.club;

public class PCLIPlayerGroupReqQuestGetReward {
	public long clubUid;
	public long boxUid;
    public int index;
    @Override
    public String toString() {
        return "PCLIPlayerGroupReqQuestGetReward{" +
                ", clubUid=" + clubUid +
                ", boxUid=" + boxUid +
                ", index=" + index +
                '}';
    }
}
