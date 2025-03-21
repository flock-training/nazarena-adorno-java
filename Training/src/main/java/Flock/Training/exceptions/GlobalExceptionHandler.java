package Flock.Training.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({BookNotFoundException.class, UserNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({BookIdMismatchException.class, UserIdMismatchException.class,
            ConstraintViolationException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleBadRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getLocalizedMessage(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Maneja excepciones de tipo ApiResponseException.
     *
     * @param ex Excepción personalizada que contiene el código de estado HTTP y el mensaje de error.
     * @return ResponseEntity con el mensaje de error y el código de estado definido en la excepción.
     */
    @ExceptionHandler(ApiResponseException.class)
    public ResponseEntity<String> handleApiResponseException(ApiResponseException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}

