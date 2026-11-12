package hotelmanagement.exceptions;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException() {
        super("Entity Not Found");
    }
}
