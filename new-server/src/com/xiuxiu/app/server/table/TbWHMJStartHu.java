package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TbWHMJStartHu {
	public static class TbWHMJStartHuInfo {
		protected int id;
		protected int dianpao_5;
		protected int other_5;
		protected int zimo_5;
		protected int dianpao_6;
		protected int other_6;
		protected int zimo_6;
		public int getId() {
			return this.id;
		}
		public void setId(int value) {
			this.id = value;
		}
		public int getDianpao_5() {
			return this.dianpao_5;
		}
		public void setDianpao_5(int value) {
			this.dianpao_5 = value;
		}
		public int getOther_5() {
			return this.other_5;
		}
		public void setOther_5(int value) {
			this.other_5 = value;
		}
		public int getZimo_5() {
			return this.zimo_5;
		}
		public void setZimo_5(int value) {
			this.zimo_5 = value;
		}
		public int getDianpao_6() {
			return this.dianpao_6;
		}
		public void setDianpao_6(int value) {
			this.dianpao_6 = value;
		}
		public int getOther_6() {
			return this.other_6;
		}
		public void setOther_6(int value) {
			this.other_6 = value;
		}
		public int getZimo_6() {
			return this.zimo_6;
		}
		public void setZimo_6(int value) {
			this.zimo_6 = value;
		}
	}
	protected List<TbWHMJStartHuInfo> list = new ArrayList<>();
	protected HashMap<Integer, TbWHMJStartHuInfo> map = new HashMap<>();
	public List<TbWHMJStartHuInfo> getList() {
		return this.list;
	}
	public HashMap<Integer, TbWHMJStartHuInfo> getMap() {
		return this.map;
	}
	public void read(String path) {
		String content = FileUtil.readFileString(path + "/TbWHMJStartHu.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			TbWHMJStartHuInfo info = new TbWHMJStartHuInfo();
			info.setId(Integer.valueOf(cel[0]));
			info.setDianpao_5(Integer.valueOf(cel[1]));
			info.setOther_5(Integer.valueOf(cel[2]));
			info.setZimo_5(Integer.valueOf(cel[3]));
			info.setDianpao_6(Integer.valueOf(cel[4]));
			info.setOther_6(Integer.valueOf(cel[5]));
			info.setZimo_6(Integer.valueOf(cel[6]));
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}