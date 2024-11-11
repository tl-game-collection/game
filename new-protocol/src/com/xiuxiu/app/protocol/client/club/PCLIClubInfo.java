package com.xiuxiu.app.protocol.client.club;

import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PCLIClubInfo {
    public PCLIClubSingleInfo selfClubInfo;
    public PCLIClubSingleInfo mainClubInfo;
    public long mainClubUid;

    @Override
    public String toString() {
        return "PCLIClubInfo{" +
                "selfClubInfo=" + selfClubInfo +
                ", mainClubInfo=" + mainClubInfo +
                ", mainClubUid=" + mainClubUid +
                '}';
    }
}
