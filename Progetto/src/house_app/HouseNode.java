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

    String id;
    int port;

    ArrayList<House> house_list=new ArrayList<House>();
    ArrayList<Measurement> house_values = new ArrayList<Measurement>();
    ArrayList<Measurement> res_values = new ArrayList<Measurement>();

    Boolean coordinator;

    String ip_server = new String("localhost");
    int port_server = 1337;

    HouseCli toRest;

    public HouseNode(ArrayList<House>h, String id_h, int port_h, HouseCli rest_cli)
    {
        house_list = h;
        id = id_h;
        port = port_h;
        toRest = rest_cli;
    }


    public void print_value(double val, HouseNode h, long time)
    {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        System.out.println("<House-"+ h.id+" : "+formatter.format(date)+"> Valore consumo "+val);
    }
}
