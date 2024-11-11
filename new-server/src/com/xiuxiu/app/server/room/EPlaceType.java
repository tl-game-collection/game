package com.xiuxiu.app.server.room;

public enum EPlaceType {
    HALL,
    GROUP,
    LEAGUE,
    UNKNOWN,
    ;

    public static EPlaceType valueOf(int value) {
        if (value < 0 || value >= EPlaceType.values().length - 1) {
            return EPlaceType.UNKNOWN;
        }
        return EPlaceType.values()[value];
    }
}