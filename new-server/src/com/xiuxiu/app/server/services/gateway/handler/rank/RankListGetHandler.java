package com.xiuxiu.app.server.services.gateway.handler.rank;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.rank.PCLIRankReqRankList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.rank.ERankType;
import com.xiuxiu.app.server.rank.NewRankManager;
import com.xiuxiu.app.server.rank.RankData;
import com.xiuxiu.app.server.statistics.TodayStatistics;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

public class RankListGetHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRankReqRankList info = (PCLIRankReqRankList) request;

        //check something
        if (info.type < RankData.RANK_TODAY || info.type > RankData.RANK_ANTEAYER) {
            Logs.RANK.warn("%s 类型错误", player);
            player.send(CommandId.CLI_NTF_RANK_LIST_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        if (info.rankType < 0 || info.rankType >= ERankType.values().length){
            Logs.RANK.warn("%s 排行榜类型错误", player);
            player.send(CommandId.CLI_NTF_RANK_LIST_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IClub club = null;
        if (info.rankType >= ERankType.CLUB_GAME_NUM.ordinal()
                || info.rankType <= ERankType.CLUB_MAIN_GAME_WINNER.ordinal() ){
            club = ClubManager.I.getClubByUid(info.fromUid);
            if (null == club){
                Logs.RANK.warn("%s 亲友圈不存在", player);
                player.send(CommandId.CLI_NTF_RANK_LIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
                return null;
            }

            //效率问题，忽略检查是否请求人在整个主圈中
            if (!club.hasMember(player.getUid()) && !club.checkIsJoinInMainClub()){
                Logs.RANK.warn("%s 亲友圈不存在", player);
                ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
                player.send(CommandId.CLI_NTF_RANK_LIST_FAIL, ec);
                return null;
            }
        }

        if (!NewRankManager.I.lock(player.getUid())) {
            Logs.RANK.warn("%s 正在操作", player);
            player.send(CommandId.CLI_NTF_RANK_LIST_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            //deal
            ERankType rankType = ERankType.values()[info.rankType];
            String selfValue = "--";
            if (null != club) {
                if (rankType == ERankType.CLUB_GAME_NUM || rankType == ERankType.CLUB_GAME_WINNER || rankType == ERankType.CLUB_GAME_SCORE) {
                    TodayStatistics todayStatistics = club.getStatisticsByRankType(rankType,player.getUid());
                    if (null != todayStatistics){
                        if (TimeUtil.isSameDay(System.currentTimeMillis(),todayStatistics.getUpdateTime())){
                            selfValue = String.valueOf(todayStatistics.getValue());
                        }else {
                            selfValue = "0";
                        }
                    }
                }
            }
            player.send(CommandId.CLI_NTF_RANK_LIST_OK, NewRankManager.I.getPCLIRankNtfRankList(info.fromUid,rankType,info.type,info.page,player.getUid(),selfValue));
            return null;
        } finally {
            NewRankManager.I.unlock(player.getUid());
        }
    }
}

