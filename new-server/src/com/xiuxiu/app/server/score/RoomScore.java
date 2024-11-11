package com.xiuxiu.app.server.score;

import java.util.List;

import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.room.normal.IRoom;

public class RoomScore extends BaseRoomScore {
    protected long playerUid1 = -1;
    protected long playerUid2 = -1;
    protected long playerUid3 = -1;
    protected long playerUid4 = -1;
    protected long playerUid5 = -1;
    protected long playerUid6 = -1;
    protected long playerUid7 = -1;
    protected long playerUid8 = -1;
    protected long playerUid9 = -1;
    protected long playerUid10 = -1;
    protected long playerUid11 = -1;

    public RoomScore() {
        this.tableType = ETableType.TB_ROOM_SCORE;
    }

    public int getScore(long playerId) {
        List<ScoreItemInfo> tempList = totalScore.getScore();
        int size = tempList.size();
        for (int i = 0; i < size; i++) {
            ScoreItemInfo info = tempList.get(i);
            if (info.getPlayerUid() == playerId) {
                return info.getScore();
            }
        }
        return 0;
    }

    @Override
    public void addScoreItemInfo(long playerId, int score, IRoom room) {
        List<ScoreItemInfo> tempList = totalScore.getScore();
        int size = tempList.size();
        int findIndex = -1;
        for (int i = 0; i < size; i++) {
            ScoreItemInfo info = tempList.get(i);
            if (info.getPlayerUid() == playerId) {
                findIndex = i;
                break;
            }
        }
        if (findIndex == -1) {
            if (size < 11) {
                ScoreItemInfo itemInfo = new ScoreItemInfo();
                itemInfo.setPlayerUid(playerId);
                itemInfo.setScore(score);
                tempList.add(itemInfo);
                this.setPlayerUid(playerId, size);
            }
        } else {
            ScoreItemInfo itemInfo = tempList.get(findIndex);
            itemInfo.setScore(score);
        }
    }

    public void setPlayerUid(long playerUid, int index) {
        if (0 == index) {
            this.playerUid1 = playerUid;
        } else if (1 == index) {
            this.playerUid2 = playerUid;
        } else if (2 == index) {
            this.playerUid3 = playerUid;
        } else if (3 == index) {
            this.playerUid4 = playerUid;
        } else if (4 == index) {
            this.playerUid5 = playerUid;
        } else if (5 == index) {
            this.playerUid6 = playerUid;
        } else if (6 == index) {
            this.playerUid7 = playerUid;
        } else if (7 == index) {
            this.playerUid8 = playerUid;
        } else if (8 == index) {
            this.playerUid9 = playerUid;
        } else if (9 == index) {
            this.playerUid10 = playerUid;
        } else if (10 == index) {
            this.playerUid11 = playerUid;
        }
    }

    public boolean hasPlayerUid(long playerUid) {
        return this.playerUid1 == playerUid || this.playerUid2 == playerUid || this.playerUid3 == playerUid
                || this.playerUid4 == playerUid || this.playerUid5 == playerUid || this.playerUid6 == playerUid
                || this.playerUid7 == playerUid || this.playerUid8 == playerUid || this.playerUid9 == playerUid
                || this.playerUid10 == playerUid || this.playerUid11 == playerUid;
    }

    public long getPlayerUid1() {
        return playerUid1;
    }

    public void setPlayerUid1(long playerUid1) {
        this.playerUid1 = playerUid1;
    }

    public long getPlayerUid2() {
        return playerUid2;
    }

    public void setPlayerUid2(long playerUid2) {
        this.playerUid2 = playerUid2;
    }

    public long getPlayerUid3() {
        return playerUid3;
    }

    public void setPlayerUid3(long playerUid3) {
        this.playerUid3 = playerUid3;
    }

    public long getPlayerUid4() {
        return playerUid4;
    }

    public void setPlayerUid4(long playerUid4) {
        this.playerUid4 = playerUid4;
    }

    public long getPlayerUid5() {
        return playerUid5;
    }

    public void setPlayerUid5(long playerUid5) {
        this.playerUid5 = playerUid5;
    }

    public long getPlayerUid6() {
        return playerUid6;
    }

    public void setPlayerUid6(long playerUid6) {
        this.playerUid6 = playerUid6;
    }

    public long getPlayerUid7() {
        return playerUid7;
    }

    public void setPlayerUid7(long playerUid7) {
        this.playerUid7 = playerUid7;
    }

    public long getPlayerUid8() {
        return playerUid8;
    }

    public void setPlayerUid8(long playerUid8) {
        this.playerUid8 = playerUid8;
    }

    public long getPlayerUid9() {
        return playerUid9;
    }

    public void setPlayerUid9(long playerUid9) {
        this.playerUid9 = playerUid9;
    }

    public long getPlayerUid10() {
        return playerUid10;
    }

    public void setPlayerUid10(long playerUid10) {
        this.playerUid10 = playerUid10;
    }

    public long getPlayerUid11() {
        return playerUid11;
    }

    public void setPlayerUid11(long playerUid11) {
        this.playerUid11 = playerUid11;
    }

    @Override
    public String toString() {
        return "RoomScore{" + "roomUid=" + roomUid + ", roomId=" + roomId + ", gameType=" + gameType + ", gameSubType="
                + gameSubType + ", roomType=" + roomType + ", groupUid=" + groupUid + ", playerUid1=" + playerUid1
                + ", playerUid2=" + playerUid2 + ", playerUid3=" + playerUid3 + ", playerUid4=" + playerUid4
                + ", playerUid5=" + playerUid5 + ", playerUid6=" + playerUid6 + ", playerUid7=" + playerUid7
                + ", playerUid8=" + playerUid8 + ", playerUid9=" + playerUid9 + ", playerUid10=" + playerUid10
                + ", playerUid11=" + playerUid11 + ", beginTime=" + beginTime + ", endTime=" + endTime + ", totalScore="
                + totalScore + ", record=" + record + ", isNew=" + isNew + ", tableType=" + tableType + ", uid=" + uid
                + ", dirty=" + dirty + '}';
    }
}
