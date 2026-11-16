package hotelmanagement.persistence;

import hotelmanagement.enums.Permission;
import hotelmanagement.enums.UserType;
import hotelmanagement.interfaces.Repository;
import hotelmanagement.exceptions.DataAccessException;
import hotelmanagement.exceptions.PersistenceException;
import hotelmanagement.model.Administrator;
import hotelmanagement.model.Receptionist;
import hotelmanagement.model.User;

import java.sql.ResultSet;
import java.util.*;
import java.sql.*;

public class UserRepository implements Repository<User> {

    //inserts a user into the database
    @Override
    public void insert(User user) throws PersistenceException {
        createTable();
        String sql = "INSERT INTO users (first_name, last_name, dni, username, password, user_type, active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getDni());
                ps.setString(4, user.getUsername());
                ps.setString(5, user.getPassword());
                ps.setString(6, user.getType().name());
                ps.setBoolean(7, user.isActive());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) user.setId(keys.getInt(1));
                }
                savePermissions(user);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar el usuario " + user.getUsername());
        }
    }

    //updates a user
    @Override
    public void update(User user) throws PersistenceException {
        createTable();
        String sql = "UPDATE users SET first_name = ?, last_name = ?, dni = ?, username = ?, password = ?, "
                + "user_type = ?, active = ? WHERE user_id = ?";
        Connection con = DatabaseConnection.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getDni());
                ps.setString(4, user.getUsername());
                ps.setString(5, user.getPassword());
                ps.setString(6, user.getType().name());
                ps.setBoolean(7, user.isActive());
                ps.setInt(8, user.getId());
                ps.executeUpdate();
                savePermissions(user);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el usuario " + user.getUsername());
        }
    }

    //returns a list with all the users
    @Override
    public List<User> findAll() throws PersistenceException {
        createTable();
        List<User> users = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                users.add(createFromRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo acceder a los usuarios");
        }
        return users;
    }

    //creates backup
    @Override
    public void exportToCsv(String folder) throws PersistenceException {
        createTable();
        CsvExporter.export("users", folder);
        CsvExporter.export("user_permission", folder);
    }

    //creates table if not exists
    private void createTable() throws PersistenceException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users ( "
                            + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "first_name VARCHAR(60) NOT NULL, "
                            + "last_name VARCHAR(60) NOT NULL, "
                            + "dni VARCHAR(20) NOT NULL UNIQUE, "
                            + "username VARCHAR(40) NOT NULL UNIQUE, "
                            + "password VARCHAR(100) NOT NULL, "
                            + "user_type VARCHAR(30) NOT NULL, "
                            + "active BOOLEAN NOT NULL DEFAULT TRUE "
                            + ") ");
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS user_permission ( "
                            + "user_id INT NOT NULL, "
                            + "permission VARCHAR(30) NOT NULL, "
                            + "PRIMARY KEY (user_id, permission), "
                            + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE "
                            + ") ");
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron crear las tablas de usuarios y permisos");
        }
    }

    //makes a row from the table into one user
    private User createFromRow(ResultSet rs) throws SQLException, PersistenceException {
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String dni = rs.getString("dni");
        String username = rs.getString("username");
        String password = rs.getString("password");

        UserType userType;
        try {
            userType = UserType.valueOf(rs.getString("user_type"));
        } catch (IllegalArgumentException e) {
            throw new DataAccessException("Tipo de usuario desconocido en la base de datos: " + rs.getString("user_type"));
        }

        User user;
        if (userType == UserType.ADMINISTRATOR) {
            user = new Administrator(firstName, lastName, dni, username, password);
        } else if (userType == UserType.RECEPTIONIST) {
            user = new Receptionist(firstName, lastName, dni, username, password);
        } else {
            throw new DataAccessException("Tipo de usuario desconocido en la base de datos: " + userType);
        }

        int id = rs.getInt("user_id");
        user.setId(id);
        user.setActive(rs.getBoolean("active"));
        user.setPermissions(loadPermissions(id));   // replaces the role's default permissions with the saved ones
        return user;
    }

    // replaces the user's saved permissions with the ones currently in memory
    private void savePermissions(User u) throws SQLException, PersistenceException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement del = con.prepareStatement("DELETE FROM user_permission WHERE user_id = ?")) {
            del.setInt(1, u.getId());
            del.executeUpdate();
        }
        try (PreparedStatement ins = con.prepareStatement(
                "INSERT INTO user_permission (user_id, permission) VALUES (?, ?)")) {
            for (Permission p : u.getPermissions()) {
                ins.setInt(1, u.getId());
                ins.setString(2, p.name());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }


    //loads permissions in database
    private Set<Permission> loadPermissions(int userId) throws SQLException, PersistenceException {
        Set<Permission> permissions = EnumSet.noneOf(Permission.class);
        Connection con = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT permission FROM user_permission WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    permissions.add(Permission.valueOf(rs.getString("permission")));
                }
            }
        }
        return permissions;
    }
}