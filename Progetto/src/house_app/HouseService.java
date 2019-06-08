package house_app;

import House_Message.HouseServiceGrpc.*;

import House_Message.HM_outer.Join;
import House_Message.HM_outer.Election;
import House_Message.HM_outer.Leave;
import House_Message.HM_outer.Boost;
import House_Message.HM_outer.Statistic;

import io.grpc.stub.StreamObserver;



public class HouseService extends HouseServiceImplBase{

    final HouseNode node;

    public HouseService(HouseNode hn){
        node = hn;
    }



    //---------------------------------------------------JOIN
    @Override
    public void presentation (Join request, StreamObserver<Join> response){

        int coordinator = node.welcome(request.getHouseId(), request.getPort());

        Join.Builder joinReply = Join.newBuilder();

        joinReply.setHouseId(Integer.parseInt(node.id));
        joinReply.setPort(node.port);
        joinReply.setIp("localhost");
        joinReply.setCoordinator(coordinator);

        response.onNext(joinReply.build());
        response.onCompleted();
    }

    //---------------------------------------------------RIMOZIONE
    @Override
    public void leaveNetwork (Leave request, StreamObserver<Leave> response){
        Leave.Builder leaveReply = Leave.newBuilder();
        if (request.getId() != Integer.parseInt(node.id)) {
          node.goodbye(request.getId(), request.getCoordinator());

          leaveReply.setId(Integer.parseInt(node.id));
          leaveReply.setCoordinator(false);
        }

        response.onNext(leaveReply.build());
        response.onCompleted();
    }
    //---------------------------------------------------Statistiche
    //per le statistiche inviate da una casa
    @Override
    public void sendStat(Statistic request, StreamObserver<Statistic> response){
         Integer progressive_residence_mean_id = node.res_Mean(request.getHouseId(), request.getMeasurementId(), request.getValue());
         response.onNext(Statistic.newBuilder().setHouseId(Integer.parseInt(node.id)).build());

        response.onCompleted();
    }

    //per le statistiche del coordinatore
    @Override
    public void spreadStat(Statistic request, StreamObserver<Statistic> response){
        node.print_value(request.getValue(), request.getTimestamp(), true); //quindi appena arriva una misurazione, questa viene stampata - MEDIA DEL CONDOMINIO
        response.onNext(Statistic.newBuilder().build());

        response.onCompleted();
    }
    //---------------------------------------------------ELEZIONE

    @Override
    public void coordinatorElection(Election request, StreamObserver<Election> response) {

        //node.startElection(request.getHouseId());
        if(!node.getInElection()) //se il nodo non è già stato interpellato per l'elezione, chiama l'elezione
            node.startElection();

        Election.Builder electionReply = Election.newBuilder();
        electionReply.setHouseId(Integer.parseInt(node.id));

        response.onNext(electionReply.build());
        response.onCompleted();
    }

    //---------------------------------------------------BOOST
    @Override
    public void boostRequest(Boost request, StreamObserver<Boost> response) {

        Boost.Builder boost = Boost.newBuilder();
        //rispondo ok se non sto usando la risorsa o se sono me stesso che la chiedo a me
        if(node.reBoost(request.getHouseId(), request.getTimestamp()) || Integer.parseInt(node.id) == request.getHouseId())
        {
            boost.setHouseId(Integer.parseInt(node.id));
            boost.setReply("OK");
            boost.setTimestamp(System.currentTimeMillis());
        }
        else
        {
            boost.setHouseId(Integer.parseInt(node.id));
            boost.setReply("WAIT");
            boost.setTimestamp(System.currentTimeMillis());
        }

        response.onNext(boost.build());
        response.onCompleted();
    }

    @Override
    public void boostRelease(Boost request, StreamObserver<Boost> response) {
        node.checkPermission(request.getHouseId(), request.getReply());
        //qui aggiorno il dizionario aggiungendo l'ok di chi fa il release della risorsa
        response.onNext(Boost.newBuilder().build());
        response.onCompleted();
    }


    //---------------------------------------------------President
    @Override
    public void imThePresident(Election request, StreamObserver<Election> responseObserver) {
    if (node.coordinator_id != request.getHouseId()) {
          node.coordinator_id = request.getHouseId(); // memorizzo il nuovo coordinatore
          node.coordinator = false;
          node.inElection = false;
        }
        responseObserver.onNext(Election.newBuilder().build());
        responseObserver.onCompleted();
    }

    //---------------------------------------------------CHECK
    @Override
    public void checkConnection (Join request, StreamObserver<Join> response){

        response.onNext(Join.newBuilder().build());
        response.onCompleted();
    }


}
