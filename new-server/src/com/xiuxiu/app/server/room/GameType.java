package com.xiuxiu.app.server.room;

import com.xiuxiu.app.server.table.TbGameInfo;
import com.xiuxiu.app.server.table.TbGameInfoManager;

public final class GameType {
	public static final int GAME_TYPE_KWX = 10001; // 卡五星
	public static final int GAME_TYPE_YJLY = 10002; // 一脚癞油(荆楚晃晃)
	public static final int GAME_TYPE_HZLZG = 10003; // 红中癞子杠
	public static final int GAME_TYPE_HHMJ = 10004; // 晃晃麻将
	public static final int GAME_TYPE_HZMJ = 10005; // 红中麻将
	public static final int GAME_TYPE_XZDD = 10006; // 血战到底
	public static final int GAME_TYPE_QPSL = 10007; // 七皮四癞
	public static final int GAME_TYPE_SYPSL = 10008; // 十一皮四癞
	public static final int GAME_TYPE_ZFBG = 10009; // 中发白杠
	public static final int GAME_TYPE_HCHH = 10010; // 汉川晃晃(一脚癞油)
	public static final int GAME_TYPE_YCXL = 10011; // 宜昌血流
	public static final int GAME_TYPE_WHMJ = 10012; // 武汉麻将
	public static final int GAME_TYPE_SCXL = 10013; // 四川血流
	public static final int GAME_TYPE_CBDD = 10014; // 赤壁剁刀
	public static final int GAME_TYPE_GYZJ = 10015; // 贵阳抓鸡
	public static final int GAME_TYPE_ZZMJ = 10016; // 转转麻将
	public static final int GAME_TYPE_CSMJ = 10017; // 长沙麻将
	public static final int GAME_TYPE_WHHH = 10018; // 武汉晃晃
	public static final int GAME_TYPE_MCMJ = 10019; // 麻城麻将
	public static final int GAME_TYPE_XTHH = 10020; // 仙桃晃晃
	public static final int GAME_TYPE_LYKD = 10021; // 抠点
	public static final int GAME_TYPE_TDH = 10022; // 推倒胡
	public static final int GAME_TYPE_YXMJ = 10023; // 阳新麻将
	public static final int GAME_TYPE_HSMJ = 10024; // 黄石麻将
	public static final int GAME_TYPE_FZMJ = 10025; // 福州麻将
	public static final int GAME_TYPE_YYMJ = 10026; // 益阳麻将
	public static final int GAME_TYPE_DYMJ = 10027; // 大冶麻将

	public static final int GAME_TYPE_RUN_FAST = 20001; // 跑得快
	public static final int GAME_TYPE_LANDLORD = 20002; // 斗地主
	public static final int GAME_TYPE_COW = 20003; // 牛牛
	public static final int GAME_TYPE_ARCH = 20004; // 打拱
	public static final int GAME_TYPE_FRIED_GOLDEN_FLOWER = 20005; // 炸金花
	public static final int GAME_TYPE_5_10_K = 20006; // 五十K
	public static final int GAME_TYPE_LEVELUP = 20007; // 升级
	public static final int GAME_TYPE_THIRTEEN = 20008; // 十三水
	public static final int GAME_TYPE_DIABLO = 20009; // 大菠萝
	public static final int GAME_TYPE_GDY = 20010; // 干瞪眼
	public static final int GAME_TYPE_CHELAKE = 20011; // 扯拉克
	public static final int GAME_TYPE_PAIGOW = 20012; // 牌九
	public static final int GAME_TYPE_SG = 20013; // 三公
	public static final int GAME_TYPE_TDK = 20014; // 填大坑
	public static final int GAME_TYPE_FIVECARDTEXAS = 20015; // 德州
	public static final int GAME_TYPE_FIVECARDSTUD = 20016; // 梭哈
	public static final int GAME_TYPE_FOLIE_FGF = 20017; // 疯狂炸金花
	public static final int GAME_TYPE_PAIGOW_ROB = 20018; // 抢庄牌九
	public static final int GAME_TYPE_BLACK_JACK = 20020; // 21点

	public static final int GAME_TYPE_HUNDRED_REDBAGBOMB = 30001; // 红包埋雷
	public static final int GAME_TYPE_HUNDRED_COW = 30002; // 百人拼十牛牛
	public static final int GAME_TYPE_HUNDRED_SJDZ = 30003; // 世界大战牛牛
	public static final int GAME_TYPE_HUNDRED_28T = 30004; // 二八筒子
	public static final int GAME_TYPE_HUNDRED_LHD = 30005; // 龙虎斗
	public static final int GAME_TYPE_HUNDRED_REDBLACK = 30006; // 红黑大战
	public static final int GAME_TYPE_HUNDRED_SHARK = 30007; // 金鲨银鲨;
	public static final int GAME_TYPE_HUNDRED_BACCARAT = 30008; // 百家乐；
	public static final int GAME_TYPE_HUNDRED_HBPB = 30009; // 红黑梅方;
	public static final int GAME_TYPE_HUNDRED_HBML = 30010; // 红包雨
	public static final int GAME_TYPE_HUNDRED_BZW = 30011; // 豹子王

	public static final int GAME_TYPE_WORDCARD_DAYE = 40001; // 大冶字牌

	public static final int GAME_TYPE_BILLIARDS = 100001; // 桌球

	public static boolean hasGameType(int gameType) {
		return null != TbGameInfoManager.I.getGameInfo(gameType);
	}

	public static String getGameName(int gameType) {
		TbGameInfo.TbGameInfoInfo info = TbGameInfoManager.I.getGameInfo(gameType);
		return null == info ? "" : info.getName();
	}

	public static boolean isWatchGame(int gameType) {
		TbGameInfo.TbGameInfoInfo info = TbGameInfoManager.I.getGameInfo(gameType);
		return null == info ? false : 1 == info.getWatch();
	}

	public static boolean isFourGame(int gameType) {
		TbGameInfo.TbGameInfoInfo info = TbGameInfoManager.I.getGameInfo(gameType);
		return null == info ? false : 1 == info.getGameType();
	}

	public static boolean isEightGame(int gameType) {
		TbGameInfo.TbGameInfoInfo info = TbGameInfoManager.I.getGameInfo(gameType);
		return null == info ? false : 2 == info.getGameType();
	}

	public static boolean isHundredGame(int gameType) {
		TbGameInfo.TbGameInfoInfo info = TbGameInfoManager.I.getGameInfo(gameType);
		return null == info ? false : 3 == info.getGameType();
	}

	public static boolean isArenaGame(int gameType) {
		return gameType == GameType.GAME_TYPE_COW || gameType == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER
				|| gameType == GameType.GAME_TYPE_THIRTEEN || gameType == GameType.GAME_TYPE_PAIGOW || gameType == GameType.GAME_TYPE_SG;
	}
}
