package org.example.traceability;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Tray {

    @Id
    @GeneratedValue
    private Long id;

    private String partType;

    private double maxWeight;

    protected Tray() {
    }

    public Tray(String partType, double maxWeight) {
        this.partType = partType;
        this.maxWeight = maxWeight;
    }

    public Long getId() { return id; }
    public String getPartType() { return partType; }
    public double getMaxWeight() { return maxWeight; }
}
