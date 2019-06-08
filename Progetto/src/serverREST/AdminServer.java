package serverREST;

import message_measurement.House;
import message_measurement.SensorMeasurement;
import simulation_src_2019.Measurement;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("server")
public class AdminServer {

    @Path("prova")
    @GET
    @Produces (MediaType.TEXT_PLAIN)
    public Response prova (){
        return Response.ok("prova").build();
    }

    @Path("prova2")
    @GET
    @Produces (MediaType.APPLICATION_JSON)
    public Response prova2 (){
        float [] f = new float[2];
        f[1] = 100.5f;
        return Response.ok(f).build();
    }

    //gestine errori
//https://jersey.github.io/documentation/latest/representations.html

//+++++++++++++++++//
//      Casa       //
//+++++++++++++++++//
    //---------------------------- Rimzione casa
    @Path("house/rm/{id: [0-9]*}")
    @DELETE
    public Response rm_house(@PathParam("id") int id) {
        if (Residence.getInstance().rm_house_to_list(id))
            return Response.ok().build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Identificativo non trovato nella residenza, non è possibile rimoverla").build();
    }

    @Path("house/rm/")
    @POST
    @Consumes({"application/json"})
    public Response rm_house(House h) {
        if (Residence.getInstance().rm_house_to_list(h))
            return Response.ok().build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Identificativo non trovato nella residenza, non è possibile rimovere la casa").build();
    }

    //---------------------------- Aggiunta casa
    @Path("house/add")
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response add_house(House h) {
        if (Residence.getInstance().add_house_to_list(h))
            return Response.ok(Residence.getInstance().houses.values()).build(); //elenco delle case
        else return Response.status(Response.Status.NOT_FOUND).entity("Identificativo già esistente nella residenza, impossibile aggiungere la casa").build();
    }

    //---------------------------- Ricezione valori
    @Path("house/values") //per la residenza
    @POST
    @Consumes({"application/json"})
    public Response add_values(SensorMeasurement sn) {
        if (Residence.getInstance().updateStatistics(sn))
            return Response.ok().build();
        else return Response.status(Response.Status.NOT_FOUND).entity("valori non validi").build();
    }

    @Path("house/values/{id: [0-9]*}") //per la casa
    @POST
    @Consumes({"application/json"})
    public Response add_values(SensorMeasurement sn, @PathParam("id") int id) {
        if (Residence.getInstance().updateStatistics(id, sn))
            return Response.ok().build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Identificativo non trovato nella residenza, impossibile aggiornare i valori").build();
    }


//++++++++++++++++++++++++++//
//      Amministratore      //
//++++++++++++++++++++++++++//
    //---------------------------- nStat di una casa o della residenza
    @Path("admin/stat/{n: [0-9]*}") //per la residenza
    @GET
    @Produces({"application/json"})
    public Response get_stat(@PathParam("n") int n) {
        if (Residence.getInstance().statistics(n) != null)
            return Response.ok(Residence.getInstance().statistics(n)).build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Errore sconosciuto").build();
    }

    @Path("admin/stat/{n: [0-9]*}/{id: [0-9]*}") //per la casa
    @GET
    @Produces({"application/json"})
    public Response get_stat(@PathParam("n") int n, @PathParam("id") int id) {
        if (Residence.getInstance().statistics(id, n) != null)
            return Response.ok(Residence.getInstance().statistics(id, n)).build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Identificativo non trovato, impossibile ottenere le statistiche").build();
    }

    //---------------------------- nStat Deviazione Standard e Media di una casa o residenza
    @Path("admin/mean_stdev/{n: [0-9]*}") //per la residenza
    @GET
    @Produces({"application/json"})
    public Response get_mean_stDev(@PathParam("n") int n) {
        if (Residence.getInstance().get_mean_stdDeviation(n) != null)
            return Response.ok(Residence.getInstance().get_mean_stdDeviation(n)).build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Errore sconosciuto").build();
    }

    @Path("admin/mean_stdev/{n: [0-9]*}/{id: [0-9]*}") //per la casa
    @GET
    @Produces({"application/json"})
    public Response get_mean_stDev(@PathParam("n") int n, @PathParam("id") int id) {
        if (Residence.getInstance().get_mean_stdDeviation(id, n) != null)
            return Response.ok(Residence.getInstance().get_mean_stdDeviation(id, n)).build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Identificativo non trovato, impossibile ottenere le statistiche").build();

    }

    //---------------------------- Visualizza lista case
    @Path("admin/house_list")
    @GET
    @Produces({"application/json"})
    public Response getHouseList() {
        if (Residence.getInstance().houses.size() > 0)
            return Response.ok(Residence.getInstance().houses.values()).build();
        else return Response.status(Response.Status.NOT_FOUND).entity("Non sono registrate case nella residenza").build();
    }

}