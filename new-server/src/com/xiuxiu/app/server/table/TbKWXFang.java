package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TbKWXFang {
	public static class TbKWXFangInfo {
		protected int id;
		protected int XG;
		protected int XY;
		protected int SY;
		protected int SZ;
		protected int YC;
		public int getId() {
			return this.id;
		}
		public void setId(int value) {
			this.id = value;
		}
		public int getXG() {
			return this.XG;
		}
		public void setXG(int value) {
			this.XG = value;
		}
		public int getXY() {
			return this.XY;
		}
		public void setXY(int value) {
			this.XY = value;
		}
		public int getSY() {
			return this.SY;
		}
		public void setSY(int value) {
			this.SY = value;
		}
		public int getSZ() {
			return this.SZ;
		}
		public void setSZ(int value) {
			this.SZ = value;
		}
		public int getYC() {
			return this.YC;
		}
		public void setYC(int value) {
			this.YC = value;
		}
	}
	protected List<TbKWXFangInfo> list = new ArrayList<>();
	protected HashMap<Integer, TbKWXFangInfo> map = new HashMap<>();
	public List<TbKWXFangInfo> getList() {
		return this.list;
	}
	public HashMap<Integer, TbKWXFangInfo> getMap() {
		return this.map;
	}
	public void read(String path) {
		String content = FileUtil.readFileString(path + "/TbKWXFang.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			TbKWXFangInfo info = new TbKWXFangInfo();
			info.setId(Integer.valueOf(cel[0]));
			info.setXG(Integer.valueOf(cel[1]));
			info.setXY(Integer.valueOf(cel[2]));
			info.setSY(Integer.valueOf(cel[3]));
			info.setSZ(Integer.valueOf(cel[4]));
			info.setYC(Integer.valueOf(cel[5]));
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}