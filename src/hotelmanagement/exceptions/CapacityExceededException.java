package hotelmanagement.exceptions;

public class CapacityExceededException extends Exception {
    public CapacityExceededException(String message) {
        super(message);
    }
}
