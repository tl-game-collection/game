package com.xiuxiu.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xiuxiu.core.log.Logs;

import java.util.List;

public final class JsonUtil {
    public static String toJson(Object value) {
        try {
        	Logs.CMD.debug("value:%s",value.toString());
        	String json = JSON.toJSONString(value, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullStringAsEmpty);
        	Logs.CMD.debug("toJson:%s",json);
            return json;
        } catch (Exception e) {
            Logs.CORE.error("JsonUtil.toJson exception, Object:%s", e, value);
        }
        return null;
    }

    public static String toJson(Object value, boolean format) {
        try {
            if (format) {
                return JSON.toJSONString(value, SerializerFeature.PrettyFormat, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullStringAsEmpty);
            } else {
                return JSON.toJSONString(value, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullStringAsEmpty);
            }
        } catch (Exception e) {
            Logs.CORE.error("JsonUtil.toJson exception, Object:%s", e, value);
        }
        return null;
    }

    public static String toJson(Object value, boolean format, boolean enumUsingName) {
        try {
            if (format) {
                if (enumUsingName) {
                    return JSON.toJSONString(value, SerializerFeature.PrettyFormat, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteEnumUsingName);
                } else {
                    return JSON.toJSONString(value, SerializerFeature.PrettyFormat, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteEnumUsingToString);
                }
            } else {
                if (enumUsingName) {
                    return JSON.toJSONString(value, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteEnumUsingName);
                } else {
                    return JSON.toJSONString(value, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteEnumUsingToString);
                }
            }
        } catch (Exception e) {
            Logs.CORE.error("JsonUtil.toJson exception, Object:%s", e, value);
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            Logs.CORE.error("JsonUtil.fromJson exception, json:%s, class:%s", e, json, clazz);
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            Logs.CORE.error("JsonUtil.fromJson exception, json:%s, type:%s", e, json, type);
        }
        return null;
    }

    public static <T> List<T> fromJson2List(String json, Class<T> clazz) {
        try {
            return JSON.parseArray(json, clazz);
        } catch (Exception e) {
            Logs.CORE.error("JsonUtil.fromJson2List exception, json:%s, class:%s", e, json, clazz);
        }
        return null;
    }
}
