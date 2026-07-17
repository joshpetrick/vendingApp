package com.company.snackledger.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"kioskId", "requestId"}))
public class Purchase {
    @Id @GeneratedValue public Long id;
    public String kioskId;
    public String requestId;
    public Long userId;
    public BigDecimal previousBalance;
    public BigDecimal purchaseTotal;
    public BigDecimal newBalance;
    public OffsetDateTime completedAt;
}
