package com.larksuite.lark.common.support;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.Callable;

/** 飞书 SDK 调用简单重试封装（仅对网络/超时类异常重试）。 */
public class ApiExecutor {

    private final ClientProperties properties;

    public ApiExecutor(ClientProperties properties) {
        this.properties = properties;
    }

    /** 按配置重试执行 callable；非传输类异常立即抛出。 */
    public <T> T execute(Callable<T> callable) throws Exception {
        int times = Math.max(1, properties.getRetryTimes() + 1);
        Exception last = null;
        for (int i = 0; i < times; i++) {
            try {
                return callable.call();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } catch (Exception e) {
                if (!isRetryable(e) || i == times - 1) {
                    throw e;
                }
                last = e;
                if (properties.getRetryIntervalMs() > 0) {
                    Thread.sleep(properties.getRetryIntervalMs());
                }
            }
        }
        throw last == null ? new IllegalStateException("Unknown call failure") : last;
    }

    private static boolean isRetryable(Throwable e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof IOException) {
                return true;
            }
            if (t instanceof HttpTimeoutException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
