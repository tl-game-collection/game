package com.xiuxiu.app.server.services.gateway.handler.player;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfGetWechatAssistant;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.db.*;
import com.xiuxiu.app.server.db.dao.IBaseDAO;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.system.AssistantWeChat;
import com.xiuxiu.app.server.system.LocationInfo;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.HttpUtil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerGetWeChatAssistantHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        @SuppressWarnings("unused")
        List<Long> clubUids = JsonUtil.fromJson2List(player.getClubUids(), Long.class);
        if (clubUids.isEmpty()) {
            long recUid = player.getRecommendInfo().getRecommendPlayerUid();
            if (recUid > 0) {
                Player recPlayer = PlayerManager.I.getPlayer(recUid);
                if (recPlayer != null && !StringUtil.isEmptyOrNull(recPlayer.getWechat())) {
                    PCLIPlayerNtfGetWechatAssistant resp = new PCLIPlayerNtfGetWechatAssistant();
                    resp.wechat = recPlayer.getWechat();
                    player.send(CommandId.CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_OK, resp);
                    return null;
                }
            }

            return getDefault(player);
        }
        List<IClub> list = new ArrayList<>();
        for (long groupId : clubUids) {
            IClub group = ClubManager.I.getClubByUid(groupId);
            if (group != null) {
                list.add(group);
            }
        }
        //由于多个群排序取最大的一个
        list.sort((x, y) -> {
            return (int) (y.getMemberCnt() - x.getMemberCnt());
        });
        IClub club = list.get(0);
        return getWithGroup(player, club);
    }

    private Object getWithGroup(Player player, IClub club) {
        if (club.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
            return getDefault(player);
        }
        ClubMember playerMemberInfo = club.getMember(player.getUid());
        ClubMember uplineInfo = club.getMember(playerMemberInfo.getUplinePlayerUid());
        while (uplineInfo != null) {
//            if (uplineInfo.getMemberType() == GroupMemberType.ELDER.ordinal()
//                    || uplineInfo.getMemberType() == GroupMemberType.CHIEF.ordinal()) {
            Player upline = PlayerManager.I.getPlayer(uplineInfo.getUid());
            if (upline != null && !StringUtil.isEmptyOrNull(upline.getWechat())
                    && upline.isEmpower()) {
                PCLIPlayerNtfGetWechatAssistant resp = new PCLIPlayerNtfGetWechatAssistant();
                resp.wechat = upline.getWechat();
                player.send(CommandId.CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_OK, resp);
                return null;
            }
            //当前玩家是群主(如果找到群主层级都还没有满足条件就跳出)
            if (uplineInfo.getUid() == club.getOwnerId()) {
                break;
            }
            //不是普通成员(没有上级)
            if (uplineInfo.getUplinePlayerUid() == -1 && uplineInfo.getJobType() != EClubJobType.NORMAL.ordinal()) {
                uplineInfo = club.getMember(club.getOwnerId());
            } else {
                //}
                uplineInfo = club.getMember(uplineInfo.getUplinePlayerUid());
            }
        }

        return getDefault(player);
    }

    private Object getDefault(Player player) {
        String location = player.getLng() + "," + player.getLat();
        PCLIPlayerNtfGetWechatAssistant resp = new PCLIPlayerNtfGetWechatAssistant();
        // 新增缓存表缓存数据
        LocationInfo locationInfo = DBManager.I.load(ETableType.TB_LOCATION_INFO, new IDBLoad<LocationInfo>() {
            @Override
            public String getRedisKey() {
                return ETableType.TB_LOCATION_INFO.getRedisKey() + location;
            }

            @Override
            public LocationInfo loadOne(IBaseDAO<LocationInfo> dao) {
                return DBManager.I.getLocationInfoDAO().loadByLocation(location);
            }
        });
        JSONObject regeoCode;
        if (null == locationInfo) {
            Map<String, String> params = new HashMap<>();
            params.put("key", Config.AMAP_KEY);
            params.put("location", location);
            params.put("extensions", "base");
            params.put("batch", "false");
            params.put("roadlevel", "0");
            String data = HttpUtil.get(Config.AMAP_REGEO_URL, params);
            if (StringUtil.isEmptyOrNull(data)) {
                Logs.PLAYER.warn("%s 获取地理位置失败", player);
                player.send(CommandId.CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_FAIL, ErrorCode.SERVER_NET_ERROR);
                return null;
            }
            JSONObject response = JsonUtil.fromJson(data, new TypeReference<JSONObject>() {
            });
            if (!"1".equals(response.getString("status"))) {
                Logs.PLAYER.warn("%s 获取地理位置失败", player);
                player.send(CommandId.CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_FAIL, ErrorCode.SERVER_NET_ERROR);
                return null;
            }
            regeoCode = response.getJSONObject("regeocode");
            DBManager.I.save(new Task() {
                @Override
                public void run() {
                    LocationInfo info = new LocationInfo();
                    info.setUid(UIDManager.I.getAndInc(UIDType.LOCATION_INFO));
                    info.setLocation(location);
                    info.setInfo(regeoCode.toJSONString());
                    info.setDirty(true);
                    List<String> keys = new ArrayList<>();
                    keys.add(ETableType.TB_LOCATION_INFO.getRedisKey() + info.getLocation());
                    DBManager.I.update(info, keys);
                }
            });
        } else {
            regeoCode = JsonUtil.fromJson(locationInfo.getInfo(), new TypeReference<JSONObject>() {
            });
        }
        JSONObject addressComponent = regeoCode.getJSONObject("addressComponent");
        String adcode = addressComponent.getString("adcode");
//        AssistantWeChat weChat = null;//DBManager.I.getAssistantWeChatDAO().loadByAdCode(Long.parseLong(adcode));
//        try {
//            weChat = DBManager.I.getAssistantWeChatDAO().loadByAdCode(Long.parseLong(adcode));
//        } catch (NumberFormatException e) {
//        }
//        if (null == weChat) {
//            // 根据 adCode 查询客服未找到，则根据 city 搜索
//            String city = addressComponent.getString("city");
//            weChat = DBManager.I.getAssistantWeChatDAO().loadByCity(city);
//            if (null == weChat) {
//                // 根据 city 查询客服未找到，则根据 province 搜索
//                String province = addressComponent.getString("province");
//                weChat = DBManager.I.getAssistantWeChatDAO().loadByProvince(province);
//                if (null == weChat) {
//                    // 根据 province 查询客服未找到，则搜索全国
//                    weChat = DBManager.I.getAssistantWeChatDAO().loadByAdCode(0);
//                    if (null == weChat) {
//                        Logs.PLAYER.warn("%s 未设置默认客服", player);
//                        player.send(CommandId.CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_FAIL, ErrorCode.REQUEST_NO_DEFAULT_ASSISTANT);
//                        return null;
//                    }
//                }
//            }
//        }
//        Logs.PLAYER.debug(weChat.getWeChat());
//        resp.wechat = weChat.getWeChat();
        String weChat = null;
        boolean isOfficialWechat = true;
        AssistantWeChat weChat1 = DBManager.I.getAssistantWeChatDAO().loadByAdCode(0);
        try {
            long playerTopUid = player.getRecommendInfo().getRecommendPlayerUid();
            if (playerTopUid > 0) {
                Player playerTop = PlayerManager.I.getPlayer(playerTopUid);
                if (playerTop != null) {
                    Logs.ROOM.info("我的上级ID " + playerTop.getUid() + " 是否授权 " + playerTop.isEmpower());
                    if (playerTop.isEmpower()) {
                        weChat = playerTop.getWechat();
                        isOfficialWechat = false;
                    } else {
                        long playerTopLevelUid = playerTop.getRecommendInfo().getRecommendPlayerUid();
                        if (playerTopLevelUid > 0) {
                            Player playerTopLevel = PlayerManager.I.getPlayer(playerTopLevelUid);
                            Logs.ROOM.info("我的上级的上级ID " + playerTopLevel.getUid() + " 是否授权 " + playerTopLevel.isEmpower());
                            if (playerTopLevel.isEmpower()) {
                                weChat = playerTopLevel.getWechat();
                                isOfficialWechat = false;
                            } else {
                                if (null != weChat1) {
                                    weChat = weChat1.getWeChat();
                                }
                            }
                        } else {
                            if (null != weChat1) {
                                weChat = weChat1.getWeChat();
                            }
                        }
                    }
                }
            } else {
                if (null != weChat1) {
                    weChat = weChat1.getWeChat();
                }
            }
        } catch (NumberFormatException e) {
        }
        Logs.PLAYER.debug(weChat);
        resp.wechat = weChat;
        resp.isOfficialWechat = isOfficialWechat;
        player.send(CommandId.CLI_NTF_PLAYER_GET_WECHAT_ASSISTANT_OK, resp);
        return null;
    }
}
