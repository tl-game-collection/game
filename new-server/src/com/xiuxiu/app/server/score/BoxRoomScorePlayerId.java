package com.xiuxiu.app.server.score;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class BoxRoomScorePlayerId extends BaseTable {
    
    private static final long serialVersionUID = -764427293959997380L;
    private long scoreUid;
    private long playerUid;
    private long clubUid;

    public BoxRoomScorePlayerId() {
        this.tableType = ETableType.TB_BOX_SCORE_PLAYER;
    }

    public long getScoreUid() {
        return scoreUid;
    }

    public void setScoreUid(long scoreUid) {
        this.scoreUid = scoreUid;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    @Override
    public String toString() {
        return "BoxRoomScorePlayerId{" + "scoreUid=" + scoreUid + ", playerUid=" + playerUid +
                ", isNew=" + isNew + ", tableType=" + tableType + ", uid=" + uid + ", dirty=" + dirty + '}';
    }
}
