package house_app;

import message_measurement.SensorMeasurement;
import org.glassfish.jersey.client.ClientConfig;
import message_measurement.House;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.synth.SynthScrollBarUI;
import javax.ws.rs.ProcessingException;
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
        //Response response = target.path("server").path("/house/add").request(MediaType.APPLICATION_JSON).post(Entity.entity(house, MediaType.APPLICATION_JSON));
        Response response = catchConnectionError(target.path("server").path("/house/add"), house, null, null, null);
        if(response == null) return null;

        if (response.getStatus() != 200) {
            System.err.println(response.readEntity(String.class) + " - " + response.getStatus());
            System.err.println("Riavviare il programma usando un identificativo differente");

            return null;
        }
        System.out.println("-------------------------");

        House[] list = response.readEntity(House[].class);
        return new ArrayList<House>(Arrays.asList(list));
    }

    //RIMOZIONE DAL SERVER
    public Boolean rm_from_server(House house) {
       //Response response = target.path("server").path("/house/rm").request().post(Entity.entity(house, MediaType.APPLICATION_JSON));
       Response response = catchConnectionError(target.path("server").path("/house/rm"), house, null, null, null);
        if(response == null) return null;


        if (response.getStatus() != 200){
            System.err.println(response.readEntity(String.class)+" - " + response.getStatus());
            return false;
        }

        System.out.println("Rimozione avvenuta con successo");
        return true;
    }

    //INVIO VALORI al server
    /**send_values vuole la casa che invia i valori, se è coordinatore o meno, i suoi valori della residenza e i propri**/
    public Boolean send_values(House house, boolean coordinator, SensorMeasurement sv_r, SensorMeasurement sv_h){
        Response response_residence;
        if (coordinator && sv_r!=null) {
            //response_residence = target.path("server").path("/house/values").request().post(Entity.entity(sv_r, MediaType.APPLICATION_JSON));
            response_residence = catchConnectionError(target.path("server").path("/house/values"), house, coordinator, sv_r, null);
            if(response_residence == null) return null;

            if (response_residence.getStatus()!=200)
                System.err.println(response_residence.readEntity(String.class)+" - " + response_residence.getStatus());
        }

        if(sv_h == null)
            return false;

        //Response response = target.path("server").path("/house/values").path(house.id+"").request().post(Entity.entity(sv_h, MediaType.APPLICATION_JSON));
        Response response = catchConnectionError(target.path("server").path("/house/values").path(house.id+""), house, coordinator, null, sv_h);
        if(response == null) return null;

        if (response.getStatus() != 200) {
            System.err.println(response.readEntity(String.class) + " - " + response.getStatus());
            return false;
        }
        return true;
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

    //check
    static int i = 0;
    private static Response catchConnectionError(WebTarget target, House house, Boolean coordinator, SensorMeasurement sv_r, SensorMeasurement sv_h){
        Response response = null;
        do{
            try{

                if (coordinator== null)
                    response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(house, MediaType.APPLICATION_JSON));
                else if(sv_r != null)
                    response = target.request().post(Entity.entity(sv_r, MediaType.APPLICATION_JSON));
                else
                    response = target.request().post(Entity.entity(sv_h, MediaType.APPLICATION_JSON));
                i=0;
                return response;

            }catch (ProcessingException pe){
                i++;
                System.err.println("Errore di connessione al server REST, tentativo "+i+" di riconnessione");
                try{
                    TimeUnit.SECONDS.sleep(3);
                }catch(InterruptedException ie) {ie.getMessage();}
            }
        }while(i<3 && sv_r!=null); //dopo 3 tentativi, rilascia response null
        System.err.println("Impossibile contattare il server REST, annullamento richiesta in corso ... ");

        return response;
    }

}
