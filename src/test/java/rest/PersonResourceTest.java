package rest;

import entities.Person;
import DTO.PersonDTO;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
@Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1, p2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Person("Hans-Jørgen", "Hansen", "12987645");
        p2 = new Person("Bodil", "Andersen", "98237682");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello World"));
    }

    @Test
    public void testCount() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/count").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(2));
    }

    @Test
    public void testGetPersonById() throws Exception {
        PersonDTO person = new PersonDTO(p1);
        
        given()
                .contentType("application/json")
                .get("/person/id/" + person.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", equalTo((int)person.getId()));
    }

    @Test
    public void testGetAllPersons() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/all")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("all.size()", is(2));
    }
    
    @Test
    @Disabled
    public void testDeletePerson() throws Exception{
        PersonDTO person = new PersonDTO(p1);
        
        given()
                .contentType("application/json")
                .delete("/person/delete/" + person.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        
        List<PersonDTO> personDTO;
        personDTO = given()
                .contentType("application/json")
                .when()
                .get("/person/all")
                .then()
                .extract().body().jsonPath().getList("all", PersonDTO.class);
        
                PersonDTO person2 = new PersonDTO(p2);
                assertThat(personDTO, containsInAnyOrder(person2));
    }
    
    @Test
    public void testAddPerson() throws Exception{
        given()
                .contentType(ContentType.JSON)
                .body(new PersonDTO("Frans", "Færdinan", "40394949"))
                .when()
                .post("/person/add/")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("fName", equalTo("Frans"))
                .body("lName", equalTo("Færdinan"))
                .body("phone", equalTo("40394949"));
    }
    
    @Test
    public void testEditPerson() throws Exception{
        PersonDTO person = new PersonDTO(p1);
        person.setPhone("19283746");
        
        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .put("person/edit/" + person.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("fName", equalTo("Hans-Jørgen"))
                .body("lName", equalTo("Hansen"))
                .body("phone", equalTo("19283746"))
                .body("id", equalTo((int)person.getId()));
    }
}
