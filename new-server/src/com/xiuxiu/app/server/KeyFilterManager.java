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
        if (StringUtil.isEmptyOrNull(words)) {
            return;
        }
        String[] wordArr = words.split("\r\n");
        List<String> wordList = new ArrayList<>();
        for (int i = 0, len = wordArr.length; i < len; ++i) {
            if (StringUtil.isEmptyOrNull(wordArr[i])) {
                continue;
            }
            wordList.add(wordArr[i]);
        }
        Collections.sort(wordList);
        this.dat.build(wordList);
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
