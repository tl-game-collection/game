package com.xiuxiu.app.server.services.gateway.handler.recommend;

import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfRecommendListInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqRecommendListInfo;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.EPlayerDone;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.player.Recommend;
import com.xiuxiu.app.server.player.RecommendManager;
import com.xiuxiu.app.server.table.DiamondCostManager;
import com.xiuxiu.core.net.message.Handler;

public class PlayerRecommendListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqRecommendListInfo info = (PCLIPlayerReqRecommendListInfo) request;
        int diamondSum = 0;
        if (!RecommendManager.I.lock(player.getUid())) {
            Logs.PLAYER.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_LIST_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            info.page = 0;
            PCLIPlayerNtfRecommendListInfo recommendListInfo = new PCLIPlayerNtfRecommendListInfo();
            List<Recommend> list = RecommendManager.I.load(player, info.page);
            for (Recommend recommend : list) {
                Player tempPlayer = PlayerManager.I.getPlayer(recommend.getRecommendedPlayerUid());
                if (null == tempPlayer) {
                    continue;
                }
                PCLIPlayerNtfRecommendListInfo.RecommendInfo recommendInfo = new PCLIPlayerNtfRecommendListInfo.RecommendInfo();
                recommendInfo.playerUid = tempPlayer.getUid();
                recommendInfo.playerName = tempPlayer.getName();
                recommendInfo.playerIcon = tempPlayer.getIcon();
                recommendInfo.isAchieve = tempPlayer.isDoneGame(EPlayerDone.DONE_GAME);
                recommendInfo.diamond = recommend.getDiamond();
                recommendInfo.bindingTime = recommend.getBindingTime();
                recommendListInfo.recommendList.add(recommendInfo);
                int doneGame = tempPlayer.isDoneGame(EPlayerDone.DONE_GAME) ? DiamondCostManager.I.getCostByGameType(0, DiamondCostManager.COST_TYPE_RECOMMEND_GAME, 0) : 0;
                diamondSum += (recommend.getDiamond() + doneGame);
            }
            recommendListInfo.diamondSum = diamondSum;
            recommendListInfo.residueSum = Constant.RECOMMEND_DIAMOND - diamondSum;
            recommendListInfo.page = info.page;
            recommendListInfo.next = list.size() == Constant.PAGE_CNT_100;
            player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_LIST_OK, recommendListInfo);
            return null;
        } finally {
            RecommendManager.I.unlock(player.getUid());
        }
    }
}
