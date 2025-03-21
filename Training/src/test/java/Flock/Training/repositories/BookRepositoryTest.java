package Flock.Training.repositories;

import Flock.Training.models.Book;
import Flock.Training.services.OpenLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@MockBean(OpenLibraryService.class)
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book book; // Un solo libro para todas las pruebas

    @BeforeEach
    void setUp() {
        book = new Book("Narrativa", "J.R.R Tolkien", "http://urlImagen.com",
                "El señor de los anillos", "El retorno del rey", "Planeta",
                "1999", 1348, "7856974123652");
    }

    @Test
    void shouldSaveAndFindBookById() {
        Book savedBook = bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("J.R.R Tolkien", foundBook.get().getAuthor());
    }

    @Test
    void shouldFindBookByTitle() {
        bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findByTitle("El señor de los anillos").stream().findAny();

        assertTrue(foundBook.isPresent());
        assertEquals("J.R.R Tolkien", foundBook.get().getAuthor());
    }

    @Test
    void shouldDeleteBook() {
        Book savedBook = bookRepository.save(book);

        bookRepository.deleteById(savedBook.getId());

        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());
        assertTrue(foundBook.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenBookNotFound() {
        Optional<Book> foundBook = bookRepository.findById(999L);
        assertTrue(foundBook.isEmpty());
    }

    @Test
    void shouldNotFindBookWithInvalidTitle() {
        bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findByTitle("Título Inexistente").stream().findAny();
        assertTrue(foundBook.isEmpty());
    }
}