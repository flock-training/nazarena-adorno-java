package Flock.Training.controllers;

import static org.mockito.Mockito.*;

import Flock.Training.exceptions.GlobalExceptionHandler;
import Flock.Training.models.Book;
import Flock.Training.models.User;
import Flock.Training.repositories.BookRepository;
import Flock.Training.repositories.UserRepository;
import Flock.Training.services.UserService;
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

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "Flock.Training.controllers")
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private UserService userService;

    private static final Long USER_ID = 1L;
    private static final Long BOOK_ID = 100L;
    private static final String URL_API = "/api/users";

    private User userWithoutId;
    private User userWithId;
    private Book book;

    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        userWithoutId = User.builder()
                .username("johndoe123")
                .name("John Doe")
                .birthdate(LocalDate.of(1990, 5, 15))
                .books(new ArrayList<>())
                .password("encodedPassword")
                .build();

        userWithId = userWithoutId.toBuilder().id(USER_ID).build();

        book = Book.builder()
                .id(BOOK_ID)
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

        mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser"); // Simula un usuario autenticado
    }

    private void mockUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userWithId));
    }

    private void mockBookExists() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
    }

    private void mockUserNotExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    }

    private void mockBookNotExists() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());
    }

    private String getUserJson() {
        return """
                {
                  "username": "johndoe123",
                  "name": "John Doe",
                  "birthdate": "1990-05-15",
                  "books": []
                }
                """;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldGetAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(userWithId));

        mockMvc.perform(get(URL_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("johndoe123"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldGetUserById() throws Exception {
        mockUserExists();

        mockMvc.perform(get(URL_API + "/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe123"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn404WhenUserNotFound() throws Exception {
        mockUserNotExists();

        mockMvc.perform(get(URL_API + "/{userId}", USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void shouldReturnCurrentUserNameWhenGetUsername() throws Exception {
        mockMvc.perform(get(URL_API + "/username").principal(mockPrincipal))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(content().string("testUser")); // Verifica que el contenido de la respuesta sea el nombre de usuario
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldAddBookToUser() throws Exception {
        mockUserExists();
        mockBookExists();
        when(userRepository.save(any(User.class))).thenReturn(userWithId);

        mockMvc.perform(post(URL_API + "/{userId}/books/{bookId}", USER_ID, BOOK_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn404WhenAddingBookToNonExistingUser() throws Exception {
        mockUserNotExists();

        mockMvc.perform(post(URL_API + "/{userId}/books/{bookId}", USER_ID, BOOK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn404WhenAddingNonExistingBookToUser() throws Exception {
        mockUserExists();
        mockBookNotExists();

        mockMvc.perform(post(URL_API + "/{userId}/books/{bookId}", USER_ID, BOOK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldRemoveBookFromUser() throws Exception {
        userWithId.addBook(book);
        mockUserExists();
        mockBookExists();
        when(userRepository.save(any(User.class))).thenReturn(userWithId);

        mockMvc.perform(delete(URL_API + "/{userId}/books/{bookId}", USER_ID, BOOK_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn404WhenRemovingBookFromNonExistingUser() throws Exception {
        mockUserNotExists();

        mockMvc.perform(delete(URL_API + "/{userId}/books/{bookId}", USER_ID, BOOK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn404WhenRemovingNonExistingBookFromUser() throws Exception {
        mockUserExists();
        mockBookNotExists();

        mockMvc.perform(delete(URL_API + "/{userId}/books/{bookId}", USER_ID, BOOK_ID))
                .andExpect(status().isNotFound());
    }

    /**
     * Prueba unitaria: Controlador de usuarios
     * Creación de usuarios utilizando userService
     */
    @Test
    void shouldCreateUser() throws Exception {
        // Simular el comportamiento del servicio al guardar el usuario
        when(userService.saveUser(any(User.class))).thenReturn(userWithoutId);

        mockMvc.perform(post(URL_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getUserJson())) // Simula un JSON de usuario
                .andExpect(status().isCreated()) // Verifica que responde con 201 Created
                .andExpect(jsonPath("$.username").value("johndoe123")); // Verifica username
    }
}
