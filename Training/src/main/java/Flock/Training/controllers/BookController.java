package Flock.Training.controllers;

import Flock.Training.dtos.BookInfoDTO;
import Flock.Training.exceptions.BookIdMismatchException;
import Flock.Training.exceptions.BookNotFoundException;
import Flock.Training.factories.BookFactory;
import Flock.Training.models.Book;
import Flock.Training.repositories.BookRepository;
import Flock.Training.services.OpenLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la gestión de libros en la API.
 */
@Tag(name = "Libros", description = "Operaciones sobre los libros")
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookFactory bookFactory;

    @Autowired
    private OpenLibraryService openLibraryService;

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

    @GetMapping("/search")
    @Operation(
            summary = "Obtener lista de libros por Editorial, Género y Año.",
            description = "Busca libros que coincidan con los parámetros ingresados y los devuelve si existen.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Libros encontrados"),
                    @ApiResponse(responseCode = "404", description = "Libros no encontrados")
            })
    public List<Book> findBooks(@RequestParam String publisher, @RequestParam String genre, @RequestParam String year) {
        List<Book> books = bookRepository.findByPublisherAndGenreAndYear(publisher, genre, year);

        if (books.isEmpty()) {
            throw new BookNotFoundException("No books were found for the entered parameters");
        }

        return books;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
        if (!book.getId().equals(id)) {
            throw new BookIdMismatchException("Book ID in path and body do not match");
        }
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
        return bookRepository.save(book);
    }


    @GetMapping("isbn/{isbn}")
    @Operation(
            summary = "Buscar libro por su ISBN en base de datos y en la API de Open Library",
            description = "Busca un libro por su ISBN en la base de datos local y lo devuelve si existe." +
                    "Si no lo encuentra, lo busca en la API de Open Library, guarda el libro hallado en la base local y devuelve la entidad creada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Libro encontrado en la base de datos local"),
                    @ApiResponse(responseCode = "201", description = "Libro encontrado en la API externa y creado en la base de datos"),
                    @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content)
            })
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        // Buscar el libro en la base de datos local
        Optional<Book> localBook = bookRepository.findByIsbn(isbn);

        if (localBook.isPresent()) {
            return ResponseEntity.ok(localBook.get()); // 200 OK
        }

        // Si no se encuentra en la base de datos, buscarlo en la API externa
        BookInfoDTO externalBook = openLibraryService.getBookInfo(isbn);

        if (externalBook == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        // Guardar en la base de datos para futuras búsquedas
        Book newBook = bookFactory.createBook(externalBook);
        bookRepository.save(newBook);

        // Guardar el libro en la BD y devolver 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
    }
}
