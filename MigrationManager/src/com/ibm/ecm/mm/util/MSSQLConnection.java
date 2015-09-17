package com.ibm.ecm.mm.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MSSQLConnection {
	static Connection conn;
	static String connectionString;
	
	static {
		conn = null;
		connectionString = "jdbc:sqlserver://127.0.0.1:1433;database=MigrationDataR1;user=sa;password=P@ssw0rd"; 
		try {
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return conn;
	}
	
	public static void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
