package com.xiuxiu.app.server.score;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.client.PCLIScoreInfo;
import com.xiuxiu.app.protocol.client.PCLIScoreItemInfo;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.StringUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class BoxArenaScoreInfoPlayerId extends BaseTable {
    private long scoreUid;
    private long playerUid;     //玩家id

    public BoxArenaScoreInfoPlayerId() {
        this.tableType = ETableType.TB_BOX_ARENA_SCORE_INFO_PLAYER_ID;
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

    @Override
    public String toString() {
        return "BoxArenaScoreInfoPlayerId{" +
                "scoreUid=" + scoreUid +
                ", playerUid=" + playerUid+
                '}';
    }
}
