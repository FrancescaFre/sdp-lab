package house_app;

import House_Message.HM_outer;
import House_Message.HouseServiceGrpc;
import House_Message.HouseServiceGrpc.*;

import House_Message.HM_outer.Join;
import House_Message.HM_outer.Election;
import House_Message.HM_outer.Leave;
import House_Message.HM_outer.Boost;
import House_Message.HM_outer.Statistic;
//import House_Message.HM_outer.President;

import io.grpc.stub.StreamObserver;
import message_measurement.House;
import simulation_src_2019.Measurement;

import javax.ws.rs.core.Response;


public class HouseService extends HouseServiceImplBase{

    HouseNode node;

    public HouseService(HouseNode hn){
        node = hn;
    }

    //---------------------------------------------------Statistiche
    @Override
    public void sendStat(Statistic request, StreamObserver<Statistic> response){

        if (request.getReply()) //se è un reply vuol dire che devo diffondere il messaggio del coordinatore
            node.print_value(request.getValue(), request.getTimestamp(), true); //quindi appena arriva una misurazione, questa viene stampata - MEDIA DEL CONDOMINIO

        Integer progressive_residence_mean_id = node.MeanStat_SendStat(request.getHouseId(), request.getMeasurementId(), request.getValue());
            System.out.println("---------------------"+progressive_residence_mean_id);

    if (progressive_residence_mean_id != null) {
          Statistic.Builder statisticReply = Statistic.newBuilder();

          statisticReply.setType("STAT");
          statisticReply.setValue(node.res_values.get(node.res_values.size() - 1).getValue());
          statisticReply.setTimestamp(System.currentTimeMillis());
          statisticReply.setMeasurementId(progressive_residence_mean_id);
          statisticReply.setHouseId(Integer.parseInt(node.id));

          statisticReply.setReply(true);

          statisticReply.build();

          response.onNext(statisticReply.build());
        }

        response.onCompleted();
    }

    //---------------------------------------------------JOIN
    @Override
    public void presentation (Join request, StreamObserver<Join> response){

        int coordinator = node.Welcome(request.getHouseId(), request.getPort());

        Join.Builder joinReply = Join.newBuilder();

        joinReply.setType("JOIN");
        joinReply.setHouseId(Integer.parseInt(node.id));
        joinReply.setPort(node.port);
        joinReply.setIp("localhost");
        joinReply.setReply(true);
        joinReply.setCoordinator(coordinator);

        response.onNext(joinReply.build());
        response.onCompleted();
    }

    //---------------------------------------------------RIMOZIONE
    @Override
    public void leaveNetwork (Leave request, StreamObserver<Leave> response){
        if (!request.getType().equals("LEAVE"))
            response.onCompleted();

        node.GoodBye(request.getId(), request.getCoordinator());

        Leave.Builder leaveReply = Leave.newBuilder();
        leaveReply.setType("LEAVE");
        leaveReply.setId(Integer.parseInt(node.id));
        leaveReply.setReply(true);
        leaveReply.setCoordinator(false);

        response.onNext(leaveReply.build());
        response.onCompleted();
    }


    //---------------------------------------------------ELEZIONE
    @Override
    public void coordinatorElection(Election request, StreamObserver<Election> response){

       node.Election(request.getHouseId());

        Election.Builder electionReply = Election.newBuilder();

        electionReply.setType("ELECTION");
        electionReply.setReply(true);

        response.onNext(electionReply.build());
        response.onCompleted();
    }

    //---------------------------------------------------BOOST
    @Override
    public void boostRequest (Boost request, StreamObserver<Boost> response){
        //se non sto usando la risorsa rispondo con ok, altrimenti con wait e lo faccio mettere in coda
    }

    //---------------------------------------------------President
    @Override
    public void imThePresident(Election request, StreamObserver<Election> responseObserver) {
        node.coordinator_id = request.getHouseId(); //memorizzo il nuovo coordinatore
        responseObserver.onCompleted();
    }


    //---------------------------------------------------President
    @Override
public void checkConnection (Join request, StreamObserver<Join> response){
        response.onCompleted();
    }
}