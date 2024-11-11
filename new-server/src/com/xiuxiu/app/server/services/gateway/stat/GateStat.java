package com.xiuxiu.app.server.services.gateway.stat;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GateStat {

    private static GateStat instance = new GateStat();

    private GateStat() {
    }

    public static GateStat getInstance() {
        return instance;
    }

    private ConcurrentHashMap<Integer, GateStatModel> statMap = new ConcurrentHashMap<>();

    public void add(int commandId, long time, boolean success) {
        GateStatModel statModel = statMap.get(commandId);
        if (statModel == null) {
            statModel = new GateStatModel();
            statMap.putIfAbsent(commandId, statModel);
            statModel = statMap.get(commandId);
        }
        statModel.add(commandId, time, success);
    }

    public String report() {
        List<GateStatModel> statModels = new ArrayList<>();
        for (GateStatModel stat : statMap.values()) {
            statModels.add(stat.clone4Report());
        }

        statModels.sort(Comparator.comparing(GateStatModel::getCostTimePerHit));
        StringBuilder sb = new StringBuilder();
        sb.append("============= Gate stat report =============\r\n");
        for (GateStatModel stat : statModels) {
            sb.append(stat.toString()).append("\r\n");
        }

        return sb.toString();
    }
}
