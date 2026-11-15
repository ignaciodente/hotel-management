package hotelmanagement.exceptions;

public class FailedConnectionException extends PersistenceException {
    public FailedConnectionException(String message) {
        super(message);
    }
}
