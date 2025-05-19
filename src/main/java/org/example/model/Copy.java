package org.example.model;

import javax.persistence.*;


/**
 * Copy represents a physical copy of a book in the library system.
 * Each Copy is associated with a specific Book and has a unique copy number
 * and status Available or Borrowed
 *
 * <p>This entity is mapped to the "Copies" table in the database.</p>
 */
@Entity
@Table(name = "Copies")
public class Copy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int copyNumber;

    @Column(nullable = false)
    private String status;


    public Copy() {
    }


    public Copy(Book book, int copyNumber, String status) {
        this.book = book;
        this.copyNumber = copyNumber;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(int copyNumber) {
        this.copyNumber = copyNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Copy{" +
                "id=" + id +
                ", book=" + book +
                ", copyNumber=" + copyNumber +
                ", status='" + status + '\'' +
                '}';
    }
}
