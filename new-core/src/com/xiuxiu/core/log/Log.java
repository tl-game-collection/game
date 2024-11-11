package com.xiuxiu.core.log;

public interface Log {
    void setLogLevel(ELogLevel logLevel);

    void setLogPath(String path, String prevFileName);

    void debug(String fmt, Object... args);

    void info(String fmt, Object... args);

    void warn(String fmt, Object... args);

    void error(String fmt, Object... args);

    void error(Throwable err);

    void error(String fmt, Throwable err, Object... args);

    void fatal(String fmt, Object... args);

    void fatal(Throwable err);

    void fatal(String fmt, Throwable err, Object... args);
}
