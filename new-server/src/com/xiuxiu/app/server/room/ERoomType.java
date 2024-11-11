package com.xiuxiu.app.server.room;

public enum ERoomType {
    NORMAL,
    @Deprecated
    ARENA,
    @Deprecated
    PLAY_FIELD,
    BOX,
    @Deprecated
    MINE_RED,
    ALL,
    ;

    public static ERoomType valueOf(int value) {
        if (value < 0 || value >= ERoomType.values().length - 1) {
            return ERoomType.ALL;
        }
        return ERoomType.values()[value];
    }
}
