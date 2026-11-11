package hotelmanagement.model;

import hotelmanagement.enums.Permission;
import hotelmanagement.enums.UserType;
import hotelmanagement.exceptions.UserException;
import hotelmanagement.interfaces.Identifiable;

import java.util.HashSet;

public class User extends Person implements Identifiable {

    //attributes

    private int idUser;
    private static int countUser=0;
    private String username;
    private String password;
    private boolean active;
    private HashSet<Permission> permission;
    private UserType type;


    //Constructor model

    public User(String name, String lastName, String dni, String username, String password, boolean active,UserType type) {
        super(name, lastName, dni);
        countUser++;
        this.idUser = countUser;
        this.username = username;
        this.password = password;
        this.active = active;
        this.permission = new HashSet<>();
        this.type=type;
    }

    //Methods


    public int getId() {
        return idUser;
    }

    public void setIdUser(int idUser) {
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

    public HashSet<Permission> getPermission() {
        return permission;
    }

    public String getType() {
        return type.name();
    }

    public boolean authenticate (String username, String password) throws UserException{
      if(!this.active){
          throw new UserException("User account is inactive");
      }
      if(username==null || password==null){
          throw new UserException("Username and password cannot be null");
      }
      return this.username.equals(username) && this.password.equals(password);
    }

    protected void defaultPermissions(){
        permission.addAll(type.getDefaultPermissions());
    };
    protected boolean hasPermission(Permission p){
        return permission.contains(p);
    }

    protected boolean setPermission(Permission p){
        return permission.add(p);
    }

    protected boolean removePermission(Permission p){
        return permission.remove(p);
    }




    @Override
    public String toString() {
        return super.toString() +
                "idUser=" + idUser +
                ", username= " + username +"}"
                ;
    }
}
