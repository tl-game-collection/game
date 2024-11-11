package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.record.RecordPlayerBriefInfo;

public class RecordMahjongPlayerBriefInfo extends RecordPlayerBriefInfo {
    public RecordMahjongPlayerBriefInfo() {
    }

    public RecordMahjongPlayerBriefInfo(IPlayer player, int index, int bureau, byte[] handCard) {
        super(player, index, bureau);
        for (int i = 0, len = handCard.length; i < len; ++i) {
            this.handCard.add(handCard[i]);
        }
    }
}
