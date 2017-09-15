package ru.wildcubes.wildbot;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class Analytics {
    @Getter private static long startNanoTime;

    public static void updateStartTime() {
        startNanoTime = System.currentTimeMillis();
    }

    public static final long getUptime() {
        return System.currentTimeMillis() - startNanoTime;
    }

    @NonNull public static String getUptimeFormatted() {
        return DurationFormatUtils.formatDurationHMS(getUptime());
    }
}
