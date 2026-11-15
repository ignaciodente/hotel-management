package hotelmanagement.persistence;

import hotelmanagement.interfaces.Repository;
import hotelmanagement.model.Guest;
import hotelmanagement.exceptions.DataAccessException;
import hotelmanagement.exceptions.PersistenceException;

import java.sql.ResultSet;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class GuestRepository implements Repository<Guest> {

    //creates table if not exists
    private void createTable() throws PersistenceException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS guest ( "
                            + "guest_id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "first_name VARCHAR(60) NOT NULL, "
                            + "last_name VARCHAR(60) NOT NULL, "
                            + "dni VARCHAR(20) NOT NULL UNIQUE, "
                            + "country VARCHAR(60), "
                            + "home_address VARCHAR(150) "
                            + ") ");
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to create the guest table");
        }
    }

    //creates a row from table into an object
    private Guest createFromRow(ResultSet rs) throws SQLException {
        Guest guest = new Guest(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("dni"),
                rs.getInt("guest_id"),
                rs.getString("country"),
                rs.getString("home_address"));
        guest.setIdGuest(rs.getInt("guest_id"));
        return guest;
    }

    //inserts guest into table a guest
    @Override
    public void insert(Guest guest) throws PersistenceException {
        createTable();
        String sql = "INSERT INTO guest (first_name, last_name, dni, country, home_address) VALUES (?, ?, ?, ?, ?)";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, guest.getName());
            ps.setString(2, guest.getLastName());
            ps.setString(3, guest.getDni());
            ps.setString(4, guest.getCountry());
            ps.setString(5, guest.getHomeAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) guest.setIdGuest(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to insert the guest " + guest.getDni());
        }
    }

    //updates guest
    @Override
    public void update(Guest guest) throws PersistenceException {
        createTable();
        String sql = "UPDATE guest SET first_name = ?, last_name = ?, dni = ?, country = ?, home_address = ? "
                + "WHERE guest_id = ?";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, guest.getName());
            ps.setString(2, guest.getLastName());
            ps.setString(3, guest.getDni());
            ps.setString(4, guest.getCountry());
            ps.setString(5, guest.getHomeAddress());
            ps.setInt(6, guest.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to update the passenger with DNI " + guest.getDni());
        }
    }

    //returns list with all the guests
    @Override
    public List<Guest> findAll() throws PersistenceException {
        createTable();
        List<Guest> guests = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM guest")) {
            while (rs.next()) {
                guests.add(createFromRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to access the guests");
        }
        return guests;
    }

    //creates backup
    @Override
    public void exportToCsv(String folder) throws PersistenceException {
        createTable();
        CsvExporter.export("guest", folder);
    }

}