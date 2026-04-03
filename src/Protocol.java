import java.time.Instant;
import java.util.Set;

public final class Protocol {
    private long id;
    private String name;
    private Set<MeasurementParam> requiredParams;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public Protocol (long id, String name, Set<MeasurementParam> requiredParams, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.requiredParams = requiredParams;
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Set<MeasurementParam> getRequiredParams() {
        return requiredParams;
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
