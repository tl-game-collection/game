package com.xiuxiu.app.server.room.normal.poker.cow;

import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.normal.IRoom;

public interface ICowRoom extends IRoom {
   /**
    * 获取配置信息
    * @return
    */
   CowInfo getCowInfo();
   /**
    * 比牌
    */
   void onDealCard();

   /**
    * 比牌结束
    */
   void onOver();
   /**
    * 竞技分兑换游戏分
    * @return
    */
   int getExchangeGoldForScore(long gold);

   /**
    * 当前阶段
    */
   int getCurPhase();

   void showCardRobBanker();
}
