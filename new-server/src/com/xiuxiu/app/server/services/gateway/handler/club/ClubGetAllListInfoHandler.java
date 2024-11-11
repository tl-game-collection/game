package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubListInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfAllListInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqAllListInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;

public class ClubGetAllListInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqAllListInfo info = (PCLIClubReqAllListInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.GROUP.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ALL_LIST_INFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        long finalClubId=club.getFinalClubId();
        PCLIClubNtfAllListInfo listInfo=new PCLIClubNtfAllListInfo();
        if(finalClubId==0){
            PCLIClubListInfo pCLIClubListInfo=new PCLIClubListInfo();
            pCLIClubListInfo.clubUid=club.getClubUid();
            pCLIClubListInfo.name=club.getName();
            pCLIClubListInfo.ownerUid=club.getOwnerId();
            pCLIClubListInfo.parentUid=finalClubId;
            pCLIClubListInfo.isIn=club.hasMember(player.getUid());
            listInfo.list.add(pCLIClubListInfo);
        }else{
            IClub club1=ClubManager.I.getClubByUid(finalClubId);
            List<Long> uidList=new ArrayList<>();
            club1.fillDepthChildClubUidList(uidList);
            uidList.add(finalClubId);
             for(int i=0, size = uidList.size();i<size;i++){
                 IClub club2=ClubManager.I.getClubByUid(uidList.get(i));
                 PCLIClubListInfo pCLIClubListInfo=new PCLIClubListInfo();
                 pCLIClubListInfo.clubUid=club2.getClubUid();
                 pCLIClubListInfo.name=club2.getName();
                 pCLIClubListInfo.ownerUid=club2.getOwnerId();
                 pCLIClubListInfo.parentUid=club2.getClubInfo().getParentUid();
                 pCLIClubListInfo.isIn=club2.hasMember(player.getUid());

                 listInfo.list.add(pCLIClubListInfo);
             }
        }
        player.send(CommandId.CLI_NTF_CLUB_ALL_LIST_INFO_OK, listInfo);
        return null;
    }
}
