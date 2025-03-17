package Flock.Training.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import Flock.Training.repositories.BookRepository;
import Flock.Training.exceptions.*;
import Flock.Training.models.Book;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador REST para la gestión de libros en la API.
 */
@Tag(name = "Libros", description = "Operaciones sobre los libros")
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    @Operation(
            summary = "Obtener todos los libros",
            description = "Devuelve una lista con todos los libros de la base de datos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida correctamente")
            }
    )
    public Iterable findAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/title/{bookTitle}")
    @Operation(
            summary = "Obtener libro por su Título",
            description = "Busca un libro por su Título y lo devuelve si existe",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Libro encontrado"),
                    @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content)
            })
    public List findByTitle(@PathVariable String bookTitle) {
        return bookRepository.findByTitle(bookTitle);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener libro por su ID",
            description = "Busca un libro por su ID y lo devuelve si existe",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Libro encontrado"),
                    @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content)
            })
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
    @ApiResponse(responseCode = "201", description = "Libro creado")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear un nuevo libro",
            description = "Registra un nuevo libro en la base de datos",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Libro creado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Error en los datos enviados", content = @Content)
            }
    )
    public Book create(@RequestBody Book book) {
        try {
            return bookRepository.save(book);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "There was an error creating the book", ex);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar libro",
            description = "Elimina un libro de la base de datos si existe",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Libro eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content)
            }
    )
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
    @Operation(
            summary = "Actualizar libro",
            description = "Actualiza los datos de un libro si existe",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Libro actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content)
            }
    )
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new BookIdMismatchException("Book ID in path and body do not match");
        }
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
        return bookRepository.save(book);
    }
}
