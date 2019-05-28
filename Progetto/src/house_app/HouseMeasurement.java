package house_app;

import simulation_src_2019.Buffer;
import simulation_src_2019.Measurement;

import java.util.ArrayList;

public class HouseMeasurement implements Buffer {

    double mean;
    int mean_index = 0;

    int sliding_window_size = 24;
    int overlap = 50;
    int mod_size = (sliding_window_size * overlap)/100;

    ArrayList<Measurement> recorded_values = new ArrayList<Measurement>();
    HouseNode house;

    public HouseMeasurement(HouseNode h){
        house = h;
    }

    @Override
    public void addMeasurement(Measurement m) {
        recorded_values.add(m);
        house.print_value(m.getValue(), m.getTimestamp());

        /*
        //se ho raccolto le prime 24 misurazioni E ho raggiunto una dimensione di modulo 12, cioè ho raggiunto il numero giusto per le misurazioni
        //if (recorded_values.size() >= sliding_window_size && recorded_values.size() % mod_size==0)
        if (recorded_values.size() >= 24 && recorded_values.size() % 12==0)
            elaboration();
        */

        if (recorded_values.size() == 24)
            elaboration();
    }

    private void elaboration(){
  /*
        //faccio la media degli ultimi 24 valori con 12 valori della media precedente. Posso usare .size nell'ultimo indice, perchè nel sublist non è incluso
        //ArrayList<Measurement> subList = new ArrayList<Measurement>(recorded_values.subList(recorded_values.size()-sliding_window_size, recorded_values.size()));
        ArrayList<Measurement> subList = new ArrayList<Measurement>(recorded_values.subList(recorded_values.size()-24, recorded_values.size())); */
        ArrayList<Measurement> subList = new ArrayList<Measurement>(recorded_values);

        //ricreo recorded value prendendo gli ultimi 12 valori, ma solo dopo aver salvato la sublist così è già pronto per essere ricaricato
        recorded_values = new ArrayList<Measurement>(recorded_values.subList(recorded_values.size()-12, recorded_values.size()));

        //faccio la media
        mean = 0;
        for (Measurement m : subList)
            mean+=m.getValue();
        mean = mean / subList.size();

        //creo una nuova misura composta dall'indice delle medie di questo sensore, il valore calcolato, e per timestamp, l'ultimo timestamp rilevato
        house.add_measurment(new Measurement(Integer.toString(mean_index), null, mean, subList.get(subList.size()-1).getTimestamp()));
        mean_index++;
    }
}