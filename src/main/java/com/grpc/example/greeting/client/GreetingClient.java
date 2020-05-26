package com.grpc.example.greeting.client;

import com.proto.greet.*;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public void run () throws SSLException {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 50051)
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
                .build();

        doUnaryCall(channel);
        doServerStreamingCall(channel);
        doClientStreamingCall(channel);
        doBiDiStreamingCall(channel);
        doUnaryCallWithDeadline(channel);
        channel.shutdown();
    }

    private void doUnaryCall(ManagedChannel channel) {
        System.out.println("================ Starting Unary Call =======================");
        GreetServiceGrpc.GreetServiceBlockingStub greetService = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder().setFirstName("Harshad").setLastName("Ranganathan").build();
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        GreetResponse greetResponse = greetService.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        System.out.println("================ Starting Server Streaming =======================");
        GreetServiceGrpc.GreetServiceBlockingStub greetService = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder().setFirstName("Harshad").setLastName("Ranganathan").build();
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();

        greetService.greetManyTimes(greetManyTimesRequest).forEachRemaining(greetManyTimesResponse -> {
            System.out.println(greetManyTimesResponse.getResult());
        });
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        System.out.println("================ Starting Client Streaming =======================");
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

    private void doBiDiStreamingCall(ManagedChannel channel) {
        System.out.println("================ Starting Bi-Directional Streaming =======================");
        GreetServiceGrpc.GreetServiceStub greetService = GreetServiceGrpc.newStub(channel);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestStreamObserver = greetService.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
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

        requestStreamObserver.onNext(GreetEveryoneRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Harshad").build()).build());
        requestStreamObserver.onNext(GreetEveryoneRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Adam").build()).build());
        requestStreamObserver.onNext(GreetEveryoneRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Pinto").build()).build());
        requestStreamObserver.onCompleted();

        try {
            countDownLatch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doUnaryCallWithDeadline(ManagedChannel channel) {
        System.out.println("================ Starting Unary Call With Deadline =======================");
        GreetServiceGrpc.GreetServiceBlockingStub greetService = GreetServiceGrpc.newBlockingStub(channel)
                .withDeadline(Deadline.after(50, TimeUnit.MILLISECONDS));

        Greeting greeting = Greeting.newBuilder().setFirstName("Harshad").setLastName("Ranganathan").build();
        GreetWithDeadlineRequest greetRequest = GreetWithDeadlineRequest.newBuilder().setGreeting(greeting).build();

        try {
            GreetWithDeadlineResponse greetResponse = greetService.greetWithDeadline(greetRequest);
            System.out.println(greetResponse.getResult());
        } catch (StatusRuntimeException statusRuntimeException) {
            if(statusRuntimeException.getStatus().getCode().equals(Status.DEADLINE_EXCEEDED.getCode())) {
                System.out.println("Deadline has been exceeded.");
            }
        }
    }

    public static void main(String[] args) throws SSLException {
        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }

}
