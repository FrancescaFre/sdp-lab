package house_app;

import House_Message.HM_outer;
import message_measurement.House;
import simulation_src_2019.Measurement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

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
    Boolean coordinator;
    Hashtable<Integer, Double> buffer = new Hashtable<Integer, Double>();
    int last_measurement_resident_to_rest = 0;
    int last_measurement_house_to_rest = 0;

    HouseCli toRest;

    public HouseNode(ArrayList<House> h, HouseCli rest_cli, House hh) {
        house = hh;

        id = Integer.toString(hh.id);
        port = hh.port;
        toRest = rest_cli;

        for (int i = 0; i < h.size(); i++) {
            house_list.put(h.get(i).id, h.get(i));
        }
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

    public void send_values() {
        //comunico i valori della casa al server

        int lastMeanRes = Integer.parseInt(res_values.get(res_values.size() - 1).getId());
        int lastMeanHouse = Integer.parseInt(house_values.get(house_values.size()-1).getId());

        //invio i valori della residenza se sono aggiornati
        if (last_measurement_house_to_rest < lastMeanRes)
            toRest.send_values(house,coordinator, res_values.get(res_values.size()-1), null);
        //invio i valori della casa se sono aggiornati
        if (last_measurement_resident_to_rest < lastMeanHouse)
            toRest.send_values(house,coordinator,null, house_values.get(house_values.size()-1));
    }

    public void print_value(double val, long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        System.out.println("<House-" + id + " : " + formatter.format(date) + "> Valore consumo " + val);
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
    public synchronized void Join(){}

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
    public void Leave(){}

    //---------------------------------------------------Quando si saluta una casa (SERVER)
    public synchronized void GoodBye(int id_h, boolean coordinator){
        if (house_list.containsKey(id_h))
            house_list.remove(id_h);

        if (coordinator)
            Election(Integer.parseInt(id)); //chiamo l'elezione con il mio nome

    }

    //---------------------------------------------------Quando si indice ad una elezione (CLIENT)
    public int startElection(int id_h){ return 9;}

    //---------------------------------------------------Quando si risponde ad una elezione (SERVER)
    public synchronized int Election(int id){

        ArrayList<Integer> index = new ArrayList<Integer>();
        for(Integer i: house_list.keySet())
            if (i > id)
                index.add(i);

        if (index.size() == 0) //non c'è nessuno più grande
            return id; //restituisco l'id della casa
        else
        {
            broadcastSimulator(index, 1);//mando i messaggi a tutti
            //zona GPRC "CLIENT" che manda una richiesta di elezione
        }

         return -1;
    }

    //---------------------------------------------------Quando TERMINA L'ELEZIONE (CLIENT)
    public synchronized void ForPresident(){}

    //---------------------------------------------------Quando chiedo l'uso del boost (CLIENT)
    public synchronized boolean Boost(){return false;}

    //---------------------------------------------------Quando rispondo per l'uso del boost (CLIENT)
    public synchronized boolean ReBoost(){return false;}


    public void broadcastSimulator(ArrayList arrayList, int i){
        switch (i){
            case 1: //elezione
                    break;
            case 2: //diffusione coordinatore
                    break;
            case 3: //diffusione statistiche
                    break;
        }
    }

}
