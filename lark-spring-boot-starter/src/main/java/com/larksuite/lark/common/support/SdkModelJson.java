package com.larksuite.lark.common.support;

import com.google.gson.Gson;

/**
 * 飞书 oapi-sdk 模型多用 Gson 注解；与 Spring 默认 Jackson 混用时，用本类解析请求体 JSON。
 */
public final class SdkModelJson {

    private static final Gson GSON = new Gson();

    private SdkModelJson() {}

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
}
