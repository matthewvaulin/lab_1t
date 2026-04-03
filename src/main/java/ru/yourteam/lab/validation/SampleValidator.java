package ru.yourteam.lab.validation;

public final class SampleValidator {
    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_TYPE_LENGTH = 64;
    private static final int MAX_LOCATION_LENGTH = 64;

    private SampleValidator() {
    }

    public static void validateForCreate(String name, String type, String location) {
        validateName(name);
        validateType(type);
        validateLocation(location);
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Ошибка: название не может быть пустым");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Ошибка: название слишком длинное (макс. 128)");
        }
    }

    public static void validateType(String type) {
        if (type == null || type.isBlank()) {
            throw new ValidationException("Ошибка: type не может быть пустым");
        }
        if (type.length() > MAX_TYPE_LENGTH) {
            throw new ValidationException("Ошибка: type слишком длинный (макс. 64)");
        }
    }

    public static void validateLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new ValidationException("Ошибка: location не может быть пустым");
        }
        if (location.length() > MAX_LOCATION_LENGTH) {
            throw new ValidationException("Ошибка: location слишком длинный (макс. 64)");
        }
    }
}
