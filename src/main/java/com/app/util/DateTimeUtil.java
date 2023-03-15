package com.app.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeUtil {

    public static LocalDateTime getDateTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time);
    }

    public static long compare(LocalDate dateDeparture, LocalTime timeDeparture, LocalDate dateArrival, LocalTime timeArrival)
    {
        return Duration.between(getDateTime(dateDeparture, timeDeparture),
                getDateTime(dateArrival, timeArrival)).toMinutes();
    }

    public static long getHoursFromMinutes(long minutes) {
        return minutes/60;
    }

    public static long getOutputMinutesFromHours(long minutes) {
        return minutes % 60;
    }


}
