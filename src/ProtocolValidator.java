import domain.Protocol;

public class ProtocolValidator {
    public static void validate(Protocol protocol) {
        if (protocol.getName() == null || protocol.getName().isBlank()) {
            throw new ValidationException("Имя протокола не может быть пустым");
        }
        if (protocol.getRequiredParams() == null || protocol.getRequiredParams().isEmpty()) {
            throw new ValidationException("Нужно указать хотя бы один параметр");
        }
        if (protocol.getOwnerUsername() == null || protocol.getOwnerUsername().isBlank()) {
            throw new ValidationException("Владелец не может быть пустым");
        }
    }
}
