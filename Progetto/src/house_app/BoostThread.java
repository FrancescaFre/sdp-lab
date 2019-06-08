package house_app;

import simulation_src_2019.SmartMeterSimulator;

import java.util.concurrent.TimeUnit;

public class BoostThread implements Runnable {

    HouseNode node;
    SmartMeterSimulator simulator;
    public BoostThread(HouseNode hn, SmartMeterSimulator sm)
    {
        simulator = sm;
        node = hn;
    }

    @Override
    public void run() {
        try{
            System.err.println("----------------------------------------------- Inizio BOOST per il nodo "+node.id);
            node.setBoost(true);

            simulator.boost();
            TimeUnit.SECONDS.sleep(30);

            System.err.println("----------------------------------------------- FINE BOOST per il nodo " + node.id);
            node.setBoost(false);
        } catch (InterruptedException e){e.printStackTrace();}
    }
}
