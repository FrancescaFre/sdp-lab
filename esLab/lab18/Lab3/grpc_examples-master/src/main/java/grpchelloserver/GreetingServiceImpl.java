package grpchelloserver;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.GreetingServiceOuterClass;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greeting(GreetingServiceOuterClass.HelloRequest request, StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver){

        //la richiesta è di tipo HelloRequest (definito in .proto)
        System.out.println(request);

        //costruisco la richiesta di tipo HelloResponse (sempre definito in .proto)
        GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.HelloResponse.newBuilder().setGreeting("Hello there, "+request.getName()).build();

        //passo la risposta nello stream
        responseObserver.onNext(response);

        //completo e finisco la comunicazione
        responseObserver.onCompleted();

    }

    @Override
    public void streamGreeting(GreetingServiceOuterClass.HelloRequest request, StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver){

        System.out.println("Metodo stream chiamato!");

        //la richiesta è di tipo HelloRequest (definito in .proto)
        System.out.println(request);

        //costruisco la richiesta di tipo HelloResponse (sempre definito in .proto)
        GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.HelloResponse.newBuilder().setGreeting("Hello there, "+request.getName()).build();

        //passo la risposta nello stream
        responseObserver.onNext(response);
        responseObserver.onNext(response);
        responseObserver.onNext(response);

        //completo e finisco la comunicazione
        responseObserver.onCompleted();

    }

}
