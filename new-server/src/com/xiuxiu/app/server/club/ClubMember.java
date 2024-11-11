package com.xiuxiu.app.server.club;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClubMember extends BaseTable {
    private long clubUid;                     //clubUid
    private long playerUid;                   //playerUid
    private AtomicInteger jobType = new AtomicInteger(0);   //职位
    private long privilege;                   //权限
    private int showNick;                     //显示昵称
    private long uplinePlayerUid = -1;         //上线uid
    private int state;                        //成员状态0.正常 1.禁玩
    private long joinTime;                    //玩家加入俱乐部时间
    private String extra;                     //备注
    private int convert;                        // 兑换竞技分 0 没有兑换过 1 兑换过
    private int onlyUpLineSetGold;             //是否只有上级才能上下分 0.不是 1.是
    /**
     * 奖励分分成比例活动，针对成员
     */
    private int divide;
    /** 最后次修改奖励分分成比例活动（针对成员）时间 */
    private long divideTime;
    /**
     * 奖励分分成比例活动，针对一条线
     */
    private int divideLine;
    /** 最后次修改奖励分分成比例活动（针对一条线）时间 */
    private long divideLineTime;
    /**
     * 成员金币活动相关数据
     */
    /**
     * 成员金币活动相关数据，玩家任务<boxid,次数>
     */
    private Map<Long, Integer> goldActivityCount = new ConcurrentHashMap<>();
    
    /**
     * 玩家喜欢玩的主要游戏，格式：map<游戏主类型,list<游戏子类型>>
     */
    private Map<Integer, List<Integer>> likeGames = new ConcurrentHashMap<>();

    /**
     * 财务对应描述信息【可以用:分开】
     */
    public String treasurerDesc;

    public boolean isForbidPlay() {
        return state == 1;
    }

    public ClubMember(){
        this.setTableType(ETableType.TB_CLUE_MEMBER);
    }

    @Override
    public long getUid() {
        return uid;
    }

    @Override
    public void setUid(long uid) {
        this.uid = uid;
    }
    
    public String getLikeGamesDb() {
        return JSON.toJSONString(likeGames);
    }

    public void setLikeGamesDb(String likeGames) {
        if (StringUtil.isEmptyOrNull(likeGames)) {
            return;
        }
        Map<Integer, List<Integer>> temp = JsonUtil.fromJson(likeGames, new TypeReference<Map<Integer, List<Integer>>>() {
        });
        if (null != temp) {
            this.likeGames = temp;
        }
    }
    
    @JSONField(serialize = false)
    public void changeLikeGames(int gameType, List<Integer> gameSubTypes) {
        if (null == gameSubTypes || gameSubTypes.size() == 0) {
            likeGames.remove(gameType);
        } else {
            likeGames.put(gameType, gameSubTypes);
        }
    }
    
    /**
     * 是否设置了主要玩的游戏
     * @return
     */
    @JSONField(serialize = false)
    public boolean isSetLikeGame() {
        return !likeGames.isEmpty();
    }
    
    @JSONField(serialize = false)
    public boolean isLikeGame(int gameType, int gameSubType) {
        if (isSetLikeGame()) {
            return likeGames.containsKey(gameType) && likeGames.get(gameType).contains(gameSubType);
        }
        return Boolean.TRUE;
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public int getJobType() {
        return jobType.get();
    }

    public void setJobType(int jobType) {
        this.jobType.getAndSet(jobType);
    }

    public boolean checkJobType(EClubJobType jobType){
        return (this.getJobType() & jobType.getType()) > 0;
    }

    public void setJobType(EClubJobType jobType,boolean isSet){
        if (isSet){
            this.setJobType(this.getJobType() | jobType.getType());
            this.dirty = true;
        }else{
            if (checkJobType(jobType)){
                this.setJobType(this.getJobType() ^ jobType.getType());
                this.dirty = true;
            }
        }

    }

    public long getPrivilege() {
        return privilege;
    }

    public void setPrivilege(long privilege) {
        this.privilege = privilege;
    }

    public int getShowNick() {
        return showNick;
    }

    public void setShowNick(int showNick) {
        this.showNick = showNick;
    }

    public long getUplinePlayerUid() {
        return uplinePlayerUid;
    }

    public void setUplinePlayerUid(long uplinePlayerUid) {
        this.uplinePlayerUid = uplinePlayerUid;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    
    public String getGoldActivityCountDb() {
        return JSON.toJSONString(goldActivityCount);
    }

    public void setGoldActivityCountDb(String goldActivity) {
        if (StringUtil.isEmptyOrNull(goldActivity)) {
            return;
        }
        Map<Long, Integer> temp = JsonUtil.fromJson(goldActivity, new TypeReference<Map<Long, Integer>>() {
        });
        if (null != temp) {
            this.goldActivityCount = temp;
        }
    }

    public int getConvert() {
        return convert;
    }

    public void setConvert(int convert) {
        this.convert = convert;
    }

    public boolean checkOnlyUpLineSetGold(){
        return false;//return getOnlyUpLineSetGold() != 0;
    }

    public int getOnlyUpLineSetGold() {
        return onlyUpLineSetGold;
    }

    public void setOnlyUpLineSetGold(int onlyUpLineSetGold) {
        this.onlyUpLineSetGold = onlyUpLineSetGold;
    }

    public int getDivide() {
        return divide;
    }

    public void setDivide(int divide) {
        this.divide = divide;
    }

    public int getDivideLine() {
        return divideLine;
    }

    public void setDivideLine(int divideLine) {
        this.divideLine = divideLine;
    }

    public synchronized void changeDivideAndDivideLine(int divide, int divideLine) {
        this.setDivide(divide);
        this.setDivideLine(divideLine);
        this.setDivideTime(System.currentTimeMillis());
        this.setDivideLineTime(System.currentTimeMillis());
        this.setDirty(Boolean.TRUE);

    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @JSONField(serialize = false)
    public Map<Long, Integer> getGoldActivityCount() {
        return goldActivityCount;
    }

    public long getDivideTime() {
        return divideTime;
    }

    public void setDivideTime(long divideTime) {
        this.divideTime = divideTime;
    }

    public long getDivideLineTime() {
        return divideLineTime;
    }

    public void setDivideLineTime(long divideLineTime) {
        this.divideLineTime = divideLineTime;
    }

    public String getTreasurerDesc() {
        return treasurerDesc;
    }

    public void setTreasurerDesc(String treasurerDesc) {
        this.treasurerDesc = treasurerDesc;
    }
}
