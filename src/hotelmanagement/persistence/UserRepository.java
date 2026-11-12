package hotelmanagement.persistence;

import hotelmanagement.model.Permission;
import hotelmanagement.model.User;
import hotelmanagement.interfaces.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

public class UserRepository implements Repository {

    private User createFromRow(ResultSet rs) {
        return null;
    }

    private void savePermissions(User u) {

    }

    private Set<Permission> loadPermissions(int userId) {
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
