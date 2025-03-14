package Flock.Training.repositories;

import Flock.Training.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link User}.
 * <p>
 * Proporciona operaciones de acceso a datos para la entidad {@link User},
 * extendiendo {@link CrudRepository}, que ya incluye métodos básicos de CRUD.
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
