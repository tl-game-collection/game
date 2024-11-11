package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TbPai9 {
	public static class TbPai9Info {
		/**
		* 列1
		*/
		protected int id;

		/**
		* 组合1
		*/
		protected int card1;

		/**
		* 组合2
		*/
		protected int card2;

		/**
		* 特殊类型
		*/
		protected int type;

		/**
		* 列3
		*/
		protected int multiple3_1;

		/**
		* 列4
		*/
		protected int multiple3_2;

		/**
		* 列5
		*/
		protected int multiple2_1;

		/**
		* 列6
		*/
		protected int multiple2_2;

		/**
		* 牌值1
		*/
		protected int num1;

		/**
		* 牌值2
		*/
		protected int num2;

		public int getId() {
			return this.id;
		}

		public void setId(int value) {
			this.id = value;
		}

		public int getCard1() {
			return this.card1;
		}

		public void setCard1(int value) {
			this.card1 = value;
		}

		public int getCard2() {
			return this.card2;
		}

		public void setCard2(int value) {
			this.card2 = value;
		}

		public int getType() {
			return this.type;
		}

		public void setType(int value) {
			this.type = value;
		}

		public int getMultiple3_1() {
			return this.multiple3_1;
		}

		public void setMultiple3_1(int value) {
			this.multiple3_1 = value;
		}

		public int getMultiple3_2() {
			return this.multiple3_2;
		}

		public void setMultiple3_2(int value) {
			this.multiple3_2 = value;
		}

		public int getMultiple2_1() {
			return this.multiple2_1;
		}

		public void setMultiple2_1(int value) {
			this.multiple2_1 = value;
		}

		public int getMultiple2_2() {
			return this.multiple2_2;
		}

		public void setMultiple2_2(int value) {
			this.multiple2_2 = value;
		}

		public int getNum1() {
			return this.num1;
		}

		public void setNum1(int value) {
			this.num1 = value;
		}

		public int getNum2() {
			return this.num2;
		}

		public void setNum2(int value) {
			this.num2 = value;
		}
	}

	protected List<TbPai9Info> list = new ArrayList<>();
	protected HashMap<Integer, TbPai9Info> map = new HashMap<>();

	public List<TbPai9Info> getList() {
		return this.list;
	}

	public HashMap<Integer, TbPai9Info> getMap() {
		return this.map;
	}

	public void read(String path) {
		String content = FileUtil.readFileString(path + "/TbPai9.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			TbPai9Info info = new TbPai9Info();
			info.setId(Integer.valueOf(cel[0]));
			info.setCard1(Integer.valueOf(cel[1]));
			info.setCard2(Integer.valueOf(cel[2]));
			info.setType(Integer.valueOf(cel[3]));
			info.setMultiple3_1(Integer.valueOf(cel[4]));
			info.setMultiple3_2(Integer.valueOf(cel[5]));
			info.setMultiple2_1(Integer.valueOf(cel[6]));
			info.setMultiple2_2(Integer.valueOf(cel[7]));
			info.setNum1(Integer.valueOf(cel[8]));
			info.setNum2(Integer.valueOf(cel[9]));
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}