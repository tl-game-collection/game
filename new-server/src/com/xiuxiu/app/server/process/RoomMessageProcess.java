package com.xiuxiu.app.server.process;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfLeaveStateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.services.gateway.MessageTask;
import com.xiuxiu.core.net.Process;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.thread.ConsumeThread;

public class RoomMessageProcess implements Process {
    private final ConsumeThread[] allThread;

    public RoomMessageProcess(ConsumeThread[] threads) {
        this.allThread = threads;
    }

    @Override
    public void exec(Task task) {
        MessageTask messageTask = (MessageTask) task;
        Player player = messageTask.getPlayer();
        if (null == player) {
            Logs.PLAYER.warn("消息处理错误, 玩家还没登陆 conn:%s", messageTask.getConn());
            messageTask.getConn().send(CommandId.ERROR, new ErrorMsg(ErrorCode.PLAYER_NO_LOGIN));
            return;
        }
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.PLAYER.warn("消息处理错误, 玩家还没有加入房间 player:%s, messagId:%d", player, messageTask.getCommandId());
            //player.send(CommandId.ERROR, ErrorCode.ROOM_NOT_EXISTS);
            player.changeRoomId(-1, -1);
            PCLIRoomNtfLeaveStateInfo info = new PCLIRoomNtfLeaveStateInfo();
            info.state = 1;
            player.send(CommandId.CLI_NTF_ROOM_LEAVE_V2_OK, info);
            return;
        }
        //int index = 0;
        //IRoomHandle roomHandle = room.getRoomHandle();
        //if (roomHandle instanceof IBoxRoomHandle) {
        //    index = (int) (((IBoxRoomHandle)roomHandle).getBoxUid() % this.allThread.length);
        //} else {
            //index = (int) (room.getRoomUid() % this.allThread.length);
        //}
        ConsumeThread thread = getThread(room.getRoomUid(),room.getGameType() == GameType.GAME_TYPE_HUNDRED_LHD || room.getGameType() == GameType.GAME_TYPE_HUNDRED_BACCARAT);
        thread.add(task);
    }

    public ConsumeThread getThread(long roomUid, boolean isHundred) {
        int hundred_length = this.allThread.length <= 8 ? 2 : 4;
        int index = 0;
        if (isHundred){
            index = (int) (roomUid % hundred_length) + (this.allThread.length - hundred_length);
        }else{
            index = (int) (roomUid % (this.allThread.length - hundred_length));
        }
        return this.allThread[index];
    }

    @Override
    public void shutdown() {
        for (int i = 0, len = this.allThread.length; i < len; ++i) {
            this.allThread[i].stop();
            try {
                this.allThread[i].join();
            } catch (InterruptedException e) {
                Logs.CORE.error(e);
            }
        }
    }
}
