package com.larksuite.lark.app.vo.data.user;

import java.util.List;

/** AE 用户批量删除请求体。 */
public record UserDeleteVo(List<String> ids) {
}

