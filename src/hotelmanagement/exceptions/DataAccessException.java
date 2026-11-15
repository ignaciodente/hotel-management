package hotelmanagement.exceptions;

public class DataAccessException extends PersistenceException {
    public DataAccessException(String message) {
        super(message);
    }
}
