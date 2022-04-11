package SQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {

	public static final String DB_URL = "jdbc:sqlite:src/main/java/SQLDatabase/restaurant.db";

	public static Connection accessWineDatabase() throws SQLException{
		Connection connect = DriverManager.getConnection(DB_URL);
		System.out.println("Connected to SQLite!");
		return connect;

	}
}