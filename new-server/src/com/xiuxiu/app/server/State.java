package com.xiuxiu.app.server;

public enum State {
    NORMAL,     // 正常
    REJECT,     // 拒绝
    DELETE,     // 删除
    AGREE,      // 同意
    PROMOTION,  // 提升
    DECLINE,    // 下降
    FORBID,     // 禁玩
    NOT_FORBID, // 取消禁玩
    LEAVE;      // 离开
}
