package hotelmanagement.model;

import hotelmanagement.enums.UserType;

public class Receptionist extends User{

    //Attributes

    //Constructor Model

    public Receptionist(String name, String lastName, String dni, String username, String password) {
        super(name, lastName, dni, username, password, true, UserType.RECEPTIONIST);
        defaultPermissions();

    }

    //Methods


    @Override
    public String toString() {
        return "Receptionist{"+super.toString()+"}";
    }
}
