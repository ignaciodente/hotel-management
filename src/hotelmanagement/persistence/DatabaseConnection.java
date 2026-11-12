package hotelmanagement.persistence;

import java.sql.Connection;

public class DatabaseConnection {

    private DatabaseConnection instance;
    private String url;
    private String user;
    private String password;
    private Connection connection;

    public DatabaseConnection(DatabaseConnection instance) {
        this.instance = instance;
    }
}
