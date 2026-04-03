import domain.*;
import validation.ProtocolValidator;
import java.util.*;

    public class ProtocolService {
        private final TreeMap<Long, Protocol> storage = new TreeMap<>();
        private static final String DEFAULT_USER = "SYSTEM";

        public Protocol create(String name, Set<MeasurementParam> params) {
            long id = IdGenerator.nextProtocolId();
            Protocol protocol = new Protocol(id, name, params, DEFAULT_USER);
            ProtocolValidator.validate(protocol);
            storage.put(id, protocol);
            return protocol;
        }

        public Protocol getById(long id) {
            return storage.get(id);
        }

        public void apply(long protocolId, long sampleId, MeasurementService measService) {
            Protocol protocol = storage.get(protocolId);
            if (protocol == null) throw new NoSuchElementException("Протокол не найден");
            // Логика проверки: есть ли у образца измерения всех requiredParams
            // Упрощенно: проверяем наличие хотя бы одного измерения каждого типа
            List<Measurement> measurements = measService.listBySample(sampleId);
            Set<MeasurementParam> foundParams = new HashSet<>();
            for (Measurement m : measurements) {
                foundParams.add(m.getParam());
            }

            Set<MeasurementParam> missing = new HashSet<>(protocol.getRequiredParams());
            missing.removeAll(foundParams);

            if (!missing.isEmpty()) {
                System.out.println("Missing params: " + String.join(", ", missing.toString()));
            } else {
                System.out.println("OK protocol is complete");
            }
        }
    }

