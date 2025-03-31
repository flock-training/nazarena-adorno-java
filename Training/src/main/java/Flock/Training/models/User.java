package Flock.Training.models;

import Flock.Training.exceptions.BookAlreadyOwnedException;
import Flock.Training.exceptions.BookNotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
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
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    private String password;

    public User(String username, String password) {
        this.username = username;
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
     * Obtiene la lista de libros del usuario como una lista inmutable.
     */
    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    /**
     * Agrega un libro a la lista del usuario, verificando si ya existe.
     */
    public void addBook(Book book) {
        if (bookExistsInUserList(book)) {
            throw new BookAlreadyOwnedException("The book with ID " + book.getId() + " and title '" + book.getTitle() + "' is already in the list.");
        }
        this.books.add(book);
    }

    /**
     * Elimina un libro de la lista del usuario si existe.
     */
    public void removeBook(Book book) {
        if (!bookExistsInUserList(book)) {
            throw new BookNotFoundException("The book with ID " + book.getId() + " and title '" + book.getTitle() + "' is not in the list.");
        }
        this.books.remove(book);
    }

    /**
     * Verifica si un libro ya existe en la lista del usuario.
     */
    private boolean bookExistsInUserList(Book book) {
        return books.stream().anyMatch(existingBook -> existingBook.getId().equals(book.getId()));
    }
}
