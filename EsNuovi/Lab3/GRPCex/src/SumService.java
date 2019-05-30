import SumEx.SumGrpc.*;
import SumEx.SumEx_outer.Input;
import SumEx.SumEx_outer.Output;
import io.grpc.stub.StreamObserver;

public class SumService extends SumImplBase {


    @Override
    public void simpleSum(Input request, StreamObserver<Output> response){
        Output resp = Output.newBuilder().setRes(request.getN1()+request.getN2()).build();
        response.onNext(resp);
        response.onCompleted();
    }

    @Override
    public void repeatedSum (Input request, StreamObserver<Output> response){

        for (int i = 1; i <= request.getN2(); i++){
            response.onNext( Output.newBuilder().setRes(request.getN1()*i).build());
        }
        response.onCompleted();
    }

    @Override
    public StreamObserver<Input> streamSum (StreamObserver<Output> response){


        return new StreamObserver<Input>() {
            @Override
            public void onNext(Input input) {
                Output resp = Output.newBuilder().setRes(input.getN1()+input.getN2()).build();
                response.onNext(resp);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                response.onCompleted();
            }
        };
    }
}
