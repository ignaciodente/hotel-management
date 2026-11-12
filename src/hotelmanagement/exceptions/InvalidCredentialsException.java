package hotelmanagement.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("InvalidCredential");
    }
}
