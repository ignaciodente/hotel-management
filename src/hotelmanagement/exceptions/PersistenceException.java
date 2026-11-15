package hotelmanagement.exceptions;

//father class for the rest of the exceptions that happen with persistence issues
public class PersistenceException extends Exception {
    public PersistenceException(String message) {
        super(message);
    }
}
