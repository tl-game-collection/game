package com.xiuxiu.core.net.codec;

import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

import java.util.HashMap;

public class JsonDecoder implements Decoder {
    private static HashMap<Integer, Class> allCommand = new HashMap<>();
    private static HashMap<String, Integer> allCommand2 = new HashMap<>();

    public static void registerCommand(int commandId, Class clazz) {
        if (null == clazz) {
            return;
        }
        allCommand.putIfAbsent(commandId, clazz);
        allCommand2.putIfAbsent(clazz.getSimpleName(), commandId);
    }

    public static int getCommandId(String className) {
        return allCommand2.get(className);
    }

    @Override
    public Object decoder(int commandId, byte[] bytes) throws Exception {
        Class clazz = allCommand.get(commandId);
        if (null == clazz) {
            return null;
        }
        return JsonUtil.fromJson(new String(bytes, Charsetutil.UTF8), clazz);
    }
}
