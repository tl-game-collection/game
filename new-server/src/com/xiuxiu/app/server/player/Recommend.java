package com.xiuxiu.app.server.player;

import com.xiuxiu.core.BaseObject;

public class Recommend extends BaseObject {
    protected long recommendPlayerUid;          // 推荐人ID
    protected long recommendedPlayerUid;        // 被推荐人ID
    protected long groupUid;                    // 群ID
    protected int state;                        // 状态
    protected int diamond;                      // 房卡
    protected int diamondSum;                   // 总房卡
    protected long bindingTime;                 // 绑定时间

    public long getRecommendPlayerUid() {
        return recommendPlayerUid;
    }

    public void setRecommendPlayerUid(long recommendPlayerUid) {
        this.recommendPlayerUid = recommendPlayerUid;
    }

    public long getRecommendedPlayerUid() {
        return recommendedPlayerUid;
    }

    public void setRecommendedPlayerUid(long recommendedPlayerUid) {
        this.recommendedPlayerUid = recommendedPlayerUid;
    }

    public long getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(long groupUid) {
        this.groupUid = groupUid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getDiamondSum() {
        return diamondSum;
    }

    public void setDiamondSum(int diamondSum) {
        this.diamondSum += diamondSum;
    }

    public long getBindingTime() {
        return bindingTime;
    }

    public void setBindingTime(long bindingTime) {
        this.bindingTime = bindingTime;
    }
}
