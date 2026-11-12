package hotelmanagement.exceptions;

/**
 * Thrown when the number of guests exceeds the capacity of the room.
 */
public class CapacityExceededException extends Exception {

    public CapacityExceededException(String message) {
        super(message);
    }
}