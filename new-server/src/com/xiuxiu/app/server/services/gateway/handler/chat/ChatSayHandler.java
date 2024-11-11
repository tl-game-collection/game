package com.xiuxiu.app.server.services.gateway.handler.chat;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.chat.PCLIChatNtfSayOkInfo;
import com.xiuxiu.app.protocol.client.chat.PCLIChatReqSayInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.ChatManager;
import com.xiuxiu.app.server.chat.EChatContentType;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;

public class ChatSayHandler implements Handler {
	@Override
	public Object handler(Object owner, Object request) {
		Player player = (Player) owner;
		PCLIChatReqSayInfo info = (PCLIChatReqSayInfo) request;
		if (info.messageType < 0 || info.messageType >= EChatContentType.values().length) {
			Logs.CHAT.warn("%s 无效消息内容类型, 聊天请求失败", player, info.toUid);
			player.send(CommandId.CLI_NTF_CHAT_SAY_FAIL, ErrorCode.REQUEST_INVALID_DATA);
			return null;
		}
		if (2 == info.messageType) {
			if (!player.hasClub(info.toUid)) {
				Logs.CHAT.warn("%s 你不在groupUid;%s 这个群里, 聊天请求失败", player, info.toUid);
				player.send(CommandId.CLI_NTF_CHAT_SAY_FAIL, ErrorCode.REQUEST_INVALID_DATA);
				return null;
			}
            IClub club = ClubManager.I.getClubByUid(info.toUid);

			if (null == club) {
				Logs.CHAT.warn("%s 群groupUid;%s 不存在, 聊天请求失败", player, info.toUid);
				player.send(CommandId.CLI_NTF_CHAT_SAY_FAIL, ErrorCode.REQUEST_INVALID_DATA);
				return null;
			}
			EChatContentType type = EChatContentType.values()[info.messageType];
			if (EChatContentType.NORMAL == type || EChatContentType.IMAGE == type || EChatContentType.VOICE == type) {
			    //TODO 禁言时间限制
//				long now = System.currentTimeMillis();
//				if (now < club.getClubInfo().getMuteTime(player.getUid())) {
//					Logs.CHAT.warn("%s 群groupUid;%s 不存在, 聊天请求失败", player, info.toUid);
//					player.send(CommandId.CLI_NTF_CHAT_SAY_FAIL, ErrorCode.PLAYER_MUTE);
//					return null;
//				}
			}
		}

		if (info.messageType == 7) {
			IClub club = ClubManager.I.getClubByUid(info.toUid);
			if (null == club) {
				Logs.CHAT.warn("%s 群groupUid;%s 不存在, 聊天请求失败", player, info.toUid);
				player.send(CommandId.CLI_NTF_CHAT_SAY_FAIL, ErrorCode.REQUEST_INVALID_DATA);
				return null;
			}
			//创建玩法桌子
			if (info.contentType == 9) {
				if (club.checkIsJoinInMainClub()) {
					IClub rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
					List<Long> allClubUid = new ArrayList<>();
					rootClub.fillDepthChildClubUidList(allClubUid);
					allClubUid.add(0,rootClub.getClubUid());
					for (int i = 0; i < allClubUid.size(); i++) {
						IClub tempClub = ClubManager.I.getClubByUid(allClubUid.get(i));
						if (tempClub == null) {
							continue;
						}
						long msgUid = ChatManager.I.chat(player, (byte) info.messageType, tempClub.getClubUid(), (byte) info.contentType, info.message,info.leagueUid,info.groupUid,info.toGroupUid);
						if (-1 == msgUid) {
							player.send(CommandId.CLI_NTF_CHAT_SAY_FAIL, ErrorCode.REQUEST_INVALID_DATA);
						} else {
							PCLIChatNtfSayOkInfo sayOkInfo = new PCLIChatNtfSayOkInfo();
							sayOkInfo.opaque = info.opaque;
							sayOkInfo.msgUid = msgUid;
							player.send(CommandId.CLI_NTF_CHAT_SAY_OK, sayOkInfo);
						}
					}
					return null;
				}
			}
		}

		long msgUid = ChatManager.I.chat(player, (byte) info.messageType, info.toUid, (byte) info.contentType, info.message,info.leagueUid,info.groupUid,info.toGroupUid);
		if (-1 == msgUid) {
			player.send(CommandId.CLI_NTF_CHAT_SAY_FAIL, ErrorCode.REQUEST_INVALID_DATA);
		} else {
			PCLIChatNtfSayOkInfo sayOkInfo = new PCLIChatNtfSayOkInfo();
			sayOkInfo.opaque = info.opaque;
			sayOkInfo.msgUid = msgUid;
			player.send(CommandId.CLI_NTF_CHAT_SAY_OK, sayOkInfo);
		}
		return null;
	}
}
