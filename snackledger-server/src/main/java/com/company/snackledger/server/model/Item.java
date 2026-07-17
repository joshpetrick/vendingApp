package com.company.snackledger.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Item {
    @Id @GeneratedValue public Long id;
    public String name;
    public String barcode;
    public BigDecimal price;
    public boolean active = true;
}
