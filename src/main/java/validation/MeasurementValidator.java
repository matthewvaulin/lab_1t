package main.java.validation;

public final class MeasurementValidator {
    private static final int MAX_UNIT_LENGTH = 16;
    private static final int MAX_METHOD_LENGTH = 64;

    private MeasurementValidator() {
    }

    public static void validate(double value, String unit, String method) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new ValidationException("Ошибка: значение должно быть числом");
        }
        if (unit == null || unit.isBlank() || method == null || method.isBlank()) {
            throw new ValidationException("Ошибка: единицы и метод не могут быть пустыми");
        }
        if (unit.length() > MAX_UNIT_LENGTH) {
            throw new ValidationException("Ошибка: единицы слишком длинные (макс. 16)");
        }
        if (method.length() > MAX_METHOD_LENGTH) {
            throw new ValidationException("Ошибка: метод слишком длинный (макс. 64)");
        }
    }
}
