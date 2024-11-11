package com.xiuxiu.core.log;

import com.xiuxiu.core.thread.ConsumeThread;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.core.utils.TimeUtil;

import java.io.File;
import java.io.IOException;

public class FileLog extends ConsumeThread<FileLog.LogItem> implements Log {
    private ELogLevel logLevel = ELogLevel.DEBUG;
    private String path = "logs";
    private String prevFileName = "log_";
    private long lastTouchTime = 0;
    private LogFileWriter file = new LogFileWriter();

    public FileLog() {
        super("Log");
        this.start();
    }

    @Override
    public void setLogLevel(ELogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void setLogPath(String path, String prevFileName) {
        this.path = path;
        this.prevFileName = prevFileName;
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
        if (StringUtil.isEmptyOrNull(fmt)) {
            fmt = "";
        }
        String log = String.format(fmt, args);
        StackTraceElement stack = Thread.currentThread().getStackTrace()[3];
        LogItem logItem = new LogItem(logLevel, fmt, err, log, stack.getClassName(), stack.getMethodName(), stack.getLineNumber(), System.currentTimeMillis(), Thread.currentThread().getName(), Thread.currentThread().getId());
        this.add(logItem);
    }

    @Override
    protected void exec(LogItem value) {
        String log = value.getContent();
        if (null != value.getErr()) {
            log += "\n" + StringUtil.exception2String(value.getErr());
        }
        String fileLog = String.format("[%s] [%s] [thread=%s/%x] %s:%s:%d %s\n"
                , TimeUtil.format(value.getTimestamp())
                , value.getLogLevel().getDesc()
                , value.getThreadName()
                , value.getThreadId()
                , value.getClassName()
                , value.getMethodName()
                , value.getLineNo()
                , log);

        log = String.format("%s[%s] [%s] [thread=%s/%x] %s:%s:%d %s \033[0m\n"
                , value.getLogLevel().getColor()
                , TimeUtil.format(value.getTimestamp())
                , value.getLogLevel().getDesc()
                , value.getThreadName()
                , value.getThreadId()
                , value.getClassName()
                , value.getMethodName()
                , value.getLineNo()
                , log);
        System.out.print(log);

        if (!TimeUtil.isSameDay(value.getTimestamp(), this.lastTouchTime)) {
            this.lastTouchTime = value.getTimestamp();
            try {
                this.file.init(this.path + File.separator + this.prevFileName + TimeUtil.format("yyyy-MM-dd", this.lastTouchTime));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.file.write(fileLog);
    }

    public static class LogItem {
        private ELogLevel logLevel;
        private String fmt;
        private Throwable err;
        private String content;
        private String className;
        private String methodName;
        private int lineNo;
        private long timestamp;
        private String threadName;
        private long threadId;

        public LogItem(ELogLevel logLevel, String fmt, Throwable err, String content, String className, String methodName, int lineNo, long timestamp, String threadName, long threadId) {
            this.logLevel = logLevel;
            this.fmt = fmt;
            this.err = err;
            this.content = content;
            this.className = className;
            this.methodName = methodName;
            this.lineNo = lineNo;
            this.timestamp = timestamp;
            this.threadName = threadName;
            this.threadId = threadId;
        }

        public ELogLevel getLogLevel() {
            return logLevel;
        }

        public String getFmt() {
            return fmt;
        }

        public Throwable getErr() {
            return err;
        }

        public String getContent() {
            return content;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public int getLineNo() {
            return lineNo;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getThreadName() {
            return threadName;
        }

        public long getThreadId() {
            return threadId;
        }
    }
}
