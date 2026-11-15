package hotelmanagement.exceptions;

/**
 * Thrown when an entity with the requested id or DNI does not exist.
 */
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {super(message);}
}