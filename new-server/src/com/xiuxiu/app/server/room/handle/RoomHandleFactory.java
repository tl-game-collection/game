package com.xiuxiu.app.server.room.handle;

import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.handle.impl.*;
import com.xiuxiu.app.server.room.handle.impl.hundred.BaccaratHundredRoomHandle;
import com.xiuxiu.app.server.room.handle.impl.hundred.LhdHundredRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;

/**
 * 房间处理简单工厂类
 * 
 * @author Administrator
 *
 */
public final class RoomHandleFactory {

    /**
     * 获取房间处理对象
     * 
     * @param room
     * @param extraParams
     * @return
     */
    public static IRoomHandle createRoomHandle(IRoom room, Object... extraParams) {
        ERoomType roomType = room.getRoomType();
        switch (roomType) {
        case NORMAL:
            return new RoomHandle(room);
        case BOX:
            Box box = (Box) extraParams[0];
            int gameType = room.getGameType();
            int gameSubType = room.getGameSubType();
            if (EBoxType.NORMAL.match(box.getBoxType()) || EBoxType.CUSTOM.match(box.getBoxType())) {
                if (gameType == GameType.GAME_TYPE_PAIGOW && gameSubType == 1) {
                    // 加锅牌九
                    return new BoxArenaPawiGowHotRoomHandle(room, box);
                }else  if (gameType == GameType.GAME_TYPE_PAIGOW && gameSubType == 0) {
                    return new BoxArenaPawiGowRoomHandle(room, box);
                }else if (gameType == GameType.GAME_TYPE_COW && gameSubType == 1){
                    return new BoxArenaCowHotRoomHandle(room, box);
                } else{
//                    return new ServiceChargeBoxRoomHandle(room, box);
                    return new MutilServiceChargeBoxRoomHandle(room, box);
                }
            } else if (EBoxType.ARENA.match(box.getBoxType())) {
                if (gameType == GameType.GAME_TYPE_PAIGOW && gameSubType == 1) {
                    // 加锅牌九
                    return new BoxArenaPawiGowHotRoomHandle(room, box);
                }else if (gameType == GameType.GAME_TYPE_COW && gameSubType == 1){
                    return new BoxArenaCowHotRoomHandle(room, box);
                } else {
                    if (gameType == GameType.GAME_TYPE_PAIGOW) {
                        return new BoxArenaPawiGowRoomHandle(room, box);
                    } else if (gameType == GameType.GAME_TYPE_THIRTEEN) {
                        return new BoxArenaThirteenRoomHandle(room, box);
                    } else {
                        return new BoxArenaRoomHandle(room, box);
                    }
                }
            } else if (EBoxType.HUNDRED.match(box.getBoxType())) {
                if (room.getGameType() == GameType.GAME_TYPE_HUNDRED_LHD) {
                    return new LhdHundredRoomHandle(room, box);
                } else if(room.getGameType() == GameType.GAME_TYPE_HUNDRED_BACCARAT) {
                    return new BaccaratHundredRoomHandle(room, box);
                }
            }
        default:
            break;
        }
        return null;
    }
}
