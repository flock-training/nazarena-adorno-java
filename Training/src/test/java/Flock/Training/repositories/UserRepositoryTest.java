package Flock.Training.repositories;

import static org.junit.jupiter.api.Assertions.*;

import Flock.Training.models.User;
import Flock.Training.services.OpenLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@MockBean(OpenLibraryService.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user; // Un solo usuario para todas las pruebas

    @BeforeEach
    void setUp() {
        user = new User("johndoe123", "John Doe", LocalDate.of(1990, 5, 15), new ArrayList<>());
    }

    @Test
    void shouldSaveAndFindUserById() {
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("johndoe123", foundUser.get().getUsername());
    }

    @Test
    void shouldFindUserByUsername() {
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("johndoe123");

        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
    }

    @Test
    void shouldDeleteUser() {
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> foundUser = userRepository.findById(999L);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldNotFindUserWithInvalidUsername() {
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("usuario_inexistente");
        assertTrue(foundUser.isEmpty());
    }
}
