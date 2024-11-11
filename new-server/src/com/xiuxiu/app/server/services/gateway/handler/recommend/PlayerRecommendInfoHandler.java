package com.xiuxiu.app.server.services.gateway.handler.recommend;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfRecommendInfo;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.player.RecommendInfo;
import com.xiuxiu.core.net.message.Handler;


public class PlayerRecommendInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerNtfRecommendInfo info = new PCLIPlayerNtfRecommendInfo();
        RecommendInfo recommendInfo = player.getRecommendInfo();
        if (null != recommendInfo) {
            if (-1 != recommendInfo.getRecommendPlayerUid()) {
                Player recommendPlayer = PlayerManager.I.getPlayer(recommendInfo.getRecommendPlayerUid());
                if (null != recommendPlayer) {
                    info.diamond = recommendInfo.getDiamond();
                    info.num = recommendInfo.getNum();
                    info.recommendPlayerUid = recommendPlayer.getUid();
                    info.recommendPlayerName = recommendPlayer.getName();
                    info.recommendPlayerIcon = recommendPlayer.getIcon();
                }
            }
        }
        player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_INFO_OK, info);
        return null;
    }
}
