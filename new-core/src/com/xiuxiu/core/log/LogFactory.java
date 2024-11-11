package com.xiuxiu.core.log;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.SpiLoaderUtil;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LogFactory {
    public static final String LOG_PATH = "boxcar.log.path";
    public static final String LOG_PATH_DEFAULT = "config/log.json";

    static {
        String json = FileUtil.readFileString("cnf/log.cnf");
        List<LogInfoDesc> allLogNode = JsonUtil.fromJson2List(json, LogInfoDesc.class);
        Iterator<LogInfoDesc> it = allLogNode.iterator();
        while (it.hasNext()) {
            LogInfoDesc desc = it.next();
            get(desc.getName(), ELogLevel.parse(desc.getLevel()), desc.getPath(), desc.getPreFixFileName());
        }
    }

    private ConcurrentHashMap<String, Log> allLog = new ConcurrentHashMap<>();
    private ELogLevel defaultLogLevel = ELogLevel.DEBUG;
    private String defaultPath = "logs";
    private String defaultPrevFileName = "log_";

    private LogFactory() {

    }

    public static void init(ELogLevel logLevel) {
        init(logLevel, "logs");
    }

    public static void init(ELogLevel logLevel, String path) {
        init(logLevel, path, "log_");
    }

    public static void init(ELogLevel logLevel, String path, String prevFileName) {
        LogFactoryHolder.instance.init0(logLevel, path, prevFileName);
    }

    public static Log get(String tag) {
        return LogFactoryHolder.instance.getLog(tag);
    }

    public static Log get(String tag, ELogLevel logLevel) {
        return LogFactoryHolder.instance.getLog(tag, logLevel);
    }

    public static Log get(String tag, ELogLevel logLevel, String path) {
        return LogFactoryHolder.instance.getLog(tag, logLevel, path);
    }

    public static Log get(String tag, ELogLevel logLevel, String path, String prevFileName) {
        return LogFactoryHolder.instance.getLog(tag, logLevel, path, prevFileName);
    }

    private void init0(ELogLevel logLevel, String path, String prevFileName) {
        this.defaultLogLevel = logLevel;
        this.defaultPath = path;
        this.defaultPrevFileName = prevFileName;
    }

    private Log getLog(String tag) {
        return this.getLog(tag, this.defaultLogLevel);
    }

    private Log getLog(String tag, ELogLevel logLevel) {
        return this.getLog(tag, logLevel, this.defaultPath, this.defaultPrevFileName);
    }

    private Log getLog(String tag, ELogLevel logLevel, String path) {
        return this.getLog(tag, logLevel, path, this.defaultPrevFileName);
    }

    private Log getLog(String tag, ELogLevel logLevel, String path, String prevFileName) {
        Log log = this.allLog.get(tag);
        if (null == log) {
            Log logImp = SpiLoaderUtil.load(Log.class, false);
            logImp.setLogLevel(logLevel);
            logImp.setLogPath(path, prevFileName);
            this.allLog.putIfAbsent(tag, logImp);
            log = this.allLog.get(tag);
        }
        return log;
    }

    private static class LogFactoryHolder {
        private static final LogFactory instance = new LogFactory();
    }
}
