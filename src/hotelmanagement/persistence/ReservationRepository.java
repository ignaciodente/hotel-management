package hotelmanagement.persistence;
import hotelmanagement.generics.Registry;
import hotelmanagement.interfaces.Repository;
import hotelmanagement.model.Guest;
import hotelmanagement.model.Reservation;
import hotelmanagement.model.Room;
import hotelmanagement.model.User;

import java.sql.ResultSet;
import java.util.List;

public class ReservationRepository implements Repository {

    //attributes
    private Registry guests;
    private Registry rooms;
    private Registry users;

    //constructor
    public void ReservationRepository(Registry g, Registry r, Registry u) {
        this.guests = g;
        this.rooms = r;
        this.users = u;
    }

    //class methods
    private Reservation createFromRow(ResultSet rs) {
        return null;
    }

    //override methods
    @Override
    public void insert(Object entity) {

    }

    @Override
    public void update(Object entity) {

    }

    @Override
    public List findAll() {
        return List.of();
    }

    @Override
    public void exportToCsv(String folder) {

    }
}
