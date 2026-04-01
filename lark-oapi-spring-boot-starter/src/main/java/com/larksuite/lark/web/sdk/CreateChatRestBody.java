package com.larksuite.lark.web.sdk;

import com.lark.oapi.service.im.v1.model.CreateChatReqBody;

/**
 * HTTP 建群 body：在 SDK {@link CreateChatReqBody} 上增加可选 {@code userIdType}，与飞书 body 字段同级；
 * 调用 OpenAPI 前会拷贝为纯 {@link CreateChatReqBody}（{@code userIdType} 只走查询参数侧，不写入飞书 body JSON）。
 */
public class CreateChatRestBody extends CreateChatReqBody {

    private String userIdType;

    public String getUserIdType() {
        return userIdType;
    }

    public void setUserIdType(String userIdType) {
        this.userIdType = userIdType;
    }
}
