package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.contact.v3.model.BatchGetIdUserResp;
import com.lark.oapi.service.contact.v3.model.CreateDepartmentResp;
import com.lark.oapi.service.contact.v3.model.CreateUserResp;
import com.lark.oapi.service.contact.v3.model.DeleteDepartmentResp;
import com.lark.oapi.service.contact.v3.model.DeleteUserResp;
import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.GetDepartmentResp;
import com.lark.oapi.service.contact.v3.model.GetUserResp;
import com.lark.oapi.service.contact.v3.model.ListDepartmentResp;
import com.lark.oapi.service.contact.v3.model.ListUserResp;
import com.lark.oapi.service.contact.v3.model.UpdateDepartmentResp;
import com.lark.oapi.service.contact.v3.model.UpdateUserResp;
import com.lark.oapi.service.contact.v3.model.User;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.contact.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 通讯录：用户 / 部门 / 批量换 ID。 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/contact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
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

    /** 分页查询部门下用户列表。 */
    @GetMapping("/users")
    public ListUserResp listUsers(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "0") String departmentId,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String pageToken,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType
    ) throws Exception {
        return contactService.listUsers(appKey, departmentId, pageSize, pageToken, userIdType, departmentIdType);
    }

    /** 创建用户。 */
    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResp createUser(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType,
            @RequestParam(required = false) String clientToken,
            @RequestBody User user
    ) throws Exception {
        return contactService.createUser(appKey, userIdType, departmentIdType, clientToken, user);
    }

    /** 更新用户。 */
    @PutMapping(path = "/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateUserResp updateUser(
            @PathVariable String userId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType,
            @RequestBody User user
    ) throws Exception {
        return contactService.updateUser(appKey, userId, userIdType, departmentIdType, user);
    }

    /** 删除用户。 */
    @DeleteMapping("/users/{userId}")
    public DeleteUserResp deleteUser(
            @PathVariable String userId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType
    ) throws Exception {
        return contactService.deleteUser(appKey, userId, userIdType);
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

    /** 查询部门详情。 */
    @GetMapping("/departments/{departmentId}")
    public GetDepartmentResp getDepartment(
            @PathVariable String departmentId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType
    ) throws Exception {
        return contactService.getDepartment(appKey, departmentId, userIdType, departmentIdType);
    }

    /** 创建部门。 */
    @PostMapping(path = "/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateDepartmentResp createDepartment(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType,
            @RequestParam(required = false) String clientToken,
            @RequestBody Department department
    ) throws Exception {
        return contactService.createDepartment(appKey, userIdType, departmentIdType, clientToken, department);
    }

    /** 更新部门。 */
    @PutMapping(path = "/departments/{departmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateDepartmentResp updateDepartment(
            @PathVariable String departmentId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_id") String userIdType,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType,
            @RequestBody Department department
    ) throws Exception {
        return contactService.updateDepartment(appKey, departmentId, userIdType, departmentIdType, department);
    }

    /** 删除部门。 */
    @DeleteMapping("/departments/{departmentId}")
    public DeleteDepartmentResp deleteDepartment(
            @PathVariable String departmentId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false, defaultValue = "open_department_id") String departmentIdType
    ) throws Exception {
        return contactService.deleteDepartment(appKey, departmentId, departmentIdType);
    }

    /** 批量查询用户 ID：通过邮箱或手机号批量查询用户 ID 信息。 */
    @PostMapping(path = "/users/batch-get-id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BatchGetIdUserResp batchGetId(@Valid @RequestBody BatchGetIdReq req) throws Exception {
        return contactService.batchGetId(req.appKey(), req.userIdType(), req.emails(), req.mobiles(), req.includeResigned());
    }
}
