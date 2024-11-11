package com.xiuxiu.app.server.process;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.services.gateway.MessageTask;
import com.xiuxiu.core.net.Process;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.thread.NameThreadFactory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatMessageProcess implements Process {
    private final ExecutorService chatExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), new NameThreadFactory("ChatExecutorService"));

    private final CopyOnWriteArrayList<Runnable> historyList = new CopyOnWriteArrayList<>();
    
    @Override
    public void exec(Task task) {
        try {
            this.chatExecutor.submit(task);
            //recTask(task);
        } catch (Exception ex) {
            Logs.CMD.error(ex);
            dumpRecTasks();
            throw new RuntimeException(ex);
        }
    }

    private void recTask(Runnable task) {
        historyList.add(task);
        if (historyList.size() > 500) {
            Logs.CMD.error("ChatMessageProcess recTask count[%s]", historyList.size());
            // 控制历史记录长度在500以内
            // 尝试10次，清理到50以内，高并发下清理一次是不够的
            for (int i = 0; i < 10; i++) {
                historyList.remove(0);
                if (historyList.size() < 501) {
                    break;
                }
            }
        }
    }

    private void dumpRecTasks() {
        int index = 0;
        for (Runnable historyTask : historyList) {
            index++;
            if (historyTask instanceof MessageTask) {
                logTask(index, (MessageTask) historyTask);
            }
        }
    }

    private void logTask(int index, MessageTask task) {
        if (task.getPlayer() == null) {
            Logs.CMD.error("ChatMessageProcess exec failed, [%s] element command [%s], player is null", index, task.getCommandId());
        } else {
            Logs.CMD.error("ChatMessageProcess exec failed, [%s] element command [%s], playerId [%s], roomId [%s]", index, task.getCommandId(), task.getPlayer().getUid(), task.getPlayer().getRoomId());
        }
    }
    
    @Override
    public void shutdown() {
        this.chatExecutor.shutdownNow();
    }
}
