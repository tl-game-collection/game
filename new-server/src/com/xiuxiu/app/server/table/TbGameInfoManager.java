package com.xiuxiu.app.server.table;

public class TbGameInfoManager {
    private static class TbGameInfoManagerHolder {
        private static final TbGameInfoManager INSTANCE = new TbGameInfoManager();
    }

    public static TbGameInfoManager I = TbGameInfoManagerHolder.INSTANCE;

    private TbGameInfo gameInfo;

    private TbGameInfoManager() {
    }

    public void init(TbGameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public TbGameInfo.TbGameInfoInfo getGameInfo(int gameType) {
        return this.gameInfo.map.get(gameType);
    }
}
