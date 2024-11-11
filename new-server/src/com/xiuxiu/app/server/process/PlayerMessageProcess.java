package com.xiuxiu.app.server.process;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.services.gateway.MessageTask;
import com.xiuxiu.core.net.Process;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.thread.ConsumeThread;

public class PlayerMessageProcess implements Process {
    private final ConsumeThread[] allThread;

    public PlayerMessageProcess(ConsumeThread[] allThread) {
        this.allThread = allThread;
    }

    @Override
    public void exec(Task task) {
        MessageTask messageTask = (MessageTask) task;
        if (null == messageTask.getPlayer()) {
            Logs.PLAYER.warn("消息处理错误, 玩家还没登陆 conn:%s", messageTask.getConn());
            messageTask.getConn().send(CommandId.ERROR, new ErrorMsg(ErrorCode.PLAYER_NO_LOGIN));
            return;
        }
        ConsumeThread thread = this.allThread[(int) (messageTask.getPlayer().getUid() % this.allThread.length)];
        thread.add(task);
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
