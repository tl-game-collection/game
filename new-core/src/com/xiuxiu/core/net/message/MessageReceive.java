package com.xiuxiu.core.net.message;

import com.xiuxiu.core.net.Connection;

public interface MessageReceive {
    void onReceive(Connection conn, RequestWrapper message);
}
