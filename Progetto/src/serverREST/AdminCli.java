package serverREST;

import message_measurement.House;
import org.glassfish.jersey.client.ClientConfig;
import simulation_src_2019.Measurement;

import java.net.URI;
import java.net.URISyntaxException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

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
        WebTarget target;

        boolean b = false;
        target = client.target(getBaseURI());

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
            b = false;
            do {
                System.out.print(
                        "Premi il numero corrispondente per scegliere un'opzione: \n" +
                                "1 ➝ Visualizza la lista delle case\n" +
                                "2 ➝ Visualizza le statistiche di consumo di una casa specifica\n" +
                                "3 ➝ Visualizza le statistiche di consumo della residenza\n" +
                                "4 ➝ Visualizza la deviazione standard e la media di cosumo di una casa specifica\n" +
                                "5 ➝ Visualizza la deviazione standard e la media di cosumo della residenza\n" +
                                "6 ➝ Chiudi il client amministratore\n"+
                                "Inserisci: "
                );

                string_container = input.nextLine();
                if (string_container.matches("[1-6]+")) {
                    select = Integer.parseInt(string_container);
                    b = true;
                }
                else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero tra quelli elencati");

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
                    close(client);
                    return;
            }
            select = 0;
        }
    }

    private static void house_list(WebTarget target)
    {
        Response response = target.path("server").path("admin/house_list").request().accept(MediaType.APPLICATION_JSON).get();


        if (response.getStatus() != 200 ){
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            return;
        }

        House[] houses = response.readEntity(House[].class);
        if (houses.length ==0)
            return;

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

        Response response = target.path("server").path("admin/stat").path(input[1]).path(input[0]).request().accept(MediaType.APPLICATION_JSON).get();

        if (response.getStatus() != 200)
        {
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            return;
        }

        Measurement[] values = response.readEntity(Measurement[].class);
        if (values.length == 0){
            System.err.println("Non ci sono valori raccolti sulla casa");
            return;
        }

        System.out.println("Raccolta di "+ values.length+" valori della casa con identificativo "+input[0]);
        for (Measurement sv : values){
            System.out.println("<"+qtPrint(sv.getTimestamp())+">: "+sv.getValue());
        }
    }

    //-------------------------------- Statistica residenza
    private static void statistics_residence(WebTarget target)
    {
        String n = getInputResidence();
        Response response = target.path("server").path("admin/stat").path(n).request().accept(MediaType.APPLICATION_JSON).get();
        Measurement[] values = response.readEntity(Measurement[].class);

        if (response.getStatus() != 200)
        {
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            return;
        }

        if (values.length == 0){
            System.err.println("Non ci sono valori raccolti sulla residenza");
            return;
        }

        System.out.println("Raccolta di "+values.length+" valori della residenza");
        for (Measurement sv : values){
            System.out.println("<"+qtPrint(sv.getTimestamp())+">: "+sv.getValue());
        }
    }

    //-------------------------------- Media e DevStandard di una casa
    private static void mean_stddev_house(WebTarget target)
    {
        //[0] = id, [1] = n
        String[] input = getInputHouse();

        Response response = target.path("server").path("admin/mean_stdev").path(input[1]).path(input[0]).request().accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() != 200)
        {
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            return;
        }

        Double[] m_stdD = response.readEntity(Double[].class);
        if (m_stdD == null || (m_stdD[0] == 0 && m_stdD[1] == 0)){
            System.err.println("Non ci sono abbastanza misurazioni per generare una media e una deviazione standard");
            return;
        }

        System.out.println("Informazioni calcolate su "+input[1]+" valori della casa con identificativo "+input[0]+"\n"+
                "--------------Media: "+m_stdD[0]+"\n"+
                "Deviazione Standard: "+m_stdD[1]+"\n");
    }


    //-------------------------------- Media e DevStandard della residenza
    private static void mean_stddev_residence(WebTarget target)
    {
        String n = getInputResidence();

        Response response = target.path("server").path("admin/mean_stdev").path(n).request().accept(MediaType.APPLICATION_JSON).get();
        Float[] m_stdD = response.readEntity(Float[].class);

        if (m_stdD == null || (m_stdD[0] == 0 && m_stdD[1] == 0)){
            System.err.println("Non ci sono abbastanza misurazioni per generare una media e una deviazione standard");
            return;
        }

        System.out.println("Informazioni calcolate su "+n+" valori della residenza\n"+
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
            else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero valido");

        }while(!b);

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
            else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero valido");
        } while (!b);

        b = false;
        do {
            System.out.println("Inserisci quanti valori memorizzati vuoi considerare: ");
            ret[1] = input.nextLine();
            if (ret[1].matches("^\\d+$"))
                b = true;
            else System.err.println(" ⚠ Input non valido ⚠\nInserire un numero valido");
        } while (!b);

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

    private static void waitSec()
    {
        try {
            Thread.sleep(5000);
        }catch (InterruptedException ie) {System.err.println("errore nella sleep di riconnessione");}
    }

    private static void close(Client client)
    {
        System.out.println("Chiusura del client amministratore");
        client.close();
        input.close();
    }

    private static String qtPrint(long time){
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(time);
    }
}
