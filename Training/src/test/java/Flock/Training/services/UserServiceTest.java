package Flock.Training.services;

import Flock.Training.TrainingApplication;
import Flock.Training.models.User;
import Flock.Training.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Prueba unitaria: Creación de usuario
 * Objetivo: Probar que el servicio guarda correctamente un usuario con la contraseña encriptada.
 */
@SpringBootTest(classes = TrainingApplication.class)
@AutoConfigureMockMvc
@MockBean(SecurityFilterChain.class)
@ComponentScan(basePackages = "Flock.Training.services")
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("UserService in Test: " + userService); // Verifica si es null
    }

    @Test
    void whenCreateUser_thenPasswordIsEncodedAndSaved() {
        // Arrange
        User user = new User(null, "testUser", "Juan Perez", LocalDate.of(1990, 5, 15), new ArrayList<>(), "plainPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User savedUser = userService.saveUser(user);

        // Assert
        assertNotNull(savedUser, "El usuario guardado no debe ser null");
        assertNotEquals("plainPassword", savedUser.getPassword(), "La contraseña debería estar encriptada");
        verify(userRepository, times(1)).save(any(User.class));
    }
}
