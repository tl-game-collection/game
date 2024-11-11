package com.xiuxiu.app.server.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecommendInfo implements Serializable {
    
    private static final long serialVersionUID = -5638052923768316409L;
    protected int num;                                              // 次数
    protected int diamond;                                          // 房卡
    protected List<Long> recommendedPlayerUid = new ArrayList<>();  // 被我推荐的人
    protected long recommendPlayerUid = -1;                         // 我的推荐人


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public List<Long> getRecommendedPlayerUid() {
        return recommendedPlayerUid;
    }

    public void setRecommendedPlayerUid(List<Long> recommendedPlayerUid) {
        this.recommendedPlayerUid = recommendedPlayerUid;
    }

    public long getRecommendPlayerUid() {
        return recommendPlayerUid;
    }

    public void setRecommendPlayerUid(long recommendPlayerUid) {
        this.recommendPlayerUid = recommendPlayerUid;
    }
}
