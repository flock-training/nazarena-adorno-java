package Flock.Training.controllers;

import Flock.Training.exceptions.*;
import Flock.Training.models.*;
import Flock.Training.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public Iterable findAll() {
        return userRepository.findAll();
    }

    @GetMapping("/username/{username}")
    public Optional<User> findByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param user Nuevo objeto usuario a guardar.
     * @return Usuario guardado.
     * @throws ResponseStatusException Si ocurre un error al crear el usuario.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        try {
            return userRepository.save(user);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "There was an error creating the user", ex);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        userRepository.deleteById(id);
    }

    /**
     * Actualiza un usuario existente en la base de datos.
     *
     * @param user El objeto usuario con los nuevos datos.
     * @param id   El ID del usuario a actualizar.
     * @return El usuario actualizado.
     * @throws UserIdMismatchException Si el ID en la ruta y el cuerpo del objeto no coinciden.
     * @throws UserNotFoundException   Si el libro con el ID especificado no existe.
     */
    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable Long id) {
        if (user.getId() != id) {
            throw new UserIdMismatchException("User ID in path and body do not match");
        }
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        return userRepository.save(user);
    }

    /**
     * Agrega un libro a la lista de libros del usuario.
     *
     * @param userId El ID del usuario propietario de la lista a actualizar.
     * @param bookId El ID del nuevo libro para agregar a la lista.
     * @return El usuario con el libro agregado a su lista.
     * @throws UserNotFoundException Si el usuario con el ID especificado no existe.
     * @throws BookNotFoundException Si el libro con el ID especificado no existe.
     */

    @PostMapping("/{userId}/books/{bookId}")
    public User addBookFromUser(@PathVariable Long userId, @PathVariable Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        user.addBook(book);
        return userRepository.save(user);
    }

    /**
     * Elimina un libro de la lista de libros del usuario.
     *
     * @param userId ID del usuario propietario de la lista a actualizar.
     * @param bookId ID del libro a eliminar.
     * @return Usuario con el libro eliminado de su lista.
     * @throws UserNotFoundException Si el usuario no existe.
     * @throws BookNotFoundException Si el libro con el ID especificado no existe.
     */
    @DeleteMapping("/{userId}/books/{bookId}")
    public User removeBookFromUser(@PathVariable Long userId, @PathVariable Long bookId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        user.removeBook(book);
        return userRepository.save(user);
    }

    /**
     * Actualiza la lista de libros del usuario con una nueva lista de libros.
     *
     * @param userId  ID del usuario.
     * @param bookIds Lista de IDs de libros que se deben asignar al usuario.
     * @return Usuario con la nueva lista de libros asignada.
     * @throws UserNotFoundException si el usuario no existe.
     */
    @PutMapping("/{userId}/books")
    public User updateUserBooks(@PathVariable Long userId, @RequestBody List<Long> bookIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        Iterable<Book> bookIterable = bookRepository.findAllById(bookIds);
        List<Book> books = StreamSupport.stream(bookIterable.spliterator(), false)
                .collect(Collectors.toList());
        user.setBooks(books);

        return userRepository.save(user);
    }
}
