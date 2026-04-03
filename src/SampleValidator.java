import domain.Sample;

public class SampleValidator {
    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_TYPE_LENGTH = 64;
    private static final int MAX_LOCATION_LENGTH = 64;

    public static void validate(Sample sample) {
        if (sample.getName() == null || sample.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (sample.getName().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Название слишком длмнное (макс. " + MAX_NAME_LENGTH + ")");
        }
        if (sample.getType() == null || sample.getType().isBlank()) {
            throw new ValidationException("Тип не может быть пустым");
        }
        if (sample.getType().length() > MAX_TYPE_LENGTH) {
            throw new ValidationException("Тип слишком длинный (макс. " + MAX_TYPE_LENGTH + ")");
        }
        if (sample.getLocation() == null || sample.getLocation().isBlank()) {
            throw new ValidationException("Место хранения не может быть пустым");
        }
        if (sample.getLocation().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Название слишком длмнное (макс. " + MAX_LOCATION_LENGTH + ")");
        }
        if (sample.getOwnerUsername() == null || sample.getOwnerUsername().isBlank()) {
            throw new ValidationException("Имя владельца не может быть пустым");
        }
    }

    public static void validateUpdate (String field, String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Поле " + field + " не может быть пустым");
        }
        if ("name".equals(field) && value.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Название слишком длинное");
        }
    }
}
