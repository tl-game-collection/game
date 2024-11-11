package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.*;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.*;
import com.xiuxiu.app.server.room.player.mahjong2.*;
import com.xiuxiu.app.server.room.record.mahjong2.*;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMahjongRoom extends Room implements IMahjongRoom, IMahjongHuanPai, IMahjongShuaiPai, IMahjongDingQue, IMahjongLaiZi, IMahjongXuanZeng, IMahjongXuanPiao, IMahjongStartHu, IMahjongLastCard {
    public static final long HUANG_PAI_TIMEOUT = 30 * 1000L;            // 换牌等待时间
    public static final long SHUAI_PAI_TIMEOUT = 30 * 1000L;            // 摔牌等待时间
    public static final long DING_QUE_TIMEOUT = 30 * 1000L;             // 定缺等待时间
    public static final long XUAN_ZENG_TIMEOUT = 30 * 1000L;            // 选增等待时间
    public static final long XUAN_PIAO_TIMEOUT = 30 * 1000L;            // 选票等待时间
    public static final long HU_TIMEOUT = 3 * 1000L;                    // 胡等待时间
    public static final long FUMBLE_HU_TIMEOUT = 10 * 1000L;            // 胡等待时间
    public static final long START_HU_TIMEOUT = 5 * 1000L;              // 起手胡等待时间
    public static final long LAST_CARD_SELECT_TIMEOUT = 10 * 1000L;     // 最后一张牌选择等待时间
    public static final long LAST_CARD_TAKE_TIMEOUT = 3 * 1000L;        // 最后一张牌打出时间
    public static final long CS_OPEN_BAR_TIMEOUT = 10 * 1000L;          // 长沙开杠选择操作时间
    public static final long AUTO_TAKE_TIMEOUT = 3 * 1000L;             // 自动打等待时间
    
    public BaseMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public BaseMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void beginHuanPai() {
        MahjongHuanPaiAction action = new MahjongHuanPaiAction(this, HUANG_PAI_TIMEOUT);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addPlayer(player);
        }
        this.addAction(action);
        PCLIMahjongNtfBeginHuanPai info = new PCLIMahjongNtfBeginHuanPai();
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_HUAN_PAI, info,false);
    }

    @Override
    public void endHuanPai() {
        HuanPaiRecordAction action = ((MahjongRecord) this.getRecord()).addHuanPaiAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            List<Byte> handCard = new ArrayList<>();
            player.addHandCardTo(handCard);
            action.addHuanPai(player.getUid(), handCard);
        }
    }

    @Override
    public ErrorCode huanPai(IPlayer player, List<Byte> cards) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法换牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法换牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (null == cards || 3 != cards.size()) {
            Logs.ROOM.warn("%s %s 牌不对:%s, 无法换牌", this, player, cards);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongHuanPaiAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法换牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            int card = cards.get(0) & 0x3F;
            card |= ((cards.get(1) & 0x3F) << 6);
            card |= ((cards.get(2) & 0x3F) << 12);
            ErrorCode err = ((MahjongHuanPaiAction) action).huanPai(mahjongPlayer, card);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是换牌动作, 无法换牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginHuanPai(IMahjongPlayer player, int card, int cnt) {
        PCLIMahjongNtfBeginHuanPai info = new PCLIMahjongNtfBeginHuanPai();
        for (int i = 0; i < cnt; ++i) {
            if (-1 != card) {
                info.card.add((byte) ((card >> (6 * i)) & 0x3F));
            }
        }

        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_HUAN_PAI, info);
    }

    @Override
    public void doSendHuanPaiInfo(IMahjongPlayer player) {
        PCLIMahjongNtfHuanPaiInfo info = new PCLIMahjongNtfHuanPaiInfo();
        info.playerUid = player.getUid();
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_HUAN_PAI_INFO, info);
    }

    @Override
    public void doSendEndHuanPai(IMahjongPlayer player, int type, int myCard, int card, int cnt) {
        PCLIMahjongNtfEndHuanPai info = new PCLIMahjongNtfEndHuanPai();
        info.type = type;
        for (int i = 0; i < cnt; ++i) {
            if (-1 != card) {
                info.card.add((byte) ((card >> (6 * i)) & 0x3F));
            }
            if (-1 != myCard) {
                info.myCard.add((byte) ((myCard >> (6 * i)) & 0x3F));
            }
        }

        player.send(CommandId.CLI_NTF_MAHJONG_END_HUAN_PAI, info);
        Logs.ROOM.debug("%s %s", this, player);
    }

    @Override
    public void beginShuaiPai() {
        MahjongShuaiPaiAction action = new MahjongShuaiPaiAction(this, SHUAI_PAI_TIMEOUT);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addPlayer(player);
        }
        this.addAction(action);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_SHUAI_PAI, null);
    }

    @Override
    public void endShuaiPai() {
        ShuaiPaiRecordAction  action = ((MahjongRecord) this.getRecord()).addShuaiPaiAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            List<Byte> list = new ArrayList<>();
            player.addCPGAnyThreeTypeTo(list);
            action.addShuaiPai(player.getUid(), list);
        }
    }

    @Override
    public ErrorCode shuaiPai(IPlayer player, List<Byte> cards) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法甩牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法甩牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (null == cards || (!cards.isEmpty() && 3 != cards.size())) {
            Logs.ROOM.warn("%s %s 牌不对:%s, 无法甩牌", this, player, cards);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongShuaiPaiAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法甩牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            int card = 0;
            if (cards.isEmpty()) {
                card = 0;
            } else {
                card = cards.get(0) & 0x3F;
                card |= ((cards.get(1) & 0x3F) << 6);
                card |= ((cards.get(2) & 0x3F) << 12);
            }

            ErrorCode err = ((MahjongShuaiPaiAction) action).shuaiPai(mahjongPlayer, card);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是甩牌动作, 无法甩牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginShuaiPai(IMahjongPlayer player, int card, int cnt) {
        PCLIMahjongNtfBeginShuaiPai info = new PCLIMahjongNtfBeginShuaiPai();
        for (int i = 0; i < cnt; ++i) {
            if (-1 != card && 0 != card) {
                info.card.add((byte) ((card >> (6 * i)) & 0x3F));
            }
        }
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_SHUAI_PAI, info);
    }

    @Override
    public void doSendEndShuaiPai() {
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_END_SHUAI_PAI, null);
        if (Switch.DEBUG) {
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                Logs.ROOM.debug("%s %s", this, player);
            }
        }
    }

    @Override
    public void doSendShuaiPaiInfo(IMahjongPlayer player, int card, int cnt) {
        PCLIMahjongNtfShuaiPaiInfo info = new PCLIMahjongNtfShuaiPaiInfo();
        info.playerUid = player.getUid();
        for (int i = 0; i < cnt; ++i) {
            if (-1 != card && 0 != card) {
                info.card.add((byte) ((card >> (6 * i)) & 0x3F));
            }
        }
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SHUAI_PAI_INFO, info);
    }

    @Override
    public void beginDingQue() {
        MahjongDingQueAction action = new MahjongDingQueAction(this, DING_QUE_TIMEOUT);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addPlayer(player);
        }
        this.addAction(action);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_DING_QUE, null);
    }

    @Override
    public void endDingQue() {
        DingQueRecordAction action = ((MahjongRecord) this.getRecord()).addDingQueAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addDingQue(player.getUid(), ((IDingQue) player).getQue());
        }
    }

    @Override
    public ErrorCode dingQue(IPlayer player, int color) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法定缺", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法定缺", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (color < MahjongUtil.COLOR_WANG || color > MahjongUtil.COLOR_HUA) {
            Logs.ROOM.warn("%s %s 颜色不对:%s, 无法定缺", this, player, color);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongDingQueAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法定缺", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongDingQueAction) action).dingQue(mahjongPlayer, color);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是定缺动作, 无法定缺", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginDingQue() {
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_DING_QUE, null);
    }

    @Override
    public void doSendBeginDingQue(IMahjongPlayer player) {
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_DING_QUE, null);
    }

    @Override
    public void doSendEndDingQue() {
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_END_DING_QUE, null);
    }

    @Override
    public void doSendDingQueInfo(IMahjongPlayer player, int color) {
        PCLIMahjongNtfDingQueInfo info = new PCLIMahjongNtfDingQueInfo();
        info.playerUid = player.getUid();
        info.color = color;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_DING_QUE_INFO, info);
    }

    @Override
    public void beginXuanZeng(int type) {
        if (4 == type) {
            MahjongXuanZengAction action = new MahjongXuanZengAction(this, XUAN_ZENG_TIMEOUT);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                action.addPlayer(player);
            }
            this.addAction(action);
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_XUAN_ZENG, null);
        } else {
            if (type > 0 && type < 4) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    ((IXuanZeng) player).setZeng(type);
                }
            }
            this.endXuanZeng();
        }
    }

    @Override
    public void endXuanZeng() {
        XuanZengRecordAction action = ((MahjongRecord) this.getRecord()).addXuanZengAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addXuanZeng(player.getUid(), ((IXuanZeng) player).getZeng());
        }
    }

    @Override
    public ErrorCode xuanZeng(IPlayer player, int value) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法选增", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法选增", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (value < 0 || value > 3) {
            Logs.ROOM.warn("%s %s 选增值不对:%s, 无法选增", this, player, value);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongXuanZengAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法起选增", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongXuanZengAction) action).xuanZeng(mahjongPlayer, value);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是选增动作, 无法选增", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginXuanZeng(IMahjongPlayer player) {
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_XUAN_ZENG, null);
    }

    @Override
    public void doSendEndXuanZeng() {
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_END_XUAN_ZENG, null);
    }

    @Override
    public void doSendXuanZengInfo(IMahjongPlayer player, int value) {
        PCLIMahjongNtfXuanZengInfo info = new PCLIMahjongNtfXuanZengInfo();
        info.playerUid = player.getUid();
        info.value = value;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_XUAN_ZENG_INFO, info);
    }

    @Override
    public void beginXuanPiao() {
        MahjongXuanPiaoAction action = new MahjongXuanPiaoAction(this, XUAN_PIAO_TIMEOUT);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addPlayer(player);
        }
        this.addAction(action);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_XUAN_PIAO, null);
    }

    @Override
    public void endXuanPiao() {
        XuanPiaoRecordAction action = ((MahjongRecord) this.getRecord()).addXuanPiaoAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addXuanPiao(player.getUid(), ((IXuanPiao) player).getPiao());
        }
    }

    @Override
    public ErrorCode xuanPiao(IPlayer player, int value) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法选票", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法选票", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (value < 0 || value > 3) {
            Logs.ROOM.warn("%s %s 选增票不对:%s, 无法选票", this, player, value);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongXuanPiaoAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法起选票", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongXuanPiaoAction) action).xuanPiao(mahjongPlayer, value);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是选票动作, 无法选票", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginXuanPiao(IMahjongPlayer player) {
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_XUAN_PIAO, null);
    }

    @Override
    public void doSendEndXuanPiao() {
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_END_XUAN_PIAO, null);
    }

    @Override
    public void doSendXuanPiaoInfo(IMahjongPlayer player, int value) {
        PCLIMahjongNtfXuanPiaoInfo info = new PCLIMahjongNtfXuanPiaoInfo();
        info.playerUid = player.getUid();
        info.value = value;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_XUAN_PIAO_INFO, info);
    }

    @Override
    public void beginStartHu() {
        boolean has = false;
        MahjongStartHuAction action = new MahjongStartHuAction(this, START_HU_TIMEOUT);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (this.isStartHu(player)) {
                action.addPlayer(player);
                has = true;
            }
        }
        if (has) {
            this.addAction(action);
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_START_HU, null);
        } else {
            this.endStartHu(false);
        }
    }

    @Override
    public void endStartHu(boolean has) {
        if (has) {
            StartHuRecordAction action = ((MahjongRecord) this.getRecord()).addStartHuAction();
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                action.addStartHu(player.getUid(), ((IStartHu) player).getHu());
            }
        }
    }

    @Override
    public ErrorCode startHu(IPlayer player, boolean select) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法起手胡", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法起手胡", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongStartHuAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法起手胡", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongStartHuAction) action).startHu(mahjongPlayer, select);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是起手胡动作, 无法起手胡", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginStartHu(IMahjongPlayer player, Object msg) {
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_START_HU, msg);
    }

    @Override
    public void doSendEndStartHu() {
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_END_START_HU, null);
    }

    @Override
    public void doSendStartHuInfo(IMahjongPlayer player, boolean select) {
        PCLIMahjongNtfStartHuInfo info = new PCLIMahjongNtfStartHuInfo();
        info.playerUid = player.getUid();
        info.selected = select;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_START_HU_INFO, info);
    }

    @Override
    public void beginLastCard(IMahjongPlayer player) {
        MahjongLastCardAction action = new MahjongLastCardAction(this);
        action.setStartIndex(player.getIndex(), this.allCard.peek());
        action.start();
        this.addAction(action);
    }

    @Override
    public void endLastCard(IMahjongPlayer player, byte card) {
        if (null == player) {
            this.onHuangZhuang(this.curBureau < this.bureau);
        } else {
            ((MahjongRecord) this.getRecord()).addLastCardAction(player.getUid(), card);

            PCLIMahjongNtfLastCardInfo info = new PCLIMahjongNtfLastCardInfo();
            info.card = card;
            info.uid = player.getUid();
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_LAST_CARD, info);

            this.onFumble(player);
        }
    }

    @Override
    public ErrorCode selectLastCard(IPlayer player, boolean select) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法选择最后一张牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法选择最后一张牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongLastCardAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法选择最后一张牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongLastCardAction) action).select(mahjongPlayer, select);
//            if (ErrorCode.OK == err) {
//                this.tick();
//            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是最后一张牌动作, 无法选择最后一张牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginLastCard(IMahjongPlayer player) {
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_LAST_CARD, null);
    }
    
}
