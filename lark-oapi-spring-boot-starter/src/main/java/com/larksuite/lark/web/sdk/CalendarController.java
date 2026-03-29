package com.larksuite.lark.web.sdk;

import com.lark.oapi.service.calendar.v4.model.CalendarEvent;
import com.lark.oapi.service.calendar.v4.model.CreateCalendarEventResp;
import com.lark.oapi.service.calendar.v4.model.GetCalendarEventResp;
import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.service.calendar.CalendarService;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
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

/** 日历：事件查询与创建。 */
@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(path = "/lark/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    public record CreateEventReq(
            String appKey,
            @NotBlank String calendarId,
            @Valid CalendarEvent body
    ) {}

    /** 获取日历事件：根据 calendarId 和 eventId 查询事件详情。 */
    @GetMapping("/events/{calendarId}/{eventId}")
    public GetCalendarEventResp getEvent(
            @PathVariable String calendarId,
            @PathVariable String eventId,
            @RequestParam(required = false) String appKey
    ) throws Exception {
        return calendarService.getEvent(appKey, calendarId, eventId);
    }

    /** 创建日历事件：在指定日历下创建一个新事件。 */
    @PostMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateCalendarEventResp createEvent(@Valid @RequestBody CreateEventReq req) throws Exception {
        if (req.body() == null) {
            throw new IllegalArgumentException("body is required");
        }
        return calendarService.createEvent(req.appKey(), req.calendarId(), req.body());
    }
}
