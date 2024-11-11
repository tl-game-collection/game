package com.xiuxiu.core.log;

public enum ELogLevel {
    DEBUG(0, "DEBUG", "\033[34m"),
    INFO(1, "INFO ", "\033[33m"),
    WARN(2, "WARN ", "\033[36m"),
    ERROR(3, "ERROR", "\033[31m"),
    FATAL(4, "FATAL", "\033[35m");

    private int level;
    private String desc;
    private String color;

    ELogLevel(int level, String desc, String color) {
        this.level = level;
        this.desc = desc;
        this.color = color;
    }

    public static ELogLevel parse(String logLevel) {
        if ("DEBUG".equalsIgnoreCase(logLevel)) {
            return DEBUG;
        }
        if ("INFO".equalsIgnoreCase(logLevel)) {
            return INFO;
        }
        if ("WARN".equalsIgnoreCase(logLevel)) {
            return WARN;
        }
        if ("ERROR".equalsIgnoreCase(logLevel)) {
            return ERROR;
        }
        if ("FATAL".equalsIgnoreCase(logLevel)) {
            return FATAL;
        }
        return DEBUG;
    }

    public int getLevel() {
        return level;
    }

    public String getDesc() {
        return desc;
    }

    public String getColor() {
        return color;
    }
}
