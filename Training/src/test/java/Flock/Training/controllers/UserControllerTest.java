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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User("johndoe123", "John Doe", LocalDate.of(1990, 5, 15), new ArrayList<>());
        book = new Book("Narrativa", "J.R.R Tolkien", "http://urlImagen.com",
                "El señor de los anillos", "El retorno del rey",
                "Planeta", "1999", 1348, "7856974123652");
        book.setId(BOOK_ID);
    }

    private void mockUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
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
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

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
    @WithMockUser(username = "user", roles = "USER")
    void shouldAddBookToUser() throws Exception {
        mockUserExists();
        mockBookExists();
        when(userRepository.save(any(User.class))).thenReturn(user);

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
        user.addBook(book);
        mockUserExists();
        mockBookExists();
        when(userRepository.save(any(User.class))).thenReturn(user);

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
        User savedUser = new User();
        savedUser.setUsername("juanPerez123");
        savedUser.setPassword("encodedPassword"); // Simulación de la contraseña encriptada

        // Simular el comportamiento del servicio al guardar el usuario
        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post(URL_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getUserJson())) // Simula un JSON de usuario
                .andExpect(status().isCreated()) // Verifica que responde con 201 Created
                .andExpect(jsonPath("$.username").value("juanPerez123")); // Verifica username
    }
}
