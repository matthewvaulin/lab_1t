package main.java.domain;

import java.time.Instant;

public final class Measurement {
    private final long id;
    private final long sampleId;
    private final MeasurementParam param;
    private final double value;
    private final String unit;
    private final String method;
    private final Instant measuredAt;
    private final String ownerUsername;
    private final Instant createdAt;
    private Instant updatedAt;

    public Measurement(long id,
                       long sampleId,
                       MeasurementParam param,
                       double value,
                       String unit,
                       String method,
                       Instant measuredAt,
                       String ownerUsername) {
        this.id = id;
        this.sampleId = sampleId;
        this.param = param;
        this.value = value;
        this.unit = unit;
        this.method = method;
        this.measuredAt = measuredAt;
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public long getId() {
        return id;
    }

    public long getSampleId() {
        return sampleId;
    }

    public MeasurementParam getParam() {
        return param;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getMethod() {
        return method;
    }

    public Instant getMeasuredAt() {
        return measuredAt;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
