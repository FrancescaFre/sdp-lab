package house_app;

import House_Message.HM_outer.*;
import House_Message.HouseServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import jdk.javadoc.internal.tool.Start;
import message_measurement.House;
import message_measurement.SensorMeasurement;
import simulation_src_2019.Measurement;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;


public class HouseNode {

    //-------------------Var
    House house;
    String id;
    int port;

    ArrayList<Measurement> house_values = new ArrayList<Measurement>();
    ArrayList<Measurement> res_values = new ArrayList<Measurement>();
    final Hashtable<Integer, House> house_list = new Hashtable<Integer, House>();
    //final Hashtable<Integer, ManagedChannel> channel_list = new Hashtable<Integer, ManagedChannel>();

    int coordinator_id;

    //------------------Cose da coordinatore
    Boolean coordinator = false;
    Hashtable<Integer, Double> buffer = new Hashtable<Integer, Double>();
    int last_measurement_resident_to_rest = -1;
    int last_measurement_house_to_rest = -1;

    HouseCli toRest;

    public HouseNode(ArrayList<House> h, HouseCli rest_cli, House hh) {
        house = hh;

        id = Integer.toString(hh.id);
        port = hh.port;
        toRest = rest_cli;

        coordinator_id = -1;

        for (int i = 0; i < h.size(); i++) {
            house_list.put(h.get(i).id, h.get(i));
        }
        //this startjoin
    }

    public void add_house_to_list(House h) {
        if (house_list.containsKey(h.id))
            return;
        house_list.put(h.id, h);
    }

    public void rm_house_from_list(House h) {
        if (!house_list.containsKey(h.id))
            return;
        house_list.remove(h.id);
    }

    public void add_measurment(Measurement m) {
        house_values.add(m);
        print_value(m.getValue(), m.getTimestamp());

        SendStat();
        send_values();
    }

//-------------------- INVIO DEI VALORI
    public void send_values() {
        //comunico i valori della casa al server

        int lastMeanRes = last_measurement_resident_to_rest;
        if (res_values.size() >= 1)
                lastMeanRes = Integer.parseInt(res_values.get(res_values.size() - 1).getId());

        int lastMeanHouse = Integer.parseInt(house_values.get(house_values.size()-1).getId());

        Measurement m_to_send;

        // invio i valori della residenza se sono aggiornati
        if (last_measurement_house_to_rest < lastMeanRes) {
            m_to_send = res_values.get(res_values.size()-1);
            toRest.send_values(house,coordinator, new SensorMeasurement(Integer.parseInt(m_to_send.getId()), m_to_send.getValue(), m_to_send.getTimestamp()), null);
            last_measurement_resident_to_rest = lastMeanRes;
        }

        //invio i valori della casa se sono aggiornati
        if (last_measurement_resident_to_rest < lastMeanHouse){
            m_to_send = house_values.get(house_values.size()-1);
            toRest.send_values(house,coordinator,null, new SensorMeasurement(Integer.parseInt(m_to_send.getId()), m_to_send.getValue(), m_to_send.getTimestamp()));
            last_measurement_house_to_rest = lastMeanHouse;
        }
    }

    public void print_value(double val, long time) {
        Timestamp ts = new Timestamp(time);
        DateFormat formatter = new SimpleDateFormat("dd/mm/yy HH:mm:ss");

        System.out.println("<House-" + id + " : " + formatter.format(ts) + " : "+last_measurement_house_to_rest+1 +"> Valore consumo " + val +" Watt");
    }

//-----------------------------------------------------------------------------------
//                        ROBA per GRPC
//-----------------------------------------------------------------------------------

    //---------------------------------------------------Quando invio una misurazione (CLIENT)
    public synchronized void SendStat(){

    }

    //---------------------------------------------------Quando si riceve una misurazione dagli altri (SERVER)
    public synchronized Integer MeanStat_SendStat(int house_id, int measure_id, Double measure) {

        int house_list_size;

        //se l'ultima misurazione mandata al serverRest è < dell'attuale, significa che quella attuale è aggiornata
        if (house_list.containsKey(house_id)) { //se sto valutando la misurazione di una casa che non è più connessa alla rete, ignoro la misurazione

            if (house_list.get(house_id).last_measurement_mean_id < measure_id) {
                house_list.get(house_id).last_measurement_mean_id = measure_id; //aggiorno l'id
                buffer.put(house_id, measure);
            }
        } else buffer.remove(house_id); //se sto considerando il valore di una casa non più connessa, la rimuovo


        if (buffer.size() >= house_list.size()) { //ho le misurazioni di tutte le case, media e inivio.
            Double mean = 0d;
            for (Double d : buffer.values())
                mean += d;
            mean /= buffer.size();
            buffer.clear();
            last_measurement_resident_to_rest++;
            res_values.add(new Measurement((last_measurement_resident_to_rest + ""), null, mean, System.currentTimeMillis()));

            //infine rispondo al nodo
            send_values();
            return last_measurement_resident_to_rest;
        }
        return null;
    }

    //---------------------------------------------------Quando ci si presenta (CLIENT)
    public synchronized void Join(){
        Join.Builder join = Join.newBuilder();
        join.setType("JOIN");
        join.setHouseId(Integer.parseInt(this.id));
        join.setPort(this.port);
        join.setIp("localhost");
        join.setCoordinator(-1);
        join.setReply(false);

        Join join_message = join.build();

        ArrayList<Integer> maybe_coordinator = new ArrayList<Integer>();

        StreamObserver<Join> so_join = new StreamObserver<Join>() {
            @Override
            public void onNext(Join join_reply) {
                if(!house_list.containsKey(join_reply.getHouseId())){
                    //se non c'è una casa nella lista che mi risponde, perchè magari si è aggiunta quando stavo facendo la join
                    House new_h = new House (join_reply.getHouseId(), join_reply.getPort());
                    house_list.put(join_reply.getHouseId(), new_h);
                }

                maybe_coordinator.add(join_reply.getCoordinator());

                if (maybe_coordinator.size() >= house_list.size()*1.70 && maybe_coordinator.size()!=0){
                    coordinator_id = set_coordinator(maybe_coordinator);

                    if (coordinator_id == -1) //se nessuno ha un coordinatore, allora si elegge
                        Election(Integer.parseInt(id));
                    if (coordinator_id == Integer.parseInt(id)) //se il coordinatore sono io, mi setto a true
                        coordinator = true;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("ERROR - JOIN-CLIENT"+throwable.getMessage() );
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
            }
        };

        for (House h : house_list.values())
            new Thread(new HouseBroadcast(h.port, join_message, so_join)).start();
    }

    private int set_coordinator(ArrayList<Integer> arr){
        //se almeno il 70% dei nodi mi risponde, setto il coordinatore in base ai loro dati

            int[] occ = new int[house_list.size()];
            //conto occorrenze
            for (Integer i : arr){
                occ[i]++;
            }

            //prendo l'id con più occorrenze
            int max = -1;
            for (Integer i : occ){
                if (max < i)
                    max = i;
            }

            return max;
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

    //---------------------------------------------------Quando viene segnalata l'ustica (CLIENT)
    public void Leave(){
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
            Election(Integer.parseInt(id)); //chiamo l'elezione con il mio nome
    }

    //---------------------------------------------------Quando si indice ad una elezione (CLIENT)
    public int startElection(int id_h){

        ArrayList<House> index = new ArrayList<House>();
        for(House i: house_list.values())
            if (i.id > id_h) //id_h è colui che ha chiamato l'elezione, cioè me stesso.
                index.add(i);

        if (index.size() == 0) //non c'è nessuno più grande
        {
            ForPresident(); //divento coordinatore perchè so già di essere adatto, quindi escludo potenziali nuovi arrivati
            return coordinator_id; //modificato da forPresident
        }

        else
        {
            Election.Builder election = Election.newBuilder();
            election.setType("ELECTION");
            election.setHouseId(Integer.parseInt(id));
            election.setReply(false);
            Election election_message = election.build();

            StreamObserver<Election> so_election = new StreamObserver<Election>() {
                @Override
                public void onNext(Election election_reply) {
                    if (election_reply.getReply())  //se ho una risposta, questa conterrà l'id del coordinatore
                        coordinator_id = election.getHouseId() != -1 ? election_reply.getHouseId() : -1;
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("ERROR - ELECTION-CLIENT"+throwable.getMessage() );
                    throwable.printStackTrace();
                }

                @Override
                public void onCompleted() {  }
            };

            for (House i:index)
                new Thread(new HouseBroadcast(i.port, election_message, so_election)).start();

        }
    }

    //---------------------------------------------------Quando si risponde ad una elezione (SERVER)
    public synchronized int Election(int id_h){
           return startElection(Integer.parseInt(id));

    }

    //---------------------------------------------------Quando TERMINA L'ELEZIONE (CLIENT)
    public synchronized void ForPresident(){

        coordinator_id = Integer.parseInt(id);
        coordinator = true;

        President.Builder president = President.newBuilder();
        president.setType("PRESIDENT");
        president.setHouseId(coordinator_id);
        President president_message = president.build();

        StreamObserver<President> so_president = new StreamObserver<President>() {
            @Override
            public void onNext(President president) { }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("ERROR - PRESIDENT-CLIENT"+throwable.getMessage() );
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() { }
        };

        for (House h : house_list.values())
            new Thread(new HouseBroadcast(h.port, president_message, so_president)).start();

    }

    //---------------------------------------------------Quando chiedo l'uso del boost (CLIENT)
    public synchronized boolean Boost(){return false;}

    //---------------------------------------------------Quando rispondo per l'uso del boost (CLIENT)
    public synchronized boolean ReBoost(){return false;}



}
