package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.MailInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class MailHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        MailInfo mail = JsonUtil.fromJson(body, MailInfo.class);
        Logs.API.debug("收到邮件内容:%s", mail);
        if (mail.isServer()) {
            MailManager.I.sendSystemMailWithServer(mail.getTitle(), mail.getContent(), mail.getItem());
        } else {
            MailManager.I.sendSystemMail(mail.getReceivePlayerUid(), mail.getTitle(), mail.getContent(), mail.getItem());
        }
        HttpServer.sendOk(httpExchange, null);
        httpExchange.close();
    }
}
