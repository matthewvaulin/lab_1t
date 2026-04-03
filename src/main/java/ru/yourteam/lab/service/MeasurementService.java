package ru.yourteam.lab.service;

import ru.yourteam.lab.domain.Measurement;
import ru.yourteam.lab.domain.MeasurementParam;
import ru.yourteam.lab.domain.Sample;
import ru.yourteam.lab.domain.SampleStatus;
import ru.yourteam.lab.validation.MeasurementValidator;
import ru.yourteam.lab.validation.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public final class MeasurementService {
    private static final String DEFAULT_USER = "SYSTEM";
    private final TreeMap<Long, Measurement> storage = new TreeMap<>();
    private final SampleService sampleService;

    public MeasurementService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    public Measurement add(long sampleId, MeasurementParam param, double value, String unit, String method) {
        Sample sample = sampleService.requireById(sampleId);
        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            throw new ValidationException("Ошибка: нельзя добавлять измерения к ARCHIVED образцу");
        }
        MeasurementValidator.validate(value, unit, method);
        long id = IdGenerator.nextMeasurementId();
        Measurement measurement = new Measurement(id, sampleId, param, value, unit, method, Instant.now(), DEFAULT_USER);
        storage.put(id, measurement);
        return measurement;
    }

    public List<Measurement> list(long sampleId, MeasurementParam param, Integer last) {
        sampleService.requireById(sampleId);
        List<Measurement> result = allForSample(sampleId);
        if (param != null) {
            result.removeIf(measurement -> measurement.getParam() != param);
        }
        result.sort(Comparator.comparing(Measurement::getMeasuredAt).reversed().thenComparing(Measurement::getId).reversed());
        if (last != null && last < result.size()) {
            return new ArrayList<>(result.subList(0, last));
        }
        return result;
    }

    public Stats stats(long sampleId, MeasurementParam param) {
        sampleService.requireById(sampleId);
        List<Measurement> values = allForSample(sampleId).stream()
                .filter(measurement -> measurement.getParam() == param)
                .toList();
        if (values.isEmpty()) {
            throw new NoSuchElementException("Ошибка: нет измерений " + param + " для sample=" + sampleId);
        }

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double sum = 0.0;
        for (Measurement measurement : values) {
            double value = measurement.getValue();
            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
        }
        return new Stats(values.size(), min, max, sum / values.size());
    }

    public int countBySample(long sampleId) {
        return allForSample(sampleId).size();
    }

    public List<MeasurementParam> distinctParams(long sampleId) {
        return allForSample(sampleId).stream()
                .map(Measurement::getParam)
                .distinct()
                .toList();
    }

    public List<Measurement> allForSample(long sampleId) {
        List<Measurement> result = new ArrayList<>();
        for (Measurement measurement : storage.values()) {
            if (measurement.getSampleId() == sampleId) {
                result.add(measurement);
            }
        }
        return result;
    }

    public static final class Stats {
        private final long count;
        private final double min;
        private final double max;
        private final double avg;

        public Stats(long count, double min, double max, double avg) {
            this.count = count;
            this.min = min;
            this.max = max;
            this.avg = avg;
        }

        public long getCount() {
            return count;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getAvg() {
            return avg;
        }
    }
}
