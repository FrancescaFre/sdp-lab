package house_app;

import House_Message.HouseServiceGrpc.*;

import House_Message.HM_outer.Join;
import House_Message.HM_outer.Election;
import House_Message.HM_outer.Leave;
import House_Message.HM_outer.Boost;
import House_Message.HM_outer.Statistic;

import io.grpc.stub.StreamObserver;


public class HouseServerGrpc extends HouseServiceImplBase{
    //NON MI FA FARE GLI OVERRIDE???????????????????????????
  // @Override
    public void send_stat(Statistic request, StreamObserver<Statistic> response){
        //controllo l'id della transazione e l'id della casa che lo manda
        //se all'id della casa corrisponde già quel id_transazione, viene scartato, perchè sarebbe un valore doppio

        //se l'id_transazione è giusto, lo inserisco nell'array di valori da considerare per fare le statistiche della residenza

        //dopo aver inserito l'id nella lista, rispondo alla casa con un onNext riempiendo i valori corrispondenti
    }

    @Override
    public void presentation (Join request, StreamObserver<Join> response){
        //mando controllo se questa casa è già presente nella lista, se non lo è viene aggiunta con le relative informazioni
        //rispondo con le informazioni del nodo e aggiungendo chi è considerato amministratore

    }

    public void coordinator_election(Election request, StreamObserver<Election> response){
        //confronto l'id in arrivo con il mio id, se sono più grande, sono un candidato per essere coordinatore
        //mando il messaggio a chi è più grande del mio id che ho nella lista delle case
        //dopo un timeout se non ricevo risposte, costruisco la mia risposta eleggendomi come coordinatore
            //se ricevo risposta, inoltro quel messaggio in risposta

    }

    public void leave_network (Leave request, StreamObserver<Leave> response){
        //prendo l'id e cancello la casa corrispondente a quell'id, poi rispondo con onComplete

    }

    public void boost_request (Boost request, StreamObserver<Boost> response){
        //se non sto usando la risorsa rispondo con ok, altrimenti con wait e lo faccio mettere in coda
    }
}
