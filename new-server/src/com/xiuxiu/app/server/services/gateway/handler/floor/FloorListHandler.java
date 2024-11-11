package com.xiuxiu.app.server.services.gateway.handler.floor;

import java.util.Comparator;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfInfo;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfList;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorReqList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.floor.FloorManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 楼层列表信息
 * @author Administrator
 *
 */
public class FloorListHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIFloorReqList info = (PCLIFloorReqList) request;
        if (info.clubUid == 0) {
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_LIST_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 无法获取包厢状态, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_LIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        boolean flag=club.getClubType()== EClubType.GOLD;

        // 判断是否加入主圈
        if (club.checkIsJoinInMainClub()) {
            long finalClubId = club.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法获取包厢状态", player, info.clubUid);
                if(flag){
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_LIST_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
                }else{
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_LIST_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
                }
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        }else{
            if (!club.hasMember(player.getUid())) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法获取包厢状态", player, info.clubUid);
                if(flag){
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_LIST_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
                }else{
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_LIST_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
                }
                return null;
            }
        }
        
        PCLIFloorNtfList list = FloorManager.I.getFloorList(club);
        // 按照楼层uid排序
        list.list.sort(new Comparator<PCLIFloorNtfInfo>() {
            @Override
            public int compare(PCLIFloorNtfInfo o1, PCLIFloorNtfInfo o2) {
                return (int) (o1.uid - o2.uid);
            }
        });

        player.send(CommandId.CLI_NTF_CLUB_FLOOR_LIST_OK, list);
        return null;
    }
}
