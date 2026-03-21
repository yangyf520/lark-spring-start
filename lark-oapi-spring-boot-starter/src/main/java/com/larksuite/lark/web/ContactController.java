package com.larksuite.lark.web;

import com.lark.oapi.service.contact.v3.model.BatchGetIdUserResp;
import com.lark.oapi.service.contact.v3.model.GetUserResp;
import com.lark.oapi.service.contact.v3.model.ListDepartmentResp;
import com.larksuite.lark.core.advice.LarkApi;
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
@LarkApi
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

    /** 查询用户详情：按 userId 查询用户信息。 */
    @GetMapping("/users/{userId}")
    public GetUserResp getUser(
            @PathVariable String userId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType
    ) throws Exception {
        return contactService.getUser(appKey, userId, userIdType, departmentIdType);
    }

    /** 分页查询部门列表：按父部门分页列出子部门。 */
    @GetMapping("/departments")
    public ListDepartmentResp listDepartments(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "0") String parentDepartmentId,
            @RequestParam(required = false, defaultValue = "false") Boolean fetchChild,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String pageToken,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType
    ) throws Exception {
        return contactService.listDepartments(
                appKey,
                parentDepartmentId,
                fetchChild,
                pageSize,
                pageToken,
                userIdType,
                departmentIdType
        );
    }

    /** 批量查询用户 ID：通过邮箱或手机号批量查询用户 ID 信息。 */
    @PostMapping(path = "/users/batch-get-id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BatchGetIdUserResp batchGetId(@Valid @RequestBody BatchGetIdReq req) throws Exception {
        return contactService.batchGetId(req.appKey(), req.userIdType(), req.emails(), req.mobiles(), req.includeResigned());
    }
}
