import domain.*;
import validation.MeasurementValidator;
import validation.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

public class MeasurementService {
        private final TreeMap<Long, Measurement> storage = new TreeMap<>();
        private final SampleService sampleService; // Ссылка для проверки образца
        private static final String DEFAULT_USER = "SYSTEM";

        public MeasurementService(SampleService sampleService) {
            this.sampleService = sampleService;
        }

        public Measurement add(long sampleId, MeasurementParam param, double value, String unit, String method) {
            Sample sample = sampleService.getById(sampleId);
            if (sample == null) throw new NoSuchElementException("Образец с id=" + sampleId + " не найден");
            if (sample.getStatus() == SampleStatus.ARCHIVED) {
                throw new ValidationException("Нельзя добавлять измерения к ARCHIVED образцу");
            }

            long id = IdGenerator.nextMeasurementId();
            Measurement measurement = new Measurement(id, sampleId, param, value, unit, method, DEFAULT_USER);
            MeasurementValidator.validate(measurement);
            storage.put(id, measurement);
            return measurement;
        }

        public List<Measurement> listBySample(long sampleId) {
            return storage.values().stream()
                    .filter(m -> m.getSampleId() == sampleId)
                    .collect(Collectors.toList());
        }

        // Для stats
        public Map<String, Double> getStats(long sampleId, MeasurementParam param) {
            List<Measurement> list = storage.values().stream()
                    .filter(m -> m.getSampleId() == sampleId && m.getParam() == param)
                    .collect(Collectors.toList());

            if (list.isEmpty()) throw new NoSuchElementException("Нет измерений " + param + " для sample=" + sampleId);

            double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (Measurement m : list) {
                double v = m.getValue();
                sum += v;
                if (v < min) min = v;
                if (v > max) max = v;
            }
            Map<String, Double> stats = new HashMap<>();
            stats.put("count", (double) list.size());
            stats.put("min", min);
            stats.put("max", max);
            stats.put("avg", sum / list.size());
            return stats;
        }
    }

