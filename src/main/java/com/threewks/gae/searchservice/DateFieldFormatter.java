package com.threewks.gae.searchservice;

import java.time.Instant;
import java.util.Date;

public class DateFieldFormatter {


    public static Date getDate(Object date) {
        try {
            return Date.from(Instant.parse((String)date));

        } catch (Exception e) {
            throw new RuntimeException("invalid date format");
        }
    }

    public static boolean isDate(Object date) {
        boolean valid = true;
        try {
            Date.from(Instant.parse((String)date));
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

}
