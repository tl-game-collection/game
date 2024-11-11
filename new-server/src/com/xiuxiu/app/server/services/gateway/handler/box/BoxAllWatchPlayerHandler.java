package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqCloseInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class BoxAllWatchPlayerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
    	Player player = (Player) owner;
        PCLIBoxReqCloseInfo info = (PCLIBoxReqCloseInfo) request;
        
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 无法获取观战名单, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_REQ_BOX_ALL_WATCH_PLAYER_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        
        // 判断是否加入主圈
        if (club.checkIsJoinInMainClub()) {
            long finalClubId = club.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法获取观战名单", player, info.clubUid);
                player.send(CommandId.CLI_REQ_BOX_ALL_WATCH_PLAYER_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        }
        
        Box box = club.getBox(info.boxUid);
        if (null == box) {
            Logs.CLUB.warn("%s 获取观战名单失败, 群:%d 包厢:%d 不存在", player, info.clubUid, info.boxUid);
            player.send(CommandId.CLI_REQ_BOX_ALL_WATCH_PLAYER_FAIL, ErrorCode.GROUP_BOX_NOT_EXISTS);
            return null;
        }
        
        ErrorCode err = box.getAllWatchPlayer(player);
        if (ErrorCode.OK == err) {
        } else {
            player.send(CommandId.CLI_REQ_BOX_ALL_WATCH_PLAYER_FAIL, err);
        }
        return null;
    }
     
}
