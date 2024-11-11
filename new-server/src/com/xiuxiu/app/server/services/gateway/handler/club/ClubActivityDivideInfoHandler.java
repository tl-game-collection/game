package com.xiuxiu.app.server.services.gateway.handler.club;

import java.util.ArrayList;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfActivityDivideInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfActivityDivideInfoItem;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqActivityDivideInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.ClubActivity;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.activity.divide.ClubActivityDivideData;
import com.xiuxiu.app.server.club.activity.divide.ClubActivityDivideDataItem;
import com.xiuxiu.app.server.club.constant.EClubActivityType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 获取奖励分成获取比例相关数据
 * 
 * @author Administrator
 *
 */
public class ClubActivityDivideInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqActivityDivideInfo info = (PCLIClubReqActivityDivideInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.id);
        if (null == club) {
            Logs.CLUB.warn("%s 亲友圈不存在:%s", player, info);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 不在群里", player, info.id);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
//        if (!club.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
//            Logs.GROUP.warn("%s 没有修改联盟奖励分成获取比例的权限", player);
//            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_FAIL,
//                    ErrorCode.GROUP_NOT_PRIVILEGE_MANAGER_SERVICE_CHARGE);
//            return null;
//        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, info);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            PCLIClubNtfActivityDivideInfo result = new PCLIClubNtfActivityDivideInfo();
            ClubActivity tempActivity = ClubActivityManager.I.getActivity(info.id, EClubActivityType.DIVIDE);
            if (null == tempActivity) {

            } else {
                ClubActivityDivideData data = tempActivity.getDivideData();
                ClubActivityDivideDataItem base = data.getBase();
                List<ClubActivityDivideDataItem> items = data.getItems();
                PCLIClubNtfActivityDivideInfoItem tempData = new PCLIClubNtfActivityDivideInfoItem();
                tempData.setLine(base.getLine());
                tempData.setMember(base.getMember());
                tempData.setNeedValue(base.getNeedValue());
                result.base = tempData;
                List<PCLIClubNtfActivityDivideInfoItem> tempList = new ArrayList<>();
                for (ClubActivityDivideDataItem temp : items) {
                    tempData = new PCLIClubNtfActivityDivideInfoItem();
                    tempData.setLine(temp.getLine());
                    tempData.setMember(temp.getMember());
                    tempData.setNeedValue(temp.getNeedValue());
                    tempList.add(tempData);
                }
                result.items = tempList;
                result.open = data.isOpen();
            }
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_INFO_OK, result);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}