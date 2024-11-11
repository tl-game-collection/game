package com.xiuxiu.app.server.db;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.BaseObject;
import com.xiuxiu.core.net.Task;

import java.io.Serializable;

public abstract class BaseTable extends BaseObject implements Serializable, Task {
    protected transient volatile boolean isNew = true;
    protected transient ETableType tableType;

    public ETableType getTableType() {
        return tableType;
    }

    public void setTableType(ETableType tableType) {
        this.tableType = tableType;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean save() {
        if (!this.dirty) {
            return false;
        }
        DBManager.I.save(this);
        return true;
    }

    @Override
    public void run() {
        if (!DBManager.I.update(this)) {
            Logs.DB.error("保持失败: %s", this);
        }
    }
}
