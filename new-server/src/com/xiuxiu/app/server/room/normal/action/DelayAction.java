package com.xiuxiu.app.server.room.normal.action;

import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.core.ICallback;

public class DelayAction extends BaseAction {
    protected ICallback<Object> callback;
    protected Object args;

    public DelayAction(IRoom room, long timeout) {
        super(room, EActionOp.AUTO_START, timeout);
    }

    public void setCallback(ICallback<Object> callback) {
        this.callback = callback;
    }

    public void setCallbackAndArgs(ICallback<Object> callback, Object args) {
        this.args = args;
    }

    @Override
    public boolean action(boolean timeout) {
        this.callback.call(this.args);
        return true;
    }

    @Override
    protected void doRecover() {
    }

    @Override
    public void online(IRoomPlayer player) {
    }

    @Override
    public void offline(IRoomPlayer player) {
    }

    @Override
    protected void operationTimeout() {
    }
}
