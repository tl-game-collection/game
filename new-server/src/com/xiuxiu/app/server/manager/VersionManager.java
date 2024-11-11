package com.xiuxiu.app.server.manager;

import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

public class VersionManager extends BaseManager {
    private static class VersionManagerHolder {
        private static VersionManager instance = new VersionManager();
    }

    public static VersionManager I = VersionManagerHolder.instance;

    private static final String VERSION_PATH = "version";

    private VersionInfo version;

    private VersionManager() {
    }

    public void init() {
        String versionStr = FileUtil.readFileString(VERSION_PATH);
        if (!StringUtil.isEmptyOrNull(versionStr)) {
            this.version = JsonUtil.fromJson(versionStr, VersionInfo.class);
        }
        if (null == this.version) {
            this.version = new VersionInfo();
        }

        this.version.setPrevVersion(this.version.getCurVersion());
        this.version.setCurVersion(this.version.getCurVersion() + 1);
        this.version.setServerState(0);

        FileUtil.writeFile(VERSION_PATH, JsonUtil.toJson(this.version));
    }

    public void setupSucc() {
        this.version.setServerState(1);
        FileUtil.writeFile(VERSION_PATH, JsonUtil.toJson(this.version));
    }

    public VersionInfo getVersion() {
        return version;
    }

    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        try {
            this.version.setServerState(0);
            FileUtil.writeFile(VERSION_PATH, JsonUtil.toJson(this.version));
        } catch (Throwable e) {
            Logs.CORE.error(e);
        }
        return 0;
    }
}
