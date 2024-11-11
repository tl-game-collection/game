package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfSetDivideLineInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetDivideLine;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

public class ClubSetDivideLineHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetDivideLine info = (PCLIClubReqSetDivideLine) request;

        //check something
        if (info.divide < 0 || info.divide > 100
                || info.divideLine < 0 || info.divideLine > 100) {
            Logs.CLUB.warn("设置抽成比例数值异常 %s", info);
            player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        if (info.tagPlayerUid == player.getUid()) {
            Logs.CLUB.warn("%s 没有修改群成员竞技场分成的权限", player);
            player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_CHANGE_PRIVILEGE);
            return null;
        }

        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        ClubMember clubMember = club.getMember(info.tagPlayerUid);
        if (null == clubMember) {
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ec);
            return null;
        }

        if (!club.matchMemberType(EClubJobType.CHIEF,player.getUid())){
//            if (clubMember.getUplinePlayerUid() != player.getUid()){
//                Logs.CLUB.warn("%s 没有修改群成员竞技场分成的权限", player);
//                player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_CHANGE_PRIVILEGE);
//                return null;
//            }
//
//            if (info.divideLine != clubMember.getDivideLine()){
//                Logs.CLUB.warn("%s 没有修改群成员竞技场分成的权限", player);
//                player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_CHANGE_PRIVILEGE);
//                return null;
//            }

            ClubMember selfClubMember = club.getMember(player.getUid());
            if(selfClubMember==null) {
                player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
                return null;
            }
            
            //直属
//            if (info.divide  >= selfClubMember.getDivide() && info.divide != 0 ){
//                Logs.CLUB.warn("%s 设置抽成比例数值异常 %s", player, info);
//                player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
//                return null;
//                //info.divide = clubMember.getDivide();
//            }
            
            
            info.divide = clubMember.getDivide();
            //一条线
            if (info.divideLine >= selfClubMember.getDivideLine()){
                Logs.CLUB.warn("%s 设置抽成比例数值异常 %s", player, info);
                player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
                return null;
            }
        }

        //deal
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            clubMember.changeDivideAndDivideLine(info.divide, info.divideLine);
            clubMember.save();

            player.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_OK, new PCLIClubNtfSetDivideLineInfo(info.clubUid, info.tagPlayerUid, info.divide, info.divideLine));

            Player tagPlayer= PlayerManager.I.getOnlinePlayer(info.tagPlayerUid);
            if (tagPlayer != null) {
                tagPlayer.send(CommandId.CLI_NTF_CLUB_SET_DIVIDE_LINE_OK, new PCLIClubNtfSetDivideLineInfo(info.clubUid, info.tagPlayerUid, info.divide, info.divideLine));
            }
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
