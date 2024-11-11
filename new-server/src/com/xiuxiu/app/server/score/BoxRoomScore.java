package com.xiuxiu.app.server.score;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BoxRoomScore extends BaseRoomScore {

    protected long boxUid;
    protected int mark;

    private List<Long> playerUids = new ArrayList<Long>();

    public BoxRoomScore() {
        this.tableType = ETableType.TB_BOX_SCORE;
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
            ScoreItemInfo itemInfo = new ScoreItemInfo();
            itemInfo.setPlayerUid(playerId);
            itemInfo.setScore(score);
            tempList.add(itemInfo);
            if (!playerUids.contains(playerId)) {
                playerUids.add(playerId);
                BoxRoomScorePlayerId tempData = new BoxRoomScorePlayerId();
                tempData.setUid(UIDManager.I.getAndInc(UIDType.SCORE_BOX_PLAYER));
                tempData.setScoreUid(this.getUid());
                tempData.setPlayerUid(playerId);
                tempData.setDirty(Boolean.TRUE);
                tempData.setClubUid(room.getFromClubUid(playerId));
                tempData.save();
            }
        } else {
            ScoreItemInfo itemInfo = tempList.get(findIndex);
            itemInfo.setScore(score);
        }
    }
    
    public int getScore(long playerId) {
        List<ScoreItemInfo> tempList = totalScore.getScore();
        for (int i = 0,  size = tempList.size(); i < size; i++) {
            ScoreItemInfo info = tempList.get(i);
            if (info.getPlayerUid() == playerId) {
                return info.getScore();
            }
        }
        return 0;
    }

    /**
     * 获取大赢家玩家uid列表
     * 
     * @return
     */
    public List<Long> getWinPlayerUids() {
        int countScore = 0;
        List<Long> winPlayerUids = new ArrayList<>();
        List<ScoreItemInfo> tempList = totalScore.getScore();
        for (int i = 0, size = tempList.size(); i < size; ++i) {
            ScoreItemInfo scoreItemInfo = tempList.get(i);
            if (null == scoreItemInfo) {
                continue;
            }
            if (scoreItemInfo.getScore() > countScore) {
                winPlayerUids.clear();
                winPlayerUids.add(scoreItemInfo.getPlayerUid());
                countScore = scoreItemInfo.getScore();
            } else if (scoreItemInfo.getScore() == countScore) {
                winPlayerUids.add(scoreItemInfo.getPlayerUid());
            }
        }
        return winPlayerUids;
    }

    public long getBoxUid() {
        return boxUid;
    }

    public void setBoxUid(long boxUid) {
        this.boxUid = boxUid;
    }

    public String getPlayerUidsDb() {
        return JsonUtil.toJson(this.playerUids);
    }

    public void setPlayerUidsDb(String value) {
        if (StringUtil.isEmptyOrNull(value)) {
            return;
        }
        List<Long> temp = JsonUtil.fromJson(value, new TypeReference<ArrayList<Long>>() {
        });
        if (null != temp) {
            this.playerUids = temp;
        }
    }

    public List<Long> getPlayerUids() {
        return playerUids;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "BoxRoomScore{" + "boxUid=" + boxUid + ", mark=" + mark
                + ", roomUid=" + roomUid + ", roomId=" + roomId + ", gameType=" + gameType + ", gameSubType="
                + gameSubType + ", roomType=" + roomType + ", groupUid=" + groupUid + ", playerUids=" + playerUids
                + ", beginTime=" + beginTime + ", endTime=" + endTime + ", totalScore="
                + totalScore + ", record=" + record + ", isNew=" + isNew + ", tableType=" + tableType + ", uid=" + uid
                + ", dirty=" + dirty + '}';
    }
}
