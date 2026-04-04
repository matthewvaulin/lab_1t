package ru.yourteam.lab.Main.validation;

import ru.yourteam.lab.Main.domain.MeasurementParam;

import java.util.Set;

public final class ProtocolValidator {
    private static final int MAX_NAME_LENGTH = 128;

    private ProtocolValidator() {
    }

    public static void validate(String name, Set<MeasurementParam> requiredParams) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Ошибка: имя протокола не может быть пустым");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Ошибка: имя протокола слишком длинное (макс. 128)");
        }
        if (requiredParams == null || requiredParams.isEmpty()) {
            throw new ValidationException("Ошибка: нужно указать хотя бы один параметр");
        }
    }
}
