package com.xiuxiu.app.server.room.normal.poker.fgf;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByFGF;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMyHandCardInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfShowOffInfoByFgf;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.ProbConfig;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.ShowOffAction;
import com.xiuxiu.app.server.room.normal.poker.IDiscard;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.FgfFAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.app.server.room.player.poker.FGFPlayer;
import com.xiuxiu.app.server.room.record.poker.FGFResultRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.RecordPokerPlayerBriefInfo;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@GameInfo(gameType = GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER)
public class FGFRoom extends PokerRoom implements IFGFRoom, IDiscard {
    protected static ArrayList<Integer> TWO_THREE_FIVE = new ArrayList<>();
    protected static ArrayList<Integer> SINGLE = new ArrayList<>();
    protected static ArrayList<Integer> PAIR = new ArrayList<>();
    protected static ArrayList<Integer> LINE = new ArrayList<>();
    protected static ArrayList<Integer> SAME = new ArrayList<>();
    protected static ArrayList<Integer> SAMELINE = new ArrayList<>();
    protected static ArrayList<Integer> THREE = new ArrayList<>();

    static {
        String[] _235 = FileUtil.readFileString("cnf/fgf/fgf_235.txt").replaceAll("\\\\\"", "\\\"").split("\n");
        String[] single = FileUtil.readFileString("cnf/fgf/fgf_single.txt").replaceAll("\\\\\"", "\\\"").split("\n");
        String[] pair = FileUtil.readFileString("cnf/fgf/fgf_pair.txt").replaceAll("\\\\\"", "\\\"").split("\n");
        String[] line = FileUtil.readFileString("cnf/fgf/fgf_line.txt").replaceAll("\\\\\"", "\\\"").split("\n");
        String[] same = FileUtil.readFileString("cnf/fgf/fgf_same.txt").replaceAll("\\\\\"", "\\\"").split("\n");
        String[] sameLine = FileUtil.readFileString("cnf/fgf/fgf_same_line.txt").replaceAll("\\\\\"", "\\\"").split("\n");
        String[] three = FileUtil.readFileString("cnf/fgf/fgf_three.txt").replaceAll("\\\\\"", "\\\"").split("\n");

        for (int i = 0; i < _235.length; ++i) {
            String[] temp = single[i].replace("\n", "").replace("\r","").split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]);
            int c = Integer.parseInt(temp[2]);
            TWO_THREE_FIVE.add(c | b << 8 | a << 16);
        }

        for (int i = 0; i < single.length; ++i) {
            String[] temp = single[i].replace("\n", "").replace("\r","").split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]);
            int c = Integer.parseInt(temp[2]);
            SINGLE.add(c | b << 8 | a << 16);
        }

        for (int i = 0; i < pair.length; ++i) {
            String[] temp = pair[i].replace("\n", "").replace("\r","").split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]);
            int c = Integer.parseInt(temp[2]);
            PAIR.add(c | b << 8 | a << 16);
        }

        for (int i = 0; i < line.length; ++i) {
            String[] temp = line[i].replace("\n", "").replace("\r","").split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]);
            int c = Integer.parseInt(temp[2]);
            LINE.add(c | b << 8 | a << 16);
        }

        for (int i = 0; i < same.length; ++i) {
            String[] temp = same[i].replace("\n", "").replace("\r","").split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]);
            int c = Integer.parseInt(temp[2]);
            SAME.add(c | b << 8 | a << 16);
        }

        for (int i = 0; i < sameLine.length; ++i) {
            String[] temp = sameLine[i].replace("\n", "").replace("\r","").split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]);
            int c = Integer.parseInt(temp[2]);
            SAMELINE.add(c | b << 8 | a << 16);
        }

        for (int i = 0; i < three.length; ++i) {
            String[] temp = three[i].replace("\n", "").replace("\r","").split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]);
            int c = Integer.parseInt(temp[2]);
            THREE.add(c | b << 8 | a << 16);
        }
    }

    protected int maxLoop = 0;                  // 总轮数 轮数上限
//  protected int mustLoop = 0;                 // 必跟轮数

    protected int bankerType = 0;               //0 赢家庄 1 轮流庄
    //protected int timeDiscard = -1;           //超时弃牌
    protected int fgfThreeAward = 0;            //豹子奖励 0 无 10，20
    protected int compareRule = 0;              //比牌规则 0 -比大小， 1-比花色，2-全比；
    protected int selectNote = 0;               //下注选择 0,1；
    protected int compareLoop = 0;              //比牌轮数；
    protected int stuffyLoop = 0;               //闷牌轮数 0（不限制） 其他次数就按1,2,3来定义

    //玩法
    protected boolean sing235Play = false;      // 高牌235吃豹子
    protected boolean outBalance = false;       // 解散局算分；
    protected boolean compareDouble = false;    // 比牌双倍；
    protected boolean comparenIvisible = false; // 未比牌不可见
    protected boolean colluder = false;         // 防勾手　
    protected boolean fillUp = false;           // 压满　
    protected boolean shuffle = false;          // 搓牌
    protected boolean firstFollowMaxReb = false;// 首轮跟最大注

    protected int curLoop = 0;                  // 当前轮
    protected int curOpIndex = -1;              // 当前操作索引
    protected int curNote = 0;                  // 当前下注
    protected boolean finish = true;            // 是否完成
    protected int overCnt = 0;                  // 已经结束人数
    protected int curLoopOpCnt = 0;             // 当前轮操作的人数
    protected int nextLoopNeedOpCnt = 0;        // 下一轮操作的人数
    protected int curLoopNeedOpCnt = 0;         // 当前轮需要操作的人数
    protected int limit = 0;                    // 单注上限
    protected int antes = 1;                    // 单注底注
    protected int rebetMul = 1;                 // 下注倍数
    protected int multiple = 2;                 // 倍数
    protected boolean isFillUp = false;         // 是否压满
    protected int fillUpCnt = 0;                // 压满人数

    protected List<Long> threePlayerList = new ArrayList<>();//豹子玩家列表

    protected int[] useCard = new int[52];

    protected FgfFAction fgfFAction;

    public FGFRoom(RoomInfo info) {
        super(info, ERoomType.NORMAL);
    }

    public FGFRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.playerNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_NUM, 8);
        this.playerMinNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_MIN_NUM, 2);
        this.allPlayer = new IRoomPlayer[this.playerNum];
        this.maxLoop = this.info.getRule().getOrDefault(RoomRule.RR_FGF_LOOP_LIMIT, 5);
//      this.mustLoop = this.info.getRule().getOrDefault(RoomRule.RR_FGF_MUST_LOOP, 0);

        this.bankerType = this.info.getRule().getOrDefault(RoomRule.RR_FGF_BANKER_TYPE, 0);
        this.timeout  = this.info.getRule().getOrDefault(RoomRule.RR_FGF_TIME_OUT, -1);
        if (-1 != this.timeout){
            this.timeout = this.timeout * 1000;
        } else if (0 == this.timeout) {
            this.timeout = -1;
        }
        this.fgfThreeAward = this.info.getRule().getOrDefault(RoomRule.RR_FGF_THREE_AWARD, 0);
        this.compareRule = this.info.getRule().getOrDefault(RoomRule.RR_FGF_COMPARE_RULE, 0);
        this.selectNote = this.info.getRule().getOrDefault(RoomRule.RR_FGF_SELECT_NOTE, 0);
        this.stuffyLoop = this.info.getRule().getOrDefault(RoomRule.RR_FGF_STUFFY_LOOP, 0);
        this.compareLoop = this.info.getRule().getOrDefault(RoomRule.RR_FGF_COMPARE_LOOP, 0);
        this.rebetMul = this.info.getRule().getOrDefault(RoomRule.RR_FGF_END_POINT_MUL, 10);

        this.sing235Play = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.SINGLE_235.getValue());
        this.outBalance = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.OUT_BALANCE.getValue());
        this.compareDouble = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.BI_PAI_DOUBLE.getValue());
        this.comparenIvisible = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.WEI_BI_PAI_BU_KE_JIAN.getValue());
        this.colluder = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.FANG_GOU_SHOU.getValue());
        this.fillUp = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.YA_MAN.getValue());
        this.shuffle= 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.BU_KE_CUO_PAI.getValue());
        this.firstFollowMaxReb = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.FIRST_FOLLOW_MAX_REB.getValue());

        this.detectionIP = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.RR_DETECTION_IP.getValue());

        if (0 == this.selectNote) {
            this.limit = 5 * this.rebetMul;
            this.antes = 1 * this.rebetMul;
        } else if (1 == this.selectNote) {
            this.limit = 10 * this.rebetMul;
            this.antes = 2 * this.rebetMul;
        } else if (2 == this.selectNote) {
            this.limit = 25 * this.rebetMul;
            this.antes = 5 * this.rebetMul;
        }

        for (int i = 0; i < this.useCard.length; ++i) {
            this.useCard[i] = -1;
        }
        this.autoReady = false;
    }

    public int getCurLoop(){
        return this.curLoop;
    }

    public int getStuffyLoop(){
        return this.stuffyLoop;
    }

     //选择下注
     public void selectRebet(long playerUid, PCIPokerNtfSelectRebetInfo resp)
     {
    	 FGFPlayer roomPlayer = (FGFPlayer) this.getRoomPlayer(playerUid);
    	 resp.type=roomPlayer.getCurCardType().getValue();
    	 List<FGFPlayer> players = new ArrayList<>();
    	  for (int j = 0; j < this.playerNum; ++j) {
    		  IRoomPlayer temp = allPlayer[j];
    		  if (null == temp || temp.isGuest()||temp.getUid()==playerUid) {
                  continue;
              }
    		  FGFPlayer tarPlayer = (FGFPlayer)temp;
    		  if(!compare(roomPlayer, tarPlayer))
    			  players.add(tarPlayer);
    	  }
    	  resp.rank=players.size()+1;
     }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            return;
        }

        for (byte i = 0; i < 52; ++i) {
            this.allCard.add(i);
        }
        ShuffleUtil.shuffle(this.allCard);
    }
    private void setBankerIndex() {
        int count = 0;
        while ((null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest()) && count <= 16) {
            count++;
            this.bankerIndex = (++this.bankerIndex) % this.playerNum;
        }
    }
    @Override
    protected void doDeal() {
        if (-1 == this.bankerIndex) {
            int count = 0;
            do {
                this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            } while ((null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest()) && count <= 16);
        }

        if (0 == this.bankerType) { //赢家当庄
            if (this.winPoker != null) {
                this.bankerIndex = this.winPoker.getIndex();
            }
        } else if( 1 == this.bankerType) {//轮流坐庄
            int count = 0;
            do {
                this.bankerIndex = (this.bankerIndex + 1) % this.playerNum;
            } while ((null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest()) && count <= 16);
        }
        setBankerIndex();
        for (int j = this.bankerIndex, k = 0; k < this.playerNum; ++k) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[(j + k) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addHandCard(this.allCard.removeFirst());
            player.addHandCard(this.allCard.removeFirst());
            player.addHandCard(this.allCard.removeFirst());
        }
//        for (int j = this.bankerIndex, k = 0; k < this.playerNum; ++k) {
//            IPokerPlayer player = (IPokerPlayer) this.allPlayer[(j + k) % this.playerNum];
//            if (null == player || player.isGuest()) {
//                continue;
//            }
//            int rand = RandomUtil.random(0, ProbConfig.FGF_THREE);
//            do {
//                int temp = -1;
//                int index = 0;
//                if (rand < ProbConfig.FGF_235) {
//                    index = RandomUtil.random(0, TWO_THREE_FIVE.size() - 1);
//                    temp = TWO_THREE_FIVE.get(index);
//                } else if (rand < ProbConfig.FGF_NONE) {
//                    index = RandomUtil.random(0, SINGLE.size() - 1);
//                    temp = SINGLE.get(index);
//                } else if (rand < ProbConfig.FGF_DOUBLE) {
//                    index = RandomUtil.random(0, PAIR.size() - 1);
//                    temp = PAIR.get(index);
//                } else if (rand < ProbConfig.FGF_LINE) {
//                    index = RandomUtil.random(0, LINE.size() - 1);
//                    temp = LINE.get(index);
//                } else if (rand < ProbConfig.FGF_SAME_COLOR) {
//                    index = RandomUtil.random(0, SAME.size() - 1);
//                    temp = SAME.get(index);
//                } else if (rand < ProbConfig.FGF_SAME_COLOR_AND_LINE) {
//                    index = RandomUtil.random(0, SAMELINE.size() - 1);
//                    temp = SAMELINE.get(index);
//                } else {
//                    index = RandomUtil.random(0, THREE.size() - 1);
//                    temp = THREE.get(index);
//                }
//                byte a = (byte) (temp & 0xff);
//                byte b = (byte) ((temp >> 8) & 0xff);
//                byte c = (byte) ((temp >> 16) & 0xff);
//                if (-1 == this.useCard[a] && -1 == this.useCard[b] && -1 == this.useCard[c]) {
//                    player.addHandCard(a);
//                    player.addHandCard(b);
//                    player.addHandCard(c);
//                    this.useCard[a] = 1;
//                    this.useCard[b] = 1;
//                    this.useCard[c] = 1;
//                    break;
//                }
//                if (rand < ProbConfig.FGF_235) {
//                    rand = RandomUtil.random(0, ProbConfig.FGF_THREE);
//                } else if (rand < ProbConfig.FGF_SAME_COLOR_AND_LINE) {
//
//                } else {
//                    rand = RandomUtil.random(0, ProbConfig.FGF_THREE);
//                }
//            } while (true);
//        }

        this.curLoopNeedOpCnt = 0;
        for (int j = this.bankerIndex, k = 0; k < this.playerNum; ++k) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[(j + k) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }
            ++this.curLoopNeedOpCnt;
            PokerUtil.sortByFGF(player.getHandCard());
            player.initHandCard();
            ((FGFPlayer) player).setCurCardType(this.getCardType(player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2)));
            ((FGFPlayer) player).setUseColorValue(PokerUtil.getFriedGoldenFlowerValue(
                    player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2),
                    ((FGFPlayer) player).getCurCardType(), true));
            ((FGFPlayer) player).setUnColorValue(PokerUtil.getFriedGoldenFlowerValue(
                    player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2),
                    ((FGFPlayer) player).getCurCardType(), false));

            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getRoomPlayerHelper().getCurBureau()));

            player.addScore(Score.POKER_FGF_NOTE, this.antes, false);
            ((PokerRecord) this.getRecord()).addFollowNoteRecordAction(player.getUid(), this.antes);
        }

        Logs.ROOM.debug("%s %s 庄家 index：", this, this.bankerIndex);
        ((PokerRecord) this.getRecord()).addBankerRecordAction(this.getRoomPlayer(this.bankerIndex % this.playerNum).getUid());
        //((PokerRecord) this.record).addBankerRecordAction(this.allPlayer[this.bankerIndex % this.playerNum].getUid());

        this.curOpIndex = this.bankerIndex;
        this.nextLoopNeedOpCnt = this.curLoopNeedOpCnt;
        this.curLoopOpCnt = 0;
        this.curLoop = 1;
        this.curNote = this.antes;
        this.finish = false;
    }

    @Override
    public void replaceHandCard(IRoomPlayer player, int card) {
        if (!Config.checkWhiteHas(player.getUid(), 3)) {
            return;
        }
        if (this.isOver) {
            return;
        }
        if (((FGFPlayer) player).isOver()) {
            return;
        }
        if (((FGFPlayer) player).isLook()) {
            return;
        }
        byte c1 = (byte) (card >> 16);
        byte c2 = (byte) (card & 0xffff);
        if (-1 != this.useCard[c2]) {
            return;
        }
        if (((IPokerPlayer) player).setHandCard(c1, c2)) {
            this.useCard[c2] = 1;
            PokerUtil.sortByFGF(((IPokerPlayer) player).getHandCard());
            ((FGFPlayer) player).setCurCardType(this.getCardType(((IPokerPlayer) player).getHandCard().get(0), ((IPokerPlayer) player).getHandCard().get(1), ((IPokerPlayer) player).getHandCard().get(2)));
            ((FGFPlayer) player).setUseColorValue(PokerUtil.getFriedGoldenFlowerValue(
                    ((IPokerPlayer) player).getHandCard().get(0), ((IPokerPlayer) player).getHandCard().get(1), ((IPokerPlayer) player).getHandCard().get(2),
                    ((FGFPlayer) player).getCurCardType(), true));
            ((FGFPlayer) player).setUnColorValue(PokerUtil.getFriedGoldenFlowerValue(
                    ((IPokerPlayer) player).getHandCard().get(0), ((IPokerPlayer) player).getHandCard().get(1), ((IPokerPlayer) player).getHandCard().get(2),
                    ((FGFPlayer) player).getCurCardType(), false));

            ((IPokerPlayer) player).initHandCard();
        }
    }

    @Override
    protected void doStart1() {
        this.fgfFAction = new FgfFAction(this, this.timeout);
        this.fgfFAction.setCurOpIndex(this.curOpIndex);
        this.addAction(this.fgfFAction);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_OPERATE, new PCLIPokerNtfFGFOperatorInfo(this.getRoomPlayer(this.bankerIndex).getUid(),null == this.fgfFAction ? 0 : this.fgfFAction.getRemainTime()));
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        FGFPlayer fgfPlayer = (FGFPlayer) this.getRoomPlayer(player.getUid());
        if (null == fgfPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIPokerNtfFGFDeskInfo deskInfo = new PCLIPokerNtfFGFDeskInfo();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.curBureau = null == fgfPlayer ? 0 : fgfPlayer.getRoomPlayerHelper().getCurBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.curLoop = this.curLoop;
       // deskInfo.remain =  null == this.fgfFAction ? 0 : this.fgfFAction.getRemainTime();
        deskInfo.remain = (int)(this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain());
        deskInfo.bankerIndex=this.bankerIndex;
        deskInfo.readyTime = ((IBoxRoomHandle)this.getRoomHandle()).getReadyTime();
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer temp = (IPokerPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            deskInfo.allScore.put(temp.getUid(), this.getClientScore(temp.getScore() + temp.getScore(Score.POKER_FGF_NOTE, false) * 100));
            deskInfo.allNote.put(temp.getUid(), temp.getScore(Score.POKER_FGF_NOTE, false));
            deskInfo.allOnlineState.put(temp.getUid(), temp.isOffline() ? false : true);
            int state = 0; // 1: 弃牌, 2: 输牌, 0: 正常
            if (((FGFPlayer) temp).isDiscard()) {
                state = 1;
            } else if (((FGFPlayer) temp).isOver()) {
                state = 2;
            }
            if (temp.getUid() == player.getUid() && ((FGFPlayer) temp).isLook()) {
                deskInfo.card.addAll(temp.getHandCard());
            }
            deskInfo.allState.put(temp.getUid(), state);
            deskInfo.allLookCard.put(temp.getUid(), ((FGFPlayer) temp).isLook());
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByFGF beginInfoByFGF = new PCLIRoomNtfBeginInfoByFGF();
            beginInfoByFGF.curBureau =this.curBureau;
            beginInfoByFGF.bankerIndex = this.bankerIndex;
            beginInfoByFGF.curLoop = this.curLoop;
            beginInfoByFGF.roomBriefInfo = this.getRoomBriefInfo(player);
            beginInfoByFGF.d = Config.checkWhiteHas(player.getUid(), 3);
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoByFGF);
        }

        PCLIRoomNtfBeginInfoByFGF beginInfoByFGF = new PCLIRoomNtfBeginInfoByFGF();
        beginInfoByFGF.curBureau = 0;
        beginInfoByFGF.bankerIndex = this.bankerIndex;
        beginInfoByFGF.curLoop = this.curLoop;
        beginInfoByFGF.roomBriefInfo = this.getRoomBriefInfo();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoByFGF);

        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (Config.checkWhiteHas(player.getUid(), 3)) {
                PCLIRoomNtfMyHandCardInfo info = new PCLIRoomNtfMyHandCardInfo();
                info.handCard.addAll(player.getHandCard());
                for (int k = 0; k < this.playerNum; ++k) {
                    IPokerPlayer temp = (IPokerPlayer) this.allPlayer[k];
                    if (null == temp || temp.isGuest() || temp.getUid() == player.getUid()) {
                        continue;
                    }
                    List<Byte> tempLst = new ArrayList<>();
                    tempLst.addAll(temp.getHandCard());
                    info.ohc.put(temp.getUid(), tempLst);
                }
                for (byte k = 0; k < 52; ++k) {
                    if (-1 == this.useCard[k]) {
                        info.rc.add(k);
                    }
                }
                player.send(CommandId.CLI_NTF_ROOM_MY_CARD, info);
            }
        }
    }

    @Override
    public ErrorCode addNote(IPlayer player, int value, int isFillUp) {
        if (ERoomState.START != this.roomState.get() || this.finish) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法加注", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer pokerPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == pokerPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法加注", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (pokerPlayer.getIndex() != this.curOpIndex) {
            Logs.ROOM.warn("%s %s 不是当前操作玩家, 无法加注", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (((FGFPlayer) pokerPlayer).isDiscard()) {
            Logs.ROOM.warn("%s %s 已经弃牌, 无法加注", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_DISCARD_NOT_ADD_NOTE;
        }
        if (((FGFPlayer) pokerPlayer).isOver()) {
            Logs.ROOM.warn("%s %s 已经比牌过了, 无法加注", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_COMPARE_NOT_ADD_NOTE;
        }
        if (value < 1) {
            Logs.ROOM.warn("%s %s 下注金额不对, 无法下注", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (1 == this.curLoop && this.firstFollowMaxReb) {
            this.curNote = this.limit;
            value = this.curNote;
        }
        if (1 == isFillUp) {
            if (this.curNote < this.limit) {
                Logs.ROOM.warn("%s %s 没有达到最大下注, 无法压满", this, player);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }
        boolean isLook = ((FGFPlayer) pokerPlayer).isLook();
        int note = (int) Math.ceil(value * 1.0 / (isLook ? this.multiple : 1));
        IArenaRoomPlayerHelper roomPlayerHelper = (IArenaRoomPlayerHelper) pokerPlayer.getRoomPlayerHelper();
                    if (this.isFillUp || (this.fillUp && 1 == isFillUp)) {
                        note = this.curNote * (this.maxLoop - this.curLoop + 1);
                        value = note * (isLook ? this.multiple : 1);
                        if (this.roomType == ERoomType.BOX && ((IBoxRoomHandle) this.getRoomHandle()).getBoxType() == EBoxType.ARENA){
                            int needArenaValue = (value + pokerPlayer.getScore(Score.POKER_FGF_NOTE, false)) * 100 / 10;
                            if (roomPlayerHelper!=null &&!roomPlayerHelper.checkEnoughGold(needArenaValue)) {
                    Logs.ROOM.warn("%s %s 竞技值不足, 无法压满, 需要[%d, %d]", this, player, needArenaValue, roomPlayerHelper.getGold());
                    return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
                }
            }
            this.isFillUp = true;
            ++this.fillUpCnt;
        } else {
            if (note < this.curNote || note > this.limit) {
                Logs.ROOM.warn("%s %s 加注筹码无效, 无法加注, player note:%d, curMinNote:%d curMaxNote:%d", this, player, note, this.curNote, this.limit);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            if (this.roomType == ERoomType.BOX && ((IBoxRoomHandle) this.getRoomHandle()).getBoxType() == EBoxType.ARENA){
                int needArenaValue = (value + pokerPlayer.getScore(Score.POKER_FGF_NOTE, false)) * 100 / 10;
                if (roomPlayerHelper!=null &&!roomPlayerHelper.checkEnoughGold(needArenaValue)) {
                    Logs.ROOM.warn("%s %s 竞技值不足, 无法压满, 需要[%d, %d]", this, player, needArenaValue, roomPlayerHelper.getGold());
                    return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
                }
            }
            this.curNote = note;
        }

        pokerPlayer.addScore(Score.POKER_FGF_NOTE, value, false);

        ((PokerRecord) this.getRecord()).addAddNoteRecordAction(player.getUid(), value, isFillUp);

        PCLIPokerNtfFGFNoteInfo noteInfo = new PCLIPokerNtfFGFNoteInfo();
        noteInfo.notePlayerUid = player.getUid();
        noteInfo.isLook = isLook;
        noteInfo.value = value;
        noteInfo.fillUp = isFillUp;
        noteInfo.isFollow = false;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_NOTE, noteInfo);
        ++this.curLoopOpCnt;
        this.next();
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode followNote(IPlayer player) {
        if (ERoomState.START != this.roomState.get() || this.finish) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法跟注", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer pokerPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == pokerPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法跟注", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (pokerPlayer.getIndex() != this.curOpIndex) {
            Logs.ROOM.warn("%s %s 不是当前操作玩家, 无法跟注", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (((FGFPlayer) pokerPlayer).isDiscard()) {
            Logs.ROOM.warn("%s %s 已经弃牌, 无法跟注", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_DISCARD_NOT_FOLLOW_NOTE;
        }
        if (((FGFPlayer) pokerPlayer).isOver()) {
            Logs.ROOM.warn("%s %s 已经比牌过了, 无法跟注", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_COMPARE_NOT_FOLLOW_NOTE;
        }
        boolean isLook = ((FGFPlayer) pokerPlayer).isLook();
        if (1 == this.curLoop && this.firstFollowMaxReb) {
            this.curNote = this.limit;
        }
        int note = (int) Math.ceil(this.curNote * 1.0 * (isLook ? this.multiple : 1));
        if (this.isFillUp) {
            note = this.curNote * (this.maxLoop - this.curLoop + 1) * (isLook ? this.multiple : 1);
        }

        IArenaRoomPlayerHelper roomPlayerHelper = (IArenaRoomPlayerHelper) pokerPlayer.getRoomPlayerHelper();
        if (this.roomType == ERoomType.BOX && ((IBoxRoomHandle) this.getRoomHandle()).getBoxType() == EBoxType.ARENA){
            int needArenaValue = (note + pokerPlayer.getScore(Score.POKER_FGF_NOTE, false)) * 100 / 10;
            if (roomPlayerHelper!=null &&!roomPlayerHelper.checkEnoughGold(needArenaValue)) {
                Logs.ROOM.warn("%s %s 竞技值不足, 无法压满, 需要[%d, %d]", this, player, needArenaValue, roomPlayerHelper.getGold());
                return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
            }
        }
        if (this.isFillUp) {
            ++this.fillUpCnt;
        }
        pokerPlayer.addScore(Score.POKER_FGF_NOTE, note, false);

        ((PokerRecord) this.getRecord()).addFollowNoteRecordAction(player.getUid(), note);

        PCLIPokerNtfFGFNoteInfo noteInfo = new PCLIPokerNtfFGFNoteInfo();
        noteInfo.notePlayerUid = player.getUid();
        noteInfo.isLook = isLook;
        noteInfo.value = note;
        noteInfo.isFollow = true;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_NOTE, noteInfo);
        ++this.curLoopOpCnt;
        this.next();
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode compare(IPlayer player, long otherPlayerUid) {
        if (ERoomState.START != this.roomState.get() || this.finish) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法比牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer pokerPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == pokerPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法比牌", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (pokerPlayer.getIndex() != this.curOpIndex) {
            Logs.ROOM.warn("%s %s 不是当前操作玩家, 无法比牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (((FGFPlayer) pokerPlayer).isDiscard()) {
            Logs.ROOM.warn("%s %s 已经弃牌, 无法比牌", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_DISCARD_NOT_FOLLOW_NOTE;
        }
        if (((FGFPlayer) pokerPlayer).isOver()) {
            Logs.ROOM.warn("%s %s 已经比牌过了, 无法比牌", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_COMPARE_NOT_COMPARE;
        }
        if (((FGFPlayer) pokerPlayer).isCurLoopComp()) {
            Logs.ROOM.warn("%s %s 已经比牌过了, 无法比牌", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_COMPARE_NOT_COMPARE;
        }
        if (this.curLoop <= 1 || this.curLoop <= this.compareLoop) {
            Logs.ROOM.warn("%s %s 比跟轮, 无法比牌", this, player);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        IPokerPlayer otherPokerPlayer = (IPokerPlayer) this.getRoomPlayer(otherPlayerUid);
        if (null == otherPokerPlayer) {
            Logs.ROOM.warn("%s %s 被比的玩家不在房间中, 无法比牌", this, otherPokerPlayer);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (((FGFPlayer) otherPokerPlayer).isDiscard()) {
            Logs.ROOM.warn("%s %s 被比的玩家已经弃牌, 无法比牌", this, otherPokerPlayer);
            return ErrorCode.ROOM_POKER_ALREADY_DISCARD_NOT_COMPARE;
        }
        if (((FGFPlayer) otherPokerPlayer).isOver()) {
            Logs.ROOM.warn("%s %s 已经比牌过了, 无法比牌", this, otherPokerPlayer);
            return ErrorCode.ROOM_POKER_ALREADY_COMPARE_NOT_COMPARE;
        }
        if (player.getUid() == otherPlayerUid) {
            Logs.ROOM.warn("%s %s 不能跟自己比, 无法比牌", this, otherPokerPlayer);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (this.isFillUp) {
            Logs.ROOM.warn("%s %s 已经压满不能比牌, 无法比牌", this, otherPokerPlayer);
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        //防勾手： 蒙牌比牌，蒙的牌到牌局结束该玩家才能看见。看过的牌只有在最后两人的情况才可以跟蒙的牌比。
        if(colluder && ((FGFPlayer) pokerPlayer).isLook() && !((FGFPlayer) otherPokerPlayer).isLook() && getLiveNum() > 2){
            Logs.ROOM.warn("%s %s 当前玩家没有看牌，无法和其比牌", this, otherPokerPlayer);
            return ErrorCode.ROOM_POKER_FGF_COLLUDER;
        }

//      int note = (int) Math.ceil(this.curNote * this.multiple);
        boolean isLook = ((FGFPlayer) pokerPlayer).isLook();
        int note = (int) Math.ceil(this.curNote * (compareDouble ? 2 : 1) * (isLook ? this.multiple : 1));//比牌双倍 //看牌翻倍
        IArenaRoomPlayerHelper roomPlayerHelper = (IArenaRoomPlayerHelper) pokerPlayer.getRoomPlayerHelper();

        if (this.roomType == ERoomType.BOX && ((IBoxRoomHandle) this.getRoomHandle()).getBoxType() == EBoxType.ARENA){
            int needArenaValue = (note + pokerPlayer.getScore(Score.POKER_FGF_NOTE, false)) * 100 / 10;
            if (roomPlayerHelper!=null &&!roomPlayerHelper.checkEnoughGold(needArenaValue)) {
                Logs.ROOM.warn("%s %s 竞技值不足, 无法压满, 需要[%d, %d]", this, player, needArenaValue, roomPlayerHelper.getGold());
                return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
            }
        }
        pokerPlayer.addScore(Score.POKER_FGF_NOTE, note, false);

        boolean isWin = this.compare(pokerPlayer, otherPokerPlayer);
        ((FGFPlayer) pokerPlayer).setWin(isWin);
        ((FGFPlayer) otherPokerPlayer).setWin(!isWin);
        ((FGFPlayer) pokerPlayer).setCurLoopComp(true);

        ((PokerRecord) this.getRecord()).addCompareCardRecordRecordAction(pokerPlayer.getUid(), otherPokerPlayer.getUid(), isWin ? pokerPlayer.getUid() : otherPokerPlayer.getUid(), isWin ? otherPokerPlayer.getUid() : pokerPlayer.getUid(), note);

        PCLIPokerNtfFGFCompareResultInfo resultInfo = new PCLIPokerNtfFGFCompareResultInfo();
        resultInfo.winPlayerUid = isWin ? pokerPlayer.getUid() : otherPokerPlayer.getUid();
        resultInfo.lostPlayerUid = isWin ? otherPokerPlayer.getUid() : pokerPlayer.getUid();
        resultInfo.initiaorPlayerUid = pokerPlayer.getUid();
        resultInfo.note = note;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_COMPARE_RESULT, resultInfo);

        ++this.overCnt;
        if (this.checkOver()) {
            this.onOver(true);
        } else {
            final FGFRoom self = this;
            DelayAction delayAction = new DelayAction(this, 2000);
            delayAction.setCallback(new ICallback<Object>() {
                @Override
                public void call(Object... args) {
                        ++self.curLoopOpCnt;
                        --self.nextLoopNeedOpCnt;
                        if (self.checkOver()) {
                            self.onOver(false);
                        } else {
                            self.next();
                        }
                }
            });
            this.addAction(delayAction);
        }
        return ErrorCode.OK;
    }

    @Override
    public void next() {
        do {
            this.curOpIndex = (this.curOpIndex + 1) % this.playerNum;
        } while (null == this.allPlayer[this.curOpIndex] || this.allPlayer[this.curOpIndex].isGuest() || ((FGFPlayer) this.allPlayer[this.curOpIndex]).isOver());

        if ((this.isFillUp && (this.nextLoopNeedOpCnt >= this.fillUpCnt)) || ((this.curLoopOpCnt >= this.curLoopNeedOpCnt) && (this.curLoop >= this.maxLoop))) {
            // 结束
            // 最后一个回合
            this.autoCompare();
            this.onOver(false);
            return;
        }
        if (this.curLoopOpCnt >= this.curLoopNeedOpCnt) {
            // 结束一个回合
            this.curLoopOpCnt = 0;
            this.curLoopNeedOpCnt = this.nextLoopNeedOpCnt;
            ++this.curLoop;
            // next loop
            PCLIPokerNtfFGFLoopInfo loopInfo = new PCLIPokerNtfFGFLoopInfo();
            loopInfo.curLoop = this.curLoop;
            this.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_LOOP, loopInfo);
        }
        ((FGFPlayer) this.allPlayer[this.curOpIndex]).setCurLoopComp(false);
        this.fgfFAction.next(this.curOpIndex);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_OPERATE, new PCLIPokerNtfFGFOperatorInfo(this.getRoomPlayer(this.curOpIndex).getUid(),null == this.fgfFAction ? 0 : this.fgfFAction.getRemainTime()));
    }

    @Override
    public ErrorCode discard(IPlayer player) {
        if (ERoomState.START != this.roomState.get() || this.finish) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法弃牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer pokerPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == pokerPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法弃牌", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (((FGFPlayer) pokerPlayer).isDiscard()) {
            Logs.ROOM.warn("%s %s 已经弃牌, 无法弃牌", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_DISCARD;
        }
        if (((FGFPlayer) pokerPlayer).isOver()) {
            Logs.ROOM.warn("%s %s 已经比牌完成, 无法弃牌", this, player);
            return ErrorCode.ROOM_POKER_ALREADY_COMPARE;
        }
//        if (this.curLoop <= this.mustLoop) {
//            Logs.ROOM.warn("%s %s 比跟轮, 无法弃牌", this, player);
//            return ErrorCode.INVALID_DATA;
//        }
        this.discard(pokerPlayer);
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode discard(IRoomPlayer player) {
        ((FGFPlayer) player).setDiscard(true);

        ((PokerRecord) this.getRecord()).addDiscardRecordAction(player.getUid());

        PCLIPokerNtfDiscardInfo discardInfo = new PCLIPokerNtfDiscardInfo();
        discardInfo.playerUid = player.getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_DISCARD, discardInfo);

        ++this.curLoopOpCnt;
        --this.nextLoopNeedOpCnt;
        ++this.overCnt;
        if (this.checkOver()) {
            this.onOver(false);
        } else if (null != this.fgfFAction && this.fgfFAction.getCurOpIndex() == player.getIndex()) {
            this.next();
        }
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode onShowOff(IRoomPlayer player) {
        PCLIRoomNtfShowOffInfoByFgf info = new PCLIRoomNtfShowOffInfoByFgf();
        info.playerUid = player.getUid();
        info.card.addAll(((FGFPlayer) player).getHandCard());
        info.cardType = ((FGFPlayer) player).getCurCardType().getValue();
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_SHOW_OFF, info);
        return ErrorCode.OK;
    }

    @Override
    public void doShowOffOver() {
        this.stop();
    }

    @Override
    protected void gameOver(boolean next) {
        this.record();
        this.getRecord().save();
       this.getRoomHandle().calculateGold();//小局结束后抽水
        this.sendGameOver0(next);
    }

    @Override
    protected void doSendGameOver(boolean next) {

    }

    protected void sendGameOver0(boolean next) {
        PCLIPokerNtfFGFGameOverInfo info = new PCLIPokerNtfFGFGameOverInfo();
        info.next = next;
        info.winPlayerUid = null == this.winPoker ? -1L : this.winPoker.getUid();
        if (0 != this.fgfThreeAward) {
            if (0 != this.threePlayerList.size()) {
                info.threePlayerList.addAll(this.threePlayerList);
            }
        }

        for (int j = 0; j < this.playerNum; ++j) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIPokerNtfFGFGameOverInfo.GameOverInfo gameOverInfo = new PCLIPokerNtfFGFGameOverInfo.GameOverInfo();
            IPlayer iPlayer = player.getPlayer();
            if (null != iPlayer) {
                gameOverInfo.name = iPlayer.getName();
                gameOverInfo.icon = iPlayer.getIcon();
            }
            gameOverInfo.card.addAll(player.getHandCard());
            gameOverInfo.cardType = ((FGFPlayer) player).getCurCardType().getValue();
            gameOverInfo.score = this.getClientScore(player.getScore(Score.SCORE, false));
            IArenaRoomPlayerHelper roomPlayerHelper = (IArenaRoomPlayerHelper) player.getRoomPlayerHelper();
            //gameOverInfo.totalScore = this.getClientScore(player.getScore());
            gameOverInfo.totalScore = this.getClientScore((int)roomPlayerHelper.getGold());
            info.allGameOverInfo.put(player.getUid(), gameOverInfo);
            if (!next) {
                PCLIPokerNtfFGFGameOverInfo.TotalCnt totalCnt = new PCLIPokerNtfFGFGameOverInfo.TotalCnt();
                totalCnt.winCnt = player.getScore(Score.ACC_WIN_CNT, true);
                totalCnt.lostCnt = player.getScore(Score.ACC_LOST_CNT, true);
                totalCnt.maxScore = player.getScore(Score.ACC_MAX_SCORE, true) / 100;
                totalCnt.maxCardType = player.getScore(Score.ACC_POKER_MAX_CARD_TYPE, true);
                gameOverInfo.totalCnt = totalCnt;
            }
        }
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, info);
    }

    protected EPokerCardType getCardType(byte a, byte b, byte c) {
        if (PokerUtil.isFriedGoldenFlowerThree(a, b, c)) {
            return EPokerCardType.FGF_THREE;
        } else if (PokerUtil.isFriedGoldenFlowerSameColorLine(a, b, c)) {
            return EPokerCardType.FGF_SAME_COLOR_AND_LINE;
        } else if (PokerUtil.isFriedGoldenFlowerSameColor(a, b, c)) {
            return EPokerCardType.FGF_SAME_COLOR;
        } else if (PokerUtil.isFriedGoldenFlowerLine(a, b, c)) {
            return EPokerCardType.FGF_LINE;
        } else if (PokerUtil.isFriedGoldenFlowerDouble(a, b, c)) {
            return EPokerCardType.FGF_DOUBLE;
        } else {
            if (PokerUtil.isFriedGoldenFlower235(a, b, c)) {
                return EPokerCardType.FGF_235;
            }
            return EPokerCardType.FGF_NONE;
        }
    }

    /**
     * 比较
     * @param player            发起方
     * @param otherPlayer       被比较方
     * @return
     */
    protected boolean compare(IPokerPlayer player, IPokerPlayer otherPlayer) {
        if (0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFGFPlayRule.SINGLE_235.getValue())) {
            //高牌235吃豹子
            if (EPokerCardType.FGF_235 == ((FGFPlayer) player).getCurCardType() && EPokerCardType.FGF_THREE == ((FGFPlayer) otherPlayer).getCurCardType()) {
                return true;
            }
            if (EPokerCardType.FGF_THREE == ((FGFPlayer) player).getCurCardType() && EPokerCardType.FGF_235 == ((FGFPlayer) otherPlayer).getCurCardType()) {
                return false;
            }
        }
        if(2 == this.compareRule){//2-全比；(先比较大小，再比较花色)
            if(((FGFPlayer) player).getUnColorValue() != ((FGFPlayer) otherPlayer).getUnColorValue()){
                return ((FGFPlayer) player).getUnColorValue() > ((FGFPlayer) otherPlayer).getUnColorValue();
            }
        } else if(0 == this.compareRule){//比较大小
            return ((FGFPlayer) player).getUnColorValue() > ((FGFPlayer) otherPlayer).getUnColorValue();
        }
        return ((FGFPlayer) player).getUseColorValue() > ((FGFPlayer) otherPlayer).getUseColorValue();
    }

    protected void autoCompare() {
        this.winPoker = null;
        PCLIPokerNtfFGFAutoCompareResultInfo infoList  = new PCLIPokerNtfFGFAutoCompareResultInfo();

        for (int i = 0; i < this.playerNum; ++i) {
            int index = (this.bankerIndex + i) % this.playerNum;
            FGFPlayer player = (FGFPlayer) this.allPlayer[index];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (player.isOver()) {
                continue;
            }
            if (null == this.winPoker) {
                this.winPoker = player;
                continue;
            }
            boolean isWin = this.compare(this.winPoker, player);
            ((FGFPlayer) this.winPoker).setWin(isWin);
            player.setWin(!isWin);

            PCLIPokerNtfFGFAutoCompareResultInfo.ResultInfo resultInfo = new PCLIPokerNtfFGFAutoCompareResultInfo.ResultInfo();
            resultInfo.winPlayerUid = isWin ? this.winPoker.getUid() : player.getUid();
            resultInfo.lostPlayerUid = isWin ? player.getUid() : this.winPoker.getUid();
            Logs.ROOM.debug(" %s  winPlayerUid:%s  lostPlayerUid:%s autoCompare resultInfo：", this, resultInfo.winPlayerUid, resultInfo.lostPlayerUid);
            infoList.list.add(resultInfo);

            ((PokerRecord) this.getRecord()).addCompareCardRecordRecordAction(this.winPoker.getUid(), player.getUid(), isWin ? this.winPoker.getUid() : player.getUid(), isWin ? player.getUid() : this.winPoker.getUid(), 0);

            this.winPoker = isWin ? this.winPoker : player;
        }

        this.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_AUTO_COMPARE_RESULT, infoList);
    }

    protected void onOver(boolean compare) {
        this.isOver = true;
        this.finish = true;
        this.fgfFAction.finish();
        this.tick();
        this.fgfFAction = null;
        Logs.ROOM.debug(" %s   in onOver  ", this);

        // 记录分数
        if (null == this.winPoker) {
            for (int i = 0; i < this.playerNum; ++i) {
                FGFPlayer player = (FGFPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (!player.isOver()) {
                    this.winPoker = player;
                    break;
                }
            }
        }

        if(0 == bankerType){//赢家做庄
            this.bankerIndex = this.winPoker.getIndex();
        }

        FGFResultRecordAction action = ((PokerRecord) this.getRecord()).addFGFResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (this.winPoker.getUid() != player.getUid()) {
                int score = this.getScore(player.getScore(Score.POKER_FGF_NOTE, false));
                player.addScore(Score.SCORE, -score, false);
                player.addScore(Score.ACC_TOTAL_SCORE, -score, true);
                this.winPoker.addScore(Score.SCORE, score, false);
                this.winPoker.addScore(Score.ACC_TOTAL_SCORE, score, true);
                player.addScore(Score.ACC_LOST_CNT, 1, true);
                Logs.ROOM.debug(" %s playerUid = %s onOver score = %s  POKER_FGF_NOTE = %s ", this, player.getUid(), score, Score.POKER_FGF_NOTE);
            } else {
                player.addScore(Score.ACC_WIN_CNT, 1, true);
            }

            if (0 != this.fgfThreeAward) {
                //是否是豹子牌型
                if (EPokerCardType.FGF_THREE == ((FGFPlayer) player).getCurCardType()) {
                    this.threePlayerList.add(player.getUid());
                }
            }
        }

        if (0 != this.fgfThreeAward) {
            //豹子奖励人数
            int threeCnt = this.threePlayerList.size();
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (this.threePlayerList.contains(player.getUid())) {
                    player.addScore(Score.SCORE, this.fgfThreeAward * (this.getCurPlayerCnt() - threeCnt) * 100, false);
                    player.addScore(Score.ACC_TOTAL_SCORE, this.fgfThreeAward * (this.getCurPlayerCnt() - threeCnt) * 100, true);
                } else {
                    player.addScore(Score.SCORE, -this.fgfThreeAward * threeCnt * 100, false);
                    player.addScore(Score.ACC_TOTAL_SCORE, -this.fgfThreeAward * threeCnt * 100, true);
                }
                Logs.ROOM.debug(" %s 豹子奖励人数 playerUid = %s onOver score = %s  ", this, player.getUid(), this.fgfThreeAward * (this.getCurPlayerCnt() - threeCnt));
            }
        }

        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            int curScore = player.getScore(Score.SCORE, false);
            player.maxScore(Score.ACC_MAX_SCORE, curScore, true);

            EPokerCardType curType = ((FGFPlayer) player).getCurCardType();
            player.maxScore(Score.ACC_POKER_MAX_CARD_TYPE, curType.getValue(), true);

            action.addResult(player.getUid(), ((IPokerPlayer) player).getHandCard(),
                    this.getClientScore(player.getScore(Score.SCORE, false)),
                    this.getClientScore(player.getScore(Score.ACC_TOTAL_SCORE, true)), ((FGFPlayer) player).getCurCardType().getValue());
        }

        long timeout = 2000 * (this.getCurPlayerCnt() - this.overCnt - 1 + (compare ? 1 : 0));
        if (timeout < 1) {
            this.gameOver(this.checkAgain());
            if (null != this.winPoker) {
                ShowOffAction showOffAction = new ShowOffAction(this, 3000L);
                showOffAction.addPlayer(this.winPoker.getUid());
                this.addAction(showOffAction);
            } else {
                this.stop();
            }
        } else {
            final FGFRoom self = this;
            DelayAction delayAction = new DelayAction(this, timeout);
            delayAction.setCallback(new ICallback<Object>() {
                @Override
                public void call(Object... args) {
                    self.gameOver(self.checkAgain());
                    if (null != self.winPoker) {
                        ShowOffAction showOffAction = new ShowOffAction(self, 3000L);
                        showOffAction.addPlayer(self.winPoker.getUid());
                        self.addAction(showOffAction);
                    } else {
                        self.stop();
                    }
                }
            });
            this.addAction(delayAction);
        }

    }

    protected boolean checkOver() {
        Logs.ROOM.debug(" %s  checkOver overCnt = %s  playerCnt = %s ", this, this.overCnt, this.getCurPlayerCnt());
        if ((this.overCnt - 1) == this.getCurPlayerCnt()) {
            return true;
        }
        int live = getLiveNum();
        return live < 2;
    }

    protected int getLiveNum() {
        int live = 0;
        for (int i = 0; i < this.playerNum; ++i) {
            FGFPlayer player = (FGFPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (!player.isOver()) {
                ++live;
            }
        }
        return live;
    }

    @Override
    protected String getFormatScore(int value) {
        return NumberUtils.get2Decimals(value);
    }

    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value / 10;
    }

    @Override
    public void clear() {
        super.clear();
        this.curLoop = 0;
        this.curOpIndex = -1;
        this.curNote = 0;
        this.finish = true;
        this.fgfFAction = null;
        this.curLoopOpCnt = 0;
        this.curLoopNeedOpCnt = 0;
        this.nextLoopNeedOpCnt = 0;
        this.threePlayerList.clear();
        this.overCnt = 0;
        this.isFillUp = false;
        this.fillUpCnt = 0;
        for (int i = 0; i < this.useCard.length; ++i) {
            this.useCard[i] = -1;
        }
    }

    @Override
    public String toString() {
        return String.format("Room[RoomUid:%d, RoomId:%d State:%s Bureau:%d/%d Loop:%d/%d WatchCnt:%d Rule:%s AllPlayer:%s]",
                this.getRoomUid(), this.getRoomId(), this.roomState.get(), this.curBureau, this.bureau, this.curLoop, this.maxLoop,
                this.watchList.size(), this.info.getRule(), Arrays.toString(this.allPlayer));
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {
        
    }
}
