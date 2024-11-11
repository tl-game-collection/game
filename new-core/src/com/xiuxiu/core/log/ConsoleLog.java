package com.xiuxiu.core.log;


import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.core.utils.TimeUtil;

public class ConsoleLog implements Log {
    private ELogLevel logLevel = ELogLevel.DEBUG;

    @Override
    public void setLogLevel(ELogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void setLogPath(String path, String prevFileName) {
    }

    @Override
    public void debug(String fmt, Object... args) {
        this.write(ELogLevel.DEBUG, fmt, null, args);
    }

    @Override
    public void info(String fmt, Object... args) {
        this.write(ELogLevel.INFO, fmt, null, args);
    }

    @Override
    public void warn(String fmt, Object... args) {
        this.write(ELogLevel.WARN, fmt, null, args);
    }

    @Override
    public void error(String fmt, Object... args) {
        this.write(ELogLevel.ERROR, fmt, null, args);
    }

    @Override
    public void error(Throwable err) {
        this.write(ELogLevel.ERROR, null, err);
    }

    @Override
    public void error(String fmt, Throwable err, Object... args) {
        this.write(ELogLevel.ERROR, fmt, err, args);
    }

    @Override
    public void fatal(String fmt, Object... args) {
        this.write(ELogLevel.FATAL, fmt, null, args);
    }

    @Override
    public void fatal(Throwable err) {
        this.write(ELogLevel.FATAL, null, err);
    }

    @Override
    public void fatal(String fmt, Throwable err, Object... args) {
        this.write(ELogLevel.FATAL, fmt, err, args);
    }

    private void write(ELogLevel logLevel, String fmt, Throwable err, Object... args) {
        if (logLevel.getLevel() < this.logLevel.getLevel()) {
            return;
        }
        String errStr = null;
        if (StringUtil.isEmptyOrNull(fmt)) {
            fmt = "";
        }
        if (null != err) {
            errStr = StringUtil.exception2String(err);
        }
        String log = String.format(fmt, args);
        if (!StringUtil.isEmptyOrNull(errStr)) {
            log += "\n" + errStr;
        }
        StackTraceElement stack = Thread.currentThread().getStackTrace()[3];
        System.out.printf("%s[%s] [%s] [thread=%s/%x] %s:%s:%d %s \033[0m\n"
                , logLevel.getColor()
                , TimeUtil.format(System.currentTimeMillis())
                , logLevel.getDesc()
                , Thread.currentThread().getName()
                , Thread.currentThread().getId()
                , stack.getClassName()
                , stack.getMethodName()
                , stack.getLineNumber()
                , log);
    }
}
