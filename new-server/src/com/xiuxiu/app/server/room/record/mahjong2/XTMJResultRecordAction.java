package com.xiuxiu.app.server.room.record.mahjong2;

import java.util.ArrayList;
import java.util.List;

public class XTMJResultRecordAction extends ResultRecordAction {
    private List<Byte> horseList = new ArrayList<>();   // 马分

    public XTMJResultRecordAction() {
        super();
    }

    public void addHorseList(List<Byte> list) {
        this.horseList.addAll(list);
    }

    public List<Byte> getHorseList() {
        return horseList;
    }

    public void setHorseList(List<Byte> horseList) {
        this.horseList = horseList;
    }
}
