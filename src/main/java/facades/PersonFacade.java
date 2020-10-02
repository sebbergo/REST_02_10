package facades;

import entities.Person;
import DTO.PersonDTO;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Path;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //TODO Remove/Change this before use
    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long renameMeCount = (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
            return renameMeCount;
        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone) {
        EntityManager em = emf.createEntityManager();
        try {
            Person p = new Person(fName, lName, phone);
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO deletePerson(long id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        if(person == null){
            throw new PersonNotFoundException("Person does not exist");
        }
        try {
            em.getTransaction().begin();
            em.remove(person);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return new PersonDTO(person);

    }

    @Override
    public PersonDTO getPersonById(long id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        if(id == 13){
            System.out.println(13/0);
        }
        if(person == null){
            throw new PersonNotFoundException("Person does not exist");
        }
        return new PersonDTO(person);
    }

    @Override
    public PersonDTO getAllPersons() {
        EntityManager em = emf.createEntityManager();
        List<Person> allPersons = null;

        try {
            allPersons = em.createNamedQuery("Person.getAllPersons", Person.class
            ).getResultList();

            return new PersonDTO(allPersons);
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException{
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, p.getId());
        if(person == null){
            throw new PersonNotFoundException("Person does not exist");
        }
        person.setfName(p.getfName());
        person.setlName(p.getlName());
        person.setPhone(p.getPhone());
        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return new PersonDTO(person);

    }

}
