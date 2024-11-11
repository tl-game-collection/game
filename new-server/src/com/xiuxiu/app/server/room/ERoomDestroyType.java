package com.xiuxiu.app.server.room;

public enum ERoomDestroyType {
    UN_DESTROY, //未解散
    APPLY_DESTROY, //通过申请解散
    ARENA_VALUE_LESS_DESTROY, //竞技分不足解散
    MANAGER_DESTROY, //管理解散
    TOP_DESTROY, //封顶解散
    MANAGER_CLOSE_BOX_DESTROY ///管理关闭包厢解散
}
