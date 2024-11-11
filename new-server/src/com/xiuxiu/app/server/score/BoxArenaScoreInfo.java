package com.xiuxiu.app.server.score;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.client.*;
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

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 17:49
 * @comment:
 */
public class BoxArenaScoreInfo extends BaseTable {
    private long time;                                          // 时间
    private List<ScoreItemInfo> score = new LinkedList<>();     // 分数
    private long boxUid;

    public BoxArenaScoreInfo() {
        this.tableType = ETableType.TB_BOX_ARENA_SCORE_INFO;
    }

    public PCLIScoreInfo toProtocolScoreInfo(ERoomType roomType) {
        PCLIScoreInfo info = new PCLIScoreInfo();
        info.time = this.time;
        info.recordUid = this.uid;
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
                itemInfo.cardType = temp.getCardType();
                itemInfo.card = temp.getCard();
                itemInfo.likeCnt = temp.getLike().size();
                itemInfo.like.addAll(temp.getLike());
                player.visitCardTo(itemInfo.allVisitCard);
                info.score.add(itemInfo);
            }
        }
        return info;
    }
    public PCLIFgfScoreInfo toProtocolFgfScoreInfo() {
        PCLIFgfScoreInfo info = new PCLIFgfScoreInfo();
        info.time = this.time;
        if (null != this.score) {
            info.score = new LinkedList<>();
            Iterator<ScoreItemInfo> it = this.score.iterator();
            while (it.hasNext()) {
                ScoreItemInfo temp = it.next();
                Player player = PlayerManager.I.getPlayer(temp.getPlayerUid());
                if (null == player) {
                    continue;
                }
                PCLIFgfScoreItemInfo itemInfo = new PCLIFgfScoreItemInfo();
                itemInfo.playerUid = temp.getPlayerUid();
                itemInfo.playerName = player.getName();
                itemInfo.icon = player.getIcon();
                itemInfo.score = NumberUtils.get2Decimals(temp.getScore());
                itemInfo.cardType = temp.getCardType();
                itemInfo.card = temp.getCard();
                itemInfo.isDiscard=temp.isDiscard;
                itemInfo.isWin=temp.isWin;
                info.score.add(itemInfo);
            }
        }
        return info;
    }

    public PCLICowScoreInfo toProtocolCowScoreInfo() {
        PCLICowScoreInfo info = new PCLICowScoreInfo();
        info.time = this.time;
        info.boxUid = this.boxUid;
        if (null != this.score) {
            info.score = new LinkedList<>();
            Iterator<ScoreItemInfo> it = this.score.iterator();
            while (it.hasNext()) {
                ScoreItemInfo temp = it.next();
                Player player = PlayerManager.I.getPlayer(temp.getPlayerUid());
                if (null == player) {
                    continue;
                }
                PCLICowScoreItemInfo itemInfo = new PCLICowScoreItemInfo();
                itemInfo.playerUid = temp.getPlayerUid();
                itemInfo.playerName = player.getName();
                itemInfo.icon = player.getIcon();
                itemInfo.score = NumberUtils.get2Decimals(temp.getScore());
                itemInfo.cardType = temp.getCardType();
                itemInfo.card = temp.getCard();
                itemInfo.handCard = temp.getTailCard();
                itemInfo.bankerMul=temp.getBankerMul();
                itemInfo.isBnaker=temp.isBnaker();
                itemInfo.lastCard=temp.getLastCard();
                itemInfo.pushMul=temp.getPushMul();
                itemInfo.laiZiCard = (byte) temp.getMonsterType();
                info.score.add(itemInfo);
            }
        }
        return info;
    }
    
    public PCLIPaiCowScoreInfo toProtocolPaiCowScoreInfo() {
        PCLIPaiCowScoreInfo info = new PCLIPaiCowScoreInfo();
        info.time = this.time;
        if (null != this.score) {
            info.score = new LinkedList<>();
            Iterator<ScoreItemInfo> it = this.score.iterator();
            while (it.hasNext()) {
                ScoreItemInfo temp = it.next();
                Player player = PlayerManager.I.getPlayer(temp.getPlayerUid());
                if (null == player) {
                    continue;
                }
                PCLIPaiCowScoreItemInfo itemInfo = new PCLIPaiCowScoreItemInfo();
                itemInfo.playerUid = temp.getPlayerUid();
                itemInfo.playerName = player.getName();
                itemInfo.icon = player.getIcon();
                itemInfo.score = NumberUtils.get2Decimals(temp.getScore());
                itemInfo.card = temp.getCard();
                itemInfo.cardType = temp.getCardTypes();
                info.score.add(itemInfo);
            }
        }
        return info;
    }

    public PCLIThirteenScoreInfo toProtocolThirteenScoreInfo() {
        PCLIThirteenScoreInfo info = new PCLIThirteenScoreInfo();
        info.time = this.time;
        if (null != this.score) {
            info.score = new LinkedList<>();
            Iterator<ScoreItemInfo> it = this.score.iterator();
            while (it.hasNext()) {
                ScoreItemInfo temp = it.next();
                Player player = PlayerManager.I.getPlayer(temp.getPlayerUid());
                if (null == player) {
                    continue;
                }
                PCLIThirteenScoreItemInfo itemInfo = new PCLIThirteenScoreItemInfo();
                itemInfo.playerUid = temp.getPlayerUid();
                itemInfo.playerName = player.getName();
                itemInfo.icon = player.getIcon();
                itemInfo.score = NumberUtils.get2Decimals(temp.getScore());
                itemInfo.card = temp.getCard();
                itemInfo.monsterType=temp.getMonsterType();
                itemInfo.headCard=temp.getHeadCard();
                itemInfo.mediumCard=temp.getMediumCard();
                itemInfo.tailCard=temp.getTailCard();
                info.score.add(itemInfo);
            }
        }
        return info;
    }

    public PCLSGScoreInfo toProtocolSGScoreInfo() {
        PCLSGScoreInfo info = new PCLSGScoreInfo();
        info.time = this.time;
        if (null != this.score) {
            info.score = new LinkedList<>();
            Iterator<ScoreItemInfo> it = this.score.iterator();
            while (it.hasNext()) {
                ScoreItemInfo temp = it.next();
                Player player = PlayerManager.I.getPlayer(temp.getPlayerUid());
                if (null == player) {
                    continue;
                }
                PCLISGScoreItemInfo itemInfo = new PCLISGScoreItemInfo();
                itemInfo.playerUid = temp.getPlayerUid();
                itemInfo.playerName = player.getName();
                itemInfo.icon = player.getIcon();
                itemInfo.score = NumberUtils.get2Decimals(temp.getScore());
                itemInfo.cardType = temp.getCardType();
                itemInfo.cardTypeExtra = temp.getCardTypeExtra();
                itemInfo.card = temp.getCard();
                itemInfo.isBnaker=temp.isBnaker();
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
        List<ScoreItemInfo> temp = JsonUtil.fromJson(score, new TypeReference<List<ScoreItemInfo>>() {
        });
        if (null != temp) {
            this.score = temp;
        }
    }

    public long getBoxUid() {
        return boxUid;
    }

    public void setBoxUid(long boxUid) {
        this.boxUid = boxUid;
    }

    @Override
    public String toString() {
        return "ArenaScoreInfo{" +
                "time=" + time +
                ", score=" + score +
                ", boxUid=" + boxUid +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }

}
