package Flock.Training.models;

import Flock.Training.exceptions.BookAlreadyOwnedException;
import Flock.Training.exceptions.BookNotFoundException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "Users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthdate;

    @ManyToMany
    @JoinTable(
            name = "user_books",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @Column(nullable = false)
    private List<Book> books = new ArrayList<>();

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public List<Book> getBooks() {
        return (List<Book>) Collections.unmodifiableList(books);
    }

    public void setBooks(List<Book> books) {
        if (this.books == null) {
            this.books = new ArrayList<>();
        }

        for (Book book : books) {
            boolean exists = this.books.stream()
                    .anyMatch(existingBook -> existingBook.getId().equals(book.getId()));
            if (exists) {
                throw new BookAlreadyOwnedException("The book with ID " + book.getId() + " and title '" + book.getTitle() + "' is already in the list.");
            }
            this.books.add(book);
        }
    }

    public void addBook(Book book) {
        if (this.books == null) {
            this.books = new ArrayList<>();
        }

        boolean exists = this.books.stream()
                .anyMatch(existingBook -> existingBook.getId().equals(book.getId()));
        if (exists) {
            throw new BookAlreadyOwnedException("The book with ID " + book.getId() + " and title '" + book.getTitle() + "' is already in the list.");
        }
        this.books.add(book);
    }

    public void removeBook(Book book) {
        if (this.books == null) {
            throw new BookNotFoundException("The user does not have a book list yet.");
        }

        boolean exists = this.books.stream()
                .anyMatch(existingBook -> existingBook.getId().equals(book.getId()));
        if (!exists) {
            throw new BookNotFoundException("The book with ID " + book.getId() + " and title '" + book.getTitle() + "' is not in the list.");
        }
        this.books.remove(book);
    }
}
