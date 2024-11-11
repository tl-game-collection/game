package com.xiuxiu.app.server.services.gateway.handler.robot;

import java.util.ArrayList;
import java.util.List;

import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 * 机器人好牌
 * @author lc
 *
 */
public class RobotCardHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        if (-1 != player.getRoomId()) {
        	//获取请求玩家身上的房间
            Room room = RoomManager.I.getRoom(player.getRoomId());
            if (null != room) {
            	List<IRoomPlayer> list = room.getCurrPlayers();
            	for(IRoomPlayer roomPlayer : list) {
            		if(roomPlayer.getUid()==player.getUid()) {
            			room.setDoDeal(true);
            			List<Integer> goodPlayer = room.getGoodPlayer();
            			if(goodPlayer==null) {
            				goodPlayer = new ArrayList<Integer>();
            				room.setGoodPlayer(goodPlayer);
            			}
            			room.addGoodPlayer(roomPlayer.getIndex());
            			break;
            		}
            	}
            }
        }
        return null;
    }
}
