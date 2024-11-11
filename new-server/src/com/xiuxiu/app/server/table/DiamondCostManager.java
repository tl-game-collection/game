package com.xiuxiu.app.server.table;

import java.util.HashMap;

public class DiamondCostManager {
    private static class DiamondCostManagerHolder {
        private static DiamondCostManager instance = new DiamondCostManager();
    }

    public static DiamondCostManager I = DiamondCostManagerHolder.instance;

    public static final int COST_TYPE_ROOM = 1;                 //  普通房和包间
    public static final int COST_TYPE_ARENA = 2;                //  竞技场
    public static final int COST_TYPE_RECOMMEND = 3;            //  推荐人
    public static final int COST_TYPE_RECOMMENDED = 4;          //  被推荐人
    public static final int COST_TYPE_RECOMMEND_GAME = 6;       //  推荐人(完成一轮游戏后)
    public static final int COST_TYPE_RECOMMENDED_GAME = 7;     //  被推荐人(完成一轮游戏后)

    protected DiamondCost diamondCost;

    protected HashMap<String, DiamondCost.DiamondCostInfo> allInfo = new HashMap<>();

    private DiamondCostManager() {
    }

    public void init(DiamondCost diamondCost) {
        this.diamondCost = diamondCost;
        for (DiamondCost.DiamondCostInfo info : diamondCost.list) {
            this.allInfo.put(info.getGameType() + "_" + info.getType() + "_" + info.getBureau(), info);
        }
    }

    public int getCostByGameType(int gameType, int type, int bureau) {
        String key = gameType + "_" + type + "_" + bureau;
        DiamondCost.DiamondCostInfo info = this.allInfo.get(key);
        return null == info ? 0 : info.getNum();
    }

}
