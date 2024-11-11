package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfValueChange;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetGoldInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class ClubSetGoldHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetGoldInfo info = (PCLIClubReqSetGoldInfo) request;

        //check something
        IClub optClub = ClubManager.I.getClubByUid(info.optClubUid);
        if (null == optClub || optClub.getClubType() != EClubType.GOLD) {
            Logs.CLUB.warn("%s CLUB_UID:%d 群不存在", player, info.optClubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }

        ClubMember member = optClub.getMember(player.getUid());
        if (member == null){
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        IClub otherClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == otherClub || otherClub.getClubType() != EClubType.GOLD) {
            Logs.CLUB.warn("%s CLUB_UID:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }

        ClubMember otherMember = otherClub.getMember(info.playerUid);
        if (otherMember == null){
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        Player otherPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (null == otherPlayer){
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        ErrorCode errorCode = this.checkInGame(info.changeArenaValue,info.optClubUid,info.clubUid,player,otherPlayer);
        if (errorCode != ErrorCode.OK){
            Logs.CLUB.warn("%s playerUid:%d 玩家在房间里无法上下分", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, errorCode);
            return null;
        }

        if (!checkPrivilege(optClub,player,otherClub,otherPlayer,otherMember,System.currentTimeMillis(),info.changeArenaValue)){
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, ErrorCode.CLUB_NOT_SET_GOLD_NO_PRIVILEGE);
            return null;
        }

        errorCode = this.checkGoldEnough(otherClub,optClub,info.playerUid,player.getUid(),info.changeArenaValue);
        if (errorCode != ErrorCode.OK) {
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_FAIL, errorCode);
            return null;
        }

        //圈主给自己上分fromNull=true,圈主给其他人下分fromNull=true
        boolean fromNull = checkIsFromNull(optClub,player,otherClub,otherPlayer,info.changeArenaValue);
        if (this.addPlayerArenaValue(info.changeArenaValue,optClub,player.getUid(),otherClub,info.playerUid,fromNull)){
            PCLIClubNtfValueChange result = new PCLIClubNtfValueChange();
            result.clubUid = info.clubUid;
            result.pUid = info.playerUid;
            ClubMemberExt clubMemberExt = otherClub.getMemberExt(info.playerUid,true);
            result.gold = clubMemberExt.getGold();
            result.rv = clubMemberExt.getRewardValue();
            result.addGold = info.changeArenaValue;
            player.send(CommandId.CLI_NTF_CLUB_SET_GOLD_OK,result);
        }
        return null;
    }
    /**
     * 能否设置竞技分
     * @return
     */
    private boolean checkPrivilege(IClub optClub, Player optPlayer,IClub otherClub,Player otherPlayer,ClubMember otherClubMember,long nowTime,int changeValue){
        boolean isJoinInMainClub = optClub.checkIsJoinInMainClub();
        boolean isMainClub = optClub.checkIsMainClub();

        //只有大圈主能下分 下分财务不走该流程
//        if (changeValue <= 0){
//            if (optClub.getOwnerId() != optPlayer.getUid()){
//                return false;
//            }
//            if (isJoinInMainClub && !isMainClub){
//                return false;
//            }
//            return true;
//        }


        //同圈操作
        if (optClub.getClubUid() == otherClub.getClubUid()) {
            //自己给自己上下分(未合圈只能是圈主操作自己,合圈只能是大圈主操作自己)
            if (otherPlayer.getUid() == optPlayer.getUid()) {
                if (otherClub.getOwnerId() != otherPlayer.getUid()) {
                    return false;
                }

                //圈锁定状态不能操作自己
                if (optClub.getClubInfo().getLockTime() >= nowTime) {
                    return false;
                }

                return isJoinInMainClub ? isMainClub : true;
            }

            //其他人不能给圈主上下分（除了圈主自己）
            if (otherClub.getOwnerId() == otherPlayer.getUid()) {
                return false;
            }

            //其他不能给上分财务上下分（除了圈主）
            if(checkIsUpTreasurer(otherClub,isJoinInMainClub,isMainClub,otherPlayer.getUid())){
                return optClub.getOwnerId() == optPlayer.getUid();
            }

            //上级上分下分
            if (otherClubMember.getUplinePlayerUid() == optPlayer.getUid()){
                return true;
            }

            //上分财务上下分
            if (checkIsUpTreasurer(optClub,isJoinInMainClub,isMainClub,optPlayer.getUid())){
                return true;
            }

            // 设置上级上分(除了上分财务，上级)其他人都不能上下分
            if (otherClubMember.checkOnlyUpLineSetGold()){
                return false;
            }

            //圈主上下分
            if (optClub.getOwnerId() == optPlayer.getUid()){
                return true;
            }

            //副圈主上下分
            if (optClub.matchMemberType(EClubJobType.DEPUTY,optPlayer.getUid())){
                return true;
            }

            return false;
        }else{
            if (!isMainClub){
                return false;
            }

            if (optClub.getClubUid() != otherClub.getFinalClubId()){
                return false;
            }

            //上分财务上下分
            if (checkIsUpTreasurer(optClub,true, true,optPlayer.getUid())){
                return true;
            }

            //大圈圈主上下分(只能给子圈圈主上下分)
            if (optClub.getOwnerId() == optPlayer.getUid() && otherClub.getOwnerId() == otherPlayer.getUid()){
                return true;
            }
            return false;
        }
    }



    private boolean checkIsUpTreasurer(IClub club,boolean isJoinInMainClub,boolean isMainClub,long playerUid){
        if (isJoinInMainClub && !isMainClub){
            return false;
        }
        return club.checkIsUpTreasurer(playerUid);
    }

    public static ErrorCode checkInGame(int changeArenaValue, long optClubUid, long clubUid, Player optPlayer, Player otherPlayer){
        if (changeArenaValue == 0){
            return ErrorCode.OK;
        }

        long checkClubUid = optClubUid;
        Player checkPlayer = optPlayer;
        ErrorCode errorCode = ErrorCode.SET_GOLD_INC_ERROR_IN_GAME;

        if (changeArenaValue < 0 ){
            checkClubUid = clubUid;
            checkPlayer = otherPlayer;
            errorCode = ErrorCode.SET_GOLD_DEC_ERROR_IN_GAME;
        }

        if (checkPlayer.getRoomId() <= 0) {
            return ErrorCode.OK;
        }

        Room room = RoomManager.I.getRoom(checkPlayer.getRoomId());
        if (room == null) {
            return ErrorCode.OK;
        }

        IBoxOwner boxOwner = room.getBoxOwner();
        if (boxOwner == null){
            return ErrorCode.OK;
        }

        if (boxOwner instanceof IClub && ((IClub) boxOwner).getEnterFromClubUid(checkPlayer.getUid()) == checkClubUid) {
            Logs.CLUB.warn("%s playerUid:%d 在游戏中不能上下分", optPlayer, otherPlayer.getUid());
            return errorCode;
        }

        return ErrorCode.OK;
    }

    private ErrorCode checkGoldEnough(IClub otherClub,IClub optClub,long otherPlayerUid,long optPlayerUid,int changeArenaValue){
        if (changeArenaValue == 0){
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        if (changeArenaValue < 0){
            return otherClub.hasGold(otherPlayerUid,-changeArenaValue) ? ErrorCode.OK : ErrorCode.CLUB_NOT_SET_GOLD_DEL_NOT_ENOUGH;
        }

        if (otherClub.getClubUid() == optClub.getClubUid() && otherPlayerUid == optPlayerUid){
            return ErrorCode.OK;
        }
        return optClub.hasGold(optPlayerUid,changeArenaValue) ? ErrorCode.OK : ErrorCode.CLUB_NOT_SET_GOLD_ADD_NOT_ENOUGH;
    }

    /**
     * 圈主在主圈给主圈的自己上分fromNull=true,圈主在主圈给其他人下分fromNull=true
     * @param optClub
     * @param optPlayer
     * @param otherClub
     * @param otherPlayer
     * @return
     */
    private boolean checkIsFromNull(IClub optClub,Player optPlayer,IClub otherClub,Player otherPlayer,int changeArenaValue){
        if (changeArenaValue == 0){
            return false;
        }
        //是圈主
        if (optClub.getOwnerId() != optPlayer.getUid()){
            return false;
        }
        //如果合圈必须是主圈
        if (optClub.checkIsJoinInMainClub() && !optClub.checkIsMainClub()){
            return false;
        }
        //上分
        if (changeArenaValue > 0){
            //必须是同圈操作主圈自己
            if (otherClub.getClubUid() != optClub.getClubUid()){
                return false;
            }
            if (otherPlayer.getUid() != optPlayer.getUid()){
                return false;
            }
        }

        return true;
    }

    private boolean addPlayerArenaValue(int changeArenaValue,IClub optClub,long optPlayerUid,IClub club,long playerUid,boolean fromNull){
        boolean rs = false;
        //上分
        if (changeArenaValue > 0) {
            //先扣管理的钱
            rs = fromNull ? true : optClub.addMemberClubGold(optPlayerUid, -changeArenaValue, playerUid, EClubGoldChangeType.INC_MANAGER_DEC);
            if (rs) {
                //再加玩家的钱
                rs = club.addMemberClubGold(playerUid, changeArenaValue, optPlayerUid, fromNull ? EClubGoldChangeType.INC_FROM_NULL : EClubGoldChangeType.INC_MANAGER);
                if (!rs && !fromNull) {
                    //玩家加钱失败返还管理
                    if(!optClub.addMemberClubGold(optPlayerUid, changeArenaValue, playerUid, EClubGoldChangeType.LEAGUE_REBACK)){
                        Logs.CLUB.warn("上分失败 clubUid:%d playerUid:%d otherPlayerUid:%d value:%d", club.getClubUid(),optPlayerUid,playerUid,changeArenaValue);
                    }
                }
            }
        } else {
            //先扣玩家的钱
            rs = club.addMemberClubGold(playerUid, changeArenaValue,optPlayerUid,fromNull ? EClubGoldChangeType.DEC_TO_NULL : EClubGoldChangeType.DEC_MANAGER);
            if (rs) {
                //再加管理的钱
                //rs = fromNull ? true :  optClub.addMemberClubGold(optPlayerUid, -changeArenaValue, playerUid, EClubGoldChangeType.DEC_MANAGER_INC);
                rs =  optClub.addMemberClubGold(optPlayerUid, -changeArenaValue, playerUid, EClubGoldChangeType.DEC_MANAGER_INC);
                      
                if (!rs) {
                    //管理加钱失败返还玩家
                    if (!club.addMemberClubGold(playerUid, -changeArenaValue, optPlayerUid, EClubGoldChangeType.LEAGUE_REBACK)){
                        Logs.CLUB.warn("下分失败 clubUid:%d playerUid:%d otherPlayerUid:%d value:%d", club.getClubUid(),optPlayerUid,playerUid,changeArenaValue);
                    }
                }
            }
        }
        return rs;
    }
}