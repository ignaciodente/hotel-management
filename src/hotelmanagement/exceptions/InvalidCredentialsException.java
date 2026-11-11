package hotelmanagement.exceptions;

/**
 * Thrown when the username or password is incorrect.
 */
public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}