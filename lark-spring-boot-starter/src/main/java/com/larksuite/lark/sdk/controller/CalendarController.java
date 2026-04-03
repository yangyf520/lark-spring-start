package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.calendar.v4.model.CalendarEvent;
import com.lark.oapi.service.calendar.v4.model.CreateCalendarEventResp;
import com.lark.oapi.service.calendar.v4.model.GetCalendarEventResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.calendar.CalendarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日历：事件查询与创建。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * 构造注入。
     * <p>
     * @param calendarService 日历服务
     */
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * 创建日历事件请求体。
     * <p>
     * @param appKey     应用配置键，可空（使用 primary）
     * @param calendarId 日历 ID，必填
     * @param body       事件体，必填
     */
    public record CreateEventReq(
            String appKey,
            @NotBlank String calendarId,
            @Valid CalendarEvent body
    ) {}

    /**
     * 根据 calendarId、eventId 查询事件详情。
     * <p>
     * @param calendarId 日历 ID
     * @param eventId    事件 ID
     * @param appKey     应用配置键，可空（使用 primary）
     * @return 飞书 SDK {@link GetCalendarEventResp}
     */
    @GetMapping("/events/{calendarId}/{eventId}")
    public GetCalendarEventResp getEvent(
            @PathVariable String calendarId,
            @PathVariable String eventId,
            @RequestParam(required = false) String appKey
    ) throws Exception {
        return calendarService.getEvent(appKey, calendarId, eventId);
    }

    /**
     * 在指定日历下创建事件。
     * <p>
     * @param req 请求体，{@code body} 必填
     * @return 飞书 SDK {@link CreateCalendarEventResp}
     */
    @PostMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateCalendarEventResp createEvent(@Valid @RequestBody CreateEventReq req) throws Exception {
        if (req.body() == null) {
            throw new IllegalArgumentException("body is required");
        }
        return calendarService.createEvent(req.appKey(), req.calendarId(), req.body());
    }
}
