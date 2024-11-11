package com.xiuxiu.app.protocol.api.temp.player;

// 修改玩家推荐人ID
public class ModifyUserRecommend {
    public long uid;            // 自己的ID
    public long targetUid;      // 目标ID
    public String sign; // md5(uid + targetUid + key)

    @Override
    public String toString() {
        return "ModifyUserRecommend{" +
                "uid=" + uid +
                ", targetUid=" + targetUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
