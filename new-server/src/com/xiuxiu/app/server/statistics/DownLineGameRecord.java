package com.xiuxiu.app.server.statistics;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;

public class DownLineGameRecord extends BaseTable {
    private long playerUid;
    private long fromUid;
    private long downLineUid;
    private int count;
    private int score;
    private long zeroTime;
    private String name;

    public DownLineGameRecord() {
        this.tableType = ETableType.TB_DOWN_LINE_GAME_RECORD;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(long fromUid) {
        this.fromUid = fromUid;
    }

    public long getDownLineUid() {
        return downLineUid;
    }

    public void setDownLineUid(long downLineUid) {
        this.downLineUid = downLineUid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getZeroTime() {
        return zeroTime;
    }

    public void setZeroTime(long zeroTime) {
        this.zeroTime = zeroTime;
    }

    @Override
    public String toString() {
        return "DownLineGameCntRecord{" +
                "playerUid=" + playerUid +
                ", fromUid=" + fromUid +
                ", downLineUid=" + downLineUid +
                ", count=" + count +
                ", zeroTime=" + zeroTime +
                ", score=" + score +
                '}';
    }

    public static DownLineGameRecord create(long upLinePlayerUid, long playerUid, long fromUid){
        DownLineGameRecord record = new DownLineGameRecord();
        record.setPlayerUid(upLinePlayerUid);
        record.setDownLineUid(playerUid);
        record.setFromUid(fromUid);
        Player player = PlayerManager.I.getPlayer(playerUid);
        record.setName(player != null ? player.getName() : "-");
        return record;
    }
}
