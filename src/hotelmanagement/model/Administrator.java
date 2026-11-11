package hotelmanagement.model;

import hotelmanagement.enums.Permission;
import hotelmanagement.enums.UserType;
import hotelmanagement.exceptions.PermissionException;
import hotelmanagement.exceptions.UserException;

import java.util.HashSet;

public class Administrator extends User{
//Attributes

    //Constructor Model

    public Administrator(String name, String lastName, String dni, int idUsuario, String username, String password) {
        super(name, lastName, dni, username, password, true, UserType.ADMINISTRATOR);
        defaultPermissions();
    }

    //Methods

    public boolean grantPermission(User u, Permission p) throws PermissionException, UserException {
        HashSet<Permission> list= u.getPermission();
        if(!this.isActive()){
            throw new UserException("An inactive administrator cannot perform this action.");
        }
        if(this.hasPermission(Permission.MANAGE_USERS)){
            throw new PermissionException("You do not have permission to manage user permissions.");
        }
        if (u==null){
            throw new UserException("No user was specified.");
        }
        if (!u.isActive()) {
            throw new UserException("Cannot grant permissions to an inactive user.");
        }

        if(list.contains(p)){
            throw new PermissionException("User already has the permission.");
        }
        if (p==null){
            throw new PermissionException("No permission was specified.");
        }
        return u.setPermission(p);
    }


    public void revokePermission(User u, Permission p) throws PermissionException,UserException{
        if (!this.isActive()) {
            throw new UserException("An inactive administrator cannot perform this action.");
        }
        if (!this.hasPermission(Permission.MANAGE_USERS)) {
            throw new PermissionException("You do not have permission to manage user permissions.");
        }
        if (u == null) {
            throw new UserException("User cannot be null.");
        }
        if (!u.isActive()) {
            throw new UserException("Cannot revoke permissions from an inactive user.");
        }
        if (p == null) {
            throw new PermissionException("No permission was specified.");
        }
        if (this.equals(u) && p == Permission.MANAGE_USERS) {
            throw new PermissionException("An administrator cannot revoke their own user management permission.");
        }
        if (!u.hasPermission(p)) {
            throw new PermissionException("The user does not have this permission.");
        }

        u.removePermission(p);
    }



    @Override
    public String toString() {
        return "Administrator {"+super.toString()+"}";
    }
}
