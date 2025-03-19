package Flock.Training.controllers;

import Flock.Training.exceptions.GlobalExceptionHandler;
import Flock.Training.models.Book;
import Flock.Training.repositories.BookRepository;
import Flock.Training.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "Flock.Training.controllers")
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Narrativa", "J.R.R Tolkien", "http://urlImagen.com",
                "El señor de los anillos", "El retorno del rey",
                "Planeta", "1999", 1348, "7856974123652");
        book.setId(1L);
    }

    @Test
    void shouldGetBookById() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        performGet("/api/books/1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.author").value(book.getAuthor()));
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        performGet("/api/books/99")
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetBooksByTitle() throws Exception {
        when(bookRepository.findByTitle(book.getTitle())).thenReturn(List.of(book));

        performGet("/api/books/title/" + book.getTitle())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value(book.getAuthor()));
    }

    @Test
    void shouldCreateBook() throws Exception {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        performPost("/api/books", book)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).deleteById(1L);

        performDelete("/api/books/1")
                .andExpect(status().isOk());

        verify(bookRepository, times(1)).deleteById(1L);
    }

    // Métodos auxiliares
    private org.springframework.test.web.servlet.ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    private org.springframework.test.web.servlet.ResultActions performPost(String url, Object content) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private org.springframework.test.web.servlet.ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url));
    }
}