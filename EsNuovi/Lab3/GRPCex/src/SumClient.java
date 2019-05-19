import SumEx.SumGrpc.*;
import SumEx.SumGrpc;
import SumEx.SumEx_outer.Input;
import SumEx.SumEx_outer.Output;

import io.grpc.stub.StreamObserver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SumClient {
    public static void main(String[] argv) {
        System.out.println("----------------Simple Sum Sync");
        SimpleSumSynch ();

        System.out.println("----------------Simple Sum ASynch");
        SimpleSumAsynch();

        System.out.println("----------------Repeated Sum Sync");
        RepeatedSumSync();

        System.out.println("----------------Repeated Sum ASync");
        RepeatedSumAsynch();

        System.out.println("----------------Stream Sum");
        StreamSum();

    }

    public static void SimpleSumSynch (){
        String host = "localhost";
        int port = 6060;
        final ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();

        //Sincrono
        //SumGRPC è praticamente il "service Sum" scritto nel proto
        SumGrpc.SumBlockingStub stub = SumGrpc.newBlockingStub(channel);

        Input request = NewInput();

        Output response = stub.simpleSum(request);
        System.out.println("SimpleSum Sincrono: "+response.getRes());

        channel.shutdown();
    }

    public static void SimpleSumAsynch(){
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:6060").usePlaintext(true).build();

        //Asincrono
        SumGrpc.SumStub stub = SumGrpc.newStub(channel);
        Input request = NewInput();

        StreamObserver <Output> so_simple = new StreamObserver<Output>() {
            @Override
            public void onNext(Output output) {
                System.out.println("SimpleSum Asincrono: "+output.getRes());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("SimpleSum Asincrono Error " +throwable.getMessage() );
            }

            @Override
            public void onCompleted() {
                System.out.println("SimpleSum Asincrono Completato");
                channel.shutdown();
            }
        };

        stub.simpleSum(request, so_simple);
        bgUaiting(channel);
    }

    public static void RepeatedSumSync(){
        String host = "localhost";
        int port = 6060;
        final ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(false).build();

        //Sincrono
        SumGrpc.SumBlockingStub stub = SumGrpc.newBlockingStub(channel);

        Input request = NewInput();

        Iterator<Output> streamResponse = stub.repeatedSum(request);

        for (int i = 0; streamResponse.hasNext(); i++) {
            System.out.println("RepeatedSum Sincrono: " + streamResponse.next().getRes());
        }
        channel.shutdown();
    }

    public static void RepeatedSumAsynch(){
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:6060").usePlaintext(true).build();

        //Asincrono
        SumGrpc.SumStub stub = SumGrpc.newStub(channel);
        Input request = NewInput();

        StreamObserver <Output> so_repeated = new StreamObserver<Output>() {
            @Override
            public void onNext(Output output) {
                System.out.println("RepeatedSum Asincrono: "+output.getRes());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("RepeatedSum Asincrono Error " +throwable.getMessage() );
            }

            @Override
            public void onCompleted() {
                System.out.println("RepeatedSum Asincrono Completato");
                channel.shutdown();
            }
        };

        stub.repeatedSum(request, so_repeated);
        bgUaiting(channel);
    }

    public static void StreamSum(){
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:6060").usePlaintext(true).build();
        SumStub stub = SumGrpc.newStub(channel);

        StreamObserver<Output> so_streamOutput = new StreamObserver<Output>() {
            @Override
            public void onNext(Output output) {
                System.out.println("StreamSum: "+output.getRes());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("StreamSum Error: "+throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("StreamSum Completato");
                channel.shutdown();
            }
        };

        StreamObserver<Input> so_streamInput = stub.streamSum(so_streamOutput);

        for (int i= 0; i<40; i++)
            so_streamInput.onNext(NewInput());

        System.out.println("Seconda mandata");
        for (int i= 0; i<5; i++)
            so_streamInput.onNext(NewInput());

        so_streamInput.onCompleted();

        bgUaiting(channel);
    }

    public static void bgUaiting(ManagedChannel channel){
        try {

            channel.awaitTermination(10, TimeUnit.SECONDS);

        }catch (InterruptedException e){
            System.err.println("------ Errore nel awaitTerminator");
            e.printStackTrace();
        }
    }
    public static Input NewInput (){

        Random rand = new Random(System.nanoTime());
        Input request = Input.newBuilder()
                .setN1(rand.nextInt(10)+1)
                .setN2(rand.nextInt(10)+1)
                .build();

        System.out.println("N1 "+ request.getN1()+" N2 "+request.getN2());
        return request;

    }
}
