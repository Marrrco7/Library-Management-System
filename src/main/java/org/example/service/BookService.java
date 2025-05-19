package org.example.service;

import org.example.model.Book;

import javax.persistence.*;
import java.util.List;

/**
 * Service class for handling operations related to books in the library system.
 * It provides CRUD operations methods.
 *
 * <p>This class interacts with the database using JPA. It uses an {@link EntityManagerFactory}
 * to create {@link EntityManager} instances for managing transactions and performing CRUD
 * operations on Book entities.</p>
 */

public class BookService {
    private EntityManagerFactory emf;

    /**
     * Constructs a BookService using the given persistence unit name.
     *
     * persistenceUnitName is the name of the persistence unit to use
     */
    public BookService(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    /**
     * Default constructor that uses "libraryPU" as the persistence unit.
     */

    public BookService() {
        this("libraryPU");
    }


    /**
    * Creates and persists a new book in the database, however the one with String role as constructor was created in
     * order to run a tests to check if a non librarian can also create books.
    */
    public void createBook(String title, String author, String publisher, int publicationYear, String isbn) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = new Book(title, author, publisher, publicationYear, isbn);
            em.persist(book);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void createBook(String role,
                           String title,
                           String author,
                           String publisher,
                           int publicationYear,
                           String isbn) {
        if (!"LIBRARIAN".equalsIgnoreCase(role)) {
            throw new IllegalStateException("Only librarians can create books!");
        }
        createBook(title, author, publisher, publicationYear, isbn);
    }

    /**
     *Every other method for CRUD operations
     */

    public List<Book> getAllBooks() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } finally {
            em.close();
        }
    }


    /**
     *Retrieves book by its unique identifier.
     */
    public Book getBookById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }




    public void updateBook(int id, String title, String author, String publisher, int publicationYear, String isbn) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                book.setTitle(title);
                book.setAuthor(author);
                book.setPublisher(publisher);
                book.setPublicationYear(publicationYear);
                book.setIsbn(isbn);
                em.merge(book);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteBook(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                em.remove(book);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     *Retrieve book by its ISBN.
     */

    public Book getBookByIsbn(String isbn) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                    .setParameter("isbn", isbn)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

}
