package org.example.service;

import org.example.model.Borrowing;
import org.example.model.Copy;
import org.example.model.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Service class for managing borrowing transactions in the library system.
 *
 * <p>This class also provides the CRUD functionalities.
 * It enforces business rules such as:
 * <ul>
 *   <li>A user can have at most 2 active borrowings at a time (then properly tested in BorrowingBusinessLogicTests.java).</li>
 *   <li>When creating a borrowing, it uses a pessimistic lock to ensure that
 *       a copy cannot be borrowed by multiple users concurrently.</li>
 *   <li>when borrowing a book, the status of the copy is updated to "Borrowed".
 *       When a borrowing is deleted, the copy status reverts to "Available".</li>
 * </ul>
 * This service interacts with {@link User}, {@link Copy}, and {@link Borrowing} entities,
 * using JPA to persist and retrieve data from the database.</p>
 */
public class BorrowingService {
    private EntityManagerFactory emf;

    public BorrowingService(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public BorrowingService() {
        this("libraryPU");
    }

    // CREATE
    public void createBorrowing(int userId, int copyId, Date borrowDate, Date returnDate) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Long activeCount = em.createQuery(
                            "SELECT COUNT(b) FROM Borrowing b WHERE b.user.id = :uId AND b.returnDate IS NULL",
                            Long.class
                    )
                    .setParameter("uId", userId)
                    .getSingleResult();

            if (activeCount >= 2) {
                throw new IllegalStateException("User already has 2 active borrowings.");
            }


            Copy copy = em.find(Copy.class, copyId, LockModeType.PESSIMISTIC_WRITE);

            if (copy != null && "Available".equalsIgnoreCase(copy.getStatus())) {
                Borrowing borrowing = new Borrowing(
                        em.find(User.class, userId),
                        copy,
                        borrowDate,
                        returnDate
                );
                copy.setStatus("Borrowed");
                em.persist(borrowing);
                em.merge(copy);
            } else {
                throw new IllegalStateException("Copy is not available.");
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }



    // READ ALL
    public List<Borrowing> getAllBorrowings() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Borrowing b", Borrowing.class).getResultList();
        } finally {
            em.close();
        }
    }

    // READ BY ID
    public Borrowing getBorrowingById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Borrowing.class, id);
        } finally {
            em.close();
        }
    }

    // UPDATE
    public void updateBorrowing(int id, Date borrowDate, Date returnDate) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Borrowing borrowing = em.find(Borrowing.class, id);
            if (borrowing != null) {
                borrowing.setBorrowDate(borrowDate);
                borrowing.setReturnDate(returnDate);
                em.merge(borrowing);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // DELETE
    public void deleteBorrowing(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Borrowing borrowing = em.find(Borrowing.class, id);
            if (borrowing != null) {
                Copy copy = borrowing.getCopy();
                if (copy != null) {
                    copy.setStatus("Available"); // Update the copy status
                    em.merge(copy);
                }
                em.remove(borrowing);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Borrowing> getBorrowingsByUserId(int userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT b FROM Borrowing b WHERE b.user.id = :userId", Borrowing.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
