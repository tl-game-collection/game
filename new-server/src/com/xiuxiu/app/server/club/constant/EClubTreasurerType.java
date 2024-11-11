package com.xiuxiu.app.server.club.constant;

public enum EClubTreasurerType {
    UP(1),      //上分财务
    DOWN(2),    //下分财务
    ;

    private int value;

    EClubTreasurerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
