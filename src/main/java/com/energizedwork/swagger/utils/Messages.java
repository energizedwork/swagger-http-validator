package com.energizedwork.swagger.utils;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;

public class Messages {

    private static final MessageBundle VALIDATION_MESSAGE_BUNDLE = loadValidationMessageBundle();

    private static MessageBundle loadValidationMessageBundle() {
        try {
            MessageSource messageSource = PropertiesMessageSource.fromResource("/com/github/fge/jsonschema/validator/validation.properties");
            return MessageBundle.withSingleSource(messageSource);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String validationMessage(String key, Object... vars) {
        return VALIDATION_MESSAGE_BUNDLE.printf(key, vars);
    }
}
