package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CowMultiple {
	public static class CowMultipleInfo {
		protected int id;
		protected int city;
		protected int wtype;
		protected String typeName;
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
		public String getTypeName() {
			return this.typeName;
		}
		public void setTypeName(String value) {
			this.typeName = value;
		}
		public int getNum() {
			return this.num;
		}
		public void setNum(int value) {
			this.num = value;
		}
	}
	protected List<CowMultipleInfo> list = new ArrayList<>();
	protected HashMap<Integer, CowMultipleInfo> map = new HashMap<>();
	public List<CowMultipleInfo> getList() {
		return this.list;
	}
	public HashMap<Integer, CowMultipleInfo> getMap() {
		return this.map;
	}
	public void read(String path) {
		String content = FileUtil.readFileString(path + "/CowMultiple.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			CowMultipleInfo info = new CowMultipleInfo();
			info.setId(Integer.valueOf(cel[0]));
			info.setCity(Integer.valueOf(cel[1]));
			info.setWtype(Integer.valueOf(cel[2]));
			info.setTypeName(cel[3]);
			info.setNum(Integer.valueOf(cel[4]));
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}