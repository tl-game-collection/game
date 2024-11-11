package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfGetDownGoldTime;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqGetDownGoldTime;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 获取club成员在本圈中下分所需相关参数
 * @date 2020/1/19 14:48
 * @author luocheng
 */
public class PlayerGetDownGoldTimeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqGetDownGoldTime info = (PCLIPlayerReqGetDownGoldTime) request;
        //----------检查and判断----------begin
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_PLAYER_GET_DOWN_GOLD_TIME_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s player:%d不在圈中", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_GET_DOWN_GOLD_TIME_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            return null;
        }
        //----------检查and判断----------end

        //----------数据and消息----------begin
        PCLIPlayerNtfGetDownGoldTime resp = new PCLIPlayerNtfGetDownGoldTime();
        resp.clubUid = info.clubUid;
        long lastTime = club.getMemberExt(player.getUid(), true).getOrderCreateTime();
        long timeSpace = System.currentTimeMillis() - lastTime;
        //时间是否小于30min
        resp.time = timeSpace <= Constant.DOWN_GOLD_TIME_SPACE && timeSpace > 0 ? Constant.DOWN_GOLD_TIME_SPACE - timeSpace : 0L;
        ClubInfo clubInfo = club.getClubInfo();
        if (club.checkIsJoinInMainClub()){
            if(!club.checkIsMainClub()){
                IClub mainClub = ClubManager.I.getClubByUid(club.getFinalClubId());
                if (mainClub != null){
                    clubInfo = mainClub.getClubInfo();
                }
            }
        }
        resp.isFreeFirst = clubInfo.checkTreasurerFirstFree();
        resp.serviceChargePercentage = clubInfo.getTreasurerServiceChargePercentage();
        resp.canDownGoldMinValue = clubInfo.getTreasurerCanDownGoldMinValue();
        resp.desc = clubInfo.getTreasurerDesc();
        resp.lastTime = lastTime;

        player.send(CommandId.CLI_NTF_PLAYER_GET_DOWN_GOLD_TIME_OK, resp);
        //----------数据and消息----------end
        return null;
    }
}
