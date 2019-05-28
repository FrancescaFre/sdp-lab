package house_app;

import message_measurement.House;
import message_measurement.SensorValue;

import simulation_src_2019.Measurement;
import simulation_src_2019.SmartMeterSimulator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class HouseNode {

    House house;
    String id;
    int port;

    ArrayList<House> house_list=new ArrayList<House>();
    ArrayList<Measurement> house_values = new ArrayList<Measurement>();
    ArrayList<Measurement> res_values = new ArrayList<Measurement>();

    Boolean coordinator;

    String ip_server = new String("localhost");
    int port_server = 1337;

    HouseCli toRest;

    public HouseNode(ArrayList<House>h, HouseCli rest_cli, House hh)
    {
        house = hh;
        house_list = h;
        id = Integer.toString(hh.id);
        port = hh.port;
        toRest = rest_cli;
    }


    public void add_measurment(Measurement m){
        res_values.add(m);
        print_value(m.getValue(), m.getTimestamp());

        //comunico i valori della casa comunicando l'ultimo valore prodotto.
        toRest.send_values(house, coordinator, res_values.get(res_values.size()-1), house_values.get(house_values.size()-1));
    }

    public void print_value(double val, long time)
    {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        System.out.println("<House-"+ id+" : "+formatter.format(date)+"> Valore consumo "+val);
    }
}
