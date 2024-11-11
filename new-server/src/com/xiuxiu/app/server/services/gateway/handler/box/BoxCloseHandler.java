package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqCloseInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 关闭包厢
 * 
 * @author Administrator
 *
 */
public class BoxCloseHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqCloseInfo info = (PCLIBoxReqCloseInfo) request;
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 关闭包厢失败, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!fromClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法关闭包厢", player, info.clubUid);
            if(fromClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法关闭包厢", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
            if(!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid())
                    || club.checkIsManager(player.getUid()))){
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_CLOSE_BOX);
                return null;
            }

        } else {
            club = fromClub;
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_CLOSE_BOX);
                return null;            }
        }
        Box box = club.getBox(info.boxUid);
        if (null == box) {
            Logs.CLUB.warn("%s 关闭包厢失败, 群:%d 包厢:%d 不存在", player, info.clubUid, info.boxUid);
            player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.GROUP_BOX_NOT_EXISTS);
            return null;
        }
        if (!BoxManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = BoxManager.I.close(player, club, box);
            if (ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, err);
            }
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
