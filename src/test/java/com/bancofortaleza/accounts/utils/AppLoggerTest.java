package com.bancofortaleza.accounts.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AppLoggerTest {

    @AfterEach
    void tearDown() {
        AppLogger.clearContext();
    }

    @Test
    void putContextShouldStoreOnlyNonBlankValues() {
        AppLogger.putContext("valid", "value");
        AppLogger.putContext("blank", " ");
        AppLogger.putContext("null", null);

        assertThat(AppLogger.getContext("valid")).isEqualTo("value");
        assertThat(AppLogger.getContext("blank")).isNull();
        assertThat(AppLogger.getContext("null")).isNull();
    }

    @Test
    void putContextShouldStoreMapValuesAndRemoveContextShouldDeleteOneKey() {
        AppLogger.putContext(Map.of("one", "1", "two", "2"));

        AppLogger.removeContext("one");

        assertThat(AppLogger.getContext("one")).isNull();
        assertThat(AppLogger.getContext("two")).isEqualTo("2");
    }

    @Test
    void clearContextShouldRemoveEveryValue() {
        AppLogger.putContext("key", "value");

        AppLogger.clearContext();

        assertThat(AppLogger.getContext("key")).isNull();
    }

    @Test
    void logMethodsShouldNotThrow() {
        AppLogger.trace(AppLoggerTest.class, "trace {}", "ok");
        AppLogger.debug(AppLoggerTest.class, "debug {}", "ok");
        AppLogger.info(AppLoggerTest.class, "info {}", "ok");
        AppLogger.warn(AppLoggerTest.class, "warn {}", "ok");
        AppLogger.warn(AppLoggerTest.class, "warn", new RuntimeException("test"));
        AppLogger.error(AppLoggerTest.class, "error {}", "ok");
        AppLogger.error(AppLoggerTest.class, "error", new RuntimeException("test"));
        AppLogger.requestStarted(AppLoggerTest.class, "GET", "/accounts");
        AppLogger.requestCompleted(AppLoggerTest.class, "GET", "/accounts", 200, 15);
        AppLogger.requestFailed(AppLoggerTest.class, "GET", "/accounts", 15, new RuntimeException("test"));
    }
}
