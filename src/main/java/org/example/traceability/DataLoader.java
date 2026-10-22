package org.example.traceability;

import org.example.animal.Animal;
import org.example.animal.AnimalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class DataLoader implements CommandLineRunner {

    private final AnimalRepository animalRepository;
    private final TrayRepository trayRepository;
    private final PartRepository partRepository;
    private final ProductRepository productRepository;

    public DataLoader(AnimalRepository animalRepository, TrayRepository trayRepository,
                      PartRepository partRepository, ProductRepository productRepository) {
        this.animalRepository = animalRepository;
        this.trayRepository = trayRepository;
        this.partRepository = partRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Animal a1 = animalRepository.save(new Animal("A-1", LocalDate.of(2026, 10, 20), 110.0, "Hansens Gård"));
        Animal a2 = animalRepository.save(new Animal("A-2", LocalDate.of(2026, 10, 20), 98.0, "Jensens Gård"));
        Animal a3 = animalRepository.save(new Animal("A-3", LocalDate.of(2026, 10, 21), 120.0, "Hansens Gård"));

        Tray hamTray1 = trayRepository.save(new Tray("skinke", 20.0));
        Tray ribTray = trayRepository.save(new Tray("ribben", 15.0));
        Tray hamTray2 = trayRepository.save(new Tray("skinke", 20.0));

        partRepository.save(new Part("skinke", 8.5, a1, hamTray1));
        partRepository.save(new Part("skinke", 8.1, a2, hamTray1));
        partRepository.save(new Part("ribben", 4.2, a1, ribTray));
        partRepository.save(new Part("ribben", 4.0, a2, ribTray));
        partRepository.save(new Part("skinke", 9.0, a3, hamTray2));

        // Pakke med samme slags dele: kun skinker fra bakke 1 (A-1 og A-2)
        productRepository.save(new Product("PART_PACKAGE", List.of(hamTray1)));
        // Halvt dyr: skinke fra bakke 2 (A-3) og ribben (A-1 og A-2)
        productRepository.save(new Product("HALF_ANIMAL", List.of(hamTray2, ribTray)));
        // Pakke med samme slags dele: kun skinker fra bakke 2 (A-3)
        productRepository.save(new Product("PART_PACKAGE", List.of(hamTray2)));
    }
}
