package hotelmanagement.persistence;
import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.exceptions.EntityNotFoundException;
import hotelmanagement.generics.Registry;
import hotelmanagement.interfaces.Repository;
import hotelmanagement.exceptions.DataAccessException;
import hotelmanagement.exceptions.PersistenceException;
import hotelmanagement.model.Guest;
import hotelmanagement.model.Reservation;
import hotelmanagement.model.Room;
import hotelmanagement.model.User;

import java.sql.ResultSet;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;


public class ReservationRepository implements Repository<Reservation> {

    // Preloaded registries, used to resolve foreign keys to objects
    private final Registry<Guest> guests;
    private final Registry<Room> rooms;
    private final Registry<User> users;

    public ReservationRepository(Registry<Guest> g, Registry<Room> r, Registry<User> u) {
        this.guests = g;
        this.rooms = r;
        this.users = u;
    }

    //create table if not exists
    private void createTable() throws PersistenceException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS reservation ( "
                            + "reservation_id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "guest_id INT NOT NULL, "
                            + "room_id INT NOT NULL, "
                            + "created_by INT NOT NULL, "
                            + "start_date DATE NOT NULL, "
                            + "end_date DATE NOT NULL, "
                            + "number_of_guests INT NOT NULL, "
                            + "status VARCHAR(30) NOT NULL, "
                            + "FOREIGN KEY (guest_id) REFERENCES guest(guest_id), "
                            + "FOREIGN KEY (room_id) REFERENCES room(room_id), "
                            + "FOREIGN KEY (created_by) REFERENCES users(user_id) "
                            + ") ");
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo crear la tabla de reservas");
        }
    }

    //makes a row into an object
    private Reservation createFromRow(ResultSet rs) throws SQLException, DataAccessException {
        try {

            Guest holder = guests.findById(rs.getInt("guest_id"));
            Room room = rooms.findById(rs.getInt("room_id"));
            User createdBy = users.findById(rs.getInt("created_by"));

            Reservation r = new Reservation(
                    holder, room, createdBy,
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getInt("number_of_guests"));
            r.setId(rs.getInt("reservation_id"));
            r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
            return r;
        } catch (EntityNotFoundException e) {
            throw new DataAccessException("Referencia inválida al cargar la reserva "
                    + rs.getInt("reservation_id") + ": " + e.getMessage());
        }
    }


    //inserts a reserve in the database
    @Override
    public void insert(Reservation r) throws PersistenceException {
        createTable();
        String sql = "INSERT INTO reservation (guest_id, room_id, created_by, start_date, end_date, "
                + "number_of_guests, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getHolder().getId());
            ps.setInt(2, r.getRoom().getId());
            ps.setInt(3, r.getCreatedBy().getId());
            ps.setDate(4, Date.valueOf(r.getStartDate()));
            ps.setDate(5, Date.valueOf(r.getEndDate()));
            ps.setInt(6, r.getNumberOfGuests());
            ps.setString(7, r.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar la reserva");
        }
    }

    //updates a reserve
    @Override
    public void update(Reservation r) throws PersistenceException {
        createTable();
        String sql = "UPDATE reservation SET guest_id = ?, room_id = ?, created_by = ?, start_date = ?, "
                + "end_date = ?, number_of_guests = ?, status = ? WHERE reservation_id = ?";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getHolder().getId());
            ps.setInt(2, r.getRoom().getId());
            ps.setInt(3, r.getCreatedBy().getId());
            ps.setDate(4, Date.valueOf(r.getStartDate()));
            ps.setDate(5, Date.valueOf(r.getEndDate()));
            ps.setInt(6, r.getNumberOfGuests());
            ps.setString(7, r.getStatus().name());
            ps.setInt(8, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la reserva " + r.getId());
        }
    }

    //returns all reservations as a list
    @Override
    public List<Reservation> findAll() throws PersistenceException {
        createTable();
        List<Reservation> list = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM reservation")) {
            while (rs.next()) {
                list.add(createFromRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo acceder a las reservas");
        }
        return list;
    }

    //creates backup
    @Override
    public void exportToCsv(String folder) throws PersistenceException {
        createTable();
        CsvExporter.export("reservation", folder);
    }
}