package com.xiuxiu.app.server.services.gateway.handler.forbid;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfAdd;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfInfo;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidReqAdd;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.forbid.Forbid;
import com.xiuxiu.app.server.forbid.ForbidManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class ForbidAddHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIForbidReqAdd info = (PCLIForbidReqAdd) request;
        if (null == info.playerUids || info.playerUids.size() > 3 || info.playerUids.size() < 2){
            Logs.CLUB.warn("参数错误");
            player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        Forbid forbid = new Forbid();
        List<Player> tempPlayers = new ArrayList<>();
        IClub club= ClubManager.I.getClubByUid(info.clubUid);
        if (null == club){
            Logs.CLUB.warn("%s 亲友圈或比赛场:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (club.checkIsMainClub()) {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.checkIsManager(player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或管理员, 权限不足, 无法添加防作弊", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        } else {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或者副圈主, 权限不足, 无法添加防作弊", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        }
        boolean flag=false;
            for (long tempPlayerUid : info.playerUids){
                Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUid);
                tempPlayers.add(tempPlayer);
                if(club.checkIsMainClub()){
                    List<Long> uidList=new ArrayList<>();
                    club.fillDepthChildClubUidList(uidList);
                    uidList.add(club.getClubUid());
                    for (int i = 0; i < uidList.size(); i++) {
                        long uid = uidList.get(i);
                        IClub club1 = ClubManager.I.getClubByUid(uid);
                        if (club1.hasMember(tempPlayerUid)||club.hasMember(tempPlayerUid)){
                            flag=true;

                        }
                    }
                }else{
                    if (club.hasMember(tempPlayerUid)){
                        flag=true;
                    }
                }

            }
            if(!flag){
                //Logs.GROUP.warn("%d 玩家不再群:%d里", tempPlayerUid, info.clubUid);
                if(club.getClubType()==EClubType.GOLD){
                    player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
                }else{
                    player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
                }
                return null;
            }
            forbid.setClubType(club.getClubType().getType());

        forbid.setClubUid(info.clubUid);
        forbid.setPlayerUids(JsonUtil.toJson(info.playerUids));
        if (ForbidManager.I.isExist(EClubType.getType(forbid.getClubType()),forbid.getClubUid(),forbid.getPlayerUidList())){
            Logs.CLUB.debug("%s 已经存在屏蔽:%d", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.FORBID_IS_EXISTS);
            return null;
        }

        forbid.setFlag(club.checkIsMainClub());
        forbid.setUid(UIDManager.I.getAndInc(UIDType.FORBID));
        forbid.setDirty(true);
        forbid.save();
        ForbidManager.I.add(forbid);

        PCLIForbidNtfAdd result = new PCLIForbidNtfAdd();
        result.clubUid = info.clubUid;
        result.info = new PCLIForbidNtfInfo();
        result.info.uid = forbid.getUid();
        for (Player tempPlayer : tempPlayers){
            if(null == tempPlayer){
                continue;
            }
            result.info.players.add(tempPlayer.getPlayerSmallInfo());
        }

        player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_OK, result);
        return null;
    }
}