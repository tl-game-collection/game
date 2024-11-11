package com.xiuxiu.app.server.room.record;

import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.core.BaseObject;
import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Record extends BaseObject {
    protected RecordRoomBriefInfo roomInfo;
    protected List<RecordPlayerBriefInfo> playerInfo = new ArrayList<>();
    protected List<RecordAction> allAction = new ArrayList<>();

    public Record() {
        this.uid = UIDManager.I.getAndInc(UIDType.RECORD);
    }

    public void addPlayer(RecordPlayerBriefInfo player) {
        this.playerInfo.add(player);
    }

    public void addAction(RecordAction action) {
        this.allAction.add(action);
    }

    public void save() {
        try{
            FileUtil.writeFile(Config.RECORD_PATH + File.separator + this.uid, JsonUtil.toJson(this, true, false));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RecordRoomBriefInfo getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RecordRoomBriefInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    public List<RecordPlayerBriefInfo> getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(List<RecordPlayerBriefInfo> playerInfo) {
        this.playerInfo = playerInfo;
    }

    public List<RecordAction> getAllAction() {
        return allAction;
    }

    public void setAllAction(List<RecordAction> allAction) {
        this.allAction = allAction;
    }
}
