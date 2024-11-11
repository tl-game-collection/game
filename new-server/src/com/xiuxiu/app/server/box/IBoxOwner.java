package com.xiuxiu.app.server.box;

import java.util.Map;
import java.util.Set;

import com.xiuxiu.app.server.score.BoxArenaScore;


/**
 * 包厢接口
 * 
 * @author Administrator
 *
 */
public interface IBoxOwner {

    /**
     * 获取包厢
     * 
     * @param boxUid
     * @return
     */
    Box getBox(long boxUid);
    
    /**
     * 获取所有包厢
     * @return
     */
    Map<Long, Box> getAllBox();
    
    /**
     * 获取玩法桌数量
     * @return
     */
    int getBoxSize();

    /**
     * 添加包厢
     * 
     * @param box
     * @return
     */
    void addBox(Box box);
    
    /**
     * 销毁包厢
     * 
     * @param boxUid
     */
    void destroyBox(long boxUid);

    /**
     * 添加包厢大赢家
     * 
     * @param finalWinClubUid
     * @param finalWinPlayerUid
     */
    void addBoxFinalWinner(Long finalWinClubUid, Long finalWinPlayerUid);

    /**
     * 添加包厢局数和分数
     * 
     * @param fromClubUid
     * @param playerUid
     * @param score
     * @param bureau
     * @param now
     */
    void addBoxScoreAndBureau(long fromClubUid, long playerUid, int score, int bureau, long now);

    /**
     * 修改竞技值,默认更新排行榜
     * @param fromClubUid
     * @param playerUid
     * @param value
     * @param optPlayer
     * @return
     */
    int addMemberValueByBox(long fromClubUid, long playerUid, int value, long optPlayer);

    /**
     * 修改竞技值
     * @param fromClubUid
     * @param playerUid
     * @param value
     * @param optPlayer
     * @param needUpdateRank
     * @return
     */
    int addMemberValueByBox(long fromClubUid, long playerUid, int value, long optPlayer, boolean needUpdateRank);

    /**
     * 分配服务费
     * @param boxUid
     * @param playerUid
     * @param cost
     * @param time
     */
    void divideServiceCharge(long boxUid, long playerUid, int cost, long time);
    
    /**
     * 当完成一局游戏时调用
     * @param boxUid
     * @param playerIds
     */
    void onFinishGame(long boxUid, Set<Long> playerIds);

    /**
     * 当完成一大局游戏时调用
     * @param fromClubUid
     * @param playerUid
     * @param now
     */
    void onFinishAllBureau(long fromClubUid, long playerUid, long now);
    
    BoxArenaScore getBoxArenaScoreIfCreate(long fromClubUid, long boxUid, long playerUid);
}
