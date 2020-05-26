package com.grpc.example.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService = CalculatorServiceGrpc.newBlockingStub(managedChannel);

        SumRequest sumRequest = SumRequest.newBuilder().setFirstNumber(1).setSecondNumber(5).build();
        SumResponse sumResponse = calculatorService.sum(sumRequest);
        System.out.println(sumResponse);

        SquareRootRequest squareRootRequest = SquareRootRequest.newBuilder().setNumber(-1).build();
        try {
            SquareRootResponse squareRootResponse = calculatorService.squareRoot(squareRootRequest);
        } catch (StatusRuntimeException statusRuntimeException) {
            statusRuntimeException.printStackTrace();
        }

        managedChannel.shutdown();
    }
}
