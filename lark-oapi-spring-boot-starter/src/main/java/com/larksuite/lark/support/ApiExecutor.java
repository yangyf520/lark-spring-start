package com.larksuite.lark.support;

import java.util.concurrent.Callable;

/** 飞书 SDK 调用简单重试封装。 */
public class ApiExecutor {

    private final ClientProperties properties;

    public ApiExecutor(ClientProperties properties) {
        this.properties = properties;
    }

    /** 按配置重试执行 callable。 */
    public <T> T execute(Callable<T> callable) throws Exception {
        int times = Math.max(1, properties.getRetryTimes() + 1);
        Exception last = null;
        for (int i = 0; i < times; i++) {
            try {
                return callable.call();
            } catch (Exception e) {
                last = e;
                if (i < times - 1 && properties.getRetryIntervalMs() > 0) {
                    Thread.sleep(properties.getRetryIntervalMs());
                }
            }
        }
        throw last == null ? new IllegalStateException("Unknown call failure") : last;
    }
}
