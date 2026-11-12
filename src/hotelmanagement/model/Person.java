package hotelmanagement.model;

import java.util.Objects;

public class Person {

    //Attributes
    private String firstName;
    private String lastName;
    private String dni;

    //constructor model


    public Person(String firstName, String lastName, String dni) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
    }

    //Methods


    public String getName() {
        return firstName;
    }

    public void setName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFullName(){
        return firstName+" "+lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Person person)) return false;
        return Objects.equals(dni, person.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dni);
    }

    @Override
    public String toString() {
        return
                "fistName='" + firstName + ", " +
                        ", lastName='" + lastName + ", " +
                        ", dni='" + dni + ", ";
    }
}
