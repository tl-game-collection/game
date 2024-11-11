package com.xiuxiu.app.server.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.server.table.GoodPoker.GoodPokerInfo;
import com.xiuxiu.core.utils.RandomUtil;

public class GoodPokerManager {
	private static class GoodCardsManagerHolder {
		private static GoodPokerManager instance = new GoodPokerManager();
	}

	public static GoodPokerManager I = GoodCardsManagerHolder.instance;

	/**
	 * 好牌配置,格式：map<游戏类型,map<好牌类型,List<GoodPokerInfo>>>>
	 */
	private Map<Integer, Map<Integer, List<GoodPokerInfo>>> goodPokers = new HashMap<>();
	
	private GoodPokerManager() {
	}
	
	public void init(GoodPoker goodPoker) {
		this.goodPokers.putAll(goodPoker.getMap());
	}
	
	public Map<Integer, List<GoodPokerInfo>> getGoodPokerByGameType(int gameType){
		return goodPokers.get(gameType);
	}
	
	public List<GoodPokerInfo> getGoodPokerInfo(int gameType, int playType){
		if (this.goodPokers.containsKey(gameType)) {
			Map<Integer, List<GoodPokerInfo>> tempMap = this.goodPokers.get(gameType);
			List<GoodPokerInfo> tempList = tempMap.get(playType);
			if (null == tempList || tempList.size() == 0) {
				return null;
			}
			return tempList;
		}
		return null;
	}
	
}
