package Flock.Training.models;

import Flock.Training.repositories.BookRepository;
import Flock.Training.services.OpenLibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@MockBean(OpenLibraryService.class)
@MockBean(SecurityFilterChain.class)
@DataJpaTest
class BookTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldSaveBookWithValidData() {
        Book book = new Book();
        book.setTitle("El Señor de los Anillos");
        book.setAuthor("J.R.R. Tolkien");
        book.setPublisher("Planeta");
        book.setYear("1999");
        book.setPages(1348);
        book.setIsbn("7856974123652");

        // No debería lanzar excepción
        assertDoesNotThrow(() -> bookRepository.save(book));
    }
}