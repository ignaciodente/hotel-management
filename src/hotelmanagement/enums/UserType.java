package hotelmanagement.enums;

import java.util.List;

public enum UserType {

    ADMINISTRATOR(List.of(
            Permission.MANAGE_USERS,
            Permission.MANAGE_ROOMS,
            Permission.PERFORM_BACKUP,
            Permission.MANAGE_RESERVATIONS,
            Permission.DO_CHECK_IN_OUT
    )),
    RECEPTIONIST(List.of(
            Permission.MANAGE_RESERVATIONS,
            Permission.DO_CHECK_IN_OUT,
            Permission.MANAGE_ROOMS
    ));

    private final List<Permission> defaultPermissions;

    UserType(List<Permission> defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }

    public List<Permission> getDefaultPermissions() {
        return defaultPermissions;
    }



}
