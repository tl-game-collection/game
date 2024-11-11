package com.xiuxiu.app.protocol.api.group;

public class AddGroupService {
    public long groupUid;
    public long arenaUid;
    public int cost;
    public long costPlayerUid;

    @Override
    public String toString() {
        return "AddGroupService{" +
                "groupUid=" + groupUid +
                ", arenaUid=" + arenaUid +
                ", cost=" + cost +
                ", costPlayerUid=" + costPlayerUid +
                '}';
    }
}
