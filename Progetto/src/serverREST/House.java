package serverREST;

import message_measurement.SensorValue;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class House {

    public int id;
    public String ip;
    public int port;
    public ArrayList<SensorValue> values;

    public House(){};

    public House (int id, String ip, int port) {
        this.id = id;
        this.port = port;
        this.ip = ip;
        values=new ArrayList<SensorValue>();
    }
    public House (int id, int port) {
        this.id = id;
        this.port = port;
        this.ip = new String("localhost");
        values=new ArrayList<SensorValue>();
    }

}
