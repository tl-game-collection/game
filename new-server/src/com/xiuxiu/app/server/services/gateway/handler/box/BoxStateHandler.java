package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfStateInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqStateInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxRoomStateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.List;

public class BoxStateHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqStateInfo info = (PCLIBoxReqStateInfo) request;
        if (info.page < 0 || info.size <= 0) {
            player.send(CommandId.CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 无法获取包厢状态, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        // 判断是否加入主圈
        if (club.checkIsJoinInMainClub()) {
            long finalClubId = club.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法获取包厢状态", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        }else{
            if (!club.hasMember(player.getUid())) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法获取包厢状态", player, info.clubUid);
                if(club.getClubType()== EClubType.GOLD){
                    player.send(CommandId.CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
                }else{
                    player.send(CommandId.CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
                }
                return null;
            }
        }
//        if (!BoxManager.I.lock(player.getUid())) {
//            Logs.GROUP.warn("%s groupUid:%d 正在操作", player, info.clubUid);
//            player.send(CommandId.CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.PLAYER_BUSY);
//            return null;
//        }
        try {
            Floor floor = club.getFloor(info.floorUid);
            if (null == floor) {
//                Logs.GROUP.warn("%s clubUid:%d 楼层:%d 不存在, 无法回去包厢状态", player, info.clubUid, info.floorUid);
                player.send(CommandId.CLI_NTF_BOX_STATE_INFO_FAIL, ErrorCode.FLOOR_NOT_EXISTS);
                return null;
            }
            PCLIBoxNtfStateInfo stateInfo = new PCLIBoxNtfStateInfo();
            stateInfo.gameType = info.gameType;
            stateInfo.clubUid = club.getClubUid();
            stateInfo.floorUid = info.floorUid;
            stateInfo.page = info.page;
            stateInfo.allPlayerCnt = floor.getAllPlayerCnt(club);
            List<PCLIBoxRoomStateInfo> tempList = null;
            if (info.drawType == 0){
                tempList = floor.getAndSetShowBoxList(club, info.flag, info.type, info.gameType);
            }else {
                tempList = floor.getAndSetShowBoxList(club, info.flag, info.gameType, info.gameSubType, info.endPoint,info.playType);
            }
            if (info.gameType <= 0 && info.drawType == 1){
                stateInfo.page = 0;
                stateInfo.totalPage = 1;
                stateInfo.list = tempList;
            }else{
                int count = null == tempList ? 0 : tempList.size();
                if (count > 0) {
                    int fromIndex = info.page * info.size;
                    int toIndex = (info.page + 1) * info.size;
                    if (toIndex > count) {
                        toIndex = count;
                        if (fromIndex > toIndex) {
                            stateInfo.page = 0;
                            fromIndex = 0;
                            toIndex = toIndex > info.size ? count :toIndex;
                        }
                    }
                    int totalPage = count / info.size;
                    if (totalPage % info.size != 0) {
                        totalPage++;
                    }

                    totalPage = totalPage == 0 ? 1 : totalPage;

                    stateInfo.totalPage = totalPage;

                    stateInfo.list = tempList.subList(fromIndex, toIndex);
                }
            }

            player.send(CommandId.CLI_NTF_BOX_STATE_INFO_OK, stateInfo);
        } finally {
//            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
