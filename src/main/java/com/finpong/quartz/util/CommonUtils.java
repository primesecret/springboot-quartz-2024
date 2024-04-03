package com.finpong.quartz.util;

import com.finpong.quartz.exception.InvalidDataException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    static final String PAYMENT_SCHEDULE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date convertStringToDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(PAYMENT_SCHEDULE_PATTERN);
        sdf.setLenient(false); // Disallow lenient parsing to ensure strict matching

        try {
            // Attempt to parse the date with the specified pattern
            Date scheduleDateTime = sdf.parse(date);

            if (scheduleDateTime.before(new Date())) {
                throw new InvalidDataException("paymentScheduleAt must be set to a date and time later than the current datetime.");
            }

            return scheduleDateTime;
        } catch (ParseException e) {
            // If parsing fails, it means the date does not match the pattern,
            throw new InvalidDataException("paymentScheduleAt must be in yyyy-MM-dd HH:mm:ss format.");
        }
    }

}
