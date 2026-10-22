package org.example.traceability;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GrpcServer {

    private final TraceabilityServiceImpl traceabilityService;
    private final int port;
    private Server server;

    public GrpcServer(TraceabilityServiceImpl traceabilityService, @Value("${grpc.port:9090}") int port) {
        this.traceabilityService = traceabilityService;
        this.port = port;
    }

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(traceabilityService)
                .build()
                .start();
        System.out.println("gRPC server started on port " + port);
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
