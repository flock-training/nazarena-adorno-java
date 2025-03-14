package Flock.Training.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import Flock.Training.repositories.BookRepository;
import Flock.Training.exceptions.*;
import Flock.Training.models.Book;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador REST para la gestión de libros en la API.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public Iterable findAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/title/{bookTitle}")
    public List findByTitle(@PathVariable String bookTitle) {
        return bookRepository.findByTitle(bookTitle);
    }

    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
    }

    /**
     * Crea un nuevo libro.
     *
     * @param book Nuevo objeto libro a guardar.
     * @return Libro guardado.
     * @throws ResponseStatusException Si ocurre un error al crear el libro.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        try {
            return bookRepository.save(book);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "There was an error creating the book", ex);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
        bookRepository.deleteById(id);
    }

    /**
     * Actualiza un libro existente en la base de datos.
     *
     * @param book El objeto libro con los nuevos datos.
     * @param id   El ID del libro a actualizar.
     * @return El libro actualizado.
     * @throws BookIdMismatchException Si el ID en la ruta y el cuerpo del objeto no coinciden.
     * @throws BookNotFoundException   Si el libro con el ID especificado no existe.
     */
    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new BookIdMismatchException("Book ID in path and body do not match");
        }
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
        return bookRepository.save(book);
    }
}
