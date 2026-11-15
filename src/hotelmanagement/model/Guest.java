package hotelmanagement.model;

import hotelmanagement.interfaces.Identifiable;

public class Guest extends Person implements Identifiable {
    //Attributes
    private int idGuest;
    private String country;
    private String homeAddress;

    //Constructor model

    public Guest(String name, String lastName, String dni, String country, String homeAddress) {
        super(name, lastName, dni);

        this.country = country;
        this.homeAddress = homeAddress;
    }

    //Methods

    public int getId() {
        return idGuest;
    }

    public void setId(int idGuest) {
        this.idGuest = idGuest;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Override
    public String toString() {
        return "Passenger{"+super.toString() +
                "idPassenger=" + idGuest +
                ", country='" + country + '\'' +
                ", homeAddress='" + homeAddress + '\'' +
                '}';
    }
}
