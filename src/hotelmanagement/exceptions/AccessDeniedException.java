package hotelmanagement.exceptions;

/**
 * Thrown when the logged-in user does not have the required permission.
 */
public class AccessDeniedException extends Exception {

    public AccessDeniedException(String message) {
        super(message);
    }
}