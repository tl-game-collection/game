package com.xiuxiu.app.server.services.account;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.Server;
import com.xiuxiu.core.service.BaseService;
import com.xiuxiu.core.service.FutureListener;
import com.xiuxiu.core.thread.NameThreadFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AccountServer extends BaseService implements Server {
    private com.sun.net.httpserver.HttpServer httpServer;

    private AuthHandler authHandler;
    private RegisterHandler registerHandler;
    private ResetHandler resetHandler;
    private BindPhoneHandler bindPhoneHandler;
    private AcquireAuthCodeHandler acquireAuthCodeHandler;
    private RealNameAuthHandler realNameAuthHandler;
    private CheckAccountHandler checkAccountHandler;
    private VerifyPhoneHandler verifyPhoneHandler;
    private TransferDataHandler transferDataHandler;
    private ChangeBindPhoneHandler changeBindPhoneHandler;

    private String ip;
    private int port;

    public AccountServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        Logs.ACCOUNT.debug(" AccountServer :%s", ip+":"+port);
    }

    @Override
    public void init() {
        this.httpServer = HttpServer.create(this.ip, this.port, false);
        this.httpServer.setExecutor(new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000), new NameThreadFactory("LoginServer")));

        this.authHandler = new AuthHandler();
        this.registerHandler = new RegisterHandler();
        this.resetHandler = new ResetHandler();
        this.bindPhoneHandler = new BindPhoneHandler();
        this.acquireAuthCodeHandler = new AcquireAuthCodeHandler();
        this.realNameAuthHandler = new RealNameAuthHandler();
        this.checkAccountHandler = new CheckAccountHandler();
        this.verifyPhoneHandler = new VerifyPhoneHandler();
        this.transferDataHandler = new TransferDataHandler();
        this.changeBindPhoneHandler = new ChangeBindPhoneHandler();

        this.httpServer.createContext("/v1/auth", this.authHandler);
        this.httpServer.createContext("/v1/register", this.registerHandler);
        this.httpServer.createContext("/v1/reset", this.resetHandler);
        this.httpServer.createContext("/v1/bindPhone", this.bindPhoneHandler);
        this.httpServer.createContext("/v1/acquireAuthCode", this.acquireAuthCodeHandler);
        this.httpServer.createContext("/v1/realNameAuth", this.realNameAuthHandler);
        this.httpServer.createContext("/v1/checkAccount", this.checkAccountHandler);
        this.httpServer.createContext("/v1/verifyPhone", this.verifyPhoneHandler);
        this.httpServer.createContext("/v1/transferData", this.transferDataHandler);
        this.httpServer.createContext("/v1/changeBindPhone", this.changeBindPhoneHandler);
    }

    @Override
    protected void doStart(FutureListener serviceListener) {
        this.httpServer.start();
        serviceListener.onSucc();
        Logs.NET.info("========================================================");
        Logs.NET.info("===========ACCOUNT SERVER START SUCCESS=================");
        Logs.NET.info("========================================================");
    }

    @Override
    protected void doStop(FutureListener serviceListener) {
        this.httpServer.stop(0);
        serviceListener.onSucc();
        Logs.NET.info("========================================================");
        Logs.NET.info("===========ACCOUNT SERVER STOP SUCCESS==================");
        Logs.NET.info("========================================================");
    }
}
