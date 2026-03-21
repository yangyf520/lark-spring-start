package com.larksuite.lark.service;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;

/** IM 发消息能力（供扩展或测试替身）。 */
public interface LarkImApi {
    /** 发送文本消息。 */
    CreateMessageResp sendText(String appKey, ReceiveIdTypeEnum receiveIdType, String receiveId, String text) throws Exception;
}
