package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfUpGold;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqUpGold;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 请求财务上分
 * @date 2020/1/9 10:57
 * @author luocheng
 */
public class ClubMemberUpGoldHandler implements Handler {
    private static long updateTime = 0;
    private static final int INTERVAL_TIME = 300000;
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqUpGold info = (PCLIPlayerReqUpGold) request;

        //check something
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_PLAYER_UP_GOLD_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }

        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s player:%d不在圈中", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_UP_GOLD_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            return null;
        }

        IClub rootClub = club;
        //合过圈
        if (club.checkIsJoinInMainClub()) {
            if (!club.checkIsMainClub()) {
                rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
            }
        }

        //财务自身不能上分
        if (rootClub.getClubInfo().getUpGoldTreasurer().contains(player.getUid())) {
            Logs.CLUB.warn("%s player:%d财务不能上分", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_UP_GOLD_FAIL, ErrorCode.PLAY_TREASURER_NO_UP_GOLD);
            return null;
        }

        if (rootClub.getClubInfo().getUpGoldTreasurer().size() == 0) {
            Logs.CLUB.warn("%s club:%d本圈没有设置上分财务", player, rootClub.getClubUid());
            player.send(CommandId.CLI_NTF_PLAYER_UP_GOLD_FAIL, ErrorCode.CLUB_NOT_SET_UP_GOLD_TREASURER);
            return null;
        }

        List<PCLIPlayerNtfUpGold.TreasurerPlayer> upGoldTreasurerPlayers = new ArrayList<>(); //上分财务
        for (Long tempPlayerUid : rootClub.getClubInfo().getUpGoldTreasurer()) {
            Player tempPlayer = PlayerManager.I.getOnlinePlayer(tempPlayerUid);
            if (tempPlayer == null) {
                continue;
            }
            PCLIPlayerNtfUpGold.TreasurerPlayer tempInfo =new PCLIPlayerNtfUpGold.TreasurerPlayer(tempPlayer.getUid(),tempPlayer.getName(),tempPlayer.getIcon(),rootClub.getGold(tempPlayer.getUid()));
            ClubMember clubMember = rootClub.getMember(tempInfo.playerUid);
            tempInfo.desc = clubMember == null ? "" : clubMember.getTreasurerDesc();
            upGoldTreasurerPlayers.add(tempInfo);
            /*
            if (respPlayer == null) {
                respPlayer = tempPlayer;
            } else {
                long respPlayerTime = rootClub.getMemberExt(respPlayer.getUid(),true).getUpGoldOrderLastTime();
                long tempPlayerTime = rootClub.getMemberExt(tempPlayer.getUid(),true).getUpGoldOrderLastTime();
                if (respPlayerTime > tempPlayerTime) {
                    respPlayer = tempPlayer;
                }
            }*/
        }
        if (upGoldTreasurerPlayers.size() <= 0) {
            Logs.CLUB.warn("%s player:%d没有上分财务在线", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_UP_GOLD_FAIL, ErrorCode.CLUB_NOT_UP_GOLD_TREASURER_ONLINE);
            return null;
        }

        IClub finalRootClub = rootClub;
        Collections.sort(upGoldTreasurerPlayers, new Comparator<PCLIPlayerNtfUpGold.TreasurerPlayer>() {
            @Override
            public int compare(PCLIPlayerNtfUpGold.TreasurerPlayer o1, PCLIPlayerNtfUpGold.TreasurerPlayer o2) {
                if(finalRootClub.getMemberExt(o1.playerUid,true).getUpGoldOrderLastTime() >= finalRootClub.getMemberExt(o2.playerUid,true).getUpGoldOrderLastTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        long nowTime = System.currentTimeMillis();
        if (nowTime >= updateTime) {
            rootClub.getMemberExt(upGoldTreasurerPlayers.get(0).playerUid, true).setUpGoldOrderLastTime(nowTime);
            updateTime = nowTime + INTERVAL_TIME;
        }

        PCLIPlayerNtfUpGold resp = new PCLIPlayerNtfUpGold();
        resp.clubUid = info.clubUid;
        resp.fromClubUid = rootClub.getClubUid();
        resp.players = upGoldTreasurerPlayers;
        player.send(CommandId.CLI_NTF_PLAYER_UP_GOLD_OK, resp);


        return null;
    }
}
