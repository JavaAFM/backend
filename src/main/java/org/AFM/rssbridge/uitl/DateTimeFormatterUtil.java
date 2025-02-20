package org.AFM.rssbridge.uitl;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;

@Component
public class DateTimeFormatterUtil {
    private final String YESTERDAY = "Вчера";
    private final String TODAY = "Сегодня";
    private final String MINUTE = "минут";
    private final String HOUR = "час";

    public LocalDateTime parseTengriTime(String input) {
        try {
            if (input.equals(TODAY)) {
                return LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
            } else if (input.equals(YESTERDAY)) {
                return LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
            } else if (input.matches("\\d{1,2} \\p{IsCyrillic}+ \\d{4}")) {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("d MMMM yyyy")
                        .toFormatter(new Locale("ru"));
                LocalDate date = LocalDate.parse(input, formatter);
                return date.atStartOfDay();
            }
            if (input.startsWith(YESTERDAY)) {
                String timePart = input.split(",")[1].trim();
                LocalTime time = LocalTime.parse(timePart, DateTimeFormatter.ofPattern("HH:mm"));
                LocalDate yesterday = LocalDate.now().minusDays(1);

                return LocalDateTime.of(yesterday, time);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

                return LocalDateTime.parse(input, formatter);
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Failed to parse date-time string: " + input, e);
        }
    }

    public LocalDateTime parseKazTagTime(String input) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("d ")
                    .appendText(java.time.temporal.ChronoField.MONTH_OF_YEAR,
                            java.util.Map.ofEntries(
                                    Map.entry(1L, "ЯНВАРЯ"),
                                    Map.entry(2L, "ФЕВРАЛЯ"),
                                    Map.entry(3L, "МАРТА"),
                                    Map.entry(4L, "АПРЕЛЯ"),
                                    Map.entry(5L, "МАЯ"),
                                    Map.entry(6L, "ИЮНЯ"),
                                    Map.entry(7L, "ИЮЛЯ"),
                                    Map.entry(8L, "АВГУСТА"),
                                    Map.entry(9L, "СЕНТЯБРЯ"),
                                    Map.entry(10L, "ОКТЯБРЯ"),
                                    Map.entry(11L, "НОЯБРЯ"),
                                    Map.entry(12L, "ДЕКАБРЯ")
                            )
                    )
                    .appendPattern(" yyyy, HH:mm")
                    .toFormatter(new Locale("ru"));
            return LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Failed to parse date-time string: " + input, e);
        }
    }

    public LocalDateTime parseAzattyqTime(String input) {
        try {
            LocalDateTime now = LocalDateTime.now();
            input = input.toLowerCase();
            if (input.matches("\\d{1,2} \\p{IsCyrillic}+ \\d{4}, \\d{2}:\\d{2}")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("ru"));
                return LocalDateTime.parse(input, formatter);
            }
            if (input.contains(HOUR)) {
                int hoursAgo = Integer.parseInt(input.replaceAll("\\D+", ""));
                return now.minusHours(hoursAgo);
            }
            if (input.contains(MINUTE)) {
                int minutesAgo = Integer.parseInt(input.replaceAll("\\D+", ""));
                return now.minusMinutes(minutesAgo);
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));
            LocalDate date = LocalDate.parse(input, dateFormatter);
            return date.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Failed to parse date-time string: " + input, e);
        }
    }


    public LocalDateTime parseOrdaTime(String input) {
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("ru"));

            if (input.startsWith(TODAY)) {
                String timePart = input.replace(TODAY, "").trim();
                LocalTime time = LocalTime.parse(timePart, timeFormatter);
                return LocalDate.now().atTime(time);
            }

            if (input.startsWith(YESTERDAY)) {
                String timePart = input.replace(YESTERDAY, "").trim();
                LocalTime time = LocalTime.parse(timePart, timeFormatter);
                return LocalDate.now().minusDays(1).atTime(time);
            }

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM HH:mm", new Locale("ru"));
            TemporalAccessor parsedDate = dateTimeFormatter.parse(input);
            int day = parsedDate.get(ChronoField.DAY_OF_MONTH);
            Month month = Month.of(parsedDate.get(ChronoField.MONTH_OF_YEAR));
            int hour = parsedDate.get(ChronoField.HOUR_OF_DAY);
            int minute = parsedDate.get(ChronoField.MINUTE_OF_HOUR);

            int currentYear = LocalDate.now().getYear();
            LocalDate date = LocalDate.of(currentYear, month, day);
            LocalTime time = LocalTime.of(hour, minute);

            return LocalDateTime.of(date, time);

        } catch (DateTimeException e) {
            throw new RuntimeException("Failed to parse date-time string: " + input, e);
        }
    }
    public LocalDateTime parseZakonDate(String input) {
        try {
            if (input.contains(TODAY)) {
                String timePart = input.split(",")[0].trim();
                LocalTime time = LocalTime.parse(timePart, DateTimeFormatter.ofPattern("HH:mm"));

                LocalDate today = LocalDate.now();
                return LocalDateTime.of(today, time);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, d MMMM yyyy", Locale.forLanguageTag("ru"));
                return LocalDateTime.parse(input.trim(), formatter);
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Failed to parse date-time string: " + input, e);
        }
    }
}

