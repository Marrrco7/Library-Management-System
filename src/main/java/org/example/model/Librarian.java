package org.example.model;

import javax.persistence.*;
import java.util.Date;


/**
 * A librarian in the library management system.
 * A librarian is associated with a unique user account,
 * has an employment date, and holds a specific position.
 *
 * <p>This entity is mapped to the Librarians table in the database.
 * It enforces a one-to-one relationship with a User, meaning each librarian
 * corresponds to a single user account.</p>
 */

@Entity
@Table(name = "Librarians")
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date employmentDate;

    @Column(nullable = false)
    private String position;


    public Librarian() {
    }

    public Librarian(User user, Date employmentDate, String position) {
        this.user = user;
        this.employmentDate = employmentDate;
        this.position = position;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "id=" + id +
                ", user=" + user +
                ", employmentDate=" + employmentDate +
                ", position='" + position + '\'' +
                '}';
    }
}
