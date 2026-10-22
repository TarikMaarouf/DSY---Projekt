package org.example.traceability;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.animal.AnimalRepository;
import org.example.grpc.AnimalList;
import org.example.grpc.AnimalRequest;
import org.example.grpc.ProductDto;
import org.example.grpc.ProductList;
import org.example.grpc.ProductRequest;
import org.example.grpc.TraceabilityServiceGrpc;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraceabilityServiceImpl extends TraceabilityServiceGrpc.TraceabilityServiceImplBase {

    private final ProductRepository productRepository;
    private final AnimalRepository animalRepository;

    public TraceabilityServiceImpl(ProductRepository productRepository, AnimalRepository animalRepository) {
        this.productRepository = productRepository;
        this.animalRepository = animalRepository;
    }

    @Override
    public void getAnimalsForProduct(ProductRequest request, StreamObserver<AnimalList> responseObserver) {
        long productId = request.getProductId();
        if (!productRepository.existsById(productId)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Product not found: " + productId)
                    .asRuntimeException());
            return;
        }
        List<String> registrationNumbers = productRepository.findAnimalRegistrationNumbersByProductId(productId);
        responseObserver.onNext(AnimalList.newBuilder()
                .addAllRegistrationNumbers(registrationNumbers)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductsForAnimal(AnimalRequest request, StreamObserver<ProductList> responseObserver) {
        String registrationNumber = request.getRegistrationNumber();
        if (!animalRepository.existsById(registrationNumber)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Animal not found: " + registrationNumber)
                    .asRuntimeException());
            return;
        }
        ProductList.Builder response = ProductList.newBuilder();
        for (Product product : productRepository.findProductsByAnimal(registrationNumber)) {
            response.addProducts(ProductDto.newBuilder()
                    .setId(product.getId())
                    .setProductType(product.getProductType())
                    .build());
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
