/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author sebas
 */
public class Tester {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
        
        Person p1 = new Person("Hans-Jørgen", "Hansen", "12343212");
        Person p2 = new Person("Bodil", "Andersen", "99273874");
        
        Address a1 = new Address("Krak Allé", "8700", "Nording");
        Address a2 = new Address("Toto Gade", "8730", "Viding");
        
        p1.setAddress(a1);
        p2.setAddress(a2);

        em.getTransaction().begin();
        em.persist(p1);
        em.persist(p2);
        em.getTransaction().commit();

        System.out.println("p1: " + p1.getfName() + ", " + p1.getlName() + ", " + p1.getPhone());
    }
}
