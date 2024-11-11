package com.xiuxiu.core.net;

public class DefaultSessionContextFactory implements SessionContextFactory {
    @Override
    public SessionContext create() {
        return new DefaultSessionContext();
    }
}
