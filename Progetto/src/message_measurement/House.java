package message_measurement;

import message_measurement.SensorValue;
import org.jetbrains.annotations.NotNull;
import simulation_src_2019.Measurement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class House implements Comparable<House> {

    public int id;
    public String ip;
    public int port;
    public ArrayList<Measurement> values;

    public House(){};

    public House (int id, String ip, int port) {
        this.id = id;
        this.port = port;
        this.ip = ip;
        values=new ArrayList<Measurement>();
    }
    public House (int id, int port) {
        this.id = id;
        this.port = port;
        this.ip = new String("localhost");
        values=new ArrayList<Measurement>();
    }


    @Override
    public int compareTo(@NotNull House o) {
        return this.id-o.id;
    }
}
