package hotelmanagement.persistence;
import java.sql.Connection;
import hotelmanagement.exceptions.FailedConnectionException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?serverTimezone=America/Argentina/Buenos_Aires";
    private static final String URL = "jdbc:mysql://localhost:3306/hotel?serverTimezone=America/Argentina/Buenos_Aires";
    private static final String USER = "root";
    private static final String PASSWORD = "contraseniawacho";

    private Connection connection;

    private DatabaseConnection() throws FailedConnectionException {
        connection = openConnection();
    }

    //synchronized is part of singleton
    public static synchronized DatabaseConnection getInstance() throws FailedConnectionException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    //opens the connection if it isn't open yet, and if it is, it returns it
    public Connection getConnection() throws FailedConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = openConnection();
            }
        } catch (SQLException e) {
            throw new FailedConnectionException("No se pudo verificar el estado de la conexión");
        }
        return connection;
    }

    //creates the hotel database and returns its connection
    private static Connection openConnection() throws FailedConnectionException {
        try (Connection server = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement st = server.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS hotel CHARACTER SET utf8mb4");
        } catch (SQLException e) {
            throw new FailedConnectionException("No se pudo conectar al servidor MySQL ni crear la base de datos hotel");
        }
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new FailedConnectionException("No se pudo conectar a la base de datos en " + URL);
        }
    }
}