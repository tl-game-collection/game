package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.record.RecordPlayerBriefInfo;

import java.util.List;

public class RecordPokerPlayerBriefInfo extends RecordPlayerBriefInfo {
    public RecordPokerPlayerBriefInfo() {
    }

    public RecordPokerPlayerBriefInfo(IPlayer player, int index, int bureau) {
        super(player, index, bureau);
    }

    public RecordPokerPlayerBriefInfo(IPlayer player, int index, int bureau, List<Byte> handCard) {
        super(player, index, bureau);
        if (handCard != null) {
            this.handCard.addAll(handCard);
        }
    }
}
