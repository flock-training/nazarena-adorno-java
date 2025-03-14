package Flock.Training.exceptions;

/**
 * Excepción que se lanza si no se encuentra el usuario en la base de datos.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
