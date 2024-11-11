package com.xiuxiu.app.server.room.normal.mahjong.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanFumbleInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;

public class MahjongFumbleAction extends BaseMahjongAction {
    protected boolean isFumbleOnBar;

    public MahjongFumbleAction(MahjongRoom room, MahjongPlayer roomPlayer, long timeout, boolean isFumbleOnBar) {
        super(room, EActionOp.FUMBLE, roomPlayer, timeout);
        this.isFumbleOnBar = isFumbleOnBar;
    }

    @Override
    public boolean action(boolean timeout) {
        ((MahjongRoom) this.room).onFumble(this.roomPlayer, timeout ? true : false);
        return true;
    }

    @Override
    protected void doRecover() {
        this.roomPlayer.send(CommandId.CLI_NTF_MAHJONG_CAN_FUMBLE, new PCLIMahjongNtfCanFumbleInfo(this.roomPlayer.getUid(), this.isFumbleOnBar));
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.roomPlayer.getUid() != player.getUid()) {
            return;
        }
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_FUMBLE, new PCLIMahjongNtfCanFumbleInfo(player.getUid(), this.isFumbleOnBar));
    }
}
