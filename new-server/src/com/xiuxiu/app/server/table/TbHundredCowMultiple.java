package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TbHundredCowMultiple {
	public static class TbHundredCowMultipleInfo {
		protected int id;
		protected int city;
		protected int wtype;
		protected int num;
		public int getId() {
			return this.id;
		}
		public void setId(int value) {
			this.id = value;
		}
		public int getCity() {
			return this.city;
		}
		public void setCity(int value) {
			this.city = value;
		}
		public int getWtype() {
			return this.wtype;
		}
		public void setWtype(int value) {
			this.wtype = value;
		}
		public int getNum() {
			return this.num;
		}
		public void setNum(int value) {
			this.num = value;
		}
	}
	protected List<TbHundredCowMultipleInfo> list = new ArrayList<>();
	protected HashMap<Integer, TbHundredCowMultipleInfo> map = new HashMap<>();
	public List<TbHundredCowMultipleInfo> getList() {
		return this.list;
	}
	public HashMap<Integer, TbHundredCowMultipleInfo> getMap() {
		return this.map;
	}
	public void read(String path) {
		String content = FileUtil.readFileString(path + "/TbHundredCowMultiple.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			TbHundredCowMultipleInfo info = new TbHundredCowMultipleInfo();
			info.setId(Integer.valueOf(cel[0]));
			info.setCity(Integer.valueOf(cel[1]));
			info.setWtype(Integer.valueOf(cel[2]));
			info.setNum(Integer.valueOf(cel[3]));
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}