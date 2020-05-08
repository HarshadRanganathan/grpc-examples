package com.grpc.example.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        final Server server = ServerBuilder.forPort(50051)
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }

}
