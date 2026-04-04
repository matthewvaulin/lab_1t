package main.java.cli;

import main.java.domain.Measurement;
import main.java.domain.MeasurementParam;
import main.java.domain.Protocol;
import main.java.domain.Sample;
import main.java.domain.SampleStatus;
import main.java.service.MeasurementService;
import main.java.service.ProtocolService;
import main.java.service.SampleService;
import main.java.validation.ValidationException;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class CommandProcessor {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner;
    private final SampleService sampleService = new SampleService();
    private final MeasurementService measurementService = new MeasurementService(sampleService);
    private final ProtocolService protocolService = new ProtocolService();

    public CommandProcessor(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            if (!scanner.hasNextLine()) {
                return;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if ("exit".equalsIgnoreCase(line)) {
                return;
            }
            try {
                dispatch(tokenize(line));
            } catch (ValidationException | NoSuchElementException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void dispatch(List<String> tokens) {
        String command = tokens.get(0);
        switch (command) {
            case "sample_add" -> handleSampleAdd();
            case "sample_list" -> handleSampleList(tokens);
            case "sample_show" -> handleSampleShow(tokens);
            case "sample_update" -> handleSampleUpdate(tokens);
            case "sample_archive" -> handleSampleArchive(tokens);
            case "meas_add" -> handleMeasurementAdd(tokens);
            case "meas_list" -> handleMeasurementList(tokens);
            case "meas_stats" -> handleMeasurementStats(tokens);
            case "prot_create" -> handleProtocolCreate();
            case "prot_apply" -> handleProtocolApply(tokens);
            default -> System.out.println("Ошибка: неизвестная команда");
        }
    }

    private void handleSampleAdd() {
        System.out.print("Название: ");
        String name = scanner.nextLine();
        System.out.print("Тип: ");
        String type = scanner.nextLine();
        System.out.print("Место: ");
        String location = scanner.nextLine();

        Sample sample = sampleService.add(name, type, location);
        System.out.println("OK sample_id=" + sample.getId());
    }

    private void handleSampleList(List<String> tokens) {
        SampleStatus status = null;
        boolean mine = false;
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if ("--status".equals(token)) {
                if (i + 1 >= tokens.size()) {
                    throw new ValidationException("Ошибка: неизвестный статус, используйте ACTIVE или ARCHIVED");
                }
                try {
                    status = SampleStatus.valueOf(tokens.get(++i).toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Ошибка: неизвестный статус, используйте ACTIVE или ARCHIVED");
                }
            } else if ("--mine".equals(token)) {
                mine = true;
            } else {
                throw new ValidationException("Ошибка: неизвестная опция " + token);
            }
        }

        List<Sample> samples;
        if (status != null) {
            samples = sampleService.listByStatus(status);
        } else if (mine) {
            samples = sampleService.listMine();
        } else {
            samples = sampleService.listAll();
        }

        System.out.println("ID Name Type Location Status");
        for (Sample sample : samples) {
            System.out.printf("%d %s %s %s %s%n",
                    sample.getId(), sample.getName(), sample.getType(), sample.getLocation(), sample.getStatus());
        }
    }

    private void handleSampleShow(List<String> tokens) {
        if (tokens.size() != 2) {
            throw new ValidationException("Ошибка: используйте sample_show <id>");
        }
        long id = parseLong(tokens.get(1), "Ошибка: id должно быть числом");
        Sample sample = sampleService.requireById(id);
        int count = measurementService.countBySample(id);
        List<MeasurementParam> params = measurementService.distinctParams(id);

        System.out.println("Sample #" + sample.getId());
        System.out.println("name: " + sample.getName());
        System.out.println("type: " + sample.getType());
        System.out.println("location: " + sample.getLocation());
        System.out.println("status: " + sample.getStatus());
        System.out.println("owner: " + sample.getOwnerUsername());
        System.out.println("measurements: " + count);
        System.out.println("params: " + (params.isEmpty() ? "-" : joinParams(params)));
    }

    private void handleSampleUpdate(List<String> tokens) {
        if (tokens.size() < 3) {
            throw new ValidationException("Ошибка: используйте sample_update <id> field=value ...");
        }
        long id = parseLong(tokens.get(1), "Ошибка: id должно быть числом");
        Map<String, String> changes = new LinkedHashMap<>();
        for (int i = 2; i < tokens.size(); i++) {
            String token = tokens.get(i);
            int eq = token.indexOf('=');
            if (eq <= 0) {
                throw new ValidationException("Ошибка: ожидается field=value");
            }
            String field = token.substring(0, eq);
            String value = token.substring(eq + 1);
            changes.put(field, value);
        }
        sampleService.update(id, changes);
        System.out.println("OK");
    }

    private void handleSampleArchive(List<String> tokens) {
        if (tokens.size() != 2) {
            throw new ValidationException("Ошибка: используйте sample_archive <id>");
        }
        long id = parseLong(tokens.get(1), "Ошибка: id должно быть числом");
        sampleService.archive(id);
        System.out.println("OK sample " + id + " archived");
    }

    private void handleMeasurementAdd(List<String> tokens) {
        if (tokens.size() != 2) {
            throw new ValidationException("Ошибка: используйте meas_add <sample_id>");
        }
        long sampleId = parseLong(tokens.get(1), "Ошибка: sample_id должно быть числом");

        System.out.print("Параметр (PH/CONDUCTIVITY/TURBIDITY/NITRATE): ");
        MeasurementParam param = MeasurementParam.parse(scanner.nextLine());
        System.out.print("Значение: ");
        double value = parseDouble(scanner.nextLine(), "Ошибка: значение должно быть числом");
        System.out.print("Единицы: ");
        String unit = scanner.nextLine();
        System.out.print("Метод: ");
        String method = scanner.nextLine();

        Measurement measurement = measurementService.add(sampleId, param, value, unit, method);
        System.out.println("OK measurement_id=" + measurement.getId());
    }

    private void handleMeasurementList(List<String> tokens) {
        if (tokens.size() < 2) {
            throw new ValidationException("Ошибка: используйте meas_list <sample_id>");
        }
        long sampleId = parseLong(tokens.get(1), "Ошибка: sample_id должно быть числом");
        MeasurementParam param = null;
        Integer last = null;

        for (int i = 2; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if ("--param".equals(token)) {
                if (i + 1 >= tokens.size()) {
                    throw new ValidationException("Ошибка: неизвестный параметр");
                }
                param = MeasurementParam.parse(tokens.get(++i));
            } else if ("--last".equals(token)) {
                if (i + 1 >= tokens.size()) {
                    throw new ValidationException("Ошибка: last должно быть числом");
                }
                last = (int) parseLong(tokens.get(++i), "Ошибка: last должно быть числом");
            } else {
                throw new ValidationException("Ошибка: неизвестная опция " + token);
            }
        }

        List<Measurement> measurements = measurementService.list(sampleId, param, last);
        System.out.println("ID Param Value Unit Method Time");
        for (Measurement measurement : measurements) {
            System.out.printf("%d %s %s %s %s %s%n",
                    measurement.getId(),
                    measurement.getParam(),
                    formatNumber(measurement.getValue()),
                    measurement.getUnit(),
                    measurement.getMethod(),
                    TIME_FORMATTER.format(measurement.getMeasuredAt().atZone(ZoneId.systemDefault())));
        }
    }

    private void handleMeasurementStats(List<String> tokens) {
        if (tokens.size() != 3) {
            throw new ValidationException("Ошибка: используйте meas_stats <sample_id> <param>");
        }
        long sampleId = parseLong(tokens.get(1), "Ошибка: sample_id должно быть числом");
        MeasurementParam param = MeasurementParam.parse(tokens.get(2));
        MeasurementService.Stats stats = measurementService.stats(sampleId, param);

        System.out.println("count: " + stats.getCount());
        System.out.println("min: " + formatNumber(stats.getMin()));
        System.out.println("max: " + formatNumber(stats.getMax()));
        System.out.println("avg: " + formatNumber(stats.getAvg()));
    }

    private void handleProtocolCreate() {
        System.out.print("Название протокола: ");
        String name = scanner.nextLine();
        System.out.print("Обязательные параметры (через запятую): ");
        String paramsLine = scanner.nextLine();
        LinkedHashSet<MeasurementParam> params = parseParams(paramsLine);
        Protocol protocol = protocolService.create(name, params);
        System.out.println("OK protocol_id=" + protocol.getId());
    }

    private void handleProtocolApply(List<String> tokens) {
        if (tokens.size() != 3) {
            throw new ValidationException("Ошибка: используйте prot_apply <protocol_id> <sample_id>");
        }
        long protocolId = parseLong(tokens.get(1), "Ошибка: protocol_id должно быть числом");
        long sampleId = parseLong(tokens.get(2), "Ошибка: sample_id должно быть числом");
        List<MeasurementParam> missing = protocolService.apply(protocolId, sampleId, sampleService, measurementService);
        if (missing.isEmpty()) {
            System.out.println("OK protocol is complete");
        } else {
            System.out.println("Missing params: " + joinParams(missing));
        }
    }

    private LinkedHashSet<MeasurementParam> parseParams(String line) {
        LinkedHashSet<MeasurementParam> result = new LinkedHashSet<>();
        if (line == null || line.isBlank()) {
            return result;
        }
        for (String part : line.split(",")) {
            if (!part.isBlank()) {
                result.add(MeasurementParam.parse(part.trim()));
            }
        }
        return result;
    }

    private String joinParams(List<MeasurementParam> params) {
        List<String> values = new ArrayList<>();
        for (MeasurementParam param : params) {
            values.add(param.name());
        }
        return String.join(", ", values);
    }

    private long parseLong(String value, String errorMessage) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ValidationException(errorMessage);
        }
    }

    private double parseDouble(String value, String errorMessage) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ValidationException(errorMessage);
        }
    }

    private String formatNumber(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (Character.isWhitespace(ch) && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(ch);
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }
}
