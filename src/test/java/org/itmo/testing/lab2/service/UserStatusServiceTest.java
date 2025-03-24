package org.itmo.testing.lab2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.itmo.testing.lab2.service.UserAnalyticsService.Session;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserStatusServiceTest {

    private UserAnalyticsService userAnalyticsService;
    private UserStatusService userStatusService;

    @BeforeEach
    void setUp() {
        userAnalyticsService = mock(UserAnalyticsService.class);
        userStatusService = new UserStatusService(userAnalyticsService);
    }

    @Test
    public void testGetUserStatus_Active() {
        // Настроим поведение mock-объекта
        when(userAnalyticsService.getTotalActivityTime("user123")).thenReturn(60L);
        String status = userStatusService.getUserStatus("user123");
        assertEquals("Active", status);

        when(userAnalyticsService.getTotalActivityTime("user123")).thenReturn(119L);
        status = userStatusService.getUserStatus("user123");
        assertEquals("Active", status);

        verify(userAnalyticsService, times(2)).getTotalActivityTime("user123");
    }

    @Test
    public void testGetUserStatus_Inactive() {
        when(userAnalyticsService.getTotalActivityTime("user123")).thenReturn(59L);
        String status = userStatusService.getUserStatus("user123");
        verify(userAnalyticsService).getTotalActivityTime("user123");
        assertEquals("Inactive", status);
    }

    @Test
    public void testGetUserStatus_HighlyActive() {
        when(userAnalyticsService.getTotalActivityTime("user123")).thenReturn(120L);
        String status = userStatusService.getUserStatus("user123");
        verify(userAnalyticsService).getTotalActivityTime("user123");
        assertEquals("Highly active", status);
    }

    @Test
    public void testGetUserStatus_NoSessions() {
        when(userAnalyticsService.getTotalActivityTime("user123"))
                .thenThrow(new IllegalArgumentException("No sessions found for user"));
        final var e = assertThrows(IllegalArgumentException.class, () -> userStatusService.getUserStatus("user123"));
        assertEquals("No sessions found for user", e.getMessage());
    }


//    @Test
//    public void testGetUserLastSessionDate_OneSession() {
//        final var now = LocalDateTime.now();
//
//        final var session = new Session(now.minusHours(1), now);
//
//        when(userAnalyticsService.getUserSessions("user123"))
//            .thenReturn(List.of(session));
//
//        final var s = userStatusService.getUserLastSessionDate("user123");
//        verify(userAnalyticsService).getUserSessions("user123");
//
//        assertTrue(s.isPresent());
//        assertEquals(session.getLoginTime().toLocalDate().toString(), s.get());
//        assertEquals(session.getLogoutTime().toLocalDate().toString(), s.get());
//    }

//    @Test
//    public void testGetUserLastSessionDate_TwoSessionsInOrder() {
//        final var now = LocalDateTime.now();
//
//        final var session1 = new Session(now.minusHours(3), now.minusHours(2));
//        final var session2 = new Session(now.minusHours(1), now);
//
//        when(userAnalyticsService.getUserSessions("user123"))
//            .thenReturn(List.of(session1, session2));
//
//        final var s = userStatusService.getUserLastSessionDate("user123");
//        verify(userAnalyticsService).getUserSessions("user123");
//
//        assertTrue(s.isPresent());
//        assertEquals(session2.getLoginTime().toLocalDate().toString(), s.get());
//        assertEquals(session2.getLogoutTime().toLocalDate().toString(), s.get());
//    }

//    @Test
//    public void testGetUserLastSessionDate_TwoSessionsOutOfOrder() {
//        final var now = LocalDateTime.now();
//
//        final var session1 = new Session(now.minusHours(3), now.minusHours(2));
//        final var session2 = new Session(now.minusHours(1), now);
//
//        when(userAnalyticsService.getUserSessions("user123"))
//            .thenReturn(List.of(session2, session1));
//
//        final var s = userStatusService.getUserLastSessionDate("user123");
//        verify(userAnalyticsService).getUserSessions("user123");
//
//        assertTrue(s.isPresent());
//        assertEquals(session2.getLoginTime().toLocalDate().toString(), s.get());
//        assertEquals(session2.getLogoutTime().toLocalDate().toString(), s.get());
//    }

//    @Test
//    public void testGetUserLastSessionDate_NoUser() {
//        when(userAnalyticsService.getUserSessions("user123")).thenReturn(null);
//        final var s = userStatusService.getUserLastSessionDate("user123");
//        verify(userAnalyticsService).getUserSessions("user123");
//        assertTrue(s.isEmpty());
//    }

//    @Test
//    public void testGetUserLastSessionDate_NoSessions() {
//        when(userAnalyticsService.getUserSessions("user123")).thenReturn(List.of());
//        final var s = userStatusService.getUserLastSessionDate("user123");
//        verify(userAnalyticsService).getUserSessions("user123");
//        assertTrue(s.isEmpty());
//    }
}
