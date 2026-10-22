package org.example.traceability;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String productType;

    @ManyToMany
    private List<Tray> trays = new ArrayList<>();

    protected Product() {
    }

    public Product(String productType, List<Tray> trays) {
        this.productType = productType;
        this.trays = new ArrayList<>(trays);
    }

    public Long getId() { return id; }
    public String getProductType() { return productType; }
    public List<Tray> getTrays() { return trays; }
}
