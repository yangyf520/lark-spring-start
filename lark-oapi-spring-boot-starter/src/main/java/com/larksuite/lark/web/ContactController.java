package com.larksuite.lark.web;

import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.service.LarkContactService;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 通讯录：用户 / 部门 / 批量换 ID。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/contact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactController {

    private final LarkContactService contactService;

    public ContactController(LarkContactService contactService) {
        this.contactService = contactService;
    }

    public record BatchGetIdReq(
            String appKey,
            String userIdType,
            String[] emails,
            String[] mobiles,
            Boolean includeResigned
    ) {}

    /** 按 userId 查询用户详情（userIdType 默认 open_id）。 */
    @GetMapping("/users/{userId}")
    public ApiResponse getUser(
            @PathVariable String userId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType
    ) {
        try {
            var resp = contactService.getUser(appKey, userId, userIdType, departmentIdType);
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 分页列出子部门。 */
    @GetMapping("/departments")
    public ApiResponse listDepartments(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "0") String parentDepartmentId,
            @RequestParam(required = false, defaultValue = "false") Boolean fetchChild,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String pageToken,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType
    ) {
        try {
            var resp = contactService.listDepartments(
                    appKey,
                    parentDepartmentId,
                    fetchChild,
                    pageSize,
                    pageToken,
                    userIdType,
                    departmentIdType
            );
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 通过邮箱或手机号批量查询 user_id 等。 */
    @PostMapping(path = "/users/batch-get-id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse batchGetId(@Valid @RequestBody BatchGetIdReq req) {
        try {
            var resp = contactService.batchGetId(req.appKey(), req.userIdType(), req.emails(), req.mobiles(), req.includeResigned());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
