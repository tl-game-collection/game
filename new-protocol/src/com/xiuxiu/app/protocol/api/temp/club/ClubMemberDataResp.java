package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class ClubMemberDataResp extends ErrorMsg {
   public static class MemberData {
      public long uid;            // 成员Uid
      public String name;         // 昵称
      public long upUid;       //上级Uid
      public String upName;     //上级昵称
      public String divide;     //分成比例
      public long costByGame;    // 游戏消耗的竞技分
      public long curGold;      // 当前竞技分
      public long upTotalScore;  // 总上分
      public long downTotalScore; // 总下分
      public long totalReward;   // 得到的总奖励分
      public long totalExchange; // 奖励分总兑换分数
      public long curRewardValue; // 当前奖励分
      public long totalGameCnt; // 游戏局数
      public long totalGameValue; // 游戏总输赢



      @Override
      public String toString() {
         return "MemberData{" +
                 "uid=" + uid +
                 ", name='" + name + '\'' +
                 ", upUid=" + upUid +
                 ", upName=" + upName + '\'' +
                 '}';
      }
   }

   public List<MemberData> data = new ArrayList<>();

   @Override
   public String toString() {
      return "ClubMemberDataResp{" +
              "data='" + data + '\'' +
              '}';
   }
}
