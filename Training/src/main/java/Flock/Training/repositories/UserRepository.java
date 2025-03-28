package Flock.Training.repositories;

import Flock.Training.models.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link User}.
 * <p>
 * Proporciona operaciones de acceso a datos para la entidad {@link User},
 * extendiendo {@link CrudRepository}, que ya incluye métodos básicos de CRUD.
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByBirthdateBetweenAndNameContainingIgnoreCase(LocalDate startDate, LocalDate endDate, String namePart);
}
