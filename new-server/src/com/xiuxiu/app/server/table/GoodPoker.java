package com.xiuxiu.app.server.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

public class GoodPoker {
	public static class GoodPokerInfo {
		/**
		 * ID
		 */
		protected int id;

		/**
		 * 类型 
		 */
		protected int type;

		/**
		 * 玩法ID
		 */
		protected int gameType;

		/**
		 * 牌库
		 */
		protected List<LinkedList<Byte>> allCard = new ArrayList<LinkedList<Byte>>();

		public int getId() {
			return this.id;
		}

		public void setId(int value) {
			this.id = value;
		}

		public int getType() {
			return this.type;
		}

		public void setType(int value) {
			this.type = value;
		}

		public int getGameType() {
			return gameType;
		}

		public void setGameType(int gameType) {
			this.gameType = gameType;
		}

		public List<LinkedList<Byte>> getAllCard() {
			return allCard;
		}

		public void setAllCard(List<LinkedList<Byte>> allCard) {
			this.allCard = allCard;
		}


	}

	/** 好牌配置,格式：map<游戏类型,Map<Integer, List<GoodPokerInfo>>> */
	protected Map<Integer, Map<Integer, List<GoodPokerInfo>>> map = new HashMap<>();

	public Map<Integer, Map<Integer, List<GoodPokerInfo>>> getMap() {
		return this.map;
	}

	public void read(String path) {
		String content = FileUtil.readFileString(path + "/GoodPoker.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			GoodPokerInfo info = new GoodPokerInfo();
			info.setId(Integer.valueOf(cel[0]));
			info.setType(Integer.valueOf(cel[1]));
			info.setGameType(Integer.valueOf(cel[2]));
			String[] cards = String.valueOf(cel[3]).split(";");
			for (String tempCard : cards) {
				String[] tempCardAttr = tempCard.split(",");
				LinkedList<Byte> tempCardList = new LinkedList<Byte>();
				for (int m = 0, n = tempCardAttr.length; m < n; m++) {
					tempCardList.add(Byte.valueOf(tempCardAttr[m]));
				}
				info.allCard.add(tempCardList);
			}
			Map<Integer, List<GoodPokerInfo>> tempMap = null;
			if (this.map.containsKey(info.getGameType())) {
				tempMap = this.map.get(info.getGameType());
			} else {
				tempMap = new HashMap<Integer, List<GoodPokerInfo>>();
				this.map.put(info.getGameType(), tempMap);
			}
			List<GoodPokerInfo> tempList = null;
			if (tempMap.containsKey(info.getType())) {
				tempList = tempMap.get(info.getType());
			} else {
				tempList = new ArrayList<GoodPoker.GoodPokerInfo>();
				tempMap.put(info.getType(), tempList);
			}
			tempList.add(info);
		}
	}
}