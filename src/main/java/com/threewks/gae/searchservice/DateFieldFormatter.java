package com.threewks.gae.searchservice;

import com.threewks.gae.searchservice.converter.InstantToDoubleConverter;

import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

public class DateFieldFormatter {
    private static final Logger LOG = Logger.getLogger(DateFieldFormatter.class.getName());

    public static boolean isDate(Object date) {
        try {
            Instant.parse((String) date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Double formatForSearch(Object date) {
        try {
            Double formatted = new InstantToDoubleConverter().convert(Instant.parse((String) date));
            LOG.info(String.format("Formatting date %s => %s", date, formatted));
            return formatted;
        } catch (Exception e) {
            throw new RuntimeException("invalid date format");
        }
    }
}
