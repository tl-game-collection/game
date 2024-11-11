package com.xiuxiu.core.net.protocol;

public enum ErrorCode implements IErrorCode {
    OK(0, "成功"),
    FAIL(1, "失败"),
    INVALID_REQUEST(2, "无效请求"),
    SERVER_INTERNAL_ERROR(3, "服务器内部错误"),
    NET_TIMEOUT(5, "网络超时"),
    NET_ERROR(6, "网络错误"),
    DB_ERROR(7, "数据库错误"),
    INVALID_DATA(8, "无效数据"),

    // 登陆相关
    LOGIN_REGISTER(100, "正在登陆"),
    LOGIN_PHONE_NULL(101, "手机号为空"),
    LOGIN_PASSWD_NULL(102, "密码为空"),

    // 账号相关
    ACCOUNT_ALREADY_EXISTS(101, "账号已经存在"),
    ACCOUNT_USERNAME_OR_PASSWD_ERROR(102, "账号用户名或者密码错误"),;

    private int ret;
    private String msg;

    ErrorCode(int ret, String msg) {
        this.ret = ret;
        this.msg = msg;
    }

    @Override
    public int getRet() {
        return ret;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
