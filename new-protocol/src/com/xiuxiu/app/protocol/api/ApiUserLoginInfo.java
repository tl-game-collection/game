package com.xiuxiu.app.protocol.api;

public class ApiUserLoginInfo{
    public String name;
    public String passwd;

    @Override
    public String toString() {
        return "ApiUserLoginInfo{" +
                "name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
