package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanFumbleInfo;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.MahjongPlayer;

public class MahjongFumbleAction extends BaseMahjongAction {
    protected boolean isFumbleOnBar;

    public MahjongFumbleAction(IRoom room, MahjongPlayer roomPlayer, long timeout, boolean isFumbleOnBar) {
        super(room, roomPlayer,EActionOp.FUMBLE,  timeout);
        this.isFumbleOnBar = isFumbleOnBar;
    }

    @Override
    public boolean action(boolean timeout) {
        ((MahjongRoom) this.room).onFumble(this.player);
        return true;
    }

    @Override
    protected void doRecover() {
        this.player.send(CommandId.CLI_NTF_MAHJONG_CAN_FUMBLE, new PCLIMahjongNtfCanFumbleInfo(this.player.getUid(), this.isFumbleOnBar));
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.player.getUid() != player.getUid()) {
            return;
        }
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_FUMBLE, new PCLIMahjongNtfCanFumbleInfo(player.getUid(), this.isFumbleOnBar));
    }
}
