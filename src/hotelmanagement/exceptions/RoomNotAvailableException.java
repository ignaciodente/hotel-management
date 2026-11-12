package hotelmanagement.exceptions;

/**
 * Thrown when a room is out of service or already reserved for the requested period.
 */
public class RoomNotAvailableException extends Exception {

    public RoomNotAvailableException(String message) {
        super(message);
    }
}