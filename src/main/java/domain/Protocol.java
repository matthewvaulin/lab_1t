package main.java.domain;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Protocol {
    private final long id;
    private final String name;
    private final LinkedHashSet<MeasurementParam> requiredParams;
    private final String ownerUsername;
    private final Instant createdAt;
    private Instant updatedAt;

    public Protocol(long id, String name, Set<MeasurementParam> requiredParams, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.requiredParams = new LinkedHashSet<>(requiredParams);
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LinkedHashSet<MeasurementParam> getRequiredParams() {
        return new LinkedHashSet<>(requiredParams);
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
