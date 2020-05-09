package com.grpc.example.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(50051).addService(new CalculatorServiceImpl()).build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }
}
