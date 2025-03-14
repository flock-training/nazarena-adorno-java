package Flock.Training.exceptions;

/**
 * Excepción que se lanza al intentar modificar un usuario si el ID en la ruta y el cuerpo del objeto no coinciden.
 */
public class UserIdMismatchException extends RuntimeException {
    public UserIdMismatchException(String message) {
        super(message);
    }
}
