package com.xiuxiu.app.server.score;

import java.util.LinkedList;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.core.utils.JsonUtil;

public abstract class BaseRoomScore extends AbstractRoomScore {

    protected ScoreInfo totalScore = new ScoreInfo();
    protected LinkedList<ScoreInfo> record = new LinkedList<>();

    @Override
    public void addRecord(ScoreInfo scoreInfo) {
        this.record.addLast(scoreInfo);
    }

    public ScoreInfo getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(ScoreInfo totalScore) {
        this.totalScore = totalScore;
    }

    public String getTotalScoreDb() {
        return JsonUtil.toJson(this.totalScore);
    }

    public void setTotalScoreDb(String totalScore) {
        this.totalScore = JsonUtil.fromJson(totalScore, ScoreInfo.class);
    }

    public LinkedList<ScoreInfo> getRecord() {
        return record;
    }

    public void setRecord(LinkedList<ScoreInfo> record) {
        this.record = record;
    }

    public String getRecordDb() {
        return JsonUtil.toJson(this.record);
    }

    public void setRecordDb(String record) {
        this.record = JsonUtil.fromJson(record, new TypeReference<LinkedList<ScoreInfo>>() {
        });
    }

}
