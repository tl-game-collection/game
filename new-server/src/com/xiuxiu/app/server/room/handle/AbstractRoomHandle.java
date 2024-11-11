package com.xiuxiu.app.server.room.handle;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMemberNotGuest;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMemberStateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.EPlayerDone;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomDestroyType;
import com.xiuxiu.app.server.room.ERoomListState;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.EState;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.HSMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.HZMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.WHMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.YXMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.poker.*;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.score.BaseRoomScore;
import com.xiuxiu.app.server.score.IRoomScore;
import com.xiuxiu.app.server.score.RoomScore;
import com.xiuxiu.app.server.score.ScoreInfo;
import com.xiuxiu.app.server.score.ScoreItemInfo;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.app.server.table.DiamondCostManager;
import com.xiuxiu.core.net.Task;

public abstract class AbstractRoomHandle implements IRoomHandle {

    protected IRoom room;

    public AbstractRoomHandle(IRoom room) {
        this.room = room;
    }

    @Override
    public void init() {

    }

    @Override
    public IRoom getRoom() {
        return this.room;
    }

    @Override
    public ErrorCode join(Player player) {
        IRoomPlayer roomPlayer = this.room.getRoomPlayer(player.getUid());
        if (null != roomPlayer) {
            this.room.changeState(roomPlayer, EState.ONLINE);
            Logs.ROOM.warn("%s %s 已经在房间里,上线", this, roomPlayer);
            return ErrorCode.OK;
        }

        if (ERoomState.FINISH == this.room.getRoomState() || ERoomState.DESTROY == this.room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法加入", this, player);
            return ErrorCode.ROOM_FINISH;
        }
        if (ERoomState.NEW == this.room.getRoomState()) {
//        if (ERoomState.DESTROY != this.room.getRoomState()) {
            if (this.room.getPlayerNum() != this.room.getPlayerCnt()) {
                ReentrantReadWriteLock.WriteLock writeLock = this.room.getLock().writeLock();
                try {
                    if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                        IRoomPlayer[] allPlayer = this.room.getAllPlayer();
                        for (int i = 0; i < this.room.getPlayerNum(); ++i) {
                            if (null == allPlayer[i]) {
                                roomPlayer = this.room.createPlayer();
                                roomPlayer.setPlayer(player);
                                roomPlayer.setIndex(i);
                                allPlayer[i] = roomPlayer;
                                this.room.addPlayerCnt();
                                if (this.room.getPlayerNum() == this.room.getPlayerCnt()) {
                                    this.room.changeState(ERoomListState.FULL);
                                }
                                this.startCheckLeave();
                                return ErrorCode.OK;
                            }
                        }
                    } else {
                        return ErrorCode.SERVER_BUSY;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (writeLock.isHeldByCurrentThread()) {
                        writeLock.unlock();
                    }
                }
            }
        }
        if (!this.room.canWatch()) {
            if (ERoomState.NEW != this.room.getRoomState()) {
                Logs.ROOM.warn("%s %s 房间已经开始 无法加入", this, player);
                return ErrorCode.ROOM_ALREADY_START;
            } else {
                Logs.ROOM.warn("%s %s 房间已经满了", this, player);
                return ErrorCode.ROOM_PLAYER_FULL;
            }
        }
        this.room.addWatchPlayerUid(player.getUid());
        return ErrorCode.ROOM_MIDDLE_JOIN;
    }

    @Override
    public ErrorCode leave(Player player) {
        boolean guessLeave = false;
        if (this.room.removeWatch(player.getUid())) {
            Logs.ROOM.debug("%s %s 游客离开", this.room, player);
            guessLeave = true;
        }
        IRoomPlayer roomPlayer = this.room.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, guessLeave:%s", this.room, player, guessLeave);
            return guessLeave ? ErrorCode.OK : ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (ERoomState.START == this.room.getRoomState() && !roomPlayer.isGuest()) {
            Logs.ROOM.warn("%s %s 房间已经开始, 离线", this.room, roomPlayer);
            IRoomHandle roomHandle = room.getRoomHandle();
            if (roomHandle instanceof IBoxRoomHandle) {
                if (GameType.isArenaGame(room.getGameType())) {
                    return ErrorCode.ROOM_LEAVE;
                }
            }
            return ErrorCode.ROOM_ALREADY_START;
        } else {
            IRoomPlayer[] allPlayer = this.room.getAllPlayer();
            ReentrantReadWriteLock.WriteLock writeLock = this.room.getLock().writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                    this.room.removeReady(player.getUid());
                    if (null != allPlayer[roomPlayer.getIndex()]) {
                        if (this.room.getPlayerNum() == this.room.getAndDecrPlayerCnt()) {
                            this.room.changeState(ERoomListState.CAN_ADD);
                        }
                    }
                    allPlayer[roomPlayer.getIndex()] = null;
                } else {
                    Logs.ROOM.warn("%s %s 尝试获取写锁失败", this.room, player);
                    return ErrorCode.ROOM_ALREADY_START;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }
            this.room.checkStart();
        }
        return ErrorCode.OK;
    }

    @Override
    public void destoryAfter() {

    }

    @Override
    public void doFinishAfter(boolean isNormal, boolean isNewBureau) {
        doFinishAfterHandle(isNormal, isNewBureau);
    }

    protected void doFinishAfterHandle(boolean isNormal, boolean isNewBureau) {
        IBoxOwner boxOwner = room.getBoxOwner();
        long nowTime = System.currentTimeMillis();
        // 推荐的玩家完成一轮游戏后 推荐人和被推荐人都 奖励房卡
        for (IRoomPlayer iRoomPlayer : room.getAllPlayer()) {
            if (null == iRoomPlayer || iRoomPlayer.isGuest()) {
                continue;
            }
            if (room.getTemporaryPropertyValue(iRoomPlayer.getUid(), RoomRule.RR_BUREAU) >= room.getBureau()) {
                Player player = PlayerManager.I.getPlayer(iRoomPlayer.getUid());
                long recommendUid = player.getRecommendInfo().getRecommendPlayerUid();
                Player recommendPlayer = PlayerManager.I.getPlayer(recommendUid);
                if (null != recommendPlayer && player.isDoneGame(EPlayerDone.DONE_GAME)) {
                    // 推荐的玩家
                    recommendPlayer.addRecommendDiamond(
                            DiamondCostManager.I.getCostByGameType(0, DiamondCostManager.COST_TYPE_RECOMMEND_GAME, 0));
                    // 被推荐的玩家
                    player.addRecommendDiamond(DiamondCostManager.I.getCostByGameType(0,
                            DiamondCostManager.COST_TYPE_RECOMMENDED_GAME, 0));
                    player.setDone(EPlayerDone.DONE_GAME, true);
                }
                if (null != boxOwner) {
                    boxOwner.onFinishAllBureau(this.getFromClubUid(iRoomPlayer.getUid()), iRoomPlayer.getUid(),
                            nowTime);
                }
            }
        }
    }

    @Override
    public void killAll(List<Long> killPlayerUids) {

    }

    @Override
    public void startBefore() {

    }
    
    @Override
    public void start() {
        
    }
    
    @Override
    public void destoryGoldHandle() {
        
    }

    @Override
    public void calculateGold() {

    }

    @Override
    public void again() {
        PCLIRoomNtfMemberNotGuest info = new PCLIRoomNtfMemberNotGuest();
        ReentrantReadWriteLock.WriteLock writeLock = this.room.getLock().writeLock();
        try {
            if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                IRoomPlayer[] allPlayer = this.room.getAllPlayer();
                for (int i = 0; i < this.room.getPlayerNum(); ++i) {
                    IRoomPlayer player = allPlayer[i];
                    if (null == player || !player.isGuest()) {
                        continue;
                    }
                    info.notGuestMembers.add(player.getUid());
                    this.room.clearGuest(player);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
        if (info.notGuestMembers.size() > 0) {
            this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_MEMBER_NOT_GUEST, info);
        }
    }

    @Override
    public boolean checkAgain(boolean killPlayer) {
        return Boolean.TRUE;
    }

    @Override
    public ErrorCode readyHandle(long playerUid, boolean checkContains) {
        this.room.addReadyPlayerUid(playerUid);
        PCLIRoomNtfMemberStateInfo memberState = new PCLIRoomNtfMemberStateInfo();
        memberState.playerUid = playerUid;
        memberState.gameType = this.room.getGameType();
        memberState.state = 1;
        this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_MEMBER_STATE, memberState);
        this.room.checkStart();
        return ErrorCode.OK;
    }

    @Override
    public void onSitup() {

    }

    @Override
    public void startCheckLeave() {

    }

    @Override
    public void doDestroy() {
        IPlayer player = PlayerManager.I.getPlayer(this.room.getOwnerPlayerUid());
        int money = getNeedRefundDiamond(this.room.getCurBureau(), this.room.getRoomType());
        if (null != player && money > 0) {
            player.addMoney(EMoneyType.DIAMOND, money, player.getUid(), 0, EMoneyExpendType.LOBBY_EXPEND_RETURN, -1);
        }
    }

    private int getNeedRefundDiamond(int tempCurBureau, ERoomType roomType) {
        if (tempCurBureau < 0 || tempCurBureau > 1) {
            return 0;
        }

        if (1 == tempCurBureau) {
            // 第一局存在打完没打完两种情况，通过curPlayerCnt判断是否打完
            return 0 == this.room.getCurPlayerCnt() ? 0 : room.getCost();
        }

        if (0 == tempCurBureau) {
            return room.getCost();
        }

        return 0;
    }

    @Override
    public long getFromClubUid(long playerUid) {
        return -1;
    }

    @Override
    public void saveRoomScore() {
        IRoomScore tempRoomScore = this.room.getRoomScore();
        if (null != tempRoomScore) {
            RoomScore roomScore = (RoomScore) tempRoomScore;
            saveRoomScore(roomScore);
            RoomScore saveRoomScore = roomScore;
            DBManager.I.save(new Task() {
                @Override
                public void run() {
                    DBManager.I.getRoomScoreDao().save(saveRoomScore);
                }
            });
        }
    }

    protected void saveRoomScore(BaseRoomScore roomScore) {
        //roomScore.setEndTime(roomScore.getEndTime());
        roomScore.setEndTime(System.currentTimeMillis());
        roomScore.getTotalScore().setTime(roomScore.getEndTime());
        IRoomPlayer[] allPlayer = this.room.getAllPlayer();
        int playerNum = this.room.getPlayerNum();
        for (int i = 0; i < playerNum; ++i) {
            IRoomPlayer temp = allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            roomScore.addScoreItemInfo(temp.getUid(), temp.getScore(Score.ACC_TOTAL_SCORE, true), room);
        }
        roomScore.setRoomType(playerNum);
        Integer maxLostScore = this.room.getRule().getOrDefault(RoomRule.RR_TOP, -1);
        if (-1 != maxLostScore) {
            int min = maxLostScore;
            for (int i = 0; i < playerNum; ++i) {
                IRoomPlayer other = allPlayer[i];
                if (null == other || other.isGuest()) {
                    continue;
                }
                int score = other.getScore(Score.ACC_TOTAL_SCORE, true) / 100;
                if (score <= 0) {
                    continue;
                }
                if (score > min) {
                    roomScore.getRecord().get(roomScore.getRecord().size() - 1)
                            .setDestroyType(ERoomDestroyType.TOP_DESTROY.ordinal());
                    roomScore.getRecord().get(roomScore.getRecord().size() - 1).setDestroyUid(other.getUid());
                    break;
                }
            }
        }
    }

    @Override
    public ErrorCode canDissolve(Player player) {
        return ErrorCode.OK;
    }

    @Override
    public IRoomPlayer createPlayer() {
        int type = this.room.getGameType();
        // TODO 更加类型创建不同玩家信息
        if (GameType.GAME_TYPE_KWX == type || GameType.GAME_TYPE_YJLY == type || GameType.GAME_TYPE_HCHH == type) {
            // 麻将
            return new MahjongPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        } else if (GameType.GAME_TYPE_RUN_FAST == type) {
            // 跑得快
            return new RunFastPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        } else if (GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER == type) {
            return new FGFPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        } else if (GameType.GAME_TYPE_COW == type) {
            return new CowPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        } else if (GameType.GAME_TYPE_THIRTEEN == type) {
            return new ThirteenPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        } else if (GameType.GAME_TYPE_PAIGOW == type) {
            return new PaiGowPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        } else if (GameType.GAME_TYPE_HUNDRED_LHD == type) {
            // 百人场-龙虎斗
            return new HundredLhdPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        } else if(GameType.GAME_TYPE_HUNDRED_BACCARAT == type){ 
            return new HundredBaccaratPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        }else if (GameType.GAME_TYPE_WHMJ == type) {
            //武汉麻将
            return new WHMJMahjongPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        }else if (GameType.GAME_TYPE_YXMJ == type) {
            //阳新麻将
            return new YXMJMahjongPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        }else if (GameType.GAME_TYPE_HSMJ == type) {
            //黄石麻将
            return new HSMJMahjongPlayer(type, this.room.getRoomUid(), this.room.getRoomId());
        }else if (GameType.GAME_TYPE_SG == type){
            return new SGPlayer(type,this.room.getRoomUid(),this.room.getRoomId());
        } else if (GameType.GAME_TYPE_HZMJ == type){
            // 红中麻将
            return new HZMJMahjongPlayer(type,this.room.getRoomUid(),this.room.getRoomId());
        }
        return null;
    }
    

    @Override
    public void record() {
        IRoomScore roomScore = room.getRoomScore();
        if (roomScore != null) {
            ScoreInfo scoreInfo = new ScoreInfo();
            scoreInfo.setTime(System.currentTimeMillis());
            scoreInfo.setUid(room.getRecord().getUid());
            scoreInfo.setDestroyType(room.getRoomDestoryType().ordinal());
            scoreInfo.setDestroyUid(room.getDestroyUid());
            IRoomPlayer[] allPlayer = room.getAllPlayer();
            for (int i = 0; i < room.getPlayerNum(); ++i) {
                IRoomPlayer temp = allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                ScoreItemInfo itemInfo = new ScoreItemInfo();
                itemInfo.setScore(room.getRecordScore(temp));
                itemInfo.setPlayerUid(temp.getUid());
                scoreInfo.getScore().add(itemInfo);
            }
            roomScore.addRecord(scoreInfo);
        }
    }
    
    @Override
    public void tickHandle(long curTime, long delay) {
        
    }
    
    @Override
    public ErrorCode offline(Player player) {
        return ErrorCode.OK;
    }
}
