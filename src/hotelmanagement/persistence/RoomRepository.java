package hotelmanagement.persistence;
import hotelmanagement.enums.RoomLevel;
import hotelmanagement.enums.RoomStatus;
import hotelmanagement.interfaces.Repository;
import hotelmanagement.exceptions.DataAccessException;
import hotelmanagement.exceptions.PersistenceException;
import hotelmanagement.model.Room;

import java.sql.ResultSet;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;

public class RoomRepository implements Repository<Room> {

    //creates table if not exists
    private void createTable() throws PersistenceException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS room ( "
                            + "room_id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "room_number INT NOT NULL UNIQUE, "
                            + "floor_number INT NOT NULL, "
                            + "capacity INT NOT NULL, "
                            + "room_level VARCHAR(30) NOT NULL, "
                            + "room_status VARCHAR(30) NOT NULL, "
                            + "unavailable_reason VARCHAR(255) NULL "
                            + ") ");
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to create the room table");
        }
    }

    //makes a row from the table into a room object
    private Room createFromRow(ResultSet rs) throws SQLException {
        Room room = new Room(
                rs.getInt("room_number"),
                rs.getInt("floor_number"),
                rs.getInt("capacity"),
                RoomLevel.valueOf(rs.getString("room_level")));
        room.setId(rs.getInt("room_id"));
        room.changeStatus(RoomStatus.valueOf(rs.getString("room_status")), rs.getString("unavailable_reason"));
        return room;
    }

    //inserts room into the table. if table not exists, it's created.
    @Override
    public void insert(Room room) throws PersistenceException {
        createTable();
        String sql = "INSERT INTO room (room_number, floor_number, capacity, room_level, room_status, unavailable_reason) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, room.getNumber());
            ps.setInt(2, room.getFloor());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getLevel().name());
            ps.setString(5, room.getStatus().name());
            ps.setString(6, room.getUnavailableReason());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) room.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("It wasnt possible to insert the room " + room.getNumber());
        }
    }

    //updates room
    @Override
    public void update(Room room) throws PersistenceException {
        createTable();
        String sql = "UPDATE room SET room_number = ?, floor_number = ?, capacity = ?, room_level = ?, "
                + "room_status = ?, unavailable_reason = ? WHERE room_id = ?";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, room.getNumber());
            ps.setInt(2, room.getFloor());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getLevel().name());
            ps.setString(5, room.getStatus().name());
            ps.setString(6, room.getUnavailableReason());
            ps.setInt(7, room.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to update room " + room.getNumber());
        }
    }

    //returns a list with the rooms
    @Override
    public List<Room> findAll() throws PersistenceException {
        createTable();
        List<Room> rooms = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM room")) {
            while (rs.next()) {
                rooms.add(createFromRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("It wasn't possible to access the rooms");
        }
        return rooms;
    }

    //creates backup
    @Override
    public void exportToCsv(String folder) throws PersistenceException {
        createTable();
        CsvExporter.export("room", folder);
    }

}