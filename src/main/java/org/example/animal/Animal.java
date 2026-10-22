package org.example.animal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
public class Animal {

    @Id
    @NotBlank
    private String registrationNumber;

    @NotNull
    private LocalDate arrivalDate;

    @Positive
    private double weight;

    @NotBlank
    private String origin;

    protected Animal() {
    }

    public Animal(String registrationNumber, LocalDate arrivalDate, double weight, String origin) {
        this.registrationNumber = registrationNumber;
        this.arrivalDate = arrivalDate;
        this.weight = weight;
        this.origin = origin;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public LocalDate getArrivalDate() { return arrivalDate; }
    public double getWeight() { return weight; }
    public String getOrigin() { return origin; }
}
