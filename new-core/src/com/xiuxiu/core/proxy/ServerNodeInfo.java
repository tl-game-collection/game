package com.xiuxiu.core.proxy;

public class ServerNodeInfo {
    public ServerNodeType type;
    public int nodeId;
    public String ip;
    public int port;

    public ServerNodeInfo() {

    }

    public ServerNodeInfo(ServerNodeType type, int nodeId, String ip, int port) {
        this.type = type;
        this.nodeId = nodeId;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return "ServerNodeInfo{" +
                "type=" + type +
                ", nodeId=" + nodeId +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
