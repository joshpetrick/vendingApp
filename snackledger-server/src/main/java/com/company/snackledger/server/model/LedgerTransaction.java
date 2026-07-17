package com.company.snackledger.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
public class LedgerTransaction {
    @Id @GeneratedValue public Long id;
    public Long userId;
    public String type;
    public BigDecimal amount;
    public BigDecimal balanceAfter;
    public String reference;
    public OffsetDateTime createdAt = OffsetDateTime.now();
}
