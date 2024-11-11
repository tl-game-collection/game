package com.xiuxiu.app.server.room.normal.poker.thirteen;

import com.xiuxiu.algorithm.poker.CardModel;
import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.algorithm.poker.TypeCard;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByPoker;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.ThirteenTakeAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.app.server.room.player.poker.ThirteenPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.RecordPokerPlayerBriefInfo;
import com.xiuxiu.app.server.room.record.poker.ThirteenResultRecordAction;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.*;

@GameInfo(gameType = GameType.GAME_TYPE_THIRTEEN)
public class ThirteenRoom extends PokerRoom {
    /**
     * 十三水每人手里牌的个数
     */
    private static final int HAND_CARD_COUNT = 13;
    /**
     * 一副牌拥有牌的个数
     */
    private static final int ONE_CARD_COUNT = 54;
    /**
     * 一副牌除去大小王剩余牌的个数
     */
    private static final int ONE_CARD_EXCEPT_JOKER = ONE_CARD_COUNT - 2;
    /**
     * 扑克牌花色 方块
     */
    private static final int DIAMONDS_COLOR = 0;
    /**
     * 扑克牌花色 梅花
     */
    private static final int CLUBS_COLOR = DIAMONDS_COLOR + 1;
    /**
     * 扑克牌花色 红桃
     */
    private static final int HEARTS_COLOR = CLUBS_COLOR + 1;
    /**
     * 扑克牌花色 黑桃
     */
    private static final int SPADES_COLOR = HEARTS_COLOR + 1;

    /**
     * 中墩，尾墩牌数
     */
    private static final int MEDIUM_TAIL_CARD_COUNT = 5;
    /**
     * 首墩牌数
     */
    private static final int HEAD_CARD_COUNT = 3;

    /**
     * 客户端配置房间类型 1 普通房间
     */
    private int thirteenRoomType;
    /**
     * 客户端配置是否通过比花色计算牌大小
     */
    private boolean useColor;
    /**
     * 客户端配置是否二人不计算打枪
     */
    private boolean twoNoShoot;
    /**
     * 客户端配置是否强袭不打枪
     */
    private boolean strikeNoShoot;
    /**
     * 客户端配置打枪计算分数方式 1 计分加1 2 计分乘以2
     */
    private int shootType;
    /**
     * 客户端配置打枪分值
     */
    private int shootValue;
    /**
     * 客户端配置理牌时间单位s 60s 90s 120s
     */
    private int handlerTime;
    /**
     * 客户端配置限制竞技值
     */
    private int robLessArenaValue = 100;

    /**
     * 每个玩家怪物牌型记录
     * key playerUid
     * value EPokerCardType
     */
	protected HashMap<Long, EPokerCardType> monsterRecord = new HashMap<>();
    /**
     * 每个玩家首牌记录
     * key playerUid
     * value EPokerCardType
     */
	protected HashMap<Long, EPokerCardType> headRank = new HashMap<>();
    /**
     * 每个玩家中牌记录
     * key playerUid
     * value EPokerCardType
     */
	protected HashMap<Long, EPokerCardType> mediumRank = new HashMap<>();
    /**
     * 每个玩家尾牌记录
     * key playerUid
     * value EPokerCardType
     */
	protected HashMap<Long, EPokerCardType> tailRank = new HashMap<>();
    /**
     * 已出牌玩家记录
     * value 玩家uid
     */
	protected List<Long> allTakeCard = new ArrayList<>();
    /**
     * 记录当前局参与的玩家数量(开局的时候记录一下正在参加的玩家数量，防止中途玩家加入数量更改)
     */
	protected int onPlayerNum;
    /**
     * 通杀玩家uid
     */
    private long passKillPlayerUid = 0;

	public ThirteenRoom(RoomInfo info) {
		super(info, ERoomType.NORMAL);
	}

	public ThirteenRoom(RoomInfo info, ERoomType roomType) {
		super(info, roomType);
	}

    /**
     * 房间初始化
     */
	@Override
	public void init() {
		super.init();
		this.playerNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_NUM, 4);
		this.playerMinNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_MIN_NUM, 2);
		this.allPlayer = new IRoomPlayer[this.playerNum];

		// play type
		this.useColor = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EThirteenPlayRule.USE_COLOR.getValue());

		// 两人不打枪
		this.twoNoShoot = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EThirteenPlayRule.TWO_NO_SHOOT.getValue());
		// 强袭不打枪
		this.strikeNoShoot = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EThirteenPlayRule.STRIKE_NO_SHOOT.getValue());

		// room type
		this.thirteenRoomType = this.info.getRule().getOrDefault(RoomRule.RR_THIRTEEN_ROOM_TYPE, 0);
		this.shootType = this.info.getRule().getOrDefault(RoomRule.RR_THIRTEEN_SHOOT, 1);
		this.handlerTime = this.info.getRule().getOrDefault(RoomRule.RR_THIRTEEN_TIME, 60);
		this.robLessArenaValue = this.info.getRule().getOrDefault(RoomRule.RR_DEPUTY_DIVIDE, 0);
		this.shootValue = (3 == this.shootType) ? 6 : (this.shootType == 2) ? 2 : 1;
        this.autoReady = false;

    }

    /**
     * 洗牌
     */
	@Override
	protected void doShuffle() {
		if (Switch.USE_CARD_LIB_POKER) {
			this.allCard.addAll(CardLibraryManager.I.getMahjongCard());
			return;
		}
		onPlayerNum = this.getCurPlayerCnt();// 当前局正在参与的人数（这个地方记录一下，防止中途加入后，这个值变化）
		for (byte i = 0; i < ONE_CARD_EXCEPT_JOKER; ++i) {
			this.allCard.add(i);
			// 每增加一人 增加一种花色；
			if (this.onPlayerNum == 5 && PokerUtil.getCardColor(i) == SPADES_COLOR) {
				this.allCard.add(i);
			} else if (this.onPlayerNum == 6 && (PokerUtil.getCardColor(i) == HEARTS_COLOR || PokerUtil.getCardColor(i) == SPADES_COLOR)) {
				this.allCard.add(i);
			} else if (this.onPlayerNum == 7 && PokerUtil.getCardColor(i) != DIAMONDS_COLOR) {
				this.allCard.add(i);
			} else if (this.onPlayerNum >= 8) {
				this.allCard.add(i);
			}
		}
		ShuffleUtil.shuffle(this.allCard);
    }


    /**
     * 设置好牌
     * @param playerGoodCards
     */
    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {
    }

    /**
     * 发牌
     */
    @Override
    protected void doDeal() {
        if (-1 == this.bankerIndex) {
            List<Integer> inGameIndexes = new ArrayList<>();
            for (int i = 0 ; i < this.allPlayer.length; i++){
                IRoomPlayer player = this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                inGameIndexes.add(i);
            }
            if (inGameIndexes.size() <= 0){
                Logs.ROOM.warn("thirteenRoom allPlayer error roomId: %d",this.getRoomId());
                return;
            }
            this.bankerIndex = inGameIndexes.get(RandomUtil.random(0,inGameIndexes.size()-1));
        }

        for (int i = 0; i < HAND_CARD_COUNT; ++i) {
            for (int j = this.bankerIndex, k = 0; k < this.playerNum; ++k) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[(j + k) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }
                player.addHandCard(this.allCard.removeFirst());
            }
        }
    }

    /**
     *发送开局信息给客户端
     */
    @Override
    protected void doSendGameStart() {
        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.initHandCard();
            PokerUtil.sort(player.getHandCard());

            this.sortBestCards(player);

            this.getRecord().addPlayer(
                    new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getBureau(), player.getHandCard()));

            PCLIRoomNtfBeginInfoByPoker roomBeginInfo = new PCLIRoomNtfBeginInfoByPoker();
            roomBeginInfo.myIndex = player.getIndex();
            roomBeginInfo.myCards = player.getHandCard();
            roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
            roomBeginInfo.bureau = player.getBureau();
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        }

        PCLIRoomNtfBeginInfoByPoker roomBeginInfo = new PCLIRoomNtfBeginInfoByPoker();
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }


    /**
     * 游戏流程正式开始
     */
    @Override
	protected void doStart1() {
		//// 理牌：60s 90s 120s 默认60s
		if (3 != this.thirteenRoomType) {
			ThirteenTakeAction action = new ThirteenTakeAction(this, this.handlerTime * 1000);
			this.addAction(action);
		}

		// 2人不打枪
		this.twoNoShoot = (this.twoNoShoot == true) && (this.onPlayerNum == 2);
	}

    /**
     * 本局游戏结束
     * @param next
     */
	@Override
	protected void gameOver(boolean next) {
		ThirteenResultRecordAction action = ((PokerRecord) this.getRecord()).addThirteenResultRecordAction();

		for (int i = 0; i < this.playerNum; ++i) {
			ThirteenPlayer player = (ThirteenPlayer) this.allPlayer[i];
			if (null == player || player.isGuest()) {
				continue;
			}
			int curScore = player.getScore(Score.SCORE, false)
					+ player.getScore(Score.POKER_THIRTEEN_MONSTER_SCORE, false);
			Logs.ROOM.debug("%s player = %s curScore = %s shoot = %s head = %s medium = %s tail = %s SCORE = %s ", this, player.getUid(),
					curScore, player.getScore(Score.POKER_THIRTEEN_SHOOT_SCORE, false),
					player.getScore(Score.POKER_THIRTEEN_HEAD_SCORE, false), player.getScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, false),
					player.getScore(Score.POKER_THIRTEEN_TAIL_SCORE, false), player.getScore(Score.SCORE, false));

			player.setScore(Score.SCORE, curScore, false);
			int bureauScore = player.getScore(Score.SCORE, false);
			player.addScore(Score.ACC_TOTAL_SCORE, bureauScore, true);
			player.maxScore(Score.ACC_MAX_SCORE, bureauScore, true);
			if (bureauScore > 0) {
				player.addScore(Score.ACC_WIN_CNT, 1, true);
			}
			if (bureauScore < 0) {
				player.addScore(Score.ACC_LOST_CNT, 1, true);
			}
			ArrayList<Byte> cards = new ArrayList<>();
			cards.addAll(player.getHeadCard());
			cards.addAll(player.getMediumCard());
			cards.addAll(player.getTailCard());
			action.addResult(player.getUid(), cards, this.getFormatScore(player.getScore(Score.SCORE, false)),
					this.getFormatScore(player.getScore(Score.ACC_TOTAL_SCORE, true)),
					this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_HEAD_SCORE, false)),
					this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, false)),
					this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_TAIL_SCORE, false)));
		}
        this.getRoomHandle().calculateGold();//小局结束后抽水
		this.record();
		this.getRecord().save();
    }

    /**
     * 发送本局游戏结束到客户端
     * @param next
     */
	@Override
	protected void doSendGameOver(boolean next) {
		// 通知结果
		PCLIPokerNtfThirteenGameOverInfo thirteenResult = new PCLIPokerNtfThirteenGameOverInfo();
		thirteenResult.next = next;
		// 通杀
		if (this.onPlayerNum > 3 && 3 != this.thirteenRoomType) {
			thirteenResult.passKillPlayerUid = this.passKillPlayerUid;
		}

		for (int i = 0; i < this.playerNum; ++i) {
			ThirteenPlayer player = (ThirteenPlayer) this.allPlayer[i];
			if (null == player || player.isGuest()) {
				continue;
			}

			PCLIPokerNtfThirteenGameOverInfo.PlayerInfo playerInfo = new PCLIPokerNtfThirteenGameOverInfo.PlayerInfo();
			IPlayer iPlayer = player.getPlayer();
			if (null != iPlayer) {
				playerInfo.name = iPlayer.getName();
				playerInfo.icon = iPlayer.getIcon();
			}
			// 打枪处理
			if (!this.twoNoShoot) {
			    Map<Long,Integer> map=player.getShootPlayerList();
                playerInfo.shootScore = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_SHOOT_SCORE, false));
                for (Map.Entry<Long,Integer> entry : map.entrySet()) {
					PCLIPokerNtfThirteenGameOverInfo.ShootList shootInfo = new PCLIPokerNtfThirteenGameOverInfo.ShootList();
					shootInfo.playerUid = player.getUid();
                    playerInfo.shootPlayerList.add(entry.getKey());
                    shootInfo.shootPlayerUid = entry.getKey();
                    shootInfo.shootScore=entry.getValue();
					thirteenResult.shootList.add(shootInfo);
				}
			}

			playerInfo.playerUid = player.getUid();
			//playerInfo.totalScore = this.getFormatScore(player.getScore());
            IArenaRoomPlayerHelper roomPlayerHelper = (IArenaRoomPlayerHelper) player.getRoomPlayerHelper();
            playerInfo.totalScore = this.getClientScore((int)roomPlayerHelper.getGold());
			playerInfo.cards.addAll(player.getHeadCard());
			playerInfo.cards.addAll(player.getMediumCard());
			playerInfo.cards.addAll(player.getTailCard());

			int curScore = player.getScore(Score.POKER_THIRTEEN_SHOOT_SCORE, false)
					+ player.getScore(Score.POKER_THIRTEEN_HEAD_SCORE, false) + player.getScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, false)
					+ player.getScore(Score.POKER_THIRTEEN_MONSTER_SCORE, false) + player.getScore(Score.POKER_THIRTEEN_TAIL_SCORE, false);
			// 怪物牌型
			playerInfo.monsterType = player.getMonsterType().getValue();
			playerInfo.monsterScore = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_MONSTER_SCORE, false));
			playerInfo.bureauScore = this.getFormatScore(player.getScore(Score.SCORE, false));

			// 普通牌型
			for (int num = 1; num <= 3; num++) {
				PCLIPokerNtfThirteenGameOverInfo.BoutInfo boutInfo = new PCLIPokerNtfThirteenGameOverInfo.BoutInfo();
				boutInfo.bout = num;
				if (1 == num) {
					// 头道
					boutInfo.cardType = player.getHeadType().getValue();
					boutInfo.score = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_HEAD_SCORE, false));
                    boutInfo.cardScore = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_HEAD_CARD_SCORE, false));

                } else if (2 == num) {
					// 中道
					boutInfo.cardType = player.getMediumType().getValue();
					boutInfo.score = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, false));
                    boutInfo.cardScore = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_MEDIUM_CARD_SCORE, false));

                } else if (3 == num) {
					// 尾道
					boutInfo.cardType = player.getTailType().getValue();
					boutInfo.score = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_TAIL_SCORE, false));
                    boutInfo.cardScore = this.getFormatScore(player.getScore(Score.POKER_THIRTEEN_TAIL_CARD_SCORE, false));

                }
				playerInfo.boutInfo.add(boutInfo);
			}

			if (!next) {
				PCLIPokerNtfThirteenGameOverInfo.TotalCnt totalCnt = new PCLIPokerNtfThirteenGameOverInfo.TotalCnt();
				totalCnt.winCnt = player.getScore(Score.ACC_WIN_CNT, true);
				totalCnt.lostCnt = player.getScore(Score.ACC_LOST_CNT, true);
				totalCnt.maxScore = this.getFormatScore(player.getScore(Score.ACC_MAX_SCORE, true));
				totalCnt.maxCardType = player.getScore(Score.ACC_POKER_MAX_CARD_TYPE, true);
				playerInfo.totalCnt = totalCnt;
			}

			thirteenResult.resultInfo.add(playerInfo);
		}

		this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, thirteenResult);
	}

    /**
     * 房间清理数据
     */
    @Override
    public void clear() {
        super.clear();
        this.passKillPlayerUid = 0;

        this.allTakeCard.clear();
        this.headRank.clear();
        this.mediumRank.clear();
        this.tailRank.clear();
        this.monsterRecord.clear();
    }

    /**
     * 同步牌局信息
     * @param player
     */
    @Override
    public void syncDeskInfo(IPlayer player) {
        ThirteenPlayer thirteenPlayer = (ThirteenPlayer) this.getRoomPlayer(player.getUid());
        if (null == thirteenPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIPokerNtfThirteenDeskInfo deskInfo = new PCLIPokerNtfThirteenDeskInfo();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.curBureau = null == thirteenPlayer ? 0 : thirteenPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.remain = this.timeout;
        if (!this.action.empty()) {
            IAction action = this.action.peek();
            if (null != action && (action instanceof ThirteenTakeAction)) {
                deskInfo.remain = (int) (((ThirteenTakeAction) action).getRemain() / 1000);
            }
        }
        deskInfo.allTakeCard.addAll(allTakeCard);
        deskInfo.thirteenRoomType = this.thirteenRoomType;

        if (this.thirteenRoomType == 3) {
            deskInfo.bankerPlayerid = this.allPlayer[this.bankerIndex].getUid();
            if (null != thirteenPlayer && !thirteenPlayer.isGuest()) {
                deskInfo.card.addAll(thirteenPlayer.getHandCard());
            }
            for (int i = 0; i < this.playerNum; ++i) {
                IPokerPlayer temp = (IPokerPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                deskInfo.allScore.put(temp.getUid(), this.getClientScore(temp.getScore()));
                deskInfo.allOnlineState.put(temp.getUid(), temp.isOffline() ? false : true);
                deskInfo.allRebetMul.put(temp.getUid(), temp.getScore(Score.POKER_THIRTEEN_REBET, false));
                deskInfo.allRobBank.put(temp.getUid(), temp.getScore(Score.POKER_THIRTEEN_ROB_BANKER_MUL, false));
            }
        } else {
            if (null != thirteenPlayer && !thirteenPlayer.isGuest()) {
                deskInfo.card.addAll(thirteenPlayer.getHandCard());
            }
        }

        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    /**
     * 结束本局
     */
    public void onOver() {
        if (1 == this.thirteenRoomType) {
            setNormalResult();
        }

        // 通杀分数处理
        if (this.onPlayerNum > 3 && 0 != this.passKillPlayerUid && 3 != this.thirteenRoomType) {
            for (int i = 0; i < this.playerNum; ++i) {
                int index = i % this.playerNum;
                ThirteenPlayer player = (ThirteenPlayer) this.allPlayer[index];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (player.getUid() != this.passKillPlayerUid) {
                    this.setPassKillScore(player);
                }

                if ((player.getUid() == this.passKillPlayerUid) && (1 == this.shootType || 2 == this.shootType)) {
                    int playerShootScore = player.getScore(Score.POKER_THIRTEEN_SHOOT_SCORE, false) * 2;
//                    player.setScore(Score.POKER_THIRTEEN_SHOOT_SCORE, playerShootScore, false);
                    player.setScore(Score.SCORE, playerShootScore, false);


                }
            }
        }
        this.gameOver(this.checkAgain());
        this.stop();
    }

    /**
     * 是否有下一轮
     * @return
     */
    @Override
    protected boolean checkAgain() {
        return true;
    }
    /**
     * 手动出牌
     * @param player
     * @param cards
     * @param monsterType
     * @param headType
     * @param mediumType
     * @param tailType
     * @return
     */
    public ErrorCode thirteenTake(Player player, List<Byte> cards, int monsterType, int headType, int mediumType, int tailType) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法打牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法打牌", this, player);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());

        if (this.allTakeCard.contains(player.getUid())) {
            Logs.ROOM.warn("%s playerUid:%d 已经出过牌", this, player.getUid());
            return ErrorCode.REPEAT_OPERATE;
        }

        if (13 != cards.size()) {
            Logs.ROOM.warn("%s playerUid:%d 出牌数量异常  cards  size =  %s ", this, player.getUid(), cards.size());
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        for (int i = 0, len = cards.size(); i < len; i++) {
            if (null == cards.get(i)) {
                Logs.ROOM.warn("%s playerUid:%d 出牌有为 null  cards  size =  %s ", this, player.getUid(), cards.size());
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }

        IAction action = this.action.peek();
        if (action instanceof ThirteenTakeAction) {
            return this.checkTake0(action, roomPlayer, cards, monsterType, headType, mediumType, tailType);
        }
        Logs.ROOM.warn("%s 本轮不是打牌动作, 无法打牌", this);
        return ErrorCode.REQUEST_INVALID_DATA;
    }

    /**
     * 重新整理牌型
     * @param player
     * @return
     */
    public ErrorCode sortCard(Player player) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法打牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer iPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());

        List<CardModel> cardModelList = new ArrayList<>(); // 推荐牌型
        cardModelList.addAll(((ThirteenPlayer) iPlayer).getCardModelList());
        PCLIPokerNtfThirteenSortCardInfo info = new PCLIPokerNtfThirteenSortCardInfo();
        if (0 != cardModelList.size()) {
            // 怪物牌型
            info.monsterType = ((ThirteenPlayer) iPlayer).getMonsterType().getValue();

            // 普通牌型
            for (CardModel var : cardModelList) {
                PCLIPokerNtfThirteenSortCardInfo.cardType newCardType = new PCLIPokerNtfThirteenSortCardInfo.cardType();

                // 头道
                newCardType.cards.addAll(var.getTypeCardList().get(2).getCardList());
                newCardType.headCard.addAll(var.getTypeCardList().get(2).getCardList());
                newCardType.headType = var.getTypeCardList().get(2).getCardType().getValue();
                // 中道
                newCardType.cards.addAll(var.getTypeCardList().get(1).getCardList());
                newCardType.mediumCard.addAll(var.getTypeCardList().get(1).getCardList());
                newCardType.mediumType = var.getTypeCardList().get(1).getCardType().getValue();
                // 尾道
                newCardType.cards.addAll(var.getTypeCardList().get(0).getCardList());
                newCardType.tailCard.addAll(var.getTypeCardList().get(0).getCardList());
                newCardType.tailType = var.getTypeCardList().get(0).getCardType().getValue();

                info.list.add(newCardType);
                Logs.ROOM.debug("%s  请求理牌 newCardType cards = %s  tail card = %s medium card = %s head card = %s ", this, newCardType.cards,
                        var.getTypeCardList().get(0).getCardList(), var.getTypeCardList().get(1).getCardList(),
                        var.getTypeCardList().get(2).getCardList());
            }
        }
        player.send(CommandId.CLI_NTF_POKER_THIRTEEN_SORT_CARD_OK, info);
        return ErrorCode.OK;
    }

	/////////////////////功能函数/////////////////////////////
    /**
     * 最佳牌型排序
     * @param pokerPlayer
     */
    private void sortBestCards(IPokerPlayer pokerPlayer){
        ThirteenPlayer player = (ThirteenPlayer) pokerPlayer;
        // 怪物牌型
        this.checkMonsterType(player, player.getHandCard());

        // 普通牌型推荐
        List<TypeCard> tailCardList = PokerUtil.getMaxCardType(player.getHandCard());
        for (int i = 0; i < tailCardList.size(); i++) {
            if (i > 3){
                break;
            }
            //先确定尾墩牌型
            TypeCard tailTypeCard = tailCardList.get(i);

            List<Byte> mediumSurplusCardList = getSurplusList(player.getHandCard(),tailTypeCard.getCardList());
            List<TypeCard> mediumCardList = PokerUtil.getMaxCardType(mediumSurplusCardList);
            for (int j = 0; j < mediumCardList.size(); j++) {
                if (j > 2){
                    break;
                }
                //先确定中墩牌型
                TypeCard mediumTypeCard = mediumCardList.get(j);
                if (mediumTypeCard.getCardType().getValue() > tailTypeCard.getCardType().getValue()) {
                    continue;
                }

                List<Byte> headSurplusCardList = getSurplusList(mediumSurplusCardList,mediumTypeCard.getCardList());
                List<TypeCard> headCardList = PokerUtil.getMaxCardType(headSurplusCardList);
                for (int n = 0; n < headCardList.size(); n++) {
                    if (n > 2){
                        break;
                    }
                    //确定首墩牌型
                    TypeCard headTypeCard = headCardList.get(n);
                    if (headTypeCard.getCardType() != EPokerCardType.DUI_ZI
                            && headTypeCard.getCardType() != EPokerCardType.ZA_PAI
                            && headTypeCard.getCardType() != EPokerCardType.SAN_TIAO) {
                        continue;
                    }

                    if (headTypeCard.getCardType().getValue() > mediumTypeCard.getCardType().getValue()) {
                        continue;
                    }

                    List<Byte> selectHeadCardList = new ArrayList<>();
                    List<Byte> selectMediumCardList = new ArrayList<>();
                    List<Byte> selectTailCardList = new ArrayList<>();
                    List<Byte> surplusCardList = getSurplusList(headSurplusCardList,headTypeCard.getCardList());
                    boolean completeCardOk = surplusCardList.size() <= 0;
                    if (surplusCardList.size() > 0) {
                        List<Byte> tempSurplusCardList = new ArrayList<>();
                        for (int m = 0; m < 30; m++) {
                            selectHeadCardList.clear();
                            selectMediumCardList.clear();
                            selectTailCardList.clear();
                            tempSurplusCardList.clear();

                            if (headTypeCard.getCardType() == EPokerCardType.ZA_PAI){
                                selectMediumCardList.addAll(mediumTypeCard.getCardList());
                                selectTailCardList.addAll(tailTypeCard.getCardList());
                                tempSurplusCardList.addAll(surplusCardList);
                                tempSurplusCardList.addAll(headTypeCard.getCardList());
                            }else{
                                selectHeadCardList.addAll(headTypeCard.getCardList());
                                selectMediumCardList.addAll(mediumTypeCard.getCardList());
                                selectTailCardList.addAll(tailTypeCard.getCardList());
                                tempSurplusCardList.addAll(surplusCardList);
                            }


                            //如果中墩和尾墩牌型一样，优先补添尾牌
                            if (mediumTypeCard.getCardType() == tailTypeCard.getCardType()) {
                                completeCard(selectTailCardList, tempSurplusCardList, false,m > 0);
                            }

                            //如果首墩和中墩牌型一样，优先补全中墩
                            if (headTypeCard.getCardType() == mediumTypeCard.getCardType()) {
                                completeCard(selectMediumCardList, tempSurplusCardList, false,m > 0);
                            }

//                            completeCard(selectMediumCardList, tempSurplusCardList, false, m > 0);
//                            completeCard(selectTailCardList, tempSurplusCardList, false, m > 0);
                            completeCard(selectHeadCardList, tempSurplusCardList, true, m > 0);
                            completeCard(selectMediumCardList, tempSurplusCardList, false,m > 0);
                            completeCard(selectTailCardList, tempSurplusCardList, false,m > 0);

                            PokerUtil.sort(selectHeadCardList);
                            if (PokerUtil.getThirteenNormalType(selectHeadCardList) != headTypeCard.getCardType()) {
                                continue;
                            }

                            PokerUtil.sort(selectMediumCardList);
                            if (PokerUtil.getThirteenNormalType(selectMediumCardList) != mediumTypeCard.getCardType()) {
                                continue;
                            }

                            PokerUtil.sort(selectTailCardList);
                            if (PokerUtil.getThirteenNormalType(selectTailCardList) != tailTypeCard.getCardType()) {
                                continue;
                            }

                            completeCardOk = true;
                            break;
                        }
                    }else{
                        selectHeadCardList.addAll(headTypeCard.getCardList());
                        selectMediumCardList.addAll(mediumTypeCard.getCardList());
                        selectTailCardList.addAll(tailTypeCard.getCardList());
                    }

                    if (!completeCardOk) {
                        continue;
                    }

                    if (0 > PokerUtil.compareNormalCardType(selectTailCardList, tailTypeCard.getCardType(), selectMediumCardList, mediumTypeCard.getCardType(), true)) {
                        break;
                    }

                    if (mediumTypeCard.getCardType() == headTypeCard.getCardType()) {
                        List<Byte> tempCard = new ArrayList<>(5);
                        tempCard.add((byte) 52);
                        tempCard.add((byte) 53);
                        tempCard.add(selectHeadCardList.get(0));
                        tempCard.add(selectHeadCardList.get(1));
                        tempCard.add(selectHeadCardList.get(2));
                        if (0 > PokerUtil.compareNormalCardType(selectMediumCardList, mediumTypeCard.getCardType(), tempCard, headTypeCard.getCardType(), true)) {
                            break;
                        }
                    }else{
                        if (mediumTypeCard.getCardType().getValue() < headTypeCard.getCardType().getValue()){
                            break;
                        }
                    }

                    setThirteenPlayerCardMode(player,selectHeadCardList,headTypeCard.getCardType(),selectMediumCardList,mediumTypeCard.getCardType(),selectTailCardList,tailTypeCard.getCardType());

                    if (headTypeCard.getCardType() == EPokerCardType.ZA_PAI){
                        break;
                    }
                }
            }
        }
    }

    /**
     * 取两个数组的差集
     * @param listOne
     * @param listTwo
     * @return
     */
    private static List<Byte> getSurplusList(List<Byte> listOne,List<Byte> listTwo){
        List<Byte> surplusList = new ArrayList<>();
        surplusList.addAll(listOne);
        PokerUtil.removeSubList(surplusList, listTwo);
        return surplusList;
    }
    /**
     * 补全牌
     * @param cardList
     * @param rawCardList
     * @param isHeadCard
     * @param isRandom
     */
    private static void completeCard(List<Byte> cardList,List<Byte> rawCardList,boolean isHeadCard,boolean isRandom){
        int needCompleteCnt = isHeadCard ? HEAD_CARD_COUNT - cardList.size() : MEDIUM_TAIL_CARD_COUNT - cardList.size();
        if (needCompleteCnt <= 0){
            return;
        }
        for (int m = 0; m < needCompleteCnt; m++){
            int index = isRandom ? RandomUtil.random(0,rawCardList.size()-1): rawCardList.size() - 1;
            if (index >= rawCardList.size() && index < 0){
                break;
            }
            cardList.add(rawCardList.remove(index));
        }
    }

    /**
     * 设置thirteenPlayer推荐牌型信息
     * @param player
     * @param headCard
     * @param headType
     * @param mediumCard
     * @param mediumType
     * @param tailCard
     * @param tailType
     */
    private void setThirteenPlayerCardMode(ThirteenPlayer player,List<Byte>headCard,EPokerCardType headType,List<Byte> mediumCard,EPokerCardType mediumType,List<Byte> tailCard,EPokerCardType tailType){
        // 除去重复部分
        List<CardModel> cardModelList = player.getCardModelList();
        for (CardModel var : cardModelList) {
            List<TypeCard> typeCardList = var.getTypeCardList();
            List<Byte> head = typeCardList.get(2).getCardList();
            List<Byte> medium = typeCardList.get(1).getCardList();
            List<Byte> tail = typeCardList.get(0).getCardList();
            if (tailCard.equals(tail) && mediumCard.equals(medium) && headCard.equals(head)) {
                return;
            }
        }

        List<TypeCard> typeCardList = new ArrayList();

        TypeCard tail = new TypeCard();
        tail.setCardList(tailCard);
        tail.setCardType(tailType);
        typeCardList.add(tail);

        TypeCard medium = new TypeCard();
        medium.setCardList(mediumCard);
        medium.setCardType(mediumType);
        typeCardList.add(medium);

        TypeCard head = new TypeCard();
        head.setCardList(headCard);
        head.setCardType(headType);
        typeCardList.add(head);

        CardModel cardModel = new CardModel();
        cardModel.setTypeCardList(typeCardList);
        player.addCardModelList(cardModel);

        if (1 == player.getCardModelList().size()) {
            // 头道
            player.addHeadCard(headCard);
            player.setHeadType(headType);
            // 中道
            player.addMediumCard(mediumCard);
            player.setMediumType(mediumType);

            // 尾道
            player.addTailCard(tailCard);
            player.setTailType(tailType);
        }

        Logs.ROOM.debug("%s tailCard = %s  tailType = %s mediumCard = %s mediumType = %s headCard = %s headType = %s", this,tailCard, tailType, mediumCard, mediumType, headCard, headType);
    }

    /**
     * 找出怪物牌型
     * @param player
     * @param tempCard
     */
    private void checkMonsterType(IPokerPlayer player, List<Byte> tempCard) {
        EPokerCardType type = EPokerCardType.NONE;
        int play = this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        List<Byte> cards = new ArrayList<>();
        cards.addAll(tempCard);
        PokerUtil.sort(cards);

        do {
            if (0 != (play & EThirteenPlayRule.ZHI_ZUN_QING_LONG.getValue())) {
                if (PokerUtil.isThirteenZhiZunQingLong(cards)) {
                    type = EPokerCardType.ZHI_ZUN_QING_LONG;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.BA_XIAN_GUO_HAI.getValue())) {
                if (PokerUtil.isThirteenBaXianGuoHai(cards)) {
                    type = EPokerCardType.BA_XIAN_GUO_HAI;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.QI_XING_LIAN_ZHU.getValue())) {
                if (PokerUtil.isThirteenQiXingLianZhu(cards)) {
                    type = EPokerCardType.QI_XING_LIAN_ZHU;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.SHI_SAN_SHUI.getValue())) {
                if (PokerUtil.isThirteenShiSanShui(cards)) {
                    type = EPokerCardType.SHI_SAN_SHUI;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.SHI_ER_HUANG_ZU.getValue())) {
                if (PokerUtil.isThirteenShiErHuangZu(cards)) {
                    type = EPokerCardType.SHI_ER_HUANG_ZU;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.SAN_TONG_HUA_SHUN.getValue())) {
                if (PokerUtil.isThirteenSanTongHuaShun(cards)) {
                    type = EPokerCardType.SAN_TONG_HUA_SHUN;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.SAN_TAO_ZHA_DAN.getValue())) {
                if (PokerUtil.isThirteenSanTaoZhaDan(cards)) {
                    type = EPokerCardType.SAN_TAO_ZHA_DAN;
                    break;
                }
            }
            // if (0 != (play & EThirteenPlayRule.COU_YI_SE.getValue())) {
            // cardValue = PokerUtil.isCouYiSe(cards);
            // if (-1 != cardValue) {
            // type = EPokerCardType.COU_YI_SE;
            // break;
            // }
            // }
            if (0 != (play & EThirteenPlayRule.QUAN_DA.getValue())) {
                if (PokerUtil.isThirteenQuanDa(cards)) {
                    type = EPokerCardType.QUAN_DA;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.QUAN_XIAO.getValue())) {
                if (PokerUtil.isThirteenQuanXiao(cards)) {
                    type = EPokerCardType.QUAN_XIAO;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.QUAN_HONG.getValue())) {
                if (PokerUtil.isThirteenQuanHong(cards)) {
                    type = EPokerCardType.QUAN_HONG;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.QUAN_HEI.getValue())) {
                if (PokerUtil.isThirteenQuanHei(cards)) {
                    type = EPokerCardType.QUAN_HEI;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.ZHONG_YUAN_YI_DIAN_HONG.getValue())) {
                if (PokerUtil.isThirteenZhongYuanYiDianHong(cards)) {
                    type = EPokerCardType.ZHONG_YUAN_YI_DIAN_HONG;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.ZHONG_YUAN_YI_DIAN_HEI.getValue())) {
                if (PokerUtil.isThirteenZhongYuanYiDianHei(cards)) {
                    type = EPokerCardType.ZHONG_YUAN_YI_DIAN_HEI;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.SI_TAO_SAN_TIAO.getValue())) {
                if (PokerUtil.isThirteenSiTaoSanTiao(cards)) {
                    type = EPokerCardType.SI_TAO_SAN_TIAO;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.WU_DUI_SAN_TIAO.getValue())) {
                if (PokerUtil.isThirteenWuDuiSanTiao(cards)) {
                    type = EPokerCardType.WU_DUI_SAN_TIAO;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.LIU_DUI_BAN.getValue())) {
                if (PokerUtil.isThirteenLiuDuiBan(cards)) {
                    type = EPokerCardType.LIU_DUI_BAN;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.SAN_TONG_HUA.getValue())) {
                if (PokerUtil.isThirteenSanTongHua(cards)) {
                    type = EPokerCardType.SAN_TONG_HUA;
                    break;
                }
            }
            if (0 != (play & EThirteenPlayRule.SAN_SHUN_ZI.getValue())) {
                if (PokerUtil.isThirteenSanShunZi(cards)) {
                    type = EPokerCardType.SAN_SHUN_ZI;
                    break;
                }
            }
        } while (false);
        Logs.ROOM.debug("%s playerUid:%d 怪物牌型 = %s ", this, player.getUid(), type.getValue());
        ((ThirteenPlayer) player).setMonsterType(type);
        this.monsterRecord.put(player.getUid(), type);
    }

    /**
     * 普通房间得分数据计算
     */
	private void setNormalResult() {
		// 普通房间 怪物牌型分数
		for (Map.Entry<Long, EPokerCardType> entry : monsterRecord.entrySet()) {
			ThirteenPlayer rankPlayer = (ThirteenPlayer) this.getRoomPlayer(entry.getKey());
			for (int j = 0; j < this.playerNum; ++j) {
				int index = (this.bankerIndex + j) % this.playerNum;
				ThirteenPlayer player = (ThirteenPlayer) this.allPlayer[index];
				if (null == player || player.isGuest() || player.getUid() == rankPlayer.getUid()) {
					continue;
				}

				if (rankPlayer.getMonsterType().getValue() > player.getMonsterType().getValue()) {
                    int score = this.getMonsterScore(rankPlayer.getMonsterType());
					player.addScore(Score.POKER_THIRTEEN_MONSTER_SCORE, -score, false);
					rankPlayer.addScore(Score.POKER_THIRTEEN_MONSTER_SCORE, score, false);
				}
			}
		}

		// 普通房间 普通牌型分数
		for (int i = 0; i < this.playerNum; ++i) {
			int index = i % this.playerNum;
			ThirteenPlayer player = (ThirteenPlayer) this.allPlayer[index];
			if (null == player || player.isGuest() || EPokerCardType.NONE != player.getMonsterType()) {
				continue;
			}

			for (int j = index + 1; j < this.playerNum; ++j) {
				ThirteenPlayer otherPlayer = (ThirteenPlayer) this.allPlayer[j];
				if (null == otherPlayer || otherPlayer.isGuest() || EPokerCardType.NONE != otherPlayer.getMonsterType()) {
					continue;
				}
				int winCnt = 0;
				int loseCnt = 0;
				int sameCnt = 0;
				int headScore = 0;
				int mediumScore = 0;
				int tailScore = 0;
                int headCardScore = 0;
                int mediumCardScore = 0;
                int tailCardScore = 0;
				// 头道
				int isWin = PokerUtil.compareNormalCardType(player.getHeadCard(), player.getHeadType(), otherPlayer.getHeadCard(),
						otherPlayer.getHeadType(), this.useColor);
				if (0 == isWin) {
					sameCnt++;
				} else {
					if (isWin > 0) {
						headScore = this.getHeadScore(player.getHeadType());
                        headCardScore=this.getScore(this.getHeadMul(player.getHeadType())-1);
						player.addScore(Score.POKER_THIRTEEN_HEAD_SCORE, headScore, false);
						otherPlayer.addScore(Score.POKER_THIRTEEN_HEAD_SCORE, -headScore, false);
                        player.addScore(Score.POKER_THIRTEEN_HEAD_CARD_SCORE, headCardScore, false);
                        otherPlayer.addScore(Score.POKER_THIRTEEN_HEAD_CARD_SCORE, -headCardScore, false);
                        player.addScore(Score.SCORE, headScore, false);
                        otherPlayer.addScore(Score.SCORE, -headScore, false);
						winCnt++;
					} else {
						headScore = this.getHeadScore(otherPlayer.getHeadType());
                        headCardScore=this.getScore(this.getHeadMul(otherPlayer.getHeadType())-1);
                        player.addScore(Score.POKER_THIRTEEN_HEAD_SCORE, -headScore, false);
						otherPlayer.addScore(Score.POKER_THIRTEEN_HEAD_SCORE, headScore, false);
                        player.addScore(Score.POKER_THIRTEEN_HEAD_CARD_SCORE, -headCardScore, false);
                        otherPlayer.addScore(Score.POKER_THIRTEEN_HEAD_CARD_SCORE, headCardScore, false);
                        player.addScore(Score.SCORE, -headScore, false);
                        otherPlayer.addScore(Score.SCORE, headScore, false);
						loseCnt++;
					}
				}
				// 中道
				isWin = PokerUtil.compareNormalCardType(player.getMediumCard(), player.getMediumType(), otherPlayer.getMediumCard(),
						otherPlayer.getMediumType(), this.useColor);
				if (0 == isWin) {
					sameCnt++;
				} else {
					if (isWin > 0) {
						mediumScore = this.getMediumScore(player.getMediumType());
                        mediumCardScore=this.getScore(this.getMediumMul(player.getMediumType())-1);
						player.addScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, mediumScore, false);
						otherPlayer.addScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, -mediumScore, false);
                        player.addScore(Score.POKER_THIRTEEN_MEDIUM_CARD_SCORE, mediumCardScore, false);
                        otherPlayer.addScore(Score.POKER_THIRTEEN_MEDIUM_CARD_SCORE, -mediumCardScore, false);
                        player.addScore(Score.SCORE, mediumScore, false);
                        otherPlayer.addScore(Score.SCORE, -mediumScore, false);
						winCnt++;
					} else {
						mediumScore = this.getMediumScore(otherPlayer.getMediumType());
                        mediumCardScore=this.getScore(this.getMediumMul(otherPlayer.getMediumType())-1);
                        player.addScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, -mediumScore, false);
						otherPlayer.addScore(Score.POKER_THIRTEEN_MEDIUM_SCORE, mediumScore, false);
                        player.addScore(Score.POKER_THIRTEEN_MEDIUM_CARD_SCORE, -mediumCardScore, false);
                        otherPlayer.addScore(Score.POKER_THIRTEEN_MEDIUM_CARD_SCORE, mediumCardScore, false);
                        player.addScore(Score.SCORE, -mediumScore, false);
                        otherPlayer.addScore(Score.SCORE, mediumScore, false);
						loseCnt++;
					}
				}
				// 尾道
				isWin = PokerUtil.compareNormalCardType(player.getTailCard(), player.getTailType(), otherPlayer.getTailCard(),
						otherPlayer.getTailType(), this.useColor);
				if (0 == isWin) {
					sameCnt++;
				} else {
					if (isWin > 0) {
						tailScore = this.getTailScore(player.getTailType());
                        tailCardScore=this.getScore(this.getTailMul(player.getTailType())-1);
						player.addScore(Score.POKER_THIRTEEN_TAIL_SCORE, tailScore, false);
						otherPlayer.addScore(Score.POKER_THIRTEEN_TAIL_SCORE, -tailScore, false);
                        player.addScore(Score.POKER_THIRTEEN_TAIL_CARD_SCORE, tailCardScore, false);
                        otherPlayer.addScore(Score.POKER_THIRTEEN_TAIL_CARD_SCORE, -tailCardScore, false);
                        player.addScore(Score.SCORE, tailScore, false);
                        otherPlayer.addScore(Score.SCORE, -tailScore, false);
						winCnt++;
					} else {
						tailScore = this.getTailScore(otherPlayer.getTailType());
                        tailCardScore=this.getScore(this.getTailMul(otherPlayer.getTailType())-1);
                        player.addScore(Score.POKER_THIRTEEN_TAIL_SCORE, -tailScore, false);
						otherPlayer.addScore(Score.POKER_THIRTEEN_TAIL_SCORE, tailScore, false);
                        player.addScore(Score.POKER_THIRTEEN_TAIL_CARD_SCORE, -tailCardScore, false);
                        otherPlayer.addScore(Score.POKER_THIRTEEN_TAIL_CARD_SCORE, tailCardScore, false);
                        player.addScore(Score.SCORE, -tailScore, false);
                        otherPlayer.addScore(Score.SCORE, tailScore, false);
						loseCnt++;
					}
				}

				// 普通打枪水数 = 6水替换大于2水的道的水 ,强袭不打枪
				if (3 == winCnt) {
					// 3道都比该玩家大则视为打枪
					this.setShootScore(player, otherPlayer, headScore, mediumScore, tailScore);
				}

				if (3 == loseCnt) {
					// lose = 3, 打枪分数处理
					this.setShootScore(otherPlayer, player, headScore, mediumScore, tailScore);
				}

				if (false == this.strikeNoShoot) {
					// 强袭打枪判断:1胜2和或2胜1和
					if ((1 == winCnt && 2 == sameCnt) || (2 == winCnt && 1 == sameCnt)) {
						this.setShootScore(player, otherPlayer, headScore, mediumScore, tailScore);
					}

					// 强袭打枪判断:1负2和或2负1和
					if ((1 == loseCnt && 2 == sameCnt) || (2 == loseCnt && 1 == sameCnt)) {
						this.setShootScore(otherPlayer, player, headScore, mediumScore, tailScore);
					}
				}

				Logs.ROOM.debug("%s player = %s otherPlayer = %s winCnt = %s sameCnt = %s loseCnt = %s strikeNoShoot = %b", this,
						player.getUid(), otherPlayer.getUid(), winCnt, sameCnt, loseCnt, this.strikeNoShoot);
			}
		}
	}

    /**
     * 计算通杀分数
     * @param otherPlayer
     */
	private void setPassKillScore(ThirteenPlayer otherPlayer) {
		if (1 == this.shootType || 2 == this.shootType) {
            ThirteenPlayer player = (ThirteenPlayer) this.getRoomPlayer(this.passKillPlayerUid);
            Map<Long,Integer> map=player.getShootPlayerList();
            if(map.containsKey(otherPlayer.getUid())){
                int value=map.get(otherPlayer.getUid());
//                otherPlayer.addScore(Score.POKER_THIRTEEN_SHOOT_SCORE, value, false);
                otherPlayer.addScore(Score.SCORE, value, false);

            }

		} else if (3 == this.shootType) {
			ThirteenPlayer player = (ThirteenPlayer) this.getRoomPlayer(this.passKillPlayerUid);
			int score = 7;
//			player.addScore(Score.POKER_THIRTEEN_SHOOT_SCORE, this.getScore(score), false);
//			otherPlayer.addScore(Score.POKER_THIRTEEN_SHOOT_SCORE, this.getScore(score) * -1, false);
            player.addScore(Score.SCORE, this.getScore(score), false);
            otherPlayer.addScore(Score.SCORE, this.getScore(score) * -1, false);

        }
	}

	private void setShootScore(ThirteenPlayer player, ThirteenPlayer otherPlayer, int headScore, int mediumScore, int tailScore) {
        // 2人不打枪
        if (false == this.twoNoShoot) {
            int total = headScore + mediumScore + tailScore;
            int addShootScore = 0;
            if (1 == this.shootType) {
                addShootScore = total + this.getScore(1) * 3;
            } else if (2 == this.shootType) {
                addShootScore = total * 2;
            } else if (3 == this.shootType) {
                int head = this.getHeadMul(player.getHeadType()) > 1 ? this.getHeadScore(player.getHeadType()) : 0;
                int medium = this.getMediumMul(player.getMediumType()) > 1 ? this.getMediumScore(player.getMediumType()) : 0;
                int tail = this.getTailMul(player.getTailType()) > 1 ? this.getTailScore(player.getTailType()) : 0;
                int score=head+medium+tail;
                addShootScore=score+this.getScore(this.shootValue);
                player.addScore(Score.SCORE, - total+this.getScore(this.shootValue) +score, false);
                otherPlayer.addScore(Score.SCORE, total-this.getScore(this.shootValue) -score, false);
                player.addScore(Score.POKER_THIRTEEN_SHOOT_SCORE, addShootScore-total, false);
                otherPlayer.addScore(Score.POKER_THIRTEEN_SHOOT_SCORE, addShootScore*-1+total , false);
            }

            // 添加打枪列表
            player.setShootPlayerList(otherPlayer.getUid(), addShootScore * -1);
            if (this.onPlayerNum > 3 && player.getShootPlayerList().size() == this.onPlayerNum - 1) {
                // 通杀判断
                this.passKillPlayerUid = player.getUid();
            }
            if (this.shootType != 3) {
                player.addScore(Score.SCORE, addShootScore - total, false);
                otherPlayer.addScore(Score.SCORE, addShootScore * -1 + total, false);
                player.addScore(Score.POKER_THIRTEEN_SHOOT_SCORE, addShootScore - total, false);
                otherPlayer.addScore(Score.POKER_THIRTEEN_SHOOT_SCORE, addShootScore * -1 + total, false);
            }
        }
    }


    /**
     * 头部普通牌型对应分值
     * @param type
     * @return
     */
	private int getHeadScore(EPokerCardType type) {
		int mul = this.getHeadMul(type);
		return this.getScore(mul);
	}

    /**
     * 头部普通牌型对应水数
     * @param type
     * @return
     */
	private int getHeadMul(EPokerCardType type) {
		int mul = 1;
		switch (type) {
			case ZA_PAI :
				mul = 1;
				break;
			case DUI_ZI :
				mul = 1;
				break;
			case SAN_TIAO :
				mul = 3;
				break;
		}
		return mul;
	}

    /**
     * 中部普通牌型对应分值
     * @param type
     * @return
     */
	private int getMediumScore(EPokerCardType type) {
		int mul = this.getMediumMul(type);
		return this.getScore(mul);
	}

    /**
     * 中部普通牌型对应水数
     * @param type
     * @return
     */
	private int getMediumMul(EPokerCardType type) {
		int mul = 1;

		switch (type) {
			case ZA_PAI :
				mul = 1;
				break;
			case DUI_ZI :
				mul = 1;
				break;
			case ER_DUI :
				mul = 1;
				break;
			case SAN_TIAO :
				mul = 1;
				break;
			case SHUN_ZI :
				mul = 1;
				break;
			case TONG_HUA :
				mul = 1;
				break;
			case HU_LU :
				mul = 2;
				break;
			case SI_TIAO :
				mul = 8;
				break;
			case TONG_HUA_SHUN :
				mul = 10;
				break;
			case WU_TIAO :
				mul = 20;
				break;
		}
		return mul;
	}

    /**
     * 尾部普通牌型对应分值
     * @param type
     * @return
     */
	private int getTailScore(EPokerCardType type) {
		int mul = this.getTailMul(type);
		return this.getScore(mul);
	}

    /**
     * 尾部普通牌型对应的水数
     * @param type
     * @return
     */
	private int getTailMul(EPokerCardType type) {
		int mul = 1;

		switch (type) {
			case ZA_PAI :
				mul = 1;
				break;
			case DUI_ZI :
				mul = 1;
				break;
			case ER_DUI :
				mul = 1;
				break;
			case SAN_TIAO :
				mul = 1;
				break;
			case SHUN_ZI :
				mul = 1;
				break;
			case TONG_HUA :
				mul = 1;
				break;
			case HU_LU :
				mul = 1;
				break;
			case SI_TIAO :
				mul = 4;
				break;
			case TONG_HUA_SHUN :
				mul = 5;
				break;
			case WU_TIAO :
				mul = 10;
				break;
		}
		return mul;
	}

    /**
     * 获取怪物牌对应分值
     * @param type
     * @return
     */
	private int getMonsterScore(EPokerCardType type) {
		return this.getScore(type.getMul());
	}

    private ErrorCode checkTake0(IAction action, IPokerPlayer player, List<Byte> cards, int monsterType, int _headType, int _mediumType,
                                 int _tailType) {
        // 怪物牌型
        if (0 != monsterType) {
            // this.checkMonsterType(player, cards);
            this.monsterRecord.put(player.getUid(), ((ThirteenPlayer) player).getMonsterType());
            player.maxScore(Score.ACC_POKER_MAX_CARD_TYPE, ((ThirteenPlayer) player).getMonsterType().getValue(), true);

            // type
            // check and set Head card type
            ((ThirteenPlayer) player).setHeadType(EPokerCardType.NONE);
            // check and set Medium card type
            ((ThirteenPlayer) player).setMediumType(EPokerCardType.NONE);
            // check and set tail card type
            ((ThirteenPlayer) player).setTailType(EPokerCardType.NONE);
        } else {
            // 普通牌型
            List<Byte> headCard = new ArrayList<>(3);
            List<Byte> mediumCard = new ArrayList<>(5);
            List<Byte> tailCard = new ArrayList<>(5);

            headCard = cards.subList(0, 3);
            mediumCard = cards.subList(3, 8);
            tailCard = cards.subList(8, cards.size());

            // check and set Head card type
            EPokerCardType headType = PokerUtil.getThirteenNormalType(headCard);
            EPokerCardType mediumType = PokerUtil.getThirteenNormalType(mediumCard);
            EPokerCardType tailType = PokerUtil.getThirteenNormalType(tailCard);

            // 检查倒水
            if (headType.getValue() > mediumType.getValue() || mediumType.getValue() > tailType.getValue()) {
                Logs.ROOM.debug("OBJ = %s player = %s 倒水1 cards = %s ", this, player.getUid(), cards);
                return ErrorCode.ROOM_POKER_THIRTEEN_POUR_WATER;
            }
            if (headType.getValue() == mediumType.getValue()) {
                List<Byte> tempCard = new ArrayList<>(5);
                tempCard.add((byte) 52);
                tempCard.add((byte) 53);
                tempCard.add(headCard.get(0));
                tempCard.add(headCard.get(1));
                tempCard.add(headCard.get(2));
                if (PokerUtil.compareNormalCardType(mediumCard, mediumType, tempCard, headType, this.useColor) < 0) {
                    Logs.ROOM.debug("OBJ = %s player = %s 倒水2 cards = %s ", this, player.getUid(), cards);
                    return ErrorCode.ROOM_POKER_THIRTEEN_POUR_WATER;
                }
            }
            if (tailType.getValue() == mediumType.getValue()) {
                if (PokerUtil.compareNormalCardType(tailCard, tailType, mediumCard, mediumType, this.useColor) < 0) {
                    Logs.ROOM.debug("OBJ = %s player = %s 倒水3 cards = %s ", this, player.getUid(), cards);
                    return ErrorCode.ROOM_POKER_THIRTEEN_POUR_WATER;
                }
            }

            // type
            ((ThirteenPlayer) player).setMonsterType(EPokerCardType.NONE);
            // check and set Head card type
            ((ThirteenPlayer) player).setHeadType(headType);
            // check and set Medium card type
            ((ThirteenPlayer) player).setMediumType(mediumType);
            // check and set tail card type
            ((ThirteenPlayer) player).setTailType(tailType);
            player.maxScore(Score.ACC_POKER_MAX_CARD_TYPE, tailType.getValue(), true);

            // card
            ((ThirteenPlayer) player).getHeadCard().clear();
            ((ThirteenPlayer) player).getMediumCard().clear();
            ((ThirteenPlayer) player).getTailCard().clear();

            ((ThirteenPlayer) player).addHeadCard(headCard);
            // check and set Medium card type
            ((ThirteenPlayer) player).addMediumCard(mediumCard);
            // check and set tail card type
            ((ThirteenPlayer) player).addTailCard(tailCard);

            // monster
            this.monsterRecord.put(player.getUid(), EPokerCardType.NONE);

            Logs.ROOM.debug("%s playerUid = %s 检查玩家牌型 Head: card = %s Type= %s, Medium: card = %s Type = %s Tail: card = %s Type = %s",
                    this, player.getUid(), ((ThirteenPlayer) player).getHeadCard(), ((ThirteenPlayer) player).getHeadType(),
                    ((ThirteenPlayer) player).getMediumCard(), ((ThirteenPlayer) player).getMediumType(),
                    ((ThirteenPlayer) player).getTailCard(), ((ThirteenPlayer) player).getTailType());
        }

        // TODO 通知已经出牌
        PCLIPokerNtfThirteenTake info = new PCLIPokerNtfThirteenTake();
        info.PlayerUid = player.getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_THIRTEEN_TAKE, info);
        Logs.ROOM.debug("OBJ = %s player = %s 玩家排出的牌型 cards = %s ", this, player.getUid(), cards);

        this.allTakeCard.add(player.getUid());

        Logs.ROOM.debug("%s %s 通知已经出牌 allTakeCard size = %s, player num = %s playerCnt = %s onPlayerNum = %s", this, player.getUid(),
                this.allTakeCard.size(), this.playerNum, this.playerCnt.get(), this.onPlayerNum);

        if (this.allTakeCard.size() == this.onPlayerNum) {
            // IAction action = this.action.peek();
            if (action instanceof ThirteenTakeAction) {
                ((ThirteenTakeAction) action).finish();
                this.tick();
            }
        }
        return ErrorCode.OK;
    }
}
