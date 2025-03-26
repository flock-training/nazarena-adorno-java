package Flock.Training.models;

import Flock.Training.exceptions.BookAlreadyOwnedException;
import Flock.Training.exceptions.BookNotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Representa la entidad 'User' de la tabla 'Users'
 */
@Entity
@Table(name = "Users")
@Schema(name = "User", description = "Representa un usuario en la plataforma")
public class User implements UserDetails {

    /**
     * Atributos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre del usuario", example = "johndoe123")
    private String username;

    @Column(nullable = false)
    @Schema(description = "Nombre completo del usuario", example = "John Doe")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-05-15")
    private LocalDate birthdate;

    /**
     * Lista de libros que posee el usuario.
     * <p>
     * Utiliza la tabla 'user_books' para relacionarlos
     * mediante 'user_id' y 'book_id'.
     */
    @ManyToMany
    @JoinTable(
            name = "user_books",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @Column(nullable = false)
    @Schema(description = "Lista de libros asociados al usuario")
    private List<Book> books = new ArrayList<>();

    private String password;


    /**
     * Constructor por defecto
     */
    public User() {
    }

    public User(String username, String name, LocalDate birthdate, List<Book> books) {
        this.username = username;
        this.name = name;
        this.birthdate = birthdate;
        this.books = books;
    }

    public User(String username, String name, LocalDate birthdate, List<Book> books, String password) {
        this.username = username;
        this.name = name;
        this.birthdate = birthdate;
        this.books = books;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Getters y Setters
     */
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

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Obtiene la lista de libros del usuario.
     * La lista devuelta es inmodificable.
     *
     * @return Lista de libros asociados al usuario.
     */
    public List<Book> getBooks() {
        return (List<Book>) Collections.unmodifiableList(books);
    }

    /**
     * Establece una nueva lista de libros para el usuario.
     *
     * @param books Lista de libros a asignar.
     */
    public void setBooks(List<Book> books) {
        this.books = books;
    }

    /**
     * Agrega un libro a la lista del usuario.
     * Si el libro ya está en la lista, lanza una excepción.
     *
     * @param book El libro a agregar.
     * @throws BookAlreadyOwnedException Si el libro ya está en la lista del usuario.
     */
    public void addBook(Book book) {
        if (this.books == null) {
            this.books = new ArrayList<>();
        }

        if (bookExistsInUserList(this.books, book)) {
            throw new BookAlreadyOwnedException("The book with ID " + book.getId() + " and title '" + book.getTitle() + "' is already in the list.");
        }
        this.books.add(book);
    }

    /**
     * Elimina un libro de la lista del usuario.
     * Si usuario no posee libros o el libro no está en la lista, lanza una excepción.
     *
     * @param book El libro a eliminar.
     * @throws BookNotFoundException Si el libro no se encuentra en la lista del usuario.
     */
    public void removeBook(Book book) {
        if (this.books == null) {
            throw new BookNotFoundException("The user does not have a book list yet.");
        }

        if (!bookExistsInUserList(this.books, book)) {
            throw new BookNotFoundException("The book with ID " + book.getId() + " and title '" + book.getTitle() + "' is not in the list.");
        }
        this.books.remove(book);
    }

    /**
     * Verifica si un libro ya existe en la lista del usuario.
     *
     * @param existingBooks Lista de libros actual del usuario.
     * @param newBook       Libro a verificar.
     * @return {@code true} si el libro ya está en la lista, {@code false} en caso contrario.
     */
    private boolean bookExistsInUserList(List<Book> existingBooks, Book newBook) {
        return existingBooks.stream()
                .anyMatch(existingBook -> existingBook.getId().equals(newBook.getId()));
    }
}
