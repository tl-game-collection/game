package com.xiuxiu.core.proxy;

public enum ServerNodeType {
    NAMED(0x01, "named"),
    GATEWAY(0x02, "gateway"),
    DB(0x04, "db"),
    LOGIN(0x08, "login"),
    HALL(0x10, "hall"),
    CHAT(0x20, "chat"),
    ROOM(0x40, "room"),
    PLAYER(0x80, "player"),
    CENTER(0x100, "center"),;

    private int type;
    private String desc;

    ServerNodeType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ServerNodeType parse(int value) {
        switch (value) {
            case 0x01:
                return NAMED;
            case 0x02:
                return GATEWAY;
            case 0x04:
                return DB;
            case 0x08:
                return LOGIN;
            case 0x10:
                return HALL;
            case 0x20:
                return CHAT;
            case 0x40:
                return ROOM;
            case 0x80:
                return PLAYER;
            case 0x100:
                return CENTER;
        }
        return null;
    }

    public int value() {
        return this.type;
    }

    @Override
    public String toString() {
        return "ServerNodeType{" +
                "type=" + type +
                ", desc='" + desc + '\'' +
                '}';
    }
}
