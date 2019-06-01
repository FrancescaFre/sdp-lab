package message_measurement;

import org.jetbrains.annotations.NotNull;
import simulation_src_2019.Measurement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class House implements Comparable<House> {

    public int id;
    public String ip;
    public int port;
    public ArrayList<SensorMeasurement> values;
    public int last_measurement_mean_id ;

    public House(){};

    public House (int id, String ip, int port) {
        this.id = id;
        this.port = port;
        this.ip = ip;
        values=new ArrayList<SensorMeasurement>();
    }
    public House (int id, int port) {
        this.id = id;
        this.port = port;
        this.ip = new String("localhost");
        values=new ArrayList<SensorMeasurement>();
        last_measurement_mean_id = -1;
    }


    @Override
    public int compareTo(@NotNull House o) {
        return this.id-o.id;
    }
}
