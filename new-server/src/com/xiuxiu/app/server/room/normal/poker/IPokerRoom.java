package com.xiuxiu.app.server.room.normal.poker;

import java.util.List;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.normal.IRoom;

public interface IPokerRoom extends IRoom, IPokerXuanPiao {
    ErrorCode take(Player player, List<Byte> cards, List<Byte> laiZiCards, int cardType);
    ErrorCode pass(Player player);
}
