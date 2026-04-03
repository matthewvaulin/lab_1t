import domain.Measurement;

public class MeasurementValidator {
    private static final int MAX_UNIT_LENGTH = 16;
    private static final int MAX_METHOD_LENGTH = 64;

    public static void validate(Measurement measurement) {
        if (Double.isNaN(measurement.getValue()) || Double.isInfinite(measurement.getValue())) {
            throw new ValidationException("Значение должно быть корректным числом");
        }
        if (measurement.getUnit() == null || measurement.getUnit().isBlank()) {
            throw new ValidationException("Единицы измерения не могут быть пустыми");
        }
        if (measurement.getMethod() == null || measurement.getMethod().isBlank()) {
            throw new ValidationException("Метод измерения не может быть пустым");
        }
        if (measurement.getOwnerUsername() == null || measurement.getOwnerUsername().isBlank()) {
            throw new ValidationException("Владелец не может быть пустым");
        }
    }
}
