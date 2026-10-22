package org.example.animal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/animals")
@Tag(name = "Animals", description = "Registrering af dyr ved station 1")
public class AnimalController {

    private final AnimalRepository repository;

    public AnimalController(AnimalRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Operation(summary = "Registrer et nyt dyr")
    public ResponseEntity<Animal> createAnimal(@Valid @RequestBody Animal animal) {
        if (repository.existsById(animal.getRegistrationNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Animal saved = repository.save(animal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{registrationNumber}")
                .buildAndExpand(saved.getRegistrationNumber())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/{registrationNumber}")
    @Operation(summary = "Hent ét dyr ud fra registreringsnummer")
    public Animal getAnimal(@PathVariable String registrationNumber) {
        return repository.findById(registrationNumber)
                .orElseThrow(() -> new AnimalNotFoundException(registrationNumber));
    }

    @GetMapping
    @Operation(summary = "Hent dyr, evt. filtreret på ankomstdato eller oprindelse")
    public List<Animal> getAnimals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String origin) {
        if (date != null) {
            return repository.findByArrivalDate(date);
        }
        if (origin != null) {
            return repository.findByOrigin(origin);
        }
        return repository.findAll();
    }
}
