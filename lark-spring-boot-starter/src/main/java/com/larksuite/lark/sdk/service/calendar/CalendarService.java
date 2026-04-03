package com.larksuite.lark.sdk.service.calendar;

import com.lark.oapi.Client;
import com.lark.oapi.service.calendar.v4.model.CalendarEvent;
import com.lark.oapi.service.calendar.v4.model.CreateCalendarEventReq;
import com.lark.oapi.service.calendar.v4.model.CreateCalendarEventResp;
import com.lark.oapi.service.calendar.v4.model.GetCalendarEventReq;
import com.lark.oapi.service.calendar.v4.model.GetCalendarEventResp;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.common.support.ApiExecutor;

/** 日历 v4：事件查询与创建；返回完整 SDK Resp。 */
public class CalendarService {

    private final ClientRegistry registry;
    private final ApiExecutor executor;

    public CalendarService(ClientRegistry registry, ApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public GetCalendarEventResp getEvent(String appKey, String calendarId, String eventId) throws Exception {
        Client client = resolveClient(appKey);
        GetCalendarEventReq req = GetCalendarEventReq.newBuilder()
                .calendarId(calendarId)
                .eventId(eventId)
                .build();
        return executor.execute("calendar.v4.event.get", appKey, "calendarId=" + calendarId + ",eventId=" + eventId,
                () -> client.calendar().v4().calendarEvent().get(req));
    }

    public CreateCalendarEventResp createEvent(String appKey, String calendarId, CalendarEvent body) throws Exception {
        Client client = resolveClient(appKey);
        CreateCalendarEventReq req = CreateCalendarEventReq.newBuilder()
                .calendarId(calendarId)
                .calendarEvent(body)
                .build();
        return executor.execute("calendar.v4.event.create", appKey, "calendarId=" + calendarId,
                () -> client.calendar().v4().calendarEvent().create(req));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
