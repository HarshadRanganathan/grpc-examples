package com.grpc.example.greeting.client;

import com.proto.greet.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public void run () {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        doUnaryCall(channel);
        doServerStreamingCall(channel);
        doClientStreamingCall(channel);
        channel.shutdown();
    }

    private void doUnaryCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetService = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder().setFirstName("Harshad").setLastName("Ranganathan").build();
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        GreetResponse greetResponse = greetService.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetService = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder().setFirstName("Harshad").setLastName("Ranganathan").build();
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();

        greetService.greetManyTimes(greetManyTimesRequest).forEachRemaining(greetManyTimesResponse -> {
            System.out.println(greetManyTimesResponse.getResult());
        });
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub greetService = GreetServiceGrpc.newStub(channel);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver = greetService.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        requestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Harshad").build()).build());
        requestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Adam").build()).build());
        requestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Pinto").build()).build());
        requestStreamObserver.onCompleted();

        try {
            countDownLatch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SSLException {
        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }

}
