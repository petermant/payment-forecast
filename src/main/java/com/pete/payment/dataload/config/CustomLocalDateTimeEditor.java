package com.pete.payment.dataload.config;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A cut-down equivalent of org.springframework.beans.propertyeditors.CustomDateEditor but for LocalDateTime
 */
public class CustomLocalDateTimeEditor extends PropertyEditorSupport {

    private final DateTimeFormatter dateTimeFormat;

    public CustomLocalDateTimeEditor(DateTimeFormatter dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    @Override
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        if (!StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        }
        else {
            try {
                setValue(LocalDateTime.parse(text, dateTimeFormat));
            }
            catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String getAsText() {
        LocalDateTime value = (LocalDateTime) getValue();
        return (value != null ? this.dateTimeFormat.format(value) : "");
    }
}
