package com.xiuxiu.app.server.services.gateway;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.services.gateway.stat.GateStat;
import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.net.message.MessageType;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.net.protocol.ProtocolException;

public class MessageTask implements Task {
    private Connection conn;
    private RequestWrapper request;
    private Handler handler;
    private Player player;
    private Object body;

    public MessageTask(Connection conn, RequestWrapper request, Handler handler) {
        this.conn = conn;
        this.request = request;
        this.handler = handler;
        this.player = PlayerManager.I.getOnlinePlayer(((GatewaySessionContext) conn.getSessionContext()).getPlayerUid());
    }

    public Connection getConn() {
        return conn;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCommandId() {
        return this.request.getCommandId();
    }

    public Object getBody() {
        if (null == this.body) {
            try {
                this.body = Codecs.getDecoder(this.request.getCodecType()).decoder(this.request.getCommandId(), (byte[]) this.request.getBody());
            } catch (Exception e) {
                Logs.CMD.error(e);
            }
        }
        return this.body;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        boolean success = true;

        ResponseWrapper response = new ResponseWrapper(this.request.getRequestId(), this.request.getProtocolVersion(), this.request.getCodecType());
        if (null != this.handler) {
            try {
                if (null == this.body) {
                    this.body = Codecs.getDecoder(response.getCodecType()).decoder(this.request.getCommandId(), (byte[]) this.request.getBody());
                }
                if (Switch.DEBUG) {
                    Logs.CMD.debug("%s receive commandId:%s message:%s", this.player, Integer.toString(this.request.getCommandId(), 16), this.body);
                }
                if (CommandId.CLI_REQ_LOGIN == this.request.getCommandId() ||
                        CommandId.CLI_REQ_RELOGIN == this.request.getCommandId()) {
                    response.setBody(this.handler.handler(this.conn, this.body));
                } else {
                    if (null == player) {
                        Logs.PLAYER.warn("Connection:%s 没有登陆", this.conn);
                    } else {
                        response.setBody(this.handler.handler(this.player, this.body));
                    }
                }
                if (null != response.getBody()) {
                    response.setCommandId(Codecs.getCommandId(response.getCodecType(), response.getBody().getClass().getSimpleName()));
                }
            } catch (Exception e) {
                      Logs.CMD.error(e);
                response.setCause(e);
                success = false;
            }
        } else {
            response.setCause(new ProtocolException("CommandId:" + response.getCommandId() + " not have handler"));
            success = false;
        }
        if (MessageType.REQUEST == response.getMessageType()) {
            if (null != response) {
                this.conn.send(response);
            }
        }
        GateStat.getInstance().add(this.request.getCommandId(), System.currentTimeMillis() - startTime, success);

    }
}
