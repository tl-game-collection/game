package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfMarkWarSituationInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqMarkWarSituationInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.score.BoxRoomScore;
import com.xiuxiu.core.net.message.Handler;

public class ClubSetWarSituationMarkHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqMarkWarSituationInfo info = (PCLIClubReqMarkWarSituationInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (!club.matchMemberType(EClubJobType.CHIEF, player.getUid()) && !club.matchMemberType(EClubJobType.DEPUTY, player.getUid())) {
            Logs.GROUP.warn("%s 群:%d不存在, 无法创建包厢", info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_MARK_WARSITUATION_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        BoxRoomScore boxRoomScore = DBManager.I.load(info.uid,ETableType.TB_BOX_SCORE);
        if (boxRoomScore == null) {
            Logs.GROUP.warn("%s 数据库找不到这条数据:%s", player, info);
            player.send(CommandId.CLI_NTF_CLUB_MARK_WARSITUATION_FAIL, ErrorCode.SERVER_INTERNAL_ERROR);
            return null;
        }
        boxRoomScore.setMark(info.markstate);
        boxRoomScore.setDirty(true);//是否修改数据库(false只修改redis,true修改redis和数据库)
        if (DBManager.I.update(boxRoomScore)) {
            PCLIClubNtfMarkWarSituationInfo respInfo = new PCLIClubNtfMarkWarSituationInfo();
            respInfo.clubUid = info.clubUid;
            respInfo.uid = info.uid;
            respInfo.markstate = info.markstate;

            player.send(CommandId.CLI_NTF_CLUB_MARK_WARSITUATION_OK, respInfo);
        } else {
            Logs.GROUP.warn("%s 保存数据库出错:%s", player, info);
            player.send(CommandId.CLI_NTF_CLUB_MARK_WARSITUATION_FAIL, ErrorCode.SERVER_INTERNAL_ERROR);
        }

        return null;
    }
}
