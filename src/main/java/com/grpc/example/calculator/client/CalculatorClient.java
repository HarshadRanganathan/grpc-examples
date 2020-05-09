package com.grpc.example.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService = CalculatorServiceGrpc.newBlockingStub(managedChannel);

        SumRequest sumRequest = SumRequest.newBuilder().setFirstNumber(1).setSecondNumber(5).build();
        SumResponse sumResponse = calculatorService.sum(sumRequest);
        System.out.println(sumResponse);

        managedChannel.shutdown();
    }
}
