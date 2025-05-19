package org.example.service;

import org.example.model.User;

import javax.persistence.*;
import java.util.List;


/**
 * Service class for managing user operations in the library management system.
 *
 * <p>This class provides CRUD operations for
 * {@link User} entities from the database using JPA. It handles user related
 * operations such as authentication, finding users by email or name,
 * and enforcing constraints like unique email addresses. It interacts
 * with the  Users table and ensures that user data is persisted and
 * managed according to the application's requirements.</p>
 */
public class UserService {
    private EntityManagerFactory emf;

    public UserService(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public UserService() {
        this("libraryPU");
    }

    // CREATE
    public void createUser(String name, String email, String phoneNumber, String address, String password, String role) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = new User(name, email, phoneNumber, address, password, role);
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }



    public void updateUser(int id, String name, String email, String phoneNumber, String address, String password, String role) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                user.setName(name);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setAddress(address);
                user.setPassword(password);
                user.setRole(role); // Update the role
                em.merge(user);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }



    public void deleteUser(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }




    public List<User> getAllUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public User getUserByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


    public User getUserById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public User authenticateUser(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Invalid credentials
        } finally {
            em.close();
        }
    }




    public User getUserByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


}
