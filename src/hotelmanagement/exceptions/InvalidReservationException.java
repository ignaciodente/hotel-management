package hotelmanagement.exceptions;

public class InvalidReservationException extends RuntimeException {
    public InvalidReservationException() {
        super("Invalid Reservation");
    }
}
