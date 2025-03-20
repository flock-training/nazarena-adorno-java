package Flock.Training.repositories;

import Flock.Training.models.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Book}.
 * <p>
 * Proporciona operaciones de acceso a datos para la entidad {@link Book},
 * extendiendo {@link CrudRepository}, que ya incluye métodos básicos de CRUD.
 */
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByTitle(String title);

    Optional<Book> findByIsbn(String isbn);
}
