package com.xiuxiu.app.server.statistics;

public enum EAccountAction {
    NONE(0, ""),
    REGISTER(1, "register"),
    LOGIN(2, "login"),
    LOGOUT(3, "logout"),
    ONLINE(4, "online"),
    ;

    private int code;
    private String string;

    EAccountAction(int code, String string) {
        this.code = code;
        this.string = string;
    }

    public int code() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String string() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public static EAccountAction parse(String value) {
        if ("register".equals(value)) {
            return REGISTER;
        }
        if ("login".equals(value)) {
            return LOGIN;
        }
        if ("logout".equals(value)) {
            return LOGOUT;
        }
        if ("online".equals(value)) {
            return ONLINE;
        }
        return NONE;
    }
}
