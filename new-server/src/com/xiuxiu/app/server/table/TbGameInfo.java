package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import com.xiuxiu.core.utils.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TbGameInfo {
	public static class TbGameInfoInfo {
		/**
		* ID
		*/
		protected int id;

		/**
		* 名称
		*/
		protected String name;

		/**
		* 是否有观察者
1是
		*/
		protected int watch;

		/**
		* 1是4人和4人以下游戏
2是8人游戏
3是百人游戏
		*/
		protected int gameType;

		public int getId() {
			return this.id;
		}

		public void setId(int value) {
			this.id = value;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String value) {
			this.name = value;
		}

		public int getWatch() {
			return this.watch;
		}

		public void setWatch(int value) {
			this.watch = value;
		}

		public int getGameType() {
			return this.gameType;
		}

		public void setGameType(int value) {
			this.gameType = value;
		}
	}

	protected List<TbGameInfoInfo> list = new ArrayList<>();
	protected HashMap<Integer, TbGameInfoInfo> map = new HashMap<>();

	public List<TbGameInfoInfo> getList() {
		return this.list;
	}

	public HashMap<Integer, TbGameInfoInfo> getMap() {
		return this.map;
	}

	public void read(String path) {
		String content = FileUtil.readFileString(path + "/TbGameInfo.txt");
		if (StringUtil.isEmptyOrNull(content)) {
			return;
		}
		content = content.replaceAll("\r\n", "\n");
		String[] allLine = content.split("\n");
		for (int i = 0, len = allLine.length; i < len; ++i) {
			String line = allLine[i];
			String[] cel = line.split("\t");
			TbGameInfoInfo info = new TbGameInfoInfo();
			info.setId(JsonUtil.fromJson(cel[0], int.class));
			info.setName(String.valueOf(cel[1]));
			info.setWatch(Integer.valueOf(cel[2]));
			info.setGameType(Integer.valueOf(cel[3]));
			this.list.add(info);
			this.map.put(Integer.valueOf(cel[0]), info);
		}
	}
}