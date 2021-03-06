package serverREST;

import message_measurement.House;
import message_measurement.SensorMeasurement;
import simulation_src_2019.Measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Residence {

    //variabili
    @XmlElement(name="house_list")
    public Hashtable<Integer, House> houses;
    public ArrayList<SensorMeasurement> residence_values;

    private static Residence instance;

    private Residence()
    {
        houses = new Hashtable<Integer, House>();
        residence_values = new ArrayList<SensorMeasurement>();
    }

    //singleton
    public synchronized static Residence getInstance()
    {
        if (instance==null)
            instance=new Residence();
        return instance;
    }

    public synchronized ArrayList<House> get_house(){
        return new ArrayList<House>(houses.values());
    }

    //operazioni gestione case
    //-------------inserimento casa
    public synchronized boolean add_house_to_list (House h){
        if (!houses.containsKey(h.id))
        {
            House hh = new House(h.id, h.port);
            houses.put(hh.id, hh);
            return true;
        }
        else return false;
    }

    public synchronized boolean add_house_to_list (int id, String ip, int port)
    {
        if (!houses.containsKey(id))
        {
            House h = ip == "localhost" ? new House (id, port) : new House (id, ip, port);

            houses.put(id, h);
            return true;
        }
        else return false;
    }

    //-------------rimozione casa
    public synchronized boolean rm_house_to_list (House h){
        if (houses.containsKey(h.id))
        {
            houses.remove(h.id);
            return true;
        }
        else return false;
    }

    public synchronized boolean rm_house_to_list (int id)
    {
        if (houses.containsKey(id))
        {
            houses.remove(id);
            return true;
        }
        else return false;
    }


    //-------------statistiche case
    //GET statistica di una casa singola
    public synchronized ArrayList<SensorMeasurement> statistics (int id, int n)
    {
        if(houses.containsKey(id))
        {
            ArrayList <SensorMeasurement> values = houses.get(id).values;
                return n < values.size() ? new ArrayList<SensorMeasurement>(values.subList(values.size()-n, values.size())) : values;

            /*
            if(n<values.size())
                return new ArrayList<Measuremente>(values.subList(values.size()-n, values.size()));
            else
                return values; //dato che n è maggiore di size, restituisco quello che ho e basta.*/
        }
        return null;
    }

    //GET statistica condominiale
    public synchronized ArrayList<SensorMeasurement> statistics (int n)
    {
        return n < residence_values.size() ? new ArrayList<SensorMeasurement>(residence_values.subList(residence_values.size()-n, residence_values.size())) : residence_values;
    }

    //PUT statistica di una casa
    public synchronized boolean updateStatistics(int id, SensorMeasurement value)
    {
        if(houses.containsKey(id))
        {
            houses.get(id).values.add(value);
            return true;
        }
        return false;
    }

    //PUT statistica globale del condomionio
    public synchronized boolean updateStatistics(SensorMeasurement value)
    {
        residence_values.add(value);
        return true;
    }

    public double [] get_mean_stdDeviation(int id, int n){
        ArrayList<SensorMeasurement> stat = statistics(id, n);
        if (stat != null)
        {
            return mean_stdDeviation(stat);
        }
        else return null;
    }

    public double [] get_mean_stdDeviation(int n)
    {
        return mean_stdDeviation(statistics(n));
    }


    private double[] mean_stdDeviation (ArrayList<SensorMeasurement> stat)
    {
        if (stat.size() == 0)
            return new double[] {0,0};

        double[] res = new double[2];
        //Calcolo media
        for (SensorMeasurement s : stat)
            res[0] += s.value;
        res[0] = res[0]/stat.size();

        //Calcolo deviazione standard
        for (SensorMeasurement s : stat)
            res[1] += (s.value-res[0])*(s.value-res[0]);
        res[1] = res[1] /(stat.size());
        res[1] = Math.sqrt(res[1]);

        return res;
    }
}
