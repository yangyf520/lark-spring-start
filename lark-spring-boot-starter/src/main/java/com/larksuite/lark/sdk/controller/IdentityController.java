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

/** 用户身份：需 user_access_token。 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/identity", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    public record UserInfoReq(String appKey, @NotBlank String userAccessToken) {}

    /** 获取用户身份信息：使用 user_access_token 获取当前用户资料。 */
    @PostMapping(path = "/user-info", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserInfoResp userInfo(@Valid @RequestBody UserInfoReq req) throws Exception {
        return identityService.getUserInfo(req.appKey(), req.userAccessToken());
    }
}
