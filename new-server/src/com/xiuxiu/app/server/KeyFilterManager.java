package com.xiuxiu.app.server;

import com.xiuxiu.core.ds.DAT;
import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyFilterManager {
    private static class KeyFilterManagerHolder {
        private static KeyFilterManager instance = new KeyFilterManager();
    }

    public static KeyFilterManager I = KeyFilterManagerHolder.instance;

    private final DAT dat = new DAT();

    private KeyFilterManager() {
    }

    public void init() {
        String words = FileUtil.readFileString(Config.KEY_FILTER_PATH);
        Logs.CORE.info("初始化关键字数据中");
        if (StringUtil.isEmptyOrNull(words)) {
            return;
        }
        String lineSeparator = System.getProperty("line.separator");
        Logs.CORE.info("当前操作系统换行符：%s", lineSeparator);
        String[] wordArr = words.split(lineSeparator);
        Logs.CORE.info("读取关键字条目数 %d 条", wordArr.length);
        List<String> wordList = new ArrayList<>();
        for (int i = 0, len = wordArr.length; i < len; ++i) {
            if (StringUtil.isEmptyOrNull(wordArr[i])) {
                continue;
            }
            wordList.add(wordArr[i]);
        }
        Collections.sort(wordList);
        this.dat.build(wordList);
        Logs.CORE.info("关键字数据初始化完成");
    }

    public String replace(String word, char mask) {
        long begin = System.currentTimeMillis();
        try {
            return dat.replace(word, mask);
        } finally {
            long cost = System.currentTimeMillis() - begin;
            Logs.CHAT.debug("%s cost:%d ms", word, cost);
        }
    }
}
