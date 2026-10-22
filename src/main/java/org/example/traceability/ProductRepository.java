package org.example.traceability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Produkt -> bakker -> dele -> dyr
    @Query("select distinct p.animal.registrationNumber from Product pr join pr.trays t, Part p "
            + "where p.tray = t and pr.id = :productId")
    List<String> findAnimalRegistrationNumbersByProductId(@Param("productId") Long productId);

    // Dyr -> dele -> bakker -> produkter
    @Query("select distinct pr from Product pr join pr.trays t, Part p "
            + "where p.tray = t and p.animal.registrationNumber = :registrationNumber")
    List<Product> findProductsByAnimal(@Param("registrationNumber") String registrationNumber);
}
