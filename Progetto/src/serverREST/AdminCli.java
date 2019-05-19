package serverREST;

import message_measurement.SensorValue;
import org.glassfish.jersey.client.ClientConfig;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//https://jersey.github.io/apidocs/2.25.1/jersey/index.html?org/glassfish/jersey/client/ClientConfig.html

public class AdminCli {
    static Scanner input;

    public static void main (String[] argv) {

        int select = 0;
        String string_container;

        input = new Scanner(System.in);


        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target(getBaseURI());

        /**
         interfaccia per scegliere
         1 --visualizzare la lista delle case

         --Statistiche
         2-- Statistiche di una casa
         3 -- Statistiche della residenza

         -- deviazione standard
         4 -- Deviazione standard di una casa
         5 -- Deviazione standard della residenza
         * **/

        while (true) {
            Boolean b = false;
            do {
                System.out.print(
                        "Premi il numero corrispondente per scegliere un'opzione: \n" +
                                "1 ➝ Visualizza la lista delle case\n" +
                                "2 ➝ Visualizza le statistiche di consumo di una casa specifica\n" +
                                "3 ➝ Visualizza le statistiche di consumo della residenza\n" +
                                "4 ➝ Visualizza la deviazione standard e la media di cosumo di una casa specifica\n" +
                                "5 ➝ Visualizza la deviazione standard e la media di cosumo della residenza\n" +
                                "6 ➝ Visualizza la deviazione standard e la media di cosumo della residenza\n"+
                                "Inserisci: "
                );

                string_container = input.nextLine();
                if (string_container.matches("[1-6]+")) {
                    select = Integer.parseInt(string_container);
                    b = true;
                }
                else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero tra quelli elencati\n");
                System.out.println("++++++ "+select);
            } while (!b);

            //interfaccia per scegliere
            //visualizzare la lista delle case

            //Statistiche
            //Statistiche di una casa
            //Statistiche della residenza

            //deviazione standard
            //Deviazione standard di una casa
            //Deviazione standard della residenza
            switch (select) {
                case 1:
                    house_list(target);
                    break;
                case 2:
                    statistics_house(target);
                    break;
                case 3:
                    statistics_residence(target);
                    break;
                case 4:
                    mean_stddev_house(target);
                    break;
                case 5:
                    mean_stddev_residence(target);
                    break;
                case 6:
                    input.close();
                    System.out.println("Chiusura del client amministratore");
                    return;
            }
            select = 0;
        }
    }

    private static void house_list(WebTarget target)
    {
        Response response = target.path("server").path("admin/house_list").request().accept(MediaType.APPLICATION_JSON).get();
        System.out.println("xXx");
        House[] houses = response.readEntity(House[].class);
        System.out.println("xxx");
        if (response.getStatus() != 200 ){
            System.err.println("Non ci sono case registrate nella residenza");
        }

        for (House h:houses)
            System.out.println(
                    "⚡--------------------------------⚡\n"+
                    "Identificativo casa: "+h.id+"\n"+
                    "Indirizzo e porta: "+h.ip+":"+h.port);
        System.out.println("⚡----------------------------------⚡\n");
    }

//-------------------------------- Statistica singola casa
    private static void statistics_house(WebTarget target)
    {
        //[0] = id, [1] = n
        String[] input = getInputHouse();

        Response response = target.path("server").path("stat").path(input[1]).path(input[0]).request().accept(MediaType.APPLICATION_JSON).get();

        if (response.getStatus() != 200)
        {
            System.err.println("Identificativo non trovato, ERRORE" + response.getStatus());
            return;
        }

        SensorValue[] values = response.readEntity(SensorValue[].class);
        if (values.length == 0){
            System.err.println("Non ci sono valori raccolti sulla casa");
        }

        System.out.println("Raccolta di "+input[1]+" valori della casa con identificativo "+input[0]+"\n");
        for (SensorValue sv : values){
            System.out.println("<"+sv.time+">: "+sv.value);
        }
    }

    //-------------------------------- Statistica residenza
    private static void statistics_residence(WebTarget target)
    {
        String n = getInputResidence();
        Response response = target.path("server").path("stat").path(n).request().accept(MediaType.APPLICATION_JSON).get();
        SensorValue[] values = response.readEntity(SensorValue[].class);

        if (values.length == 0){
            System.err.println("Non ci sono valori raccolti sulla residenza");
        }

        System.out.println("Raccolta di "+n+" valori della residenza\n");
        for (SensorValue sv : values){
            System.out.println("<"+sv.time+">: "+sv.value);
        }
    }

    //-------------------------------- Media e DevStandard di una casa
    private static void mean_stddev_house(WebTarget target)
    {
        //[0] = id, [1] = n
        String[] input = getInputHouse();

        Response response = target.path("server").path("/mean_stDev").path(input[1]).path(input[0]).request().accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() != 200)
        {
            System.err.println("Identificativo non trovato. ERRORE "+response.getStatus());
            return;
        }

        Double[] m_stdD = response.readEntity(Double[].class);
        if (m_stdD[0] == 0 && m_stdD[1] == 0){
            System.err.println("Non ci sono abbastanza misurazioni per generare una media e una deviazione standard");
        }
        System.out.println("Raccolta di "+input[1]+" valori della casa con identificativo "+input[0]+"\n"+
                "--------------Media: "+m_stdD[0]+"\n"+
                "Deviazione Standard: "+m_stdD[1]+"\n");
    }


    //-------------------------------- Media e DevStandard della residenza
    private static void mean_stddev_residence(WebTarget target)
    {
        String n = getInputResidence();

        Response response = target.path("server").path("/mean_stDev").path(n).request().accept(MediaType.APPLICATION_JSON).get();
        Float[] m_stdD = response.readEntity(Float[].class);

        if (m_stdD[0] == 0 && m_stdD[1] == 0){
            System.err.println("Non ci sono abbastanza misurazioni per generare una media e una deviazione standard");
        }

        System.out.println("Raccolta di "+n+" valori della residenza\n"+
                "--------------Media: "+m_stdD[0]+"\n"+
                "Deviazione Standard: "+m_stdD[1]+"\n");
    }


    //--------------------------------------------------------------------------------- UTILIES
    private static String getInputResidence(){
        String  n= "-1";
        boolean b = false;

        do {
            System.out.println("Inserisci quanti valori memorizzati vuoi considerare: ");
            n = input.nextLine();
            if (n.matches("^\\d+$"))
                b = true;
            else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero valido\n");

        }while(b);

        return n;
    }

    private static String[] getInputHouse() {
        //[0] = id, [1] = n
        String[] ret = new String[2];
        boolean b = false;

        do {
            System.out.println("Inserisci il numero identificativo della casa da controllare: ");
            ret[0] = input.nextLine();
            if (ret[0].matches("^\\d+$"))
                b = true;
            else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero valido\n");
        } while (b);

        b = false;
        do {
            System.out.println("Inserisci quanti valori memorizzati vuoi considerare: ");
            ret[1] = input.nextLine();
            if (ret[1].matches("^\\d+$"))
                b = true;
            else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero valido\n");
        } while (b);

        return ret;
    }

    private static URI getBaseURI() {
        URI uri = null;
        try {
            uri = new URI("http://localhost:1337/");
        }
        catch (URISyntaxException e){System.err.println("Errore nell'uri");}

        return uri;
    }
}
