package com.larksuite.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.service.calendar.v4.model.CreateCalendarEventReq;
import com.lark.oapi.service.calendar.v4.model.CreateCalendarEventResp;
import com.lark.oapi.service.calendar.v4.model.CalendarEvent;
import com.lark.oapi.service.calendar.v4.model.GetCalendarEventReq;
import com.lark.oapi.service.calendar.v4.model.GetCalendarEventResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.LarkApiExecutor;

/** 日历 v4：事件查询与创建。 */
public class LarkCalendarService {

    private final OapiClientRegistry registry;
    private final LarkApiExecutor executor;

    public LarkCalendarService(OapiClientRegistry registry, LarkApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    /** 查询日历事件。 */
    public GetCalendarEventResp getEvent(String appKey, String calendarId, String eventId) throws Exception {
        Client client = resolveClient(appKey);
        GetCalendarEventReq req = GetCalendarEventReq.newBuilder()
                .calendarId(calendarId)
                .eventId(eventId)
                .build();
        return executor.execute(() -> client.calendar().v4().calendarEvent().get(req));
    }

    /** 在指定日历下创建事件。 */
    public CreateCalendarEventResp createEvent(String appKey, String calendarId, CalendarEvent body) throws Exception {
        Client client = resolveClient(appKey);
        CreateCalendarEventReq req = CreateCalendarEventReq.newBuilder()
                .calendarId(calendarId)
                .calendarEvent(body)
                .build();
        return executor.execute(() -> client.calendar().v4().calendarEvent().create(req));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
