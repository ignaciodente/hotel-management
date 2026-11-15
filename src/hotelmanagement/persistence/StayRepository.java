package hotelmanagement.persistence;

import hotelmanagement.generics.Registry;
import hotelmanagement.interfaces.Repository;
import hotelmanagement.exceptions.DataAccessException;
import hotelmanagement.exceptions.EntityNotFoundException;
import hotelmanagement.exceptions.PersistenceException;

import java.sql.ResultSet;
import java.util.List;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class StayRepository implements Repository<Stay> {

    private final Registry reservations;
    private final Registry guests;

    public StayRepository(Registry r, Registry g) {
        this.reservations = r;
        this.guests = g;
    }

    //creates table if not exist
    private void createTable() throws PersistenceException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS stay ( "
                            + "stay_id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "reservation_id INT NOT NULL UNIQUE, "
                            + "check_in_date DATETIME NOT NULL, "
                            + "check_out_date DATETIME NULL, "
                            + "total_amount DECIMAL(12,2) NULL, "
                            + "FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id) "
                            + ") ");
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS stay_guest ( "
                            + "stay_id INT NOT NULL, "
                            + "guest_id INT NOT NULL, "
                            + "PRIMARY KEY (stay_id, guest_id), "
                            + "FOREIGN KEY (stay_id) REFERENCES stay(stay_id) ON DELETE CASCADE, "
                            + "FOREIGN KEY (guest_id) REFERENCES guest(guest_id) "
                            + ") ");
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to create the stay table / stay_guest");
        }
    }

    //makes a row into an object
    //TIENE DOS COMENTADOS QUE RESOLVER
    private Stay createFromRow(ResultSet rs) throws SQLException, PersistenceException {
        try {
            int stayId = rs.getInt("stay_id");
       //     Reservation reservation = reservations.findById(rs.getInt("reservation_id"));
            List<Guest> occupants = loadOccupants(stayId);

            Timestamp out = rs.getTimestamp("check_out_date");
            LocalDateTime checkOut = (out == null) ? null : out.toLocalDateTime();
            double total = rs.getDouble("total_amount");
            Double totalAmount = rs.wasNull() ? null : total;

            //return new Stay(stayId, reservation, occupants
            //           rs.getTimestamp ("check_in_date").toLocalDateTime(), checkOut, totalAmount);
            return null; //TEMPORARIO HASTA RESOLVER LO DE ARRIBA
        } catch (EntityNotFoundException e) {
            throw new DataAccessException("Unavailable reference when uploading the stay "
                    + rs.getInt("stay_id") + ": " + e.getMessage());
        }
    }

    //inserts a stay into the database
    @Override
    public void insert(Stay s) throws PersistenceException {
        createTable();
        String sql = "INSERT INTO stay (reservation_id, check_in_date, check_out_date, total_amount) "
                + "VALUES (?, ?, ?, ?)";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getReservation().getId());
                ps.setTimestamp(2, Timestamp.valueOf(s.getCheckInDate()));
                setNullableTimestamp(ps, 3, s.getCheckOutDate());
                setNullableDouble(ps, 4, s.getTotalAmount());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) s.setId(keys.getInt(1));
                }
                saveOccupants(s);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to insert the stay");
        }
    }

    //updates a stay into the database
    @Override
    public void update(Stay s) throws PersistenceException {
        createTable();
        String sql = "UPDATE stay SET check_in_date = ?, check_out_date = ?, total_amount = ? WHERE stay_id = ?";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(s.getCheckInDate()));
                setNullableTimestamp(ps, 2, s.getCheckOutDate());
                setNullableDouble(ps, 3, s.getTotalAmount());
                ps.setInt(4, s.getId());
                ps.executeUpdate();
                saveOccupants(s);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to update the stay " + s.getId());
        }
    }

    //returns a list with all the stays
    @Override
    public List<Stay> findAll() throws PersistenceException {
        createTable();
        List<Stay> list = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM stay")) {
            while (rs.next()) {
                list.add(createFromRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to read the stays");
        }
        return list;
    }

    //creathes backup
    @Override
    public void exportToCsv(String folder) throws PersistenceException {
        createTable();
        CsvExporter.export("stay", folder);
        CsvExporter.export("stay_guest", folder);
    }



    // Replaces the stay's saved occupants with the ones currently in memory
    private void saveOccupants(Stay s) throws SQLException, PersistenceException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement del = con.prepareStatement("DELETE FROM stay_guest WHERE stay_id = ?")) {
            del.setInt(1, s.getId());
            del.executeUpdate();
        }
        try (PreparedStatement ins = con.prepareStatement("INSERT INTO stay_guest (stay_id, guest_id) VALUES (?, ?)")) {
            for (Guest g : s.getOccupants()) {
                ins.setInt(1, s.getId());
                ins.setInt(2, g.getId());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    //returns a list with all the guests
    private List<Guest> loadOccupants(int stayId) throws SQLException, EntityNotFoundException, PersistenceException {
        List<Guest> occupants = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT guest_id FROM stay_guest WHERE stay_id = ?")) {
            ps.setInt(1, stayId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
              //      occupants.add(guests.findById(rs.getInt("guest_id")));
                }
            }
        }
        return occupants;
    }

    // sets a timestamp in the query or makes it null in the database when the value is null
    private static void setNullableTimestamp(PreparedStatement ps, int idx, LocalDateTime value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.TIMESTAMP);
        else ps.setTimestamp(idx, Timestamp.valueOf(value));
    }

    // sets a decimal in the query or makes it null in the database when the value is null
    private static void setNullableDouble(PreparedStatement ps, int idx, Double value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DECIMAL);
        else ps.setDouble(idx, value);
    }
}

