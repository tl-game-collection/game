package com.xiuxiu.app.server.room.normal.Hundred;

import com.xiuxiu.app.server.player.IPlayer;

import java.util.HashMap;

public abstract class HundredBanker implements IHundredBanker {
    protected long bankerUid;
    protected long playerUid;
    protected HashMap<String, Integer> param;
    protected int winScore = 0;
    protected int bureau;
    protected int value;
    protected boolean down;

    public HundredBanker(long bankerUid, IPlayer player, HashMap<String, Integer> param) {
        this.bankerUid = bankerUid;
        this.playerUid = null == player ? -1L : player.getUid();
        this.param = param;
    }

    @Override
    public long getBankerUid() {
        return this.bankerUid;
    }

    @Override
    public long getUid() {
        return this.playerUid;
    }

    @Override
    public int getParam(String key) {
        return this.param.getOrDefault(key, 0);
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public void setWinScore(int score) {
        this.winScore = score;
    }

    @Override
    public int getWinScore() {
        return this.winScore;
    }

    @Override
    public void addValue(int value) {
        this.value += value;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void incBureau() {
        ++this.bureau;
    }

    @Override
    public int getBureau() {
        return this.bureau;
    }

    @Override
    public void down(boolean down) {
        this.down = down;
    }

    @Override
    public boolean isDown() {
        return this.down;
    }

    @Override
    public void clear() {
        this.winScore = 0;
    }
}
