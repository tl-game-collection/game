package com.xiuxiu.app.server.room.normal.Hundred;

public interface IHundredBanker {
    boolean isSystem();
    long getBankerUid();
    long getUid();
    int getParam(String key);
    int getScore();
    void setWinScore(int score);
    int getWinScore();
    void addValue(int value);
    int getValue();
    void incBureau();
    int getBureau();
    void clear();
    void down(boolean down);
    boolean isDown();
}
