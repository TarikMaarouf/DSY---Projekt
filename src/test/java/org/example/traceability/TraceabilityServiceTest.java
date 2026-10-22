package org.example.traceability;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.animal.Animal;
import org.example.animal.AnimalRepository;
import org.example.grpc.AnimalList;
import org.example.grpc.AnimalRequest;
import org.example.grpc.ProductDto;
import org.example.grpc.ProductList;
import org.example.grpc.ProductRequest;
import org.example.grpc.TraceabilityServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc // samme konfiguration som AnimalControllerTest, så Spring genbruger konteksten og gRPC-porten
class TraceabilityServiceTest {

    @Autowired private AnimalRepository animalRepository;
    @Autowired private TrayRepository trayRepository;
    @Autowired private PartRepository partRepository;
    @Autowired private ProductRepository productRepository;

    private ManagedChannel channel;
    private TraceabilityServiceGrpc.TraceabilityServiceBlockingStub stub;
    private Product halfAnimal;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        partRepository.deleteAll();
        trayRepository.deleteAll();
        animalRepository.deleteAll();

        Animal a1 = animalRepository.save(new Animal("A-1", LocalDate.of(2026, 10, 20), 110.0, "Hansens Gård"));
        Animal a2 = animalRepository.save(new Animal("A-2", LocalDate.of(2026, 10, 20), 98.0, "Jensens Gård"));
        Animal a3 = animalRepository.save(new Animal("A-3", LocalDate.of(2026, 10, 21), 120.0, "Hansens Gård"));
        animalRepository.save(new Animal("A-4", LocalDate.of(2026, 10, 21), 105.0, "Nygård"));

        Tray hamTray1 = trayRepository.save(new Tray("skinke", 20.0));
        Tray ribTray = trayRepository.save(new Tray("ribben", 15.0));
        Tray hamTray2 = trayRepository.save(new Tray("skinke", 20.0));

        partRepository.save(new Part("skinke", 8.5, a1, hamTray1));
        partRepository.save(new Part("skinke", 8.1, a2, hamTray1));
        partRepository.save(new Part("ribben", 4.2, a1, ribTray));
        partRepository.save(new Part("ribben", 4.0, a2, ribTray));
        partRepository.save(new Part("skinke", 9.0, a3, hamTray2));

        productRepository.save(new Product("PART_PACKAGE", List.of(hamTray1)));
        halfAnimal = productRepository.save(new Product("HALF_ANIMAL", List.of(hamTray2, ribTray)));

        channel = ManagedChannelBuilder.forAddress("localhost", 9091).usePlaintext().build();
        stub = TraceabilityServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        channel.shutdownNow();
        productRepository.deleteAll();
        partRepository.deleteAll();
        trayRepository.deleteAll();
        animalRepository.deleteAll();
    }

    @Test
    void getAnimalsForProductReturnsAllAnimalsInTrays() {
        AnimalList result = stub.getAnimalsForProduct(
                ProductRequest.newBuilder().setProductId(halfAnimal.getId()).build());

        assertEquals(3, result.getRegistrationNumbersCount());
        assertTrue(result.getRegistrationNumbersList().containsAll(List.of("A-1", "A-2", "A-3")));
    }

    @Test
    void getProductsForAnimalReturnsAllProducts() {
        ProductList result = stub.getProductsForAnimal(
                AnimalRequest.newBuilder().setRegistrationNumber("A-1").build());

        List<String> types = result.getProductsList().stream().map(ProductDto::getProductType).toList();
        assertEquals(2, result.getProductsCount());
        assertTrue(types.containsAll(List.of("PART_PACKAGE", "HALF_ANIMAL")));
    }

    @Test
    void animalWithoutPartsHasNoProducts() {
        ProductList result = stub.getProductsForAnimal(
                AnimalRequest.newBuilder().setRegistrationNumber("A-4").build());

        assertEquals(0, result.getProductsCount());
    }

    @Test
    void unknownProductReturnsNotFound() {
        StatusRuntimeException e = assertThrows(StatusRuntimeException.class, () ->
                stub.getAnimalsForProduct(ProductRequest.newBuilder().setProductId(999).build()));

        assertEquals(Status.Code.NOT_FOUND, e.getStatus().getCode());
    }

    @Test
    void unknownAnimalReturnsNotFound() {
        StatusRuntimeException e = assertThrows(StatusRuntimeException.class, () ->
                stub.getProductsForAnimal(AnimalRequest.newBuilder().setRegistrationNumber("UKENDT").build()));

        assertEquals(Status.Code.NOT_FOUND, e.getStatus().getCode());
    }
}
