package com.xiuxiu.app.server.score;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.client.PCLIScoreInfo;
import com.xiuxiu.app.protocol.client.PCLIScoreItemInfo;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomDestroyType;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.StringUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ScoreInfo extends BaseTable {
    protected long time;
    protected List<ScoreItemInfo> score = new LinkedList<>();
    protected long destroyUid = -1;
    protected int destroyType = ERoomDestroyType.UN_DESTROY.ordinal();

    public ScoreInfo() {
        this.tableType = ETableType.TB_ARENA_SCORE_DETAIL;
    }

    public PCLIScoreInfo toProtocolScoreInfo(ERoomType roomType) {
        PCLIScoreInfo info = new PCLIScoreInfo();
        info.time = this.time;
        info.recordUid = this.uid;
        info.destroyUid = this.destroyUid;
        info.destroyType = this.destroyType;
        if (null != this.score) {
            info.score = new LinkedList<>();
            Iterator<ScoreItemInfo> it = this.score.iterator();
            while (it.hasNext()) {
                ScoreItemInfo temp = it.next();
                Player player = PlayerManager.I.getPlayer(temp.getPlayerUid());
                if (null == player) {
                    continue;
                }
                PCLIScoreItemInfo itemInfo = new PCLIScoreItemInfo();
                itemInfo.playerUid = temp.getPlayerUid();
                itemInfo.playerName = player.getName();
                itemInfo.icon = player.getIcon();
                itemInfo.score = NumberUtils.get2Decimals(temp.getScore());
                itemInfo.likeCnt = temp.getLike().size();
                itemInfo.like.addAll(temp.getLike());
                player.visitCardTo(itemInfo.allVisitCard);
                info.score.add(itemInfo);
            }
        }
        return info;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<ScoreItemInfo> getScore() {
        return score;
    }

    public void setScore(List<ScoreItemInfo> score) {
        this.score = score;
    }

    public String getScoreDb() {
        return JsonUtil.toJson(this.score);
    }

    public void setScoreDb(String score) {
        if (StringUtil.isEmptyOrNull(score)) {
            return;
        }
        List<ScoreItemInfo> temp = JsonUtil.fromJson(score, new TypeReference<List<ScoreItemInfo>>() {});
        if (null != temp) {
            this.score = temp;
        }
    }

    public long getDestroyUid() {
        return destroyUid;
    }

    public void setDestroyUid(long destroyUid) {
        this.destroyUid = destroyUid;
    }

    public int getDestroyType() {
        return destroyType;
    }

    public void setDestroyType(int destroyType) {
        this.destroyType = destroyType;
    }

    public boolean checkIsDestroy(){
        return this.destroyType != ERoomDestroyType.UN_DESTROY.ordinal();
    }

    @Override
    public String toString() {
        return "ArenaScoreInfo{" +
                "time=" + time +
                ", score=" + score +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                ", destroyUid=" + destroyUid +
                ", destroyType=" + destroyType +
                '}';
    }
}
