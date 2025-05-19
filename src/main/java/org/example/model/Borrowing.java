package org.example.model;

import javax.persistence.*;
import java.util.Date;



/**
 * It represents a borrowing transaction in the library system.
 * A Borrowing links a user with a copy of a book, recording the
 * borrow date and also the return date optionally.
 *
 * <p>This entity is mapped to the Borrowings table in the database.
 * It enforces that each borrowing must have an associated user and copy,
 * and a borrow date must always be provided.</p>
 */
@Entity
@Table(name = "Borrowings")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "copyId", nullable = false)
    private Copy copy;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date borrowDate;

    @Temporal(TemporalType.DATE)
    private Date returnDate;

    public Borrowing() {
    }

    public Borrowing(User user, Copy copy, Date borrowDate, Date returnDate) {
        this.user = user;
        this.copy = copy;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
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

    public Copy getCopy() {
        return copy;
    }

    public void setCopy(Copy copy) {
        this.copy = copy;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }


    @Override
    public String toString() {
        return "Borrowing{" +
                "id=" + id +
                ", user=" + user +
                ", copy=" + copy +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
