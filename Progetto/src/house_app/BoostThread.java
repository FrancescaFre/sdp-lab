package house_app;

import simulation_src_2019.SmartMeterSimulator;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class BoostThread implements Runnable {

    HouseNode node;
    SmartMeterSimulator simulator;

    String service = "";

    public BoostThread(HouseNode hn, SmartMeterSimulator sm, String s)
    {
        service = s;
        simulator = sm;
        node = hn;
    }

    @Override
    public void run() {

        if(service.equals("BOOST")) {
            try{
                System.err.println("----------------------------------------------- Inizio BOOST per il nodo "+node.id);
                node.setBoost(true);

                simulator.boost();
               TimeUnit.SECONDS.sleep(30);

                System.err.println("----------------------------------------------- Fine BOOST per il nodo " + node.id);
                node.setBoost(false);
            } catch (InterruptedException e){e.printStackTrace();}
        }

        if(service.equals("SLEEP")){
            //qui ha nel run la sleep

            try {
                TimeUnit.SECONDS.sleep(30);
              //  node.checkNodeAlive();
            } catch (InterruptedException e) {
                System.err.println("ERRORE");
                e.printStackTrace();
            }

        }
    }
}
