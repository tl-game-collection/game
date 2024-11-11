package com.xiuxiu.app.server.club;

import com.xiuxiu.app.server.club.constant.EOpStateType;

public class ApplyInfo {
    private long fUid; //申请uid
    private long tUid; //被申请uid
    private long aTime; //申请时间
    private int state = EOpStateType.NORMAL.ordinal(); //申请状态
    private long pOne; //额外参数param1
    private long pTwo; //额外参数param2

    public long getfUid() { return fUid; }
    public void setfUid(long fUid) {
        this.fUid = fUid;
    }

    public long gettUid() {
        return tUid;
    }
    public void settUid(long tUid) { this.tUid = tUid; }

    public long getaTime() {
        return aTime;
    }
    public void setaTime(long aTime) {
        this.aTime = aTime;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    public long getpOne() {
        return pOne;
    }
    public void setpOne(long pOne) {
        this.pOne = pOne;
    }

    public long getpTwo() {
        return pTwo;
    }
    public void setpTwo(long pTwo) {
        this.pTwo = pTwo;
    }

    @Override
    public String toString() {
        return "ApplyInfo{" +
                "fUid=" + fUid +
                ", tUid=" + tUid +
                ", aTime=" + aTime +
                ", state=" + state +
                ", pOne=" + pOne +
                ", pTwo=" + pTwo +
                '}';
    }

    public static ApplyInfo createApplyInfoByMergeOrLeave(IClub fromClub, IClub toClub,EOpStateType type, long nowTime){
        ApplyInfo info = new ApplyInfo();
        info.setfUid(fromClub.getClubUid());
        info.settUid(toClub.getClubUid());
        info.setaTime(nowTime);
        info.setState(type.ordinal());
        long[] totalValues = fromClub.getTotalGoldAndRewardValue();
        info.setpOne(totalValues[0]);
        info.setpTwo(totalValues[1]);
        return info;
    }

    public static ApplyInfo copyApplyInfo(ApplyInfo applyInfo){
        ApplyInfo info = new ApplyInfo();
        info.setfUid(applyInfo.getfUid());
        info.settUid(applyInfo.gettUid());
        info.setaTime(applyInfo.getaTime());
        info.setState(applyInfo.getState());
        info.setpOne(applyInfo.getpOne());
        info.setpTwo(applyInfo.getpTwo());
        return info;
    }
}
