package hotelmanagement.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("AccessDenied");
    }
}
