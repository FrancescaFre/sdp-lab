package house_app;

import org.glassfish.jersey.client.ClientConfig;
import message_measurement.House;
import simulation_src_2019.Measurement;

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

public class HouseCli{

    //Classe relazionata alla casa che si interfaccia con il server

    //1 -- Aggiungere la casa al server --> Ricevere dal server la lista di case e salvarsele
    //2 -- Rimuoversi dal server
    //3 -- Spegenere il client
    //4 -- Inviare le Statistiche al server
        //if (coordinatore) manda le proprie stat anche le globali, else solo le proprie

    String ip_server;
    int port_server;

    WebTarget target;
    Client client;

    //SETTING CLIENT
    public HouseCli (String ip_s, int port_s){
        //info su dove collegarsi
        this.ip_server = ip_s;
        this.port_server = port_s;

        ClientConfig config = new ClientConfig();
        client = ClientBuilder.newClient(config);

        target= client.target(getBaseURI(ip_server, port_server));
    }

    //AGGIUNTA AL SERVER
    public ArrayList<House> add_to_server(House house) {
        Response response = target.path("server").path("/house/add").request(MediaType.APPLICATION_JSON).post(Entity.entity(house, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println(response.readEntity(String.class) + " - " + response.getStatus());
            System.err.println("Riavviare il programma usando un identificativo differente");

            return null;
        }
        return new ArrayList<House>(Arrays.asList(response.readEntity(House[].class)));
    }

    //RIMOZIONE DAL SERVER
    public boolean rm_from_server(House house) {
        Response response = target.path("server").path("/house/rm").request().post(Entity.entity(house, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200){
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            return false;
        }

        System.out.println("Rimozione avvenuta con successo");
        return true;
    }

    //INVIO VALORI al server
    /**send_values vuole la casa che invia i valori, se è coordinatore o meno, i suoi valori della residenza e i propri**/
    public void send_values(House house, boolean coordinator, Measurement sv_r, Measurement sv_h){
        Response response_residence;
        if (coordinator && sv_r!=null) {
            response_residence = target.path("server").path("/house/values").request().post(Entity.entity(sv_r, MediaType.APPLICATION_JSON));
            if (response_residence.getStatus()!=200)
                System.err.println(response_residence.readEntity(String.class)+" - " + response_residence.getStatus());
        }

        if(sv_h == null)
            return;

        Response response = target.path("server").path("/house/values").path(house.id+"").request().post(Entity.entity(sv_h, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200)
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
    }

    // Chiusura connessione
    public void close(){
        client.close();
    }

    // Creazione dell'URI
    private static URI getBaseURI(String ip_server, int port_server) {
        URI uri = null;
        try {
            uri = new URI("http://"+ip_server+":"+port_server+"/");
        }
        catch (URISyntaxException e){System.err.println("Errore nell'uri");}

        return uri;
    }
}
