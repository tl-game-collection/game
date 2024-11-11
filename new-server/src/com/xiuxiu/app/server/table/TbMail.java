package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TbMail {
	public static class TbMailInfo {
		protected int id;
		protected int type;
		protected String title;
		protected String content;
		protected String item;
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
		public String getTitle() {
			return this.title;
		}
		public void setTitle(String value) {
			this.title = value;
		}
		public String getContent() {
			return this.content;
		}
		public void setContent(String value) {
			this.content = value;
		}
		public String getItem() {
			return this.item;
		}
		public void setItem(String value) {
			this.item = value;
		}
	}
	protected List<TbMailInfo> list = new ArrayList<>();
	protected HashMap<Integer, TbMailInfo> map = new HashMap<>();
	public List<TbMailInfo> getList() {
		return this.list;
	}
	public HashMap<Integer, TbMailInfo> getMap() {
		return this.map;
	}
	public void read(String path) {
		String content = FileUtil.readFileString(path + "/TbMail.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			TbMailInfo info = new TbMailInfo();
			info.setId(Integer.valueOf(cel[0]));
			info.setType(Integer.valueOf(cel[1]));
			info.setTitle(cel[2]);
			info.setContent(cel[3]);
			info.setItem(cel[4]);
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}