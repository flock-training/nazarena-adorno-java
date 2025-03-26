package Flock.Training.controllers;

import Flock.Training.dtos.BookInfoDTO;
import Flock.Training.exceptions.GlobalExceptionHandler;
import Flock.Training.models.Book;
import Flock.Training.repositories.BookRepository;
import Flock.Training.repositories.UserRepository;
import Flock.Training.services.OpenLibraryService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @MockBean
    private OpenLibraryService openLibraryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;

    private BookInfoDTO bookInfoDTO;

    private static final String URL_API = "/api/books";

    private static String isbn;

    @BeforeEach
    void setUp() {
        book = new Book("Narrativa", "J.R.R Tolkien", "http://urlImagen.com",
                "El señor de los anillos", "El retorno del rey",
                "Planeta", "1999", 1348, "7856974123652");
        book.setId(1L);

        bookInfoDTO = new BookInfoDTO(
                "7856974123652",
                "El señor de los anillos",
                "El retorno del rey",
                "Planeta",
                "1999",
                1348,
                List.of("J.R.R Tolkien")
        );

        isbn = book.getIsbn();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
        // Simula un usuario autenticado
    void shouldGetBookById() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        performGet(URL_API + "/1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.author").value(book.getAuthor()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        performGet(URL_API + "/99")
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldGetBooksByTitle() throws Exception {
        when(bookRepository.findByTitle(book.getTitle())).thenReturn(List.of(book));

        performGet(URL_API + "/title/" + book.getTitle())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value(book.getAuthor()));
    }

    @Test
    void shouldCreateBook() throws Exception {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        performPost(URL_API, book)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDeleteBook() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).deleteById(1L);

        performDelete(URL_API + "/1")
                .andExpect(status().isOk());

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnBookFromDatabase() throws Exception {
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        mockMvc.perform(get(URL_API + "/isbn/" + isbn).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("El señor de los anillos"));

        verify(bookRepository, times(1)).findByIsbn(isbn);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldFetchFromExternalApiAndSave() throws Exception {
        when(bookRepository.findByIsbn("7856974123652")).thenReturn(Optional.empty());
        when(openLibraryService.getBookInfo("7856974123652")).thenReturn(bookInfoDTO);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        mockMvc.perform(get(URL_API + "/isbn/" + isbn).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("El señor de los anillos"));

        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(openLibraryService, times(1)).getBookInfo(isbn);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnNotFoundWhenBookDoesNotExistAnywhere() throws Exception {
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
        when(openLibraryService.getBookInfo(isbn)).thenReturn(null);

        mockMvc.perform(get(URL_API + "/isbn/" + isbn).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(openLibraryService, times(1)).getBookInfo(isbn);
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