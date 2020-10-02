package facades;

import entities.Person;
import utils.EMF_Creator;
import DTO.PersonDTO;
import exceptions.PersonNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class PersonTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    Person p1;
    Person p2;

    public PersonTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            p1 = new Person("Hans-Jørgen", "Hansen", "12987645");
            p2 = new Person("Bodil", "Andersen", "98237682");

            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testPersonCount() {
        assertEquals(2, facade.getPersonCount(), "Expects two rows in the database");
    }

    @Test
    public void testGetPersonById()throws PersonNotFoundException {
        assertEquals(p2.getId(), facade.getPersonById(p2.getId()).getId());
    }
    
    @Test
    public void testGetAll(){
        assertEquals(2, facade.getAllPersons().getAll().size());
    }
    
    @Test
    public void testAddPerson(){
        PersonDTO p1 = facade.addPerson("Hugo", "Langstrømpe", "98761234");
        PersonDTO p2 = facade.addPerson("Chili", "Bumbum", "98761234");
        PersonDTO p3 = facade.addPerson("Ib", "Libititi", "98761234");
        assertEquals(5, facade.getAllPersons().getAll().size());
    }
    
    @Test
    public void testDeletePerson() throws PersonNotFoundException {
        facade.deletePerson(p1.getId());
        assertEquals(1, facade.getAllPersons().getAll().size());
    }
    
    @Test
    public void testEditPerson() throws PersonNotFoundException{
        PersonDTO personDTO = new PersonDTO(p1);
        personDTO.setfName("Christian");
        PersonDTO personEdit = facade.editPerson(personDTO);
        assertEquals("Christian", personEdit.getfName());
    }
    
    @Test
    public void negativeTestDeletePerson() throws PersonNotFoundException{
        long id = 13;
        try{
            PersonDTO person = facade.deletePerson(id);
            assertEquals("Hans-Jørgen", person.getfName());
            assert false;
        }catch (PersonNotFoundException e){
            assert true;
        }
    }
    
    @Test
    public void negativeTestEditPerson() throws PersonNotFoundException{
        try{
            PersonDTO pDTO = new PersonDTO(new Person("Casper", "Utke", "12345678"));
            PersonDTO personEdit = facade.editPerson(pDTO);
            assert false;
        }catch (PersonNotFoundException e){
            assert true;
        }
    }
    
    @Test
    public void negativTestGetPersonByIdPersonException() throws PersonNotFoundException{
        try{
            long id = 1000000;
            facade.getPersonById(id);
            assert false;
        }catch (PersonNotFoundException e){
            assert true;
        }
    }
    
    @Test
    public void negativTestGetPersonById500(){
        try{
            long id = 13;
            facade.getPersonById(id);
            assert false;
        }catch (Exception e){
            assert true;
        }
    }
}
