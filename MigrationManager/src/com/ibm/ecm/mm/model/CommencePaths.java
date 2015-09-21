package com.ibm.ecm.mm.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.ibm.ecm.mm.util.MSSQLConnection;

public class CommencePaths {
	private ArrayList<CommencePath> commencePaths;
	
	public CommencePaths() {
	
		try {
			
			Connection conn = MSSQLConnection.getConnection();
			String query = "select document_id, commence_path, business_path from dbo.commence_path where document_id = 1";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			CommencePath commencePath = null;
			
			while (rs.next()) {	
				
			commencePath = new CommencePath();
			
			commencePath.setDocumentId(rs.getInt(1));
			commencePath.setCommencePath(rs.getString(2));
			commencePath.setBusinessPath(rs.getString(3));
			
			commencePaths.add(commencePath);
				
			}

			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
}
