package org.example.traceability;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.example.animal.Animal;

@Entity
public class Part {

    @Id
    @GeneratedValue
    private Long id;

    private String partType;

    private double weight;

    @ManyToOne(optional = false)
    private Animal animal;

    @ManyToOne(optional = false)
    private Tray tray;

    protected Part() {
    }

    public Part(String partType, double weight, Animal animal, Tray tray) {
        this.partType = partType;
        this.weight = weight;
        this.animal = animal;
        this.tray = tray;
    }

    public Long getId() { return id; }
    public String getPartType() { return partType; }
    public double getWeight() { return weight; }
    public Animal getAnimal() { return animal; }
    public Tray getTray() { return tray; }
}
