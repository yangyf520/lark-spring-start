package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.authen.v1.model.GetUserInfoResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.identity.IdentityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户身份：需 user_access_token。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/identity", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityController {

    private final IdentityService identityService;

    /**
     * 构造注入。
     * <p>
     * @param identityService 身份服务
     */
    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    /**
     * 用户信息请求体。
     * <p>
     * @param appKey          应用配置键，可空（使用 primary）
     * @param userAccessToken 用户访问令牌，必填
     */
    public record UserInfoReq(String appKey, @NotBlank String userAccessToken) {}

    /**
     * 使用 user_access_token 获取当前用户资料。
     * <p>
     * @param req 请求体
     * @return 飞书 SDK {@link GetUserInfoResp}
     */
    @PostMapping(path = "/user-info", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserInfoResp userInfo(@Valid @RequestBody UserInfoReq req) throws Exception {
        return identityService.getUserInfo(req.appKey(), req.userAccessToken());
    }
}
