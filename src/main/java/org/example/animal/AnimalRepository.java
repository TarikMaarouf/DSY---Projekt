package org.example.animal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, String> {

    List<Animal> findByArrivalDate(LocalDate arrivalDate);

    List<Animal> findByOrigin(String origin);
}
