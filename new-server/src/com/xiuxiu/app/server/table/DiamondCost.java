package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiamondCost {
	public static class DiamondCostInfo {
		protected int id;
		protected int type;
		protected int gameType;
		protected int bureau;
		protected int num;
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
			return this.gameType;
		}
		public void setGameType(int value) {
			this.gameType = value;
		}
		public int getBureau() {
			return this.bureau;
		}
		public void setBureau(int value) {
			this.bureau = value;
		}
		public int getNum() {
			return this.num;
		}
		public void setNum(int value) {
			this.num = value;
		}
	}
	protected List<DiamondCostInfo> list = new ArrayList<>();
	protected HashMap<Integer, DiamondCostInfo> map = new HashMap<>();
	public List<DiamondCostInfo> getList() {
		return this.list;
	}
	public HashMap<Integer, DiamondCostInfo> getMap() {
		return this.map;
	}
	public void read(String path) {
		String content = FileUtil.readFileString(path + "/DiamondCost.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			DiamondCostInfo info = new DiamondCostInfo();
			info.setId(Integer.valueOf(cel[0]));
			info.setType(Integer.valueOf(cel[1]));
			info.setGameType(Integer.valueOf(cel[2]));
			info.setBureau(Integer.valueOf(cel[3]));
			info.setNum(Integer.valueOf(cel[4]));
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}