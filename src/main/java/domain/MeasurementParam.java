package main.java.domain;

public enum MeasurementParam {
    PH,
    CONDUCTIVITY,
    TURBIDITY,
    NITRATE;

    public static MeasurementParam parse(String value) {
        try {
            return MeasurementParam.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка: неизвестный параметр");
        }
    }
}
