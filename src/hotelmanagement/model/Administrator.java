package hotelmanagement.model;

import hotelmanagement.enums.UserType;

public class Administrator extends User{
//Attributes

    //Constructor Model

    public Administrator(String name, String lastName, String dni, String username, String password) {
        super(name, lastName, dni, username, password, true, UserType.ADMINISTRATOR);

    }

    //Methods





    @Override
    public String toString() {
        return "Administrator {"+super.toString()+"}";
    }
}
