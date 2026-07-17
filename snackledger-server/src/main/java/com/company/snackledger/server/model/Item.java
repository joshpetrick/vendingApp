package com.company.snackledger.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
public class Item {
    @Id @GeneratedValue public Long id;
    public String name;
    public String barcode;
    public BigDecimal price;
    @Size(max = 500) public String imageUrl;
    public boolean active = true;
}
