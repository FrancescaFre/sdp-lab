package house_app;

import message_measurement.House;
import simulation_src_2019.Buffer;
import simulation_src_2019.Measurement;

import java.util.ArrayList;

public class HouseMeasurement implements Buffer {

    ArrayList<Measurement> recorded_values = new ArrayList<Measurement>();
    HouseNode house;
    public HouseMeasurement(HouseNode h){
        house = h;
    }

    @Override
    public void addMeasurement(Measurement m) {
        recorded_values.add(m);
        house.print_value(m.getValue(), house, m.getTimestamp());
    }



}