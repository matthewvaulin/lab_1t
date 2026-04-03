import domain.Sample;
import domain.SampleStatus;
import validation.SampleValidator;
import validation.ValidationException;

import java.util.*;

public class SampleService {
        // Вариант 4: TreeMap (сортировка по ключу ID)
        private final TreeMap<Long, Sample> storage = new TreeMap<>();
        private static final String DEFAULT_USER = "SYSTEM";

        public Sample add(String name, String type, String location) {
            long id = IdGenerator.nextSampleId();
            Sample sample = new Sample(id, name, type, location, DEFAULT_USER);
            SampleValidator.validate(sample);
            storage.put(id, sample);
            return sample;
        }

        public Sample getById(long id) {
            return storage.get(id);
        }

        public List<Sample> listAll() {
            return new ArrayList<>(storage.values());
        }

        public List<Sample> listByStatus(SampleStatus status) {
            List<Sample> result = new ArrayList<>();
            for (Sample s : storage.values()) {
                if (s.getStatus() == status) result.add(s);
            }
            return result;
        }

        public void update(long id, String field, String value) {
            Sample sample = storage.get(id);
            if (sample == null) throw new NoSuchElementException("Образец с id=" + id + " не найден");

            // Простая логика обновления полей
            switch (field) {
                case "name": sample.setName(value); break;
                case "type": sample.setType(value); break;
                case "location": sample.setLocation(value); break;
                case "status":
                    try {
                        sample.setStatus(SampleStatus.valueOf(value.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException("Статус только ACTIVE или ARCHIVED");
                    }
                    break;
                default: throw new ValidationException("Нельзя менять поле '" + field + "'");
            }
            SampleValidator.validateUpdate(field, value);
        }

        public void archive(long id) {
            Sample sample = storage.get(id);
            if (sample == null) throw new NoSuchElementException("Образец не найден");
            if (sample.getStatus() == SampleStatus.ARCHIVED) {
                throw new ValidationException("Образец уже ARCHIVED");
            }
            sample.setStatus(SampleStatus.ARCHIVED);
        }
    }

