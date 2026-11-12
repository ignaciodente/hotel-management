package hotelmanagement.persistence;

import hotelmanagement.generics.Registry;
import hotelmanagement.interfaces.Repository;
import hotelmanagement.model.Guest;
import hotelmanagement.model.Stay;

import javax.swing.plaf.synth.Region;
import java.sql.ResultSet;
import java.util.List;

public class StayRepository implements Repository {

    private Registry reservations;
    private Registry guests;

    public StayRepository(Registry reservations, Registry guests) {
        this.reservations = reservations;
        this.guests = guests;
    }

    private Stay createFromRow(ResultSet rs) {
        return null;
    }

    private void saveOccupants(Stay s){

    }

    private List loadOccupants(int stayId){
        return null;
    }


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
