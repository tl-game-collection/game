package com.xiuxiu.core.log;

public interface Logs {
    Log CORE = LogFactory.get("core");
    Log NET = LogFactory.get("net");
    Log CONN = LogFactory.get("conn");
    Log HB = LogFactory.get("hb");
    Log CMD = LogFactory.get("cmd");
}
