package Flock.Training.security;

import Flock.Training.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración: Seguridad
 * Objetivo: Verificar que los endpoints están protegidos correctamente.
 */

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Mockear el comportamiento del UserDetailsService para que devuelva un usuario mockeado
        when(userDetailsService.loadUserByUsername("newUser")).thenReturn(
                new User(null, "newUser", "Juan Perez", LocalDate.of(1990, 5, 15), new ArrayList<>(), "encodedPassword"));
    }

    @Test
    void whenPostToCreateUser_thenAccessibleWithoutAuth() throws Exception {
        // Mockear la contraseña encriptada
        when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");

        // Realizar la solicitud POST para crear un nuevo usuario
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newUser\", \"name\":\"Juan Perez\", \"birthdate\":\"1990-05-15\", \"books\":[], \"password\":\"1234\"}"))
                .andExpect(status().isCreated())  // 201 CREATED
                .andExpect(jsonPath("$.username").value("newUser"));  // Comprobamos que el nombre de usuario esté correcto
    }

    @Test
    void whenPostToCreateBook_thenAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Book\", \"author\":\"Author\"}"))
                .andExpect(status().isCreated()); // 201 CREATED
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void whenAccessProtectedEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk()); // 200 OK porque está autenticado
    }

    @Test
    void whenAccessProtectedEndpointWithoutAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }
}