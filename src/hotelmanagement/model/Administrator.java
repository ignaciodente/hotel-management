package hotelmanagement.model;

import hotelmanagement.enums.Permission;
import hotelmanagement.enums.UserType;
import hotelmanagement.exceptions.PermissionException;

public class Administrator extends User{
//Attributes

    //Constructor Model

    public Administrator(String name, String lastName, String dni, String username, String password) {
        super(name, lastName, dni, username, password, UserType.ADMINISTRATOR);

    }

    //Methods

    public boolean revokePermission(Permission p)throws PermissionException{
        throw new PermissionException("Permissions cannot be revoked from an administrator");
    }



    @Override
    public String toString() {
        return "Administrator {"+super.toString()+"}";
    }
}
