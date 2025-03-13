package Flock.Training.exceptions;

public class BookAlreadyOwnedException extends RuntimeException {
    public BookAlreadyOwnedException(String message) {
        super(message);
    }
}
