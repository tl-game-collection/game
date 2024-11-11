package com.xiuxiu.app.server.services.gateway.handler.box;

import java.util.Iterator;
import java.util.Map;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfBoxListInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqListInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.constant.EBoxState;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class BoxListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqListInfo info = (PCLIBoxReqListInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 无法获取包厢列表, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_LIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        // 判断是否请求主圈,判断该玩家是否属于主圈的成员圈中的某成员玩家(只能获取主圈信息列表)
        if (!club.hasMember(player.getUid())) {
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_BOX_LIST_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_BOX_LIST_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        // 判断是否加入主圈
        if (club.checkIsJoinInMainClub()) {
            long finalClubId = club.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法获取包厢列表", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_LIST_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        }
        Map<Long, Box> allBox = club.getAllBox();
        Iterator<Map.Entry<Long, Box>> it = allBox.entrySet().iterator();
        PCLIBoxNtfBoxListInfo list = new PCLIBoxNtfBoxListInfo();
        while (it.hasNext()) {
            Box box = it.next().getValue();
            if (EBoxState.INIT == box.getState()) {
                list.list.add(box.getBoxInfo());
            }
        }
        player.send(CommandId.CLI_NTF_BOX_LIST_OK, list);
        return null;
    }
}
