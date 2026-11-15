package hotelmanagement.exceptions;

/**
 * Thrown when a reservation is not in the right status for the requested operation.
 */
public class InvalidReservationException extends Exception {

    public InvalidReservationException(String message) {
        super(message);
    }
}