package com.larksuite.lark.web;

import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.service.LarkCalendarService;
import com.lark.oapi.service.calendar.v4.model.CalendarEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 日历：事件查询与创建。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
public class CalendarController {

    private final LarkCalendarService calendarService;

    public CalendarController(LarkCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    public record CreateEventReq(
            String appKey,
            @NotBlank String calendarId,
            @Valid CalendarEvent body
    ) {}

    /** 查询日历事件。 */
    @GetMapping("/events/{calendarId}/{eventId}")
    public ApiResponse getEvent(
            @PathVariable String calendarId,
            @PathVariable String eventId,
            @RequestParam(required = false) String appKey
    ) {
        try {
            var resp = calendarService.getEvent(appKey, calendarId, eventId);
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 在指定日历下创建事件。 */
    @PostMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse createEvent(@Valid @RequestBody CreateEventReq req) {
        if (req.body() == null) {
            return ApiResponse.failure("INVALID_ARGUMENT", "body is required");
        }
        try {
            var resp = calendarService.createEvent(req.appKey(), req.calendarId(), req.body());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
