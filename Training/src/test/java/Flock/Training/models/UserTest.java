package Flock.Training.models;

import Flock.Training.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldCreateUserWithConstructor() {
        User user = new User("johndoe123", "John Doe", LocalDate.of(1990, 5, 15), new ArrayList<>());

        assertThat(user.getUsername()).isEqualTo("johndoe123");
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getBirthdate()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(user.getBooks()).isEmpty();
    }

    @Test
    void shouldSetAndGetValues() {
        User user = new User();
        user.setUsername("janedoe456");
        user.setName("Jane Doe");
        user.setBirthdate(LocalDate.of(1985, 3, 22));

        assertThat(user.getUsername()).isEqualTo("janedoe456");
        assertThat(user.getName()).isEqualTo("Jane Doe");
        assertThat(user.getBirthdate()).isEqualTo(LocalDate.of(1985, 3, 22));
    }

    @Test
    void shouldValidateUserWithNullName() {
        User user = new User();
        user.setUsername("user123");
        user.setName(null);
        user.setBirthdate(LocalDate.of(1995, 8, 10));

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
            entityManager.flush();
        });
    }


    @Test
    void shouldValidateUserWithEmptyUsername() {
        User user = new User();
        user.setUsername(null);
        user.setName("Valid Name");
        user.setBirthdate(LocalDate.of(2000, 1, 1));

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
            entityManager.flush();
        });
    }
}