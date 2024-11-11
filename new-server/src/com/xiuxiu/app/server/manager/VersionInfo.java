package com.xiuxiu.app.server.manager;

public class VersionInfo {
    private int prevVersion = -1;
    private int curVersion = 0;
    private int serverState = 0;

    public int getPrevVersion() {
        return prevVersion;
    }

    public void setPrevVersion(int prevVersion) {
        this.prevVersion = prevVersion;
    }

    public int getCurVersion() {
        return curVersion;
    }

    public void setCurVersion(int curVersion) {
        this.curVersion = curVersion;
    }

    public int getServerState() {
        return serverState;
    }

    public void setServerState(int serverState) {
        this.serverState = serverState;
    }
}
