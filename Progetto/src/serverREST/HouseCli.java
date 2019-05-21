package serverREST;


import message_measurement.SensorValue;
import org.glassfish.jersey.client.ClientConfig;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//https://jersey.github.io/apidocs/2.25.1/jersey/index.html?org/glassfish/jersey/client/ClientConfig.html

public class HouseCli extends Thread{

    //Classe thread della casa che si interfaccia con il server

    //1 -- Aggiungere la casa al server
    //2 -- Ricevere dal server la lista di case e salvarsele
    //3 -- Rimuoversi dal server
    //4 -- Spegenere il client

    //5 -- Inviare le Statistiche al server
    //if (coordinatore) manda le proprie stat anche le globali, else solo le proprie

    Boolean coordinator;
    House house;
    ArrayList<House> houses_list;
    ArrayList<SensorValue> values_house;
    ArrayList<SensorValue> values_residence;
    String ip_server;
    int port_server;

    public HouseCli (House id, Boolean cord, ArrayList<House> h_l, ArrayList<SensorValue> val, ArrayList<SensorValue> val_res,  String ip_s, int port_s){
        //riferimenti della classe HOUSE, che contiene tutti i valori synch
        this.house = id;
        this.coordinator = cord;
        this.houses_list = h_l;
        this.values_house = val;
        this.values_residence = val_res;

        //info su dove collegarsi
        this.ip_server = ip_s;
        this.port_server = port_s;
    }


    public void run() {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target;

        //-------------- tentativo di collegarsi al server, 3 tentativi da 5000 millisec, alla peggio ci mette 15 secondi per connettersi, o non si connette proprio

        target= client.target(getBaseURI(ip_server, port_server));

        //Aggiungo la casa al server
        //se non va, faccio riaprire il client per fargli cambiare id
        if (!add_to_server(target, house, houses_list))
            close(client);




    }

    private static boolean add_to_server(WebTarget target, House house, ArrayList<House> houses_list){

        Response response = target.path("server").path("/house/add").request(MediaType.APPLICATION_JSON).post(Entity.entity(house, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200)
        {
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            System.err.println("Riavviare il programma usando un identificativo differente");

            return false;
        }

        houses_list = new ArrayList<House>(Arrays.asList(response.readEntity(House[].class)));
        return true;
    }

    private static boolean rm_from_server(WebTarget target, House house) {
        Response response = target.path("server").path("/house/rm").request().post(Entity.entity(house, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200){
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            return false;
        }

        System.out.println("Rimozione avvenuta con successo");
        return true;
    }

    private static void send_values(WebTarget target, House house, boolean coordinator, ArrayList<SensorValue> sv_r, ArrayList<SensorValue> sv_h){
        Response response_residence;
        if (coordinator) {
            response_residence = target.path("server").path("/house/values").request().post(Entity.entity(sv_r, MediaType.APPLICATION_JSON));
            if (response_residence.getStatus()!=200)
                System.err.println(response_residence.readEntity(String.class)+" - " + response_residence.getStatus());
        }
        Response response = target.path("server").path("/house/values").path(house.id+"").request().post(Entity.entity(sv_h, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200)
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());

    }

    private static void close(Client c){
        c.close();
    }


    private static URI getBaseURI(String ip_server, int port_server) {
        URI uri = null;
        try {
            uri = new URI("http://"+ip_server+":"+port_server+"/");
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

}
