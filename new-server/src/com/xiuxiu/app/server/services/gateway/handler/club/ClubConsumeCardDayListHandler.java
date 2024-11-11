package com.xiuxiu.app.server.services.gateway.handler.club;

import java.util.ArrayList;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubConsumeCard;
import com.xiuxiu.app.protocol.client.club.PCLIClubConsumeCardDayList;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqConsumeCardDayList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecordDetail;
import com.xiuxiu.core.net.message.Handler;

/**
 * 请求房卡消耗统计每天列表
 * 
 * @author Administrator
 *
 */
public class ClubConsumeCardDayListHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqConsumeCardDayList info = (PCLIClubReqConsumeCardDayList) request;
        if (0 == info.id || info.page < 0 || info.size < 0) {
            player.send(CommandId.CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }

        IClub club = ClubManager.I.getClubByUid(info.id);
        if (null == club) {
            Logs.GROUP.warn("%s clubUid:%d 群不存在", player, info.id);
            player.send(CommandId.CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.GROUP.warn("%s groupUid:%d 不在群里", player, info.id);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        if (!club.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
            Logs.GROUP.warn("%s 没有查看房卡消耗统计列表的权限", player);
            player.send(CommandId.CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.GROUP.warn("%s info:%s 正在操作", player, info);
            player.send(CommandId.CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            int fromIndex = info.page * info.size;
            int toIndex = (info.page + 1) * info.size;
            int totalCount = DBManager.I.getMoneyExpendRecordDetailDao().countByClubUidAndTime(info.id, info.time);
            if (toIndex > totalCount) {
                toIndex = totalCount;
            }
            int totalPage = totalCount / info.size;
            if (totalPage % info.size != 0) {
                totalPage++;
            }

            totalPage = totalPage == 0 ? 1 : totalPage;

            PCLIClubConsumeCardDayList result = new PCLIClubConsumeCardDayList();
            result.page = info.page;
            result.totalPage = totalPage;
            List<MoneyExpendRecordDetail> list = DBManager.I.getMoneyExpendRecordDetailDao().getByClubUidAndTime(info.id,
                    info.time, fromIndex, toIndex);
            if (list != null) {
                List<PCLIClubConsumeCard> tempList = new ArrayList<PCLIClubConsumeCard>();
                for (MoneyExpendRecordDetail detail : list) {
                    PCLIClubConsumeCard temp = new PCLIClubConsumeCard();
                    temp.value = detail.getValue();
                    temp.key = detail.getPlayerName();
                    tempList.add(temp);
                }
                result.data = tempList;
            }
            player.send(CommandId.CLI_NTF_CLUB_CONSUME_CARD_DAY_LIST_OK, result);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}