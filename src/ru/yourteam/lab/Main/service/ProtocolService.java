package ru.yourteam.lab.Main.service;

import ru.yourteam.lab.Main.domain.MeasurementParam;
import ru.yourteam.lab.Main.domain.Protocol;
import ru.yourteam.lab.Main.validation.ProtocolValidator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

public final class ProtocolService {
    private static final String DEFAULT_USER = "SYSTEM";
    private final TreeMap<Long, Protocol> storage = new TreeMap<>();

    public Protocol create(String name, Set<MeasurementParam> requiredParams) {
        ProtocolValidator.validate(name, requiredParams);
        long id = IdGenerator.nextProtocolId();
        Protocol protocol = new Protocol(id, name, requiredParams, DEFAULT_USER);
        storage.put(id, protocol);
        return protocol;
    }

    public Protocol requireById(long id) {
        Protocol protocol = storage.get(id);
        if (protocol == null) {
            throw new NoSuchElementException("Ошибка: протокол с id=" + id + " не найден");
        }
        return protocol;
    }

    public List<MeasurementParam> apply(long protocolId, long sampleId, SampleService sampleService, MeasurementService measurementService) {
        Protocol protocol = requireById(protocolId);
        sampleService.requireById(sampleId);

        LinkedHashSet<MeasurementParam> found = new LinkedHashSet<>();
        measurementService.allForSample(sampleId)
                .forEach(measurement -> found.add(measurement.getParam()));

        List<MeasurementParam> missing = new ArrayList<>();
        for (MeasurementParam param : protocol.getRequiredParams()) {
            if (!found.contains(param)) {
                missing.add(param);
            }
        }
        return missing;
    }
}
