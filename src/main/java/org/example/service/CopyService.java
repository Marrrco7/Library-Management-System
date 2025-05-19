package org.example.service;

import org.example.model.Copy;
import org.example.model.Book;

import javax.persistence.*;
import java.util.List;


/**
 * Service class for managing copies of books in the library system.
 *
 * <p>provided CRUD functinalities for
 * {@link Copy} entities. It interacts with the database using JPA,
 * leveraging an {@code EntityManagerFactory} to perform operations on
 * copies of books, such as checking availability and managing their status.</p>
 */
public class CopyService {
    private EntityManagerFactory emf;

    public CopyService(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public CopyService() {
        this("libraryPU");
    }

    public void createCopy(int bookId, int copyNumber, String status) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, bookId);
            if (book != null) {
                Copy copy = new Copy(book, copyNumber, status);
                em.persist(copy);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Copy> getAllCopies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Copy c", Copy.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Copy getCopyById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Copy.class, id);
        } finally {
            em.close();
        }
    }

    public void updateCopy(int id, int copyNumber, String status) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Copy copy = em.find(Copy.class, id);
            if (copy != null) {
                copy.setCopyNumber(copyNumber);
                copy.setStatus(status);
                em.merge(copy);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteCopy(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Copy copy = em.find(Copy.class, id);
            if (copy != null) {
                em.remove(copy);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Copy> getAvailableCopies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Copy c WHERE c.status = 'Available'", Copy.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
