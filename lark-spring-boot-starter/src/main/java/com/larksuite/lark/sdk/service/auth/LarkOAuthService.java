package com.larksuite.lark.sdk.service.auth;

import com.lark.oapi.service.authen.v1.model.CreateAccessTokenResp;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 飞书网页 OAuth：授权码换 user_access_token 成功后，{@code GET /lark/auth/authorize} 的业务回调。
 * <p>
 * Starter 会注册默认 Bean（继承 {@link #onAuthorized} 的默认实现）；宿主应再提供自定义 Bean（如 {@code @Component}）
 * 覆盖之，在其中写登录、Session 等。若未覆盖且浏览器仍访问该 GET，默认实现会抛错（仅 POST 换票则不受影响）。
 * 说明见模块 {@code README.md}「LarkOAuthService」。
 */
public interface LarkOAuthService {

    /**
     * @param appKey         使用的应用配置键（与 {@code lark.oapi.apps} 的 key 一致；未传 query 时为当前 primary）
     * @param request        当前请求（可读写 session、cookie 等）
     * @param state          OAuth {@code state} 原样透传，可能为空
     * @param userProfile    换票响应中的用户信息，见 {@link LarkOAuthUserProfile#raw()}
     * @param tokenResponse  飞书 SDK 完整换票响应（与 {@code userProfile.raw()} 同源，便于取 requestId 等）
     * @return 直接作为 HTTP 响应体返回（由全局 Advice 统一包装）
     */
    default Object onAuthorized(String appKey, HttpServletRequest request, String state,
                                LarkOAuthUserProfile userProfile, CreateAccessTokenResp tokenResponse) throws Exception {
        throw new IllegalStateException("请实现 LarkOAuthService，或换票仅用 POST /lark/auth/access-token");
    }
}
