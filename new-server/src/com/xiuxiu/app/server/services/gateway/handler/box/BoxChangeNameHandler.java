package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqChangeNameInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 修改包厢名字
 * 
 * @author Administrator
 *
 */
public class BoxChangeNameHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqChangeNameInfo info = (PCLIBoxReqChangeNameInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 修改包厢名字失败, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_MODIFY_RULE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (null == info.boxName) {
            return null;
        }
        info.boxName = info.boxName.trim();
        if (info.boxName.length() > 20) {
            return null;
        }
        Box box = club.getBox(info.boxUid);
        if (null == box) {
            Logs.CLUB.warn("%s 群:%d 包厢:%d 不存在, 无法修改包厢名字", player, info.clubUid, info.boxUid);
            player.send(CommandId.CLI_NTF_BOX_MODIFY_RULE_FAIL, ErrorCode.GROUP_BOX_NOT_EXISTS);
            return null;
        }
        if (!BoxManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CHANGE_NAME_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = BoxManager.I.modifyName(player, club, box, info.boxName);
            if (ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_BOX_CHANGE_NAME_FAIL, err);
            }
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
