package org.example.service;

import org.example.model.Publisher;

import javax.persistence.*;
import java.util.List;


/**
 * Service class for managing publishers in the library management system.
 *
 * <p>This class provides CRUD operations for
 * {@link Publisher} entities from the database using JPA. It interacts
 * with the Publishers table and handles the persistence logic for
 * publisher related operations.</p>
 *
 */
public class PublisherService {
    private EntityManagerFactory emf;

    public PublisherService(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public PublisherService() {
        this("libraryPU");
    }


    public void createPublisher(String name, String address, String phoneNumber) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Publisher publisher = new Publisher(name, address, phoneNumber);
            em.persist(publisher);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


    public List<Publisher> getAllPublishers() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Publisher p", Publisher.class).getResultList();
        } finally {
            em.close();
        }
    }


    public Publisher getPublisherById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Publisher.class, id);
        } finally {
            em.close();
        }
    }


    public void updatePublisher(int id, String name, String address, String phoneNumber) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Publisher publisher = em.find(Publisher.class, id);
            if (publisher != null) {
                publisher.setName(name);
                publisher.setAddress(address);
                publisher.setPhoneNumber(phoneNumber);
                em.merge(publisher);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


    public void deletePublisher(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Publisher publisher = em.find(Publisher.class, id);
            if (publisher != null) {
                em.remove(publisher);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
