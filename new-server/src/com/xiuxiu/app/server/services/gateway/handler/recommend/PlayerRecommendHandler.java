package com.xiuxiu.app.server.services.gateway.handler.recommend;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqRecommendInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.player.RecommendManager;
import com.xiuxiu.core.net.message.Handler;

public class PlayerRecommendHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqRecommendInfo info = (PCLIPlayerReqRecommendInfo) request;
        if (!RecommendManager.I.lock(player.getUid())) {
            Logs.PLAYER.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            if (-1 != player.getRecommendInfo().getRecommendPlayerUid()) {
                Logs.PLAYER.warn("%s 已经推荐过了", player);
                player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_FAIL, ErrorCode.PLAYER_RECOMMENDED);
                return null;
            }
            Player recommendPlayer = PlayerManager.I.getPlayer(info.recommendPlayerUid);
            if (null == recommendPlayer) {
                Logs.PLAYER.warn("%s 推荐用户:%d不存在", player, info.recommendPlayerUid);
                player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
                return null;
            }
            RecommendManager.I.recommend(recommendPlayer, player, -1);
            player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_OK, null);
            return null;
        } finally {
            RecommendManager.I.unlock(player.getUid());
        }
    }
}
