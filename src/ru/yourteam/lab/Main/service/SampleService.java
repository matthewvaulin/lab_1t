package ru.yourteam.lab.Main.service;

import ru.yourteam.lab.Main.domain.Sample;
import ru.yourteam.lab.Main.domain.SampleStatus;
import ru.yourteam.lab.Main.validation.SampleValidator;
import ru.yourteam.lab.Main.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public final class SampleService {
    private static final String DEFAULT_USER = "SYSTEM";
    private final TreeMap<Long, Sample> storage = new TreeMap<>();

    public Sample add(String name, String type, String location) {
        SampleValidator.validateForCreate(name, type, location);
        long id = IdGenerator.nextSampleId();
        Sample sample = new Sample(id, name, type, location, DEFAULT_USER);
        storage.put(id, sample);
        return sample;
    }

    public Sample requireById(long id) {
        Sample sample = storage.get(id);
        if (sample == null) {
            throw new NoSuchElementException("Ошибка: образец с id=" + id + " не найден");
        }
        return sample;
    }

    public List<Sample> listAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Sample> listByStatus(SampleStatus status) {
        List<Sample> result = new ArrayList<>();
        for (Sample sample : storage.values()) {
            if (sample.getStatus() == status) {
                result.add(sample);
            }
        }
        return result;
    }

    public List<Sample> listMine() {
        return listAll();
    }

    public void update(long id, Map<String, String> changes) {
        Sample sample = requireById(id);

        String newName = sample.getName();
        String newType = sample.getType();
        String newLocation = sample.getLocation();
        SampleStatus newStatus = sample.getStatus();

        for (Map.Entry<String, String> entry : changes.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.isBlank()) {
                throw new ValidationException("Ошибка: " + field + " не может быть пустым");
            }
            switch (field) {
                case "name" -> {
                    SampleValidator.validateName(value);
                    newName = value;
                }
                case "type" -> {
                    SampleValidator.validateType(value);
                    newType = value;
                }
                case "location" -> {
                    SampleValidator.validateLocation(value);
                    newLocation = value;
                }
                case "status" -> {
                    try {
                        newStatus = SampleStatus.valueOf(value.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException("Ошибка: статус только ACTIVE или ARCHIVED");
                    }
                }
                default -> throw new ValidationException("Ошибка: нельзя менять поле '" + field + "'");
            }
        }

        sample.setName(newName);
        sample.setType(newType);
        sample.setLocation(newLocation);
        sample.setStatus(newStatus);
    }

    public void archive(long id) {
        Sample sample = requireById(id);
        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            throw new ValidationException("Ошибка: образец уже ARCHIVED");
        }
        sample.setStatus(SampleStatus.ARCHIVED);
    }
}
