package com.zl.blockchain.util;

import com.google.gson.Gson;

/**
 * json字符串与类对象的互相转换
 */
public class JsonUtil {

    private static Gson GSON = new Gson();

    /**
     * 将类对象转换为json字符串
     * @param object 类对象
     * @return
     */
    public static String toString(Object object) {
        return GSON.toJson(object);
    }

    /**
     * 将json字符串转为类对象
     * @param json json字符串
     * @param classOfT 类
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json, Class<T> classOfT) {
        return GSON.fromJson(json,classOfT);
    }
}
