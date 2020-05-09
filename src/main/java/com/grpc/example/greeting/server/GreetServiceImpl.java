package com.grpc.example.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String result = "Hello " + firstName;

        GreetResponse greetResponse = GreetResponse.newBuilder().setResult(result).build();

        // return the response to client
        responseObserver.onNext(greetResponse);
        // complete the RPC call
        responseObserver.onCompleted();
    }
}
