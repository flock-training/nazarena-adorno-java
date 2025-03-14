package Flock.Training.exceptions;

/**
 * Excepción que se lanza al intentar agregar un libro ya existente en la lista de un usuario.
 */
public class BookAlreadyOwnedException extends RuntimeException {
    public BookAlreadyOwnedException(String message) {
        super(message);
    }
}
