package com.company.snackledger.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "kioskIdentifier"))
public class KioskDevice {
    @Id @GeneratedValue public Long id;
    public String kioskName;
    public String kioskIdentifier;
    public String apiKeyHash;
    public boolean active = true;
    public OffsetDateTime lastConnectionAt;
    public String lastKnownIpAddress;
    public String applicationVersion;
    public OffsetDateTime createdAt = OffsetDateTime.now();
    public OffsetDateTime updatedAt = OffsetDateTime.now();
}
