package house_app;


import House_Message.HM_outer;
import House_Message.HM_outer.*;
import House_Message.HouseServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class HouseBroadcast implements Runnable {

    int choose = 0;
    StreamObserver streamObserver;
    ManagedChannel channel;

    Join join;
    Election election;
    Statistic statistic;
    Leave leave;
    Boost boost;


    public HouseBroadcast(int port, Join message, StreamObserver so)
    {
        streamObserver = so;
        channel =  ManagedChannelBuilder.forAddress("localhost", port).usePlaintext(true).build();
        join = message;
        choose = 0;
    }

    public HouseBroadcast(int port, Election message, StreamObserver so)
    {
        streamObserver = so;
        //channel =  ManagedChannelBuilder.forAddress("localhost", port).usePlaintext(true).build();
        election = message;
        if (message.getType().equals("ELECTION"))
            choose = 1;
        else
            choose = 2 ;
    }

    public HouseBroadcast(int port, Statistic message, StreamObserver so)
    {
        streamObserver = so;
        channel =  ManagedChannelBuilder.forAddress("localhost", port).usePlaintext(true).build();
        statistic = message;
        if (message.getType().equals("STAT_HOUSE"))
            choose = 3;
        else
            choose = 4;  //è il messaggio che il coordinatore manda a tutti con le stat giuste
    }

    public HouseBroadcast(int port, Leave message, StreamObserver so)
    {
        streamObserver = so;
        channel =  ManagedChannelBuilder.forAddress("localhost", port).usePlaintext(true).build();
        leave = message;
        choose = 5;
    }

    public HouseBroadcast(int port, Boost message, StreamObserver so)
    {
        streamObserver = so;
        channel =  ManagedChannelBuilder.forAddress("localhost", port).usePlaintext(true).build();
        boost = message;
        if (message.getTimestamp()!=0)
            choose = 6;
        else
            choose = 7;
    }


    @Override
    public void run() {
        switch (choose){
            case 0: //ingresso
                HouseServiceGrpc.newStub(channel).presentation(join, streamObserver);
                break;

            case 1:
                HouseServiceGrpc.newStub(channel).coordinatorElection(election, streamObserver);
                break;
            case 2: //diffusione coordinatore
                HouseServiceGrpc.newStub(channel).imThePresident(election, streamObserver);
                break;

            case 3: //diffusione statistiche
                HouseServiceGrpc.newStub(channel).sendStat(statistic, streamObserver);
                break;
            case 4: //diffuzione statistiche del coordinatore
                HouseServiceGrpc.newStub(channel).spreadStat(statistic, streamObserver);
                break;

            case 5: //leave
                HouseServiceGrpc.newStub(channel).leaveNetwork(leave, streamObserver);
                break;

            case 6: //BOOST
                HouseServiceGrpc.newStub(channel).boostRequest(boost, streamObserver);
                break;
            case 7: //Notificare a chi ha bisogno del boost
                HouseServiceGrpc.newStub(channel).boostRelease(boost, streamObserver);
                break;

        }
        try {
            channel.awaitTermination(30,TimeUnit.SECONDS);
            channel.shutdown();
        } catch (InterruptedException e) {
            System.err.println("ERRORE AWAIT TERMINATION");
        }
    }
}
