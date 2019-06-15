package house_app;

import com.sun.xml.bind.v2.runtime.Coordinator;
import message_measurement.House;
import simulation_src_2019.SmartMeterSimulator;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HouseAPP {
    static Scanner input;
    static HouseCli toREST =null;
    static SmartMeterSimulator sm = null;
    static House house= null;
    static HouseNode node = null;
    static boolean b = false;
    static HouseMeasurement measurement= null;

    public static void main (String[] argv){

        //VARIABILI IMPORTANTI
        String id;
        int port;

        String ip_server = new String("localhost");
        int port_server = 1337;

        //-------------------------------------------------Creazione della casa
        input = new Scanner(System.in);
        b = false;
        Random random = new Random();
        do {
      System.out.println(
          "Inserisci un numero identificativo per la casa inferiore a 16.383 ");
            id = input.nextLine();
            if (id.matches("^\\d+$") && Integer.parseInt(id) < 16383)
                b = true;
            else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero valido");

        }while(!b);

        port = Integer.parseInt(id)+49152;
        //numero di porta invece è randomico
        // port = random.nextInt(65535-49152 +1)+49152 ;
        System.out.println("Casa creata\nID: "+id+"\nAddr: localhost:"+port);

        //------------------------------------------------Creazione del client e richiesta di aggiunta al server
        //HouseCli toREST = new HouseCli(ip_server, port_server);
        toREST = new HouseCli(ip_server, port_server);

        ArrayList<House> house_list=new ArrayList<House>();
        house = new House(Integer.parseInt(id), port);

        house_list = toREST.add_to_server(house);
        if (house_list== null)  //se non ritorna la lista delle case significa che qualcosa è andato storto e chiudo la connessione
        {
            toREST.close();
            input.close();
            System.err.println("Non è stato possibile aggiungere la casa al server amministratore. \nArresto del programma in corso");
            return;
        }

        //-------------------------------------------------Settaggio delle ultime istanze

        //creo il nodo della casa
        node = new HouseNode(house_list, toREST, house);

        //creo il misuratore
        measurement = new HouseMeasurement(node);

        //avvio il simulatore
        sm = new SmartMeterSimulator(measurement);

        node.setSimulator(sm);

        //-------------------------------------------------Inizio dell'interfaccia utente
        b=false;
        String ris;
        while(!b)
        {
            System.out.println(
                    "1 - Rimuoviti dalla lista delle case dal server e chiudi l'applicazione\n"+
                    "2 - Richiesta BOOST"

                    +"\n\n---- FEATURE ----"
                    +"\n3 - Leggi il coordinatore e vedi se sei coordinatore"
                    +"\n4 - Fai partire l'elezione"
                    +" "

            );

            ris = null;
            try{
                ris = input.nextLine();
            }catch (IllegalStateException is){ b = true; ris="5"; return;}

            if(ris.matches("^\\d+$") && !b)
                switch (Integer.parseInt(ris))
                {
                    case 1://chiudi la sessione - Leave
                            close();
                            break;
                    case 2: //boost
                            node.boost();
                            break;
                    case 3://leggi il coordinatore
                            System.out.println("Questa casa con id <" +node.id+ "> e ha come coordinatore: <"+node.coordinator_id+">");
                            break;
                    case 4: //partire un'elezione
                            node.startElection();
                            break;
                    case 5: break;
                }

        };
        System.err.println("\nChiusura applicazione, attendere 30 secondi ...");
        //fine main
    }

    public static void close(){

        System.err.println("Inizio procedura per la chiusura controllata dell' applicazione");

        sm.stopMeGently();

        node.leave();

        toREST.close();

        b=true;

        try {
          node.server.server.shutdown().awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("ERRORE NELLO SHUTDOWN DEL SERVER");
            //node.server.server.shutdownNow();
        }

        input.close();
    }
}
