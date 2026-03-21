package com.larksuite.lark.web;

import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.service.LarkIdentityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 用户身份：需 user_access_token。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/identity", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityController {

    private final LarkIdentityService identityService;

    public IdentityController(LarkIdentityService identityService) {
        this.identityService = identityService;
    }

    public record UserInfoReq(String appKey, @NotBlank String userAccessToken) {}

    /** 使用用户 access_token 获取用户资料。 */
    @PostMapping(path = "/user-info", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse userInfo(@Valid @RequestBody UserInfoReq req) {
        try {
            var resp = identityService.getUserInfo(req.appKey(), req.userAccessToken());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
