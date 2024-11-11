package com.xiuxiu.app.protocol.client.mahjong;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCLIMahjongNtfGameOverInfoByKWX extends PCLIRoomGameOverInfo {
    public static class ScoreInfo extends PCLIMahjongNtfGameOverInfo.ScoreInfo {
        public int horseScore;          // 马分
        public int piaoScore;           // 飘分
        public String totalScore1;       // 目前为止总分


        @Override
        public String toString() {
            return "ScoreInfo{" +
                    "barCnt=" + barCnt +
                    ", gangScore=" + gangScore +
                    ", fangScore=" + fangScore +
                    ", horseScore=" + horseScore +
                    ", piaoScore=" + piaoScore +
                    ", huScore=" + huScore +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", totalScore1='" + totalScore1 + '\'' +
                    '}';
        }
    }

    public HashMap<Long, PCLIMahjongNtfGameOverInfo.ScoreInfo> allScore = new HashMap<>();
    public HashMap<Long, PCLIMahjongNtfGameOverInfo.FinalResult> allFinalResult = new HashMap<>();
    public long fangPaoUid = -1;                                                                                // 放炮PlayerUid
    public long ziMoUid = -1;                                                                                   // 自摸PlayerUid
    public int huCard = -1;                                                                                     // 胡牌card
    public boolean huangZhuang;                                                                                 // 荒庄

    public HashMap<Long/*playerUid*/, List<Byte>> allCard = new HashMap<>();                                        // 所有牌
    public HashMap<Long/*playerUid*/, HashMap<Integer/*huId*/, Integer/*huScore*/>> allPaiXing = new HashMap<>();   // 胡牌型
    public List<Long> huPlayerUids = new ArrayList<>();                                                             // 胡牌玩家Uid列表

    public List<Integer> buyHorse;
    public int buyHorseScore;
    public boolean chaDaJiao;                                                                                   // 查大叫
    public boolean huangZhuangPeiFu;                                                                            // 荒庄赔付
    public List<Long> chaDaJiaoList = new ArrayList<>();                                                        // 查大叫列表
    public boolean youZhongYou;                                                                                 // 油中油
    public byte youZhongYouFumbleCard = -1;                                                                     // 油中油摸到的牌

    public Map<Long,Integer> takeLaiZiList = new HashMap<>();                                                   // 打赖子玩家对应数量

    public HashMap<Long/*playerUid*/, List<Byte>> allCardKou = new HashMap<>();                                        // 所有牌扣牌

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByKWX{" +
                "buyHorse=" + buyHorse +
                ", buyHorseScore=" + buyHorseScore +
                ", chaDaJiao=" + chaDaJiao +
                ", huangZhuangPeiFu=" + huangZhuangPeiFu +
                ", chaDaJiaoList=" + chaDaJiaoList +
                ", youZhongYou=" + youZhongYou +
                ", youZhongYouFumbleCard=" + youZhongYouFumbleCard +
                ", allCardKou=" + allCardKou +
                ", takeLaiZiList=" + takeLaiZiList +
                ", allScore=" + allScore +
                ", allFinalResult=" + allFinalResult +
                ", fangPaoUid=" + fangPaoUid +
                ", ziMoUid=" + ziMoUid +
                ", huCard=" + huCard +
                ", huangZhuang=" + huangZhuang +
                ", allCard=" + allCard +
                ", allPaiXing=" + allPaiXing +
                ", huPlayerUids=" + huPlayerUids +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
