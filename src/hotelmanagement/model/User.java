package hotelmanagement.model;

import hotelmanagement.enums.Permission;
import hotelmanagement.enums.UserType;
import hotelmanagement.exceptions.PermissionException;
import hotelmanagement.interfaces.Identifiable;

import java.util.HashSet;
import java.util.Set;

public abstract class User extends Person implements Identifiable {

    //attributes

    private int idUser;
    private String username;
    private String password;
    private boolean active;
    private Set<Permission> permission;
    private UserType type;


    //Constructor model

    protected User(String name, String lastName, String dni, String username, String password,UserType type) {
        super(name, lastName, dni);
        this.username = username;
        this.password = password;
        this.permission = new HashSet<>();
        this.type=type;
        defaultPermissions();
    }

    //Methods


    public int getId() {
        return idUser;
    }

    public void setId(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public UserType getType() {
        return type;
    }

    public boolean authenticate (String username, String password){

      return this.username.equals(username) && this.password.equals(password);
    }

    protected void defaultPermissions(){
        permission.addAll(type.getDefaultPermissions());
    };
    public boolean hasPermission(Permission p){
        return permission.contains(p);
    }

    public boolean grantPermission(Permission p){
        return permission.add(p);
    }

    public boolean revokePermission(Permission p) throws PermissionException {
        if (!permission.contains(p)){
            throw new PermissionException("The user does not have this permission.");
        }
        return permission.remove(p);
    }


    public Set<Permission> getPermission() {
        return permission;
    }


    public void setPermissions(Set<Permission> p){
        this.permission=p;
    }

    @Override
    public String toString() {
        return super.toString() +
                "idUser=" + idUser +
                ", username= " + username +"}"
                ;
    }
}
