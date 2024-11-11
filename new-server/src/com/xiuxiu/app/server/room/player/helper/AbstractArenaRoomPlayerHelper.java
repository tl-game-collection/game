package com.xiuxiu.app.server.room.player.helper;

import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;

public abstract class AbstractArenaRoomPlayerHelper extends AbstractRoomPlayerHelper implements IArenaRoomPlayerHelper {

    protected volatile IBoxOwner boxOwner = null;

    public AbstractArenaRoomPlayerHelper(IRoomPlayer roomPlayer) {
        super(roomPlayer);
    }

    @Override
    public IBoxOwner getBoxOwner() {
        if (null != this.boxOwner) {
            return this.boxOwner;
        }

        synchronized (this) {
            if (null != this.boxOwner) {
                return this.boxOwner;
            }

            Room room = RoomManager.I.getRoom(roomPlayer.getRoomId());

            if (null == room) {
                return this.boxOwner;
            }
            this.boxOwner = room.getBoxOwner();
            return this.boxOwner;
        }
    }

    @Override
    public IClub getFromClub() {
        IBoxOwner boxOwner = getBoxOwner();
        if (null == boxOwner) {
            return null;
        }
        IClub mainClub = ClubManager.I.getMainClub(boxOwner);
        if (null == mainClub) {
            return null;
        }
        return ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(roomPlayer.getUid()));
    }

    @Override
    public long getGold() {
        IClub fromClub = getFromClub();
        return null == fromClub ? 0 : fromClub.getGold(roomPlayer.getUid());
    }

    @Override
    public boolean checkEnoughGold(long value) {
        return value >= 0 && getGold() >= value;
    }

    @Override
    public boolean addGold(long playerUid, int value, long optPlayerUid, EClubGoldChangeType changeType) {
        IClub fromClub = getFromClub();
        return fromClub != null ? fromClub.addMemberClubGold(playerUid, value, optPlayerUid, changeType) : false;
    }
}
