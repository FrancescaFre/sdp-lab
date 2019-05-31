package message_measurement;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class SensorMeasurement{


    public double value;
    public int id;
    public long timestamp;

    public SensorMeasurement(){}

    public SensorMeasurement (int id_v, double value_v, long time) {
        id = id_v;
        value= value_v;
        timestamp = time;
    }
}

