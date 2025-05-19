package org.example.service;

import org.example.model.Librarian;
import org.example.model.User;

import javax.persistence.*;
import java.util.List;


/**
 * Service class for managing librarians in the library management system.
 *
 * <p>This class provides methods to create, retrieve, update, and delete
 * librarian records. It handles the persistence of {@link Librarian} entities
 * by interacting with the database using JPA.</p>
 *
 * <p>Each librarian is associated with a specific {@link User} account, and
 * this service ensures the proper creation and management of librarians.</p>
 */


public class LibrarianService {
    private EntityManagerFactory emf;

    public LibrarianService(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public LibrarianService() {
        this("libraryPU");
    }


    public void createLibrarian(int userId, String position, java.util.Date employmentDate) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                Librarian librarian = new Librarian(user, employmentDate, position);
                em.persist(librarian);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


    public List<Librarian> getAllLibrarians() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Librarian l", Librarian.class).getResultList();
        } finally {
            em.close();
        }
    }


    public Librarian getLibrarianById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Librarian.class, id);
        } finally {
            em.close();
        }
    }


    public void updateLibrarian(int id, String position, java.util.Date employmentDate) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Librarian librarian = em.find(Librarian.class, id);
            if (librarian != null) {
                librarian.setPosition(position);
                librarian.setEmploymentDate(employmentDate);
                em.merge(librarian);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


    public void deleteLibrarian(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Librarian librarian = em.find(Librarian.class, id);
            if (librarian != null) {
                em.remove(librarian);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
