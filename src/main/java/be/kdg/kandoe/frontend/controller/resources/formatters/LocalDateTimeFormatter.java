package be.kdg.kandoe.frontend.controller.resources.formatters;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatter to convert from and to {@link LocalDateTime} for Jackson JSON Mapping
 */

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
    private String pattern;
    
    public LocalDateTimeFormatter(String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        if (text == null || text.isEmpty())
            return null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.pattern, locale);
        
        return LocalDateTime.parse(text, formatter);
    }

    @Override
    public String print(LocalDateTime localDateTime, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.pattern, locale);
        return localDateTime.format(formatter);
    }
}
