package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PCLIClubNtfClubGetApplyListInfo {
    public long clubUid;
    public List<PCLIClubNtfApplyInfo> list = new ArrayList<>();                // 俱乐部申请列表
    public List<PCLIClubMergeNtfApplyInfo> applyInfos = new ArrayList<>();     // 俱乐部合并申请列表
    public int applyType;               //申请列表类型 EApplyType

    @Override
    public String toString() {
        return "PCLIClubNtfClubGetApplyListInfo{" +
                "clubUid=" + clubUid +
                ", list=" + list +
                ", applyInfos=" + applyInfos +
                ", applyType=" + applyType +
                '}';
    }
}
