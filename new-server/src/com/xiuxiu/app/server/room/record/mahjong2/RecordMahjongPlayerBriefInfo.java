package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.record.RecordPlayerBriefInfo;

public class RecordMahjongPlayerBriefInfo extends RecordPlayerBriefInfo {
    public RecordMahjongPlayerBriefInfo() {
    }

    public RecordMahjongPlayerBriefInfo(IMahjongPlayer player, int index, int bureau) {
        super(player.getPlayer(), index, bureau);
        player.addHandCardTo(this.handCard);
    }
}
