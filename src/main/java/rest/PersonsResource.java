package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import DTO.PersonDTO;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Todo Remove or change relevant parts before ACTUAL use
@Path("/person")
public class PersonsResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        long count = FACADE.getPersonCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }

    @Path("id/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonById(@PathParam("id") long id) throws PersonNotFoundException {
        PersonDTO result = FACADE.getPersonById(id);
        if(result == null){
            throw new PersonNotFoundException("No person with such an id has been found");
        }
        return GSON.toJson(result);
    }

    @Path("all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPersons() {
        PersonDTO result = FACADE.getAllPersons();
        return GSON.toJson(result);
    }

    @Path("add")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addPerson(String person) {
        PersonDTO p = GSON.fromJson(person, PersonDTO.class);
        FACADE.addPerson(p.getfName(), p.getlName(), p.getPhone());
        return Response.ok(p).build();
    }

    @Path("edit/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response editPerson(@PathParam("id") long id, String person) throws PersonNotFoundException {
        PersonDTO pDTO = GSON.fromJson(person, PersonDTO.class);
        pDTO.setId(id);
        PersonDTO pEdit = FACADE.editPerson(pDTO);
        return Response.ok(pEdit).build();
    }
    
    @Path("delete/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") long id) throws PersonNotFoundException {
        PersonDTO pDel = FACADE.deletePerson(id);
        return "{\"status\": \"removed\"}";
    }
}
