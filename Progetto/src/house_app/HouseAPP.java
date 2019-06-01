package house_app;

import com.sun.xml.bind.v2.runtime.Coordinator;
import message_measurement.House;
import simulation_src_2019.SmartMeterSimulator;

import java.util.*;

public class HouseAPP {
    static Scanner input;

    public static void main (String[] argv){
        /*la casa deve fare 3 cose

        Thread coinvolti:
        t_1: il client rest, si interfaccia con il server REST
        t_2: si occupa di prendere le misurazioni, fare lo sliding window e aggiornare i valore in house_app
        t_3: si occupa della comunicazione con gli altri peer
                t_3.1: si occupa di mandare i messaggi in broadcast

        ---Misurazioni:
        *analizzare le misurazioni per calcolare il consumo (t_2)
        *inviare le misurazioni al server rest (t_1)
        *inviare le misurazioni agli altri peer (per forza? Non basta all'amministratore?)
        *la misurazione deve avere un overlap del 50% con un buffer di 24
        * ad ogni nuova media, bisogna stamparla

        ---interazioni nella rete:
        *presentarsi a tutte le case (lista ottenuta dalla lista case del clihouse) (t_2 e t_3)
        *tutte le case devono saper aggiungere e rimuovere case
        *elezione del master
        *

        ---Ruolo del master
        *calcolare le statistiche globali da mandare al server
        *inviare le statistiche al server rest

        ---INTERFACCIA CASA
        * chiedere il boost
        * eliminarsi
        */

        //VARIABILI IMPORTANTI
        String id;
        int port;

        String ip_server = new String("localhost");
        int port_server = 1337;

        //-------------------------------------------------Creazione della casa
        input = new Scanner(System.in);
        boolean b = false;
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
        HouseCli toREST = new HouseCli(ip_server, port_server);

        ArrayList<House> house_list=new ArrayList<House>();
        House house = new House(Integer.parseInt(id), port);

        house_list = toREST.add_to_server(house);
        if (house_list== null)  //se non ritorna la lista delle case significa che qualcosa è andato storto e chiudo la connessione
        {
            toREST.close();
            input.close();
            System.err.println("Non è stato possibile aggiungere la casa al server amministratore. \nArresto del programma in corso");
            return;
        }

        //-------------------------------------------------Settaggio delle ultime istanze
        //la lista esiste la ordino (serve?)
        house_list.sort(House::compareTo);

        //creo il nodo della casa
        HouseNode node = new HouseNode(house_list, toREST, house);

        //creo il misuratore
        HouseMeasurement measurement = new HouseMeasurement(node);

        //avvio il simulatore
        SmartMeterSimulator sm = new SmartMeterSimulator(measurement);
        sm.start();

        //start lato server gprc del nodo
        new Thread(new HouseServer(node, port)).start();
        //-------------------------------------------------Inizio dell'interfaccia utente
        b=false;
        String ris;
        do
        {
            System.out.println(
                    "1 - Rimuoviti dalla lista delle case dal server e chiudi l'applicazione\n"+
                    "2 - Richiesta BOOST"

                    +"\n TESTING"
                    +"\n 3- leggi il coordinatore e vedi se sei coordinatore"
                    +"\n 4 - fai partire l'elezione"
                    +"\n "

            );

            ris = input.nextLine();
            if(ris.matches("^\\d+$"))
                switch (Integer.parseInt(ris))
                {
                    case 1://chiudi la sessione - Leave
                            b = true;
                            break;
                    case 2: //boost
                            break;
                    case 3://leggi il coordinatore
                            System.out.println("Questa casa con id " +node.id+ "\nha come coordinatore: "+node.coordinator_id+" ed è coordinatore? "+ node.coordinator);
                            break;
                    case 4: //partire un'elezione
                            node.startElection(Integer.parseInt(node.id));
                            break;
                }
                if(ris=="1")
                    b=true;
                //if(ris=="2")
                    //chiamata per il boost al nodo
        }while (!b);

        toREST.rm_from_server(house);
        toREST.close();
        input.close();
        sm.stopMeGently();
        //fine main
    }

}
