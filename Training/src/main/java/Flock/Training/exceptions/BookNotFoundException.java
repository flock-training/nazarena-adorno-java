package Flock.Training.exceptions;

/**
 * Excepción que se lanza si no se encuentra el ID del libro en la lista del usuario.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
