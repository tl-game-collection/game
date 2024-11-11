package com.xiuxiu.core;

public abstract class BaseObject {
    protected long uid;
    protected transient volatile boolean dirty;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
