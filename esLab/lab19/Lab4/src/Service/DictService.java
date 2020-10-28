package Service;

import Resource.Word;
import Resource.Dict;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("dict")
public class DictService {
    @GET
    @Produces({"application/json"})
    public Response getDict(){
        return Response.ok(Dict.getInstance()).build();
    }

    @Path("add")
    @POST
    @Consumes({"application/json"})
    public Response addWord(Word w){
        Dict.getInstance().add(w);
        return Response.ok().build();
        //ok() è vuoto perchè non si produce nulla
    }

    @Path("getDef/{word}")
    @GET
    public Response getDef(@PathParam("word") String parola){
        String definition = Dict.getInstance().getDef(parola);
        if (definition != null)
            return  Response.ok(definition).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("delete")
    @DELETE
    @Consumes({"application/json"})
    public Response deleteWord(Word w){
        System.out.println("before deleete");
        Dict.getInstance().delete(w.key);
        System.out.println("after deleett");
        if (Dict.getInstance().getDef(w.key)==null)
                return Response.ok().build();
        else
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    @Path("update")
    @PUT
    @Consumes({"application/json"})
    public Response updateDict(Word w){
        Dict.getInstance().update(w);
        return Response.ok().build();
    }
}
