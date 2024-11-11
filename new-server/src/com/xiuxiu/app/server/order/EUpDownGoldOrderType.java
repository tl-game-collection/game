package com.xiuxiu.app.server.order;

public enum  EUpDownGoldOrderType {
    //等待处理
    WAIT(0),
    //已处理
    DEAL(1),
    //已拒绝
    REFUSE(2),
    ;

    private int value;

    EUpDownGoldOrderType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
