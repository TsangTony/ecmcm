package com.ibm.ecm.mm.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MSSQLConnection {
	static Connection conn;
	static String connectionString;
		
	public static Connection getConnection() {
		connectionString = "jdbc:sqlserver://127.0.0.1:1433;database=ECMCM;user=sa;password=P@ssw0rd"; 
		try {
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
