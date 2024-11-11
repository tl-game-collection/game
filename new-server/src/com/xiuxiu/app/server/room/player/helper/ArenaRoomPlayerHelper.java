package com.xiuxiu.app.server.room.player.helper;

import java.util.Iterator;
import java.util.Map;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.score.BoxArenaScore;

public class ArenaRoomPlayerHelper extends AbstractArenaRoomPlayerHelper {

    private BoxArenaScore arenaScore;

    public ArenaRoomPlayerHelper(IRoomPlayer roomPlayer) {
        super(roomPlayer);
    }

    @Override
    public void init() {
        try {
            IRoom room = RoomManager.I.getRoom(roomPlayer.getRoomId());
            IBoxRoomHandle roomHandle = (IBoxRoomHandle) room.getRoomHandle();
            IClub fromClub = getFromClub();
            this.arenaScore = boxOwner.getBoxArenaScoreIfCreate(null == fromClub ? -1 : fromClub.getClubUid(),
                    roomHandle.getBoxUid(), roomPlayer.getUid());
            Iterator<Map.Entry<String, Integer>> it = this.arenaScore.getAllCnt().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                roomPlayer.setScore(entry.getKey(), entry.getValue(), true);
            }
        } catch (NullPointerException e) {
            Logs.GROUP.error(e);
        }
    }

    @Override
    public int getCurBureau() {
        return null == arenaScore ? -1 : arenaScore.getBureau();
    }

    @Override
    public void record(int score, long recordUid, long now) {
        Room room = RoomManager.I.getRoom(roomPlayer.getRoomId());
        if (null == room) {
            return;
        }
        this.arenaScore.bureauInc();
        this.arenaScore.addScore(score);
        this.arenaScore.addRecord(recordUid);

        this.arenaScore.getAllCnt().put(Score.ACC_MAX_SCORE, roomPlayer.getScore(Score.ACC_MAX_SCORE, true));
        this.arenaScore.getAllCnt().put(Score.ACC_POKER_MAX_CARD_TYPE,
                roomPlayer.getScore(Score.ACC_POKER_MAX_CARD_TYPE, true));
        this.arenaScore.getAllCnt().put(Score.ACC_WIN_CNT, roomPlayer.getScore(Score.ACC_WIN_CNT, true));
        this.arenaScore.getAllCnt().put(Score.ACC_LOST_CNT, roomPlayer.getScore(Score.ACC_LOST_CNT, true));
        this.arenaScore.setDirty(Boolean.TRUE);
        this.arenaScore.save();
    }

    public BoxArenaScore getArenaScore() {
        return arenaScore;
    }
}
