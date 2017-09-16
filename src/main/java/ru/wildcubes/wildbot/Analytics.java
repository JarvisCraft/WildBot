package ru.wildcubes.wildbot;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class Analytics {
    @Getter private static long timeMark;

    public static void updateStartTime() {
        timeMark = System.currentTimeMillis();
    }

    public static long getUptime() {
        return System.currentTimeMillis() - timeMark;
    }

    @NonNull public static String getUptimeFormatted() {
        return DurationFormatUtils.formatDurationHMS(getUptime());
    }
}
