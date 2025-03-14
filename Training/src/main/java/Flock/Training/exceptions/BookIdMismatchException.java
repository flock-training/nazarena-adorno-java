package Flock.Training.exceptions;

/**
 * Excepción que se lanza al intentar modificar un libro si el ID en la ruta y el cuerpo del objeto no coinciden.
 */
public class BookIdMismatchException extends RuntimeException {
    public BookIdMismatchException(String message) {
        super(message);
    }
}
