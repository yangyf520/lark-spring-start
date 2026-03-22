package com.larksuite.lark.oapi.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Starter 暴露的 HTTP 能力开关：总开关 + REST 与事件回调可分别关闭。
 * <p>
 * {@code lark.api.enabled=false} 时 REST 与 Webhook 均不注册；为 true 时再受 {@code rest} / {@code webhook} 子项控制。
 */
@ConfigurationProperties(prefix = "lark.api")
public class StarterApiProperties {

    private boolean enabled = true;
    private Rest rest = new Rest();
    private Webhook webhook = new Webhook();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Rest getRest() {
        return rest;
    }

    public void setRest(Rest rest) {
        this.rest = rest == null ? new Rest() : rest;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    public void setWebhook(Webhook webhook) {
        this.webhook = webhook == null ? new Webhook() : webhook;
    }

    /** {@code /api/lark/**} 下 JSON API（带 {@code @LarkApi} 的控制器、Advice、拦截器）。 */
    public static class Rest {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /** 飞书事件 HTTP 回调 {@code /api/lark/webhook}。 */
    public static class Webhook {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
