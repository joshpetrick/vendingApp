package com.company.snackledger.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class AppUser {
    @Id @GeneratedValue public Long id;
    public String username;
    public String displayName;
    public String badgeId;
    public BigDecimal balance = BigDecimal.ZERO;
    public boolean active = true;
}
