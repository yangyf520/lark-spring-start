package com.larksuite.lark.sdk.service.auth;

import com.lark.oapi.service.authen.v1.model.CreateAccessTokenRespBody;

/**
 * OAuth 授权码换票成功后，飞书返回的用户侧信息（来自 {@link CreateAccessTokenRespBody}）。
 * <p>
 * 各系统需要的字段不同，请通过 {@link #raw()} 调用 oapi-sdk 提供的 getter（如 {@code getName()}、
 * {@code getAccessToken()}、{@code getOpenId()} 等，以当前 SDK 版本为准）。
 *
 * @param raw 换票响应 data，非空（由 {@link com.larksuite.lark.sdk.controller.AuthController} 在换票成功后传入）
 */
public record LarkOAuthUserProfile(CreateAccessTokenRespBody raw) {
}
