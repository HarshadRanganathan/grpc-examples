package com.grpc.example.greeting.client;

import com.proto.greet.*;
import io.grpc.*;

import javax.net.ssl.SSLException;

public class GreetingClient {

    public static void main(String[] args) throws SSLException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        // unary
        GreetServiceGrpc.GreetServiceBlockingStub greetService = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder().setFirstName("Harshad").setLastName("Ranganathan").build();
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        GreetResponse greetResponse = greetService.greet(greetRequest);
        System.out.println(greetResponse.getResult());

        // server streaming
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();
        greetService.greetManyTimes(greetManyTimesRequest).forEachRemaining(greetManyTimesResponse -> {
            System.out.println(greetManyTimesResponse.getResult());
        });

        channel.shutdown();
    }

}
