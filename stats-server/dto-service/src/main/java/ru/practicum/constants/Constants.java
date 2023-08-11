package ru.practicum.constants;

import java.time.format.DateTimeFormatter;

public final class Constants {
    private Constants() {
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
}
