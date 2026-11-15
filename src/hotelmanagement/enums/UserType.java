package hotelmanagement.enums;


import java.util.Set;

public enum UserType {

    ADMINISTRATOR(Set.of(
            Permission.MANAGE_USERS,
            Permission.MANAGE_ROOMS,
            Permission.PERFORM_BACKUP,
            Permission.MANAGE_RESERVATIONS,
            Permission.DO_CHECK_IN_OUT
    )),
    RECEPTIONIST(Set.of(
            Permission.MANAGE_RESERVATIONS,
            Permission.DO_CHECK_IN_OUT,
            Permission.MANAGE_ROOMS
    ));

    private final Set<Permission> defaultPermissions;

    UserType(Set<Permission> defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }

    public Set<Permission> getDefaultPermissions() {
        return defaultPermissions;
    }



}
