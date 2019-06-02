package house_app;

import House_Message.HM_outer.*;
import House_Message.HouseServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import message_measurement.House;
import message_measurement.SensorMeasurement;
import simulation_src_2019.Measurement;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class HouseNode {

    //-------------------Var
    House house;
    String id;
    int port;

    ArrayList<Measurement> house_values = new ArrayList<Measurement>();
    ArrayList<Measurement> res_values = new ArrayList<Measurement>();
    final Hashtable<Integer, House> house_list = new Hashtable<Integer, House>();

    int coordinator_id;

    //------------------Cose da coordinatore
    Boolean coordinator = false;
    Hashtable<Integer, Double> buffer = new Hashtable<Integer, Double>();
    int last_measurement_resident_to_rest = -1;
    int last_measurement_house_to_rest = -1;

    HouseCli toRest;

    //-----------------Boost
    //Costruttore
    public HouseNode(ArrayList<House> h, HouseCli rest_cli, House hh) {
        house = hh;

        id = Integer.toString(hh.id);
        port = hh.port;
        toRest = rest_cli;

        coordinator_id = -1;

        for (int i = 0; i < h.size(); i++) {
            house_list.put(h.get(i).id, h.get(i));
        }
        //start lato server gprc del nodo
        new Thread(new HouseServer(this, port)).start();

        Join(); //grpc di presentazione
    }

    //Add Measurement
    public void add_measurment(Measurement m) {
        house_values.add(m);
        send_house_values();
    }

    // Stampa dei valori
    public void print_value(double val, long time, boolean residence_val) {
        Timestamp ts = new Timestamp(time);
        DateFormat formatter = new SimpleDateFormat("dd/mm/yy HH:mm:ss");
        if(residence_val)
            System.out.println("<Residence : " + formatter.format(ts) + "> Valore consumo " + val +" Watt");
        else
            System.out.println("<House-" + id + " : " + formatter.format(ts) + " : "+(last_measurement_house_to_rest) +"> Valore consumo " + val +" Watt");
    }

    //-------------------- INVIO DEI VALORI al server REST
    public void send_house_values() {
        //comunico i valori della casa al server rest
        int lastMeanHouse = Integer.parseInt(house_values.get(house_values.size()-1).getId());

        Measurement m_to_send;
        //invio i valori della casa se sono aggiornati
        if (last_measurement_house_to_rest < lastMeanHouse){
            m_to_send = house_values.get(house_values.size()-1);
            toRest.send_values(house,coordinator,null, new SensorMeasurement(Integer.parseInt(m_to_send.getId()), m_to_send.getValue(), m_to_send.getTimestamp()));
            last_measurement_house_to_rest = lastMeanHouse;

            print_value(m_to_send.getValue(), m_to_send.getTimestamp(), false);
        }

    // se non c'è il coordinatore, fermo il sendStat
        if (coordinator_id != -1) // se esiste il coordinatore
        {
            SendStat();
        }else{
            System.err.println("Chiamata in send_house per mancato coordinator ");
            startElection(Integer.parseInt(id));
            SendStat();
        }
    }

    public void send_res_values(){
        //se ho almeno un valore nella residenza da inviare
        //prendo l'ultimo id generato
        int lastMeanRes = Integer.parseInt(res_values.get(res_values.size()-1).getId());

        Measurement m_to_send;
        //confronto l'ultimo id generato nella lista dei valori con la variabile che si memorizza l'ultimo valore inviato
        // invio i valori della residenza se sono aggiornati
        if (last_measurement_resident_to_rest < lastMeanRes && coordinator) { //se sono coordinatore invio info
            m_to_send = res_values.get(res_values.size()-1);
            toRest.send_values(house,coordinator, new SensorMeasurement(Integer.parseInt(m_to_send.getId()), m_to_send.getValue(), m_to_send.getTimestamp()), null);
            last_measurement_resident_to_rest = lastMeanRes;
            diffuseStat();
        }
    }

//-----------------------------------------------------------------------------------
//                        ROBA per GRPC
//-----------------------------------------------------------------------------------

    //---------------------------------------------------Quando ci si presenta (CLIENT)
    public synchronized void Join(){

        if (house_list.size() == 1)
        {
            //se la lista di case è composto solo da un nodo (cioè me stesso) si autoelegge come coordinatore
            imThePresident();
            return;
        }

        System.err.println("START JOIN ------");
        Join.Builder join = Join.newBuilder();
        join.setType("JOIN");
        join.setHouseId(Integer.parseInt(this.id));
        join.setPort(this.port);
        join.setIp("localhost");
        join.setCoordinator(-1);
        join.setReply(false);

        Join join_message = join.build();


        ArrayList<Integer> maybe_coordinator = new ArrayList<Integer>();
        //struttura dati che uso per salvare i coordinatori che ricevo dalle altre case
        //considerando che alla join contribuiscono molti nodi, anche quelli che sono appena entrati senza un coordinatore

        StreamObserver<Join> so_join = new StreamObserver<Join>() {
            @Override
            public void onNext(Join join_reply) {
                if(!house_list.containsKey(join_reply.getHouseId())){
                    //se non c'è una casa nella lista che mi risponde, perchè magari si è aggiunta quando stavo facendo la join
                    House new_h = new House (join_reply.getHouseId(), join_reply.getPort());
                    house_list.put(join_reply.getHouseId(), new_h);
                }

                if (join_reply.getHouseId() != Integer.parseInt(id))
                    maybe_coordinator.add(join_reply.getCoordinator());

                //quando il maybe_coordinator contiene almeno il l'80% delle case esistenti nella rete
                //if (maybe_coordinator.size() >= house_list.size()*(0.8) && maybe_coordinator.size()!=0){

                //-1 perchè escludo me stesso
                if (maybe_coordinator.size() >= (house_list.size()-1) && maybe_coordinator.size()!=0){
                    coordinator_id = set_coordinator(maybe_coordinator);

                    if (coordinator_id == -1) //se nessuno ha un coordinatore, allora si elegge
                        Election(Integer.parseInt(id));
                    if (coordinator_id == Integer.parseInt(id))
                        imThePresident();     //se il coordinatore sono io, mi setto a true
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("----------");
                System.err.println("ERROR - JOIN-CLIENT "+throwable.getMessage() );
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {}
        };

        for (House h : house_list.values())
            new Thread(new HouseBroadcast(h.port, join_message, so_join)).start();
    }

    private int set_coordinator(ArrayList<Integer> arr){
      Hashtable<Integer, Integer> hashtable = new Hashtable<Integer, Integer>();
      for (Integer i : arr)
          hashtable.put(i, 0);
      for (Integer i : arr)
          hashtable.put(i, hashtable.get(i) + 1);

      int max = -1;
      int index = -1;
      for (Integer i: hashtable.keySet())
          if (hashtable.get(i) > max)
          {
              max = hashtable.get(i);
              index = i;
          }

      return index;
    }

    //---------------------------------------------------Quando si accoglie una nuova casa (SERVER)
    public synchronized int Welcome(int id_h, int port){
        if (!house_list.containsKey(id_h))
        {   //Se non la casa, l'aggiungo
            House h = new House(id_h, port);
            house_list.put(id_h, h);
        }
        return coordinator_id;
    }

  // ---------------------------------------------------Quando invio una misurazione (CLIENT)
    public synchronized void SendStat() {
        if (house_values.size() == 0) return;

        ArrayList<Measurement> m_to_remove = new ArrayList<Measurement>();

        for (Measurement m : house_values) {

          Statistic.Builder stat = Statistic.newBuilder();
          stat.setType("STAT");
          stat.setValue(m.getValue());
          stat.setTimestamp(m.getTimestamp());
          stat.setMeasurementId(Integer.parseInt(m.getId()));
          stat.setHouseId(Integer.parseInt(id));

          Statistic stat_message = stat.build();

          final int[] i = new int[1];

          ArrayList<Integer> list = new ArrayList<Integer>(house_list.keySet());
      StreamObserver<Statistic> so_stat =
          new StreamObserver<Statistic>() {
            @Override
            public void onNext(Statistic statistic_reply) {
              if (list.contains(statistic_reply.getHouseId())) {
                synchronized (HouseNode.this) {
                  i[0]++;
                  list.remove(list.indexOf(statistic_reply.getHouseId()));
                }
              }
            }

            @Override
            public void onError(Throwable throwable) {
              StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;

              System.out.println("----------");
              System.err.println("\nERROR - SEND_STAT-CLIENT: " + throwable.getMessage());
              // if (throwable.getMessage().matches("UNAVAILABLE"))
              if (statusRuntimeException.getStatus().equals(Status.UNAVAILABLE))
                synchronized (HouseNode.this) { i[0]++; }
            }

            @Override
            public void onCompleted() {
              // se ho un errore di connessione e la somma di tutti i messaggi è uguale
              if (checkMiss(i[0], list))
              // se è a true, significa che è stato rimosso anche il coordinatore ed è da
              // inviare nuovamente la media
              {
                System.err.println(" ---------------- Dentro checkMiss");
                startElection(Integer.parseInt(id)); // quindi si elegge un nuovo coordinatore
                SendStat(); // si manda la media
              }
            }
          };

          for (House h : house_list.values())
            new Thread(new HouseBroadcast(h.port, stat_message, so_stat)).start();

        m_to_remove.add(m); //dopo averla usata, si rimuove
        }

        for (int j=0; j<m_to_remove.size(); j++){
            house_values.remove(m_to_remove.get(j));
        }
    }


    /*
    * Aggiorno la lista delle case e controllo se tra i messaggi mancanti ci sia anche l'amministratore
    *
    * Nel OnNext e OnError ho incrementato un indice per sapere quante sono le risposte totali (andate male e bene)
    * Nell'OnNext invece scarto le risposte andate bene
    *
    * Nel checkMiss controllo questi valori, se ho come indice un numero == al numero di messaggi nviati (house_list.size)
    * e, ma la lista.size> 1, cioè ci sono case non scartate, vuol dire che queste sono cadute prima del previsto
    *
    * Considerando che l'invio di statistiche è l'azione più frequente di tutte, penso che sia sufficente fare solo in questa chiamata la pulizia di nodi deceduti in modo improvviso
    * */

    public synchronized boolean checkMiss(int all_resp, ArrayList<Integer> list){
        boolean return_bool = false;

        if (list.size() > 0)

        if (all_resp == house_list.size() && list.size() > 0)
            for (Integer i:list)    //per ogni indice rimasto controllo se è raggiungibile, se no, si elimina dalla lista
            {
                //Sincrona la prova di connessione

                final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", house_list.get(i).port).usePlaintext(true).build();
                HouseServiceGrpc.HouseServiceBlockingStub stub = HouseServiceGrpc.newBlockingStub(channel);

                Join join_m = null;
                try
                {
                    join_m= stub.checkConnection(join_m);
                }
                catch (StatusRuntimeException e){
                    System.err.println("CASA "+house_list.get(i).id+" NON RAGGIUNGIBILE - Rimozione in corso");
                    if(i == coordinator_id) //se l'id che ho provato è il coordinatore, nuova elezione
                        return_bool = true;
                    toRest.rm_from_server(house_list.get(i)); //mi occupo anche di segnalare al server rest che ci sono case rimosse
                    house_list.remove(i);
                }
                if (join_m!=null) //ha successo la comunicazione
                    System.err.println("Si era perso un messaggio con casa "+house_list.get(i).id+", la casa è ancora attiva");

                channel.shutdown();
            }
        return return_bool;
    }


    //---------------------------------------------------Quando si riceve una misurazione dagli altri (SERVER)
    public synchronized Integer MeanStat_SendStat(int house_id, int measure_id, Double measure) {

        //se l'ultima misurazione mandata al serverRest è < dell'attuale, significa che quella attuale è aggiornata
        if (house_list.containsKey(house_id))
        {
            if (house_list.get(house_id).last_measurement_mean_id <= measure_id)
            {
                house_list.get(house_id).last_measurement_mean_id = measure_id; //aggiorno l'id
                buffer.put(house_id, measure);
            }
        } else buffer.remove(house_id); //se sto considerando il valore di una casa non più connessa, la rimuovo

        //ho le misurazioni di tutte le case, faccio la media e inivio.
        if (buffer.size() >= house_list.size()) {

            Double mean = 0d;
          for (Double d : buffer.values())
            mean += d;

            mean /= buffer.size();
            buffer.clear();

            res_values.add(new Measurement(((last_measurement_resident_to_rest + 1)+ ""), null, mean, System.currentTimeMillis()));

            //infine mando al rest i dati della media
            send_res_values();
            return last_measurement_resident_to_rest;
        }
        return null; //non ho abbastanza valori per fare la media
    }

    public void diffuseStat(){
        Measurement m = res_values.get(res_values.size()-1);
        Statistic.Builder diffuse_stat = Statistic.newBuilder();
        diffuse_stat.setType("STAT");
        diffuse_stat.setValue(m.getValue());
        diffuse_stat.setTimestamp(m.getTimestamp());
        diffuse_stat.setMeasurementId(Integer.parseInt(m.getId()));
        diffuse_stat.setHouseId(Integer.parseInt(id));
        diffuse_stat.setReply(true);

        Statistic diffuse_stat_message = diffuse_stat.build();

        StreamObserver<Statistic> so_diffuse = new StreamObserver<Statistic>() {
            @Override
            public void onNext(Statistic statistic) {   }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("\nERROR - SEND_DIFFUSE_STAT-CLIENT: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {

            }
        };
        for (House h : house_list.values())
            new Thread(new HouseBroadcast(h.port, diffuse_stat_message, so_diffuse)).start();

    }

    //---------------------------------------------------Quando viene segnalata l'ustica (CLIENT)
    public void Leave(){

        toRest.rm_from_server(house); //comunico al server rest che la casa abbandona la rete

        Leave.Builder leave = Leave.newBuilder();
        leave.setType("LEAVE");
        leave.setId(Integer.parseInt(id));
        leave.setCoordinator(coordinator);

        Leave leave_message = leave.build();

        StreamObserver<Leave> so_leave = new StreamObserver<Leave>() {
            @Override
            public void onNext(Leave leave) { }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("ERROR - LEAVE-CLIENT"+throwable.getMessage() );
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() { }
        };

        for (House h : house_list.values())
            new Thread(new HouseBroadcast(h.port, leave_message, so_leave)).start();
    }

    //---------------------------------------------------Quando si saluta una casa (SERVER)
    public synchronized void GoodBye(int id_h, boolean coordinator){
        if (house_list.containsKey(id_h))
            house_list.remove(id_h);

        if (coordinator)
            startElection(Integer.parseInt(id)); //chiamo l'elezione con il mio nome
    }

    //---------------------------------------------------Quando si indice ad una elezione (CLIENT)
    public void startElection(int id_h){

        System.err.println("START ELECTION");
        //tutti i nodi che fanno un'elezione inizializzano i valori del coordinatore
        coordinator_id = -1;
        coordinator = false;

        ArrayList<House> index = new ArrayList<House>();
        for(House i: house_list.values())
            if (i.id > Integer.parseInt(id)) //il nodo memorizza chi è più grande
                index.add(i);

        System.err.println("dimensione di index "+index.size());

        if (index.size() == 0) //non c'è nessuno più grande
        {
            imThePresident(); //divento coordinatore perchè so già di essere adatto, quindi escludo potenziali nuovi arrivati
        }
        else
        {//mando messaggi per eleggere
            Election.Builder election = Election.newBuilder();
            election.setType("ELECTION");
            election.setHouseId(Integer.parseInt(id));
            election.setReply(false);
            Election election_message = election.build();

            final int[] i = {0};
            StreamObserver<Election> so_election = new StreamObserver<Election>() {
                @Override
                public void onNext(Election election_reply) {
//
                }

                @Override
                public void onError(Throwable throwable) {
                    StatusRuntimeException statrun = (StatusRuntimeException) throwable;
                    System.err.println("ERROR - ELECTION-CLIENT"+statrun.getStatus() );
                //devo gestire la situazione in cui ho n nodi più grandi di me e nessuno risponde
                    if (statrun.getStatus().equals(Status.UNAVAILABLE))
                      synchronized (HouseNode.this){ i[0]++; }

                    if(i[0]==index.size())//nessuno ha risposto
                        imThePresident();
                }

                @Override
                public void onCompleted() {  }
            };

      for (House h : index)
        new Thread(new HouseBroadcast(h.port, election_message, so_election)).start();

        }
    }

    //---------------------------------------------------Quando si risponde ad una elezione (SERVER)
    public void Election(int id_h){
            startElection(Integer.parseInt(id));

    }

   //---------------------------------------------------Quando TERMINA L'ELEZIONE (CLIENT)
    public synchronized void imThePresident(){
        System.out.println("SONO IL PRESIDENTE");

        coordinator_id = Integer.parseInt(id);
        coordinator = true;

        Election.Builder president = Election.newBuilder();
        president.setType("PRESIDENT");
        president.setHouseId(coordinator_id);
        Election president_message = president.build();

        StreamObserver<Election> so_president = new StreamObserver<Election>() {
            @Override
            public void onNext(Election president) { }

            @Override
            public void onError(Throwable throwable) {
             //   if(throwable.getMessage().toUpperCase().matches("(.*)without a response(.*)"))
                System.out.println("Non ci sono case con cui comunicare");
                System.err.println("ERROR - PRESIDENT-CLIENT "+throwable.getMessage() );
            }

            @Override
            public void onCompleted() { }
        };

        if(house_list.size()>0)
            for (House h : house_list.values())
                new Thread(new HouseBroadcast(h.port, president_message, so_president)).start();

    }

    //---------------------------------------------------Quando chiedo l'uso del boost (CLIENT)
    public synchronized boolean Boost(){return false;}

    //---------------------------------------------------Quando rispondo per l'uso del boost (CLIENT)
    public synchronized boolean ReBoost(){return false;}



}
