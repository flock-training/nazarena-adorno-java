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

    private Book bookWithId;

    private Book bookWithoutId;

    private BookInfoDTO bookInfoDTO;

    private static final String URL_API = "/api/books";

    private static String isbn;


    @BeforeEach
    void setUp() {

        bookWithoutId = Book.builder()
                .id(null)
                .genre("Narrativa")
                .author("J.R.R Tolkien")
                .image("http://urlImagen.com")
                .title("El señor de los anillos")
                .subtitle("El retorno del rey")
                .publisher("Planeta")
                .year("1999")
                .pages(1348)
                .isbn("7856974123652")
                .build();

        bookWithId = bookWithoutId.toBuilder().id(1L).build();

        bookInfoDTO = new BookInfoDTO(
                "7856974123652",
                "El señor de los anillos",
                "El retorno del rey",
                "Planeta",
                "1999",
                1348,
                List.of("J.R.R Tolkien")
        );

        isbn = bookWithId.getIsbn();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
        // Simula un usuario autenticado
    void shouldGetBookById() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookWithId));

        performGet(URL_API + "/1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(bookWithId.getTitle()))
                .andExpect(jsonPath("$.author").value(bookWithId.getAuthor()));
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
        when(bookRepository.findByTitle(bookWithId.getTitle())).thenReturn(List.of(bookWithId));

        performGet(URL_API + "/title/" + bookWithId.getTitle())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value(bookWithId.getAuthor()));
    }

    @Test
    void shouldCreateBook() throws Exception {
        when(bookRepository.save(any(Book.class))).thenReturn(bookWithoutId);

        performPost(URL_API, bookWithoutId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(bookWithoutId.getTitle()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDeleteBook() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookWithId));
        doNothing().when(bookRepository).deleteById(1L);

        performDelete(URL_API + "/1")
                .andExpect(status().isNoContent());

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnBookFromDatabase() throws Exception {
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookWithId));

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
        when(bookRepository.save(any(Book.class))).thenReturn(bookWithoutId);

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