package hotelmanagement.persistence;
import hotelmanagement.interfaces.Repository;
import hotelmanagement.model.Room;
import java.sql.ResultSet;
import java.util.List;

import hotelmanagement.persistence.DatabaseConnection;

public class RoomRepository implements Repository{

    private Room createFromRow(ResultSet rs) {
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
